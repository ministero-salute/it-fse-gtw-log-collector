package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationErrorResponseDTOTest {


    @Test
    public void testConstructorAndGetters() {
        String traceID = "TraceID";
        String spanID = "SpanID";
        String type = "Type";
        String title = "Title";
        String detail = "Detail";
        Integer status = 400;
        String instance = "Instance";
        String workflowInstanceId = "WorkflowInstanceId";

        ValidationErrorResponseDTO errorResponseDTO = new ValidationErrorResponseDTO(
                new LogTraceInfoDTO(spanID, traceID),
                type,
                title,
                detail,
                status,
                instance,
                workflowInstanceId);

        assertEquals(traceID, errorResponseDTO.getTraceID());
        assertEquals(spanID, errorResponseDTO.getSpanID());
        assertEquals(type, errorResponseDTO.getType());
        assertEquals(title, errorResponseDTO.getTitle());
        assertEquals(detail, errorResponseDTO.getDetail());
        assertEquals(status, errorResponseDTO.getStatus());
        assertEquals(instance, errorResponseDTO.getInstance());
        assertEquals(workflowInstanceId, errorResponseDTO.getWorkflowInstanceId());
    }

}