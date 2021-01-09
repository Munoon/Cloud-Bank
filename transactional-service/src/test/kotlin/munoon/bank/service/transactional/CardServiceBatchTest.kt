package munoon.bank.service.transactional

import munoon.bank.service.transactional.card.AdminCreateCardTo
import munoon.bank.service.transactional.card.CardService
import munoon.bank.service.transactional.config.CardServiceBatchConfig
import munoon.bank.service.transactional.transaction.UserTransactionTo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import java.util.*

@ActiveProfiles("cardServicePayouter")
internal class CardServiceBatchTest : AbstractTest() {
    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var cardServiceJob: Job

    @Autowired
    private lateinit var cardService: CardService

    @MockBean
    private lateinit var restTemplate: RestTemplate

    @Test
    fun cardJob() {
        val card = cardService.createCard(AdminCreateCardTo(100, "gold", null, "1111", true))

        val parameters = JobParametersBuilder()
            .addDate("startDate", Date())
            .toJobParameters()
        val run = jobLauncher.run(cardServiceJob, parameters)

        assertThat(run.status).isEqualTo(BatchStatus.COMPLETED)

        verify(restTemplate, times(1)).postForObject(
            CardServiceBatchConfig.CARD_SERVICE_REQUEST_URL,
            card.id,
            UserTransactionTo::class.java
        )
    }
}