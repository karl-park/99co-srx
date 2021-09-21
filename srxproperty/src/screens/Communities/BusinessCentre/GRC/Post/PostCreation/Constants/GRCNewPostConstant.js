import {PropertyType} from '../../../../../../../constants';
import {
  Communities_Icon_All_Residents,
  Communities_Icon_Condo,
  Communities_Icon_Hdb,
  Communities_Icon_Landed,
} from '../../../../../../../assets';

const SubTypes = PropertyType.SubTypes;

const HDB_SubTypes = [
  SubTypes.HDB_1Room,
  SubTypes.HDB_2Rooms,
  SubTypes.HDB_3Rooms,
  SubTypes.HDB_4Rooms,
  SubTypes.HDB_5Rooms,
  SubTypes.HDB_Executive,
  SubTypes.HDB_HUDC,
  SubTypes.HDB_Jumbo,
];

const CondoApt_SubTypes = [SubTypes.Condominium, SubTypes.Apartment];

const Landed_SubTypes = [
  SubTypes.Terrace,
  SubTypes.SemiDetached,
  SubTypes.Detached,
];

const AllResidentialType = {
  key: 'All',
  value: 0,
  subTypes: [],
  icon: Communities_Icon_All_Residents,
};

const GRC_POST_PROPERTY_TYPES = [
  {
    key: 'All',
    value: 0,
    subTypes: [],
    icon: Communities_Icon_All_Residents,
  },
  {
    key: 'HDB',
    value: PropertyType.Type.HDB,
    subTypes: HDB_SubTypes,
    icon: Communities_Icon_Hdb,
  },
  {
    key: 'Condo',
    value: PropertyType.Type.Condo,
    subTypes: CondoApt_SubTypes,
    icon: Communities_Icon_Condo,
  },
  {
    key: 'Landed',
    value: PropertyType.Type.Landed,
    subTypes: Landed_SubTypes,
    icon: Communities_Icon_Landed,
  },
];

const GRCNewPostConstant = {
  allResidentialType: AllResidentialType,
  propertyTypes: GRC_POST_PROPERTY_TYPES,
};

export {GRCNewPostConstant};
