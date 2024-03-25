package it.finanze.sanita.fse2.ms.gtw.logcollector.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ISchedulerSrv;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Component
@Slf4j
public class ProcessKpiLogScheduler {

	@Autowired
	private ISchedulerSrv schedulerSrv;

	/**
	 * Scheduler.
	 */
	@Scheduled(cron = "${scheduler.kpi-log}")
	@SchedulerLock(name = "processKpiLogScheduler", lockAtMostFor = "60m")
	public void schedulingTask() {
		log.debug("Recupero dei kpi log da mongo - Start");
		schedulerSrv.scheduleLog(LogCollectorKpiETY.class);
		log.debug("Recupero dei kpi log da mongo - End");
	}
}
