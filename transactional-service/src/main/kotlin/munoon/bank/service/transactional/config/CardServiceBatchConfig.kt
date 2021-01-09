package munoon.bank.service.transactional.config

import munoon.bank.service.transactional.card.Card
import munoon.bank.service.transactional.card.CardRepository
import munoon.bank.service.transactional.transaction.UserTransactionTo
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.client.RestTemplate
import java.util.*

@Configuration
@EnableBatchProcessing
@Profile("cardServicePayouter")
class CardServiceBatchConfig(private val jobBuilderFactory: JobBuilderFactory) {
    @Bean
    fun cardServiceJob(cardServiceStep: Step? = null) = jobBuilderFactory.get("cardServiceJob")
        .incrementer(RunIdIncrementer())
        .start(cardServiceStep!!)
        .build()

    @Configuration
    @EnableScheduling
    @ConditionalOnProperty(name = ["spring.scheduling.enabled"], havingValue = "true")
    inner class ScheduledSalaryJobLauncher(private val jobLauncher: JobLauncher) {
        @Scheduled(cron = "\${cron.cardPayouterJob}")
        fun cardPayouter() {
            val parameters = JobParametersBuilder()
                .addDate("startDate", Date())
                .toJobParameters()
            jobLauncher.run(cardServiceJob(), parameters)
        }
    }

    @Configuration
    inner class CardServiceStepConfig(private val stepBuilderFactory: StepBuilderFactory) {
        @Bean
        fun cardServiceStep() = stepBuilderFactory.get("cardServiceStep")
                .chunk<Card, UserTransactionTo>(100)
                .reader(cardServiceReader())
                .processor(cardProcessor())
                .writer(noopWriter())
                .taskExecutor(taskExecutor())
                .build()

        @Bean
        @StepScope
        fun cardServiceReader(cardRepository: CardRepository? = null) = RepositoryItemReaderBuilder<Card>()
                .repository(cardRepository!!)
                .methodName("findWithService")
                .sorts(mapOf("registered" to Sort.Direction.ASC))
                .pageSize(100)
                .name("cardService")
                .saveState(false)
                .build()

        @Bean
        fun cardProcessor(restTemplate: RestTemplate? = null) = ItemProcessor<Card, UserTransactionTo> {
            restTemplate!!.postForObject(CARD_SERVICE_REQUEST_URL, it.id, UserTransactionTo::class.java)
        }
    }

    @Bean
    fun noopWriter() = ItemWriter<Any> {}

    @Bean
    fun taskExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = 64
        maxPoolSize = 64
        setThreadNamePrefix("task-executor-thread-")
    }

    companion object {
        const val CARD_SERVICE_REQUEST_URL = "http://transactional-service/microservices/transaction/payout/card/service"
    }
}