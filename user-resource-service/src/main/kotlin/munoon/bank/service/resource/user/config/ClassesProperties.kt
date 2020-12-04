package munoon.bank.service.resource.user.config

import lombok.NoArgsConstructor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@Component
@RefreshScope
@NoArgsConstructor
@ConfigurationProperties("application")
class ClassesProperties {
    lateinit var classes: List<String>
}