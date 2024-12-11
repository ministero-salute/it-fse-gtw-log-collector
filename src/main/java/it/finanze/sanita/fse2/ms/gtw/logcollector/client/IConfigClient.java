package it.finanze.sanita.fse2.ms.gtw.logcollector.client;

import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.config.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ConfigItemTypeEnum;

public interface IConfigClient {
    ConfigItemDTO getConfigurationItems(ConfigItemTypeEnum type);
    String getProps(String props, String previous, ConfigItemTypeEnum ms);
}
