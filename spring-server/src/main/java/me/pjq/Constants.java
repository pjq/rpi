package me.pjq;

import me.pjq.Utils.Log;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

public enum Constants {
    INSTANCE;
    private static final String TAG = "Constants";
    public static String deviceName = "RpiCarHome";
    public static String productKey = "tKB3pmbLvnA";
    public static String secret = "fT9ryVgfucZNs2g0VZkj8kzV3eNjY55E";
    public static String accessKeyId = "LTAICKNMlWBxm7GR";
    public static String accessKeySecret = "cMgi0pjAewppBdpESDlI3CXZpAKFwc";
    public static String phone = "18621517768";
    public static String signName = "树霉派IoT";
    public static String templateCode = "SMS_110310049";

    public static String pubTopic = "/" + productKey + "/" + deviceName + "/update";
    //用于测试的topic
    public static String subTopic = "/" + productKey + "/" + deviceName + "/get";

    private static final String CONFIG_FILE = "./src/main/resources/config.properties";

    //10 seconds, interval for SensorStatus update.
    public static final long SENSOR_STATUS_UPDATE_INTERVAL = 10000;
    //    long RELAY_OFF_INTERVAL = 5 * 60 * 1000;
    // duration for auto turn off the power via relay control
    public static long RELAY_OFF_INTERVAL = 30 * 1000;


    private Constants() {
        try {
            Log.log(TAG, "init: " + CONFIG_FILE);
            getAllProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //读取Properties的全部信息
    public static HashMap<String, String> getAllProperties(String filePath) throws IOException {
        Properties pps = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        pps.load(in);
        Enumeration en = pps.propertyNames(); //得到配置文件的名字
        HashMap<String, String> map = new HashMap<>();

        while (en.hasMoreElements()) {
            String strKey = (String) en.nextElement();
            String strValue = pps.getProperty(strKey);
            strValue = new String(strValue.getBytes("ISO-8859-1"),"utf-8");
            System.out.println(strKey + "=" + strValue);
            map.put(strKey, strValue);
        }

        return map;
    }

    public static void getAllProperties() throws IOException {
        File file = new File(".");
        Log.log(TAG, file.getAbsolutePath());

        HashMap<String, String> map = getAllProperties(CONFIG_FILE);

        if (map.containsKey("deviceName")) {
            deviceName = map.get("deviceName");
        }

        if (map.containsKey("productKey")) {
            productKey = map.get("productKey");
        }
        if (map.containsKey("secret")) {
            secret = map.get("secret");
        }
        if (map.containsKey("accessKeyId")) {
            accessKeyId = map.get("accessKeyId");
        }
        if (map.containsKey("accessKeySecret")) {
            accessKeySecret = map.get("accessKeySecret");
        }
        if (map.containsKey("phone")) {
            phone = map.get("phone");
        }
        if (map.containsKey("signName")) {
            signName = map.get("signName");
        }
        if (map.containsKey("templateCode")) {
            templateCode = map.get("templateCode");
        }

        pubTopic = "/" + productKey + "/" + deviceName + "/update";
        subTopic = "/" + productKey + "/" + deviceName + "/get";
    }
}
