package munoon.bank.service.auth.authentication

import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
sealed class AuthenticationInfo(val status: AuthenticationStatus)

enum class AuthenticationStatus {
    SUCCESS, FAIL
}

@Data
@NoArgsConstructor
data class SuccessAuthenticationInfo(val redirectUrl: String) : AuthenticationInfo(AuthenticationStatus.SUCCESS)

@Data
@NoArgsConstructor
data class FailAuthenticationInfo(
    val errorCode: String?,
    val errorMessage: String?
) : AuthenticationInfo(AuthenticationStatus.FAIL)