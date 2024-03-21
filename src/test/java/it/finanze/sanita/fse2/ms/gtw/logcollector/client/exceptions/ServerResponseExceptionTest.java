package it.finanze.sanita.fse2.ms.gtw.logcollector.client.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ServerResponseExceptionTest {

    @Test
    public void testServerResponseExceptionConstructorWithMessage() {
        String errorMessage = "Test error message";
        ServerResponseException exception = new ServerResponseException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getTarget());
        assertNull(exception.getStatus());
        assertEquals(0, exception.getStatusCode());
        assertNull(exception.getDetail());
    }

    @Test
    public void testServerResponseExceptionAllArgsConstructor() {
        String target = "Test target";
        String message = "Test message";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        int statusCode = 500;
        String detail = "Test detail";

        ServerResponseException exception = new ServerResponseException(target, message, status, statusCode, detail);

        assertEquals(target, exception.getTarget());
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(detail, exception.getDetail());
    }

    @Test
    public void testServerResponseExceptionCopyConstructor() {
        String target = "Test target";
        String message = "Test message";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        int statusCode = 500;
        String detail = "Test detail";

        ServerResponseException originalException = new ServerResponseException(target, message, status, statusCode, detail);
        ServerResponseException copiedException = new ServerResponseException(originalException);

        assertEquals(originalException.getTarget(), copiedException.getTarget());
        assertEquals(originalException.getMessage(), copiedException.getMessage());
        assertEquals(originalException.getStatus(), copiedException.getStatus());
        assertEquals(originalException.getStatusCode(), copiedException.getStatusCode());
        assertEquals(originalException.getDetail(), copiedException.getDetail());
    }

}