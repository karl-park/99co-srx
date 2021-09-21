import request from "./axios";

function railTransitInformation() {
  return request({
    url: "/api/v1/lookup",
    method: "POST",
    params: {
      action: "railTransitInformation"
    }
  });
}
const LocationSearchService = {
  railTransitInformation
};

export { LocationSearchService };
