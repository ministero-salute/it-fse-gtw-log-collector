package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.mongodb.client.result.UpdateResult;

import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ProcessedStatusEnum;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.impl.LogCollectorRepo;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(LogCollectorRepo.class)
class RepositoryIsolationTest {
    @Autowired private LogCollectorRepo repository;
    @MockitoBean private MongoTemplate mongo;

    @Test
    void savesOriginalEntity() {
        LogCollectorControlETY entity = new LogCollectorControlETY();
        repository.save(entity);
        verify(mongo).save(same(entity));
    }

    @Test
    void countFiltersUnprocessedAndUnknownWorkflow() {
        when(mongo.count(any(Query.class), eq(LogCollectorControlETY.class))).thenReturn(7L);
        assertEquals(7, repository.countUnprocessedLogs(LogCollectorControlETY.class));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).count(query.capture(), eq(LogCollectorControlETY.class));
        assertSelection(query.getValue());
    }

    @Test
    void findFiltersAndSortsOldestFirst() {
        List<LogCollectorControlETY> expected = List.of(new LogCollectorControlETY());
        when(mongo.find(any(Query.class), eq(LogCollectorControlETY.class))).thenReturn(expected);
        assertSame(expected, repository.findUnprocessedLog(LogCollectorControlETY.class));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).find(query.capture(), eq(LogCollectorControlETY.class));
        assertSelection(query.getValue());
        assertEquals(new Document("op_timestamp_start", 1), query.getValue().getSortObject());
    }

    @Test
    void updateConvertsIdsAndMarksProcessing() {
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(LogCollectorControlETY.class)))
                .thenReturn(UpdateResult.acknowledged(3, 2L, null));
        String id = "507f1f77bcf86cd799439011";
        assertEquals(2, repository.update(List.of(id), LogCollectorControlETY.class));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateMulti(query.capture(), update.capture(), eq(LogCollectorControlETY.class));
        assertEquals(new Document("_id", new Document("$in", List.of(new ObjectId(id)))), query.getValue().getQueryObject());
        assertProcessing(update.getValue());
    }

    @Test
    void invalidIdIsRejectedWithoutMongoCall() {
        assertThrows(IllegalArgumentException.class, () -> repository.update(List.of("invalid"), LogCollectorControlETY.class));
        verifyNoInteractions(mongo);
    }

    @Test
    void updateAllTargetsOnlyUnprocessedAndReturnsModifiedCount() {
        when(mongo.updateMulti(any(Query.class), any(Update.class), eq(LogCollectorControlETY.class)))
                .thenReturn(UpdateResult.acknowledged(5, 4L, null));
        assertEquals(4, repository.updateAllUnprocessed(LogCollectorControlETY.class));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateMulti(query.capture(), update.capture(), eq(LogCollectorControlETY.class));
        assertEquals(new Document("processed", ProcessedStatusEnum.UNPROCESSED.getCode()), query.getValue().getQueryObject());
        assertProcessing(update.getValue());
    }

    @Test
    void mongoFailureIsPropagated() {
        IllegalStateException cause = new IllegalStateException("Mongo unavailable");
        when(mongo.count(any(Query.class), eq(LogCollectorControlETY.class))).thenThrow(cause);
        assertSame(cause, assertThrows(IllegalStateException.class,
                () -> repository.countUnprocessedLogs(LogCollectorControlETY.class)));
    }

    private void assertSelection(Query query) {
        assertEquals(new Document("processed", ProcessedStatusEnum.UNPROCESSED.getCode())
                .append("$and", List.of(new Document("workflow_instance_id", new Document("$ne", "UNKNOWN_WORKFLOW_ID")))),
                query.getQueryObject());
    }

    private void assertProcessing(Update update) {
        assertEquals(new Document("$set", new Document("processed", ProcessedStatusEnum.PROCESSING.getCode())),
                update.getUpdateObject());
    }
}
