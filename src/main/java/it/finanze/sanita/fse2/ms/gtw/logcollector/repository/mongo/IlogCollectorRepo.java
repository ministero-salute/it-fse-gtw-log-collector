package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo;

import java.util.List;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;

public interface IlogCollectorRepo {

     void save(LogCollectorBase ety);

     <T extends LogCollectorBase> long countUnprocessedLogs(Class<T> clazz);

     <T extends LogCollectorBase> List<T> findUnprocessedLog(Class<T> clazz);

     <T extends LogCollectorBase> int update(List<String> objectIds, Class<T> clazz);

     <T extends LogCollectorBase> int updateAllUnprocessed(Class<T> clazz);
}
