package it.finanze.sanita.fse2.ms.gtw.logcollector.repository;

import it.finanze.sanita.fse2.ms.gtw.logcollector.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.ProfileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionNaming {

    @Autowired
    private ProfileUtility profileUtility;

    @Bean("logCollectorKpiBean")
    public String getLogCollectorKpiCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.LOG_KPI_COLLECTION_NAME;
        }
        return Constants.Collections.LOG_KPI_COLLECTION_NAME;
    }

    @Bean("logCollectorControlBean")
    public String getLogCollectorControlCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.LOG_CONTROL_COLLECTION_NAME;
        }
        return Constants.Collections.LOG_CONTROL_COLLECTION_NAME;
    }
}
