package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static it.finanze.sanita.fse2.ms.gtw.logcollector.client.routes.base.ClientRoutes.Config.CFG_ITEMS_PAGE_SIZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import it.finanze.sanita.fse2.ms.gtw.logcollector.client.IConfigClient;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.config.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ConfigItemTypeEnum;
import it.finanze.sanita.fse2.ms.gtw.logcollector.service.impl.ConfigSRV;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.ProfileUtility;

@Tag("ISOLATION")
@ExtendWith(MockitoExtension.class)
class ConfigServiceIsolationTest {
    @Mock private IConfigClient client;
    @Mock private ProfileUtility profiles;
    @InjectMocks private ConfigSRV service;

    private ConfigItemDTO items(String value) {
        ConfigItemDTO.ConfigDataItemDTO data = new ConfigItemDTO.ConfigDataItemDTO();
        data.setItems(Map.of(CFG_ITEMS_PAGE_SIZE, value));
        ConfigItemDTO result = new ConfigItemDTO();
        result.setConfigurationItems(List.of(data));
        return result;
    }

    private void initialize() {
        when(client.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenReturn(items("10"));
        when(client.getConfigurationItems(ConfigItemTypeEnum.LOG_COLLECTOR)).thenReturn(items("25"));
        service.postConstruct();
    }

    @Test
    void specificConfigurationOverridesGenericAndRemainsCached() {
        initialize();
        assertEquals(25, service.getPageSize());
        assertEquals(300_000L, service.getRefreshRate());
        verify(client, never()).getProps(anyString(), anyString(), any());
    }

    @Test
    void testProfileSkipsRemoteInitialization() {
        when(profiles.isTestProfile()).thenReturn(true);
        service.postConstruct();
        verifyNoInteractions(client);
    }

    @Test
    void emptySpecificConfigurationPreservesGenericValues() {
        when(client.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenReturn(items("10"));
        ConfigItemDTO empty = new ConfigItemDTO();
        empty.setConfigurationItems(List.of());
        when(client.getConfigurationItems(ConfigItemTypeEnum.LOG_COLLECTOR)).thenReturn(empty);
        service.postConstruct();
        assertEquals(10, service.getPageSize());
    }

    @Test
    @SuppressWarnings("unchecked")
    void expiredPropertyRefreshesOnceAndCachesNewValue() {
        initialize();
        Map<String, Pair<Long, String>> props = (Map<String, Pair<Long, String>>) ReflectionTestUtils.getField(service, "props");
        props.put(CFG_ITEMS_PAGE_SIZE, Pair.of(0L, "25"));
        when(client.getProps(CFG_ITEMS_PAGE_SIZE, "25", ConfigItemTypeEnum.LOG_COLLECTOR)).thenReturn("50");
        assertEquals(50, service.getPageSize());
        assertEquals(50, service.getPageSize());
        verify(client).getProps(CFG_ITEMS_PAGE_SIZE, "25", ConfigItemTypeEnum.LOG_COLLECTOR);
    }

    @Test
    void initializationFailureIsPropagated() {
        IllegalStateException failure = new IllegalStateException("config unavailable");
        when(client.getConfigurationItems(ConfigItemTypeEnum.GENERIC)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, service::postConstruct));
    }

    @Test
    void invalidPageSizeIsRejected() {
        when(client.getConfigurationItems(any())).thenReturn(items("invalid"));
        service.postConstruct();
        assertThrows(NumberFormatException.class, service::getPageSize);
    }
}
