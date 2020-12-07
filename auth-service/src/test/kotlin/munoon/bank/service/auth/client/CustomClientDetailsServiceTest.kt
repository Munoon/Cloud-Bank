package munoon.bank.service.auth.client

import munoon.bank.common.util.exception.NotFoundException
import munoon.bank.service.auth.AbstractTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired

internal class CustomClientDetailsServiceTest : AbstractTest() {
    @Autowired
    private lateinit var customClientDetailsService: CustomClientDetailsService

    @Test
    fun loadClientByClientId() {
        val client = customClientDetailsService.loadClientByClientId(ClientTestData.DEFAULT_CLIENT_ID)
        assertThat(client).usingRecursiveComparison().isEqualTo(ClientTestData.DEFAULT_CLIENT)
    }

    @Test
    fun loadClientByClientIdNotFound() {
        assertThrows(NotFoundException::class.java) {
            customClientDetailsService.loadClientByClientId("UNKNOWN_CLIENT_ID")
        }
    }
}