package munoon.bank.common.error;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class ErrorInfo {
    private String url;
    private ErrorType errorType;
    private List<String> details;

    public ErrorInfo(CharSequence url, ErrorType errorType, String detils) {
        this.url = url.toString();
        this.errorType = errorType;
        this.details = Collections.singletonList(detils);
    }

    public ErrorInfo(CharSequence url, ErrorType errorType, List<String> details) {
        this.url = url.toString();
        this.errorType = errorType;
        this.details = details;
    }
}
