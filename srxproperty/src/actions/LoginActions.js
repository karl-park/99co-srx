import {Navigation} from 'react-native-navigation';
import {Alert} from 'react-native';
import analytics from '@react-native-firebase/analytics';
import {LoginManager} from 'react-native-fbsdk';
import CookieManager from '@react-native-community/cookies';
import {AlertMessage, LoadingState, AppConstant} from '../constants';
import {GoogleSignin, statusCodes} from '@react-native-community/google-signin';
import {
  UserUtil,
  CommonUtil,
  ObjectUtil,
  DebugUtil,
  ChatDatabaseUtil,
  ShortlistDatabaseUtil,
} from '../utils';
import {LOGIN_HAS_ERROR, SAVE_LOGIN_USER_PO, NOT_LOGIN} from './types';
import {LoginService} from '../services';
import {store} from '../store';
import {UserPO} from '../dataObject';
import {loadPropertyTrackers} from './MyPropertiesAction';
import {clearBusiness} from './CommunitiesBusinessAction';

//Need to be sorted by alphabetically after confirm finishing
const getErrorMessage = error => {
  return {type: LOGIN_HAS_ERROR, errorMessage: error};
};

const saveLoginUserPO = userPO => {
  return {type: SAVE_LOGIN_USER_PO, userPO: userPO};
};

const userNotLoggedIn = () => {
  return {type: NOT_LOGIN};
};

const saveEmailAndPassword = (username, password, facebookId) => {
  if (!ObjectUtil.isEmpty(username)) {
    UserUtil.saveUserEmail(username);
  }
  if (!ObjectUtil.isEmpty(password)) {
    UserUtil.saveUserPassword(password);
  }
  if (!ObjectUtil.isEmpty(facebookId)) {
    UserUtil.saveUserFBId(facebookId);
  }
};

//to be removed if not using
const isUserLoggedIn = () => {
  console.log(store.getState().loginData);
  const loginData = store.getState().loginData;
  if (!ObjectUtil.isEmpty(loginData)) {
    const {userPO} = loginData;
    return !ObjectUtil.isEmpty(userPO);
  }
  return false;
};

const login = ({username, password}) => {
  return dispatch => {
    //call login api
    LoginService.login({
      username,
      password,
    })
      .catch(error => {
        dispatch(getErrorMessage(error));
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (!ObjectUtil.isEmpty(response.user)) {
                const {user} = response;
                if (!ObjectUtil.isEmpty(user.encryptedUserId)) {
                  analytics().setUserId(user.encryptedUserId);
                  analytics().setUserProperty('email', username);

                  //save email and password in AsynStorage
                  //saveEmailAndPassword(username, password, null);

                  dispatch(saveLoginUserPO(new UserPO(response.user)));
                  UserUtil.saveLoginUserPO(new UserPO(response.user));
                  dispatch(loadPropertyTrackers());
                  // dispatch(loadUserProfile(user));
                } else if (!ObjectUtil.isEmpty(user.errorMsg)) {
                  Alert.alert(AlertMessage.ErrorMessageTitle, user.errorMsg);
                }
              }
            }
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
          dispatch(
            getErrorMessage(
              'An error occurred unexpectedly. Please try again later.',
            ),
          );
        }
      });
  };
};

const signup = (name, email, mobile, password, hasAgreedToPDPA) => {
  return dispatch => {
    LoginService.createNewAccount({
      name,
      password,
      email,
      mobile,
      hasAgreedToPDPA,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
        dispatch(getErrorMessage(error));
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (!ObjectUtil.isEmpty(response.user)) {
                const {user} = response;
                if (!ObjectUtil.isEmpty(user.encryptedUserId)) {
                  analytics().setUserId(user.encryptedUserId);
                  analytics().setUserProperty('email', email);

                  //save email and password in AsynStorage
                  //saveEmailAndPassword(email, password, null);
                  dispatch(saveLoginUserPO(new UserPO(response.user)));
                  UserUtil.saveLoginUserPO(new UserPO(response.user));
                }
              }
            }
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
          dispatch(
            getErrorMessage(
              'An error occurred unexpectedly. Please try again later.',
            ),
          );
        }
      });
  };
};

const fblogin = ({name, facebookId, username}) => {
  return dispatch => {
    LoginService.fbLogin({
      name,
      facebookId,
      username,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
        dispatch(getErrorMessage(error));
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (!ObjectUtil.isEmpty(response.user)) {
                const {user} = response;
                if (!ObjectUtil.isEmpty(user.encryptedUserId)) {
                  analytics().setUserId(user.encryptedUserId);
                  analytics().setUserProperty('email', username);

                  //save email and password in AsynStorage
                  //saveEmailAndPassword(username, null, facebookId);

                  dispatch(saveLoginUserPO(new UserPO(response.user)));
                  UserUtil.saveLoginUserPO(new UserPO(response.user));
                  dispatch(loadPropertyTrackers());
                  // dispatch(loadUserProfile(user));
                }
              }
            }
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
          dispatch(
            getErrorMessage(
              'An error occurred unexpectedly. Please try again later.',
            ),
          );
        }
      });
  };
};

const appleSignIn = ({name, identityToken, appleUser, email}) => {
  return dispatch => {
    LoginService.signInWithApple({
      name,
      socialIdToken: identityToken,
      socialId: appleUser,
      email,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
        dispatch(getErrorMessage(error));
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (!ObjectUtil.isEmpty(response.user)) {
                const {user} = response;
                if (!ObjectUtil.isEmpty(user.encryptedUserId)) {
                  analytics().setUserId(user.encryptedUserId);
                  analytics().setUserProperty('email', email);

                  //save email and password in AsynStorage
                  //saveEmailAndPassword(username, null, facebookId);

                  dispatch(saveLoginUserPO(new UserPO(response.user)));
                  UserUtil.saveLoginUserPO(new UserPO(response.user));
                  dispatch(loadPropertyTrackers());
                  // dispatch(loadUserProfile(user));
                }
              }
            }
          }
        } else {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            'An error occurred unexpectedly. Please try again later.',
          );
          dispatch(
            getErrorMessage(
              'An error occurred unexpectedly. Please try again later.',
            ),
          );
        }
      });
  };
};

const performGoogleSignin = ({
  socialId,
  socialIdToken,
  name,
  username,
  photoUrl,
}) => {
  return dispatch => {
    LoginService.socialLogin({
      socialId,
      socialIdToken,
      name,
      username,
      type: 'GOOGLE',
      photoURL: photoUrl,
    })
      .catch(error => {
        dispatch(getErrorMessage(error));
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          } else if (!ObjectUtil.isEmpty(response.user)) {
            const {user} = response;
            if (!ObjectUtil.isEmpty(user.encryptedUserId)) {
              analytics().setUserId(user.encryptedUserId);
              analytics().setUserProperty('email', username);

              dispatch(saveLoginUserPO(new UserPO(response.user)));
              UserUtil.saveLoginUserPO(new UserPO(response.user));
              dispatch(loadPropertyTrackers());
            }
          }
        } else {
          dispatch(
            getErrorMessage(
              'An error occurred unexpectedly. Please try again later.',
            ),
          );
        }
      });
  };
};

const loginWithPtUserId = ({ptUserId, email, mobile}) => {
  return dispatch => {
    LoginService.loginWithPtUserId({
      ptUserId,
      email,
      mobile,
    })
      .then(response => {
        //{result: "success"}
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          }
        }
      })
      .catch(error => {
        dispatch(
          getErrorMessage(
            'An error occurred unexpectedly. Please try again later.',
          ),
        );
      });
  };
};

const logoutUser = keepCookies => {
  return dispatch => {
    LoginManager.logOut();
    LoginService.unregisterAPNSToken();

    this.googleSignOut();

    // DebugUtil.removeChosenAppDomainURL();
    ChatDatabaseUtil.deleteAll();
    // clearing cookies when user logging out
    // set keepCookies = true when wanna keep the cookies
    // this parameter is added to keep the cookies while switching server
    if (!keepCookies) {
      // clear cookies
      CookieManager.clearAll().then(success => {
        console.log('CookieManager.clearAll =>', success);
      });
    }
    dispatch(clearBusiness());
    UserUtil.clearUserDataWhileLogout().then(() => {
      dispatch(saveLoginUserPO(null));
    });
  };
};

const loadUserProfile = userPO => {
  return dispatch => {
    LoginService.loadUserProfile({
      encryptedUserId: userPO.encryptedUserId,
      username: userPO.email,
    })
      .catch(error => {
        dispatch(getErrorMessage(error));
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            dispatch(getErrorMessage(error));
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const userPO = new UserPO(response.result);
              dispatch(saveLoginUserPO(userPO));
            }
          }
        }
      });
  };
};

const updateUserPO = userPO => {
  return dispatch => {
    if (!ObjectUtil.isEmpty(userPO)) {
      UserUtil.saveLoginUserPO(new UserPO(userPO));
      dispatch(saveLoginUserPO(new UserPO(userPO)));
    }
  };
};

const updateEmailAndPassword = (username, password, userPO) => {
  return dispatch => {
    //update email and password and userPO also
    saveEmailAndPassword(username, password, null);
    dispatch(saveLoginUserPO(new UserPO(userPO)));
  };
};

const notLoggedIn = () => {
  return dispatch => {
    dispatch(userNotLoggedIn());
  };
};

//google sign out
googleSignOut = async () => {
  try {
    await GoogleSignin.signOut();
  } catch (error) {
    console.error('error in google logged out' + error);
  }
};

export {
  login,
  signup,
  fblogin,
  appleSignIn,
  isUserLoggedIn,
  loginWithPtUserId,
  logoutUser,
  loadUserProfile,
  updateUserPO,
  updateEmailAndPassword,
  notLoggedIn,
  performGoogleSignin,
};
