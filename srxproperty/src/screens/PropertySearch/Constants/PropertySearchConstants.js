import {
  PropertyType,
  Models,
  Tenures,
  FilterOptions,
  Furnish,
  Floors,
  ListedDate,
  LeaseTerm
} from "../../../constants";
import {
  PropertySearch_All_Commercial,
  PropertySearch_All_Residential,
  PropertySearch_Condo,
  PropertySearch_Factory,
  PropertySearch_Hdb,
  PropertySearch_Landed,
  PropertySearch_Newlaunches,
  PropertySearch_Retail,
  PropertySearch_Office,
  PropertySearch_Warehouse,
  PropertySearch_Land,
  PropertySearch_Shophouse_Hdb,
  PropertySearch_Shophouse
} from "../../../assets";

const SubTypes = PropertyType.SubTypes;
const FilterOptionsDesc = FilterOptions.FilterOptionDescription;

const CondoApt_Set = new Set([
  SubTypes.Condominium,
  SubTypes.Apartment
  // Models.Model.executive_Condo,
  // Models.Model.walkup_Apt
]);

const HDB_Set = new Set([
  SubTypes.HDB_1Room,
  SubTypes.HDB_2Rooms,
  SubTypes.HDB_3Rooms,
  SubTypes.HDB_4Rooms,
  SubTypes.HDB_5Rooms,
  SubTypes.HDB_Executive,
  SubTypes.HDB_HUDC,
  SubTypes.HDB_Jumbo
]);

const Landed_Set = new Set([
  SubTypes.Terrace,
  SubTypes.SemiDetached,
  SubTypes.Detached
  // Models.Model.cluster_Hse
]);

const Commercial_Set = new Set([
  SubTypes.Office,
  SubTypes.Retail,
  SubTypes.Shophouse,
  SubTypes.Warehouse,
  SubTypes.Factory,
  SubTypes.HDB_Shophouse,
  SubTypes.Land
]);

const Retail_Set = new Set([SubTypes.Retail]);
const Office_Set = new Set([SubTypes.Office]);
const Factory_Set = new Set([SubTypes.Factory]);
const Warehouse_Set = new Set([SubTypes.Warehouse]);
const Land_Set = new Set([SubTypes.Land]);
const HDB_Shophouse_Set = new Set([SubTypes.HDB_Shophouse]);
const Shophouse_Set = new Set([SubTypes.Shophouse]);

const All_Residential_Set = new Set([
  ...Array.from(CondoApt_Set),
  ...Array.from(HDB_Set),
  ...Array.from(Landed_Set)
]);

const All_Property_Set = new Set([
  ...Array.from(All_Residential_Set),
  ...Array.from(Commercial_Set)
]);

export const PropertyTypeValueSet = {
  all: All_Property_Set,
  allResidential: All_Residential_Set,
  condo: CondoApt_Set,
  hdb: HDB_Set,
  landed: Landed_Set,
  newlaunches: CondoApt_Set,
  commercial: Commercial_Set,
  retail: Retail_Set,
  office: Office_Set,
  factory: Factory_Set,
  warehouse: Warehouse_Set,
  land: Land_Set,
  hdbshophouse: HDB_Shophouse_Set,
  shophouse: Shophouse_Set
};

export const CondoApt_Options = [
  {
    key: "Condo / Apt",
    value: new Set([SubTypes.Condominium, SubTypes.Apartment]),
    indentLevel: 1
  },
  {
    key: Models.ModelDescription.executive_Condo,
    value: new Set([Models.Model.executive_Condo]),
    indentLevel: 1
  },
  {
    key: Models.ModelDescription.walkup_Apt,
    value: new Set([Models.Model.walkup_Apt]),
    indentLevel: 1
  }
];

export const All_Residential_Options = [
  {
    key: FilterOptionsDesc.all_residential,
    value: All_Residential_Set,
    indentLevel: 0
  },
  {
    key: FilterOptionsDesc.condo,
    value: CondoApt_Set,
    indentLevel: 0,
    subItems: CondoApt_Options
  },
  {
    key: FilterOptionsDesc.hdb,
    value: HDB_Set,
    indentLevel: 0
  },
  {
    key: FilterOptionsDesc.landed,
    value: Landed_Set,
    indentLevel: 0
  }
];

export const All_Property_Type_Options = [
  ...All_Residential_Options,
  {
    key: FilterOptionsDesc.commercial,
    value: Commercial_Set,
    indentLevel: 0
  }
];

//Filter Options Constants
//Tenure
const Tenure_Set = new Set([
  Tenures.TenureType.Freehold_999_years,
  Tenures.TenureType.Leasehold_99_years,
  Tenures.TenureType.Leasehold_30_years,
  Tenures.TenureType.Leasehold_60_years
]);

const SubTenureArray = [
  // {
  //   key: FilterOptionsDesc.select_all,
  //   value: FilterOptionsDesc.all_tenure
  // },
  ...Tenures.TENURE_FILTER_ARRAY
];

export const All_Tenure_Options = [
  {
    key: FilterOptionsDesc.all_tenure,
    value: Tenure_Set,
    data: SubTenureArray
  }
];

//Property Type
const SubCondoArray = [
  // {
  //   key: FilterOptionsDesc.select_all,
  //   value: FilterOptionsDesc.condo
  // },
  ...CondoApt_Options
];

const SubHDBArray = [
  // {
  //   key: FilterOptionsDesc.select_all,
  //   value: FilterOptionsDesc.hdb
  // },
  ...PropertyType.HDB_ARRAY
];

const SubLandedArray = [
  // {
  //   key: FilterOptionsDesc.select_all,
  //   value: FilterOptionsDesc.landed
  // },
  ...PropertyType.LANDED_ARRAY,
  {
    key: Models.ModelDescription.cluster_Hse,
    value: Models.Model.cluster_Hse
  }
];

const SubCommercialArray = [
  {
    key: FilterOptionsDesc.select_all,
    value: FilterOptionsDesc.commercial
  },
  ...PropertyType.COMMERCIAL_ARRAY
];

export const CondoApt_Model_Set = new Set([
  Models.Model.executive_Condo,
  Models.Model.walkup_Apt
]);

export const Landed_Model_Set = new Set([Models.Model.cluster_Hse]);

export const All_Property_Type_Set = new Set([
  FilterOptionsDesc.condo,
  FilterOptionsDesc.hdb,
  FilterOptionsDesc.landed,
  FilterOptionsDesc.commercial
]);

//Residential
export const PropertyType_Residential_Options = [
  {
    key: FilterOptionsDesc.all_residential,
    value: new Set([
      ...Array.from(All_Residential_Set),
      ...Array.from(CondoApt_Model_Set),
      ...Array.from(Landed_Model_Set)
    ]),
    data: [],
    image: PropertySearch_All_Residential
  },
  {
    key: FilterOptionsDesc.condo,
    value: new Set([
      ...Array.from(CondoApt_Set),
      ...Array.from(CondoApt_Model_Set)
    ]),
    data: SubCondoArray,
    image: PropertySearch_Condo
  },
  {
    key: FilterOptionsDesc.hdb,
    value: HDB_Set,
    data: SubHDBArray,
    image: PropertySearch_Hdb
  },
  {
    key: FilterOptionsDesc.landed,
    value: new Set([
      ...Array.from(Landed_Set),
      ...Array.from(Landed_Model_Set)
    ]),
    data: SubLandedArray,
    image: PropertySearch_Landed
  }
];

//Commercial
export const PropertyType_Commerical_Options = [
  {
    key: FilterOptionsDesc.commercial,
    value: Commercial_Set,
    data: SubCommercialArray,
    image: PropertySearch_All_Commercial
  },
  {
    key: FilterOptionsDesc.office,
    value: SubTypes.Office,
    data: [],
    image: PropertySearch_Office
  },
  {
    key: FilterOptionsDesc.warehouse,
    value: SubTypes.Warehouse,
    data: [],
    image: PropertySearch_Warehouse
  },
  {
    key: FilterOptionsDesc.shophouse,
    value: SubTypes.Shophouse,
    data: [],
    image: PropertySearch_Shophouse
  },
  {
    key: FilterOptionsDesc.retail,
    value: SubTypes.Retail,
    data: [],
    image: PropertySearch_Retail
  },
  {
    key: FilterOptionsDesc.land,
    value: SubTypes.Land,
    data: [],
    image: PropertySearch_Land
  },
  {
    key: FilterOptionsDesc.factory,
    value: SubTypes.Factory,
    data: [],
    image: PropertySearch_Factory
  },
  {
    key: FilterOptionsDesc.hdbshophouse,
    value: SubTypes.HDB_Shophouse,
    data: [],
    image: PropertySearch_Shophouse_Hdb
  }
];

//Furnishing
export const Furnishing_Options = [
  {
    key: FilterOptionsDesc.furnishing,
    value: new Set([
      ...Furnish.Level.NOT,
      ...Furnish.Level.HALF,
      ...Furnish.Level.FULL
    ]),
    data: Furnish.FURNISH_ARRAY
  }
];

//Floor
export const Floor_Set = new Set([
  Floors.Type.GROUND,
  Floors.Type.LOW,
  Floors.Type.MID,
  Floors.Type.HIGH,
  Floors.Type.PENTHOUSE
]);

const SubFloorLevelArray = [
  // {
  //   key: FilterOptionsDesc.any,
  //   value: FilterOptionsDesc.floorLevel
  // },
  ...Floors.FLOOR_ARRAY
];

export const Floor_Options = [
  {
    key: FilterOptionsDesc.floorLevel,
    value: Floor_Set,
    data: SubFloorLevelArray
  }
];

export const ListedDate_Array = [
  {
    key: FilterOptionsDesc.any,
    value: ""
  },
  ...ListedDate.LISTED_DATE_ARRAY
];

//Room Type
export const ROOM_TYPE_ARRAY = [
  {
    key: FilterOptionsDesc.any,
    value: undefined
  },
  {
    key: FilterOptionsDesc.entireHouse,
    value: false
  },
  {
    key: FilterOptionsDesc.room,
    value: true
  }
];

export const RoomType_Rent_Options = [
  {
    key: FilterOptionsDesc.roomType,
    value: new Set([false, true]),
    data: ROOM_TYPE_ARRAY
  }
];

//Lease Term
export const Lease_Term_Options = [
  {
    key: FilterOptionsDesc.leaseTerm,
    value: new Set([
      LeaseTerm.LeaseTermOption.flexible,
      LeaseTerm.LeaseTermOption.shortTerm,
      LeaseTerm.LeaseTermOption.between_12_and_23_months,
      LeaseTerm.LeaseTermOption.above_24_months
    ]),
    data: LeaseTerm.LEASE_TERM_ARRAY
  }
];
