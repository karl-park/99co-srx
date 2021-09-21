import {
  Floors,
  Furnish,
  LandTypes,
  LeaseTerm,
  PropertyType,
  Tenures
} from "../constants";
import { PropertyTypeUtil } from "./PropertyTypeUtil";
import { LANDTYPE_ARRAY } from "../constants/LandTypes";

const PropertyOptionsUtil = {
  getPropertyTypesArrayForType,
  getPropertyTypesArrayForSubtype,
  getTypesOfAreas,
  getTenures,
  getBuiltYears,
  getFloorTypeDescription,
  getFurnishLevelDescription,
  getLeaseTermDescriptions,
  getTenureDescription,
  getTenureDescriptions
};

const NON_SELECT_OPTION = { key: "Select", value: "" };

function getPropertyTypesArrayForSubtype(subType) {
  var wantedArray;
  if (PropertyTypeUtil.isCondo(subType))
    wantedArray = PropertyType.CONDO_APT_ARRAY;
  else if (PropertyTypeUtil.isHDB(subType))
    wantedArray = PropertyType.HDB_ARRAY;
  else if (PropertyTypeUtil.isLanded(subType))
    wantedArray = PropertyType.LANDED_ARRAY;
  else if (PropertyTypeUtil.isCommercial(subType))
    wantedArray = PropertyType.COMMERCIAL_ARRAY;
  else {
    wantedArray = [
      ...PropertyType.CONDO_APT_ARRAY,
      ...PropertyType.HDB_ARRAY,
      ...PropertyType.LANDED_ARRAY,
      ...PropertyType.COMMERCIAL_ARRAY
    ];
  }

  return [{ key: "Property Types", value: "" }, ...wantedArray];
}

function getPropertyTypesArrayForType(propertyType) {
  if (propertyType == PropertyType.Type.HDB)
    return getPropertyTypesArrayForSubtype(PropertyType.SubTypes.HDB_3Rooms);
  else if (propertyType == PropertyType.Type.Condo)
    return getPropertyTypesArrayForSubtype(PropertyType.SubTypes.Condominium);
  else return getPropertyTypesArrayForSubtype();
}

function getTypesOfAreas() {
  return [{ key: "Type Of Area", value: "" }, ...LandTypes.LANDTYPE_ARRAY];
}

function getTenures() {
  return [{ key: "Tenure", value: "" }, ...Tenures.TENURE_ARRAY];
}

function getBuiltYears() {
  const year = new Date().getFullYear();
  const builtYear = [];
  builtYear.push({ key: "Built Year", value: "" });
  for (var i = year; i > 1960; i--) {
    builtYear.push({ key: i.toString(), value: i.toString() });
  }
  builtYear.push({ key: "Before 1960", value: "1959" });
  return builtYear;
}

function getFloorTypeDescription(floorType) {
  for (let entry of Object.entries(Floors.Type)) {
    const key = entry[0];
    const value = entry[1];
    if (value === floorType) {
      return Floors.Description[key];
    }
  }

  return "";
}

function getFurnishLevelDescription(furnishLvl) {
  for (let entry of Object.entries(Furnish.Level)) {
    const key = entry[0];
    const value = entry[1];
    if (value === furnishLvl) {
      return Furnish.Description[key];
    }
  }

  return "";
}

function getLeaseTermDescriptions(leaseTermOptionArray) {
  var leaseTermDescription = "";
  leaseTermOptionArray.forEach(item => {
    for (let entry of Object.entries(LeaseTerm.LeaseTermOption)) {
      const key = entry[0];
      const value = entry[1];
      if (value === item) {
        leaseTermDescription +=
          LeaseTerm.LeaseTermOptionDescription[key] + ", ";
      }
    }
  });
  return leaseTermDescription.substring(0, leaseTermDescription.length - 2);
}

function getTenureDescriptions(tenureValueArray) {
  var tenureDescription = "";
  tenureValueArray.forEach(item => {
    for (let entry of Object.entries(Tenures.TenureType)) {
      const key = entry[0];
      const value = entry[1];
      if (value === item) {
        tenureDescription += Tenures.TenureTypeDescription[key] + ", ";
      }
    }
  });
  return tenureDescription.substring(0, tenureDescription.length - 2);
}

function getTenureDescription(tenureCode) {
  var tenureDescription = "";
  switch (tenureCode) {
    case 1:
      tenureDescription = Tenures.TenureTypeDescription.Freehold_999_years;
      break;
    case 2:
      tenureDescription = Tenures.TenureTypeDescription.Leasehold_99_years;
      break;
    case 3:
      tenureDescription = Tenures.TenureTypeDescription.Leasehold_999_years;
      break;
    case 4:
      tenureDescription = Tenures.TenureTypeDescription.Leasehold_60_years;
      break;
    case 5:
      tenureDescription = Tenures.TenureTypeDescription.Leasehold_30_years;
      break;
    case 6:
      tenureDescription = Tenures.TenureTypeDescription.Leasehold_103_years;
      break;
    default:
      break;
  }

  return tenureDescription;
}

export { PropertyOptionsUtil };
