package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.KafkaTopicCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ISchedulerSrv;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
public class SchedulerSrvTest {

    @Autowired
    private ISchedulerSrv schedulerSrv;

    @MockBean
    private IlogCollectorRepo logCollectorRepo;

    @MockBean
    private IConfigSRV configSRV;

    @MockBean
    private KafkaTopicCfg kafkaTopicCfg;

    @MockBean
    private IKafkaSRV kafkaSRV;

    @ParameterizedTest
    @ValueSource(classes = { LogCollectorControlETY.class, LogCollectorKpiETY.class })
    @DisplayName("Test method for scheduler logs")
    void testScheduleLog(Class<? extends LogCollectorBase> clazz) {
        // Constants
        long totalLog = 2L;
        int pageSize = 1;

        // Mock methods
        when(logCollectorRepo.countUnprocessedLogs(clazz)).thenReturn(totalLog);
        when(configSRV.getPageSize()).thenReturn(pageSize);

        ResultDto result = schedulerSrv.scheduleLog(clazz);
        assertEquals((int) totalLog, result.getTotalRecord());
        assertEquals(pageSize, result.getPageSize());
        assertEquals((int) totalLog, result.getProcessedLog());
    }

}
