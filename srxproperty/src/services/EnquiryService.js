import request, { multiPartRequest } from "./axios";

submitSrxAgentEnquiryV2 = ({
  listingId,
  agentUserId,
  fullName,
  countryCode,
  mobileLocalNumber,
  email,
  message,
  enquirySource
}) => {
  /**
   * enquirySource
   * "M" => default,
   * "O" => concierge request
   */
  const params = {
    listingId,
    agentUserId,
    fullName,
    countryCode,
    mobileLocalNumber,
    email,
    message,
    enquirySource: enquirySource ? enquirySource : "M"
  };

  //temporary changed to fetch as having issue to send emoji with Axios
  return multiPartRequest(
    "/srx/agent/submitSrxAgentEnquiryV2/searchAgents.srx",
    params
  );
  // return request({
  //   url: "/srx/agent/submitSrxAgentEnquiryV2/searchAgents.srx",
  //   method: "POST",
  //   headers: { "content-type": "application/json;charset=utf8mb4" },
  //   params: params
  // });
};

const EnquiryService = {
  submitSrxAgentEnquiryV2
};

export { EnquiryService };
