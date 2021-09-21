/**
 * This page store some values that used to pass to backend
 */

const MapSearchZoomLevel = {
  HDBTown: 0,
  District: 1,
  Landmark: 2,
  Region: 3,
  PlanningArea: 4,
  Subzone: 5,
  DetailedView: 6
};

/**
 * Type of location search with map
 * For api "findMapView"
 * parameter name: searchMode
 */
const MapSearchMode = {
  Location: "Location",
  District: "District",
  HDBTown: "HDBTown",
  MRT: "MRT",
  School: "School",
  Lifestyle: "Lifestyle"
};

/**
 * For map keyword searches
 * For api "findMapView"
 * parameter name: searchTypeAuto
 */
const MapSearchTypeAuto = {
  None: "None",
  BuildingKey: "BuildingKey",
  StreetKey: "StreetKey",
  PostalCode: "PostalCode"
};

/**
 * get Region boundaries
 * For api "findMapRegionView"
 * parameter name: filterType
 */
const MapRegionFilterType = {
  Region: 0,
  PlanningArea: 1,
  Subzone: 2,
  GCB: 3,
  Landed: 4,
  District: 5,
  HDBTown: 6,
  ConservationBuildings: 7
};

export { MapSearchZoomLevel, MapSearchMode, MapSearchTypeAuto, MapRegionFilterType };
