package contracts.microservices

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "deactivate users cards by microservice"

    request {
        url "/microservices/card/deactivate?userId=100"
        method POST()
    }

    response {
        status NO_CONTENT()
    }
}