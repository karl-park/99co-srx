const API_KEY = "AIzaSyDYdT8Hj03tfgt0Tj2xDQul-pfnuOIjAIo";

const request = function(url) {
  const onSuccess = function (response) {
    if (__DEV__) {
      console.log(response);
    }
    return response;
  };

  const onError = function (error) {
    if (error.response) {
      // Request was made but server responded with something
      // other than 2xx
      console.error("Status:", error.response.status);
      console.error("Data:", error.response.data);
      console.error("Headers:", error.response.headers);
    } else {
      // Something else happened while setting up the request
      // triggered the error
      console.error("Error Message:", error.message);
    }

    return Promise.reject(error.response || error.message);
  };

  return fetch(url)
    .then(response => response.json())
    .then(responseJson => onSuccess(responseJson))
    .catch(onError);
}

function getDirection({startLoc, destinationLoc, mode}) {
  /**
   * mode
   *  1. transit
   *  2. driving,
   *  3. walking
   *  4. bicycling
   */
  var url = `https://maps.googleapis.com/maps/api/directions/json?origin=${startLoc}&destination=${destinationLoc}&mode=${mode}&key=${API_KEY}`;

  if (mode === 'transit') {
    url += `&transit_routing_preference=less_walking`;
  }

  return request(url);
}

function getNearbySearch({ latitude, longitude, type, radius}) {
  //for type, refer to https://developers.google.com/places/web-service/supported_types

  var url = `https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${latitude},${longitude}&type=${type}&key=${API_KEY}`;

  if (radius) {
    url += `&radius=${radius}`;
  }

  return request(url);

}

const GoogleDirectionService = {
    getDirection,
    getNearbySearch
};

export { GoogleDirectionService };
