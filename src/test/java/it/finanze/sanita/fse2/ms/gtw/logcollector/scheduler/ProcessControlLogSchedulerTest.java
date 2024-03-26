package it.finanze.sanita.fse2.ms.gtw.logcollector.scheduler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ISchedulerSrv;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class ProcessControlLogSchedulerTest {

    @Autowired
    private ProcessControlLogScheduler scheduler;

    @MockBean
    private ISchedulerSrv service;

    @Test
    @DisplayName("Test scheduler Control Log")
    void scheduleControlLogTest() {
        scheduler.schedulingTask();
        verify(service, times(1)).scheduleLog(LogCollectorControlETY.class);
    }

}