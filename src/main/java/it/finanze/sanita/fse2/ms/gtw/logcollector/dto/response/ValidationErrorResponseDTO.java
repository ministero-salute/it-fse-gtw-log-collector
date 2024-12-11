package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response;

import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ValidationErrorResponseDTO extends ErrorResponseDTO {

    @Schema(description = "Identificativo del workflow")
    @Size(min = 0, max = 256)
    private String workflowInstanceId;

    public ValidationErrorResponseDTO(final LogTraceInfoDTO traceInfo, final String inType, final String inTitle,
            final String inDetail, final Integer inStatus, final String inInstance, final String inWorkflowInstanceId) {
        super(traceInfo, inType, inTitle, inDetail, inStatus, inInstance);
        workflowInstanceId = inWorkflowInstanceId;
    }
}
