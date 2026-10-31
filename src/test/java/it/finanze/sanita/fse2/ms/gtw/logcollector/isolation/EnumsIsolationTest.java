package it.finanze.sanita.fse2.ms.gtw.logcollector.isolation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import it.finanze.sanita.fse2.ms.gtw.logcollector.enums.*;

@Tag("ISOLATION")
class EnumsIsolationTest {
    @ParameterizedTest
    @EnumSource(RestExecutionResultEnum.class)
    void restResultLookupIsCaseInsensitive(RestExecutionResultEnum value) {
        assertSame(value, RestExecutionResultEnum.get(value.getType().toUpperCase(Locale.ROOT)));
        assertFalse(value.getTitle().isBlank());
        if (value != RestExecutionResultEnum.OK && value != RestExecutionResultEnum.OK_FORCED) {
            assertNotNull(value.getErrorCategory());
        }
    }

    @Test
    void unknownRestResultIsAbsent() {
        assertNull(RestExecutionResultEnum.get("unknown"));
        assertNull(RestExecutionResultEnum.get(null));
    }

    @ParameterizedTest
    @CsvSource({"OK,OK", "SYNTAX_ERROR,SYNTAX_ERROR", "SEMANTIC_ERROR,SEMANTIC_ERROR",
            "VOCABULARY_ERROR,VOCABULARY_ERROR", "SCHEMATRON_NOT_FOUND,GENERIC_ERROR", "SEMANTIC_WARNING,GENERIC_ERROR"})
    void rawValidationMapsToExpectedRestResult(RawValidationEnum input, RestExecutionResultEnum expected) {
        assertSame(expected, RestExecutionResultEnum.fromRawResult(input));
        assertFalse(input.getDescription().isBlank());
        assertEquals(2, input.getCode().length());
    }

    @ParameterizedTest
    @EnumSource(ErrorLogEnum.class)
    void errorCodesAreUniqueAndDescribed(ErrorLogEnum error) {
        assertTrue(error.getCode().startsWith("KO-"));
        assertFalse(error.getDescription().isBlank());
        assertEquals(1, Stream.of(ErrorLogEnum.values()).filter(e -> e.getCode().equals(error.getCode())).count());
    }

    @ParameterizedTest
    @EnumSource(RegionEnum.class)
    void regionCodesRoundTrip(RegionEnum region) {
        assertSame(region, RegionEnum.getFromCode(region.getCode()));
        assertFalse(region.getDescription().isBlank());
    }

    @Test
    void configurationPriorityIsGenericThenSpecific() {
        assertEquals(java.util.List.of(ConfigItemTypeEnum.GENERIC, ConfigItemTypeEnum.LOG_COLLECTOR), ConfigItemTypeEnum.priority());
        assertNull(RegionEnum.getFromCode("invalid"));
    }
}
