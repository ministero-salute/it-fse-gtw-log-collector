package it.finanze.sanita.fse2.ms.gtw.logcollector.controller.handler;

import brave.Tracer;
import it.finanze.sanita.fse2.ms.gtw.logcollector.client.exceptions.ServerResponseException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response.ErrorResponseDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response.LogTraceInfoDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.ConnectionRefusedException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *	Exceptions Handler.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler  extends ResponseEntityExceptionHandler {

    /**
     * Tracker log.
     */
    @Autowired
    private Tracer tracer;


    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<ErrorResponseDTO> handleGenericValidationException(final ValidationException ex, final WebRequest request) {
        log.error("" , ex);
        Integer status = 400;

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        return new ResponseEntity<>(ex.getError(), headers, status);
    }

    /**
     * Management generic exception.
     *
     * @param ex		exception
     * @param request	request
     * @return
     */
    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<ErrorResponseDTO> handleGenericException(final Exception ex, final WebRequest request) {
        log.error("" , ex);
        Integer status = 500;

        ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/generic-error", "Errore generico", "Errore generico", status, "/msg/generic-error");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        return new ResponseEntity<>(out, headers, status);
    }

    /**
     * Management connection refused exception.
     *
     * @param ex		exception
     * @param request	request
     * @return
     */
    @ExceptionHandler(value = {ConnectionRefusedException.class})
    protected ResponseEntity<ErrorResponseDTO> handleConnectionRefusedException(final ConnectionRefusedException ex, final WebRequest request) {
        log.error("" , ex);
        Integer status = 500;

        String detailedMessage = ex.getMessage();
        ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/connection-refused", ex.getMessage(), detailedMessage, status, "/msg/connection-refused");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        return new ResponseEntity<>(out, headers, status);
    }

    /**
     * Management connection refused exception.
     *
     * @param ex		exception
     * @param request	request
     * @return
     */
    @ExceptionHandler(value = {ServerResponseException.class})
    protected ResponseEntity<ErrorResponseDTO> handleServerException(final ServerResponseException ex, final WebRequest request) {
        log.error("" , ex);
        Integer status = 400;

        ErrorResponseDTO out = new ErrorResponseDTO(getLogTraceInfo(), "/msg/server-error", "Server error", ExceptionUtils.getMessage(ex), status, "/msg/server-error");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        return new ResponseEntity<>(out, headers, status);
    }

    private LogTraceInfoDTO getLogTraceInfo() {
        return new LogTraceInfoDTO(
                tracer.currentSpan().context().spanIdString(),
                tracer.currentSpan().context().traceIdString());
    }



}
