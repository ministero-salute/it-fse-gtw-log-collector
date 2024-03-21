package it.finanze.sanita.fse2.ms.gtw.logcollector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class LogCfg {

    @Value("${log.ingestion.url}")
    private String url;
    
    @Value("${log.expire.date.day}")
	private Integer expiringDateDAy;
    
}
