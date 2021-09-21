//the object in the array, “key” should act as the "title", “value“ should be the "id"
const USER_TYPE_BUY = "1";
const USER_TYPE_TITLE_BUY = "Buy";

const USER_TYPE_BUY_NEW_PROJECTS = "1A";
const USER_TYPE_TITLE_BUY_NEW_PROJECTS = "Buy New Projects";

const USER_TYPE_SELL = "2";
const USER_TYPE_TITLE_SELL = "Sell";

const USER_TYPE_RENT_AS_TENANT = "3";
const USER_TYPE_TITLE_RENT_AS_TENANT = "Rent as Tenant";

const USER_TYPE_RENT_AS_LANDLORD = "4";
const USER_TYPE_TITLE_RENT_AS_LANDLORD = "Rent as Landlord";

export const UserTypeValue = {
  buy: USER_TYPE_BUY,
  buyNewProjects: USER_TYPE_BUY_NEW_PROJECTS,
  sell: USER_TYPE_SELL,
  rentAsTenant: USER_TYPE_RENT_AS_TENANT,
  rentAsLandlord: USER_TYPE_RENT_AS_LANDLORD
};

export const UserTypeDescription = {
  buy: USER_TYPE_TITLE_BUY,
  buyNewProjects: USER_TYPE_TITLE_BUY_NEW_PROJECTS,
  sell: USER_TYPE_TITLE_SELL,
  rentAsTenant: USER_TYPE_TITLE_RENT_AS_TENANT,
  rentAsLandlord: USER_TYPE_TITLE_RENT_AS_LANDLORD
};

export const USER_TYPE_ARRAY = [
  {
    key: USER_TYPE_TITLE_BUY,
    value: USER_TYPE_BUY
  },
  {
    key: USER_TYPE_TITLE_BUY_NEW_PROJECTS,
    value: USER_TYPE_BUY_NEW_PROJECTS
  },
  {
    key: USER_TYPE_TITLE_SELL,
    value: USER_TYPE_SELL
  },
  {
    key: USER_TYPE_TITLE_RENT_AS_TENANT,
    value: USER_TYPE_RENT_AS_TENANT
  },
  {
    key: USER_TYPE_TITLE_RENT_AS_LANDLORD,
    value: USER_TYPE_RENT_AS_LANDLORD
  }
];

export const getUserTypeDescription = userTypeKey => {
  for (let entry of Object.entries(UserTypeValue)) {
    const key = entry[0];
    const value = entry[1];
    if (value == userTypeKey) {
      return UserTypeDescription[key];
    }
  }
  return "";
};
