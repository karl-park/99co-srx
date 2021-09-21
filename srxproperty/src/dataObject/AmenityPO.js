import { CommonUtil, ObjectUtil } from "../utils";

class AmenityPO {
  constructor(data) {
    if (data) {
      this.cdAmenityId = data.cdAmenityId;
      this.distance = data.distance;
      this.id = data.id;
      this.latitude = data.latitude;
      this.longitude = data.longitude;
      this.name = data.name;

      //you may set this manually
      this.isUserLocation = false;

      this.drivingRoute = null;
      this.transitRoute = null;
      this.walkingRoute = null;
    }
  }

  getName() {
    return this.name;
  }

  getDistanceDesc() {
    return this.distance;
  }

  setDrivingRoute(routes) {
    this.drivingRoute = routes;
  }

  getDrivingRoute() {
    return this.drivingRoute;
  }

  setTransitRoute(routes) {
    this.transitRoute = routes;
  }

  getTransitRoute() {
    return this.transitRoute;
  }

  setWalkingRoute(routes) {
    this.walkingRoute = routes;
  }

  getWalkingRoute() {
    return this.walkingRoute;
  }
}

export { AmenityPO };
