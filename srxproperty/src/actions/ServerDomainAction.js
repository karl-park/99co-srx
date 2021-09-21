import {UPDATE_SERVER_DOMAIN} from './types';
import {DebugUtil, ObjectUtil} from '../utils';
import {AppConstant} from '../constants';
import CookieManager from '@react-native-community/cookies';
import {logoutUser} from './LoginActions';
import { clearBusiness } from './CommunitiesBusinessAction';

const retrieveDomainUrl = () => {
  return dispatch => {
    DebugUtil.getChosenAppDomainURL().then(result => {
      if (result) {
        console.log('Chosen App Domain URL in Action' + result);
        dispatch({type: UPDATE_SERVER_DOMAIN, payload: result});
      }
    });
  };
};

const updateDomainUrl = ({newDomainUrl}) => {
  return dispatch => {
    DebugUtil.setChosenAppDomainURL({newDomainUrl}).then(result => {
      console.log('Updated Domain ' + result);
      dispatch({type: UPDATE_SERVER_DOMAIN, payload: result});
      CookieManager.get(newDomainUrl).then(cookies => {
        const userSessionTokenCookie = cookies['userSessionToken'];
        if (ObjectUtil.isEmpty(userSessionTokenCookie)) {
          dispatch(logoutUser(true));
        } else {
          dispatch(clearBusiness());
        }
      });
    });
  };
};

const clearDomainUrl = () => {
  return dispatch => {
    DebugUtil.removeChosenAppDomainURL().then(() => {
      dispatch({type: UPDATE_SERVER_DOMAIN, payload: AppConstant.DomainUrl});
    });
  };
};

export {retrieveDomainUrl, updateDomainUrl, clearDomainUrl};
