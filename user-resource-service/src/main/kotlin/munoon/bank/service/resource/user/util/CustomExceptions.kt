package munoon.bank.service.resource.user.util

class FieldValidationException : RuntimeException {
    val field: String

    constructor(message: String, field: String) : super(message) {
        this.field = field
    }

    constructor(message: String, cause: Throwable, field: String) : super(message, cause) {
        this.field = field
    }
}