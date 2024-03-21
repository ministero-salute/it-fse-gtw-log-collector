package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EsitoDTOTest {

    @Test
    void getNumInsert() {
        Integer numInsert = 10;

        EsitoDTO esitoDTO = new EsitoDTO();
        esitoDTO.setNumInsert(numInsert);

        assertEquals(numInsert, esitoDTO.getNumInsert());
    }

}