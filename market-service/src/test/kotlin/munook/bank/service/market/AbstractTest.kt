package munook.bank.service.market

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = ["classpath:db/data.sql"])
abstract class AbstractTest