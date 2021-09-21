import request from "./axios";
import { ObjectUtil, StringUtil } from "../utils";

findAgentsWithQuery = ({ query }) => {
  var searchText;
  if (!ObjectUtil.isEmpty(query) && typeof query === "string") {
    searchText = query.trim().replace(/ /g, "");
  } else if (typeof query === "number" && query > 0) {
    searchText = StringUtil.decimalValue(query, 0);
  }

  if (searchText.startsWith("+65")) {
    searchText = searchText.substring(3);
  }

  if (searchText > 0) {
    //mobile
    return request({
      url: "/mobile/iphone/consumer2.mobile3",
      method: "POST",
      params: {
        action: "findAgentsByMobile",
        mobile: searchText
      }
    });
  } else {
    //name
    return request({
      url: "/mobile/iphone/consumer2.mobile3",
      method: "POST",
      params: {
        action: "findAgentsByName",
        name: searchText
      }
    });
  }
};

loadConciergeAgents = ({
  propertyType,
  userType,
  districtId,
  townId,
  locationType,
  location
}) => {
  return request({
    url: "/srx/pcn/concierge.pcn",
    method: "POST",
    params: {
      action: "loadConciergeAgents",
      propertyType,
      userType,
      districtId,
      townId,
      locationType,
      location,
      limit: 6 //only show 6 agents
    }
  });
};

loadInitialAgents = () => {
  return request({
    url: "/mobile/iphone/consumer2.mobile3",
    method: "POST",
    params: {
      action: "initialFindAgentLoad"
    }
  });
};

mobileVerifyConsumerConcierge = ({ name, email, mobileNum, section }) => {
  return request({
    url: "/srx/pcn/concierge.pcn",
    method: "POST",
    params: {
      action: "mobileVerifyConsumerConcierge",
      name,
      email,
      mobileNum,
      section
    }
  });
};

searchAgents = ({
  searchText,
  page,
  maxResults,
  selectedDistrictIds,
  selectedHdbTownIds,
  selectedAreaSpecializations
}) => {
  return request({
    url: "/srx/agent/searchAgentsV2/redefinedSearch.srx",
    method: "POST",
    params: {
      searchText,
      page,
      maxResults: 10,
      selectedDistrictIds,
      selectedHdbTownIds,
      selectedAreaSpecializations,
      addConversationIds: "true"
    }
  });
};

//Concierge Send Request
sendRequest = ({
  name,
  email,
  userType,
  mobileNum,
  propertyType,
  districtTownId,
  keywords,
  locationType
}) => {
  return request({
    url: "/srx/pcn/concierge.pcn",
    method: "POST",
    params: {
      action: "sendRequest",
      name,
      email,
      userType,
      mobileNum,
      propertyType,
      districtTownId,
      keywords,
      locationType
    }
  });
};

const AgentSearchService = {
  findAgentsWithQuery,
  loadConciergeAgents,
  loadInitialAgents,
  mobileVerifyConsumerConcierge,
  searchAgents,
  sendRequest
};

export { AgentSearchService };
