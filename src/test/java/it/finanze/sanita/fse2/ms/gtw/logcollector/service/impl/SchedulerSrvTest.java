package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
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
class SchedulerSrvTest {

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

    @MockBean
    private LogCfg logCfg;

    @ParameterizedTest
    @MethodSource("provideArgumentsForServiceTest")
    @DisplayName("Test method for scheduler logs")
    <T extends LogCollectorBase> void testScheduleLog(Class<T> clazz, long totalLog, int pageSize,
            int expiringDateDay, int processedLog) {

        // Mock methods
        when(logCollectorRepo.countUnprocessedLogs(clazz)).thenReturn(totalLog);
        when(configSRV.getPageSize()).thenReturn(pageSize);
        when(logCollectorRepo.findUnprocessedLog(clazz)).thenReturn(buildLogs(clazz, (int) totalLog));
        when(logCfg.getExpiringDateDay()).thenReturn(expiringDateDay);

        // Call method
        ResultDto result = schedulerSrv.scheduleLog(clazz);

        // Assertions
        assertEquals((int) totalLog, result.getTotalRecord());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(processedLog, result.getProcessedLog());
    }

    private <T extends LogCollectorBase> List<T> buildLogs(Class<T> clazz, int size) {
        List<T> out = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                T temp = clazz.newInstance();
                temp.setTimestampStart(new Date());
                temp.setId("test");
                out.add(temp);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    private static Stream<Arguments> provideArgumentsForServiceTest() {
        // Argomenti del test
        // clazz - totalLogs - pageSize - expiringDateDay - processedLog (Result)
        return Stream.of(
                Arguments.of(LogCollectorControlETY.class, 100, 5, 5, 100),
                Arguments.of(LogCollectorControlETY.class, 10, 50, 5, 0),
                Arguments.of(LogCollectorControlETY.class, 10, 50, -1, 10),
                Arguments.of(LogCollectorKpiETY.class, 100, 5, 5, 100),
                Arguments.of(LogCollectorKpiETY.class, 10, 50, 5, 0),
                Arguments.of(LogCollectorKpiETY.class, 10, 50, -1, 10));
    }

}