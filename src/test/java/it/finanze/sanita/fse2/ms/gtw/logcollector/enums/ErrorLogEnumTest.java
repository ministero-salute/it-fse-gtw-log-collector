package it.finanze.sanita.fse2.ms.gtw.logcollector.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorLogEnumTest {

    @Test
    void testEnumValues() {
        assertEquals(11, ErrorLogEnum.values().length);

        assertEquals("Errore nella firma del PDF", ErrorLogEnum.KO_SIGN_PDF.getDescription());
        assertEquals("KO-MONGO-DB-NOT-FOUND", ErrorLogEnum.KO_MONGO_DB_NOT_FOUND.getCode());
    }

}