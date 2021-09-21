package sg.searchhouse.agentconnect.model.app

import android.os.Bundle

class NotificationParams(
    val bundle: Bundle? = null,
    val params: Map<String, String> = mapOf()
) {
    companion object {
        const val PARAM_TYPE = "type"
        const val PARAM_CONVERSATION_ID = "conversationId"
        const val PARAM_PAYLOAD = "payload"
        const val PARAM_USER_NAME = "otherUserName"
        const val PARAM_MESSAGE_ID = "messageId"
        const val PARAM_ENQUIRY_ID = "enquiryId"
        const val PARAM_ANNOUNCEMENT = "a"
    }

    fun getNotificationType(): String? {
        var type: String? = null
        if (bundle != null) {
            type = bundle.getString(PARAM_TYPE, null)
        } else if (params.isNotEmpty()) {
            type = params[PARAM_TYPE]
        }
        return type
    }

    fun getConversationId(): String {
        if (bundle != null) {
            return bundle.getString(PARAM_CONVERSATION_ID, "")
        } else if (params.isNotEmpty()) {
            return params[PARAM_CONVERSATION_ID] ?: ""
        }
        return ""
    }

    fun getPayLoad(): String {
        if (bundle != null) {
            return bundle.getString(PARAM_PAYLOAD, "")
        } else if (params.isNotEmpty()) {
            return params[PARAM_PAYLOAD] ?: ""
        }
        return ""
    }

    fun getUserName(): String {
        if (bundle != null) {
            return bundle.getString(PARAM_USER_NAME, "")
        } else if (params.isNotEmpty()) {
            return params[PARAM_USER_NAME] ?: ""
        }
        return ""
    }

    fun getMessageId(): String {
        if (bundle != null) {
            return bundle.getString(PARAM_MESSAGE_ID, "")
        } else if (params.isNotEmpty()) {
            return params[PARAM_MESSAGE_ID] ?: ""
        }
        return ""
    }

    fun getEnquiryId(): String {
        if (bundle != null) {
            return bundle.getString(PARAM_ENQUIRY_ID, "")
        } else if (params.isNotEmpty()) {
            return params[PARAM_ENQUIRY_ID] ?: ""
        }
        return ""
    }

    fun getAnnouncement(): String {
        if (bundle != null) {
            return bundle.getString(PARAM_ANNOUNCEMENT, "")
        } else if (params.isNotEmpty()) {
            return params[PARAM_ANNOUNCEMENT] ?: ""
        }
        return ""
    }
}