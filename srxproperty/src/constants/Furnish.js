const FURNISH_FULL = "FULL";
const FURNISH_TITLE_FULL = "Fully Furnished";

const FURNISH_HALF = "HALF";
const FURNISH_TITLE_HALF = "Partial Furnished";

const FURNISH_NOT = "NOT";
const FURNISH_TITLE_NOT = "Not Furnished";

export const Level = {
    FULL: FURNISH_FULL,
    HALF: FURNISH_HALF,
    NOT: FURNISH_NOT
};

export const Description = {
    FULL: FURNISH_TITLE_FULL,
    HALF: FURNISH_TITLE_HALF,
    NOT: FURNISH_TITLE_NOT
};

export const FURNISH_ARRAY = [
    {
        key: FURNISH_TITLE_NOT,
        value: FURNISH_NOT
    },
    {
        key: FURNISH_TITLE_HALF,
        value: FURNISH_HALF
    },
    {
        key: FURNISH_TITLE_FULL,
        value: FURNISH_FULL
    }
]