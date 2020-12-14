package munoon.bank.common.validation.pageable.size;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PageSizeValidatorTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValid() {
        TestData data = new TestData(PageRequest.of(0, 15));
        Set<ConstraintViolation<TestData>> result = validator.validate(data);
        assertThat(result).hasSize(0);
    }

    @Test
    void testInvalidLess() {
        TestData data = new TestData(PageRequest.of(0, 5));
        Set<ConstraintViolation<TestData>> result = validator.validate(data);
        assertThat(result).hasSize(1);
    }

    @Test
    void testInvalidMore() {
        TestData data = new TestData(PageRequest.of(0, 25));
        Set<ConstraintViolation<TestData>> result = validator.validate(data);
        assertThat(result).hasSize(1);
    }

    @AllArgsConstructor
    private static class TestData {
        @PageSize(min = 10, max = 20)
        Pageable pageable;
    }
}