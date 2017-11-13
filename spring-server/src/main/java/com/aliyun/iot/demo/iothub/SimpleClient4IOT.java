/**
 * aliyun.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.aliyun.iot.demo.iothub;

import com.aliyun.iot.util.LogUtil;
import com.aliyun.iot.util.SignUtil;
import com.google.gson.Gson;
import me.pjq.car.CarController;
import me.pjq.model.CarAction;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

/**
 * IoT套件JAVA版设备接入demo
 */
public class SimpleClient4IOT {
	
	/******这里是客户端需要的参数*******/
    public static String deviceName = "RpiCarHome";
    public static String productKey = "tKB3pmbLvnA";
    public static String secret = "fT9ryVgfucZNs2g0VZkj8kzV3eNjY55E";

    //用于测试的topic
    private static String subTopic = "/" + productKey + "/" + deviceName + "/get";
    private static String pubTopic = "/" + productKey + "/" + deviceName + "/update";

    public static boolean isRunning = false;
//    public static void main(String... strings) throws Exception {
//        start();
//    }

    public static void start() throws Exception {
        //客户端设备自己的一个标记，建议是MAC或SN，不能为空，32字符内
        if (isRunning) {
            return;
        }

        String clientId = InetAddress.getLocalHost().getHostAddress();

        //设备认证
        Map<String, String> params = new HashMap<String, String>();
        params.put("productKey", productKey); //这个是对应用户在控制台注册的 设备productkey
        params.put("deviceName", deviceName); //这个是对应用户在控制台注册的 设备name
        params.put("clientId", clientId);
        String t = System.currentTimeMillis() + "";
        params.put("timestamp", t);

        //MQTT服务器地址，TLS连接使用ssl开头
        String targetServer = "ssl://" + productKey + ".com.aliyun.iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";

        //客户端ID格式，两个||之间的内容为设备端自定义的标记，字符范围[0-9][a-z][A-Z]
        String mqttclientId = clientId + "|securemode=2,signmethod=hmacsha1,timestamp=" + t + "|";
        String mqttUsername = deviceName + "&" + productKey; //mqtt用户名格式
        String mqttPassword = SignUtil.sign(params, secret, "hmacsha1"); //签名

        System.err.println("mqttclientId=" + mqttclientId);

        connectMqtt(targetServer, mqttclientId, mqttUsername, mqttPassword, deviceName);
    }

    public static void connectMqtt(String url, String clientId, String mqttUsername,
                                   String mqttPassword, final String deviceName) throws Exception {
        MemoryPersistence persistence = new MemoryPersistence();
        SSLSocketFactory socketFactory = createSSLSocket();
        final MqttClient sampleClient = new MqttClient(url, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setMqttVersion(4); // MQTT 3.1.1
        connOpts.setSocketFactory(socketFactory);

        //设置是否自动重连
        connOpts.setAutomaticReconnect(true);

        //如果是true，那么清理所有离线消息，即QoS1或者2的所有未接收内容
        connOpts.setCleanSession(false);

        connOpts.setUserName(mqttUsername);
        connOpts.setPassword(mqttPassword.toCharArray());
        connOpts.setKeepAliveInterval(65);

        LogUtil.print(clientId + "进行连接, 目的地: " + url);
        sampleClient.connect(connOpts);

        sampleClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                LogUtil.print("连接失败,原因:" + cause);
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                LogUtil.print("接收到消息,来至Topic [" + topic + "] , 内容是:["
                    + new String(message.getPayload(), "UTF-8") + "],  ");

                String payload = message.toString();
                CarAction carAction = new Gson().fromJson(payload, CarAction.class);
                CarController.getInstance().init().control(carAction);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //如果是QoS0的消息，token.resp是没有回复的
                LogUtil.print("消息发送成功! " + ((token == null || token.getResponse() == null) ? "null"
                    : token.getResponse().getKey()));
            }
        });
        LogUtil.print("连接成功:---");

        //这里测试发送一条消息
        String content = "{'content':'msg from :" + clientId + "," + System.currentTimeMillis() + "'}";

        MqttMessage message = new MqttMessage(content.getBytes("utf-8"));
        message.setQos(0);
        //System.out.println(System.currentTimeMillis() + "消息发布:---");
        sampleClient.publish(pubTopic, message);

        //一次订阅永久生效 
        //这个是第一种订阅topic方式，回调到统一的callback
        sampleClient.subscribe(subTopic);

        //这个是第二种订阅方式, 订阅某个topic，有独立的callback
        //sampleClient.subscribe(subTopic, new IMqttMessageListener() {
        //    @Override
        //    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //
        //        LogUtil.print("收到消息：" + message + ",topic=" + topic);
        //    }
        //});

        //回复RRPC响应
        final ExecutorService executorService = new ThreadPoolExecutor(2,
            4, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100), new CallerRunsPolicy());

        String reqTopic = "/sys/" + productKey + "/" + deviceName + "/rrpc/request/+";
        sampleClient.subscribe(reqTopic, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                LogUtil.print("收到请求：" + message + ", topic=" + topic);
                String messageId = topic.substring(topic.lastIndexOf('/') + 1);
                final String respTopic = "/sys/" + productKey + "/" + deviceName + "/rrpc/response/" + messageId;
                String content = "hello world";
                final MqttMessage response = new MqttMessage(content.getBytes());
                response.setQos(0); //RRPC只支持QoS0
                //不能在回调线程中调用publish，会阻塞线程，所以使用线程池
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sampleClient.publish(respTopic, response);
                            LogUtil.print("回复响应成功，topic=" + respTopic);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private static SSLSocketFactory createSSLSocket() throws Exception {
        SSLContext context = SSLContext.getInstance("TLSV1.2");
        context.init(null, new TrustManager[] {new ALiyunIotX509TrustManager()}, null);
        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }
}
