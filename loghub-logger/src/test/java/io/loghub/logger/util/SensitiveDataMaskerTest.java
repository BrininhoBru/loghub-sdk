package io.loghub.logger.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SensitiveDataMasker.
 */
class SensitiveDataMaskerTest {

    @AfterEach
    void tearDown() {
        SensitiveDataMasker.resetSensitiveFields();
    }

    // Email masking tests
    @Test
    void shouldMaskEmail() {
        String input = "User email is john.doe@example.com";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("j***@***.com"));
        assertFalse(result.contains("john.doe"));
        assertFalse(result.contains("example.com"));
    }

    @Test
    void shouldMaskMultipleEmails() {
        String input = "From: alice@test.com To: bob@company.org";
        String result = SensitiveDataMasker.mask(input);

        assertFalse(result.contains("alice@"));
        assertFalse(result.contains("bob@"));
        assertTrue(result.contains("a***@***.com"));
        assertTrue(result.contains("b***@***.org"));
    }

    // Credit card masking tests
    @Test
    void shouldMaskCreditCard() {
        String input = "Card number: 4111-1111-1111-1111";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("1111")); // Last 4 digits shown
        assertTrue(result.contains("***"));
        assertFalse(result.contains("4111-1111-1111-1111"));
    }

    @Test
    void shouldMaskCreditCardWithSpaces() {
        String input = "Card: 4111 1111 1111 1234";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("1234")); // Last 4 digits shown
        assertTrue(result.contains("***"));
    }

    @Test
    void shouldMaskCreditCardWithoutSeparators() {
        String input = "Card: 4111111111111111";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("1111")); // Last 4 digits shown
        assertTrue(result.contains("***"));
    }

    // CPF masking tests
    @Test
    void shouldMaskCPFWithDots() {
        String input = "CPF do cliente: 123.456.789-09";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("-09")); // Last 2 digits shown
        assertTrue(result.contains("***"));
        assertFalse(result.contains("123.456.789-09"));
    }

    @Test
    void shouldMaskCPFWithoutDots() {
        String input = "CPF: 12345678909";
        String result = SensitiveDataMasker.mask(input);

        // CPF sem formatação também deve ser mascarado
        assertTrue(result.contains("***"));
    }

    // CNPJ masking tests
    @Test
    void shouldMaskCNPJ() {
        String input = "CNPJ: 12.345.678/0001-95";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("-95")); // Last 2 digits shown
        assertTrue(result.contains("***"));
        assertFalse(result.contains("12.345.678/0001-95"));
    }

    // Phone masking tests
    @Test
    void shouldMaskPhoneNumber() {
        String input = "Phone: (11) 98765-4321";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("4321")); // Last 4 digits shown
        assertTrue(result.contains("***"));
    }

    @Test
    void shouldMaskInternationalPhone() {
        String input = "Call +55 11 98765-4321";
        String result = SensitiveDataMasker.mask(input);

        assertTrue(result.contains("4321")); // Last 4 digits shown
    }

    // Sensitive field tests
    @Test
    void shouldIdentifySensitiveFields() {
        assertTrue(SensitiveDataMasker.isSensitiveField("password"));
        assertTrue(SensitiveDataMasker.isSensitiveField("PASSWORD"));
        assertTrue(SensitiveDataMasker.isSensitiveField("userPassword"));
        assertTrue(SensitiveDataMasker.isSensitiveField("user_password"));
        assertTrue(SensitiveDataMasker.isSensitiveField("token"));
        assertTrue(SensitiveDataMasker.isSensitiveField("apiKey"));
        assertTrue(SensitiveDataMasker.isSensitiveField("api_key"));
        assertTrue(SensitiveDataMasker.isSensitiveField("secret"));
        assertTrue(SensitiveDataMasker.isSensitiveField("cpf"));
        assertTrue(SensitiveDataMasker.isSensitiveField("cardNumber"));
        assertTrue(SensitiveDataMasker.isSensitiveField("cvv"));
    }

    @Test
    void shouldNotIdentifyNonSensitiveFields() {
        assertFalse(SensitiveDataMasker.isSensitiveField("username"));
        assertFalse(SensitiveDataMasker.isSensitiveField("email"));
        assertFalse(SensitiveDataMasker.isSensitiveField("name"));
        assertFalse(SensitiveDataMasker.isSensitiveField("orderId"));
    }

    @Test
    void shouldMaskSensitiveFieldValue() {
        String result = SensitiveDataMasker.maskIfSensitive("password", "mySecretPassword123");

        assertNotEquals("mySecretPassword123", result);
        assertTrue(result.contains("***"));
    }

    @Test
    void shouldNotMaskNonSensitiveFieldValue() {
        String result = SensitiveDataMasker.maskIfSensitive("username", "john_doe");

        assertEquals("john_doe", result);
    }

    // Value masking tests
    @Test
    void shouldMaskShortValue() {
        String result = SensitiveDataMasker.maskValue("1234");

        assertEquals("******", result);
    }

    @Test
    void shouldMaskMediumValue() {
        String result = SensitiveDataMasker.maskValue("12345678");

        assertTrue(result.startsWith("12"));
        assertTrue(result.contains("***"));
        assertEquals(8, result.length()); // "12" + "***" + "***" = 8
    }

    @Test
    void shouldMaskLongValue() {
        String result = SensitiveDataMasker.maskValue("myLongSecretValue123");

        assertTrue(result.startsWith("my"));
        assertTrue(result.endsWith("23"));
        assertTrue(result.contains("***"));
    }

    // Custom field tests
    @Test
    void shouldAddCustomSensitiveField() {
        // Usar um nome que não contenha nenhuma palavra sensível já cadastrada
        assertFalse(SensitiveDataMasker.isSensitiveField("mySpecialField"));

        SensitiveDataMasker.addSensitiveField("mySpecialField");

        assertTrue(SensitiveDataMasker.isSensitiveField("mySpecialField"));
    }

    @Test
    void shouldRemoveSensitiveField() {
        // Adicionar um campo personalizado
        SensitiveDataMasker.addSensitiveField("mySpecialData");
        assertTrue(SensitiveDataMasker.isSensitiveField("mySpecialData"));

        // Remover o campo personalizado
        SensitiveDataMasker.removeSensitiveField("mySpecialData");

        // Após remover, não deve mais ser considerado sensível
        assertFalse(SensitiveDataMasker.isSensitiveField("mySpecialData"));
    }

    @Test
    void shouldResetToDefaults() {
        // Adiciona um campo personalizado que não contém palavras sensíveis
        SensitiveDataMasker.addSensitiveField("mySpecialData");
        assertTrue(SensitiveDataMasker.isSensitiveField("mySpecialData"));

        SensitiveDataMasker.resetSensitiveFields();

        // Após reset, campos padrão devem existir e o personalizado não
        assertTrue(SensitiveDataMasker.isSensitiveField("password"));
        assertTrue(SensitiveDataMasker.isSensitiveField("token"));
        assertFalse(SensitiveDataMasker.isSensitiveField("mySpecialData"));
    }

    // Edge cases
    @Test
    void shouldHandleNullInput() {
        assertNull(SensitiveDataMasker.mask(null));
        assertNull(SensitiveDataMasker.maskIfSensitive("field", null));
        assertNull(SensitiveDataMasker.maskValue(null));
        assertFalse(SensitiveDataMasker.isSensitiveField(null));
    }

    @Test
    void shouldHandleEmptyInput() {
        assertEquals("", SensitiveDataMasker.mask(""));
        assertEquals("", SensitiveDataMasker.maskValue(""));
    }

    @Test
    void shouldNotMaskTextWithoutSensitiveData() {
        String input = "This is a normal log message without sensitive data";
        String result = SensitiveDataMasker.mask(input);

        assertEquals(input, result);
    }

    @Test
    void shouldHandleNullFieldName() {
        String result = SensitiveDataMasker.maskIfSensitive(null, "value");
        assertEquals("value", result);
    }

    @Test
    void shouldAddNullFieldSafely() {
        // Não deve lançar exceção
        SensitiveDataMasker.addSensitiveField(null);
        SensitiveDataMasker.addSensitiveField("");
    }

    @Test
    void shouldRemoveNullFieldSafely() {
        // Não deve lançar exceção
        SensitiveDataMasker.removeSensitiveField(null);
    }
}

