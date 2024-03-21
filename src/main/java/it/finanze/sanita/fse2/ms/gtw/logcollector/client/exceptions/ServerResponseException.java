package it.finanze.sanita.fse2.ms.gtw.logcollector.client.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ServerResponseException extends RuntimeException{

    /**
     *
     */
    private static final long serialVersionUID = 1062386344600523308L;

    private String target;

    private String message;

    private HttpStatus status;

    private int statusCode;

    private String detail;

    public ServerResponseException(ServerResponseException e) {
        this.target = e.getTarget();
        this.message = e.getMessage();
        this.status = e.getStatus();
        this.statusCode = e.getStatusCode();
        this.detail = e.getDetail();
    }

    /**
     * Message constructor.
     *
     * @param msg	Message to be shown.
     */
    public ServerResponseException(final String msg) {
        message = msg;
    }
}
