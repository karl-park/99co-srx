import request from "./axios";

function loadNewProjectLaunches() {
  return request({
    url: "/srx/directory/condo/loadNewProjectLaunches/directory.srx?",
    method: "POST"
  });
}

function loadNewProjectLaunchesSearch({
  searchText,
  selectedDistrictIds,
  selectedHdbTownIds,
  cdResearchSubTypes,
  isUnderConstruction,
  minSalePrice,
  maxSalePrice,
  page
}) {
  return request({
    url: "/srx/directory/condo/loadNewProjectLaunchesSearch/directory.srx?",
    method: "POST",
    params: {
      searchText,
      selectedDistrictIds,
      selectedHdbTownIds,
      cdResearchSubTypes,
      isUnderConstruction,
      minSalePrice,
      maxSalePrice,
      page
    }
  });
}

const NewLaunchesService = {
  loadNewProjectLaunches,
  loadNewProjectLaunchesSearch
};

export { NewLaunchesService };
