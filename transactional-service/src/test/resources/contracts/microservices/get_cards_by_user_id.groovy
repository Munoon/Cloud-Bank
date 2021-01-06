package contracts.microservices

import org.springframework.cloud.contract.spec.Contract
import org.springframework.cloud.contract.spec.internal.ServerDslProperty

import java.util.regex.Pattern

Contract.make {
    description "get cards by user id"

    request {
        url "/microservices/card/100"
        method GET()
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([[
                id: $(consumer("CARD_ID"), producer(anyRegex())),
                userId: 100,
                type: "default",
                number: "123456789012",
                balance: 0.0,
                active: true,
                primary: true,
                registered: $(consumer("2020-12-06T00:10"), producer(anyRegex()))
        ]])
    }
}

static ServerDslProperty anyRegex() {
    return new ServerDslProperty(Pattern.compile(".*"))
}