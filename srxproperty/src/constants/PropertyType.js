const SUBTYPE_HDB_3_ROOMS = 1;
const SUBTYPE_TITLE_HDB_3_ROOMS = '3 Rooms';

const SUBTYPE_HDB_4_ROOMS = 2;
const SUBTYPE_TITLE_HDB_4_ROOMS = '4 Rooms';

const SUBTYPE_HDB_5_ROOMS = 3;
const SUBTYPE_TITLE_HDB_5_ROOMS = '5 Rooms';

const SUBTYPE_HDB_EXECUTIVE = 4;
const SUBTYPE_TITLE_HDB_EXECUTIVE = 'Executive';

const SUBTYPE_TERRACE = 5;
const SUBTYPE_TITLE_TERRACE = 'Terrace';

const SUBTYPE_CONDOMINIUM = 6;
const SUBTYPE_TITLE_CONDOMINIUM = 'Condo';

const SUBTYPE_APARTMENT = 7;
const SUBTYPE_TITLE_APARTMENT = 'Apartment';

const SUBTYPE_SEMI_DETACHED = 8;
const SUBTYPE_TITLE_SEMI_DETACHED = 'Semi-Detached';

const SUBTYPE_DETACHED = 10;
const SUBTYPE_TITLE_DETACHED = 'Detached';

const SUBTYPE_OFFICE = 11;
const SUBTYPE_TITLE_OFFICE = 'Office';

const SUBTYPE_RETAIL = 12;
const SUBTYPE_TITLE_RETAIL = 'Retail';

const SUBTYPE_SHOP_HOUSE = 13;
const SUBTYPE_TITLE_SHOP_HOUSE = 'Shop House';

const SUBTYPE_WAREHOUSE = 14;
const SUBTYPE_TITLE_WAREHOUSE = 'Warehouse';

const SUBTYPE_FACTORY = 15;
const SUBTYPE_TITLE_FACTORY = 'Factory';

const SUBTYPE_HDB_1_ROOM = 16;
const SUBTYPE_TITLE_HDB_1_ROOM = '1 Room';

const SUBTYPE_HDB_2_ROOMS = 17;
const SUBTYPE_TITLE_HDB_2_ROOMS = '2 Rooms';

const SUBTYPE_HBD_HUDC = 18;
const SUBTYPE_TITLE_HBD_HUDC = 'HUDC';

const SUBTYPE_HBD_JUMBO = 19;
const SUBTYPE_TITLE_HBD_JUMBO = 'Jumbo';

const SUBTYPE_HDB_SHOPHOUSE = 20;
const SUBTYPE_TITLE_HDB_SHOPHOUSE = 'HDB Shophouse';

const SUBTYPE_LAND = 21;
const SUBTYPE_TITLE_LAND = 'Land';

/*
 * Property Type
 */
const TYPE_HDB = 1;
const TYPE_TITLE_HDB = 'HDB';

const TYPE_CONDO = 2;
const TYPE_TITLE_CONDO = 'Condo / Apt';

const TYPE_LANDED = 3;
const TYPE_TITLE_LANDED = 'Landed';

const TYPE_COMMERCIAL = 4;
const TYPE_TITLE_COMMERCIAL = 'Commercial';

/*
 * Subtype arrays
 */

export const CONDO_APT_ARRAY = [
  {
    key: SUBTYPE_TITLE_CONDOMINIUM,
    value: SUBTYPE_CONDOMINIUM,
  },
  {
    key: SUBTYPE_TITLE_APARTMENT,
    value: SUBTYPE_APARTMENT,
  },
];

export const HDB_ARRAY = [
  {
    key: SUBTYPE_TITLE_HDB_1_ROOM,
    value: SUBTYPE_HDB_1_ROOM,
  },
  {
    key: SUBTYPE_TITLE_HDB_2_ROOMS,
    value: SUBTYPE_HDB_2_ROOMS,
  },
  {
    key: SUBTYPE_TITLE_HDB_3_ROOMS,
    value: SUBTYPE_HDB_3_ROOMS,
  },
  {
    key: SUBTYPE_TITLE_HDB_4_ROOMS,
    value: SUBTYPE_HDB_4_ROOMS,
  },
  {
    key: SUBTYPE_TITLE_HDB_5_ROOMS,
    value: SUBTYPE_HDB_5_ROOMS,
  },
  {
    key: SUBTYPE_TITLE_HDB_EXECUTIVE,
    value: SUBTYPE_HDB_EXECUTIVE,
  },
  {
    key: SUBTYPE_TITLE_HBD_HUDC,
    value: SUBTYPE_HBD_HUDC,
  },
  {
    key: SUBTYPE_TITLE_HBD_JUMBO,
    value: SUBTYPE_HBD_JUMBO,
  },
];

export const LANDED_ARRAY = [
  {
    key: SUBTYPE_TITLE_TERRACE,
    value: SUBTYPE_TERRACE,
  },
  {
    key: SUBTYPE_TITLE_SEMI_DETACHED,
    value: SUBTYPE_SEMI_DETACHED,
  },
  {
    key: SUBTYPE_TITLE_DETACHED,
    value: SUBTYPE_DETACHED,
  },
];

export const COMMERCIAL_ARRAY = [
  {
    key: SUBTYPE_TITLE_OFFICE,
    value: SUBTYPE_OFFICE,
  },
  {
    key: SUBTYPE_TITLE_RETAIL,
    value: SUBTYPE_RETAIL,
  },
  {
    key: SUBTYPE_TITLE_SHOP_HOUSE,
    value: SUBTYPE_SHOP_HOUSE,
  },
  {
    key: SUBTYPE_TITLE_WAREHOUSE,
    value: SUBTYPE_WAREHOUSE,
  },
  {
    key: SUBTYPE_TITLE_FACTORY,
    value: SUBTYPE_FACTORY,
  },
  {
    key: SUBTYPE_TITLE_HDB_SHOPHOUSE,
    value: SUBTYPE_HDB_SHOPHOUSE,
  },
  {
    key: SUBTYPE_TITLE_LAND,
    value: SUBTYPE_LAND,
  },
];

export const ALL_SUBTYPE_ARRAY = [
  ...CONDO_APT_ARRAY,
  ...LANDED_ARRAY,
  ...HDB_ARRAY,
  ...COMMERCIAL_ARRAY,
];

export const Type = {
  HDB: TYPE_HDB,
  Condo: TYPE_CONDO,
  Landed: TYPE_LANDED,
  Commercial: TYPE_COMMERCIAL,
};

export const TypeDescription = {
  HDB: TYPE_TITLE_HDB,
  Condo: TYPE_TITLE_CONDO,
  Landed: TYPE_TITLE_LANDED,
  Commercial: TYPE_TITLE_COMMERCIAL,
};

export const SubTypes = {
  Condominium: SUBTYPE_CONDOMINIUM,
  Apartment: SUBTYPE_APARTMENT,
  Terrace: SUBTYPE_TERRACE,
  SemiDetached: SUBTYPE_SEMI_DETACHED,
  Detached: SUBTYPE_DETACHED,
  HDB_1Room: SUBTYPE_HDB_1_ROOM,
  HDB_2Rooms: SUBTYPE_HDB_2_ROOMS,
  HDB_3Rooms: SUBTYPE_HDB_3_ROOMS,
  HDB_4Rooms: SUBTYPE_HDB_4_ROOMS,
  HDB_5Rooms: SUBTYPE_HDB_5_ROOMS,
  HDB_Executive: SUBTYPE_HDB_EXECUTIVE,
  HDB_HUDC: SUBTYPE_HBD_HUDC,
  HDB_Jumbo: SUBTYPE_HBD_JUMBO,
  Office: SUBTYPE_OFFICE,
  Retail: SUBTYPE_RETAIL,
  Shophouse: SUBTYPE_SHOP_HOUSE,
  Warehouse: SUBTYPE_WAREHOUSE,
  Factory: SUBTYPE_FACTORY,
  HDB_Shophouse: SUBTYPE_HDB_SHOPHOUSE,
  Land: SUBTYPE_LAND,
};

export const SubTypesDescription = {
  Condominium: SUBTYPE_TITLE_CONDOMINIUM,
  Apartment: SUBTYPE_TITLE_APARTMENT,
  Terrace: SUBTYPE_TITLE_TERRACE,
  SemiDetached: SUBTYPE_TITLE_SEMI_DETACHED,
  Detached: SUBTYPE_TITLE_DETACHED,
  HDB_1Room: SUBTYPE_TITLE_HDB_1_ROOM,
  HDB_2Rooms: SUBTYPE_TITLE_HDB_2_ROOMS,
  HDB_3Rooms: SUBTYPE_TITLE_HDB_3_ROOMS,
  HDB_4Rooms: SUBTYPE_TITLE_HDB_4_ROOMS,
  HDB_5Rooms: SUBTYPE_TITLE_HDB_5_ROOMS,
  HDB_Executive: SUBTYPE_TITLE_HDB_EXECUTIVE,
  HDB_HUDC: SUBTYPE_TITLE_HBD_HUDC,
  HDB_Jumbo: SUBTYPE_TITLE_HBD_JUMBO,
  Office: SUBTYPE_TITLE_OFFICE,
  Retail: SUBTYPE_TITLE_RETAIL,
  Shophouse: SUBTYPE_TITLE_SHOP_HOUSE,
  Warehouse: SUBTYPE_TITLE_WAREHOUSE,
  Factory: SUBTYPE_TITLE_FACTORY,
  HDB_Shophouse: SUBTYPE_TITLE_HDB_SHOPHOUSE,
  Land: SUBTYPE_TITLE_LAND,
};