package munoon.bank.service.resource.user.config

import munoon.bank.common.transaction.to.PaySalaryTransactionDataTo
import munoon.bank.common.transaction.to.SalaryUserTransactionInfoTo
import munoon.bank.service.resource.user.client.TransactionClient
import munoon.bank.service.resource.user.user.UserEntity
import munoon.bank.service.resource.user.user.UserRepository
import org.springframework.batch.core.JobParametersBuilder
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
import java.util.*

@Configuration
@EnableBatchProcessing
@Profile("salaryPayouter")
class SalaryBatchConfig(private val jobLauncher: JobLauncher,
                        private val stepBuilderFactory: StepBuilderFactory,
                        private val jobBuilderFactory: JobBuilderFactory) {
    @Configuration
    @EnableScheduling
    @ConditionalOnProperty(name = ["spring.scheduling.enabled"], havingValue = "true")
    inner class ScheduledSalaryJobLauncher {
        @Scheduled(cron = "\${cron.salaryPayout}")
        fun salaryPayouter() {
            val parameters = JobParametersBuilder()
                .addDate("startDate", Date())
                .toJobParameters()
            jobLauncher.run(salaryJob(), parameters)
        }
    }

    @Bean
    fun salaryJob() = jobBuilderFactory.get("salaryJob")
            .incrementer(RunIdIncrementer())
            .start(salaryStep())
            .build()

    @Bean
    fun salaryStep() = stepBuilderFactory.get("salaryStep")
            .chunk<UserEntity, SalaryUserTransactionInfoTo>(100)
            .reader(salaryUsersReader())
            .processor(salaryProcessor())
            .writer(noopSalaryWriter())
            .taskExecutor(taskExecutor())
            .throttleLimit(10)
            .build()

    @Bean
    @StepScope
    fun salaryUsersReader(userRepository: UserRepository? = null) = RepositoryItemReaderBuilder<UserEntity>()
            .repository(userRepository!!)
            .methodName("findAllWithSalary")
            .sorts(mapOf("registered" to Sort.Direction.ASC))
            .pageSize(100)
            .name("users")
            .saveState(false)
            .build()

    @Bean
    fun salaryProcessor(transactionClient: TransactionClient? = null) =
            ItemProcessor<UserEntity, SalaryUserTransactionInfoTo> {
                val data = PaySalaryTransactionDataTo(it.id!!, it.salary!!)
                transactionClient!!.payoutSalary(data)
            }

    @Bean
    fun noopSalaryWriter() = ItemWriter<SalaryUserTransactionInfoTo> {}

    @Bean
    fun taskExecutor() = ThreadPoolTaskExecutor().apply {
        corePoolSize = 64
        maxPoolSize = 64
        setThreadNamePrefix("task-executor-thread-")
    }
}