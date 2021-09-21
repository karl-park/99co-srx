const FLOOR_GROUND = "GROUND";
const FLOOR_TITLE_GROUND = "Ground Floor";

const FLOOR_LOW = "LOW";
const FLOOR_TITLE_LOW = "Low Floor";

const FLOOR_MID = "MID";
const FLOOR_TITLE_MID = "Mid Floor";

const FLOOR_HIGH = "HIGH";
const FLOOR_TITLE_HIGH = "High Floor";

const FLOOR_PENTHOUSE = "PENTHOUSE";
const FLOOR_TITLE_PENTHOUSE = "Penthouse";

export const Type = {
    GROUND: FLOOR_GROUND,
    LOW: FLOOR_LOW,
    MID: FLOOR_MID,
    HIGH: FLOOR_HIGH,
    PENTHOUSE: FLOOR_PENTHOUSE
}

export const Description = {
    GROUND: FLOOR_TITLE_GROUND,
    LOW: FLOOR_TITLE_LOW,
    MID: FLOOR_TITLE_MID,
    HIGH: FLOOR_TITLE_HIGH,
    PENTHOUSE: FLOOR_TITLE_PENTHOUSE
}

export const FLOOR_ARRAY = [
    {
        key: FLOOR_TITLE_GROUND,
        value: FLOOR_GROUND
    },
    {
        key: FLOOR_TITLE_LOW,
        value: FLOOR_LOW
    },
    {
        key: FLOOR_TITLE_MID,
        value: FLOOR_MID
    },
    {
        key: FLOOR_TITLE_HIGH,
        value: FLOOR_HIGH
    },
    {
        key: FLOOR_TITLE_PENTHOUSE,
        value: FLOOR_PENTHOUSE
    }
]