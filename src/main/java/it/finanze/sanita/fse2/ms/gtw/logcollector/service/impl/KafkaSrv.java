package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ILogCollectorSrv;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaSrv implements IKafkaSRV {

    @Autowired
    ILogCollectorSrv logCollectorService;

    @Autowired
    @Qualifier("notxkafkatemplate")
    protected KafkaTemplate<String, String> notxKafkaTemplate;

    @Override
    @KafkaListener(topics = "#{'${kafka.notifier.topic}'}", clientIdPrefix = "#{'${kafka.client-id}'}", containerFactory = "kafkaListenerDeadLetterContainerFactory", autoStartup = "${event.topic.auto.start}", groupId = "#{'${kafka.consumer.group-id}'}")
    public void genericTopicListener(ConsumerRecord<String, String> cr,
            @Header(KafkaHeaders.DELIVERY_ATTEMPT) int delivery) throws Exception {
        log.info(cr.value());
        logCollectorService.saveLogEvent(cr.value());
    }

    @Override
    public RecordMetadata sendMessage(String topic, String key, String value) {
        RecordMetadata out = null;
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
        try {
            out = kafkaSend(producerRecord);
        } catch (Exception e) {
            log.error("Send failed.", e);
            throw new BusinessException(e);
        }
        return out;
    }

    private RecordMetadata kafkaSend(ProducerRecord<String, String> producerRecord) {
        RecordMetadata out = null;
        notxKafkaTemplate.send(producerRecord);
        return out;
    }

}
