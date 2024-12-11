package it.finanze.sanita.fse2.ms.gtw.logcollector.dto.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfigItemsDTO {
    private List<ConfigItemDTO> configurationItems;
    private Integer size;
}
