package it.finanze.sanita.fse2.ms.gtw.logcollector.scheduler;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka.KafkaTopicCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IKafkaSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.DateUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProcessKpiLogScheduler {


	@Autowired
	private LogCfg logCfg;

	@Autowired
	private IConfigSRV configSRV;

	@Autowired
	private KafkaTopicCfg kafkaTopicCfg;

	@Autowired
	private IlogCollectorRepo logCollectorRepo;

	@Autowired
	private IKafkaSRV kafkaSRV;

	/**
	 * Scheduler.
	 */
	@Scheduled(cron = "${scheduler.kpi-log}")
	@SchedulerLock(name = "processKpiLogScheduler", lockAtMostFor = "60m")
	public void schedulingTask() {
		log.debug("Recupero dei kpi log da mongo - Start");
		scheduleKpiLog();
		log.debug("Recupero dei kpi log da mongo - End");
	}


	public ResultDto scheduleKpiLog() {
		ResultDto resultDto = new ResultDto();
		long totalLog = logCollectorRepo.countUnprocessedLogs(LogCollectorKpiETY.class);

		if(totalLog >= configSRV.getPageSize()){
			List<? extends LogCollectorBase> logs = logCollectorRepo.findUnprocessedLog(LogCollectorKpiETY.class);
			List<String> objectId = logs.stream().map(log -> log.getId()).collect(Collectors.toList());
			logCollectorRepo.update(objectId,LogCollectorKpiETY.class);
			kafkaSRV.sendMessage(kafkaTopicCfg.getSenderTopic(),"key", Constants.Mongo.Fields.LOG_TYPE_KPI);
			log.info("kafkaNoTproducer -> WakeUp LogSender"+ Constants.Mongo.Fields.LOG_TYPE_KPI);
		}else if (totalLog != 0){
			List<? extends LogCollectorBase> logs = logCollectorRepo.findUnprocessedLog(LogCollectorKpiETY.class);
			Date oldestLog = DateUtility.addDay(logs.get(0).getTimestampStart(), logCfg.getExpiringDateDAy());
            if (new Date().after(oldestLog)) {
				List<String> objectId = logs.stream().map(log -> log.getId()).collect(Collectors.toList());
				logCollectorRepo.update(objectId,LogCollectorKpiETY.class);
            	kafkaSRV.sendMessage(kafkaTopicCfg.getSenderTopic(),"key", Constants.Mongo.Fields.LOG_TYPE_KPI);
				log.info("kafkaNoTproducer -> [Expired Logs] WakeUp LogSender "+Constants.Mongo.Fields.LOG_TYPE_KPI);
            }
		}

		resultDto.setTotalRecord((int)totalLog);
		resultDto.setPageSize(configSRV.getPageSize());

		return resultDto;
	}
}
