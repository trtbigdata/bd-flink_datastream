package com.hupu.datastream;

import com.hupu.config.KafkaConfig;
import com.hupu.utils.ParseUserTrack;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.Properties;

public class UserTrack {
    public static void main(String[] args) throws Exception {
        // 添加运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //加载kafka.properties
        Properties kafkaProperties =  KafkaConfig.getKafkaProperties();

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getProperty("bootstrap.servers"));
        //可更加实际拉去数据和客户的版本等设置此值，默认30s
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        //每次poll的最大数量
        //注意该值不要改得太大，如果poll太多数据，而不能在下次poll之前消费完，则会触发一次负载均衡，产生卡顿
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 30);
        //当前消费实例所属的消费组，请在控制台申请之后填写
        //属于同一个组的消费实例，会负载消费消息
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getProperty("dm.user.track.group.id"));

        String topics = kafkaProperties.getProperty("topic");
        ArrayList<String> topicList = new ArrayList();
        for (String topic : topics.split(",")){
            topicList.add(topic);
        }

        FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010<>(topicList, new SimpleStringSchema(),props);

        env.addSource(kafkaConsumer)
                .map(new ParseUserTrack());





        env.execute("alikafkaconsumerdemo");

    }

}
