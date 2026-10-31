package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.IssuerDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.dto.LocalityDTO;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.exceptions.ValidationException;
import it.finanze.sanita.fse2.ms.gtw.logcollector.repository.CollectionNaming;
import it.finanze.sanita.fse2.ms.gtw.logcollector.utility.*;

@Tag("ISOLATION")
class UtilitiesIsolationTest {
    @Test
    void encodesBytesWithoutPlatformCharsetDependency() {
        byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
        assertEquals("aGVsbG8=", StringUtility.encodeBase64(bytes));
        assertEquals("68656c6c6f", StringUtility.encodeHex(bytes));
        assertEquals("", StringUtility.encodeBase64(new byte[0]));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void recognizesAbsentStrings(String value) {
        assertTrue(StringUtility.isNullOrEmpty(value));
    }

    @Test
    void whitespaceIsNotAnEmptyString() {
        assertFalse(StringUtility.isNullOrEmpty(" "));
    }

    @Test
    void jsonRoundTripAndClonePreserveValues() {
        Map<String, String> input = Map.of("name", "gateway");
        assertEquals(input, JsonUtility.jsonToObject(JsonUtility.objectToJson(input), Map.class));
        assertEquals(input, JsonUtility.clone(input, Map.class));
        assertEquals(input, JsonUtility.validateJson("{\"name\":\"gateway\"}", Map.class));
        assertEquals("null", JsonUtility.objectToJson(null));
    }

    @Test
    void invalidJsonUsesLenientAndStrictContracts() {
        assertNull(JsonUtility.jsonToObject("invalid", Map.class));
        assertThrows(ValidationException.class, () -> JsonUtility.validateJson("invalid", Map.class));
        assertEquals("", JsonUtility.objectToJson(new Object()));
    }

    @Test
    void addsCalendarDaysAndWrapsInvalidDate() {
        Date date = Date.from(Instant.parse("2025-01-15T12:00:00Z"));
        assertEquals(date, DateUtility.addDay(date, 0));
        assertTrue(DateUtility.addDay(date, 2).after(date));
        assertTrue(DateUtility.addDay(date, -2).before(date));
        assertNotNull(assertThrows(BusinessException.class, () -> DateUtility.addDay(null, 1)).getCause());
    }

    @Test
    void readsClasspathFileAndReturnsNullForMissingFile() {
        assertArrayEquals("isolation fixture\n".getBytes(StandardCharsets.UTF_8),
                FileUtility.getFileFromInternalResources("isolation-fixture.txt"));
        assertNull(FileUtility.getFileFromInternalResources("missing-isolation-fixture.txt"));
    }

    @ParameterizedTest
    @CsvSource({"test,true,false", "TEST_local,true,false", "dev,false,true", "docker,false,true", "ISOLATION,false,false"})
    void profilesAndCollectionNamesFollowCurrentContract(String profile, boolean test, boolean dev) {
        ProfileUtility utility = new ProfileUtility();
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(profile);
        ReflectionTestUtils.setField(utility, "environment", environment);
        assertEquals(test, utility.isTestProfile());
        assertEquals(dev, utility.isDevOrDockerProfile());
        CollectionNaming naming = new CollectionNaming();
        ReflectionTestUtils.setField(naming, "profileUtility", utility);
        assertEquals((test ? "test_" : "") + "log_collector", naming.getLogCollectorControlCollection());
        assertEquals((test ? "test_" : "") + "log_collector_kpi", naming.getLogCollectorKpiCollection());
    }

    @Test
    void noEnvironmentOrActiveProfileIsNotTestOrDev() {
        ProfileUtility utility = new ProfileUtility();
        assertFalse(utility.isTestProfile());
        assertFalse(utility.isDevOrDockerProfile());
        ReflectionTestUtils.setField(utility, "environment", new MockEnvironment());
        assertFalse(utility.isTestProfile());
        assertFalse(utility.isDevOrDockerProfile());
    }

    @ParameterizedTest
    @CsvSource(value = {"120|null|null|null", "120001|001|null|null", "120001123456|001|123456|null",
            "120123456|null|123456|null", "120RSSMRA80A01H501U|null|null|RSSMRA80A01H501U",
            "120001RSSMRA80A01H501U|001|null|RSSMRA80A01H501U", "120unknown|null|null|null"},
            delimiter = '|', nullValues = "null")
    void decodesIssuerVariants(String input, String asl, String enterprise, String fiscalCode) {
        IssuerDTO issuer = IssuerDTO.decodeIssuer("prefix#" + input);
        assertEquals("prefix#" + input, issuer.getRawValue());
        assertEquals("120", issuer.getRegion());
        assertEquals(asl, issuer.getAsl());
        assertEquals(enterprise, issuer.getEnterprise());
        assertEquals(fiscalCode, issuer.getFiscalCode());
    }

    @Test
    void unknownIssuerRegionIsNotInferred() {
        assertNull(IssuerDTO.decodeIssuer("998").getRegion());
        assertThrows(StringIndexOutOfBoundsException.class, () -> IssuerDTO.decodeIssuer("12"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"plain", "name&wrong&ISO^^^^120001", "name&2.16.840.1.113883.2.9.4.1.3&ISO",
            "name&2.16.840.1.113883.2.9.4.1.3&ISO^^^^123"})
    void incompleteLocalityDoesNotInventStructure(String input) {
        LocalityDTO actual = LocalityDTO.decodeLocality(input);
        assertEquals(input, actual.getRawValue());
        assertNull(actual.getAslCode());
        assertNull(actual.getStructure());
    }

    @Test
    void decodesAslOnlyLocality() {
        LocalityDTO actual = LocalityDTO.decodeLocality("name&2.16.840.1.113883.2.9.4.1.3&ISO^^^^120001");
        assertEquals("001", actual.getAslCode());
        assertNull(actual.getStructure());
    }
}
