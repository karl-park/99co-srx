import request, {multiPartRequest} from './axios';
import {store} from '../store';
import {ObjectUtil} from '../utils';

function createNewAccount({name, password, email, mobile, hasAgreedToPDPA}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'createNewAccount',
      name,
      password,
      email,
      mobile,
      hasAgreedToPDPA,
    },
  });
}

function fbLogin({name, facebookId, username}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'FBLogin',
      name,
      facebookId,
      username,
    },
  });
}

function signInWithApple({name, socialIdToken, socialId, email}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'socialLogin',
      type: 'APPLE',
      name,
      socialId,
      socialIdToken,
      username: email,
    },
  });
}

function findByMobile({mobile, postalCode}) {
  return request({
    url: '/srx/tracker/findByMobile/propertyTracker.srx',
    method: 'POST',
    params: {
      mobile,
      postalCode,
    },
  });
}

function forgotPassword({email}) {
  return request({
    url: '/srx/user/forgotPassword/user.srx',
    method: 'POST',
    params: {
      email,
    },
  });
}

function loadUserProfile({encryptedUserId, username}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'GET',
    params: {
      action: 'loadUserProfile',
      encryptedUserId,
      username,
    },
  });
}

function loadUserSettings() {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'loadUserSettings',
    },
  });
}

function login({username, password}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'login',
      username,
      password,
    },
  });
}

function registerAPNSToken({token}) {
  return request({
    url: '/mobile/iphone/consumer2.mobile3',
    method: 'POST',
    params: {
      action: 'registerAPNSToken',
      token,
    },
  });
}

function requestMobileVerification({countryCode, mobile}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'requestMobileVerificationV2',
      countryCode,
      mobile,
    },
  });
}

function sendProfessionalHelp({callbackInd, message}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'sendProfessionalHelp',
      callbackInd,
      message,
    },
  });
}

function sendProfessionalHelpNologin({
  name,
  email,
  mobile,
  callbackInd,
  message,
}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'sendProfessionalHelpNologin',
      name,
      email,
      mobile,
      callbackInd,
      message,
    },
  });
}

function socialLogin({
  socialId,
  socialIdToken,
  name,
  username,
  type,
  photoURL,
}) {
  return request({
    url: '/mobile/iphone/consumer.mobile3?',
    method: 'POST',
    params: {
      action: 'socialLogin',
      socialId,
      socialIdToken,
      name,
      username,
      type,
      photoURL,
    },
  });
}

function unregisterAPNSToken() {
  const userPO = store.getState().loginData.userPO;
  if (!ObjectUtil.isEmpty(userPO)) {
    return request({
      url: '/mobile/iphone/consumer2.mobile3',
      method: 'POST',
      params: {
        action: 'unRegisterAPNSToken',
      },
    });
  }
}

function updateUserAccount({
  name,
  hasAgreedToPDPA,
  isUpdatingPwd,
  oldPwd,
  newPwd,
  communityAlias,
}) {
  let parameters = {
    name,
    hasAgreedToPDPA,
    isUpdatingPwd,
    oldPwd,
    newPwd,
  };

  if (!ObjectUtil.isEmpty(communityAlias)) {
    parameters.communityAlias = communityAlias;
  }
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'updateUserAccount',
      // username,//do not set username manually
      ...parameters,
    },
  });
}

function updateUserSettings({
  enewsletterInd,
  promotionsInd,
  communityMessagesInd,
}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'updateUserSettings',
      enewsletterInd,
      promotionsInd,
      communityMessagesInd,
    },
  });
}

function verifyMobile({verificationCode, countryCode, mobile}) {
  return request({
    url: '/mobile/iphone/consumer3.mobile3?',
    method: 'POST',
    params: {
      action: 'verifyMobile',
      verificationCode,
      countryCode,
      mobile,
    },
  });
}

function updateProfilePhoto({photo}) {
  let params = {
    action: 'updatePhoto',
    filename: photo,
  };
  return multiPartRequest('/api/v1/user/info', params);
}

function removePhoto() {
  return request({
    url: '/api/v1/user/info',
    method: 'POST',
    params: {
      action: 'removePhoto',
    },
  });
}

function vetCommunityAlias({communityAlias}) {
  return request({
    url: '/api/v1/user/info',
    method: 'GET',
    params: {
      action: 'vetCommunityAlias',
      communityAlias,
    },
  });
}

function loginWithPtUserId({ptUserId, email, mobile}) {
  return request({
    url: '/srx/tracker/login/propertyTracker.srx',
    method: 'POST',
    params: {
      ptUserId,
      email,
      mobile,
    },
  });
}

const LoginService = {
  createNewAccount,
  fbLogin,
  signInWithApple,
  findByMobile,
  forgotPassword,
  loadUserProfile,
  loadUserSettings,
  login,
  registerAPNSToken,
  requestMobileVerification,
  sendProfessionalHelp,
  sendProfessionalHelpNologin,
  socialLogin,
  unregisterAPNSToken,
  updateUserAccount,
  updateUserSettings,
  verifyMobile,
  updateProfilePhoto,
  removePhoto,
  vetCommunityAlias,
  loginWithPtUserId,
};

export {LoginService};
