package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.bson.Document;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ProcessedStatusEnum;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.*;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.mongo.IlogCollectorRepo;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl.LogCollectorSrv;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig(LogCollectorSrv.class)
class LogCollectorIsolationTest {
    @Autowired private LogCollectorSrv service;
    @MockitoBean private IlogCollectorRepo repository;

    @Test
    void normalizesDatesIssuerLocalityAndApplication() throws Exception {
        Document input = new Document("log_type", "control-structured-log")
                .append("op_timestamp_start", "01-02-2025 10:20:30.000")
                .append("op_timestamp_end", "01-02-2025 10:21:30.000")
                .append("op_issuer", "prefix#120001")
                .append("op_locality", "name&2.16.840.1.113883.2.9.4.1.3&ISO^^^^120001123456")
                .append("op_application_id", "app")
                .append("op_application_vendor", "vendor")
                .append("op_application_version", "1.2");

        Document actual = service.buildDocumentToSave(input.toJson());

        SimpleDateFormat format = new SimpleDateFormat(Constants.App.Custom.DATE_PATTERN);
        assertEquals(format.parse("01-02-2025 10:20:30.000"), actual.getDate("op_timestamp_start"));
        assertEquals(format.parse("01-02-2025 10:21:30.000"), actual.getDate("op_timestamp_end"));
        assertEquals("120", actual.get("op_issuer", Document.class).getString("region"));
        assertEquals("001", actual.get("op_issuer", Document.class).getString("asl"));
        assertEquals("123456", actual.get("op_locality", Document.class).getString("structure"));
        Document application = actual.get("op_subj_application", Document.class);
        assertEquals("app", application.getString("subject_application_id"));
        assertEquals("vendor", application.getString("subject_application_vendor"));
        assertEquals("1.2", application.getString("subject_application_version"));
        assertFalse(actual.containsKey("op_application_id"));
        assertFalse(actual.containsKey("op_application_vendor"));
        assertFalse(actual.containsKey("op_application_version"));
    }

    @Test
    void omitsAbsentOrEmptyOptionalFields() throws Exception {
        Document actual = service.buildDocumentToSave("{\"op_issuer\":\"\",\"op_locality\":\"\",\"op_application_id\":\"\"}");
        assertNull(actual.get("op_timestamp_start"));
        assertNull(actual.get("op_timestamp_end"));
        assertFalse(actual.containsKey("op_issuer"));
        assertFalse(actual.containsKey("op_locality"));
        assertFalse(actual.containsKey("op_subj_application"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"op_application_id", "op_application_vendor", "op_application_version"})
    void retainsAnApplicationWithOnlyOneNonEmptyField(String field) throws Exception {
        Document actual = service.buildDocumentToSave(new Document(field, "value").toJson());
        assertEquals(1, actual.get("op_subj_application", Document.class).size());
        assertTrue(actual.get("op_subj_application", Document.class).containsValue("value"));
        assertFalse(actual.containsKey(field));
    }

    @ParameterizedTest
    @ValueSource(strings = {"control-structured-log", "kpi-structured-log", "other"})
    void persistsCorrectEntityAndResetsProcessedStatus(String type) {
        service.saveLogEvent(new Document("log_type", type).append("processed", 99).toJson());
        ArgumentCaptor<LogCollectorBase> saved = ArgumentCaptor.forClass(LogCollectorBase.class);
        verify(repository).save(saved.capture());
        assertEquals(type.equals("control-structured-log") ? LogCollectorControlETY.class : LogCollectorKpiETY.class,
                saved.getValue().getClass());
        assertEquals(ProcessedStatusEnum.UNPROCESSED.getCode(), saved.getValue().getProcessed());
    }

    @Test
    void rejectsInvalidDateBeforePersistence() {
        assertThrows(ParseException.class, () -> service.buildDocumentToSave("{\"op_timestamp_start\":\"invalid\"}"));
        verifyNoInteractions(repository);
    }

    @Test
    void wrapsMalformedJsonWithoutSaving() {
        BusinessException failure = assertThrows(BusinessException.class, () -> service.saveLogEvent("not-json"));
        assertNotNull(failure.getCause());
        verifyNoInteractions(repository);
    }

    @Test
    void preservesRepositoryFailureAsCause() {
        RuntimeException cause = new IllegalStateException("Mongo unavailable");
        doThrow(cause).when(repository).save(any());
        assertSame(cause, assertThrows(BusinessException.class, () -> service.saveLogEvent("{}")).getCause());
    }
}
