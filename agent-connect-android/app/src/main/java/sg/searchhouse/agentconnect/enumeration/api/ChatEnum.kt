package sg.searchhouse.agentconnect.enumeration.api

class ChatEnum {

    //TODO: use string type first, need to change to Int after updating data type of Type in SSMConversationPO
    enum class ConversationType(val value: String) {
        BLAST("1"),
        ONE_TO_ONE("2"),
        GROUP_CHAT("4"),
        BLAST_NEW_TYPE("7"),
        AGENT_ENQUIRY("1025"),
        SRX_ANNOUNCEMENTS("1026")
    }

    enum class ChatTabGroup(val value: String) {
        ALL(
            listOf(
                ConversationType.BLAST.value,
                ConversationType.ONE_TO_ONE.value,
                ConversationType.GROUP_CHAT.value,
                ConversationType.BLAST_NEW_TYPE.value,
                ConversationType.AGENT_ENQUIRY.value,
                ConversationType.SRX_ANNOUNCEMENTS.value
            ).joinToString(",")
        ),
        PUBLIC(
            listOf(
                ConversationType.ONE_TO_ONE.value,
                ConversationType.AGENT_ENQUIRY.value
            ).joinToString(",")
        ),
        SRX(ConversationType.SRX_ANNOUNCEMENTS.value),
        AGENT(
            listOf(
                ConversationType.BLAST.value,
                ConversationType.ONE_TO_ONE.value,
                ConversationType.GROUP_CHAT.value,
                ConversationType.BLAST_NEW_TYPE.value
            ).joinToString(",")
        )
    }

    //TODO: use string type first, need to change to Int after updating data type of Type in SSMMessagePO
    enum class UserType(val value: String) {
        PUBLIC("2"),
        AGENT("1")
    }

    enum class MessageType(val value: Int) {
        MESSAGE(1),
        LISTING(2),
        PHOTO(3),
        DOCUMENT(4),
        PUBLIC_LISTING(5)
    }

    enum class ChatMessagingParamType {
        CONVERSATION_ID,
        USER,
        AGENT
    }
}
