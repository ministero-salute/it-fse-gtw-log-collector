package it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl;



import static it.finanze.sanita.fse2.ms.gtw.logcollector.client.routes.base.ClientRoutes.Config.CFG_ITEMS_PAGE_SIZE;
import static it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ConfigItemTypeEnum.LOG_COLLECTOR;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.finanze.sanita.fse2.ms.gtw.logcollector.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.config.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.IConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.ProfileUtility;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ConfigSRV implements IConfigSRV {

    @Autowired
    private IConfigClient client;

    @Autowired
    private ProfileUtility profiles;

    private final Map<String, Pair<Long, String>> props;

    public ConfigSRV() {
        this.props = new HashMap<>();
    }

    @PostConstruct
    public void postConstruct() {
        if (!profiles.isTestProfile()) {
            init();
        } else {
            log.info("Skipping gtw-config initialization due to test profile");
        }
    }

    @Override
    public int getPageSize() {
        long lastUpdate = props.get(CFG_ITEMS_PAGE_SIZE).getKey();
        if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
            synchronized (Locks.CFG_ITEMS_PAGE_SIZE) {
                if (new Date().getTime() - lastUpdate >= getRefreshRate()) {
                    refresh(CFG_ITEMS_PAGE_SIZE);
                }
            }
        }
        return Integer.parseInt(props.get(CFG_ITEMS_PAGE_SIZE).getValue());
    }

    private void refresh(String name) {
        String previous = props.getOrDefault(name, Pair.of(0L, null)).getValue();
        String prop = client.getProps(name, previous, LOG_COLLECTOR);
        props.put(name, Pair.of(new Date().getTime(), prop));
    }

    private void init() {
        for(ConfigItemTypeEnum en : ConfigItemTypeEnum.priority()) {
            log.info("[GTW-CFG] Retrieving {} properties ...", en.name());
            ConfigItemDTO items = client.getConfigurationItems(en);
            List<ConfigItemDTO.ConfigDataItemDTO> opts = items.getConfigurationItems();
            for(ConfigItemDTO.ConfigDataItemDTO opt : opts) {
                opt.getItems().forEach((key, value) -> {
                    log.info("[GTW-CFG] Property {} is set as {}", key, value);
                    props.put(key, Pair.of(new Date().getTime(), value));
                });
            }
            if(opts.isEmpty()) log.info("[GTW-CFG] No props were found");
        }
    }

    @Override
    public long getRefreshRate() {
        return 300_000L;
    }

    private static final class Locks {
        public static final Object CFG_ITEMS_PAGE_SIZE = new Object();
    }

}
