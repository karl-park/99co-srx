//Concierge Constants
import { UserType, PropertyType } from "../../../constants";
import {
  PropertySearch_Hdb,
  PropertySearch_Condo,
  PropertySearch_Landed,
  PropertySearch_Factory
} from "../../../assets";

//Property Type Value for help me find agent and choose my own agent
const TYPE_HDB = "HS,HR";
const TYPE_CONDO = "CS,CR";
const TYPE_LANDED = "LS,LR";
const TYPE_COMMERICAL = "CMS,CMR";

//Menu for Concierge Agents
const MENU_FIND_AGENT = "Help me find my agent";
const MENU_OWN_AGENT = "Choose my own agent";
const MENU_AGENT_DIRECTORY = "View agent directory";

//Title to show in Help me find agent & Choose my own agent screen
const FIND_AGENT_TITLE =
  "Tell us about your needs and we'll find the agent best suited to you";
const OWN_AGENT_TITLE = "Enter criteria and we'll find matching agents";

//Min and Max Price for price options in find agent form
const MIN_PRICE = "Min price";
const MAX_PRICE = "Max price";

//Filter Options Constants
const AREA = "Areas";
const DISTRICT = "Districts";

//Concierge Menu
export const Concierge_Menu = {
  findAgent: MENU_FIND_AGENT,
  ownAgent: MENU_OWN_AGENT,
  agentDirectory: MENU_AGENT_DIRECTORY
};

//Concierge Title
export const Concierge_Title = {
  findAgentTitle: FIND_AGENT_TITLE,
  ownAgentTitle: OWN_AGENT_TITLE
};

//Concierge Price Range Type
export const Concierge_PriceRange_Label = {
  minPriceTitle: MIN_PRICE,
  maxPriceTitle: MAX_PRICE
};

export const Concierge_FilterOptions = {
  area: AREA,
  district: DISTRICT,
  hdb: PropertyType.TypeDescription.HDB
};

export const Sale_Options = new Set([
  UserType.UserTypeValue.buy,
  UserType.UserTypeValue.buyNewProjects,
  UserType.UserTypeValue.sell
]);

export const All_PropertyType_Options = [
  {
    key: PropertyType.TypeDescription.HDB,
    value: TYPE_HDB,
    icon: PropertySearch_Hdb
  },
  {
    key: "Condo",
    value: TYPE_CONDO,
    icon: PropertySearch_Condo
  },
  {
    key: PropertyType.TypeDescription.Landed,
    value: TYPE_LANDED,
    icon: PropertySearch_Landed
  },
  {
    key: PropertyType.TypeDescription.Commercial,
    value: TYPE_COMMERICAL,
    icon: PropertySearch_Factory
  }
];

export const Concierge_PropertyType_Options = [
  {
    key: PropertyType.TypeDescription.Condo,
    value: "1"
  },
  {
    key: PropertyType.TypeDescription.Commercial,
    value: "2"
  },
  {
    key: PropertyType.TypeDescription.HDB,
    value: "3"
  },
  {
    key: PropertyType.TypeDescription.Landed,
    value: "4"
  }
];

export const Concierge_CondoAndApt_Option = [
  {
    key: PropertyType.TypeDescription.Condo,
    value: "1"
  }
];
