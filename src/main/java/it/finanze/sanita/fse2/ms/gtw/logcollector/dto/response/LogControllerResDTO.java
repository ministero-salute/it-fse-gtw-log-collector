package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.entity.LogCollectorControlETY;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LogControllerResDTO {


    private List<LogCollectorControlETY> entity;

    public LogControllerResDTO() {
        super();
    }

    public LogControllerResDTO(final LogTraceInfoDTO traceInfo, final List<LogCollectorControlETY> inEntity) {
        entity = inEntity;
    }

}
