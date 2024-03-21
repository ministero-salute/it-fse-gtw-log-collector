package it.finanze.sanita.fse2.ms.gtw.logcollector.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegionEnumTest {

    @Test
    void testGetFromCode() {
        RegionEnum regionValid = RegionEnum.getFromCode("001");
        assertNotNull(regionValid);
        assertEquals("SASN", regionValid.getDescription());

        RegionEnum regionInvalid = RegionEnum.getFromCode("9999");
        assertNull(regionInvalid);
    }

    @Test
    void testEnumValues() {
        assertEquals(24, RegionEnum.values().length);

        assertEquals("Regione Sicilia", RegionEnum.SICILIA.getDescription());
        assertEquals("Ministero della Salute", RegionEnum.MS.getDescription());
    }

}