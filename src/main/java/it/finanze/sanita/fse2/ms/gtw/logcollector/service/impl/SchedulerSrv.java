package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.KafkaTopicCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ISchedulerSrv;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.DateUtility;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchedulerSrv implements ISchedulerSrv {

    @Autowired
    private IlogCollectorRepo logCollectorRepo;

    @Autowired
    private LogCfg logCfg;

    @Autowired
    private IConfigSRV configSRV;

    @Autowired
    private KafkaTopicCfg kafkaTopicCfg;

    @Autowired
    private IKafkaSRV kafkaSRV;

    @Override
    public <T extends LogCollectorBase> ResultDto scheduleLog(Class<T> clazz) {
        ResultDto resultDto = new ResultDto();
        String logType = clazz.equals(LogCollectorControlETY.class) ? Constants.Mongo.Fields.LOG_TYPE_CONTROL
                : Constants.Mongo.Fields.LOG_TYPE_KPI;
        // Calcolo numero di log "UNPROCESSED"
        long totalLog = logCollectorRepo.countUnprocessedLogs(clazz);
        long processedLog = 0;
        int pageSize = configSRV.getPageSize();

        // Se il numero di log da processare è superiore alla PAGE_SIZE
        if (totalLog >= pageSize) {
            // Aggiorno tutti i log da "UNPROCESSED" -> "PROCESSING"
            logCollectorRepo.updateAllUnprocessed(clazz);
            // Invio il messaggio Kafka al Sender per far inviare i log
            kafkaSRV.sendMessage(kafkaTopicCfg.getSenderTopic(), "logType", logType);
            log.debug("kafkaNoTproducer -> WakeUp LogSender. LOG TYPE : " + logType);
            processedLog = totalLog;
        } else if (totalLog != 0) {
            // Se ci sono log da inviare ma non sono abbastanza per la PAGE_SIZE
            List<T> logs = logCollectorRepo.findUnprocessedLog(clazz);
            Date oldestLog = DateUtility.addDay(logs.get(0).getTimestampStart(), logCfg.getExpiringDateDay());
            // Se ci sono dei log scaduti li invio ugualmente
            if (new Date().after(oldestLog)) {
                List<String> objectId = logs.stream().map(log -> log.getId()).collect(Collectors.toList());
                logCollectorRepo.update(objectId, clazz);
                kafkaSRV.sendMessage(kafkaTopicCfg.getSenderTopic(), "logType", logType);
                log.debug("kafkaNoTproducer -> [Expired Logs] WakeUp LogSender. LOG TYPE : " + logType);
                processedLog = totalLog;
            }
        }

        resultDto.setTotalRecord((int) totalLog);
        resultDto.setPageSize(pageSize);
        resultDto.setProcessedLog((int) processedLog);

        return resultDto;
    }

}
