const LEASE_TERM_FLEXIBLE = 4;
const LEASE_TERM_TITLE_FLEXIBLE = "Flexible";

const LEASE_TERM_SHORT_TERM = 1;
const LEASE_TERM_TITLE_SHORT_TERM = "Short-term";

const LEASE_TERM_12_TO_23_MONTHS = 2;
const LEASE_TERM_TITLE_12_TO_23_MONTHS = "12 months to 23 months";

const LEASE_TERM_ABOVE_24_MONTHS = 3;
const LEASE_TERM_TITLE_ABOVE_24_MONTHS = "> 24 months";

export const LeaseTermOption = {
  flexible: LEASE_TERM_FLEXIBLE,
  shortTerm: LEASE_TERM_SHORT_TERM,
  between_12_and_23_months: LEASE_TERM_12_TO_23_MONTHS,
  above_24_months: LEASE_TERM_ABOVE_24_MONTHS
};

export const LeaseTermOptionDescription = {
  flexible: LEASE_TERM_TITLE_FLEXIBLE,
  shortTerm: LEASE_TERM_TITLE_SHORT_TERM,
  between_12_and_23_months: LEASE_TERM_TITLE_12_TO_23_MONTHS,
  above_24_months: LEASE_TERM_TITLE_ABOVE_24_MONTHS
};

export const LEASE_TERM_ARRAY = [
  {
    key: LEASE_TERM_TITLE_FLEXIBLE,
    value: LEASE_TERM_FLEXIBLE
  },
  {
    key: LEASE_TERM_TITLE_SHORT_TERM,
    value: LEASE_TERM_SHORT_TERM
  },
  {
    key: LEASE_TERM_TITLE_12_TO_23_MONTHS,
    value: LEASE_TERM_12_TO_23_MONTHS
  },
  {
    key: LEASE_TERM_TITLE_ABOVE_24_MONTHS,
    value: LEASE_TERM_ABOVE_24_MONTHS
  }
];
