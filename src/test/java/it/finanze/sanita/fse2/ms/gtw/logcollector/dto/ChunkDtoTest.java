package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChunkDtoTest {


    @Test
    void getRecords() {

        List<LogCollectorBase> records = new ArrayList<>();
        ChunkDto chunkDto = new ChunkDto();
        chunkDto.setRecords(records);
        assertEquals(records, chunkDto.getRecords());
    }

    @Test
    void getControllRecord() {

        List<LogCollectorControlETY> controlRecords = new ArrayList<>();

        ChunkDto chunkDto = new ChunkDto();
        chunkDto.setControllRecord(controlRecords);

        assertEquals(controlRecords, chunkDto.getControllRecord());


    }

    @Test
    void getKpiRecord() {
        List<LogCollectorKpiETY> kpiRecords = new ArrayList<>();
        ChunkDto chunkDto = new ChunkDto();
        chunkDto.setKpiRecord(kpiRecords);

        assertEquals(kpiRecords, chunkDto.getKpiRecord());

    }

    @Test
    void getLogType() {

        String logType = "testLogType";
        ChunkDto chunkDto = new ChunkDto();
        chunkDto.setLogType(logType);

        assertEquals(logType, chunkDto.getLogType());

    }

    @Test
    void testAllArgsConstruct() {
        List<LogCollectorBase> records = new ArrayList<>();
        List<LogCollectorControlETY> controlRecords = new ArrayList<>();
        List<LogCollectorKpiETY> kpiRecords = new ArrayList<>();
        String logType = "testLogType";

        ChunkDto chunkDto = new ChunkDto(records, controlRecords, kpiRecords, logType);

        assertEquals(records, chunkDto.getRecords());
        assertEquals(controlRecords, chunkDto.getControllRecord());
        assertEquals(kpiRecords, chunkDto.getKpiRecord());
        assertEquals(logType, chunkDto.getLogType());
    }

}