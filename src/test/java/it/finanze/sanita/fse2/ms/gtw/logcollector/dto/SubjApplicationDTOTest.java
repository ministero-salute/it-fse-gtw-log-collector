package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubjApplicationDTOTest {


    @Test
    void testGetterAndSetter() {
        SubjApplicationDTO dto = new SubjApplicationDTO();

        dto.setSubject_application_id("GTW");
        dto.setSubject_application_vendor("GATEWAY");
        dto.setSubject_application_version("R2");

        assertEquals("GTW", dto.getSubject_application_id());
        assertEquals("GATEWAY", dto.getSubject_application_vendor());
        assertEquals("R2", dto.getSubject_application_version());
    }


}