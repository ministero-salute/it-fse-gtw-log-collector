package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ILogCollectorSrv;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl.KafkaSrv;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(KafkaSrv.class)
class KafkaIsolationTest {
    @Autowired private KafkaSrv service;
    @MockitoBean private ILogCollectorSrv collector;
    @MockitoBean(name = "notxkafkatemplate") private KafkaTemplate<String, String> template;

    @Test
    void listenerPassesOriginalPayloadToCollector() throws Exception {
        service.genericTopicListener(new ConsumerRecord<>("input", 0, 1L, "key", "{\"message\":\"event\"}"), 1);
        verify(collector).saveLogEvent("{\"message\":\"event\"}");
        verifyNoInteractions(template);
    }

    @Test
    void listenerPropagatesFailureForRetry() {
        BusinessException cause = new BusinessException("save failed");
        doThrow(cause).when(collector).saveLogEvent("event");
        assertSame(cause, assertThrows(BusinessException.class,
                () -> service.genericTopicListener(new ConsumerRecord<>("input", 0, 1L, "key", "event"), 2)));
    }

    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    void sendsTopicKeyAndPayloadWithoutClaimingAcknowledgement() {
        assertNull(service.sendMessage("sender", "logType", "kpi-structured-log"));
        ArgumentCaptor<ProducerRecord<String, String>> record = ArgumentCaptor.forClass((Class) ProducerRecord.class);
        verify(template).send(record.capture());
        assertEquals("sender", record.getValue().topic());
        assertEquals("logType", record.getValue().key());
        assertEquals("kpi-structured-log", record.getValue().value());
    }

    @Test
    void wrapsSynchronousSendFailure() {
        IllegalStateException cause = new IllegalStateException("broker unavailable");
        when(template.send(any(ProducerRecord.class))).thenThrow(cause);
        assertSame(cause, assertThrows(BusinessException.class,
                () -> service.sendMessage("sender", "key", "event")).getCause());
    }
}
