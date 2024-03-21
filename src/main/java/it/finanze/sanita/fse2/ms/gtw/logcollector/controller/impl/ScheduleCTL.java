package it.finanze.sanita.fse2.ms.gtw.logcollector.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.finanze.sanita.fse2.ms.gtw.logcollector.controller.IScheduleCTL;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.scheduler.ProcessControlLogScheduler;
import it.finanze.sanita.fse2.ms.gtw.logcollector.scheduler.ProcessKpiLogScheduler;

@RestController
public class ScheduleCTL implements IScheduleCTL {

	@Autowired
	private ProcessControlLogScheduler processControlLogScheduler;
	
	@Autowired
	private ProcessKpiLogScheduler processKpiLogScheduler;
	
	@Override
	public ResultDto runControlLogScheduler() {
		return processControlLogScheduler.scheduleControlLog();
	}

	@Override
	public ResultDto runKpiLogScheduler() {
		return processKpiLogScheduler.scheduleKpiLog();
	}
 

}
