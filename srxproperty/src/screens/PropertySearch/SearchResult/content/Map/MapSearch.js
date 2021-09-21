import React, { Component } from "react";
import { View, Animated, Dimensions, Easing } from "react-native";
import PropTypes from "prop-types";
import MapView, {
  Polygon,
  PROVIDER_GOOGLE,
  Marker,
  Circle
} from "react-native-maps";
import { Navigation } from "react-native-navigation";
import { SearchPropertyService } from "../../../../../services";
import { FilterButton, TransactedSwitch } from "../../components";
import { Styles } from "../../Styles";
import { ObjectUtil, GoogleAnalyticUtil } from "../../../../../utils";
import { SRXColor } from "../../../../../constants";
import { ListingSearchManager } from "../../../Manager";
import {
  MapListingResults,
  ClusterAnnotation,
  ListingAnnotation
} from "./components";
import {
  MapSearchZoomLevel,
  MapSearchMode,
  MapSearchTypeAuto,
  MapRegionFilterType
} from "./constant";
import { LocationSearchOptions } from "../../../Location";
import { Spacing } from "../../../../../styles";

var { height, width } = Dimensions.get("window");

const INIT_REGION = {
  latitude: 1.335758310278024,
  longitude: 103.81860293808234,
  latitudeDelta: 0.45,
  longitudeDelta: 0.45
};

/**
 * if  zoom level <= 11, show region
 * else if zoom level <= 13, show planning area
 * else if zoom level <= 15, show subzone
 * else show individual marker
 */

/**
 * region = (0, 11]
 * planningArea = (11, 13]
 * subzone = (13, 15]
 *
 * zoomLevel > 15, show details (listings) instead of cluster
 */
const MapZoomLevel = {
  region: 11,
  planningArea: 13,
  subzone: 15
};

const GeoLocation = data => {
  return {
    id: data.id,
    location: {
      latitude: data.latitude,
      longitude: data.longitude
    }
  };
};

class MapSearch extends Component {
  static propTypes = {
    onFilterPressed: PropTypes.func,
    onTransactedSwitchValueChange: PropTypes.func,
    isTransacted: PropTypes.bool,
    searchOptions: PropTypes.object,
    componentId: PropTypes.string,
    disabledSearchModification: PropTypes.bool,
    disableGoogleAnalytic: PropTypes.bool
  };

  static defaultProps = {
    disabledSearchModification: false,
    disableGoogleAnalytic: false
  };

  state = {
    /**
     * data for marker/annotation, it could be shown as cluster or listings
     */
    data: [],
    /**
     * an key-value object to get listing ids with hash value, the keys are listing ids
     */
    listingIdHashes: {},
    /**
     * listings that load with ids by selecting listing marker
     */
    listingPOs: [],

    selectedMarkerItemHash: null,
    listingsContainerY: new Animated.Value(-height),

    featuredAgents: null,

    currentRegion: INIT_REGION,
    zoomLevel: MapZoomLevel.region,
    displayingPolygon: null,

    regionPolygon: null,
    isRegionPolygonLoading: false,
    planingAreaPolygon: null,
    isPlaningAreaPolygonLoading: false,
    subZonePolygon: null,
    isSubZonePolygonLoading: false
  };

  constructor(props) {
    super(props);

    this.renderMapData = this.renderMapData.bind(this);
    this.onRegionChangeComplete = this.onRegionChangeComplete.bind(this);
    this.onClusterAnnotationPressed = this.onClusterAnnotationPressed.bind(
      this
    );
    this.onListingAnnotationPressed = this.onListingAnnotationPressed.bind(
      this
    );
    this.onListingLoaded = this.onListingLoaded.bind(this);
  }

  componentDidMount() {
    //regions
    const {
      searchText,
      selectedAmenitiesIds,
      selectedDistrictIds,
      selectedHdbTownIds
    } = this.props.searchOptions;
    if (
      !ObjectUtil.isEmpty(searchText) ||
      (!ObjectUtil.isEmpty(selectedAmenitiesIds) || selectedAmenitiesIds > 0) ||
      (!ObjectUtil.isEmpty(selectedDistrictIds) || selectedDistrictIds > 0) ||
      (!ObjectUtil.isEmpty(selectedHdbTownIds) || selectedHdbTownIds > 0)
    ) {
      this.onSelectedLocationUpdate();
    } else {
      // this.loadRegionView({ filterType: MapRegionFilterType.Region });
      this.loadListingInRegion(INIT_REGION, null, true);
    }
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (prevProps.searchOptions != this.props.searchOptions) {
      const prevOptions = prevProps.searchOptions;
      const currentOptions = this.props.searchOptions;

      if (
        prevOptions.searchText !== currentOptions.searchText ||
        prevOptions.selectedAmenitiesIds !==
          currentOptions.selectedAmenitiesIds ||
        prevOptions.selectedDistrictIds !==
          currentOptions.selectedDistrictIds ||
        prevOptions.selectedHdbTownIds !== currentOptions.selectedHdbTownIds
      ) {
        //address updated
        this.onSelectedLocationUpdate();
      } else {
        const { currentRegion } = this.state;
        if (!ObjectUtil.isEmpty(currentRegion)) {
          this.loadListingInRegion(currentRegion, null, true);
        }
      }
    }

    if (prevProps.isTransacted != this.props.isTransacted) {
      this.setState({ selectedMarkerItemHash: null, listingPOs: [] }, () => {
        const { currentRegion } = this.state;
        if (!ObjectUtil.isEmpty(currentRegion)) {
          this.loadListingInRegion(currentRegion, null, true);
        }
      });
    }

    if (prevState.zoomLevel != this.state.zoomLevel) {
      //   const {
      //     regionPolygon,
      //     planingAreaPolygon,
      //     subZonePolygon,
      //     zoomLevel
      //   } = this.state;
      //   if (zoomLevel <= MapZoomLevel.region) {
      //     if (ObjectUtil.isEmpty(regionPolygon)) {
      //       this.loadRegionView({ filterType: MapRegionFilterType.Region });
      //     }
      //   } else if (zoomLevel <= MapZoomLevel.planningArea) {
      //     if (ObjectUtil.isEmpty(planingAreaPolygon)) {
      //       this.loadRegionView({ filterType: MapRegionFilterType.PlanningArea });
      //     }
      //   } else if (zoomLevel <= MapZoomLevel.subzone) {
      //     if (ObjectUtil.isEmpty(subZonePolygon)) {
      //       this.loadRegionView({ filterType: MapRegionFilterType.Subzone });
      //     }
      //   }
      // }
      // if (prevState.listingContainerSize !== this.state.listingContainerSize) {
      //   if (!ObjectUtil.isEmpty(this.state.selectedMarkerItemHash)) {
      //     this.showListingContainer();
      //   }
      if (this.state.zoomLevel <= MapZoomLevel.subzone) {
        this.hideListingContainer();
      }
    }
    if (
      prevState.listingPOs !== this.state.listingPOs &&
      !ObjectUtil.isEmpty(this.state.listingPOs)
    ) {
      this.showListingContainer();
    }
    if (
      prevState.selectedMarkerItemHash !== this.state.selectedMarkerItemHash
    ) {
      if (ObjectUtil.isEmpty(this.state.selectedMarkerItemHash)) {
        this.hideListingContainer();
      }
    }
  }

  onSelectedLocationUpdate() {
    this.setState({ selectedMarkerItemHash: null }, () => {
      const {
        searchText,
        selectedAmenitiesIds,
        selectedDistrictIds,
        selectedHdbTownIds
      } = this.props.searchOptions;
      const { currentRegion } = this.state;
      if (!ObjectUtil.isEmpty(selectedDistrictIds) || selectedDistrictIds > 0) {
        this.trackMapSearchEvent();
        this.loadSelectedZones({
          filterType: MapRegionFilterType.District,
          filterId: selectedDistrictIds
        });
      } else if (
        !ObjectUtil.isEmpty(selectedHdbTownIds) ||
        selectedHdbTownIds > 0
      ) {
        this.trackMapSearchEvent();
        this.loadSelectedZones({
          filterType: MapRegionFilterType.HDBTown,
          filterId: selectedHdbTownIds
        });
      } else if (
        !ObjectUtil.isEmpty(selectedAmenitiesIds) ||
        selectedAmenitiesIds > 0
      ) {
        this.setState({ displayingPolygon: null, searchPoints: null }, () => {
          this.loadListingForSelectedLocation(currentRegion, null, true);
        });
      } else if (!ObjectUtil.isEmpty(searchText)) {
        this.setState({ displayingPolygon: null, searchPoints: null }, () => {
          this.loadListingForSelectedLocation(currentRegion, null, true);
        });
      } else {
        this.setState({ displayingPolygon: null, searchPoints: null }, () => {
          this.loadListingForSelectedLocation(currentRegion, null, true);
        });
      }
    });
  }

  showListingContainer() {
    const {
      mapScreenSize,
      listingContainerSize,
      listingsContainerY
    } = this.state;
    // const heightDiff = mapScreenSize.height - listingContainerSize.height;
    Animated.timing(listingsContainerY, {
      toValue: 0,
      duration: 100,
      easing: Easing.out(Easing.ease)
    }).start();
  }

  hideListingContainer() {
    const {
      mapScreenSize,
      listingContainerSize,
      listingsContainerY
    } = this.state;
    Animated.timing(listingsContainerY, {
      toValue: -mapScreenSize.height,
      duration: 100,
      easing: Easing.out(Easing.ease)
    }).start();
  }

  //to be deleted
  loadListingsLocation() {
    const { searchOptions } = this.props;
    SearchPropertyService.searchListing({
      ...searchOptions,
      searchType: "latLngBounds"
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const { listingResult } = response;
        if (!ObjectUtil.isEmpty(listingResult)) {
          const { listingLocations } = listingResult;
          if (
            !ObjectUtil.isEmpty(listingLocations) &&
            Array.isArray(listingLocations)
          ) {
            const newData = [];
            listingLocations.map(item => {
              newData.push(new GeoLocation(item));
            });
            this.setState({ data: newData });
          }
        }
      }
    });
  }

  loadListingsByIds({ listingIds }) {
    const { searchOptions, isTransacted } = this.props;

    this.setState({ listingPOs: [] }, () => {
      this.searchManager = new ListingSearchManager({
        ...searchOptions,
        // type: searchOptions.type,
        listingIds: listingIds.join(","),
        radiusInKm: "0",
        isTransacted,
        featuredAgents: true
      });
      this.searchManager.register(this.onListingLoaded);
      this.searchManager.search({ tracking: false });
    });
  }

  onListingLoaded({ allListings, featuredAgents, error, manager }) {
    let listings = [];
    if (!ObjectUtil.isEmpty(allListings)) {
      listings = allListings;
    }

    this.setState({
      listingPOs: listings,
      featuredAgents
    });
  }

  loadRegionView({ filterType }) {
    const {
      isRegionPolygonLoading,
      isPlaningAreaPolygonLoading,
      isSubZonePolygonLoading,
      zoomLevel
    } = this.state;

    let loadingObject;
    if (filterType === MapRegionFilterType.Region && !isRegionPolygonLoading) {
      loadingObject = { isRegionPolygonLoading: true };
    } else if (
      filterType === MapRegionFilterType.PlanningArea &&
      !isPlaningAreaPolygonLoading
    ) {
      loadingObject = { isPlaningAreaPolygonLoading: true };
    } else if (
      filterType === MapRegionFilterType.Subzone &&
      !isSubZonePolygonLoading
    ) {
      loadingObject = { isSubZonePolygonLoading: true };
    }

    if (!ObjectUtil.isEmpty(loadingObject)) {
      this.setState(loadingObject, () => {
        SearchPropertyService.findMapRegionView({ filterType: filterType })
          .then(response => {
            let stateToUpdate;
            if (filterType === MapRegionFilterType.Region) {
              stateToUpdate = { isRegionPolygonLoading: false };
            } else if (filterType === MapRegionFilterType.PlanningArea) {
              stateToUpdate = { isPlaningAreaPolygonLoading: false };
            } else if (filterType === MapRegionFilterType.Subzone) {
              stateToUpdate = { isSubZonePolygonLoading: false };
            }

            if (!ObjectUtil.isEmpty(response)) {
              const { result } = response;
              if (!ObjectUtil.isEmpty(result)) {
                const { features } = result;
                if (!ObjectUtil.isEmpty(features)) {
                  if (filterType === MapRegionFilterType.Region) {
                    stateToUpdate = {
                      ...stateToUpdate,
                      regionPolygon: features
                    };
                  } else if (filterType === MapRegionFilterType.PlanningArea) {
                    stateToUpdate = {
                      ...stateToUpdate,
                      planingAreaPolygon: features
                    };
                  } else if (filterType === MapRegionFilterType.Subzone) {
                    stateToUpdate = {
                      ...stateToUpdate,
                      subZonePolygon: features
                    };
                  }
                }
              }
            }

            this.setState(stateToUpdate);
          })
          .catch(error => {
            if (filterType === MapRegionFilterType.Region) {
              this.setState({ isRegionPolygonLoading: false });
            } else if (filterType === MapRegionFilterType.PlanningArea) {
              this.setState({ isPlaningAreaPolygonLoading: false });
            } else if (filterType === MapRegionFilterType.Subzone) {
              this.setState({ isSubZonePolygonLoading: false });
            }
          });
      });
    }
  }

  trackMapSearchEvent() {
    const { searchOptions, isTransacted, disableGoogleAnalytic } = this.props;

    const {
      locationType,
      selectedDistrictIds,
      selectedHdbTownIds,
      selectedAmenitiesIds,
      searchText,
      displayText,
      suggestionEntryType,
      ...rest
    } = searchOptions;

    let newSearch = {
      ...searchOptions,
      isTransacted: isTransacted
    };

    if (!disableGoogleAnalytic) {
      GoogleAnalyticUtil.trackMapListingSearch({
        parameters: newSearch
      });
    }
  }

  loadSelectedZones({ filterType, filterId }) {
    SearchPropertyService.findMapRegionView({ filterType, filterId }).then(
      response => {
        if (!ObjectUtil.isEmpty(response)) {
          const { result } = response;
          if (!ObjectUtil.isEmpty(result)) {
            const { features } = result;
            if (!ObjectUtil.isEmpty(features)) {
              this.setState({ displayingPolygon: features }, () => {
                const lats = [];
                const lngs = [];
                if (!ObjectUtil.isEmpty(features)) {
                  features.map(item => {
                    const { geometry } = item;
                    if (!ObjectUtil.isEmpty(geometry)) {
                      const { coordinates } = geometry;
                      coordinates.map(coordinateArray => {
                        coordinateArray.map(coordinate => {
                          const latitude = coordinate[1];
                          const longitute = coordinate[0];
                          lats.push(latitude);
                          lngs.push(longitute);
                        });
                      });
                    }
                  });
                }

                // calc the min and max lng and lat
                var minlat = Math.min.apply(null, lats),
                  maxlat = Math.max.apply(null, lats);
                var minlng = Math.min.apply(null, lngs),
                  maxlng = Math.max.apply(null, lngs);

                const centerLat = (maxlat + minlat) / 2;
                const centerLng = (maxlng + minlng) / 2;
                const latDelta = Math.abs(maxlat - centerLat) * 2;
                const lngDelta = Math.abs(maxlng - centerLng) * 2;

                const newRegion = {
                  latitude: centerLat,
                  longitude: centerLng,
                  latitudeDelta: latDelta,
                  longitudeDelta: lngDelta
                };
                // this.loadListingInRegion(newRegion);
                this.map.animateToRegion(newRegion);
              });
            }
          }
        }
      }
    );
  }

  getZoomLevelWithRegion(region) {
    return Math.round(Math.log(360 / region.longitudeDelta) / Math.LN2);
  }

  getLongitudeDeltaWithZoomLevel(level) {
    return 360 / Math.pow(Math.E, level * Math.LN2);
  }

  loadListingInRegion(region, zoomLevel, tracking) {
    /**
     * region is required
     */
    const { searchOptions, isTransacted } = this.props;

    const {
      locationType,
      selectedDistrictIds,
      selectedHdbTownIds,
      selectedAmenitiesIds,
      searchText,
      displayText,
      suggestionEntryType,
      ...rest
    } = searchOptions;
    let newSearch = {
      ...rest,
      isTransacted: isTransacted,

      //default parameters that are required
      zoomLevel: MapSearchZoomLevel.Region, //starting from 3 = region
      searchMode: MapSearchMode.Location
    };

    if (region) {
      const { latitude, latitudeDelta, longitude, longitudeDelta } = region;

      newSearch = {
        ...newSearch,
        displayMinLat: latitude - latitudeDelta / 2,
        displayMinLng: longitude - longitudeDelta / 2,
        displayMaxLat: latitude + latitudeDelta / 2,
        displayMaxLng: longitude + longitudeDelta / 2,
        ignoreDisplayBounds: false
      };
    }

    if (zoomLevel) {
      newSearch = { ...newSearch, zoomLevel };
    } else if (region) {
      const zoom = this.getZoomLevelWithRegion(region);
      if (zoom <= MapZoomLevel.region) {
        newSearch = { ...newSearch, zoomLevel: MapSearchZoomLevel.Region };
      } else if (zoom <= MapZoomLevel.planningArea) {
        newSearch = {
          ...newSearch,
          zoomLevel: MapSearchZoomLevel.PlanningArea
        };
      } else if (zoom <= MapZoomLevel.subzone) {
        newSearch = { ...newSearch, zoomLevel: MapSearchZoomLevel.Subzone };
      } else {
        newSearch = {
          ...newSearch,
          zoomLevel: MapSearchZoomLevel.DetailedView
        };
      }
    }

    if (newSearch.zoomLevel == MapSearchZoomLevel.DetailedView) {
      newSearch = {
        ...newSearch,
        ignoreDisplayBounds: false
      };
    } else {
      newSearch = {
        ...newSearch,
        ignoreDisplayBounds: true
      };
    }

    if (!ObjectUtil.isEmpty(searchOptions)) {
      if (searchOptions.selectedDistrictIds) {
        newSearch = {
          ...newSearch,
          searchMode: MapSearchMode.District,
          selectedDistrictIds: searchOptions.selectedDistrictIds
        };
      } else if (searchOptions.selectedHdbTownIds) {
        newSearch = {
          ...newSearch,
          searchMode: MapSearchMode.HDBTown,
          selectedHdbTownIds: searchOptions.selectedHdbTownIds
        };
      } else if (searchOptions.selectedAmenitiesIds) {
        let numberOfAmenities = 0;
        if (typeof searchOptions.selectedAmenitiesIds === "string") {
          let amenitiesArray = searchOptions.selectedAmenitiesIds.split(",");
          numberOfAmenities = amenitiesArray.length;
        } else if (typeof searchOptions.selectedAmenitiesIds === "number") {
          numberOfAmenities = 1;
        }

        let finalZoomLevel = newSearch.zoomLevel;
        const zoom = this.getZoomLevelWithRegion(region);
        if (zoom >= MapZoomLevel.subzone) {
          finalZoomLevel = MapSearchZoomLevel.DetailedView;
        } else {
          finalZoomLevel = MapSearchZoomLevel.Region;
        }

        let searchMode = MapSearchMode.MRT; //if really couldn't get any type, pass this
        if (!ObjectUtil.isEmpty(locationType)) {
          if (locationType === LocationSearchOptions.mrt) {
            searchMode = MapSearchMode.MRT;
          } else if (locationType === LocationSearchOptions.school) {
            searchMode = MapSearchMode.School;
          }
        }

        newSearch = {
          ...newSearch,
          searchMode,
          zoomLevel: finalZoomLevel,
          selectedAmenitiesIds: searchOptions.selectedAmenitiesIds,
          distance: 0.5,
          ignoreDisplayBounds: false
        };
      } else if (!ObjectUtil.isEmpty(searchOptions.searchText)) {
        /**
         * The value suggestionEntryType should be passing from the auto complete, or history ("recent searches")
         *
         * however, for version 4.1.1 & verion before that, suggestionEntryType is missing, without setting the value, it will default to "None"
         * default to "None" will causing it to load 6k plus results, which will cause the app laggy for a while to handle the data and render the markers
         * to prevent this, the following methods is added, by adding a guessing values to minimise the possibility of no result situation
         */
        let handledSearchTypeAuto = MapSearchTypeAuto.BuildingKey;
        if (!ObjectUtil.isEmpty(suggestionEntryType)) {
          if (suggestionEntryType.toUpperCase() === "POSTAL") {
            handledSearchTypeAuto = MapSearchTypeAuto.PostalCode;
          } else if (suggestionEntryType.toUpperCase() === "STREET") {
            handledSearchTypeAuto = MapSearchTypeAuto.StreetKey;
          }
        } else {
          if (
            searchOptions.searchText.toLowerCase().includes("street") ||
            searchOptions.searchText.toLowerCase().includes("road")
          ) {
            handledSearchTypeAuto = MapSearchTypeAuto.StreetKey;
          } else if (!isNaN(searchOptions.searchText)) {
            handledSearchTypeAuto = MapSearchTypeAuto.PostalCode;
          }
        }

        newSearch = {
          ...newSearch,
          searchMode: MapSearchMode.Location,
          zoomLevel: MapSearchZoomLevel.DetailedView,
          searchText: searchOptions.searchText,
          ignoreDisplayBounds: true,
          searchTypeAuto: handledSearchTypeAuto || MapSearchTypeAuto.BuildingKey
        };
      }
    }

    if (tracking) {
      this.trackMapSearchEvent();
    }

    SearchPropertyService.findMapView(newSearch).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const { result } = response;
        if (!ObjectUtil.isEmpty(result)) {
          const { listingIdHashes, mapData, center } = result;
          if (!ObjectUtil.isEmpty(mapData)) {
            const { features } = mapData;
            if (Array.isArray(features)) {
              let stateToUpdate = {
                data: features,
                listingIdHashes
              };
              if (!searchOptions.selectedAmenitiesIds) {
                if (!ObjectUtil.isEmpty(center)) {
                  stateToUpdate = {
                    ...stateToUpdate,
                    searchPoints: [
                      {
                        name: displayText,
                        latitude: center.latitude,
                        longitude: center.longitude
                      }
                    ]
                  };
                } else {
                  stateToUpdate = {
                    ...stateToUpdate,
                    searchPoints: null
                  };
                }
              }
              this.setState(stateToUpdate);
            }
          }
        }
      }
    });
  }

  loadListingForSelectedLocation(region, zoomLevel, tracking) {
    /**
     * region is required
     */
    const { searchOptions, isTransacted } = this.props;

    const {
      locationType,
      selectedDistrictIds,
      selectedHdbTownIds,
      selectedAmenitiesIds,
      searchText,
      displayText,
      suggestionEntryType,
      ...rest
    } = searchOptions;
    let newSearch = {
      ...rest,
      isTransacted: isTransacted,

      //default parameters that are required
      zoomLevel: MapSearchZoomLevel.Region, //starting from 3 = region
      searchMode: MapSearchMode.Location
    };

    if (region) {
      const { latitude, latitudeDelta, longitude, longitudeDelta } = region;

      newSearch = {
        ...newSearch,
        displayMinLat: latitude - latitudeDelta / 2,
        displayMinLng: longitude - longitudeDelta / 2,
        displayMaxLat: latitude + latitudeDelta / 2,
        displayMaxLng: longitude + longitudeDelta / 2,
        ignoreDisplayBounds: false
      };
    }

    if (zoomLevel) {
      newSearch = { ...newSearch, zoomLevel };
    } else if (region) {
      const zoom = this.getZoomLevelWithRegion(region);
      if (zoom <= MapZoomLevel.region) {
        newSearch = { ...newSearch, zoomLevel: MapSearchZoomLevel.Region };
      } else if (zoom <= MapZoomLevel.planningArea) {
        newSearch = {
          ...newSearch,
          zoomLevel: MapSearchZoomLevel.PlanningArea
        };
      } else if (zoom <= MapZoomLevel.subzone) {
        newSearch = { ...newSearch, zoomLevel: MapSearchZoomLevel.Subzone };
      } else {
        newSearch = {
          ...newSearch,
          zoomLevel: MapSearchZoomLevel.DetailedView
        };
      }
    }

    if (newSearch.zoomLevel == MapSearchZoomLevel.DetailedView) {
      newSearch = {
        ...newSearch,
        ignoreDisplayBounds: false
      };
    } else {
      newSearch = {
        ...newSearch,
        ignoreDisplayBounds: true
      };
    }

    if (!ObjectUtil.isEmpty(searchOptions)) {
      if (searchOptions.selectedDistrictIds) {
        newSearch = {
          ...newSearch,
          searchMode: MapSearchMode.District,
          selectedDistrictIds: searchOptions.selectedDistrictIds
        };
      } else if (searchOptions.selectedHdbTownIds) {
        newSearch = {
          ...newSearch,
          searchMode: MapSearchMode.HDBTown,
          selectedHdbTownIds: searchOptions.selectedHdbTownIds
        };
      } else if (searchOptions.selectedAmenitiesIds) {
        let numberOfAmenities = 0;
        if (typeof searchOptions.selectedAmenitiesIds === "string") {
          let amenitiesArray = searchOptions.selectedAmenitiesIds.split(",");
          numberOfAmenities = amenitiesArray.length;
        } else if (typeof searchOptions.selectedAmenitiesIds === "number") {
          numberOfAmenities = 1;
        }

        let finalZoomLevel = zoomLevel;
        if (!zoomLevel) {
          finalZoomLevel = 3; //numberOfAmenities > 1 ? 3 : 6;
        }

        let searchMode = MapSearchMode.MRT; //if really couldn't get any type, pass this
        if (!ObjectUtil.isEmpty(locationType)) {
          if (locationType === LocationSearchOptions.mrt) {
            searchMode = MapSearchMode.MRT;
          } else if (locationType === LocationSearchOptions.school) {
            searchMode = MapSearchMode.School;
          }
        }

        newSearch = {
          ...newSearch,
          searchMode: searchMode,
          zoomLevel: finalZoomLevel,
          selectedAmenitiesIds: searchOptions.selectedAmenitiesIds,
          distance: 0.5,
          ignoreDisplayBounds: true
        };
      } else if (!ObjectUtil.isEmpty(searchOptions.searchText)) {
        /**
         * The value suggestionEntryType should be passing from the auto complete, or history ("recent searches")
         *
         * however, for version 4.1.1 & verion before that, suggestionEntryType is missing, without setting the value, it will default to "None"
         * default to "None" will causing it to load 6k plus results, which will cause the app laggy for a while to handle the data and render the markers
         * to prevent this, the following methods is added, by adding a guessing values to minimise the possibility of no result situation
         */
        let handledSearchTypeAuto = MapSearchTypeAuto.BuildingKey;
        if (!ObjectUtil.isEmpty(suggestionEntryType)) {
          if (suggestionEntryType.toUpperCase() === "POSTAL") {
            handledSearchTypeAuto = MapSearchTypeAuto.PostalCode;
          } else if (suggestionEntryType.toUpperCase() === "STREET") {
            handledSearchTypeAuto = MapSearchTypeAuto.StreetKey;
          }
        } else {
          if (
            searchOptions.searchText.toLowerCase().includes("street") ||
            searchOptions.searchText.toLowerCase().includes("road")
          ) {
            handledSearchTypeAuto = MapSearchTypeAuto.StreetKey;
          } else if (!isNaN(searchOptions.searchText)) {
            handledSearchTypeAuto = MapSearchTypeAuto.PostalCode;
          }
        }

        newSearch = {
          ...newSearch,
          searchMode: MapSearchMode.Location,
          zoomLevel: MapSearchZoomLevel.DetailedView,
          searchText: searchOptions.searchText,
          distance: 0.5,
          ignoreDisplayBounds: true,
          searchTypeAuto: handledSearchTypeAuto || MapSearchTypeAuto.BuildingKey
        };
      }
    }

    if (tracking) {
      this.trackMapSearchEvent();
    }

    SearchPropertyService.findMapView(newSearch).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const { result } = response;
        if (!ObjectUtil.isEmpty(result)) {
          const { listingIdHashes, mapData, center } = result;

          if (!ObjectUtil.isEmpty(mapData)) {
            const { features } = mapData;
            if (Array.isArray(features)) {
              this.setState(
                {
                  data: features,
                  listingIdHashes
                },
                () => {
                  const centerPoints = [];
                  if (!ObjectUtil.isEmpty(center)) {
                    centerPoints.push({
                      name: displayText,
                      latitude: center.latitude,
                      longitude: center.longitude
                    });
                  }
                  if (
                    searchOptions.selectedAmenitiesIds ||
                    searchOptions.searchText
                  ) {
                    this.getBoundsOfListings(features);
                    if (newSearch.zoomLevel === MapSearchZoomLevel.Region) {
                      if (!ObjectUtil.isEmpty(features)) {
                        features.map(item => {
                          const { geometry, properties } = item;
                          if (
                            !ObjectUtil.isEmpty(geometry) &&
                            !ObjectUtil.isEmpty(properties)
                          ) {
                            const { latitude, longitude } = geometry;
                            const { name } = properties;

                            centerPoints.push({
                              latitude,
                              longitude,
                              name
                            });
                          }
                        });
                      }
                    }
                  }
                  this.setState({
                    searchPoints: centerPoints
                  });
                }
              );
            }
          }
        }
      }
    });
  }

  getBoundsOfListings(data) {
    const lats = [];
    const lngs = [];
    if (!ObjectUtil.isEmpty(data)) {
      data.map(item => {
        const { geometry } = item;
        if (!ObjectUtil.isEmpty(geometry)) {
          const { latitude, longitude } = geometry;

          lats.push(latitude);
          lngs.push(longitude);
        }
      });

      // calc the min and max lng and lat
      var minlat = Math.min.apply(null, lats),
        maxlat = Math.max.apply(null, lats);
      var minlng = Math.min.apply(null, lngs),
        maxlng = Math.max.apply(null, lngs);

      const centerLat = (maxlat + minlat) / 2;
      const centerLng = (maxlng + minlng) / 2;
      const latDelta = Math.max(
        Math.abs(maxlat - centerLat) * 2 + 0.2 / 111,
        1.2 / 111
      );
      const lngDelta = Math.max(
        Math.abs(maxlng - centerLng) * 2 + 0.2 / 111,
        1.2 / 111
      );

      const newRegion = {
        latitude: centerLat,
        longitude: centerLng,
        latitudeDelta: latDelta,
        longitudeDelta: lngDelta
      };

      this.map.animateToRegion(newRegion);
    }
  }

  onRegionChangeComplete(region) {
    let zoom = this.getZoomLevelWithRegion(region);

    let loadingRegion = region; // disable this checking

    /**
     * To load smaller area while listings is showing
     */
    /*
    const { mapScreenSize, listingContainerSize, selectedMarkerItemHash } = this.state;
    
    if (!ObjectUtil.isEmpty(selectedMarkerItemHash)) {
      loadingRegion.latitudeDelta = region.latitudeDelta * (mapScreenSize.height-listingContainerSize.height)/mapScreenSize.height;
      loadingRegion.latitude = region.latitude + loadingRegion.latitudeDelta/2;
    }
    */ this.setState(
      { zoomLevel: zoom, currentRegion: region },
      () => {
        this.loadListingInRegion(loadingRegion, null, false);
      }
    );
  }

  onClusterAnnotationPressed({ nativeEvent: { coordinate } }) {
    // latitude: 1.301793102728908
    // latitudeDelta: 0.30018084442992055
    // longitude: 103.83359015694761
    // longitudeDelta: 0.2134607961335746
    const { zoomLevel } = this.state;

    const lngDelta = this.getLongitudeDeltaWithZoomLevel(zoomLevel + 2);
    const targetLocation = {
      latitude: coordinate.latitude,
      longitude: coordinate.longitude,
      latitudeDelta: lngDelta,
      longitudeDelta: lngDelta
    };
    this.map.animateToRegion(targetLocation);
  }

  onListingAnnotationPressed({ event, data }) {
    if (!ObjectUtil.isEmpty(data)) {
      const { geometry, properties } = data;
      if (!ObjectUtil.isEmpty(properties)) {
        const { hash } = properties;
        if (!ObjectUtil.isEmpty(hash)) {
          const listingIds = this.getKeysByValue(
            this.state.listingIdHashes,
            hash
          );
          this.setState({ selectedMarkerItemHash: hash }, () => {
            const { currentRegion } = this.state;

            this.loadListingsByIds({ listingIds });
            if (!ObjectUtil.isEmpty(currentRegion)) {
              const targetLocation = {
                latitude: geometry.latitude - currentRegion.latitudeDelta / 4,
                longitude: geometry.longitude,
                latitudeDelta: currentRegion.latitudeDelta,
                longitudeDelta: currentRegion.longitudeDelta
              };
              this.map.animateToRegion(targetLocation, 100);
            }
          });
        }
      }
    }
  }

  getKeysByValue(object, value) {
    const matchedKeys = [];
    Object.keys(object).map(key => {
      if (object[key] == value) {
        matchedKeys.push(key);
      }
    });
    return matchedKeys;
  }

  onCardItemPressed = listingPO => {
    const { isTransacted } = this.props;
    if (isTransacted) {
      //direct to agent CV
      let agentPO = listingPO.getAgentPO();
      if (!ObjectUtil.isEmpty(agentPO)) {
        Navigation.push(this.props.componentId, {
          component: {
            name: "ConciergeStack.AgentCV",
            passProps: {
              agentUserId: agentPO.getAgentId()
            }
          }
        });
      }
    } else {
      //direct to listing detailed page
      Navigation.push(this.props.componentId, {
        component: {
          name: "PropertySearchStack.ListingDetails",
          passProps: {
            listingId: listingPO.getListingId(),
            refType: listingPO.listingType
          }
        }
      });
    }
  };

  onEndReached = () => {
    if (this.searchManager) this.searchManager.loadMore();
  };

  onFeaturedAgentSelected = agentPO => {
    if (!ObjectUtil.isEmpty(agentPO)) {
      Navigation.push(this.props.componentId, {
        component: {
          name: "ConciergeStack.AgentCV",
          passProps: {
            agentUserId: agentPO.getAgentId()
          }
        }
      });
    }
  };

  inBounds(point, bounds) {
    const NE_long = bounds.longitude + bounds.longitudeDelta / 2;
    const SW_long = bounds.longitude - bounds.longitudeDelta / 2;

    const NE_lat = bounds.latitude + bounds.latitudeDelta / 2;
    const SW_lat = bounds.latitude - bounds.latitudeDelta / 2;

    var eastBound = point.longitude < NE_long;
    var westBound = point.longitude > SW_long;
    var inLong;

    if (NE_long < SW_long) {
      inLong = eastBound || westBound;
    } else {
      inLong = eastBound && westBound;
    }

    var inLat = point.latitude > SW_lat && point.latitude < NE_lat;
    return inLat && inLong;
  }

  renderFilterButton() {
    const { onFilterPressed } = this.props;
    return <FilterButton onPress={onFilterPressed} />;
  }

  renderTransactedSwitch() {
    const {
      isTransacted,
      onTransactedSwitchValueChange,
      searchOptions
    } = this.props;
    return (
      <TransactedSwitch
        isSale={searchOptions.type === "S"}
        isTransacted={isTransacted}
        onSwitchValueChange={onTransactedSwitchValueChange}
      />
    );
  }

  renderFilterButtonAndTransactedSwitch() {
    const { disabledSearchModification } = this.props;
    if (!disabledSearchModification) {
      return (
        <View
          style={[
            Styles.filterAndSortContainer,
            { paddingHorizontal: Spacing.M }
          ]}
        >
          {this.renderFilterButton()}
          {this.renderTransactedSwitch()}
        </View>
      );
    }
  }

  renderSearchPointsMarker() {
    const { searchPoints, zoomLevel } = this.state;
    if (Array.isArray(searchPoints) && zoomLevel >= MapZoomLevel.subzone) {
      return searchPoints.map(point => {
        return (
          <Marker
            title={point.name}
            coordinate={{
              latitude: point.latitude,
              longitude: point.longitude
            }}
          />
        );
      });
    }
  }

  renderSearchPointsCircle() {
    const { searchPoints, zoomLevel } = this.state;
    if (Array.isArray(searchPoints) && zoomLevel >= MapZoomLevel.subzone) {
      return searchPoints.map(point => {
        return (
          <Circle
            center={{
              latitude: point.latitude,
              longitude: point.longitude
            }}
            radius={500}
            strokeColor={SRXColor.Teal}
            fillColor={SRXColor.Teal + "1A"} //#rrggbbaa
          />
        );
      });
    }
  }

  renderPolygonItem(coordinate) {
    const polygon = [];
    coordinate.map(item =>
      polygon.push({ latitude: item[1], longitude: item[0] })
    );
    return (
      <Polygon
        coordinates={polygon}
        strokeWidth={3}
        strokeColor={SRXColor.Teal}
        fillColor={SRXColor.Teal + "33"}
      />
    );
  }

  renderPolygonGroups(item) {
    const { geometry } = item;
    if (!ObjectUtil.isEmpty(geometry)) {
      const { coordinates } = geometry;
      return coordinates.map(coordinate => this.renderPolygonItem(coordinate));
    }
  }

  renderPolygons() {
    const {
      zoomLevel,
      displayingPolygon,
      regionPolygon,
      planingAreaPolygon,
      subZonePolygon
    } = this.state;

    const {
      searchText,
      selectedAmenitiesIds,
      selectedDistrictIds,
      selectedHdbTownIds
    } = this.props.searchOptions;

    if (!ObjectUtil.isEmpty(displayingPolygon)) {
      return displayingPolygon.map(item => this.renderPolygonGroups(item));
    } else {
      /**
       * if searchText or amenities
       * no polygon for the location to show
       */
      if (
        ObjectUtil.isEmpty(searchText) &&
        ObjectUtil.isEmpty(selectedAmenitiesIds) &&
        zoomLevel <= MapZoomLevel.subzone
      ) {
        if (
          zoomLevel > MapZoomLevel.planningArea &&
          !ObjectUtil.isEmpty(subZonePolygon)
        ) {
          return subZonePolygon.map(item => this.renderPolygonGroups(item));
        } else if (
          zoomLevel > MapZoomLevel.region &&
          !ObjectUtil.isEmpty(planingAreaPolygon)
        ) {
          return planingAreaPolygon.map(item => this.renderPolygonGroups(item));
        } else if (!ObjectUtil.isEmpty(regionPolygon)) {
          return regionPolygon.map(item => this.renderPolygonGroups(item));
        }
      }
    }
  }

  renderMapData() {
    const {
      data,
      zoomLevel,
      selectedMarkerItemHash,
      currentRegion
    } = this.state;
    const { isTransacted } = this.props;
    if (!ObjectUtil.isEmpty(data)) {
      return data.map(item => {
        const { geometry, properties } = item;
        const { hash } = properties;
        if (this.inBounds(geometry, currentRegion)) {
          if (hash) {
            return (
              <ListingAnnotation
                key={hash}
                data={item}
                coordinate={geometry}
                onPress={this.onListingAnnotationPressed}
                isSelected={selectedMarkerItemHash === hash}
                isTransacted={isTransacted}
              >
                {properties.numListings}
              </ListingAnnotation>
            );
          } else {
            return (
              <ClusterAnnotation
                key={properties.markerId}
                title={properties.name}
                isTransacted={isTransacted}
                coordinate={geometry}
                onPress={this.onClusterAnnotationPressed}
              >
                {properties.numListings}
              </ClusterAnnotation>
            );
          }
        }
      });
    }
  }

  renderMap() {
    const { zoomLevel, listingsContainerY, currentRegion } = this.state;
    return (
      <MapView
        provider={PROVIDER_GOOGLE}
        style={{ flex: 1 }}
        ref={r => {
          this.map = r;
        }}
        initialRegion={INIT_REGION}
        minZoomLevel={9}
        maxZoomLevel={18}
        mapPadding={{
          top: 10,
          right: 10,
          bottom: 10,
          left: 10
        }}
        paddingAdjustmentBehavior={"always"}
        onRegionChangeComplete={this.onRegionChangeComplete}
        onPress={e => {
          if (e.nativeEvent.action === "marker-press") {
            // pressed a marker
          } else {
            // pressed the map
            this.setState({ selectedMarkerItemHash: null });
          }
        }}
      >
        {this.renderPolygons()}
        {this.renderMapData()}
        {this.renderSearchPointsCircle()}
        {this.renderSearchPointsMarker()}
      </MapView>
    );
  }

  renderListingsOverlay() {
    const { listingPOs, featuredAgents, listingsContainerY } = this.state;
    const { isTransacted } = this.props;
    return (
      <Animated.View
        style={{
          width: "100%",
          backgroundColor: "white",
          position: "absolute",
          bottom: listingsContainerY
        }}
      >
        <MapListingResults
          style={{ backgroundColor: "white" }}
          onLayout={({
            nativeEvent: {
              layout: { x, y, width, height }
            }
          }) => {
            this.setState({ listingContainerSize: { x, y, width, height } });
          }}
          listingPOs={listingPOs}
          featuredAgents={featuredAgents}
          isTransacted={isTransacted}
          onItemSelected={this.onCardItemPressed}
          onFeaturedAgentSelected={this.onFeaturedAgentSelected}
          onEndReached={this.onEndReached}
        />
      </Animated.View>
    );
  }

  render() {
    return (
      <View
        style={{ flex: 1, justifyContent: "flex-end", width: width }}
        onLayout={({
          nativeEvent: {
            layout: { x, y, width, height }
          }
        }) => {
          this.setState({ mapScreenSize: { width, height } });
        }}
      >
        {this.renderFilterButtonAndTransactedSwitch()}
        {this.renderMap()}
        {this.renderListingsOverlay()}
      </View>
    );
  }
}

export { MapSearch };
