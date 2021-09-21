import request from './axios';
import {Platform} from 'react-native';
import {PropertyTypeUtil} from '../utils';

function promotionGetXValue({
  type,
  postalCode,
  floorNum,
  unitNum,
  buildingNum,
  subType,
  landType,
  streetId,
  size,
  builtSizeGfa,
  pesSize,
  tenure,
  builtYear,
}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'promotionGetXValue',
      type,
      postalCode,
      floorNum: floorNum ?? '',
      unitNum: unitNum ?? '',
      buildingNum,
      subType,
      landType,
      id: streetId,
      size,
      builtSizeGfa,
      pesSize,
      tenure,
      builtYear,
      cdApp: '1', //1 = Home Search App
      cdPlatform: Platform.OS == 'ios' ? '0' : '1', //0 = ios, 1 = android
    },
  });
}

function promotionGetXValueTrend({
  type,
  postalCode,
  floorNum,
  unitNum,
  buildingNum,
  subType,
  landType,
  streetId,
  size,
  builtSizeGfa,
  pesSize,
  tenure,
  builtYear,
}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'promotionGetXValueTrend',
      type,
      postalCode,
      floorNum: floorNum ?? '',
      unitNum: unitNum ?? '',
      buildingNum,
      subType,
      landType,
      id: streetId,
      size,
      builtSizeGfa,
      pesSize,
      tenure,
      builtYear,
      landedXValue: PropertyTypeUtil.isLanded(subType) ? 'Y' : 'N',
      cdApp: '1', //1 = Home Search App
      cdPlatform: Platform.OS == 'ios' ? '0' : '1', //0 = ios, 1 = android
    },
  });
}

function getXValueFeaturedAgent({postalCode, crsId}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3',
    method: 'POST',
    params: {
      action: 'getXValueFeaturedAgent',
      postalCode,
      crsId,
    },
  });
}

function loadXValueProjectListings({projectId, isSale, cdResearchSubTypes}) {
  return request({
    url: '/srx/listing/loadListings2/redefinedSearch.srx',
    method: 'POST',
    params: {
      action: 'loadProjectListings',
      projectId,
      searchType: 'feature',
      cdResearchSubTypes,
      isSale,
      channelsFilter: 4,
    },
  });
}

//to be moved to another file
function trackAgentAdViewMobileInXValue({agentPO, source, viewId}) {
  return request({
    url: '/srx/agent/trackAgentAdViewMobile/redefinedSearch.srx',
    method: 'POST',
    params: {
      agentUserId: agentPO.userId,
      agentBookingId: agentPO.trackingId,
      viewId,
      mobileInd: true,
      source,
      tag: '',
    },
  });
}

const XValueService = {
  promotionGetXValue,
  promotionGetXValueTrend,
  getXValueFeaturedAgent,
  loadXValueProjectListings,
  trackAgentAdViewMobileInXValue,
};

export {XValueService};
