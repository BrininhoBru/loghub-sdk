package io.loghub.contract;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SdkInfo model.
 */
class SdkInfoTest {

    @Test
    void shouldCreateWithBuilder() {
        SdkInfo sdk = SdkInfo.builder()
                .language("java")
                .version("1.0.0")
                .build();

        assertEquals("java", sdk.getLanguage());
        assertEquals("1.0.0", sdk.getVersion());
    }

    @Test
    void shouldCreateWithConstructor() {
        SdkInfo sdk = new SdkInfo("java", "1.0.0");

        assertEquals("java", sdk.getLanguage());
        assertEquals("1.0.0", sdk.getVersion());
    }

    @Test
    void shouldBeEqualForSameValues() {
        SdkInfo sdk1 = new SdkInfo("java", "1.0.0");
        SdkInfo sdk2 = new SdkInfo("java", "1.0.0");

        assertEquals(sdk1, sdk2);
        assertEquals(sdk1.hashCode(), sdk2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        SdkInfo sdk1 = new SdkInfo("java", "1.0.0");
        SdkInfo sdk2 = new SdkInfo("java", "2.0.0");

        assertNotEquals(sdk1, sdk2);
    }

    @Test
    void shouldHaveToString() {
        SdkInfo sdk = new SdkInfo("java", "1.0.0");

        String str = sdk.toString();
        assertTrue(str.contains("java"));
        assertTrue(str.contains("1.0.0"));
    }
}

