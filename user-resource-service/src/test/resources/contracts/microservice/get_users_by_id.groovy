package contracts.microservice

import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.ServerDslProperty

import java.util.regex.Pattern

Contract.make {
    description "get user's by microservice"

    request {
        url "/microservices/users?ids=100"
        method GET()
        headers {
            contentType applicationJson()
        }
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([[
              id: 100,
              name: "Nikita",
              surname: "Ivchenko",
              username: "munoon",
              class: 10,
              roles: ["ROLE_COURIER", "ROLE_TEACHER", "ROLE_BARMEN", "ROLE_ADMIN"],
              registered: $(consumer("2020-12-06T00:10"), producer(new ServerDslProperty(Pattern.compile(".*"))))
        ]])
    }
}