package com.qwak.tutorial01;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DemoProducerWithCallback {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger(DemoProducerWithCallback.class);

        String bootstrapServers = "127.0.0.1:9092";
        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // Send data to see round-robin in action
        for (int i = 0; i < 100; i++) {
            // create Producer record
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("firsttopic", "hello world");
            // send data - async
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // execute every time a record successfully sent or an exception is thrown
                    if (e == null) {
                        logger.info("Received new metadata:" +
                                "\nTopic: " + recordMetadata.topic() +
                                "\nPartition: " + recordMetadata.partition() +
                                "\nOffset: " + recordMetadata.offset() +
                                "\nTime: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing: ", e);
                    }
                }
            });
        }

        // flush data
        producer.flush();

        // flush and close
        producer.close();
    }
}
