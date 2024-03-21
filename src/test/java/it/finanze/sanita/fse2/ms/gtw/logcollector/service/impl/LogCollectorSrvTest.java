package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.impl.LogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IConfigSRV;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class LogCollectorSrvTest {

    @Mock
    private LogCollectorRepo logCollectorRepo;

    @Mock
    private LogCfg logCfg;

    @Mock
    private IConfigSRV configSRV;

    @InjectMocks
    private LogCollectorSrv logCollectorService;

    @Test
    void buildDocumentToSaveThrowBusinessException() {
        LogCollectorControlETY log = new LogCollectorControlETY();
        String logInComing = String.valueOf(log);

        assertThrows(BusinessException.class,()->logCollectorService.saveLogEvent(logInComing));
    }






}