import { PropertyType } from "../constants";

isCondo = subtype => {
  return (
    subtype == PropertyType.SubTypes.Condominium ||
    subtype == PropertyType.SubTypes.Apartment
  );
};

isHDB = subtype => {
  return (
    (subtype > 0 && subtype <= PropertyType.SubTypes.HDB_Executive) ||
    subtype == PropertyType.SubTypes.HDB_1Room ||
    subtype == PropertyType.SubTypes.HDB_2Rooms ||
    subtype == PropertyType.SubTypes.HDB_HUDC ||
    subtype == PropertyType.SubTypes.HDB_Jumbo
  );
};

isLanded = subtype => {
  return (
    subtype == PropertyType.SubTypes.Terrace ||
    subtype == PropertyType.SubTypes.SemiDetached ||
    subtype == PropertyType.SubTypes.Detached
  );
};

isCommercial = subtype => {
  return (
    (subtype >= PropertyType.SubTypes.Office &&
      subtype <= PropertyType.SubTypes.Factory) ||
    subtype == PropertyType.SubTypes.HDB_Shophouse ||
    subtype == PropertyType.SubTypes.Land
  );
};

isPrivate = subtype => {
  return this.isCondo(subtype) || this.isLanded(subtype)
};

getPropertyTypeDescription = subtype => {
  for (i = 0; i < PropertyType.ALL_SUBTYPE_ARRAY.length; i++) {
    const item = PropertyType.ALL_SUBTYPE_ARRAY[i];
    if (subtype == item.value) {
      return item.key;
    }
  }

  return "";
};

export const PropertyTypeUtil = {
  isCondo,
  isHDB,
  isLanded,
  isCommercial,
  isPrivate,
  getPropertyTypeDescription
};
