package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.R
import sg.searchhouse.agentconnect.data.repository.AuthRepository
import sg.searchhouse.agentconnect.model.api.userprofile.GreetingResponse
import sg.searchhouse.agentconnect.util.SessionUtil
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import java.util.*
import javax.inject.Inject

class DashboardSearchViewModel constructor(application: Application) :
    ApiBaseViewModel<GreetingResponse>(application) {
    @Inject
    lateinit var userAuthRepository: AuthRepository
    @Inject
    lateinit var applicationContext: Context

    var greeting: LiveData<String>

    init {
        viewModelComponent.inject(this)
        greeting = Transformations.map(mainResponse) { response ->
            response?.appGreeting?.greeting ?: run {
                getPlaceholderGreeting()
            }
        }
        getAppGreeting()
    }

    fun getAppGreeting() {
        performRequest(userAuthRepository.getAppGreeting())
    }

    private fun getPlaceholderGreeting(): String {
        //Get user name from session
        val currentUser = SessionUtil.getCurrentUser()
        var userName: String = applicationContext.getString(R.string.generic_username)
        currentUser?.let {
            if (currentUser.name.isNotEmpty()) {
                userName = currentUser.name
            }
        }
        return applicationContext.getString(
            R.string.dashboard_greeting,
            applicationContext.getText(getGreeting()),
            "<b>$userName</b>"
        )
    }

    @StringRes
    private fun getGreeting(): Int {
        val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hourOfDay < 12 -> R.string.greeting_morning
            hourOfDay < 18 -> R.string.greeting_afternoon
            else -> R.string.greeting_evening
        }
    }

    override fun shouldResponseBeOccupied(response: GreetingResponse): Boolean {
        return true
    }
}