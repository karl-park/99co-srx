import React, { Component } from "react";
import { View, SafeAreaView, Image, Text, StyleSheet } from "react-native";
import PropTypes from "prop-types";
import MapView, {
  PROVIDER_GOOGLE,
  Marker,
  Circle,
  Overlay
} from "react-native-maps";
import Polyline from "@mapbox/polyline";
import {
  Amenity_Route_Start_Point,
  Amenity_Route_End_Point
} from "../../assets";
import { SRXColor } from "../../constants";
import { AmenitiesCategoryPO, AmenityPO } from "../../dataObject";
import { GoogleDirectionService } from "../../services";
import { NumberUtil, ObjectUtil, CommonUtil } from "../../utils";
import { AmenityOptions } from "./AmenityOptions";
import { AmenitiesCategories, AmenityTypes, TravelTypes } from "./constant";
import { Avatar } from "../../components";

const STATE_NOT_FETCH_YET = "Not_Fetch_Yet";
const STATE_LOADING = "loading";
const STATE_LOADED = "loaded";

const SolidLines = ({ coords }) => {
  return (
    <MapView.Polyline
      coordinates={coords}
      strokeWidth={4}
      strokeColor={SRXColor.Teal}
    />
  );
};

const DashedLines = ({ coords }) => {
  return (
    <MapView.Polyline
      coordinates={coords}
      strokeWidth={2}
      strokeColor={SRXColor.Teal}
      lineDashPattern={[5, 8]}
      lineCap={"butt"}
    />
  );
};

const RouteStartMarker = ({ coordinate }) => {
  return (
    <Marker coordinate={coordinate}>
      <Image
        style={{ width: 15, height: 15 }}
        source={Amenity_Route_Start_Point}
      />
    </Marker>
  );
};

const RouteEndMarker = ({ coordinate }) => {
  return (
    <Marker coordinate={coordinate}>
      <Image
        style={{ width: 15, height: 15 }}
        source={Amenity_Route_End_Point}
      />
    </Marker>
  );
};

class AmenitiesScreen extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: "Amenities"
        }
      },
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true
      }
    };
  }

  static defaultProps = {
    latitude: 1.29027,
    longitude: 103.851959,
    initialCategory: AmenityTypes.Others
  };

  state = {
    category: AmenityTypes.Others,
    selectedAmenity: null,
    travelMode: TravelTypes.Transit,
    trainAmenities: null,
    busStopAmenities: null,
    schoolAmenities: null,
    retailAmenities: null,
    foodBeverageAmenities: [],
    hospitalAmenities: [],
    worshipAmenities: [],
    otherAmenities: [],
    //use this to track the state of amenities getting from google places
    foodBeverageLoadingState: STATE_NOT_FETCH_YET,
    hospitalLoadingState: STATE_NOT_FETCH_YET,
    churchLoadingState: STATE_NOT_FETCH_YET,
    mosqueLoadingState: STATE_NOT_FETCH_YET
  };

  constructor(props) {
    super(props);
    this.handleAmenities = this.handleAmenities.bind(this);
    this.loadNearbyAmenitiesFromGooglePlaces = this.loadNearbyAmenitiesFromGooglePlaces.bind(
      this
    );
    this.updateGooglePlacesResult = this.updateGooglePlacesResult.bind(this);
    this.onErrorFromGooglePlace = this.onErrorFromGooglePlace.bind(this);
    this.loadRoute = this.loadRoute.bind(this);
    this.onTabChanged = this.onTabChanged.bind(this);
    this.onAmenitySelected = this.onAmenitySelected.bind(this);
    this.onAmenityUpdated = this.onAmenityUpdated.bind(this);
    this.onTravelModeSelected = this.onTravelModeSelected.bind(this);
    this.renderPropertyMarker = this.renderPropertyMarker.bind(this);
    this.renderRoutes = this.renderRoutes.bind(this);
    this.getRouteCoords = this.getRouteCoords.bind(this);
    this.renderStartPt = this.renderStartPt.bind(this);
    this.renderEndPt = this.renderEndPt.bind(this);
  }

  componentDidMount() {
    this.handleAmenities();

    const { initialCategory } = this.props;
    const { category } = this.state;
    if (initialCategory != category) {
      this.setState({ category: initialCategory });
    }
  }

  handleAmenities() {
    var trainAmenities = [];
    var busStopAmenities = [];
    var schoolAmenities = [];
    var retailAmenities = [];

    const { amenitiesGroups } = this.props;

    if (!ObjectUtil.isEmpty(amenitiesGroups)) {
      for (let i = 0; i < amenitiesGroups.length; i++) {
        const amenitiesCategory = amenitiesGroups[i];
        if (!ObjectUtil.isEmpty(amenitiesCategory)) {
          const { amenities, id } = amenitiesCategory;
          if (!ObjectUtil.isEmpty(amenities) && Array.isArray(amenities)) {
            switch (id) {
              case AmenitiesCategories.MRT:
                trainAmenities = [...amenities];
                break;
              case AmenitiesCategories.Bus:
                busStopAmenities = [...amenities];
                break;

              case AmenitiesCategories.Schools:
                schoolAmenities = [...amenities];
                break;

              case AmenitiesCategories.Retail:
                retailAmenities = [...amenities];
                break;

              default:
              // do nothing
            }
          }
        }
      }
    }

    this.setState({
      trainAmenities,
      busStopAmenities,
      schoolAmenities,
      retailAmenities
    });
  }

  loadNearbyAmenitiesFromGooglePlaces({ type }) {
    /**
     * Food & Beverage
     * 1. restaurant
     */

    /**
     * Hospital & Clinic
     *
     * 1. doctor
     * 2. hospital
     */

    /**
     * Places of Worship
     * 1. church
     * 2. mosque
     */

    const { latitude, longitude } = this.props;

    GoogleDirectionService.getNearbySearch({
      latitude: latitude,
      longitude: longitude,
      type,
      radius: "2000"
    })
      .then(response => {
        this.updateGooglePlacesResult({ response, type });
      })
      .catch(error => {
        this.onErrorFromGooglePlace({ error, type });
      });
  }

  updateGooglePlacesResult({ response, type }) {
    const { latitude, longitude } = this.props;
    const { worshipAmenities, category } = this.state;

    const { status, results } = response;
    if (status === "OK") {
      const amenityResult = [];
      results.map(item => {
        const { geometry, name, id } = item;
        const convertedAmenity = new AmenityPO();
        convertedAmenity.name = name;
        convertedAmenity.id = id;
        convertedAmenity.latitude = geometry.location.lat;
        convertedAmenity.longitude = geometry.location.lng;
        const distance = CommonUtil.getDistance({
          location1: { latitude, longitude },
          location2: {
            latitude: geometry.location.lat,
            longitude: geometry.location.lng
          }
        });
        convertedAmenity.distance = Math.round(distance * 100) / 100 + "km";
        amenityResult.push(convertedAmenity);
      });

      if (type == "restaurant") {
        let sortedFoodAmenities = this.getSortedAmenities([...amenityResult]);
        this.setState(
          {
            foodBeverageLoadingState: STATE_LOADED,
            foodBeverageAmenities: sortedFoodAmenities
          },
          () => {
            if (
              category === AmenityTypes.FoodAndBeverage &&
              !ObjectUtil.isEmpty(sortedFoodAmenities)
            ) {
              this.onAmenitySelected({
                amenity: sortedFoodAmenities[0],
                category
              });
            }
          }
        );
      } else if (type == "doctor" || type == "hospital") {
        let sortedHospitalAmenities = this.getSortedAmenities([
          ...amenityResult
        ]);
        this.setState(
          {
            hospitalLoadingState: STATE_LOADED,
            hospitalAmenities: sortedHospitalAmenities
          },
          () => {
            if (
              category === AmenityTypes.Hospital &&
              !ObjectUtil.isEmpty(sortedHospitalAmenities)
            ) {
              this.onAmenitySelected({
                amenity: sortedHospitalAmenities[0],
                category
              });
            }
          }
        );
      } else if (type == "church") {
        this.setState(
          {
            churchLoadingState: STATE_LOADED,
            mosqueLoadingState: STATE_LOADING,
            worshipAmenities: amenityResult
          },
          () => {
            this.loadNearbyAmenitiesFromGooglePlaces({ type: "mosque" });
          }
        );
      } else if (type == "mosque") {
        let sortedWorshipAmenities = this.getSortedAmenities([
          ...worshipAmenities,
          ...amenityResult
        ]);

        this.setState(
          {
            mosqueLoadingState: STATE_LOADED,
            worshipAmenities: sortedWorshipAmenities
          },
          () => {
            if (
              category === AmenityTypes.Worship &&
              !ObjectUtil.isEmpty(sortedWorshipAmenities)
            ) {
              this.onAmenitySelected({
                amenity: sortedWorshipAmenities[0],
                category
              });
            }
          }
        );
      }
    }
  }

  onErrorFromGooglePlace({ error, type }) {
    const { worshipAmenities } = this.state;

    if (type == "restaurant") {
      this.setState({
        foodBeverageLoadingState: STATE_LOADED,
        foodBeverageAmenities: []
      });
    } else if (type == "doctor" || type == "hospital") {
      this.setState({
        hospitalLoadingState: STATE_LOADED,
        hospitalAmenities: []
      });
    } else if (type == "church") {
      this.setState(
        { churchLoadingState: STATE_LOADED, worshipAmenities: [] },
        () => {
          this.loadNearbyAmenitiesFromGooglePlaces({ type: "mosque" });
        }
      );
    } else if (type == "mosque") {
      let sortedWorshipAmenities = this.getSortedAmenities([
        ...worshipAmenities
      ]);

      this.setState(
        {
          mosqueLoadingState: STATE_LOADED,
          worshipAmenities: sortedWorshipAmenities
        },
        () => {
          if (
            category === AmenityTypes.Worship &&
            !ObjectUtil.isEmpty(sortedWorshipAmenities)
          ) {
            this.onAmenitySelected({
              amenity: sortedWorshipAmenities[0],
              category
            });
          }
        }
      );
    }
  }

  onTabChanged(category) {
    const {
      trainAmenities,
      busStopAmenities,
      schoolAmenities,
      retailAmenities,
      foodBeverageAmenities,
      hospitalAmenities,
      worshipAmenities,
      foodBeverageLoadingState,
      hospitalLoadingState,
      churchLoadingState,
      mosqueLoadingState,
      otherAmenities,
      travelMode
    } = this.state;
    if (
      category == AmenityTypes.FoodAndBeverage &&
      foodBeverageLoadingState === STATE_NOT_FETCH_YET
    ) {
      this.setState(
        { category, foodBeverageLoadingState: STATE_LOADING },
        () => {
          this.loadNearbyAmenitiesFromGooglePlaces({ type: "restaurant" });
        }
      );
    } else if (
      category == AmenityTypes.Hospital &&
      hospitalLoadingState === STATE_NOT_FETCH_YET
    ) {
      this.setState({ category, hospitalLoadingState: STATE_LOADING }, () => {
        this.loadNearbyAmenitiesFromGooglePlaces({ type: "doctor" });
      });
    } else if (
      category == AmenityTypes.Worship &&
      (churchLoadingState === STATE_NOT_FETCH_YET ||
        mosqueLoadingState === STATE_NOT_FETCH_YET)
    ) {
      /**
       * 1st load church, then load mosque
       * the result will not be shown until both loaded
       */
      this.setState({ category, churchLoadingState: STATE_LOADING }, () => {
        this.loadNearbyAmenitiesFromGooglePlaces({ type: "church" });
      });
    } else {
      let amenitySelectOnDefault, amenitiesOfCategory;

      if (category === AmenityTypes.MRT) {
        amenitiesOfCategory = trainAmenities;
      } else if (category === AmenityTypes.Bus) {
        amenitiesOfCategory = busStopAmenities;
      } else if (category === AmenityTypes.Schools) {
        amenitiesOfCategory = schoolAmenities;
      } else if (category === AmenityTypes.Retail) {
        amenitiesOfCategory = retailAmenities;
      } else if (category === AmenityTypes.FoodAndBeverage) {
        amenitiesOfCategory = foodBeverageAmenities;
      } else if (category === AmenityTypes.Hospital) {
        amenitiesOfCategory = hospitalAmenities;
      } else if (category === AmenityTypes.Worship) {
        amenitiesOfCategory = worshipAmenities;
      } else if (category === AmenityTypes.Others) {
        amenitiesOfCategory = otherAmenities;
      }

      if (!ObjectUtil.isEmpty(amenitiesOfCategory)) {
        amenitySelectOnDefault = amenitiesOfCategory[0];
      }

      let travelModeOnDefault = travelMode;
      if (travelMode === TravelTypes.None) {
        travelModeOnDefault = TravelTypes.Transit;
      }
      console.log(category);
      this.setState({ category, travelMode: travelModeOnDefault }, () => {
        this.onAmenitySelected({ category, amenity: amenitySelectOnDefault });
      });
    }
  }

  onAmenitySelected({ amenity, category }) {
    this.setState({ selectedAmenity: amenity }, () => {
      if (!ObjectUtil.isEmpty(amenity)) {
        if (ObjectUtil.isEmpty(amenity.getTransitRoute())) {
          this.loadRoute({ amenity, category, mode: TravelTypes.Transit });
        }
        if (ObjectUtil.isEmpty(amenity.getDrivingRoute())) {
          this.loadRoute({ amenity, category, mode: TravelTypes.Drive });
        }
        if (ObjectUtil.isEmpty(amenity.getWalkingRoute())) {
          this.loadRoute({ amenity, category, mode: TravelTypes.Walk });
        }
      }
    });
  }

  loadRoute({ amenity, category, mode }) {
    const { latitude, longitude, showFuzzyLocation } = this.props;
    const {
      trainAmenities,
      busStopAmenities,
      schoolAmenities,
      retailAmenities,
      foodBeverageAmenities,
      hospitalAmenities,
      worshipAmenities,
      otherAmenities
    } = this.state;

    const startLoc = latitude + "," + longitude;
    const destinationLoc = amenity.latitude + "," + amenity.longitude;

    var travelMode;
    if (mode == TravelTypes.Transit) {
      travelMode = "transit";
    } else if (mode == TravelTypes.Walk) {
      travelMode = "walking";
    } else {
      //driving
      travelMode = "driving";
    }

    GoogleDirectionService.getDirection({
      startLoc,
      destinationLoc,
      mode: travelMode
    })
      .then(resp => {
        if (!ObjectUtil.isEmpty(resp)) {
          const { status, routes } = resp;
          if (status === "OK") {
            //update object
            if (mode == TravelTypes.Transit) {
              amenity.setTransitRoute(routes[0]);
            } else if (mode == TravelTypes.Walk) {
              amenity.setWalkingRoute(routes[0]);
            } else {
              //driving
              amenity.setDrivingRoute(routes[0]);
            }

            //update array in state to trigger screen render
            switch (category) {
              case AmenityTypes.MRT:
                this.setState({ trainAmenities: [...trainAmenities] });
                break;

              case AmenityTypes.Bus:
                this.setState({ busStopAmenities: [...busStopAmenities] });
                break;

              case AmenityTypes.Schools:
                this.setState({ schoolAmenities: [...schoolAmenities] });
                break;

              case AmenityTypes.Retail:
                this.setState({ retailAmenities: [...retailAmenities] });
                break;

              case AmenityTypes.FoodAndBeverage:
                this.setState({
                  foodBeverageAmenities: [...foodBeverageAmenities]
                });
                break;

              case AmenityTypes.Hospital:
                this.setState({
                  hospitalAmenities: [...hospitalAmenities]
                });
                break;

              case AmenityTypes.Worship:
                this.setState({
                  worshipAmenities: [...worshipAmenities]
                });
                break;

              case AmenityTypes.Others:
                this.setState({
                  otherAmenities: [...otherAmenities]
                });
                break;

              default:
                break;
            }
          }
        }
      })
      .catch(err => {
        console.log(err);
      });
  }

  onTravelModeSelected({ travelMode, amenity }) {
    this.setState({ travelMode, selectedAmenity: amenity });
  }

  //for amenity custom location
  onAmenityUpdated({ amenity }) {
    this.setState({ otherAmenities: [amenity] }, () => {
      const { category } = this.state;
      console.log(category);
      if (category === AmenityTypes.Others) {
        this.onAmenitySelected({ amenity, category: AmenityTypes.Others });
      }
    });
  }

  renderAmenityOptions() {
    const {
      trainAmenities,
      busStopAmenities,
      schoolAmenities,
      retailAmenities,
      foodBeverageAmenities,
      hospitalAmenities,
      worshipAmenities,
      otherAmenities,
      selectedAmenity,
      travelMode,
      category
    } = this.state;
    const { amenitiesOptions, nearbyUsers } = this.props;
    var source;
    if (nearbyUsers) {
      source = AmenityOptions.Sources.MyPropertyDetails;
    } else {
      source = AmenityOptions.Sources.ListingDetails;
    }
    return (
      <AmenityOptions
        trainAmenities={trainAmenities}
        busStopAmenities={busStopAmenities}
        schoolAmenities={schoolAmenities}
        retailAmenities={retailAmenities}
        foodBeverageAmenities={foodBeverageAmenities}
        hospitalAmenities={hospitalAmenities}
        worshipAmenities={worshipAmenities}
        otherAmenities={otherAmenities}
        selectedAmenity={selectedAmenity}
        selectedTravelMethod={travelMode}
        onTabChanged={this.onTabChanged}
        onAmenitySelected={this.onAmenitySelected}
        onTravelModeSelected={this.onTravelModeSelected}
        onAmenityUpdated={this.onAmenityUpdated}
        category={category}
        amenitiesOptions={amenitiesOptions}
        source={source}
        nearbyUsers={nearbyUsers}
      />
    );
  }

  getSortedAmenities(arrayToSort) {
    if (!ObjectUtil.isEmpty(arrayToSort) && Array.isArray(arrayToSort)) {
      arrayToSort.sort(function(item1, item2) {
        var distance1 = item1.distance.toUpperCase(); // ignore upper and lowercase
        var distance2 = item2.distance.toUpperCase(); // ignore upper and lowercase
        if (distance1 < distance2) {
          return -1;
        }
        if (distance1 > distance2) {
          return 1;
        }

        // distance must be equal
        return 0;
      });

      return arrayToSort;
    }
    return [];
  }

  renderStep({ step }) {
    if (!ObjectUtil.isEmpty(step)) {
      const { travel_mode, polyline } = step;
      let points = Polyline.decode(polyline.points);
      let coords = points.map((point, index) => {
        return {
          latitude: point[0],
          longitude: point[1]
        };
      });

      if (travel_mode === "WALKING") {
        return <DashedLines coords={coords} />;
      } else {
        return <SolidLines coords={coords} />;
      }
    }
  }

  getRouteCoords() {
    const { selectedAmenity, travelMode } = this.state;

    if (!ObjectUtil.isEmpty(selectedAmenity)) {
      var route;
      if (!ObjectUtil.isEmpty(travelMode)) {
        if (travelMode === TravelTypes.Transit) {
          route = selectedAmenity.getTransitRoute();
        } else if (travelMode === TravelTypes.Drive) {
          route = selectedAmenity.getDrivingRoute();
        } else if (travelMode === TravelTypes.Walk) {
          route = selectedAmenity.getWalkingRoute();
        }
      }

      if (!ObjectUtil.isEmpty(route)) {
        let points = Polyline.decode(route.overview_polyline.points);
        let coords = points.map((point, index) => {
          return {
            latitude: point[0],
            longitude: point[1]
          };
        });

        return coords;
      }
    }
    return null;
  }

  renderRoutes() {
    const routeCoords = this.getRouteCoords();

    if (!ObjectUtil.isEmpty(routeCoords)) {
      return <SolidLines coords={routeCoords} />;
    }
  }

  renderStartPt() {
    const routeCoords = this.getRouteCoords();

    if (!ObjectUtil.isEmpty(routeCoords)) {
      const startPtCoord = routeCoords[0];

      return <RouteStartMarker coordinate={startPtCoord} />;
    }
  }

  renderEndPt() {
    const routeCoords = this.getRouteCoords();

    if (!ObjectUtil.isEmpty(routeCoords)) {
      const endPtCoord = routeCoords[routeCoords.length - 1];

      return <RouteEndMarker coordinate={endPtCoord} />;
    }
  }

  renderPropertyMarker() {
    const {
      latitude,
      longitude,
      showFuzzyLocation,
      locationTitle
    } = this.props;
    let lat_Num = NumberUtil.floatValue(latitude);
    let long_Num = NumberUtil.floatValue(longitude);
    console.log(locationTitle);
    if (showFuzzyLocation) {
      return (
        <Circle
          center={{
            latitude: lat_Num,
            longitude: long_Num
          }}
          radius={60}
          strokeColor={SRXColor.Teal}
          fillColor={SRXColor.Teal + "7E"} //#rrggbbaa, 7E ==> 50% transparency
        />
      );
    } else {
      return (
        <Marker
          title={locationTitle}
          coordinate={{
            latitude: lat_Num,
            longitude: long_Num
          }}
        />
      );
    }
  }

  renderSelectedAmenityMarker() {
    const { selectedAmenity } = this.state;
    const { latitude, longitude } = selectedAmenity;

    let lat_Num = NumberUtil.floatValue(latitude);
    let long_Num = NumberUtil.floatValue(longitude);

    if (!selectedAmenity.isUserLocation) {
      return (
        <Marker
          coordinate={{
            latitude: lat_Num,
            longitude: long_Num
          }}
          pinColor={SRXColor.Teal}
          title={selectedAmenity.name}
        />
      );
    }
  }

  renderMap() {
    const { latitude, longitude, style } = this.props;
    const { travelMode, selectedAmenity } = this.state;

    let lat_Num = NumberUtil.floatValue(latitude);
    let long_Num = NumberUtil.floatValue(longitude);
    let lat_Delta = 0.002;
    let long_Delta = 0.002;

    const initialRegion = {
      latitude: lat_Num,
      longitude: long_Num,
      latitudeDelta: lat_Delta,
      longitudeDelta: long_Delta
    };

    if (
      !ObjectUtil.isEmpty(selectedAmenity) &&
      selectedAmenity instanceof AmenityPO
    ) {
      var route;
      if (!ObjectUtil.isEmpty(travelMode)) {
        if (travelMode === TravelTypes.Transit) {
          route = selectedAmenity.getTransitRoute();
        } else if (travelMode === TravelTypes.Drive) {
          route = selectedAmenity.getDrivingRoute();
        } else if (travelMode === TravelTypes.Walk) {
          route = selectedAmenity.getWalkingRoute();
        }
      }

      if (!ObjectUtil.isEmpty(route)) {
        const { bounds, legs } = route;
        if (!ObjectUtil.isEmpty(bounds)) {
          const { northeast, southwest } = bounds;

          if (
            !ObjectUtil.isEmpty(northeast) &&
            !ObjectUtil.isEmpty(southwest)
          ) {
            lat_Num = (northeast.lat + southwest.lat) / 2;
            long_Num = (northeast.lng + southwest.lng) / 2;
            lat_Delta = northeast.lat - southwest.lat + 0.002;
            long_Delta = northeast.lng - southwest.lng + 0.002;
          }
        }

        var steps;
        // if (!ObjectUtil.isEmpty(legs)) {
        //   const leg = legs[0];
        //   if (!ObjectUtil.isEmpty(leg) && !ObjectUtil.isEmpty(leg.steps)) {
        //     steps = leg.steps;
        //   }
        // }

        return (
          <MapView
            showsUserLocation
            ref={component => (this.map = component)}
            provider={PROVIDER_GOOGLE}
            mapPadding={{
              top: 80,
              right: 10,
              bottom: 10,
              left: 10
            }}
            style={{
              flex: 1,
              // position: "absolute",
              width: "100%",
              height: "50%"
            }}
            initialRegion={initialRegion}
            region={{
              latitude: lat_Num,
              longitude: long_Num,
              latitudeDelta: lat_Delta,
              longitudeDelta: long_Delta
            }}
          >
            {this.renderPropertyMarker()}
            {this.renderSelectedAmenityMarker()}
            {/* {!ObjectUtil.isEmpty(steps)
              ? steps.map(step => this.renderStep({ step }))
              : this.renderRoutes()} */}
            {this.renderRoutes()}
            {this.renderStartPt()}
            {this.renderEndPt()}
          </MapView>
        );
      } else {
        const lat_selected_Num = NumberUtil.floatValue(
          selectedAmenity.latitude
        );
        const lng_selected_Num = NumberUtil.floatValue(
          selectedAmenity.longitude
        );
        const lat_Property_Num = NumberUtil.floatValue(latitude);
        const lng_Property_Num = NumberUtil.floatValue(longitude);

        lat_Num = (lat_selected_Num + lat_Property_Num) / 2;
        long_Num = (lng_selected_Num + lng_Property_Num) / 2;
        lat_Delta = Math.abs(lat_selected_Num - lat_Property_Num) + 0.002;
        long_Delta = Math.abs(lng_selected_Num - lng_Property_Num) + 0.002;

        return (
          <MapView
            showsUserLocation
            ref={component => (this.map = component)}
            provider={PROVIDER_GOOGLE}
            mapPadding={{
              top: 80,
              right: 10,
              bottom: 10,
              left: 10
            }}
            style={{
              flex: 1,
              // position: "absolute",
              width: "100%",
              height: "50%"
            }}
            initialRegion={initialRegion}
            region={{
              latitude: lat_Num,
              longitude: long_Num,
              latitudeDelta: lat_Delta,
              longitudeDelta: long_Delta
            }}
            showsPointsOfInterest={false}
          >
            {this.renderPropertyMarker()}
            {this.renderSelectedAmenityMarker()}
          </MapView>
        );
      }
    } else {
      return (
        <MapView
          showsUserLocation
          provider={PROVIDER_GOOGLE}
          mapPadding={{
            top: 52,
            right: 10,
            bottom: 10,
            left: 10
          }}
          style={{
            flex: 1,
            // position: "absolute",
            width: "100%",
            height: "50%"
          }}
          initialRegion={initialRegion}
          region={{
            latitude: lat_Num,
            longitude: long_Num,
            latitudeDelta: lat_Delta,
            longitudeDelta: lat_Delta
          }}
        >
          {this.renderPropertyMarker()}
          {this.renderNearByOwner()}
        </MapView>
      );
    }
  }

  renderNearByOwner() {
    const { nearbyUsers } = this.props;
    if (!ObjectUtil.isEmpty(nearbyUsers)) {
      let i=0;
      let output = nearbyUsers.map((item, index) => (
        <Marker
          coordinate={{
            latitude: item.latitude,
            longitude: item.longitude
          }}
          zIndex={i++}
        >
          <Avatar size={24} name={item.name} borderLess={true} />
          {/* <View
            style={{
              justifyContent: "center",
              alignSelf: "center",
              alignItems: "center",
              width: 20,
              height: 20,
              backgroundColor: "#F0E68C",
              borderRadius: 10,
              borderWidth:1
            }}
          >
            <Text>{item.name}</Text>
          </View> */}
        </Marker>
      ));
      return output;
    }
  }

  render() {
    const { style } = this.props;

    return (
      <SafeAreaView style={{ flex: 1 }}>
        <View
          style={{
            flex: 1,
            flexDirection: "column",
            justifyContent: "space-between"
          }}
        >
          {this.renderMap()}
          {this.renderAmenityOptions()}
        </View>
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  userInitialNameCirlce: {
    justifyContent: "center",
    alignSelf: "center",
    alignItems: "center",
    width: 20,
    height: 20,
    backgroundColor: "#F0E68C",
    borderRadius: 10,
    borderWidth: 1
  }
});

AmenitiesScreen.propTypes = {
  locationTitle: PropTypes.string,
  latitude: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  longitude: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  /**
   * indicate to show fuzzy location
   * instead of an accurate location of the property (to prevent indicating the exact landed property)
   */
  showFuzzyLocation: PropTypes.bool,
  amenitiesGroups: PropTypes.arrayOf(PropTypes.instanceOf(AmenitiesCategoryPO)),
  amenitiesOptions: PropTypes.arrayOf(PropTypes.object),
  /**
   * Optional
   *
   */
  nearbyUsers: PropTypes.arrayOf(PropTypes.object)
};

export { AmenitiesScreen };
