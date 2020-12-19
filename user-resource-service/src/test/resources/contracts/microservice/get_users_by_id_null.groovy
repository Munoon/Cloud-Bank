package contracts.microservice

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "get user's by microservice - not exist"

    request {
        url urlPath("/microservices/users") {
            queryParameters {
                parameter 'ids': 999
            }
        }
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
        body([])
    }
}
