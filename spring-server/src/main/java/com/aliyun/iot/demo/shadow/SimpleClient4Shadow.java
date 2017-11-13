package com.aliyun.iot.demo.shadow;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.iot.util.AliyunWebUtils;
import com.aliyun.iot.util.LogUtil;
import com.aliyun.iot.util.SignUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 设备影子功能使用demo
 */
public class SimpleClient4Shadow {

    /**
     * 认证服务器地址 每个区域不一样
     */
    private static String authUrl = "https://com.aliyun.iot-auth.cn-shanghai.aliyuncs.com/auth/devicename";

    /**
     * 设备key和secret信息
     */
    private static String deviceName = "";
    private static String productKey = "";
    private static String deviceSecret = "";

    /**
     * 设备影子topic
     */
    private static String shadowAckTopic = "/shadow/get/" + productKey + "/" + deviceName;
    private static String shadowUpdateTopic = "/shadow/update/" + productKey + "/" + deviceName;

    /**
     * 影子版本号
     */
    private static long shadowVersion = 0;

    /**
     * 根据属性key-value 生成shadow json格式数据
     *
     * @param attributeMap
     * @return
     */
    private static String genUpdateShadowMsg(Map<String, Object> attributeMap) {
        Set<String> attSet = attributeMap.keySet();
        Map<String, Object> attMap = new LinkedHashMap<String, Object>();
        for (String attKey : attSet) {
            attMap.put(attKey, attributeMap.get(attKey));
        }

        Map<String, Object> reportedMap = new LinkedHashMap<String, Object>();
        reportedMap.put("reported", attMap);

        Map<String, Object> shadowJsonMap = new LinkedHashMap<String, Object>();
        shadowJsonMap.put("method", "update");
        shadowJsonMap.put("state", reportedMap);

        //shadow version自增
        shadowVersion++;

        shadowJsonMap.put("version", shadowVersion);

        return JSON.toJSONString(shadowJsonMap);
    }

    /**
     * 生成clean shadow json数据
     *
     * @param reportMsg
     * @return
     */
    private static String genCleanShadowMsg(String reportMsg) {

        Map<String, Object> stateMap = new LinkedHashMap<String, Object>();

        if (reportMsg == null || reportMsg.length() == 0) {
            stateMap.put("reported", "null");
        } else {
            JSONObject reportJsonObj = JSON.parseObject(reportMsg);
            Set<String> attSet = reportJsonObj.keySet();

            Map<String, Object> attMap = new LinkedHashMap<String, Object>();
            for (String attKey : attSet) {
                attMap.put(attKey, reportJsonObj.getString(attKey));
            }
            stateMap.put("reported", attMap);
        }
        stateMap.put("desired", "null");

        Map<String, Object> cleanShadowMap = new LinkedHashMap<String, Object>();
        cleanShadowMap.put("method", "update");
        cleanShadowMap.put("state", stateMap);

        shadowVersion++;
        cleanShadowMap.put("version", shadowVersion);

        return JSON.toJSONString(cleanShadowMap);
    }

    /**
     * 删除影子某个属性
     * 只需要把属性value置为"null"即可
     *
     * @param attributeMap
     * @return
     */
    private static String genDeleteShadowMsg(Map<String, Object> attributeMap) {
        Set<String> attSet = attributeMap.keySet();
        Map<String, Object> attMap = new LinkedHashMap<String, Object>();
        for (String attKey : attSet) {
            attMap.put(attKey, attributeMap.get(attKey));
        }

        Map<String, Object> reportedMap = new LinkedHashMap<String, Object>();
        reportedMap.put("reported", attMap);

        Map<String, Object> shadowJsonMap = new LinkedHashMap<String, Object>();
        shadowJsonMap.put("method", "delete");
        shadowJsonMap.put("state", reportedMap);

        //shadow version自增
        shadowVersion++;

        shadowJsonMap.put("version", shadowVersion);

        return JSON.toJSONString(shadowJsonMap);
    }

    /**
     * 解析出desired信息
     *
     * @param message
     * @param sampleClient
     * @throws Exception
     */
    private static void parseDesiredMsg(MqttMessage message, MqttClient sampleClient) throws Exception {

        JSONObject shadowJsonObj = JSON.parseObject(message.toString());
        JSONObject payloadJsonObj = shadowJsonObj.getJSONObject("payload");

        shadowVersion = shadowJsonObj.getLong("version");
        System.out.println("shadowVersion:" + shadowVersion);

        //解析出desired
        JSONObject stateJsonObj = payloadJsonObj.getJSONObject("state");
        String desiredString = stateJsonObj.getString("desired");
        System.out.println("desiredString:" + desiredString);

        //TODO 根据desired信息做业务处理

        //清空shadow信息
        if (desiredString != null) {
            //reported字段可能为空
            String cleanShadowMsg = genCleanShadowMsg(stateJsonObj.getString("reported"));
            System.out.println("cleanShadowMsg:" + cleanShadowMsg);

            MqttMessage cleanShadowMqttMsg = new MqttMessage(cleanShadowMsg.getBytes("UTF-8"));
            message.setQos(1);
            sampleClient.publish(shadowUpdateTopic, cleanShadowMqttMsg);
            System.out.println("send clean shadow msg done");
        }
    }

    public static void main2(String... strings) throws Exception {

        /* 客户端设备 自己的一个标记 */
        String clientId = productKey + "&" + deviceName;

        Map<String, String> params = new HashMap<String, String>(16);

        /** 这个是对应用户在控制台注册的 设备productkey */
        params.put("productKey", productKey);

        /** 这个是对应用户在控制台注册的 设备name */
        params.put("deviceName", deviceName);
        params.put("timestamp", "" + System.currentTimeMillis());
        params.put("clientId", clientId);

        //签名
        params.put("sign", SignUtil.sign(params, deviceSecret, "hmacMD5"));

        //请求资源 mqtt
        params.put("resources", "mqtt");

        String result = AliyunWebUtils.doPost(authUrl, params, 5000, 5000);
        System.out.println("result=[" + result + "]");

        JSONObject mapResult;
        try {
            mapResult = JSON.parseObject(result);
        } catch (Exception e) {
            System.out.println("https auth result is invalid json fmt");
            return;
        }

        if ("200".equals(mapResult.getString("code"))) {
            LogUtil.print("认证成功！" + mapResult.get("data"));
            LogUtil.print("data=[" + mapResult + "]");
        } else {
            System.err.println("认证失败！");
            throw new RuntimeException(
                "认证失败：" + mapResult.get("code") + "," + mapResult.get("message"));
        }

        JSONObject data = (JSONObject)mapResult.get("data");

        //sign TODO 服务器返回的sign签名 防止域名劫持验证
        //mqtt服务器 TODO
        String targetServer = "ssl://"
            + data.getJSONObject("resources").getJSONObject("mqtt")
            .getString("host")
            + ":" + data.getJSONObject("resources").getJSONObject("mqtt")
            .getString("port");

        String token = data.getString("iotToken");
        String iotId = data.getString("iotId");

        //客户端ID格式:
        /* 设备端自定义的标记，字符范围[0-9][a-z][A-Z] */
        String mqttClientId = clientId;

        /* 认证后得到的云端iotId */
        String mqttUsername = iotId;

        /* 认证后得到的token 有效期7天 */
        String mqttPassword = token;

        System.err.println("mqttclientId=" + mqttClientId);

        connectMqtt(targetServer, mqttClientId, mqttUsername, mqttPassword);
    }

    private static void connectMqtt(String url, String clientId, String mqttUsername,
                                    String mqttPassword) throws Exception {

        MemoryPersistence persistence = new MemoryPersistence();
        SSLSocketFactory socketFactory = createSSLSocket();
        final MqttClient sampleClient = new MqttClient(url, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();

        /* MQTT 3.1.1 */
        connOpts.setMqttVersion(4);
        connOpts.setSocketFactory(socketFactory);

        //设置是否自动重连
        connOpts.setAutomaticReconnect(true);

        //如果是true 那么清理所有离线消息，即qos1 或者 2的所有未接收内容
        connOpts.setCleanSession(false);

        connOpts.setUserName(mqttUsername);
        connOpts.setPassword(mqttPassword.toCharArray());
        connOpts.setKeepAliveInterval(65);
        LogUtil.print(clientId + "进行连接, 目的地: " + url);

        //sampleClient.setManualAcks(true);//不要自动回执ack
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
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //如果是qos 0消息 token.resp是没有回复的
                LogUtil.print("消息发送成功! " + ((token == null || token.getResponse() == null) ? "null"
                    : token.getResponse().getKey()));
            }
        });
        LogUtil.print("连接成功:---");

        //订阅shadow topic
        sampleClient.subscribe(shadowAckTopic, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                LogUtil.print("收到消息：" + message + ",topic=" + topic);

                JSONObject shadowJsonObj = JSON.parseObject(message.toString());
                String shadowMethod = shadowJsonObj.getString("method");
                JSONObject payloadJsonObj = shadowJsonObj.getJSONObject("payload");

                /* method是reply，解析成功还是失败*/
                if ("reply".equals(shadowMethod)) {

                    String status = payloadJsonObj.getString("status");
                    String stateInfo = payloadJsonObj.getString("state");
                    if ("success".equals(status)) {
                        if (stateInfo == null) {
                            System.out.println("update shadow success");
                        } else {
                            //解析出desired信息
                            parseDesiredMsg(message, sampleClient);
                        }
                    } else {
                        JSONObject errorJsonObj = payloadJsonObj.getJSONObject("content");
                        String errorCode = errorJsonObj.getString("errorcode");
                        String errorMsg = errorJsonObj.getString("errormessage");
                        System.out.println("errorCode:" + errorCode);
                        System.out.println("errorMsg:" + errorMsg);
                    }

                }
                /* method是control，解析出desired和version信息 */
                else if ("control".equals(shadowMethod)) {
                    parseDesiredMsg(message, sampleClient);
                }
            }
        });

        //获取影子内容，解析出version信息
        String getShadowInfo = "{\"method\": \"get\"}";
        MqttMessage shadowMessage = new MqttMessage(getShadowInfo.getBytes("UTF-8"));
        shadowMessage.setQos(1);
        sampleClient.publish(shadowUpdateTopic, shadowMessage);

        //等待获取到版本号
        Thread.sleep(1000);

        //更新设备影子
        Map<String, Object> attMap = new HashMap<String, Object>(128);
        attMap.put("window", "open");
        attMap.put("led", "on");
        attMap.put("temperature", 28);
        attMap.put("light", "high");

        String shadowUpdateMsg = genUpdateShadowMsg(attMap);
        System.out.println("updateShadowMsg: " + shadowUpdateMsg);

        MqttMessage message = new MqttMessage(shadowUpdateMsg.getBytes("UTF-8"));
        message.setQos(1);
        sampleClient.publish(shadowUpdateTopic, message);

        //删除影子某个属性
        Map<String, Object> deleteAttMap = new HashMap<String, Object>(128);

        //把属性值设置为"null"
        deleteAttMap.put("temperature", "null");
        deleteAttMap.put("led", "null");
        deleteAttMap.put("window", "null");

        String deleteShadowMsg = genDeleteShadowMsg(deleteAttMap);
        System.out.println("deleteShadowMsg: " + deleteShadowMsg);

        MqttMessage deleteMessage = new MqttMessage(deleteShadowMsg.getBytes("UTF-8"));
        deleteMessage.setQos(1);
        sampleClient.publish(shadowUpdateTopic, deleteMessage);

    }

    private static SSLSocketFactory createSSLSocket() throws Exception {
        SSLContext context = SSLContext.getInstance("TLSV1.2");
        context.init(null, new TrustManager[] {new com.aliyun.iot.demo.iothub.ALiyunIotX509TrustManager()}, null);
        SSLSocketFactory socketFactory = context.getSocketFactory();
        return socketFactory;
    }
}
