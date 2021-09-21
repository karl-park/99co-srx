package sg.searchhouse.agentconnect.data.repository

import retrofit2.Call
import sg.searchhouse.agentconnect.data.datasource.SrxDataSource
import sg.searchhouse.agentconnect.model.api.DefaultResultResponse
import sg.searchhouse.agentconnect.model.api.agent.AgentPO
import sg.searchhouse.agentconnect.model.api.auth.*
import sg.searchhouse.agentconnect.model.api.userprofile.GreetingResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(private val srxDataSource: SrxDataSource) {
    fun loginWithEmail(loginRequest: LoginWithEmailRequest): Call<LoginResponse> {
        return srxDataSource.loginWithEmail(loginRequest)
    }

    fun loginWithCea(loginRequest: LoginWithCeaRequest): Call<LoginResponse> {
        return srxDataSource.loginWithCea(loginRequest)
    }

    fun verifyOTP(loginResponseData: LoginResponseData): Call<VerifyOtpResponse> {
        return srxDataSource.verifyOTP(loginResponseData)
    }

    fun resendOTP(loginResponseData: LoginResponseData): Call<VerifyOtpResponse> {
        return srxDataSource.resendOTP(loginResponseData)
    }

    fun resetPassword(forgotPasswordRequest: ForgotPasswordRequest): Call<ResetPasswordResponse> {
        return srxDataSource.resetPassword(forgotPasswordRequest)
    }

    fun createAccount(createAccountRequest: CreateAccountRequest): Call<CreateAccountResponse> {
        return srxDataSource.createAccount(createAccountRequest)
    }

    fun scheduleCallback(scheduleCallbackRequest: ScheduleCallbackRequest): Call<ScheduleCallbackResponse> {
        return srxDataSource.scheduleCallback(scheduleCallbackRequest)
    }

    fun resetDevice(resetDeviceRequest: ResetDeviceRequest): Call<ResetDeviceResponse> {
        return srxDataSource.resetDevice(resetDeviceRequest)
    }

    fun registerToken(token: String): Call<DefaultResultResponse> {
        val registerTokenRequest = RegisterTokenRequest(token)
        return srxDataSource.registerToken(registerTokenRequest)
    }

    fun unRegisterToken(): Call<DefaultResultResponse> {
        return srxDataSource.unRegisterToken()
    }

    fun logout(): Call<DefaultResultResponse> {
        return srxDataSource.logout()
    }

    fun getAppGreeting(): Call<GreetingResponse> {
        return srxDataSource.getAppGreeting()
    }

    fun checkVersion(): Call<CheckVersionResponse> {
        return srxDataSource.checkVersion()
    }

    fun loginAsAgent(agent: AgentPO): Call<DefaultResultResponse> {
        return srxDataSource.loginAsAgent(agent)
    }

    fun getConfig(): Call<GetConfigResponse> {
        return srxDataSource.getConfig()
    }
}