import { District, MRTs, MRTConstants } from "../../../constants";

const SubDistricts = District.Districts;
const SubDistrictsDescription = District.DistrictsDescription;

//Districts Value Set
const District_CCR_Set = new Set([
  SubDistricts.D1,
  SubDistricts.D2,
  SubDistricts.D4,
  SubDistricts.D6,
  SubDistricts.D7,
  SubDistricts.D9,
  SubDistricts.D10,
  SubDistricts.D11
]);

const District_RCR_Set = new Set([
  SubDistricts.D2,
  SubDistricts.D3,
  SubDistricts.D7,
  SubDistricts.D8,
  SubDistricts.D12,
  SubDistricts.D13,
  SubDistricts.D14,
  SubDistricts.D15
]);

const District_OCR_Set = new Set([
  SubDistricts.D5,
  SubDistricts.D16,
  SubDistricts.D17,
  SubDistricts.D18,
  SubDistricts.D19,
  SubDistricts.D20,
  SubDistricts.D21,
  SubDistricts.D22,
  SubDistricts.D23,
  SubDistricts.D24,
  SubDistricts.D25,
  SubDistricts.D26,
  SubDistricts.D27,
  SubDistricts.D28
]);

//District Region Object
const District_CCR = {
  key: SubDistrictsDescription.CCR,
  value: District_CCR_Set,
  data: District.DISTRICT_CORE_CENTRAL_REGION_ARRAY
};

const District_RCR = {
  key: SubDistrictsDescription.RCR,
  value: District_RCR_Set,
  data: District.DISTRICT_REST_CENTRAL_REGION_ARRAY
};

const District_OCR = {
  key: SubDistrictsDescription.OCR,
  value: District_OCR_Set,
  data: District.DISTRICT_OUTSIDE_CENTRAL_REGION_ARRAY
};

//District Array
export const All_Districts = [
  {
    index: 0,
    key: "Core Central Region",
    code: "CCR",
    data: [District_CCR]
  },
  {
    index: 1,
    key: "Rest of Central Region",
    code: "RCR",
    data: [District_RCR]
  },
  {
    index: 2,
    key: "Outside Central Region",
    code: "OCR",
    data: [District_OCR]
  }
];

//MRT Section
const MRTStations = MRTConstants.MRTStations;
const MRTLinesDescription = MRTConstants.MRTLinesDescription;
const MRTList = MRTs.MRT_LIST;

//MRT Line Set
const MRT_EastWestLine_Set = new Set([
  MRTStations.EW29,
  MRTStations.EW28,
  MRTStations.EW27,
  MRTStations.EW26,
  MRTStations.EW25,
  MRTStations.EW24_NS1,
  MRTStations.EW23,
  MRTStations.EW22,
  MRTStations.EW21,
  MRTStations.EW20,
  MRTStations.EW19,
  MRTStations.EW18,
  MRTStations.EW17,
  MRTStations.EW16_NE3,
  MRTStations.EW15,
  MRTStations.EW14_NS26,
  MRTStations.EW13_NS25,
  MRTStations.EW12_DT14,
  MRTStations.EW11,
  MRTStations.EW10,
  MRTStations.EW9,
  MRTStations.EW8_CC9,
  MRTStations.EW7,
  MRTStations.EW6,
  MRTStations.EW5,
  MRTStations.EW4,
  MRTStations.EW3,
  MRTStations.EW2_DT32,
  MRTStations.EW1,
  MRTStations.CG1_DT35,
  MRTStations.CG2
]);

const MRT_NorthSouthLine_Set = new Set([
  MRTStations.EW24_NS1,
  MRTStations.NS2,
  MRTStations.NS3,
  MRTStations.NS4_BP1,
  MRTStations.NS5,
  MRTStations.NS7,
  MRTStations.NS8,
  MRTStations.NS9,
  MRTStations.NS10,
  MRTStations.NS11,
  MRTStations.NS12,
  MRTStations.NS13,
  MRTStations.NS14,
  MRTStations.NS15,
  MRTStations.NS16,
  MRTStations.NS17_CC15,
  MRTStations.EW13_NS25,
  MRTStations.NS18,
  MRTStations.NS19,
  MRTStations.NS20,
  MRTStations.NS21_DT11,
  MRTStations.NS22,
  MRTStations.NS24_NE6_CC1,
  MRTStations.EW13_NS25,
  MRTStations.EW14_NS26,
  MRTStations.NS27,
  MRTStations.NS28
]);

const MRT_NorthEastLine_Set = new Set([
  MRTStations.NE1,
  MRTStations.EW16_NE3,
  MRTStations.NE4_DT19,
  MRTStations.NE5,
  MRTStations.NE6,
  MRTStations.NE7_DT12,
  MRTStations.NE8,
  MRTStations.NE9,
  MRTStations.NE10,
  MRTStations.NE11,
  MRTStations.NE12_CC13,
  MRTStations.NE13,
  MRTStations.NE14,
  MRTStations.NE15,
  MRTStations.NE16,
  MRTStations.NE17
]);

const MRT_CircleLine_Set = new Set([
  MRTStations.CC29,
  MRTStations.CC28,
  MRTStations.CC27,
  MRTStations.CC26,
  MRTStations.CC25,
  MRTStations.CC24,
  MRTStations.CC23,
  MRTStations.CC22,
  MRTStations.CC21,
  MRTStations.CC20,
  MRTStations.CC19_DT9,
  MRTStations.CC18,
  MRTStations.CC17,
  MRTStations.CC16,
  MRTStations.NS17_CC15,
  MRTStations.CC14,
  MRTStations.NE12_CC13,
  MRTStations.CC12,
  MRTStations.CC11,
  MRTStations.CC10_DT26,
  MRTStations.EW8_CC9,
  MRTStations.CC8,
  MRTStations.CC7,
  MRTStations.CC6,
  MRTStations.CC5,
  MRTStations.CC4_DT15,
  MRTStations.CC3,
  MRTStations.CC2,
  MRTStations.NS24_NE6_CC1,
  MRTStations.CE1_DT16,
  MRTStations.CE2
]);

const MRT_DowntownLine_Set = new Set([
  MRTStations.DT1,
  MRTStations.DT2,
  MRTStations.DT3,
  MRTStations.DT5,
  MRTStations.DT6,
  MRTStations.DT7,
  MRTStations.DT8,
  MRTStations.CC19_DT9,
  MRTStations.DT10,
  MRTStations.NS21_DT11,
  MRTStations.NE7_DT12,
  MRTStations.DT13,
  MRTStations.EW12_DT14,
  MRTStations.CC4_DT15,
  MRTStations.CE1_DT16,
  MRTStations.DT17,
  MRTStations.DT18,
  MRTStations.NE4_DT19,
  MRTStations.DT20,
  MRTStations.DT21,
  MRTStations.DT22,
  MRTStations.DT23,
  MRTStations.DT24,
  MRTStations.DT25,
  MRTStations.CC10_DT26,
  MRTStations.DT27,
  MRTStations.DT28,
  MRTStations.DT29,
  MRTStations.DT30,
  MRTStations.DT31,
  MRTStations.EW2_DT32,
  MRTStations.DT33,
  MRTStations.DT34,
  MRTStations.CG1_DT35,
  MRTStations.DT36,
  MRTStations.DT37
]);

const MRT_ThomosonEastCoastLine_Set = new Set([
  MRTStations.TE1,
  MRTStations.TE2,
  MRTStations.TE3,
  MRTStations.TE4,
  MRTStations.TE5,
  MRTStations.TE6,
  MRTStations.TE7,
  MRTStations.TE8,
  MRTStations.TE9,
  MRTStations.TE10,
  MRTStations.TE11,
  MRTStations.TE12,
  MRTStations.TE13,
  MRTStations.TE14,
  MRTStations.TE15,
  MRTStations.TE16,
  MRTStations.TE17,
  MRTStations.TE18,
  MRTStations.TE19,
  MRTStations.TE20,
  MRTStations.TE21,
  MRTStations.TE22,
  MRTStations.TE23,
  MRTStations.TE24,
  MRTStations.TE25,
  MRTStations.TE26,
  MRTStations.TE27,
  MRTStations.TE28,
  MRTStations.TE29,
  MRTStations.TE30,
  MRTStations.TE31
]);

const MRT_BukitPajangLRT_Set = new Set([
  MRTStations.BP2,
  MRTStations.BP3,
  MRTStations.BP4,
  MRTStations.BP5,
  MRTStations.BP6,
  MRTStations.BP7,
  MRTStations.BP8,
  MRTStations.BP9,
  MRTStations.BP10,
  MRTStations.BP11,
  MRTStations.BP12,
  MRTStations.BP13,
  MRTStations.BP14
]);

const MRT_SengKangLRT_Set = new Set([
  MRTStations.SE1,
  MRTStations.SE2,
  MRTStations.BP3,
  MRTStations.SE4,
  MRTStations.SE5,
  MRTStations.SW1,
  MRTStations.SW2,
  MRTStations.SW3,
  MRTStations.SW4,
  MRTStations.SW5,
  MRTStations.SW6,
  MRTStations.SW7,
  MRTStations.SW8
]);

const MRT_PunggolLRT_Set = new Set([
  MRTStations.PE1,
  MRTStations.PE2,
  MRTStations.PE3,
  MRTStations.PE4,
  MRTStations.PE5,
  MRTStations.PE6,
  MRTStations.PE7,
  MRTStations.PW1,
  MRTStations.PW2,
  MRTStations.PW3,
  MRTStations.PW4,
  MRTStations.PW5,
  MRTStations.PW6,
  MRTStations.PW7
]);

//MRT Objectsion
const MRT_EAST_WEST_LINE = {
  key: MRTLinesDescription.EastWestLine,
  value: MRT_EastWestLine_Set,
  data: MRTList.EAST_WEST_LINE_ARRAY
};

const MRT_NORTH_SOUTH_LINE = {
  key: MRTLinesDescription.NorthSouthLine,
  value: MRT_NorthSouthLine_Set,
  data: MRTList.NORTH_SOUTH_LINE_ARRAY
};

const MRT_NORTH_EAST_LINE = {
  key: MRTLinesDescription.NorthEastLine,
  value: MRT_NorthEastLine_Set,
  data: MRTList.NORTH_EAST_LINE_ARRAY
};

const MRT_CIRCLE_LINE = {
  key: MRTLinesDescription.CircleLine,
  value: MRT_CircleLine_Set,
  data: MRTList.CIRCLE_LINE_ARRAY
};

const MRT_DOWNTOWN_LINE = {
  key: MRTLinesDescription.DowntownLine,
  value: MRT_DowntownLine_Set,
  data: MRTList.DOWNTOWN_LINE_ARRAY
};

const MRT_THOMOSON_EAST_COAST_LINE = {
  key: MRTLinesDescription.ThomosonEastCoastLine,
  value: MRT_ThomosonEastCoastLine_Set,
  data: MRTList.THOMOSON_EAST_COAST_LINE_ARRAY
};

const MRT_BUKIT_PAJANG_LRT = {
  key: MRTLinesDescription.BukitPajangLRT,
  value: MRT_BukitPajangLRT_Set,
  data: MRTList.BUKIT_PAJANG_LRT_ARRAY
};

const MRT_SENGKANG_LRT = {
  key: MRTLinesDescription.SengkangLRT,
  value: MRT_SengKangLRT_Set,
  data: MRTList.SENGKANG_LRT_ARRAY
};

const MRT_PUNGGOL_LRT = {
  key: MRTLinesDescription.PunggolLRT,
  value: MRT_PunggolLRT_Set,
  data: MRTList.PUNGGOL_LRT_ARRAY
};

export const All_MRTs = [
  {
    index: 0,
    key: "East West Line",
    value: "EW",
    colorCode: "#009530",
    data: [MRT_EAST_WEST_LINE]
  },
  {
    index: 1,
    key: "North South Line",
    value: "NS",
    colorCode: "#DC241F",
    data: [MRT_NORTH_SOUTH_LINE]
  },
  {
    index: 2,
    key: "North East Line",
    value: "NE",
    colorCode: "#9016B2",
    data: [MRT_NORTH_EAST_LINE]
  },
  {
    index: 3,
    key: "Circle Line",
    value: "CC",
    colorCode: "#FF9A00",
    data: [MRT_CIRCLE_LINE]
  },
  {
    index: 4,
    key: "Downtown Line",
    value: "DT",
    colorCode: "#0354A6",
    data: [MRT_DOWNTOWN_LINE]
  },
  {
    index: 5,
    key: "Thomson East Coast Line",
    value: "TEC",
    colorCode: "#734538",
    data: [MRT_THOMOSON_EAST_COAST_LINE]
  },
  {
    index: 6,
    key: "Bukit Pajang LRT",
    value: "BP",
    colorCode: "#748477",
    data: [MRT_BUKIT_PAJANG_LRT]
  },
  {
    index: 7,
    key: "Sengkang LRT",
    value: "SE",
    colorCode: "#748477",
    data: [MRT_SENGKANG_LRT]
  },
  {
    index: 8,
    key: "Punggol LRT",
    value: "PE",
    colorCode: "#748477",
    data: [MRT_PUNGGOL_LRT]
  }
];
