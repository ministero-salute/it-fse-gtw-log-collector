package it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions;

public class ConnectionRefusedException extends RuntimeException{


    /**
     * Serial.
     */
    private static final long serialVersionUID = 4731633511982796785L;

    private final String url;

    public ConnectionRefusedException(final String inUrl,final String msg) {
        super(msg);
        url = inUrl;
    }

    public String getUrl() {
        return url;
    }
}
