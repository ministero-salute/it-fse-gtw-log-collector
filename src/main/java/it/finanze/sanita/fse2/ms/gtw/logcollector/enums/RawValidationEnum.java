package it.finanze.sanita.fse2.ms.gtw.logcollector.enums;

public enum RawValidationEnum {


    OK("00", "OK"),
    SYNTAX_ERROR("01", "Errore di sintassi"),
    VOCABULARY_ERROR("02", "Errore dovuto alle terminologie utilizzate"),
    SEMANTIC_ERROR("03", "Errore semantico"),
    SCHEMATRON_NOT_FOUND("04", "Schematron not found"),
    SEMANTIC_WARNING("05", "Warning semantico");

    private String code;
    private String description;

    private RawValidationEnum(String inCode, String inDescription) {
        code = inCode;
        description = inDescription;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
