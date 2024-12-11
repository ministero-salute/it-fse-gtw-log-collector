package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalityDTOTest {


    @Test
    public void testDecodeLocality_WithValidLocality() {
        String locality = "201123456";
        LocalityDTO localityDTO = LocalityDTO.decodeLocality(locality);

        assertNotNull(localityDTO);
        assertEquals("201", localityDTO.getAslCode());
        assertEquals("123456", localityDTO.getStructure());
    }

    @Test
    public void testDecodeLocality_WithEmptyLocality() {
        String locality = null;
        LocalityDTO localityDTO = LocalityDTO.decodeLocality(locality);

        assertNotNull(localityDTO);
        assertEquals(null, localityDTO.getAslCode());
        assertEquals(null, localityDTO.getStructure());
        assertEquals(locality, localityDTO.getRawValue());
    }

    @Test
    void testBuilder() {
        String rawValue = "201123456";
        String aslCode = "201";
        String structure = "123456";

        LocalityDTO localityDTO = LocalityDTO.builder()
                .rawValue(rawValue)
                .aslCode(aslCode)
                .structure(structure)
                .build();

        assertNotNull(localityDTO);
        assertEquals(rawValue, localityDTO.getRawValue());
        assertEquals(aslCode, localityDTO.getAslCode());
        assertEquals(structure, localityDTO.getStructure());
    }


}