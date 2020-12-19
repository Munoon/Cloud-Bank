package munoon.bank.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class MicroserviceUtils {
    public static final String MICROSERVICE_HEADER_NAME = "Cloud-Bank-Microservice-Name";
    private static final String UNKNOWN_MICROSERVICE_NAME = "[unknown]";

    public static String getMicroserviceName() {
        var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String microserviceName = request.getHeader(MICROSERVICE_HEADER_NAME);
        return microserviceName == null ? UNKNOWN_MICROSERVICE_NAME : microserviceName;
    }
}
