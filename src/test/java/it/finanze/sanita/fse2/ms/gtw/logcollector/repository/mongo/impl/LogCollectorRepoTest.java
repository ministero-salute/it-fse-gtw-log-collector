package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.impl;

import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
@Disabled
class LogCollectorRepoTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private LogCfg logCfg;

    @InjectMocks
    private LogCollectorRepo logCollectorRepo;


    @Test
    public void testSave() {
        LogCollectorBase entity = new LogCollectorBase();
      //  logCollectorRepo.save(entity);
        verify(mongoTemplate, times(1)).save(entity);
    }

    @Test
    public void testCountUnprocessedLogs() {
        long expectedResult = 5L;
        Class clazz = LogCollectorBase.class;
        Query query = new Query(Criteria.where(Constants.Mongo.Fields.PROCESSED).is(Boolean.FALSE)
        			.andOperator(Criteria.where(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID).ne(Constants.Mongo.Query.UNKNOWN_WORKFLOW_ID)));
        when(mongoTemplate.count(query, clazz)).thenReturn(expectedResult);

        long result = logCollectorRepo.countUnprocessedLogs(clazz);
        assertEquals(expectedResult, result);
    }

//    @Test
//    public void testFindUnsendedLog() {
//        Class<? extends LogCollectorBase> clazz = LogCollectorBase.class;
//        List<LogCollectorBase> expectedLogs = new ArrayList<>();
//
//        Query query = new Query(Criteria.where(Constants.Mongo.Fields.PROCESSED).is(Boolean.FALSE));
//        query.limit(logCfg.getPageSize());
//        query.with(Sort.by(Constants.Mongo.Fields.OP_TIMESTAMP_START).ascending());
//
//        doReturn(expectedLogs).when(mongoTemplate).find(query,LogCollectorControlETY.class);
//
//        List<? extends LogCollectorBase> result = logCollectorRepo.findUnsendedLog(clazz);
//        assertEquals(expectedLogs, result);
//    }

    @Test
    public void testUpdate() {
        List<String> objectIds = new ArrayList<>();
        Class clazz = LogCollectorBase.class;

        List<ObjectId> objectIdList = new ArrayList<>();
        for (String id : objectIds) {
            objectIdList.add(new ObjectId(id));
        }

        Query query = new Query(Criteria.where(Constants.Mongo.Fields.ID).in(objectIdList));
        Update update = Update.update(Constants.Mongo.Fields.PROCESSED, true);

        UpdateResult updateResultMock = mock(UpdateResult.class);
        when(updateResultMock.getModifiedCount()).thenReturn(2L);

        when(mongoTemplate.updateMulti(query, update, clazz)).thenReturn(updateResultMock);

        int result = logCollectorRepo.update(objectIds, clazz);
        assertEquals(2, result);
    }
}