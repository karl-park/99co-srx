/**
 * Server values
 */
const pt_Occupancy = {
  Own: 1,
  Rented: 2,
  Interested: 3
};

const pt_OwnerOption = {
  LookingForBuyer: 1,
  LookingForTenant: 2
};

const pt_Purpose = {
  Residence: 1,
  Investment: 2
};

const pt_Occupancy_Array = [
  {
    key: "I own this property",
    value: pt_Occupancy.Own
  },
  {
    key: "I have rented this property",
    value: pt_Occupancy.Rented
  },
  {
    key: "I'm interested in this property",
    value: pt_Occupancy.Interested
  }
];

const pt_Purpose_Array = [
  {
    key: "Residence",
    value: pt_Purpose.Residence
  },
  {
    key: "Investment",
    value: pt_Purpose.Investment
  }
];
export {
  pt_Occupancy,
  pt_OwnerOption,
  pt_Purpose,
  pt_Occupancy_Array,
  pt_Purpose_Array
};
