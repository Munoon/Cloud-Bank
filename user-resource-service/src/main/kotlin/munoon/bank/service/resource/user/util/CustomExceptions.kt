package munoon.bank.service.resource.user.util

class NotFoundException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

class FieldValidationException : RuntimeException {
    val field: String

    constructor(message: String, field: String) : super(message) {
        this.field = field
    }

    constructor(message: String, cause: Throwable, field: String) : super(message, cause) {
        this.field = field
    }
}