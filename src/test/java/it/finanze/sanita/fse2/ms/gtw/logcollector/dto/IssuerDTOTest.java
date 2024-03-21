package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IssuerDTOTest {

    @Test
    void testDecodeIssuer_RegionalCertificate() {
        String issuer = "integrity:S1#190201234567XX";

        IssuerDTO dto = IssuerDTO.decodeIssuer(issuer);

        assertEquals(issuer, dto.getRawValue());
        assertEquals("190", dto.getRegion());
        assertNull(dto.getAsl());
        assertNull(dto.getEnterprise());
        assertNull(dto.getFiscalCode());
    }


}