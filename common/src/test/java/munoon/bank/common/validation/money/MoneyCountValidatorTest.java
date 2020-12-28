package munoon.bank.common.validation.money;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyCountValidatorTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValid() {
        TestData testData = new TestData(1.0);
        var result = validator.validate(testData);
        assertThat(result).isEmpty();
    }

    @Test
    void testInvalid() {
        TestData testData = new TestData(1.00001);
        var result = validator.validate(testData);
        assertThat(result).isNotEmpty();
    }

    @Test
    void testNull() {
        TestData testData = new TestData(null);
        var result = validator.validate(testData);
        assertThat(result).isEmpty();
    }

    @AllArgsConstructor
    private static class TestData {
        @ValidMoneyCount
        Double value;
    }
}