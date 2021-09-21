import request from "./axios";

loadOnlineCommunicationsWithOptionalSearch = ({
  id,
  title,
  channel,
  limit,
  page
}) => {
  return request({
    url: "/common/insert.onlinecommunication",
    method: "GET",
    params: {
      action: "loadOnlineCommunicationsWithOptionalSearch",
      id,
      title,
      channel,
      limit,
      page
    }
  });
};

const NewsPropertyService = {
  loadOnlineCommunicationsWithOptionalSearch
};

export { NewsPropertyService };
