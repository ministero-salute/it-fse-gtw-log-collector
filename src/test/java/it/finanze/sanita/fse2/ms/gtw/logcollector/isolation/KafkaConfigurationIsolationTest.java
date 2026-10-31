package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.oauth2.CustomAuthenticateCallbackHandler;

@Tag("ISOLATION")
class KafkaConfigurationIsolationTest {
    private KafkaPropertiesCfg properties;
    private KafkaProducerPropertiesCfg producerProperties;
    private KafkaConsumerPropertiesCfg consumerProperties;
    private KafkaProducerCfg producer;
    private KafkaConsumerCfg consumer;

    @BeforeEach
    void setup() {
        properties = new KafkaPropertiesCfg();
        properties.setBootstrapServers("broker.invalid:9092");
        properties.setClientId("isolation");
        producerProperties = new KafkaProducerPropertiesCfg();
        producerProperties.setRetries(3);
        consumerProperties = new KafkaConsumerPropertiesCfg();
        consumerProperties.setConsumerGroupId("isolation-group");
        consumerProperties.setIsolationLevel("read_committed");
        consumerProperties.setAutoCommit("false");
        consumerProperties.setAutoOffsetReset("earliest");
        consumerProperties.setDeadLetterExceptions(List.of("java.lang.IllegalArgumentException"));
        producer = new KafkaProducerCfg();
        consumer = new KafkaConsumerCfg();
        ReflectionTestUtils.setField(producer, "kafkaPropCFG", properties);
        ReflectionTestUtils.setField(producer, "kafkaProducerPropCFG", producerProperties);
        ReflectionTestUtils.setField(consumer, "kafkaPropCFG", properties);
        ReflectionTestUtils.setField(consumer, "kafkaConsumerPropCFG", consumerProperties);
        ReflectionTestUtils.setField(consumer, "kafkaTopicCFG", new KafkaTopicCfg());
    }

    @Test
    void baseConfigurationDoesNotInventSecuritySettings() {
        Map<String, Object> p = producer.producerWithoutTransactionConfigs();
        Map<String, Object> c = consumer.consumerConfigs();
        assertEquals("isolation-noTx", p.get(ProducerConfig.CLIENT_ID_CONFIG));
        assertEquals(3, p.get(ProducerConfig.RETRIES_CONFIG));
        assertEquals("broker.invalid:9092", p.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("isolation", c.get(ConsumerConfig.CLIENT_ID_CONFIG));
        assertEquals("isolation-group", c.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("read_committed", c.get(ConsumerConfig.ISOLATION_LEVEL_CONFIG));
        assertEquals("false", c.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        assertEquals("earliest", c.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        for (Map<String, Object> config : List.of(p, c)) {
            assertFalse(config.containsKey("security.protocol"));
            assertFalse(config.containsKey("sasl.mechanism"));
            assertFalse(config.containsKey("ssl.truststore.password"));
            assertFalse(config.containsKey("sasl.login.callback.handler.class"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"OAUTHBEARER", "PLAIN"})
    void optionalSecurityIsCopiedForProducerAndConsumer(String mechanism) {
        properties.setProtocol("SASL_SSL");
        properties.setMechanism(mechanism);
        properties.setConfigJaas("login required;");
        properties.setTrustoreLocation("truststore.jks");
        properties.setTrustorePassword("secret".toCharArray());
        properties.setCallbackHandlerClass("example.Callback");
        properties.setTenantId("tenant");
        properties.setAppId("app");
        properties.setPfxPathName("certificate.pfx");
        properties.setPwd("certificate-secret");
        for (Map<String, Object> config : List.of(producer.producerWithoutTransactionConfigs(), consumer.consumerConfigs())) {
            assertEquals("SASL_SSL", config.get("security.protocol"));
            assertEquals(mechanism, config.get("sasl.mechanism"));
            assertEquals("login required;", config.get("sasl.jaas.config"));
            assertEquals("truststore.jks", config.get("ssl.truststore.location"));
            assertEquals("secret", config.get("ssl.truststore.password"));
            assertEquals("example.Callback", config.get("sasl.client.callback.handler.class"));
            if (mechanism.equals("OAUTHBEARER")) {
                assertEquals(CustomAuthenticateCallbackHandler.class, config.get("sasl.login.callback.handler.class"));
                assertEquals("tenant", config.get("kafka.oauth.tenantId"));
                assertEquals("app", config.get("kafka.oauth.appId"));
                assertEquals("certificate.pfx", config.get("kafka.oauth.pfxPathName"));
                assertEquals("certificate-secret", config.get("kafka.oauth.pwd"));
            } else {
                assertFalse(config.containsKey("sasl.login.callback.handler.class"));
            }
        }
    }

    @Test
    void factoriesAreLazyAndRequireNoBroker() {
        var factory = producer.producerFactoryWithoutTransaction();
        assertEquals("isolation-noTx", factory.getConfigurationProperties().get(ProducerConfig.CLIENT_ID_CONFIG));
        assertNotNull(producer.producerDeadFactory());
        assertNotNull(producer.notxKafkaTemplate());
        assertNotNull(producer.noTxKafkaDeadTemplate());
        assertEquals("isolation-group", consumer.consumerFactory().getConfigurationProperties().get(ConsumerConfig.GROUP_ID_CONFIG));
    }

    @ParameterizedTest
    @ValueSource(strings = {"java.lang.IllegalArgumentException", "nonexistent.Exception"})
    @SuppressWarnings("unchecked")
    void listenerFactoryEnablesDeliveryAttemptHeaderAndToleratesInvalidExceptionClass(String exception) {
        consumerProperties.setDeadLetterExceptions(List.of(exception));
        KafkaTemplate<Object, Object> deadLetter = mock(KafkaTemplate.class);
        var factory = consumer.kafkaListenerDeadLetterContainerFactory(deadLetter);
        var container = factory.createContainer("isolation-topic");
        assertTrue(container.getContainerProperties().isDeliveryAttemptHeader());
        assertFalse(container.isRunning());
        verify(deadLetter, never()).send(any(org.apache.kafka.clients.producer.ProducerRecord.class));
    }
}
