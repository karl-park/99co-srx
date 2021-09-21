import request from "./axios";

function getUpdateAppStatus() {
  return request({
    url: "/mobile/iphone/consumer3.mobile3",
    method: "POST",
    params: {
      action: "getUpdateAppStatus",
    }
  });
}

const GeneralService = {
  getUpdateAppStatus
};

export { GeneralService };