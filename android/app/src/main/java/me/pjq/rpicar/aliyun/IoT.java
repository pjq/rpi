package me.pjq.rpicar.aliyun;

import android.util.Base64;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.iot.model.v20170620.PubRequest;
import com.aliyuncs.iot.model.v20170620.PubResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import org.apache.log4j.BasicConfigurator;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by i329817(Jianqing.Peng@sap.com) on 13/11/2017.
 */

public enum IoT {
    instance;
    public static String deviceName = "RpiCarHome";
    public static String productKey = "tKB3pmbLvnA";
    public static String secret = "fT9ryVgfucZNs2g0VZkj8kzV3eNjY55E";
    DefaultAcsClient client;

    //用于测试的topic
    private static String subTopic = "/" + productKey + "/" + deviceName + "/get";
    private static String pubTopic = "/" + productKey + "/" + deviceName + "/update";

    String accessKey = "LTAICKNMlWBxm7GR";
    String accessSecret = "cMgi0pjAewppBdpESDlI3CXZpAKFwc";
    String AccountEndpoint = "https://1386496277130610.mns.cn-shanghai.aliyuncs.com/";
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private IoT() {
//        BasicConfigurator.configure();
        init();
    }

    public void init() {
        try {
            DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "Iot", "iot.cn-shanghai.aliyuncs.com");
        } catch (ClientException e) {
            e.printStackTrace();
        }
        IClientProfile profile = DefaultProfile.getProfile("cn-shanghai", accessKey, accessSecret);
        client = new DefaultAcsClient(profile); //初始化SDK客户端

        receive();
    }

    public void send(final String message) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                sendToIoT(message);
            }
        });
    }

    private void sendToIoT(String message) {
        PubRequest request = new PubRequest();
        request.setProductKey(productKey);
//        request.setMessageContent(Base64.encodeBase64String("hello world".getBytes()));
        String encodeMessage = null;
        try {
            encodeMessage = new String(Base64.encode(message.getBytes(), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        request.setMessageContent(encodeMessage);
//        request.setTopicFullName("/productKey/deviceName/get");
        request.setTopicFullName(subTopic);
        request.setQos(0); //目前支持QoS0和QoS1
        PubResponse response = null;
        try {
            response = client.getAcsResponse(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }

        if (null != response) {
            System.out.println(response.getSuccess());
            System.out.println(response.getErrorMessage());
        }
    }

//    public static String getBASE64(String s) {
//        if (s == null) return null;
//        return (new sun.misc.BASE64Encoder()).encode( s.getBytes() );
//    }

    public void receive() {
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                CloudAccount account = new CloudAccount(
//                        accessKey,
//                        accessSecret,
//                        AccountEndpoint);
//                MNSClient client = account.getMNSClient();
//                CloudQueue queue = client.getQueueRef("aliyun-iot-" + productKey ); //参数请输入IoT自动创建的队列名称，例如上面截图中的aliyun-iot-3AbL0062osF
//                while (true) {
//                    // 获取消息
//                    Message popMsg = queue.popMessage(10); //长轮询等待时间为10秒
//                    System.out.println("popMsg: " + popMsg);
//                    if (popMsg != null) {
//                        System.out.println("PopMessage Body: "
//                                + popMsg.getMessageBodyAsRawString()); //获取原始消息
//                        queue.deleteMessage(popMsg.getReceiptHandle()); //从队列中删除消息
//                    } else {
//                        System.out.println("Continuing");
//                    }
//                }
//            }
//        });
    }
}
