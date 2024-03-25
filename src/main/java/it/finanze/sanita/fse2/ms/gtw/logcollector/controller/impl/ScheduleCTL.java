package it.finanze.sanita.fse2.ms.gtw.logcollector.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.gtw.logcollector.controller.IScheduleCTL;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.ISchedulerSrv;

@RestController
public class ScheduleCTL implements IScheduleCTL {

	@Autowired
	private ISchedulerSrv schedulerSrv;

	@Override
	public ResultDto runControlLogScheduler() {
		return schedulerSrv.scheduleLog(LogCollectorControlETY.class);
	}

	@Override
	public ResultDto runKpiLogScheduler() {
		return schedulerSrv.scheduleLog(LogCollectorKpiETY.class);
	}

}
