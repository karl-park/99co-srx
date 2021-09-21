import request from "./axios";

findCommercialAppSurveyResult = () => {
  return request({
    url: "/common/survey?",
    method: "GET",
    params: {
      action: "findCommercialAppSurveyResult"
    }
  });
};

getAppGreeting = () => {
  return request({
    url: "/mobile/iphone/consumer3.mobile3?",
    method: "GET",
    params: {
      action: "getAppGreeting"
    }
  });
};

submitCommercialAppSurvey = ({ answer }) => {
  return request({
    url: "/common/survey?",
    method: "POST",
    params: {
      action: "submitCommercialAppSurvey",
      answer
    }
  });
};

//For Market Watch in homeScreen
loadMarketWatchIndices = () => {
  return request({
    url: "/srx/indices/loadMarketWatchIndices/indices.srx",
    method: "GET",
    params: {
      action: "loadMarketWatchIndices"
    }
  });
};

const MainScreenService = {
  findCommercialAppSurveyResult,
  getAppGreeting,
  submitCommercialAppSurvey,
  loadMarketWatchIndices
};

export { MainScreenService };
