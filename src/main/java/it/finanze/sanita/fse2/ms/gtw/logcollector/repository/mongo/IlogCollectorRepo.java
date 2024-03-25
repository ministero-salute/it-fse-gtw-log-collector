package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;

public interface IlogCollectorRepo {

     long countUnprocessedLogs(Class<? extends LogCollectorBase> clazz);

     List<? extends LogCollectorBase> findUnprocessedLog(Class<? extends LogCollectorBase> clazz);

     void save(LogCollectorBase ety);

     int update(List<String> objectIds, Class<? extends LogCollectorBase> clazz);

     int updateAllUnprocessed(Class<? extends LogCollectorBase> clazz);
}
