package it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions;

import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.response.ErrorResponseDTO;
import lombok.Getter;

/**
 *
 * Validation exeception.
 *
 */
public class ValidationException extends RuntimeException{

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 5881948810042221356L;

    @Getter
    private transient ErrorResponseDTO error;
    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public ValidationException(final String msg) {
        super(msg);
    }

    public ValidationException(final ErrorResponseDTO inError) {
        error = inError;
    }

    /**
     * Complete constructor.
     *
     * @param msg	Message to be shown.
     * @param e		Exception to be shown.
     */
    public ValidationException(final String msg, final Exception e) {
        super(msg, e);
    }

    /**
     * Exception constructor.
     *
     * @param e	Exception to be shown.
     */
    public ValidationException(final Exception e) {
        super(e);
    }

}

