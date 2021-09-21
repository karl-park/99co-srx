//the object in the array, “key” should act as the "title", “value“ should be the "id"
const SORT_OPTION_DEFAULT = "default";
const SORT_OPTION_TITLE_DEFAULT = "Relevance";

const SORT_OPTION_DATE_NEW_TO_OLD = "datePostedDesc";
const SORT_OPTION_TITLE_DATE_NEW_TO_OLD = "Date (new to old)";

const SORT_OPTION_DATE_OLD_TO_NEW = "datePostedAsc";
const SORT_OPTION_TITLE_DATE_OLD_TO_NEW = "Date (old to new)";

const SORT_OPTION_PRICE_LOW_TO_HIGH = "priceAsc";
const SORT_OPTION_TITLE_PRICE_LOW_TO_HIGH = "Price (low to high)";

const SORT_OPTION_PRICE_HIGH_TO_LOW = "priceDesc";
const SORT_OPTION_TITLE_PRICE_HIGH_TO_LOW = "Price (high to low)";

const SORT_OPTION_SIZE_LOW_TO_HIGH = "sizeAsc";
const SORT_OPTION_TITLE_SIZE_LOW_TO_HIGH = "Size (low to high)";

const SORT_OPTION_SIZE_HIGH_TO_LOW = "sizeDesc";
const SORT_OPTION_TITLE_SIZE_HIGH_TO_LOW = "Size (high to low)";

const SORT_OPTION_LISTING_PSF_ASC = "psfAsc";
const SORT_OPTION_TITLE_LISTING_PSF_ASC = "PSF (low to high)";

const SORT_OPTION_LISTING_PSF_DESC = "psfDesc";
const SORT_OPTION_TITLE_LISTING_PSF_DESC = "PSF (high to low)";

const SORT_OPTION_BEDROOM_ASC = "numBedroomsAsc";
const SORT_OPTION_TITLE_BEDROOM_ASC = "Bedrooms (least to most)";

const SORT_OPTION_BEDROOM_DESC = "numBedroomsDesc";
const SORT_OPTION_TITLE_BEDROOM_DESC = "Bedrooms (most to least)";

const SORT_OPTION_DISTANCE_ASC = "distanceAsc";
const SORT_OPTION_TITLE_DISTANCE_ASC = "Distance (near to far)";

const SORT_OPTION_DISTANCE_DESC = "distanceDesc";
const SORT_OPTION_TITLE_DISTANCE_DESC = "Distance (far to near)";

export const sortOption = {
  default: SORT_OPTION_DEFAULT,
  date_new_to_old: SORT_OPTION_DATE_NEW_TO_OLD,
  date_old_to_new: SORT_OPTION_DATE_OLD_TO_NEW,
  price_low_to_high: SORT_OPTION_PRICE_LOW_TO_HIGH,
  price_high_to_low: SORT_OPTION_PRICE_HIGH_TO_LOW,
  size_low_to_high: SORT_OPTION_SIZE_LOW_TO_HIGH,
  size_high_to_low: SORT_OPTION_SIZE_HIGH_TO_LOW,
  psf_low_to_high: SORT_OPTION_LISTING_PSF_ASC,
  psf_high_to_low: SORT_OPTION_LISTING_PSF_DESC,
  bedroom_least_to_most: SORT_OPTION_BEDROOM_ASC,
  bedroom_most_to_least: SORT_OPTION_BEDROOM_DESC,
  distance_near_to_far: SORT_OPTION_DISTANCE_ASC,
  distance_far_to_near: SORT_OPTION_DISTANCE_DESC
};

export const sortOptionDescription = {
  default: SORT_OPTION_TITLE_DEFAULT,
  date_new_to_old: SORT_OPTION_TITLE_DATE_NEW_TO_OLD,
  date_old_to_new: SORT_OPTION_TITLE_DATE_OLD_TO_NEW,
  price_low_to_high: SORT_OPTION_TITLE_PRICE_LOW_TO_HIGH,
  price_high_to_low: SORT_OPTION_TITLE_PRICE_HIGH_TO_LOW,
  size_low_to_high: SORT_OPTION_TITLE_SIZE_LOW_TO_HIGH,
  size_high_to_low: SORT_OPTION_TITLE_SIZE_HIGH_TO_LOW,
  psf_low_to_high: SORT_OPTION_TITLE_LISTING_PSF_ASC,
  psf_high_to_low: SORT_OPTION_TITLE_LISTING_PSF_DESC,
  bedroom_least_to_most: SORT_OPTION_TITLE_BEDROOM_ASC,
  bedroom_most_to_least: SORT_OPTION_TITLE_BEDROOM_DESC,
  distance_near_to_far: SORT_OPTION_TITLE_DISTANCE_ASC,
  distance_far_to_near: SORT_OPTION_TITLE_DISTANCE_DESC
};

export const SORT_OPTION_ARRAY = [
  {
    key: SORT_OPTION_TITLE_DEFAULT,
    value: SORT_OPTION_DEFAULT
  },
  {
    key: SORT_OPTION_TITLE_DATE_NEW_TO_OLD,
    value: SORT_OPTION_DATE_NEW_TO_OLD
  },
  {
    key: SORT_OPTION_TITLE_DATE_OLD_TO_NEW,
    value: SORT_OPTION_DATE_OLD_TO_NEW
  },
  {
    key: SORT_OPTION_TITLE_PRICE_LOW_TO_HIGH,
    value: SORT_OPTION_PRICE_LOW_TO_HIGH
  },
  {
    key: SORT_OPTION_TITLE_PRICE_HIGH_TO_LOW,
    value: SORT_OPTION_PRICE_HIGH_TO_LOW
  },
  {
    key: SORT_OPTION_TITLE_SIZE_LOW_TO_HIGH,
    value: SORT_OPTION_SIZE_LOW_TO_HIGH
  },
  {
    key: SORT_OPTION_TITLE_SIZE_HIGH_TO_LOW,
    value: SORT_OPTION_SIZE_HIGH_TO_LOW
  },
  {
    key: SORT_OPTION_TITLE_LISTING_PSF_ASC,
    value: SORT_OPTION_LISTING_PSF_ASC
  },
  {
    key: SORT_OPTION_TITLE_LISTING_PSF_DESC,
    value: SORT_OPTION_LISTING_PSF_DESC
  },
  {
    key: SORT_OPTION_TITLE_BEDROOM_ASC,
    value: SORT_OPTION_BEDROOM_ASC
  },
  {
    key: SORT_OPTION_TITLE_BEDROOM_DESC,
    value: SORT_OPTION_BEDROOM_DESC
  }
];

export const SORT_OPTION_SEARCH_LOCATION_ARRAY = [
  {
    key: SORT_OPTION_TITLE_DEFAULT,
    value: SORT_OPTION_DEFAULT
  },
  {
    key: SORT_OPTION_TITLE_DATE_NEW_TO_OLD,
    value: SORT_OPTION_DATE_NEW_TO_OLD
  },
  {
    key: SORT_OPTION_TITLE_DATE_OLD_TO_NEW,
    value: SORT_OPTION_DATE_OLD_TO_NEW
  },
  {
    key: SORT_OPTION_TITLE_DISTANCE_ASC,
    value: SORT_OPTION_DISTANCE_ASC
  },
  {
    key: SORT_OPTION_TITLE_DISTANCE_DESC,
    value: SORT_OPTION_DISTANCE_DESC
  },
  {
    key: SORT_OPTION_TITLE_PRICE_LOW_TO_HIGH,
    value: SORT_OPTION_PRICE_LOW_TO_HIGH
  },
  {
    key: SORT_OPTION_TITLE_PRICE_HIGH_TO_LOW,
    value: SORT_OPTION_PRICE_HIGH_TO_LOW
  },
  {
    key: SORT_OPTION_TITLE_SIZE_LOW_TO_HIGH,
    value: SORT_OPTION_SIZE_LOW_TO_HIGH
  },
  {
    key: SORT_OPTION_TITLE_SIZE_HIGH_TO_LOW,
    value: SORT_OPTION_SIZE_HIGH_TO_LOW
  },
  {
    key: SORT_OPTION_TITLE_LISTING_PSF_ASC,
    value: SORT_OPTION_LISTING_PSF_ASC
  },
  {
    key: SORT_OPTION_TITLE_LISTING_PSF_DESC,
    value: SORT_OPTION_LISTING_PSF_DESC
  },
  {
    key: SORT_OPTION_TITLE_BEDROOM_ASC,
    value: SORT_OPTION_BEDROOM_ASC
  },
  {
    key: SORT_OPTION_TITLE_BEDROOM_DESC,
    value: SORT_OPTION_BEDROOM_DESC
  }
];
