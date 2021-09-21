//Leads
//For Google Analytics

const LeadTypes = {
  call: "Call",
  SSM: "SSM",
  whatsapp: "WhatsApp",
  email: "Email",
  sms: "SMS",
  EnquirySubmission: "Submit Contact form"
};

const LeadSources = {
  /**
   * Listings :
   * If going to enquiry form by selecting listings
   * then use "listings"
   * even if selecting listings from agentcv, use "lisitngs"
   */
  listings: "Listings",
  listingDetails: "ListingDetails",
  agentCV: "AgentCV",
  featuredListingPlus: "FeaturedListingPlus",
  communities: "Communities"
};

const ListingDetailsViewingItems = {
  listingMedias: "Listing Photos/ Drone/ v360",
  keyInformation: "Key Information",
  description: "Description",
  facilities: "Facilities",
  homeValueEstimator: "Home Value Estimator (X-value)",
  mortgageCalculator: "Mortgage Calculator",
  similarLiistings: "Similar Listings",
  contactAnAgent: "Contact An Agent"
};

export { LeadTypes, LeadSources, ListingDetailsViewingItems };
