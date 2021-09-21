import {combineReducers} from 'redux';
import ShortlistedItemsReducer from './ShortlistedItemsReducer';
import CommunitiesBusinessReducer from './CommunitiesBusinessReducer';
import CommunitiesReducer from './CommunitiesReducer';
import LoginReducer from './LoginReducer';
import MyPropertiesReducer from './MyPropertiesReducer';
import ViewlistedItemsReducer from './ViewlistedItemsReducer';
import ServerDomainReducer from './ServerDomainReducer';

export default combineReducers({
  shortlistData: ShortlistedItemsReducer,
  loginData: LoginReducer,
  myPropertiesData: MyPropertiesReducer,
  viewlistData: ViewlistedItemsReducer,
  serverDomain: ServerDomainReducer,
  communitiesData: CommunitiesReducer,
  communitiesBusinessData: CommunitiesBusinessReducer,
});
