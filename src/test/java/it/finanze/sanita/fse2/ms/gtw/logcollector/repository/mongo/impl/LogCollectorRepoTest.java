package it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ActiveProfiles;

import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.config.LogCfg;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ProcessedStatusEnum;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class LogCollectorRepoTest {

    @SpyBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private LogCfg logCfg;

    @Autowired
    private IlogCollectorRepo logCollectorRepo;

    @Test
    @DisplayName("Test count UNPROCESSED logs in db")
    void testCountUnprocessedLogs() {
        long expectedResult = 5L;
        Class<? extends LogCollectorBase> clazz = LogCollectorControlETY.class;
        Query query = new Query(
                Criteria.where(Constants.Mongo.Fields.PROCESSED).is(ProcessedStatusEnum.UNPROCESSED.getCode())
                        .andOperator(Criteria.where(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID)
                                .ne(Constants.Mongo.Query.UNKNOWN_WORKFLOW_ID)));
        when(mongoTemplate.count(query, clazz)).thenReturn(expectedResult);

        long result = logCollectorRepo.countUnprocessedLogs(clazz);
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Test find UNPROCESSED logs in db")
    void testFindUnprocessedLog() {
        Class<? extends LogCollectorBase> clazz = LogCollectorControlETY.class;
        List<LogCollectorControlETY> expected = new ArrayList<>();
        expected.add(new LogCollectorControlETY());

        // Create query
        Query query = new Query(
                Criteria.where(Constants.Mongo.Fields.PROCESSED).is(ProcessedStatusEnum.UNPROCESSED.getCode())
                        .andOperator(Criteria.where(Constants.Mongo.Fields.WORKFLOW_INSTANCE_ID)
                                .ne(Constants.Mongo.Query.UNKNOWN_WORKFLOW_ID)));
        query.with(Sort.by(Constants.Mongo.Fields.OP_TIMESTAMP_START).ascending());

        // Mock methods
        doReturn(expected).when(mongoTemplate).find(query, LogCollectorControlETY.class);

        List<? extends LogCollectorBase> actual = logCollectorRepo.findUnprocessedLog(clazz);
        assertEquals(expected.size(), actual.size());
    }

    @Test
    @DisplayName("Test update logs with ids")
    void testUpdate() {
        Long lenght = 10L;
        List<String> objectIds = buildObjectIds(lenght);
        Class<? extends LogCollectorBase> clazz = LogCollectorControlETY.class;
        List<ObjectId> objectIdList = new ArrayList<>();
        for (String id : objectIds) {
            objectIdList.add(new ObjectId(id));
        }

        Query query = new Query(Criteria.where(Constants.Mongo.Fields.ID).in(objectIdList));
        Update update = Update.update(Constants.Mongo.Fields.PROCESSED, ProcessedStatusEnum.PROCESSING.getCode());

        UpdateResult updateResultMock = mock(UpdateResult.class);
        when(updateResultMock.getModifiedCount()).thenReturn(lenght);

        when(mongoTemplate.updateMulti(query, update,
                clazz)).thenReturn(updateResultMock);

        int result = logCollectorRepo.update(objectIds, clazz);
        assertEquals(lenght, result);
    }

    private List<String> buildObjectIds(Long lenght) {
        List<String> objectIds = new ArrayList<>();
        for (int i = 0; i < lenght; i++) {
            objectIds.add(String.valueOf(new ObjectId()));
        }
        return objectIds;
    }

}