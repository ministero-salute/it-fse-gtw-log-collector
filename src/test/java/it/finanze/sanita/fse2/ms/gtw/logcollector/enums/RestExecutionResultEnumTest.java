package it.finanze.sanita.fse2.ms.gtw.logcollector.enums;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(Constants.Profile.TEST)
class RestExecutionResultEnumTest {


    @Test
    void testEnumValues() {
        assertEquals(18, RestExecutionResultEnum.values().length);

        assertEquals("00", RestExecutionResultEnum.OK.getType());
        assertEquals("Errore in fase di estrazione del CDA.", RestExecutionResultEnum.MINING_CDA_ERROR.getTitle());
        assertEquals(ErrorLogEnum.KO_PUB, RestExecutionResultEnum.SYNTAX_ERROR.getErrorCategory());
    }

    @Test
    void testGet() {
        assertEquals(RestExecutionResultEnum.SYNTAX_ERROR, RestExecutionResultEnum.get("/msg/syntax"));
        assertEquals(RestExecutionResultEnum.OK, RestExecutionResultEnum.get("00"));
    }

    @Test
    void testFromRawResult() {
        assertEquals(RestExecutionResultEnum.VOCABULARY_ERROR, RestExecutionResultEnum.fromRawResult(RawValidationEnum.VOCABULARY_ERROR));
        assertEquals(RestExecutionResultEnum.OK, RestExecutionResultEnum.fromRawResult(RawValidationEnum.OK));
        assertEquals(RestExecutionResultEnum.SEMANTIC_ERROR, RestExecutionResultEnum.fromRawResult(RawValidationEnum.SEMANTIC_ERROR));
        assertEquals(RestExecutionResultEnum.SYNTAX_ERROR, RestExecutionResultEnum.fromRawResult(RawValidationEnum.SYNTAX_ERROR));
        assertEquals(RestExecutionResultEnum.GENERIC_ERROR, RestExecutionResultEnum.fromRawResult(RawValidationEnum.SCHEMATRON_NOT_FOUND));

    }

}