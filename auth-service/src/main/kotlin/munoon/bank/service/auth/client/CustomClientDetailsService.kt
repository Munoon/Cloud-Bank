package munoon.bank.service.auth.client

import munoon.bank.common.util.exception.NotFoundException
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors.toMap

@Service
@RefreshScope
class CustomClientDetailsService(oAuth2Clients: OAuth2Clients) : ClientDetailsService {
    private val storage: Map<String, ClientDetails>

    init {
        this.storage = oAuth2Clients.clients
                .stream()
                .map { CustomClientDetails(it) }
                .collect(toMap({ it.clientId }, { it }))
    }


    override fun loadClientByClientId(clientId: String): ClientDetails = Optional.ofNullable(storage.get(clientId))
            .orElseThrow { NotFoundException("Client with id '$clientId' is not found!") }
}