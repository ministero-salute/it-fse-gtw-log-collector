package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogTraceInfoDTOTest {

    @Test
    public void testConstructorAndGetters() {
        String spanID = "SpanID";
        String traceID = "TraceID";

        LogTraceInfoDTO logTraceInfoDTO = new LogTraceInfoDTO(spanID, traceID);

        assertEquals(spanID, logTraceInfoDTO.getSpanID());
        assertEquals(traceID, logTraceInfoDTO.getTraceID());
    }

    @Test
    public void testEqualsAndHashCode() {
        String spanID = "SpanID";
        String traceID = "TraceID";

        LogTraceInfoDTO logTraceInfoDTO1 = new LogTraceInfoDTO(spanID, traceID);
        LogTraceInfoDTO logTraceInfoDTO2 = new LogTraceInfoDTO(spanID, traceID);

        assertEquals(logTraceInfoDTO1, logTraceInfoDTO2);
        assertEquals(logTraceInfoDTO1.hashCode(), logTraceInfoDTO2.hashCode());
    }


}