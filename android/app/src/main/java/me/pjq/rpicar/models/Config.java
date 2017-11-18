package me.pjq.rpicar.models;

public class Config {
    public String deviceName;
    public String productKey;
    public String secret;
    public String pubTopic;
    public String subTopic;

    public Config(String deviceName, String productKey, String secret) {
        this.deviceName = deviceName;
        this.productKey = productKey;
        this.secret = secret;
        //用于测试的topic
        pubTopic = "/" + productKey + "/" + deviceName + "/update";
        subTopic = "/" + productKey + "/" + deviceName + "/get";
    }


    public static Config getConfigRpiCarHome() {
        String deviceName = "RpiCarHome";
        String productKey = "tKB3pmbLvnA";
        String secret = "fT9ryVgfucZNs2g0VZkj8kzV3eNjY55E";

        return new Config(deviceName, productKey, secret);
    }

    public static Config getConfigRpiCarClient() {
        String deviceName = "RpiCarClient";
        String productKey = "tKB3pmbLvnA";
        String secret = "w7TT5kvx1xdzfVogH7RfUUto4kWoSCq4";

        return new Config(deviceName, productKey, secret);
    }
}
