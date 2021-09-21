import { MRTStations, MRTStationsDescription } from "./MRTConstants";

const EAST_WEST_LINE_ARRAY = [
    {
        key: MRTStationsDescription.EW29,
        value: MRTStations.EW29
    },
    {
        key: MRTStationsDescription.EW28,
        value: MRTStations.EW28
    },
    {
        key: MRTStationsDescription.EW27,
        value: MRTStations.EW27
    },
    {
        key: MRTStationsDescription.EW26,
        value: MRTStations.EW26
    },
    {
        key: MRTStationsDescription.EW25,
        value: MRTStations.EW25
    },
    {
        key: MRTStationsDescription.EW24_NS1,
        value: MRTStations.EW24_NS1
    },
    {
        key: MRTStationsDescription.EW23,
        value: MRTStations.EW23
    },
    {
        key: MRTStationsDescription.EW22,
        value: MRTStations.EW22
    },
    {
        key: MRTStationsDescription.EW21,
        value: MRTStations.EW21
    },
    {
        key: MRTStationsDescription.EW20,
        value: MRTStations.EW20
    },
    {
        key: MRTStationsDescription.EW19,
        value: MRTStations.EW19
    },
    {
        key: MRTStationsDescription.EW18,
        value: MRTStations.EW18
    },
    {
        key: MRTStationsDescription.EW17,
        value: MRTStations.EW17
    },
    {
        key: MRTStationsDescription.EW16_NE3,
        value: MRTStations.EW16_NE3
    },
    {
        key: MRTStationsDescription.EW15,
        value: MRTStations.EW15
    },
    {
        key: MRTStationsDescription.EW14_NS26,
        value: MRTStations.EW14_NS26
    },
    {
        key: MRTStationsDescription.EW13_NS25,
        value: MRTStations.EW13_NS25
    },
    {
        key: MRTStationsDescription.EW12_DT14,
        value: MRTStations.EW12_DT14
    },
    {
        key: MRTStationsDescription.EW11,
        value: MRTStations.EW11
    },
    {
        key: MRTStationsDescription.EW10,
        value: MRTStations.EW10
    },
    {
        key: MRTStationsDescription.EW9,
        value: MRTStations.EW9
    },
    {
        key: MRTStationsDescription.EW8_CC9,
        value: MRTStations.EW8_CC9
    },
    {
        key: MRTStationsDescription.EW7,
        value: MRTStations.EW7
    },
    {
        key: MRTStationsDescription.EW6,
        value: MRTStations.EW6
    },
    {
        key: MRTStationsDescription.EW5,
        value: MRTStations.EW5
    },
    {
        key: MRTStationsDescription.EW4,
        value: MRTStations.EW4
    },
    {
        key: MRTStationsDescription.EW3,
        value: MRTStations.EW3
    },
    {
        key: MRTStationsDescription.EW2_DT32,
        value: MRTStations.EW2_DT32
    },
    {
        key: MRTStationsDescription.EW1,
        value: MRTStations.EW1
    },
    {
        key: MRTStationsDescription.CG1_DT35,
        value: MRTStations.CG1_DT35
    },
    {
        key: MRTStationsDescription.CG2,
        value: MRTStations.CG2
    },
];

const NORTH_SOUTH_LINE_ARRAY = [

    {
        key: MRTStationsDescription.EW24_NS1,
        value: MRTStations.EW24_NS1
    },
    {
        key: MRTStationsDescription.NS2,
        value: MRTStations.NS2
    },
    {
        key: MRTStationsDescription.NS3,
        value: MRTStations.NS3
    },
    {
        key: MRTStationsDescription.NS4_BP1,
        value: MRTStations.NS4_BP1
    },
    {
        key: MRTStationsDescription.NS5,
        value: MRTStations.NS5
    },
    {
        key: MRTStationsDescription.NS7,
        value: MRTStations.NS7
    },
    {
        key: MRTStationsDescription.NS8,
        value: MRTStations.NS8
    },
    {
        key: MRTStationsDescription.NS9,
        value: MRTStations.NS9
    },
    {
        key: MRTStationsDescription.NS10,
        value: MRTStations.NS10
    },
    {
        key: MRTStationsDescription.NS11,
        value: MRTStations.NS11
    },
    {
        key: MRTStationsDescription.NS12,
        value: MRTStations.NS12
    },
    {
        key: MRTStationsDescription.NS13,
        value: MRTStations.NS13
    },
    {
        key: MRTStationsDescription.NS14,
        value: MRTStations.NS14
    },
    {
        key: MRTStationsDescription.NS15,
        value: MRTStations.NS15
    },
    {
        key: MRTStationsDescription.NS16,
        value: MRTStations.NS16
    },
    {
        key: MRTStationsDescription.NS17_CC15,
        value: MRTStations.NS17_CC15
    },
    {
        key: MRTStationsDescription.NS18,
        value: MRTStations.NS18
    },
    {
        key: MRTStationsDescription.NS19,
        value: MRTStations.NS19
    },
    {
        key: MRTStationsDescription.NS20,
        value: MRTStations.NS20
    },
    {
        key: MRTStationsDescription.NS21_DT11,
        value: MRTStations.NS21_DT11
    },
    {
        key: MRTStationsDescription.NS22,
        value: MRTStations.NS22
    },
    {
        key: MRTStationsDescription.NS24_NE6_CC1,
        value: MRTStations.NS24_NE6_CC1
    },
    {
        key: MRTStationsDescription.EW13_NS25,
        value: MRTStations.EW13_NS25
    },
    {
        key: MRTStationsDescription.EW14_NS26,
        value: MRTStations.EW14_NS26
    },
    {
        key: MRTStationsDescription.NS27,
        value: MRTStations.NS27
    },
    {
        key: MRTStationsDescription.NS28,
        value: MRTStations.NS28
    },
];

const NORTH_EAST_LINE_ARRAY = [
    {
        key: MRTStationsDescription.NE1,
        value: MRTStations.NE1
    },
    {
        key: MRTStationsDescription.EW16_NE3,
        value: MRTStations.EW16_NE3
    },
    {
        key: MRTStationsDescription.NE4_DT19,
        value: MRTStations.NE4_DT19
    },
    {
        key: MRTStationsDescription.NE5,
        value: MRTStations.NE5
    },
    {
        key: MRTStationsDescription.NE6,
        value: MRTStations.NE6
    },
    {
        key: MRTStationsDescription.NE7_DT12,
        value: MRTStations.NE7_DT12
    },
    {
        key: MRTStationsDescription.NE8,
        value: MRTStations.NE8
    },
    {
        key: MRTStationsDescription.NE9,
        value: MRTStations.NE9
    },
    {
        key: MRTStationsDescription.NE10,
        value: MRTStations.NE10
    },
    {
        key: MRTStationsDescription.NE11,
        value: MRTStations.NE11
    },
    {
        key: MRTStationsDescription.NE12_CC13,
        value: MRTStations.NE12_CC13
    },
    {
        key: MRTStationsDescription.NE13,
        value: MRTStations.NE13
    },
    {
        key: MRTStationsDescription.NE14,
        value: MRTStations.NE14
    },
    {
        key: MRTStationsDescription.NE15,
        value: MRTStations.NE15
    },
    {
        key: MRTStationsDescription.NE16,
        value: MRTStations.NE16
    },
    {
        key: MRTStationsDescription.NE17,
        value: MRTStations.NE17
    }
];

const CIRCLE_LINE_ARRAY = [
    {
        key: MRTStationsDescription.CC29,
        value: MRTStations.CC29
    },
    {
        key: MRTStationsDescription.CC28,
        value: MRTStations.CC28
    },
    {
        key: MRTStationsDescription.CC27,
        value: MRTStations.CC27
    },
    {
        key: MRTStationsDescription.CC26,
        value: MRTStations.CC26
    },
    {
        key: MRTStationsDescription.CC25,
        value: MRTStations.CC25
    },
    {
        key: MRTStationsDescription.CC24,
        value: MRTStations.CC24
    },
    {
        key: MRTStationsDescription.CC23,
        value: MRTStations.CC23
    },
    {
        key: MRTStationsDescription.CC22,
        value: MRTStations.CC22
    },
    {
        key: MRTStationsDescription.CC21,
        value: MRTStations.CC21
    },
    {
        key: MRTStationsDescription.CC20,
        value: MRTStations.CC20
    },
    {
        key: MRTStationsDescription.CC19_DT9,
        value: MRTStations.CC19_DT9
    },
    {
        key: MRTStationsDescription.CC18,
        value: MRTStations.CC18
    },
    {
        key: MRTStationsDescription.CC17,
        value: MRTStations.CC17
    },
    {
        key: MRTStationsDescription.CC16,
        value: MRTStations.CC16
    },
    {
        key: MRTStationsDescription.NS17_CC15,
        value: MRTStations.NS17_CC15
    },
    {
        key: MRTStationsDescription.CC14,
        value: MRTStations.CC14
    },
    {
        key: MRTStationsDescription.NE12_CC13,
        value: MRTStations.NE12_CC13
    },
    {
        key: MRTStationsDescription.CC12,
        value: MRTStations.CC12
    },
    {
        key: MRTStationsDescription.CC11,
        value: MRTStations.CC11
    },
    {
        key: MRTStationsDescription.CC10_DT26,
        value: MRTStations.CC10_DT26
    },
    {
        key: MRTStationsDescription.EW8_CC9,
        value: MRTStations.EW8_CC9
    },
    {
        key: MRTStationsDescription.CC8,
        value: MRTStations.CC8
    },
    {
        key: MRTStationsDescription.CC7,
        value: MRTStations.CC7
    },
    {
        key: MRTStationsDescription.CC6,
        value: MRTStations.CC6
    },
    {
        key: MRTStationsDescription.CC5,
        value: MRTStations.CC5
    },
    {
        key: MRTStationsDescription.CC4_DT15,
        value: MRTStations.CC4_DT15
    },
    {
        key: MRTStationsDescription.CC3,
        value: MRTStations.CC3
    },
    {
        key: MRTStationsDescription.CC2,
        value: MRTStations.CC2
    },
    {
        key: MRTStationsDescription.NS24_NE6_CC1,
        value: MRTStations.NS24_NE6_CC1
    },
    {
        key: MRTStationsDescription.CE1_DT16,
        value: MRTStations.CE1_DT16
    },
    {
        key: MRTStationsDescription.CE2,
        value: MRTStations.CE2
    },
];

const DOWNTOWN_LINE_ARRAY = [
    {
        key: MRTStationsDescription.DT1,
        value: MRTStations.DT1
    },
    {
        key: MRTStationsDescription.DT2,
        value: MRTStations.DT2
    },
    {
        key: MRTStationsDescription.DT3,
        value: MRTStations.DT3
    },
    {
        key: MRTStationsDescription.DT5,
        value: MRTStations.DT5
    },
    {
        key: MRTStationsDescription.DT6,
        value: MRTStations.DT6
    },
    {
        key: MRTStationsDescription.DT7,
        value: MRTStations.DT7
    },
    {
        key: MRTStationsDescription.DT8,
        value: MRTStations.DT8
    },
    {
        key: MRTStationsDescription.CC19_DT9,
        value: MRTStations.CC19_DT9
    },
    {
        key: MRTStationsDescription.DT10,
        value: MRTStations.DT10
    },
    {
        key: MRTStationsDescription.NS21_DT11,
        value: MRTStations.NS21_DT11
    },
    {
        key: MRTStationsDescription.NE7_DT12,
        value: MRTStations.NE7_DT12
    },
    {
        key: MRTStationsDescription.DT13,
        value: MRTStations.DT13
    },
    {
        key: MRTStationsDescription.EW12_DT14,
        value: MRTStations.EW12_DT14
    },
    {
        key: MRTStationsDescription.CC4_DT15,
        value: MRTStations.CC4_DT15
    },
    {
        key: MRTStationsDescription.CE1_DT16,
        value: MRTStations.CE1_DT16
    },
    {
        key: MRTStationsDescription.DT17,
        value: MRTStations.DT17
    },
    {
        key: MRTStationsDescription.DT18,
        value: MRTStations.DT18
    },
    {
        key: MRTStationsDescription.NE4_DT19,
        value: MRTStations.NE4_DT19
    },
    {
        key: MRTStationsDescription.DT20,
        value: MRTStations.DT20
    },
    {
        key: MRTStationsDescription.DT21,
        value: MRTStations.DT21
    },
    {
        key: MRTStationsDescription.DT22,
        value: MRTStations.DT22
    },
    {
        key: MRTStationsDescription.DT23,
        value: MRTStations.DT23
    },
    {
        key: MRTStationsDescription.DT24,
        value: MRTStations.DT24
    },
    {
        key: MRTStationsDescription.DT25,
        value: MRTStations.DT25
    },
    {
        key: MRTStationsDescription.CC10_DT26,
        value: MRTStations.CC10_DT26
    },
    {
        key: MRTStationsDescription.DT27,
        value: MRTStations.DT27
    },
    {
        key: MRTStationsDescription.DT28,
        value: MRTStations.DT28
    },
    {
        key: MRTStationsDescription.DT29,
        value: MRTStations.DT29
    },
    {
        key: MRTStationsDescription.DT30,
        value: MRTStations.DT30
    },
    {
        key: MRTStationsDescription.DT31,
        value: MRTStations.DT31
    },
    {
        key: MRTStationsDescription.EW2_DT32,
        value: MRTStations.EW2_DT32
    },
    {
        key: MRTStationsDescription.DT33,
        value: MRTStations.DT33
    },
    {
        key: MRTStationsDescription.DT34,
        value: MRTStations.DT34
    },
    {
        key: MRTStationsDescription.CG1_DT35,
        value: MRTStations.CG1_DT35
    },
    {
        key: MRTStationsDescription.DT36,
        value: MRTStations.DT36
    },
    {
        key: MRTStationsDescription.DT37,
        value: MRTStations.DT37
    }
];

const THOMOSON_EAST_COAST_LINE_ARRAY = [
    {
        key: MRTStationsDescription.TE1,
        value: MRTStations.TE1
    },
    {
        key: MRTStationsDescription.TE2,
        value: MRTStations.TE2
    },
    {
        key: MRTStationsDescription.TE3,
        value: MRTStations.TE3
    },
    {
        key: MRTStationsDescription.TE4,
        value: MRTStations.TE4
    },
    {
        key: MRTStationsDescription.TE5,
        value: MRTStations.TE5
    },
    {
        key: MRTStationsDescription.TE6,
        value: MRTStations.TE6
    },
    {
        key: MRTStationsDescription.TE7,
        value: MRTStations.TE7
    },
    {
        key: MRTStationsDescription.TE8,
        value: MRTStations.TE8
    },
    {
        key: MRTStationsDescription.TE9,
        value: MRTStations.TE9
    },
    {
        key: MRTStationsDescription.TE10,
        value: MRTStations.TE10
    },
    {
        key: MRTStationsDescription.TE11,
        value: MRTStations.TE11
    },
    {
        key: MRTStationsDescription.TE12,
        value: MRTStations.TE12
    },
    {
        key: MRTStationsDescription.TE13,
        value: MRTStations.TE13
    },
    {
        key: MRTStationsDescription.TE14,
        value: MRTStations.TE14
    },
    {
        key: MRTStationsDescription.TE15,
        value: MRTStations.TE15
    },
    {
        key: MRTStationsDescription.TE16,
        value: MRTStations.TE16
    },
    {
        key: MRTStationsDescription.TE17,
        value: MRTStations.TE17
    },
    {
        key: MRTStationsDescription.TE18,
        value: MRTStations.TE18
    },
    {
        key: MRTStationsDescription.TE19,
        value: MRTStations.TE19
    },
    {
        key: MRTStationsDescription.TE20,
        value: MRTStations.TE20
    },
    {
        key: MRTStationsDescription.TE21,
        value: MRTStations.TE21
    },
    {
        key: MRTStationsDescription.TE22,
        value: MRTStations.TE22
    },
    {
        key: MRTStationsDescription.TE23,
        value: MRTStations.TE23
    },
    {
        key: MRTStationsDescription.TE24,
        value: MRTStations.TE24
    },
    {
        key: MRTStationsDescription.TE25,
        value: MRTStations.TE25
    },
    {
        key: MRTStationsDescription.TE26,
        value: MRTStations.TE26
    },
    {
        key: MRTStationsDescription.TE27,
        value: MRTStations.TE27
    },
    {
        key: MRTStationsDescription.TE28,
        value: MRTStations.TE28
    },
    {
        key: MRTStationsDescription.TE29,
        value: MRTStations.TE29
    },
    {
        key: MRTStationsDescription.TE30,
        value: MRTStations.TE30
    },
    {
        key: MRTStationsDescription.TE31,
        value: MRTStations.TE31 //done
    }
];

const BUKIT_PAJANG_LRT_ARRAY = [
    {
        key: MRTStationsDescription.BP2,
        value: MRTStations.BP2
    },
    {
        key: MRTStationsDescription.BP3,
        value: MRTStations.BP3
    },
    {
        key: MRTStationsDescription.BP4,
        value: MRTStations.BP4
    },
    {
        key: MRTStationsDescription.BP5,
        value: MRTStations.BP5
    },
    {
        key: MRTStationsDescription.BP6,
        value: MRTStations.BP6
    },
    {
        key: MRTStationsDescription.BP7,
        value: MRTStations.BP7
    },
    {
        key: MRTStationsDescription.BP8,
        value: MRTStations.BP8
    },
    {
        key: MRTStationsDescription.BP9,
        value: MRTStations.BP9
    },
    {
        key: MRTStationsDescription.BP10,
        value: MRTStations.BP10
    },
    {
        key: MRTStationsDescription.BP11,
        value: MRTStations.BP11
    },
    {
        key: MRTStationsDescription.BP12,
        value: MRTStations.BP12
    },
    {
        key: MRTStationsDescription.BP13,
        value: MRTStations.BP13
    },
    {
        key: MRTStationsDescription.BP14,
        value: MRTStations.BP14 //done
    }
];

const SENGKANG_LRT_ARRAY = [
    {
        key: MRTStationsDescription.SE1,
        value: MRTStations.SE1
    },
    {
        key: MRTStationsDescription.SE2,
        value: MRTStations.SE2
    },
    {
        key: MRTStationsDescription.SE3,
        value: MRTStations.BP3
    },
    {
        key: MRTStationsDescription.SE4,
        value: MRTStations.SE4
    },
    {
        key: MRTStationsDescription.SE5,
        value: MRTStations.SE5
    },
    {
        key: MRTStationsDescription.SW1,
        value: MRTStations.SW1
    },
    {
        key: MRTStationsDescription.SW2,
        value: MRTStations.SW2
    },
    {
        key: MRTStationsDescription.SW3,
        value: MRTStations.SW3
    },
    {
        key: MRTStationsDescription.SW4,
        value: MRTStations.SW4
    },
    {
        key: MRTStationsDescription.SW5,
        value: MRTStations.SW5
    },
    {
        key: MRTStationsDescription.SW6,
        value: MRTStations.SW6
    },
    {
        key: MRTStationsDescription.SW7,
        value: MRTStations.SW7
    },
    {
        key: MRTStationsDescription.SW8,
        value: MRTStations.SW8
    }
];

const PUNGGOL_LRT_ARRAY = [
    {
        key: MRTStationsDescription.PE1,
        value: MRTStations.PE1
    },
    {
        key: MRTStationsDescription.PE2,
        value: MRTStations.PE2
    },
    {
        key: MRTStationsDescription.PE3,
        value: MRTStations.PE3
    },
    {
        key: MRTStationsDescription.PE4,
        value: MRTStations.PE4
    },
    {
        key: MRTStationsDescription.PE5,
        value: MRTStations.PE5
    },
    {
        key: MRTStationsDescription.PE6,
        value: MRTStations.PE6
    },
    {
        key: MRTStationsDescription.PE7,
        value: MRTStations.PE7
    },
    {
        key: MRTStationsDescription.PW1,
        value: MRTStations.PW1
    },
    {
        key: MRTStationsDescription.PW2,
        value: MRTStations.PW2
    },
    {
        key: MRTStationsDescription.PW3,
        value: MRTStations.PW3
    },
    {
        key: MRTStationsDescription.PW4,
        value: MRTStations.PW4
    },
    {
        key: MRTStationsDescription.PW5,
        value: MRTStations.PW5
    },
    {
        key: MRTStationsDescription.PW6,
        value: MRTStations.PW6
    },
    {
        key: MRTStationsDescription.PW7,
        value: MRTStations.PW7
    }
];

export const MRT_LIST = {
    EAST_WEST_LINE_ARRAY,
    NORTH_SOUTH_LINE_ARRAY,
    NORTH_EAST_LINE_ARRAY,
    CIRCLE_LINE_ARRAY,
    DOWNTOWN_LINE_ARRAY,
    THOMOSON_EAST_COAST_LINE_ARRAY,
    BUKIT_PAJANG_LRT_ARRAY,
    SENGKANG_LRT_ARRAY,
    PUNGGOL_LRT_ARRAY
}

export const getMrtDescription = (districtKey) => {
    for (let entry of Object.entries(MRTStations)) {
        const key = entry[0];
        const value = entry[1];
        if (value == districtKey) {
          return MRTStationsDescription[key];
        }
    }
    return "";
}