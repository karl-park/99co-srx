const TENURE_FREEHOLD_999_YRS = 1;
const TENURE_TITLE_FREEHOLD_999_YRS = "Freehold";

const TENURE_LEASEHOLD_99_YRS = 2;
const TENURE_TITLE_LEASEHOLD_99_YRS = "Leasehold-99";

const TENURE_LEASEHOLD_999_YRS = 3;
const TENURE_TITLE_LEASEHOLD_999_YRS = "Leasehold-999";

//Added More Tenure list for Filter By website
const TENURE_LEASEHOLD_60_YRS = 4;
const TENURE_TITLE_LEASEHOLD_60_YRS = "Leasehold-60";

const TENURE_LEASEHOLD_30_YRS = 5;
const TENURE_TITLE_LEASEHOLD_30_YRS = "Leasehold-30";

const TENURE_LEASEHOLD_103_YRS = 6;
const TENURE_TITLE_LEASEHOLD_103_YRS = "Leasehold-103";

export const TENURE_ARRAY = [
  {
    key: TENURE_TITLE_FREEHOLD_999_YRS,
    value: TENURE_FREEHOLD_999_YRS
  },
  {
    key: TENURE_TITLE_LEASEHOLD_99_YRS,
    value: TENURE_LEASEHOLD_99_YRS
  }
];

export const TENURE_FILTER_ARRAY = [
  {
    key: TENURE_TITLE_FREEHOLD_999_YRS,
    value: TENURE_FREEHOLD_999_YRS
  },
  {
    key: TENURE_TITLE_LEASEHOLD_99_YRS,
    value: TENURE_LEASEHOLD_99_YRS
  },
  {
    key: TENURE_TITLE_LEASEHOLD_60_YRS,
    value: TENURE_LEASEHOLD_60_YRS
  },
  {
    key: TENURE_TITLE_LEASEHOLD_30_YRS,
    value: TENURE_LEASEHOLD_30_YRS
  }
];

export const TenureType = {
  Freehold_999_years: TENURE_FREEHOLD_999_YRS,
  Leasehold_99_years: TENURE_LEASEHOLD_99_YRS,
  Leasehold_999_years: TENURE_LEASEHOLD_999_YRS,
  Leasehold_60_years: TENURE_LEASEHOLD_60_YRS,
  Leasehold_30_years: TENURE_LEASEHOLD_30_YRS,
  Leasehold_103_years: TENURE_LEASEHOLD_103_YRS
};

export const TenureTypeDescription = {
  Freehold_999_years: TENURE_TITLE_FREEHOLD_999_YRS,
  Leasehold_99_years: TENURE_TITLE_LEASEHOLD_99_YRS,
  Leasehold_999_years: TENURE_TITLE_LEASEHOLD_999_YRS,
  Leasehold_60_years: TENURE_TITLE_LEASEHOLD_60_YRS,
  Leasehold_30_years: TENURE_TITLE_LEASEHOLD_30_YRS,
  Leasehold_103_years: TENURE_TITLE_LEASEHOLD_103_YRS
};
