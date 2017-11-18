package me.pjq;

import me.pjq.Utils.Log;
import me.pjq.model.Config;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;


public enum Constants {
    INSTANCE;
    private static final String TAG = "Constants";
//    public static String deviceName = "RpiCarHome";
//    public static String productKey = "tKB3pmbLvnA";
//    public static String secret = "fT9ryVgfucZNs2g0VZkj8kzV3eNjY55E";

//    public static String deviceName = "RpiCarClient";
//    public static String productKey = "tKB3pmbLvnA";
//    public static String secret = "w7TT5kvx1xdzfVogH7RfUUto4kWoSCq4";

//    public static String accessKeyId = "LTAICKNMlWBxm7GR";
//    public static String accessKeySecret = "cMgi0pjAewppBdpESDlI3CXZpAKFwc";
//    public static String phone = "18621517768";
//    public static String signName = "树霉派IoT";
//    public static String templateCode = "SMS_110310049";

    //10 seconds, interval for SensorStatus update.
//    public static long SENSOR_STATUS_UPDATE_INTERVAL = 10000;
    // duration for auto turn off the power via relay control
//    public static long RELAY_OFF_INTERVAL = 30 * 1000;

//    public static String pubTopic = "/" + productKey + "/" + deviceName + "/update";
    //用于测试的topic
//    public static String subTopic = "/" + productKey + "/" + deviceName + "/get";

    private static final String CONFIG_FILE = "config.properties";
    private static final String CONFIG_FILE_DEFAULT = "./src/main/resources/config.properties";
    private Config config;

    public Config getConfig() {
        return config;
    }

    private Constants() {
        try {
            config = getAllProperties();
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
            strValue = new String(strValue.getBytes("ISO-8859-1"), "utf-8");
            Log.log(TAG, strKey + "=" + strValue);
            map.put(strKey, strValue);
        }

        return map;
    }

    public static Config getAllProperties() throws IOException {
        String configFile = CONFIG_FILE_DEFAULT;
        File file = new File(configFile);
        if (!file.exists()) {
            configFile = CONFIG_FILE;
        }

        Log.log(TAG, "init: " + configFile);
        HashMap<String, String> map = getAllProperties(configFile);
        String deviceName = map.get("deviceName");

        String productKey = map.get("productKey");
        String secret = map.get("secret");

        Config config = new Config(deviceName, productKey, secret);

        if (map.containsKey("accessKeyId")) {
            config.accessKeyId = map.get("accessKeyId");

        }
        if (map.containsKey("accessKeySecret")) {
            config.accessKeySecret = map.get("accessKeySecret");
        }
        if (map.containsKey("phone")) {
            config.phone = map.get("phone");
        }
        if (map.containsKey("signName")) {
            config.signName = map.get("signName");
        }
        if (map.containsKey("templateCode")) {
            config.templateCode = map.get("templateCode");
        }

        if (map.containsKey("SENSOR_STATUS_UPDATE_INTERVAL")) {
            config.SENSOR_STATUS_UPDATE_INTERVAL = Long.valueOf(map.get("SENSOR_STATUS_UPDATE_INTERVAL"));
        }

        if (map.containsKey("RELAY_OFF_INTERVAL")) {
            config.RELAY_OFF_INTERVAL = Long.valueOf(map.get("RELAY_OFF_INTERVAL"));
        }

        Log.log(TAG, "SENSOR_STATUS_UPDATE_INTERVAL: " + config.SENSOR_STATUS_UPDATE_INTERVAL);
        Log.log(TAG, "RELAY_OFF_INTERVAL: " + config.RELAY_OFF_INTERVAL);

        config.pubTopic = "/" + productKey + "/" + deviceName + "/update";
        config.subTopic = "/" + productKey + "/" + deviceName + "/get";

        return config;
    }
}
