package contracts.users

import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.ServerDslProperty

import java.util.regex.Pattern

Contract.make {
    description "get user's profile"

    request {
        url "/profile"
        method GET()
        headers {
            contentType applicationJson()
            header(authorization(), "Bearer DEFAULT_USER")
        }
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body (
                id: 100,
                name: "Nikita",
                surname: "Ivchenko",
                username: "munoon",
                class: 10,
                roles: ["ROLE_COURIER", "ROLE_TEACHER", "ROLE_BARMEN", "ROLE_ADMIN"],
                registered: $(consumer([2020, 12, 6, 0, 10]), producer(new ServerDslProperty(Pattern.compile(".*"))))
        )
    }
}