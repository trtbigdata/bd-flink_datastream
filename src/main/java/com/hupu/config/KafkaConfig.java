package com.hupu.config;


import com.hupu.utils.Constants;

import java.util.Properties;

public class KafkaConfig {
    private static Properties properties;

    public static void configureSasl() {
        //如果用-D或者其它方式设置过，这里不再设置
        if (null == System.getProperty(Constants.JAVA_SECURITY_AUTH_LOGIN_CONFIG)) {
            //请注意将XXX修改为自己的路径
            //这个路径必须是一个文件系统可读的路径，不能被打包到jar中
            System.setProperty(Constants.JAVA_SECURITY_AUTH_LOGIN_CONFIG, getKafkaProperties().getProperty(Constants.JAVA_SECURITY_AUTH_LOGIN_CONFIG));
        }
    }

    public synchronized static Properties getKafkaProperties() {
        if (null != properties) {
            return properties;
        }
        //获取配置文件kafka.properties的内容
        Properties kafkaProperties = new Properties();
        try {
            kafkaProperties.load(KafkaConfig.class.getClassLoader().getResourceAsStream(Constants.KAFKA_PROPERTIES));
        } catch (Exception e) {
            //没加载到文件，程序要考虑退出
            e.printStackTrace();
        }
        properties = kafkaProperties;
        return kafkaProperties;
    }
}
