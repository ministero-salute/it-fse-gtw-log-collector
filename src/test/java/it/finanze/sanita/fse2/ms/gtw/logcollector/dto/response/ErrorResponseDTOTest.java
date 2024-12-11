package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseDTOTest {

    @Test
    public void testBuilder() {
        String traceID = "TraceID";
        String spanID = "SpanID";
        String type = "Type";
        String title = "Title";
        String detail = "Detail";
        Integer status = 500;
        String instance = "Instance";

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .traceID(traceID)
                .spanID(spanID)
                .type(type)
                .title(title)
                .detail(detail)
                .status(status)
                .instance(instance)
                .build();

        assertNotNull(errorResponseDTO);
        assertEquals(traceID, errorResponseDTO.getTraceID());
        assertEquals(spanID, errorResponseDTO.getSpanID());
        assertEquals(type, errorResponseDTO.getType());
        assertEquals(title, errorResponseDTO.getTitle());
        assertEquals(detail, errorResponseDTO.getDetail());
        assertEquals(status, errorResponseDTO.getStatus());
        assertEquals(instance, errorResponseDTO.getInstance());
    }

    @Test
    public void testBuilderWithLogTraceInfoDTO() {

        LogTraceInfoDTO logTraceInfoDTO = new LogTraceInfoDTO("SpanId","TraceID");

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(logTraceInfoDTO);

        assertEquals("SpanId",errorResponseDTO.getSpanID());
        assertEquals("TraceID",errorResponseDTO.getTraceID());

    }




}