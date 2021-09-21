import { repository } from "./RealmDatabaseUtil";

function findAll(conversationId) {
  converstationIdString = conversationId.toString();
  let searchString = 'conversationId = "' + converstationIdString + '"';
  if (
    repository.objects("ConversationList").filtered(searchString).length > 0
  ) {
    return repository
      .objects("ChatMessages")
      .filtered(searchString)
      .sorted("messageId", true);
  } else {
    return;
  }
}

function save(conversationId, message) {
  converstationIdString = conversationId.toString();
  let messageId = parseInt(message.messageId);
  let msgString = JSON.stringify(message);
  let searchString = 'conversationId = "' + converstationIdString + '"';
  let addedToConversationList =
    repository.objects("ConversationList").filtered(searchString).length == 0;
  if (addedToConversationList) {
    try {
      repository.write(() => {
        repository.create("ConversationList", {
          conversationId: converstationIdString
        });
      });
    } catch (error) {
      console.log("Chat Database save error: " + error);
    }
  }
  repository.write(() => {
    try {
      repository.create("ChatMessages", {
        conversationId: converstationIdString,
        messageId: messageId,
        messages: msgString,
        messages2: msgString
      });
    } catch (error) {
      console.log("Chat Database save error: " + error);
    }
  });
}

function deleteCorruptedData(conversationId) {
  converstationIdString = conversationId.toString();
  let searchString = 'conversationId = "' + converstationIdString + '"';
  let corruptedData = repository
    .objects("ConversationList")
    .filtered(searchString);
  repository.write(() => {
    repository.delete(corruptedData);
  });
}

function deleteAll() {
  repository.write(() => {
    repository.deleteAll();
  });
}

const ChatDatabaseUtil = {
  deleteAll,
  deleteCorruptedData,
  findAll,
  save
};

export { ChatDatabaseUtil };
