/**
 * Key strings of Amenities
 */
const AmenityKey_MRT = "MRT";
const AmenityKey_Bus = "Bus";
const AmenityKey_School = "Schools";
const AmenityKey_Retail = "Retail";
const AmenityKey_FoodAndBeverage = "FoodAndBeverage";
const AmenityKey_Hospital = "Hospital";
const AmenityKey_Worship = "Worship";
const AmenityKey_Others = "Others";

const AmenityTypes = {
  MRT: AmenityKey_MRT,
  Bus: AmenityKey_Bus,
  Schools: AmenityKey_School,
  Retail: AmenityKey_Retail,
  FoodAndBeverage: AmenityKey_FoodAndBeverage,
  Hospital: AmenityKey_Hospital,
  Worship: AmenityKey_Worship,
  Others: AmenityKey_Others,
};

const TravelTypes = {
  None: "None",
  Transit: "Transit", //Bus/mrt
  Drive: "Drive",
  Walk: "Walk"
};

const AmenitiesOptions = [
  {
    key: AmenityKey_Others,
    title: "Your Location"
  },
  {
    key: AmenityKey_MRT,
    title: "MRT"
  },
  {
    key: AmenityKey_Bus,
    title: "Bus"
  },
  {
    key: AmenityKey_School,
    title: "Schools"
  },
  {
    key: AmenityKey_Retail,
    title: "Retail"
  },
  {
    key: AmenityKey_FoodAndBeverage,
    title: "F&B"
  },
  {
    key: AmenityKey_Hospital,
    title: "Hospital"
  },
  {
    key: AmenityKey_Worship,
    title: "Places of Worship"
  }
];

/**
 * Value from servers
 */

const cdAmenity = {
  Trains: 15,
  BusStops: 1,
  PrimarySchools: 3,
  SecondarySchools: 4,
  ShoppingMalls: 36,
  Groceries: 14,
  Integrated: 17
};

//id to match with back-end
const AmenitiesCategories = {
  MRT: 1,
  Bus: 2,
  Schools: 3,
  Retail: 4,
  FoodAndBeverage: 5,
  Hospital: 6,
  Worship: 7
};

const AmenitiesSource = {
  ListingDetails: "ListingDetails",
  MyPropertyDetails: "MyPropertyDetails"
};

export {
  AmenitiesSource,
  cdAmenity,
  AmenityTypes,
  AmenitiesCategories,
  TravelTypes,
  AmenitiesOptions
};
