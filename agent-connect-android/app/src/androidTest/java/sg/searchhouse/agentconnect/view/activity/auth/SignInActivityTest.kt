package sg.searchhouse.agentconnect.view.activity.auth

import okhttp3.mockwebserver.MockResponse
import org.junit.Test
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.constant.Endpoint
import sg.searchhouse.agentconnect.dsl.toJsonObjectOrNull
import sg.searchhouse.agentconnect.helper.Json
import sg.searchhouse.agentconnect.model.api.auth.LoginWithEmailRequest
import sg.searchhouse.agentconnect.test.base.BaseApiActivityTest
import sg.searchhouse.agentconnect.test.dsl.*
import sg.searchhouse.agentconnect.view.activity.main.MainActivity

class SignInActivityTest : BaseApiActivityTest() {
    @Test
    fun whenEnterCorrectCredentials_andClickLoginButton_thenFinishGoToMainActivity() {
        (user launch SignInActivity::class.java).run {
            user write "donaldtrump@fmail.com" on R.id.et_username
            user write "bui1dawa11" on R.id.et_password
            user click R.id.btn_login

            expectFinishActivity()
            this expectLaunch MainActivity::class.java
        }
    }

    @Test
    fun whenEnterInvalidCredentials_andClickLoginButton_thenShowErrorTextUnderPasswordTextBox() {
        (user launch SignInActivity::class.java).run {
            user write "sipkinpeng@fmail.com" on R.id.et_username
            user write "nothinghapen" on R.id.et_password
            user click R.id.btn_login
            R.id.text_layout_password expectShowErrorText "Password does not match with record."
        }
    }

    override fun getDispatcher() = dispatcher {
        if (this isPost Endpoint.USER_AUTH_LOGIN) {
            bodyString().toJsonObjectOrNull(LoginWithEmailRequest::class.java)?.run {
                when {
                    email == "donaldtrump@fmail.com" && password == "bui1dawa11" -> {
                        MockResponse().setResponseCode(200)
                            .setBody(RESPONSE_LOGIN_SUCCESS.toString())
                    }
                    else -> MockResponse().setResponseCode(400)
                        .setBody(RESPONSE_LOGIN_FAIL.toString())
                }
            }
        } else {
            null
        }
    }

    companion object {
        private val RESPONSE_LOGIN_SUCCESS = Json {
            "result" to 1
        }

        private val RESPONSE_LOGIN_FAIL = Json {
            "errorCode" to "InvalidLogin"
            "error" to "Password does not match with record."
        }
    }
}