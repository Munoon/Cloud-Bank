package munoon.bank.common.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ErrorInfoField extends ErrorInfo {
    @Getter
    private final Map<String, List<String>> fields;

    public ErrorInfoField(CharSequence url, Map<String, List<String>> fields) {
        super(url, ErrorType.VALIDATION_ERROR, Collections.emptyList());
        this.fields = fields;
    }

    public ErrorInfoField(CharSequence url, String detils, Map<String, List<String>> fields) {
        super(url, ErrorType.VALIDATION_ERROR, detils);
        this.fields = fields;
    }

    @JsonCreator
    public ErrorInfoField(@JsonProperty("url") CharSequence url,
                          @JsonProperty("details") List<String> details,
                          @JsonProperty("fields") Map<String, List<String>> fields) {
        super(url, ErrorType.VALIDATION_ERROR, details);
        this.fields = fields;
    }
}
