package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LogTraceInfoDTO {

    /**
     * Span.
     */
    private final String spanID;

    /**
     * Trace.
     */
    private final String traceID;
}