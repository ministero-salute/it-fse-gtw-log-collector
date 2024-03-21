package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogControllerResDTOTest {

    @Test
    public void testConstructorWithList() {
        List<LogCollectorControlETY> entityList = new ArrayList<>();
        LogCollectorControlETY entity1 = new LogCollectorControlETY();
        entityList.add(entity1);

        LogControllerResDTO responseDTO = new LogControllerResDTO(null, entityList);

        assertNotNull(responseDTO);
        assertEquals(entityList, responseDTO.getEntity());
    }

    @Test
    public void testSetterAndGetter() {
        List<LogCollectorControlETY> entityList = new ArrayList<>();
        LogCollectorControlETY entity1 = new LogCollectorControlETY();
        entityList.add(entity1);

        LogControllerResDTO responseDTO = new LogControllerResDTO();
        responseDTO.setEntity(entityList);

        assertNotNull(responseDTO);
        assertEquals(entityList, responseDTO.getEntity());
    }

}