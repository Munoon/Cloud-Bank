package munoon.bank.service.auth.config

import lombok.NoArgsConstructor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
@NoArgsConstructor
@ConfigurationProperties("jwt.certificate")
class JwtCertificateProperties {
    var store: JwtCertificateStoreSettings? = null
    var key: JwtCertificateKeySettings? = null

    @NoArgsConstructor
    class JwtCertificateStoreSettings {
        lateinit var file: Resource
        lateinit var password: String
    }

    @NoArgsConstructor
    class JwtCertificateKeySettings {
        lateinit var alias: String
        lateinit var password: String
    }
}