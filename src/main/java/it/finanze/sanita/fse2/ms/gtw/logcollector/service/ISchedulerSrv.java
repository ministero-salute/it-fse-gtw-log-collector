package it.finanze.sanita.fse2.ms.gtw.logcollector.service;

import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.ResultDto;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;

public interface ISchedulerSrv {

    <T extends LogCollectorBase> ResultDto scheduleLog(Class<T> clazz);

}
