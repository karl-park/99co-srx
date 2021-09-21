import {
  PropertyType,
  Tenures,
  FilterOptions,
  LeaseTerm
} from "../../../constants";
import {
  PropertySearch_Hdb,
  PropertySearch_Condo,
  PropertySearch_Landed
} from "../../../assets";

const SubTypes = PropertyType.SubTypes;
const SubTypesDescription = PropertyType.SubTypesDescription;
const SmartFilterOptions = FilterOptions.FilterOptionDescription;
const LeaseTermValue = LeaseTerm.LeaseTermOption;
const LeaseTermDescription = LeaseTerm.LeaseTermOptionDescription;

var currentYear = new Date().getFullYear();

//HDB
const HDB_SubTypes = [
  SubTypes.HDB_1Room,
  SubTypes.HDB_2Rooms,
  SubTypes.HDB_3Rooms,
  SubTypes.HDB_4Rooms,
  SubTypes.HDB_5Rooms,
  SubTypes.HDB_Executive,
  SubTypes.HDB_HUDC,
  SubTypes.HDB_Jumbo
];

//Condo
const CondoApt_SubTypes = [SubTypes.Condominium, SubTypes.Apartment];

//Landed
const Landed_SubTypes = [
  SubTypes.Terrace,
  SubTypes.SemiDetached,
  SubTypes.Detached
];

//Commerical
const Commercial_SubTypes = [
  SubTypes.Office,
  SubTypes.Retail,
  SubTypes.Shophouse,
  SubTypes.Warehouse,
  SubTypes.Factory,
  SubTypes.HDB_Shophouse,
  SubTypes.Land
];

//HDB Rooms
const HDB_OtherTypes = [
  SubTypes.HDB_1Room,
  SubTypes.HDB_2Rooms,
  SubTypes.HDB_HUDC,
  SubTypes.HDB_Jumbo
];

//Built Year
export const BuildYear_Set = new Set([
  SmartFilterOptions.years_0_to_5,
  SmartFilterOptions.years_5_to_10,
  SmartFilterOptions.years_10_to_20,
  SmartFilterOptions.years_20_to_30,
  SmartFilterOptions.years_more_than_40
]);

//Property Type
export const PropertyType_Set = new Set([
  SmartFilterOptions.hdb,
  SmartFilterOptions.condo,
  SmartFilterOptions.landed,
  SmartFilterOptions.commercial
]);

//ModelNotRequired
export const ModelNotRequired_Set = new Set([
  SubTypesDescription.SemiDetached,
  SubTypesDescription.Detached
]);

//Bedrooms
export const CondoBedRoom_Set = new Set([
  SmartFilterOptions.condo_1_bedroom,
  SmartFilterOptions.condo_2_bedroom,
  SmartFilterOptions.condo_3_bedroom,
  SmartFilterOptions.condo_4_bedroom,
  SmartFilterOptions.condo_5_bedroom,
  SmartFilterOptions.condo_more_than_6_bedroom
]);

//Tenure
export const Tenure_Set = new Set([
  SmartFilterOptions.freehold_tenure,
  SmartFilterOptions.leasehold_tenure
]);

//Lease Term
export const LeaseTerm_Set = new Set([
  LeaseTermDescription.flexible,
  LeaseTermDescription.shortTerm,
  LeaseTermDescription.between_12_and_23_months,
  LeaseTermDescription.above_24_months
]);

//Lease Term Array for Rent
const LeaseTerm_Options = [
  {
    key: LeaseTermDescription.flexible,
    value: [LeaseTermValue.flexible],
    count: 0,
    child: null
  },
  {
    key: LeaseTermDescription.shortTerm,
    value: [LeaseTermValue.shortTerm],
    count: 0,
    child: null
  },
  {
    key: LeaseTermDescription.between_12_and_23_months,
    value: [LeaseTermValue.between_12_and_23_months],
    count: 0,
    child: null
  },
  {
    key: LeaseTermDescription.above_24_months,
    value: [LeaseTermValue.above_24_months],
    count: 0,
    child: null
  }
];

//Tenure Array for Sale
const Tenure_Options = [
  {
    key: SmartFilterOptions.freehold_tenure,
    value: [Tenures.TenureType.Freehold_999_years],
    count: 0,
    child: null
  },
  {
    key: SmartFilterOptions.leasehold_tenure,
    value: [
      Tenures.TenureType.Leasehold_99_years,
      Tenures.TenureType.Leasehold_60_years,
      Tenures.TenureType.Leasehold_30_years
    ],
    count: 0,
    child: null
  }
];

//Landed Built Year
const Landed_Year_Options = [
  {
    key: SmartFilterOptions.years_0_to_5,
    value: [currentYear - 5, currentYear],
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.years_5_to_10,
    value: [currentYear - 10, currentYear - 5],
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.years_10_to_20,
    value: [currentYear - 20, currentYear - 10],
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.years_20_to_30,
    value: [currentYear - 30, currentYear - 20],
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.years_more_than_40,
    value: [currentYear - 40, ""],
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  }
];

//HDB Built Year
const HDB_Year_Options = [
  {
    key: SmartFilterOptions.years_0_to_5,
    value: [currentYear - 5, currentYear],
    count: 0,
    child: null
  },
  {
    key: SmartFilterOptions.years_5_to_10,
    value: [currentYear - 10, currentYear - 5],
    count: 0,
    child: null
  },
  {
    key: SmartFilterOptions.years_10_to_20,
    value: [currentYear - 20, currentYear - 10],
    count: 0,
    child: null
  },
  {
    key: SmartFilterOptions.years_20_to_30,
    value: [currentYear - 30, currentYear - 20],
    count: 0,
    child: null
  },
  {
    key: SmartFilterOptions.years_more_than_40,
    value: [currentYear - 40, ""],
    count: 0,
    child: null
  }
];

//Landed SubTypes
const LandedType_Options = [
  {
    key: SubTypesDescription.Terrace,
    value: [SubTypes.Terrace],
    count: 0,
    child: Landed_Year_Options
  },
  {
    key: SubTypesDescription.SemiDetached,
    value: [SubTypes.SemiDetached],
    count: 0,
    child: Landed_Year_Options
  },
  {
    key: SubTypesDescription.Detached,
    value: [SubTypes.Detached],
    count: 0,
    child: Landed_Year_Options
  },
  {
    key: SmartFilterOptions.others,
    value: [30, 40],
    count: 0,
    child: Landed_Year_Options
  }
];

//Commerical SubTypes
export const CommercialType_Options = [
  {
    key: SubTypesDescription.Retail,
    value: [SubTypes.Retail],
    count: 0,
    child: null
  },
  {
    key: SubTypesDescription.Office,
    value: [SubTypes.Office],
    count: 0,
    child: null
  },
  {
    key: SubTypesDescription.Factory,
    value: [SubTypes.Factory],
    count: 0,
    child: null
  },
  {
    key: SubTypesDescription.Warehouse,
    value: [SubTypes.Warehouse],
    count: 0,
    child: null
  },
  {
    key: SubTypesDescription.Land,
    value: [SubTypes.Land],
    count: 0,
    child: null
  },
  {
    key: SubTypesDescription.HDB_Shophouse,
    value: [SubTypes.HDB_Shophouse],
    count: 0,
    child: null
  },
  {
    key: SubTypesDescription.Shophouse,
    value: [SubTypes.Shophouse],
    count: 0,
    child: null
  },
  {
    key: SmartFilterOptions.others,
    value: [30, 40],
    count: 0,
    child: null
  }
];

//Condo Bedrooms
const Condo_Rooms_Options = [
  {
    key: SmartFilterOptions.condo_1_bedroom,
    value: 1,
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.condo_2_bedroom,
    value: 2,
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.condo_3_bedroom,
    value: 3,
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.condo_4_bedroom,
    value: 4,
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.condo_5_bedroom,
    value: 5,
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  },
  {
    key: SmartFilterOptions.condo_more_than_6_bedroom,
    value: 6,
    count: 0,
    child: null,
    tenure: Tenure_Options,
    leaseterm: LeaseTerm_Options
  }
];

//HDB SubTypes
const HDB_Rooms_Options = [
  {
    key: SmartFilterOptions.hdb_3_rooms,
    value: [SubTypes.HDB_3Rooms],
    count: 0,
    child: HDB_Year_Options
  },
  {
    key: SmartFilterOptions.hdb_4_rooms,
    value: [SubTypes.HDB_4Rooms],
    count: 0,
    child: HDB_Year_Options
  },
  {
    key: SmartFilterOptions.hdb_5_rooms,
    value: [SubTypes.HDB_5Rooms],
    count: 0,
    child: HDB_Year_Options
  },
  {
    key: SmartFilterOptions.hdb_executive_room,
    value: [SubTypes.HDB_Executive],
    count: 0,
    child: HDB_Year_Options
  },
  {
    key: SmartFilterOptions.others,
    value: HDB_OtherTypes,
    count: 0,
    child: HDB_Year_Options
  }
];

//Residential Type
export const Residential_Options = [
  {
    key: SmartFilterOptions.hdb,
    value: HDB_SubTypes,
    count: 0,
    child: HDB_Rooms_Options,
    icon: PropertySearch_Hdb
  },
  {
    key: SmartFilterOptions.condo,
    value: CondoApt_SubTypes,
    count: 0,
    child: Condo_Rooms_Options,
    icon: PropertySearch_Condo
  },
  {
    key: SmartFilterOptions.landed,
    value: Landed_SubTypes,
    count: 0,
    child: LandedType_Options,
    icon: PropertySearch_Landed
  }
];

//Commercial Type
export const Commercial_Options = [
  {
    key: SmartFilterOptions.commercial,
    value: Commercial_SubTypes,
    count: 0,
    child: CommercialType_Options
  }
];
