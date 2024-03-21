package it.finanze.sanita.fse2.ms.gtw.logcollector.enums;

import lombok.Getter;

public enum ProcessedStatusEnum {

    UNPROCESSED("UNPROCESSED"),
    PROCESSING("PROCESSING"),
    PROCESSED("PROCESSED");

    @Getter
    String code;

    private ProcessedStatusEnum(String inCode){
        code = inCode;
    }

}
