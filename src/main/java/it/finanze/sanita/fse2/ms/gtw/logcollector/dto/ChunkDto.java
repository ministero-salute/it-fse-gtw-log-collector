package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorBase;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorKpiETY;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkDto {

        List<? extends LogCollectorBase> records;

        List<LogCollectorControlETY> controllRecord;

        List<LogCollectorKpiETY> kpiRecord;

        String logType;

}
