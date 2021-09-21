import request, { multiPartRequest } from "./axios";

function createSsmConversation({ userIds, encryptedUserId, isBlast }) {
  return request({
    url: "/mobile/cobroke/ssm2.cobroke",
    method: "POST",
    params: {
      action: "createSsmConversation",
      userIds,
      // encryptedUserId,
      isBlast
    }
  });
}

function sendSsm({
  conversationId,
  userId,
  isBlast,
  messageType,
  message,
  filename,
  listingId
}) {
  const params = {
    action: "sendSsm",
    conversationId,
    userId,
    isBlast,
    messageType,
    message,
    filename,
    listingId
  };

  return multiPartRequest("/mobile/cobroke/ssm2.cobroke", params);
}

function loadSsmMessages({ userId, conversationId, messageId }) {
  return request({
    url: "/mobile/cobroke/ssm2.cobroke",
    method: "POST",
    params: {
      action: "loadSsmMessages",
      userId,
      conversationId,
      messageId
    }
  });
}

function loadSsmConversations({ userId, startIndex }) {
  return request({
    url: "/mobile/cobroke/ssm2.cobroke",
    method: "POST",
    params: {
      action: "loadSsmConversations",
      userId,
      numOfResults: 20,
      startIndex
    }
  });
}

function loadUnreadConversationsCount({ userId }) {
  return request({
    url: "/mobile/cobroke/ssm2.cobroke",
    method: "POST",
    params: {
      action: "loadUnreadConversationsCount",
      userId
    }
  });
}

function findConversationIds({ otherUserIds }) {
  return request({
    url: "/mobile/cobroke/ssm2.cobroke",
    method: "POST",
    params: {
      action: "findConversationIds",
      otherUserIds
    }
  });
}

function findFeaturedAgentsV2() {
  return request({
    url: "/srx/agent/findFeaturedAgentsV2/redefinedSearch.srx",
    method: "POST",
    params: {
      action: "findFeaturedAgentsV2"
    }
  });
}

resetUnreadCount = ({ userId, conversationId }) => {
  return request({
    url: "/mobile/cobroke/ssm2.cobroke",
    method: "POST",
    params: {
      action: "resetUnreadCount",
      userId,
      conversationId
    }
  });
};

const ChatService = {
  createSsmConversation,
  findConversationIds,
  findFeaturedAgentsV2,
  loadSsmConversations,
  loadSsmMessages,
  loadUnreadConversationsCount,
  resetUnreadCount,
  sendSsm
};

export { ChatService };
