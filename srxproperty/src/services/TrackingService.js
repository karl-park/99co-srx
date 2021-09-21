import request from './axios';
import {store} from '../store';
import {ObjectUtil} from '../utils';
import DeviceInfo from 'react-native-device-info';

leadTracking = ({
  //all required
  subject,
  leadSource,
  feature,
  medium,
  encryptedToUserId,
}) => {
  /**
   * subject
   * 12 - Consumer
   */

  /**
   * platform
   * 3 - Consumer App
   */

  /**
   * leadSource
   * 8 - Agent CV
   */

  /**
   * feature
   * 12 - Contact Agent
   */

  /**
   * medium
   * 3 - call, 1 - Sms, 6 - whatsapp
   */

  var encryptedUserId;
  const userPO = store.getState().loginData.userPO;
  if (!ObjectUtil.isEmpty(userPO)) {
    encryptedUserId = userPO.encryptedUserId;
  }

  return request({
    url: 'mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'leadTracking',
      subject,
      platform: '3',
      leadSource,
      feature,
      medium,
      encryptedFromUserId: encryptedUserId,
      encryptedToUserId, //agentUserId, no need to be encrypted from front end
    },
  });
};

saveLead = ({ptUserId, postalCode}) => {
  return request({
    url: '/srx/tracker/saveLead/propertyTracker.srx',
    method: 'POST',
    params: {
      action: '7',
      ptUserId,
      postalCode,
      cdLeadSourceId: '55',
      userToken: DeviceInfo.getUniqueId(),
    },
  });
};

trackAgentAdViewMobile = ({
  //all required
  agentUserId,
  agentBookingId,
  viewId,
}) => {
  return request({
    url: '/srx/agent/trackAgentAdViewMobile/redefinedSearch.srx',
    method: 'POST',
    params: {
      agentUserId,
      agentBookingId,
      viewId,
      mobileInd: true,
      tag: '',
    },
  });
};

trackListingCallSMSOrWhatsapp = ({
  //all required
  listingId,
  type,
}) => {
  return request({
    url: '/srx/trackListingCallSMSOrWhatsapp/listing.srx',
    method: 'POST',
    params: {
      listingId,
      type, //C - call, S - Sms, W - whatsapp
      source: 'A', //A - App, M - Mobile (Web), W - Web
    },
  });
};

const TrackingService = {
  leadTracking,
  saveLead,
  trackAgentAdViewMobile,
  trackListingCallSMSOrWhatsapp,
};

export {TrackingService};
