package munoon.bank.service.transactional.util

class NotEnoughBalanceException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}