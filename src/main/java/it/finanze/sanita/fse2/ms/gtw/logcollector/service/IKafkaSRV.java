package it.finanze.sanita.fse2.ms.gtw.logcollector.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface IKafkaSRV {

    /**
     * Kafka generic listener
     * @param cr
     * @param messageHeaders
     * @throws InterruptedException
     */
	void genericTopicListener(ConsumerRecord<String, String> cr, @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws Exception ;

    RecordMetadata sendMessage(String topic, String key, String value);
}

