import request from "./axios";

findActiveAndTransactedListingsByUserId = ({ userId }) => {
  return request({
    url:
      "/srx/listing/findActiveAndTransactedListingsByUserId/redefinedSearch.srx",
    method: "GET",
    params: {
      action: "findActiveAndTransactedListingsByUserId",
      userId
    }
  });
};

loadUserAgentWithCv = ({ userId }) => {
  return request({
    url: "/srx/agent/loadUserAgentWithCv/redefinedSearch.srx",
    method: "GET",
    params: {
      action: "loadUserAgentWithCv",
      userId
    }
  });
};

const AgentCvService = {
  findActiveAndTransactedListingsByUserId,
  loadUserAgentWithCv
};

export { AgentCvService };
