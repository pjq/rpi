/**
 * aliyun.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.aliyun.iot.demo.iothub;

//import com.aliyun.com.aliyun.iot.util.LogUtil;
//import com.aliyun.com.aliyun.iot.util.SignUtil;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import me.pjq.rpicar.Constants;
import com.aliyun.iot.util.LogUtil;
import com.aliyun.iot.util.SignUtil;
import me.pjq.rpicar.models.Config;
import me.pjq.rpicar.models.SensorStatus;
import me.pjq.rpicar.models.WeatherItem;
import me.pjq.rpicar.utils.Log;

//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
//import me.pjq.Constants;
//import me.pjq.Utils.Log;
//import me.pjq.car.CarController;
//import me.pjq.model.CarAction;
//import me.pjq.model.Config;

/**
 * IoT套件JAVA版设备接入demo
 */
public class SimpleClient4IOT {
    /******这里是客户端需要的参数*******/
    private static final String TAG = "SimpleClient4IOT";

    public MqttClient sampleClient;

    private Listener listener;

    Config config = null;
    final ExecutorService executorService = new ThreadPoolExecutor(2,
            4, 600, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100), new CallerRunsPolicy());


    private boolean isRunning = false;
//    public static void main(String... strings) throws Exception {
//        init();
//    }

    public SimpleClient4IOT(Config config) {
        try {
            this.config = config;
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void start() throws Exception {
        //客户端设备自己的一个标记，建议是MAC或SN，不能为空，32字符内
        if (isRunning) {
            return;
        }
        Log.log(TAG, "init the IoT connection..." + config.deviceName);

        String clientId = InetAddress.getLocalHost().getHostAddress();

        //设备认证
        Map<String, String> params = new HashMap<String, String>();
        params.put("productKey", config.productKey); //这个是对应用户在控制台注册的 设备productkey
        params.put("deviceName", config.deviceName); //这个是对应用户在控制台注册的 设备name
        params.put("clientId", clientId);
        String t = System.currentTimeMillis() + "";
        params.put("timestamp", t);

        //MQTT服务器地址，TLS连接使用ssl开头
        String targetServer = "ssl://" + config.productKey + ".com.aliyun.com.aliyun.iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";

        //客户端ID格式，两个||之间的内容为设备端自定义的标记，字符范围[0-9][a-z][A-Z]
        String mqttclientId = clientId + "|securemode=2,signmethod=hmacsha1,timestamp=" + t + "|";
        String mqttUsername = config.deviceName + "&" + config.productKey; //mqtt用户名格式
        String mqttPassword = SignUtil.sign(params, config.secret, "hmacsha1"); //签名

        System.err.println("mqttclientId=" + mqttclientId);

        connectMqtt(targetServer, mqttclientId, mqttUsername, mqttPassword, config.deviceName);

        isRunning = true;
    }

    public void sendMessage(String content) throws MqttException, UnsupportedEncodingException {
//        String content = "{'content':'msg from :" + clientId + "," + System.currentTimeMillis() + "'}";
        MqttMessage message = new MqttMessage(content.getBytes("utf-8"));
        message.setQos(0);
        sampleClient.publish(config.pubTopic, message);
    }

    public void sendSMS(String content) {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
//替换成你的AK
//        final String accessKeyId = "yourAccessKeyId";//你的accessKeyId,参考本文档步骤2
//        final String accessKeySecret = "yourAccessKeySecret";//你的accessKeySecret，参考本文档步骤2
//初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", Constants.accessKeyId,
                Constants.accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(Constants.phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(Constants.signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(Constants.templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(String.format("{\"code\":\"%s\"}", content));
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
//请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
//请求成功
            Log.log(TAG, "send sms success");
        } else {
            Log.log(TAG, "send sms failed: " + sendSmsResponse.getMessage());
        }
    }

    private void connectMqtt(String url, String clientId, String mqttUsername,
                             String mqttPassword, final String deviceName) throws Exception {
        MemoryPersistence persistence = new MemoryPersistence();
        SSLSocketFactory socketFactory = createSSLSocket();
        sampleClient = new MqttClient(url, clientId, persistence);
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
                Log.log(TAG, "Receive message,from Topic [" + topic + "] , content:["
                        + new String(message.getPayload(), "UTF-8") + "],  ");

                String payload = message.toString();
                if (payload.contains("temperature")) {
                    WeatherItem weatherItem = new Gson().fromJson(payload, WeatherItem.class);

                    if (null != listener) {
                        listener.onUpdate(weatherItem);
                    }
                } else if (payload.contains("")) {
                    SensorStatus sensorStatus = new Gson().fromJson(payload, SensorStatus.class);

                    if (null != listener) {
                        listener.onUpdate(sensorStatus);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //如果是QoS0的消息，token.resp是没有回复的
                LogUtil.print("Send message success! " + ((token == null || token.getResponse() == null) ? "null"
                        : token.getResponse().getKey()));
            }
        });
        Log.log(TAG, "Connect success---");

//        sendSMS("Connected");

        String content = "{'content':'msg from :" + clientId + "," + System.currentTimeMillis() + "'}";
        sendMessage(content);

        //一次订阅永久生效
        //这个是第一种订阅topic方式，回调到统一的callback
        sampleClient.subscribe(config.subTopic);

        //这个是第二种订阅方式, 订阅某个topic，有独立的callback
        //sampleClient.subscribe(subTopic, new IMqttMessageListener() {
        //    @Override
        //    public void messageArrived(String topic, MqttMessage message) throws Exception {
        //
        //        LogUtil.print("收到消息：" + message + ",topic=" + topic);
        //    }
        //});

        //回复RRPC响应


//        String reqTopic = "/sys/" + productKey + "/" + deviceName + "/rrpc/request/+";
//        sampleClient.subscribe(reqTopic, new IMqttMessageListener() {
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                LogUtil.print("收到请求：" + message + ", topic=" + topic);
//                String messageId = topic.substring(topic.lastIndexOf('/') + 1);
//                final String respTopic = "/sys/" + productKey + "/" + deviceName + "/rrpc/response/" + messageId;
//                String content = "hello world";
//                final MqttMessage response = new MqttMessage(content.getBytes());
//                response.setQos(0); //RRPC只支持QoS0
//                //不能在回调线程中调用publish，会阻塞线程，所以使用线程池
//                executorService.submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            sampleClient.publish(respTopic, response);
//                            LogUtil.print("回复响应成功，topic=" + respTopic);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });
    }

    private static SSLSocketFactory createSSLSocket() throws Exception {
        SSLContext context = SSLContext.getInstance("TLSV1.2");
        context.init(null, new TrustManager[]{new ALiyunIotX509TrustManager()}, null);
        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }

    public interface Listener {
        void onUpdate(SensorStatus sensorStatus);

        void onUpdate(WeatherItem weatherItem);
    }
}
