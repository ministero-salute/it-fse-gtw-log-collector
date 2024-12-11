package it.finanze.sanita.fse2.ms.gtw.logcollector.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RawValidationEnumTest {

    @Test
    void testEnumValues() {
        assertEquals(6, RawValidationEnum.values().length);

        assertEquals("00", RawValidationEnum.OK.getCode());
        assertEquals("Errore semantico", RawValidationEnum.SEMANTIC_ERROR.getDescription());
        assertEquals("Schematron not found", RawValidationEnum.SCHEMATRON_NOT_FOUND.getDescription());
    }

}