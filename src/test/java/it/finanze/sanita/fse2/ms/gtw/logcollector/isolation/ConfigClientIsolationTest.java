package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import it.finanze.sanita.fse2.ms.gtw.logcollector.client.impl.ConfigClient;
import it.finanze.sanita.fse2.ms.gtw.logcollector.client.routes.ConfigClientRoutes;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.config.ConfigItemDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.ConfigItemTypeEnum;

@Tag("ISOLATION")
@ActiveProfiles("ISOLATION")
@SpringJUnitConfig({ConfigClient.class, ConfigClientRoutes.class})
@TestPropertySource(properties = "ms.url.gtw-config=http://config.invalid")
class ConfigClientIsolationTest {
    @Autowired private ConfigClient client;
    @Autowired private ConfigClientRoutes routes;
    @MockitoBean private RestTemplate http;

    @Test
    void retrievesConfigurationItemsUsingTypedRoute() {
        ConfigItemDTO expected = new ConfigItemDTO();
        when(http.getForObject(routes.getConfigItems(ConfigItemTypeEnum.LOG_COLLECTOR), ConfigItemDTO.class)).thenReturn(expected);
        assertSame(expected, client.getConfigurationItems(ConfigItemTypeEnum.LOG_COLLECTOR));
        assertEquals("http://config.invalid/v1/config-items?type=LOG_COLLECTOR", routes.getConfigItems(ConfigItemTypeEnum.LOG_COLLECTOR));
    }

    @Test
    void usesSpecificPropertyWhenAvailable() {
        when(http.getForObject(routes.getConfigItem(ConfigItemTypeEnum.LOG_COLLECTOR, "page-size"), String.class)).thenReturn("25");
        assertEquals("25", client.getProps("page-size", "10", ConfigItemTypeEnum.LOG_COLLECTOR));
        verify(http, never()).getForObject(routes.getConfigItem(ConfigItemTypeEnum.GENERIC, "page-size"), String.class);
    }

    @Test
    void fallsBackToGenericProperty() {
        when(http.getForObject(routes.getConfigItem(ConfigItemTypeEnum.GENERIC, "page-size"), String.class)).thenReturn("50");
        assertEquals("50", client.getProps("page-size", "10", ConfigItemTypeEnum.LOG_COLLECTOR));
        verify(http).getForObject(routes.getConfigItem(ConfigItemTypeEnum.LOG_COLLECTOR, "page-size"), String.class);
    }

    @Test
    void returnsNullWhenNeitherScopeDefinesProperty() {
        assertNull(client.getProps("missing", "10", ConfigItemTypeEnum.LOG_COLLECTOR));
    }

    @Test
    void retainsPreviousValueWhenConfigIsUnreachable() {
        when(http.getForEntity(routes.status(), String.class)).thenThrow(new ResourceAccessException("offline"));
        assertEquals("10", client.getProps("page-size", "10", ConfigItemTypeEnum.LOG_COLLECTOR));
        verify(http, never()).getForObject(anyString(), eq(String.class));
    }

    @Test
    void returnsUnchangedProperty() {
        when(http.getForObject(routes.getConfigItem(ConfigItemTypeEnum.LOG_COLLECTOR, "page-size"), String.class)).thenReturn("10");
        assertEquals("10", client.getProps("page-size", "10", ConfigItemTypeEnum.LOG_COLLECTOR));
    }

    @Test
    void propagatesUnexpectedHttpFailures() {
        IllegalStateException cause = new IllegalStateException("invalid response");
        when(http.getForEntity(routes.status(), String.class)).thenThrow(cause);
        assertSame(cause, assertThrows(IllegalStateException.class,
                () -> client.getProps("page-size", "10", ConfigItemTypeEnum.LOG_COLLECTOR)));
    }
}
