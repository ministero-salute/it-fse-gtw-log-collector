package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.KafkaTopicCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl.SchedulerSrv;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(SchedulerSrv.class)
class SchedulerIsolationTest {
    @Autowired private SchedulerSrv service;
    @MockitoBean private IlogCollectorRepo repository;
    @MockitoBean private LogCfg logConfig;
    @MockitoBean private KafkaTopicCfg topics;
    @MockitoBean private IConfigSRV config;
    @MockitoBean private IKafkaSRV kafka;

    @BeforeEach
    void configure() {
        when(config.getPageSize()).thenReturn(10);
        when(topics.getSenderTopic()).thenReturn("sender");
        when(logConfig.getExpiringDateDay()).thenReturn(2);
    }

    @Test
    void emptyQueueDoesNotReadOrPublish() {
        ResultDto result = service.scheduleLog(LogCollectorControlETY.class);
        assertResult(result, 0, 0);
        verify(repository, never()).findUnprocessedLog(any());
        verify(repository, never()).updateAllUnprocessed(any());
        verifyNoInteractions(kafka);
    }

    @ParameterizedTest
    @ValueSource(longs = {10, 11})
    void fullControlBatchIsMarkedBeforePublishing(long count) {
        when(repository.countUnprocessedLogs(LogCollectorControlETY.class)).thenReturn(count);
        assertResult(service.scheduleLog(LogCollectorControlETY.class), (int) count, (int) count);
        var order = inOrder(repository, kafka);
        order.verify(repository).updateAllUnprocessed(LogCollectorControlETY.class);
        order.verify(kafka).sendMessage("sender", "logType", "control-structured-log");
        verify(repository, never()).findUnprocessedLog(any());
    }

    @Test
    void fullKpiBatchPublishesKpiType() {
        when(repository.countUnprocessedLogs(LogCollectorKpiETY.class)).thenReturn(10L);
        assertResult(service.scheduleLog(LogCollectorKpiETY.class), 10, 10);
        verify(kafka).sendMessage("sender", "logType", "kpi-structured-log");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void partialBatchIsPublishedOnlyWhenOldestRecordHasExpired(boolean expired) {
        LogCollectorControlETY log = new LogCollectorControlETY();
        log.setId("507f1f77bcf86cd799439011");
        log.setTimestampStart(expired ? Date.from(Instant.parse("2000-01-01T00:00:00Z"))
                : Date.from(Instant.parse("2100-01-01T00:00:00Z")));
        when(repository.countUnprocessedLogs(LogCollectorControlETY.class)).thenReturn(1L);
        when(repository.findUnprocessedLog(LogCollectorControlETY.class)).thenReturn(List.of(log));
        assertResult(service.scheduleLog(LogCollectorControlETY.class), 1, expired ? 1 : 0);
        if (expired) {
            verify(repository).update(List.of(log.getId()), LogCollectorControlETY.class);
            verify(kafka).sendMessage("sender", "logType", "control-structured-log");
        } else {
            verify(repository, never()).update(anyList(), any());
            verifyNoInteractions(kafka);
        }
    }

    @Test
    void databaseFailurePreventsPublishing() {
        when(repository.countUnprocessedLogs(LogCollectorControlETY.class)).thenReturn(10L);
        RuntimeException cause = new IllegalStateException("update failed");
        when(repository.updateAllUnprocessed(LogCollectorControlETY.class)).thenThrow(cause);
        assertSame(cause, assertThrows(IllegalStateException.class,
                () -> service.scheduleLog(LogCollectorControlETY.class)));
        verifyNoInteractions(kafka);
    }

    @Test
    void kafkaFailureIsNotReportedAsSuccess() {
        when(repository.countUnprocessedLogs(LogCollectorControlETY.class)).thenReturn(10L);
        RuntimeException cause = new IllegalStateException("send failed");
        when(kafka.sendMessage(anyString(), anyString(), anyString())).thenThrow(cause);
        assertSame(cause, assertThrows(IllegalStateException.class,
                () -> service.scheduleLog(LogCollectorControlETY.class)));
        verify(repository).updateAllUnprocessed(LogCollectorControlETY.class);
    }

    private void assertResult(ResultDto result, int total, int processed) {
        assertEquals(total, result.getTotalRecord());
        assertEquals(10, result.getPageSize());
        assertEquals(processed, result.getProcessedLog());
    }
}
