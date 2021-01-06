package munoon.bank.service.resource.user.batch.job

import munoon.bank.common.transaction.to.PaySalaryTransactionDataTo
import munoon.bank.service.resource.user.AbstractTest
import munoon.bank.service.resource.user.client.TransactionClient
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
import java.util.*

@ActiveProfiles("salaryPayouter")
internal class SalaryBatchJobTest : AbstractTest() {
    @Autowired
    private lateinit var jobLauncher: JobLauncher

    @Autowired
    private lateinit var salaryJob: Job

    @MockBean
    private lateinit var transactionalClient: TransactionClient

    @Test
    fun salaryJob() {
        val parameters = JobParametersBuilder()
            .addDate("startDate", Date())
            .toJobParameters()
        val run = jobLauncher.run(salaryJob, parameters)

        assertThat(run.status).isEqualTo(BatchStatus.COMPLETED)

        val data = PaySalaryTransactionDataTo(100, 100.0)
        verify(transactionalClient, times(1)).payoutSalary(data)
    }
}