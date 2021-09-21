package sg.searchhouse.agentconnect.viewmodel.fragment.main.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import sg.searchhouse.agentconnect.data.repository.ChatRepository
import sg.searchhouse.agentconnect.model.api.chat.LoadSsmConversationsResponse
import sg.searchhouse.agentconnect.viewmodel.base.ApiBaseViewModel
import javax.inject.Inject

class DashboardConversationsViewModel constructor(application: Application) :
    ApiBaseViewModel<LoadSsmConversationsResponse>(application) {

    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var chatRepository: ChatRepository

    private val unreadMessageCount: LiveData<Int> = Transformations.map(mainResponse) {
        it.unreadMessageCount
    }

    init {
        viewModelComponent.inject(this)
    }

    fun findUnreadAllSsmConversationsFirst() {
        performRequest(chatRepository.findUnreadAllSsmConversations())
    }

    private fun findUnreadAllSsmConversationsNext() {
        performNextRequest(chatRepository.findUnreadAllSsmConversations())
    }

    fun findUnreadAllSsmConversations() {
        val count = unreadMessageCount.value ?: 0
        if (count > 0) {
            findUnreadAllSsmConversationsNext()
        } else {
            findUnreadAllSsmConversationsFirst()
        }
    }

    override fun shouldResponseBeOccupied(response: LoadSsmConversationsResponse): Boolean {
        // TODO: This is temp solution until new endpoint available
        // UnreadCount field can't be used because current GET request reset the state
        val unreadConversations = response.convos.filter { it.unreadCount > 0 }
        return unreadConversations.isNotEmpty()
    }
}