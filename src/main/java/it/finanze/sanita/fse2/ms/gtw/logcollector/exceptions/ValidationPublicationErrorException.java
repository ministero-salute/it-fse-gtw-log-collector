package it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions;

import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.RestExecutionResultEnum;
import lombok.Getter;

@Getter
public class ValidationPublicationErrorException extends RuntimeException{

    /**
     * Serial.
     */
    private static final long serialVersionUID = 1943329995457133624L;

    private final RestExecutionResultEnum result;

    private String errorInstance;

    public ValidationPublicationErrorException(final RestExecutionResultEnum inResult, final String msg, final String inErrorInstance) {
        super(msg);
        result = inResult;
        errorInstance = inErrorInstance;
    }
}
