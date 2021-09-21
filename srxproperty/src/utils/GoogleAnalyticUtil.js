import analytics from '@react-native-firebase/analytics';
import {ObjectUtil} from './ObjectUtil';
import {PropertyTypeUtil} from './PropertyTypeUtil';
import {SearchPropertyService} from '../services';
import {UserType, LeadSources} from '../constants';

function trackListingSearch({parameters}) {
  /**
   * required to convert it to readable parameters
   * Not tracking all parameters
   * as there is a cap of 25 parameters
   * and cap of 100 characters per parameters
   */

  const newParameters = {};
  if (parameters) {
    const {
      cdResearchSubTypes,
      type,
      searchText,
      searchType,
      selectedAmenitiesIds,
      selectedDistrictIds,
      selectedHdbTownIds,
      isRoomRental,
      startResultIndex,
      isTransacted,
      ...rest
    } = parameters;

    //page
    newParameters['page'] =
      startResultIndex / SearchPropertyService.maxPerCount + 1; //start from 1

    newParameters['isTransacted'] = isTransacted;

    //sale or rent
    newParameters['type'] = type;
    if (type === 'R' && isRoomRental) {
      newParameters['isRoomRental'] = isRoomRental;
    }

    //for location
    if (searchType) {
      newParameters['searchType'] = searchType;
    }

    if (searchText) {
      newParameters['searchText'] = searchText;
    }

    if (selectedAmenitiesIds) {
      newParameters['selectedAmenitiesIds'] = selectedAmenitiesIds;
    }

    if (selectedDistrictIds) {
      newParameters['selectedDistrictIds'] = selectedDistrictIds;
    }

    if (selectedHdbTownIds) {
      newParameters['selectedHdbTownIds'] = selectedHdbTownIds;
    }

    //handle property type
    if (!ObjectUtil.isEmpty(cdResearchSubTypes)) {
      const subtypes = cdResearchSubTypes.split(',');

      var containsCondo = false;
      var containsHDB = false;
      var containsLanded = false;
      var containsCommercial = false;

      subtypes.map(item => {
        if (!containsCondo && PropertyTypeUtil.isCondo(item)) {
          containsCondo = true;
        } else if (!containsHDB && PropertyTypeUtil.isHDB(item)) {
          containsHDB = true;
        } else if (!containsLanded && PropertyTypeUtil.isLanded(item)) {
          containsLanded = true;
        } else if (!containsCommercial && PropertyTypeUtil.isCommercial(item)) {
          containsCommercial = true;
        }
      });

      const propertyTypeCollection = [];
      if (containsCondo && containsHDB && containsLanded) {
        propertyTypeCollection.push('Residential');
      } else {
        if (containsCondo) {
          propertyTypeCollection.push('Condominium');
        }
        if (containsHDB) {
          propertyTypeCollection.push('HDB');
        }
        if (containsLanded) {
          propertyTypeCollection.push('Landed');
        }
      }
      if (containsCommercial) {
        propertyTypeCollection.push('Commercial');
      }

      newParameters.propertyType = propertyTypeCollection.join(',');
      newParameters.cdResearchSubTypes = cdResearchSubTypes;
    }
  }

  console.log(newParameters);
  analytics().logEvent('Search_Listings', newParameters);
}

function trackMapListingSearch({parameters}) {
  /**
   * required to convert it to readable parameters
   * Not tracking all parameters
   * as there is a cap of 25 parameters
   * and cap of 100 characters per parameters
   */

  const newParameters = {};
  if (parameters) {
    const {
      cdResearchSubTypes,
      type,
      searchText,
      searchType,
      selectedAmenitiesIds,
      selectedDistrictIds,
      selectedHdbTownIds,
      isRoomRental,
      isTransacted,
      ...rest
    } = parameters;

    newParameters['isTransacted'] = isTransacted;

    //sale or rent
    newParameters['type'] = type;
    if (type === 'R' && isRoomRental) {
      newParameters['isRoomRental'] = isRoomRental;
    }

    //for location
    newParameters['searchType'] = !ObjectUtil.isEmpty(searchText)
      ? 'keyword'
      : 'feature'; //check ListingSearchManager for logic

    if (searchText) {
      newParameters['searchText'] = searchText;
    }

    if (selectedAmenitiesIds) {
      newParameters['selectedAmenitiesIds'] = selectedAmenitiesIds;
    }

    if (selectedDistrictIds) {
      newParameters['selectedDistrictIds'] = selectedDistrictIds;
    }

    if (selectedHdbTownIds) {
      newParameters['selectedHdbTownIds'] = selectedHdbTownIds;
    }

    //handle property type
    if (!ObjectUtil.isEmpty(cdResearchSubTypes)) {
      const subtypes = cdResearchSubTypes.split(',');

      var containsCondo = false;
      var containsHDB = false;
      var containsLanded = false;
      var containsCommercial = false;

      subtypes.map(item => {
        if (!containsCondo && PropertyTypeUtil.isCondo(item)) {
          containsCondo = true;
        } else if (!containsHDB && PropertyTypeUtil.isHDB(item)) {
          containsHDB = true;
        } else if (!containsLanded && PropertyTypeUtil.isLanded(item)) {
          containsLanded = true;
        } else if (!containsCommercial && PropertyTypeUtil.isCommercial(item)) {
          containsCommercial = true;
        }
      });

      const propertyTypeCollection = [];
      if (containsCondo && containsHDB && containsLanded) {
        propertyTypeCollection.push('Residential');
      } else {
        if (containsCondo) {
          propertyTypeCollection.push('Condominium');
        }
        if (containsHDB) {
          propertyTypeCollection.push('HDB');
        }
        if (containsLanded) {
          propertyTypeCollection.push('Landed');
        }
      }
      if (containsCommercial) {
        propertyTypeCollection.push('Commercial');
      }

      newParameters.propertyType = propertyTypeCollection.join(',');
      newParameters.cdResearchSubTypes = cdResearchSubTypes;
    }
  }

  console.log(newParameters);
  analytics().logEvent('Search_Listings_Map', newParameters);
}

function trackListingDetailsUserActions({viewingItem}) {
  //track when viewing the item or expanding the section
  /**
   * viewingItem
   * Use ListingDetailsViewingItems from constant folder
   */
  if (viewingItem) {
    const newParameters = {
      viewingItem,
    };
    console.log(newParameters);
    analytics().logEvent('ListingDetails_ViewingItems', newParameters);
  }
}

function trackLeads({leadType, source}) {
  /**
   * leadType
   * Use LeadTypes from constant folder
   */

  /**
   * source
   * Use LeadSources from constant folder
   */
  if (leadType && source) {
    const newParameters = {
      leadType,
    };

    let eventName;
    if (source === LeadSources.listings) {
      eventName = 'Leads_Listings';
    } else if (source === LeadSources.listingDetails) {
      eventName = 'Leads_ListingDetails';
    } else if (source === LeadSources.agentCV) {
      eventName = 'Leads_AgentCV';
    }
    console.log(eventName);
    console.log(newParameters);
    if (eventName) {
      console.log(eventName);
      console.log(newParameters);
      analytics().logEvent(eventName, newParameters);
    }
  }
}

//Track for concierge form submission
function trackConciergeSubmission({parameters}) {
  /**
   * required to convert it to readable parameters
   * Not tracking all parameters
   * as there is a cap of 25 parameters
   * and cap of 100 characters per parameters
   */

  const newParameters = {};
  if (parameters) {
    const {
      conciergeMenu,
      userType,
      propertyType,
      districtTownId,
      keywords,
    } = parameters;

    //start adding params
    if (conciergeMenu) {
      newParameters['conciergeMenu'] = conciergeMenu;
    }

    newParameters['userType'] = UserType.getUserTypeDescription(userType);

    if (propertyType) {
      newParameters['propertyType'] = propertyType;
    }

    if (districtTownId) {
      newParameters['districtTownId'] = districtTownId;
    }

    if (keywords) {
      newParameters['keywords'] = keywords;
    }

    console.log(newParameters);
    analytics().logEvent('Concierge_Submission', newParameters);
  }
}

function trackShortlisting({parameters}) {
  /**
   * required to convert it to readable parameters
   * Not tracking all parameters
   * as there is a cap of 25 parameters
   * and cap of 100 characters per parameters
   */

  const newParameters = {};
  if (parameters) {
    const {
      cdResearchSubType,
      districtHdbTown,
      postalDistrictId,
      postalHdbTownId,
      propertyType,
      type,
    } = parameters;

    //start adding params
    newParameters['type'] = type; //sale or rent
    if (cdResearchSubType) {
      newParameters['cdResearchSubType'] = cdResearchSubType;
    }

    if (districtHdbTown) {
      newParameters['districtHdbTown'] = districtHdbTown;
    }

    if (postalDistrictId) {
      newParameters['postalDistrictId'] = postalDistrictId;
    }

    if (postalHdbTownId) {
      newParameters['postalHdbTownId'] = postalHdbTownId;
    }

    if (propertyType) {
      newParameters['propertyType'] = propertyType;
    }
    console.log(newParameters);
    analytics().logEvent('Shortlist_Listings', newParameters);
  }
}

function trackXValueRequest({parameters}) {
  /**
   * required to convert it to readable parameters
   * Not tracking all parameters
   * as there is a cap of 25 parameters
   * and cap of 100 characters per parameters
   */

  const newParameters = {};
  if (parameters) {
    const {
      type, //S or R
      subType,
    } = parameters;

    //   //start adding params
    newParameters['type'] = type; //sale or rent

    if (PropertyTypeUtil.isHDB(subType)) {
      newParameters['propertyType'] = 'HDB';
    } else if (PropertyTypeUtil.isCondo(subType)) {
      newParameters['propertyType'] = 'Condominium';
    } else if (PropertyTypeUtil.isLanded(subType)) {
      newParameters['propertyType'] = 'Landed';
    } else if (PropertyTypeUtil.isCommercial(subType)) {
      newParameters['propertyType'] = 'Commercial';
    } else {
      newParameters['propertyType'] = 'Unknown';
    }

    console.log(newParameters);
    analytics().logEvent('Request_XValue', newParameters);
  }
}

function trackCommunityActivity({parameters}) {
  /**
   * required to convert it to readable parameters
   * Not tracking all parameters
   * as there is a cap of 25 parameters
   * and cap of 100 characters per parameters
   */
  const newParameters = {};
  if (parameters) {
    const {type} = parameters; //type Like, Dislike, Comment, Share, Post, Detail
    newParameters['type'] = type;

    if(type === "Shiok"){
      newParameters['type'] = "Communities_Like";
    } else if(type ==="Jialat"){
      newParameters['type'] = "Communities_Dislike";
    } else if(type ==="comment"){
      newParameters['type'] = "Communities_Comment";
    }else if(type ==="share"){
      newParameters['type'] = "Communities_Share";
    }else if(type ==="post"){
      newParameters['type'] = "Communities_Post";
      const {hasGIF, hasImage, hasText} = parameters;
      newParameters['hasText'] = hasText;
      newParameters['hasImage'] = hasImage;
      newParameters['hasGIF'] = hasGIF;
    }else if(type === "detail"){
      newParameters['type'] = "Communities_Detail";
    }

    console.log("track community activity");
    console.log(newParameters);

    analytics().logEvent('Communities_Activity', newParameters);
  }
}

const GoogleAnalyticUtil = {
  trackListingSearch,
  trackMapListingSearch,
  trackListingDetailsUserActions,
  trackLeads,
  trackConciergeSubmission,
  trackShortlisting,
  trackXValueRequest,
  trackCommunityActivity,
};

export {GoogleAnalyticUtil};
