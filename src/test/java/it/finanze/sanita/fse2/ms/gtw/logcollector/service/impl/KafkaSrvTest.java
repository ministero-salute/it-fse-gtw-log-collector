package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import com.google.gson.Gson;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.KafkaTopicCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.LocalityDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.SubjApplicationDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IKafkaSRV;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@EmbeddedKafka
class KafkaSrvTest {

    @SpyBean
    private IKafkaSRV kafkaSRV;

    @Autowired
    private KafkaTopicCfg kafkaTopicCFG;

    @Test
    void InComingControllLogs() {

        String logTopic = kafkaTopicCFG.getNotifierTopic();

        Map<String, Object> map = new HashMap<>();
        MessageHeaders headers = new MessageHeaders(map);


        LogCollectorControlETY logOkExample = new LogCollectorControlETY();
        IssuerDTO opIssuer = new IssuerDTO();
        LocalityDTO opLocality = new LocalityDTO();
        SubjApplicationDTO opSubjApplication = new SubjApplicationDTO();
        Gson gson = new Gson();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentdate = new Date();

        logOkExample.setWorkflowInstanceId("2.16.840.1.113883.2.9.2.120.4.4.46a41df0ab0514f11c0811056832c3225e06c8e11824f27c7e5517ca5cfc57fe.e0177e0824^^^^urn:ihe:iti:xdw:2013:workflowInstanceId");
        logOkExample.setTypeIdExtension("POCD_MT000040UV02");
        logOkExample.setLogType("control-structured-log"); //"kpi-structured-log"-Per save su collection log_collector_kpi //"control-structured-log" -Per save su collection in log_collector
        logOkExample.setMessage("Validation CDA completed for workflow instance Id 2.16.840.1.113883.2.9.2.120.4.4.46a41df0ab0514f11c0811056832c3225e06c8e11824f27c7e5517ca5cfc57fe.e0177e0824^^^^urn:ihe:iti:xdw:2013:workflowInstanceId");
        logOkExample.setOperation("VAL-CDA2");
        logOkExample.setResult("OK");
        logOkExample.setTimestampStart(currentdate);
        logOkExample.setTimestampEnd(currentdate);
        opIssuer.setRawValue("integrity:S1#190201234567XX");
        opIssuer.setRegion("190");
        logOkExample.setIssuer(opIssuer);
        opLocality.setRawValue("201123456");
        opLocality.setAslCode("201");
        opLocality.setStructure("123456");
        logOkExample.setLocality(opLocality);
        logOkExample.setDocumentType("Referto di Laboratorio");
        logOkExample.setRole("AAS");
        logOkExample.setFiscalCode("SSSMNN75B01F257L");
        logOkExample.setGatewayName("FSE2-GTW-00");
        logOkExample.setMicroserviceName("gtw-dispatcher");
        opSubjApplication.setSubject_application_version("GTW");
        opSubjApplication.setSubject_application_id("GATEWAY");
        opSubjApplication.setSubject_application_version("R2");
        logOkExample.setOpSubjApplication(opSubjApplication);
        String logInComing = gson.toJson(logOkExample);


        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(logTopic,
                1, 0, logTopic, logInComing);

        assertAll(() -> assertDoesNotThrow(() -> kafkaSRV.genericTopicListener(record1, 1)));

    }

    @Test
    void InComingKpiLogs() {

        String logTopic = kafkaTopicCFG.getNotifierTopic();

        Map<String, Object> map = new HashMap<>();
        MessageHeaders headers = new MessageHeaders(map);


        LogCollectorControlETY logOkExample = new LogCollectorControlETY();
        IssuerDTO opIssuer = new IssuerDTO();
        LocalityDTO opLocality = new LocalityDTO();
        SubjApplicationDTO opSubjApplication = new SubjApplicationDTO();
        Gson gson = new Gson();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentdate = new Date();

        logOkExample.setLogType("kpi-structured-log"); //"kpi-structured-log"-Per save su collection log_collector_kpi //"control-structured-log" -Per save su collection in log_collector
        logOkExample.setMessage("Validation CDA completed for workflow instance Id 2.16.840.1.113883.2.9.2.120.4.4.46a41df0ab0514f11c0811056832c3225e06c8e11824f27c7e5517ca5cfc57fe.e0177e0824^^^^urn:ihe:iti:xdw:2013:workflowInstanceId");
        logOkExample.setOperation("VAL-CDA2");
        logOkExample.setResult("OK");
        logOkExample.setTimestampStart(currentdate);
        logOkExample.setTimestampEnd(currentdate);
        opIssuer.setRawValue("integrity:S1#190201234567XX");
        opIssuer.setRegion("190");
        logOkExample.setIssuer(opIssuer);
        opLocality.setRawValue("201123456");
        opLocality.setAslCode("201");
        opLocality.setStructure("123456");
        logOkExample.setLocality(opLocality);
        logOkExample.setDocumentType("Referto di Laboratorio");
        logOkExample.setRole("AAS");
        logOkExample.setFiscalCode("SSSMNN75B01F257L");
        logOkExample.setGatewayName("FSE2-GTW-00");
        logOkExample.setMicroserviceName("gtw-dispatcher");
        opSubjApplication.setSubject_application_vendor("GTW");
        opSubjApplication.setSubject_application_version("GATEWAY");
        opSubjApplication.setSubject_application_version("R2");
        logOkExample.setOpSubjApplication(opSubjApplication);
        String logInComing = gson.toJson(logOkExample);


        ConsumerRecord<String, String> record1 = new ConsumerRecord<>(logTopic,
                1, 0, logTopic, logInComing);

        assertAll(() -> assertDoesNotThrow(() -> kafkaSRV.genericTopicListener(record1, 1)));

    }
}