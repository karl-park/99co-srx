import {Navigation} from 'react-native-navigation';
import {
  //General
  WebScreen,
  //Splash Screen
  Intro,
  //Auto Complete
  AutoComplete,
  SuggestionAutoComplete,
  //user authentification
  SgHomeUpdateDetails,
  SgHomeSignInScreen,
  ForgotPassword,
  //property search
  AmenitiesScreen,
  EnquiryForm,
  ListingDetails,
  NewLaunchesProjectResult,
  SearchResultTopBar,
  PropertySearchResult,
  PropertySearchLocation,
  HDBEstatesOption,
  MRTSearchOption,
  DistrictsOption,
  SchoolOption,
  SchoolGroupList,
  TravelTimeOption,
  PropertyFilterOptions,
  //listing
  ListingSearchResultList,
  TransactedListingResultList,
  // Shortlist
  ShortlistedPage,
  //xvalue
  XValueForm,
  XValueResult,
  //conceirge
  ConciergeHomeScreen,
  AgentDirectory,
  AgentCV,
  ConciergeMainForm,
  AgentListingResult,
  AgentTransactionsResult,
  CredentialsAndAwards,
  //Chat
  ChatHomeScreen,
  ChatRoom,
  ChatRoomTopBar,
  PhotoWithCaption,
  //Side menu
  UpdateProfile,
  NeedHelp,
  Settings,
  VerifyMobileNumber,
  SwitchServerMain,
  PasswordModal,
  // home screen
  HomeScreen,
  //Menu
  MenuList,
  //UserProfile
  UserDashboard,

  //PropertyNews
  PropertyNewsHomeScreen,
  NewsDetails,
  NewsDetailsResultList,

  //My Properties
  MyPropertyDetails,
  MyPropertyForm,
  MyPropertyVerifyMobile,
  MyPropertyOTPVerification,
  VerifyOwnershipIntro,
  SignInRegister,

  //Communities
  CommunitiesNotification,
  SortPostModal,
  ShareModal,
  VerifyOTPModal,
  VerifyMobileModal,
  CommunitiesModal,
  SignInRegisterModal,
  AddPropertyModal,
  CommunitiesPostDetails,
  CommunitiesNewPost,
  SharePostWithComment,
  CommunityPostContactModal,
  CommunitiesReport,
  CommunityOptions,
  CommunitiesIntro,

  //GRC Business Center
  CommunitiesGRCNewPost,
  GRCMainScreen,
  GRCBusinessTopBar,
  CommunitySelection,
  CommunitiesCommentSortModal,
} from '../screens';
import {Enquiry, UploadOptionModal, OptionsSelectionModal} from '../components';

import {Provider} from 'react-redux';
import {store} from '../store';
import {AgentDirectoryTopBar} from '../screens/Concierge/Components/AgentDirectoryTopBar';
import {DetailActionOptionsModal} from '../screens/Communities/PostDetails/DetailsModal';

registerComponents = () => {
  /**
   * Components
   */
  Navigation.registerComponentWithRedux(
    'Enquiry.contactSheet',
    () => Enquiry,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'AndroidOptionsDialog.SelectionModal',
    () => OptionsSelectionModal,
  );
  /**
   * Splash Screen
   */
  Navigation.registerComponentWithRedux(
    'SplashScreen.Intro',
    () => Intro,
    Provider,
    store,
  );

  /**
   * User Authentification
   */
  Navigation.registerComponentWithRedux(
    'LoginStack.sgHomeUpdateDetailScreen',
    () => SgHomeUpdateDetails,
    Provider,
    store,
  );
  Navigation.registerComponentWithRedux(
    'LoginStack.sgHomeSignInScreen',
    () => SgHomeSignInScreen,
    Provider,
    store,
  );
  Navigation.registerComponent(
    'LoginStack.forgotPassword',
    () => ForgotPassword,
  );
  Navigation.registerComponentWithRedux(
    'LoginStack.signInRegister',
    () => SignInRegister,
    Provider,
    store,
  );

  Navigation.registerComponent('LoginStack.generalWebScreen', () => WebScreen);

  /**
   * Auto Complete
   */
  Navigation.registerComponent('AddressAutoComplete', () => AutoComplete);

  Navigation.registerComponent(
    'SuggestionAutoComplete',
    () => SuggestionAutoComplete,
  );

  Navigation.registerComponent(
    'CommunitiesNotification',
    () => CommunitiesNotification,
  );

  /**
   * Property Search + Home Screen
   */
  Navigation.registerComponent(
    'PropertySearchResult.TopBar',
    () => SearchResultTopBar,
  );

  Navigation.registerComponentWithRedux(
    'PropertySearchStack.searchResult',
    () => PropertySearchResult,
    Provider,
    store,
  );
  Navigation.registerComponent(
    'PropertySearchStack.PropertySearchLocation',
    () => PropertySearchLocation,
  );
  Navigation.registerComponentWithRedux(
    'PropertySearchStack.ListingDetails',
    () => ListingDetails,
    Provider,
    store,
  );
  Navigation.registerComponent(
    'PropertySearchStack.AmenitiesScreen',
    () => AmenitiesScreen,
  );

  Navigation.registerComponent(
    'PropertySearchStack.PropertyFilterOptions',
    () => PropertyFilterOptions,
  );

  Navigation.registerComponent(
    'PropertySearchStack.DistrictsOption',
    () => DistrictsOption,
  );
  Navigation.registerComponent(
    'PropertySearchStack.HDBEstatesOption',
    () => HDBEstatesOption,
  );
  Navigation.registerComponent(
    'PropertySearchStack.MRTSearchOption',
    () => MRTSearchOption,
  );
  Navigation.registerComponent(
    'PropertySearchStack.SchoolOption',
    () => SchoolOption,
  );
  Navigation.registerComponent(
    'PropertySearchStack.SchoolGroupList',
    () => SchoolGroupList,
  );
  Navigation.registerComponent(
    'PropertySearchStack.TravelTimeOption',
    () => TravelTimeOption,
  );
  /**
   * listings
   */

  Navigation.registerComponentWithRedux(
    'PropertySearchStack.ListingList',
    () => ListingSearchResultList,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'PropertySearchStack.TransactedListingList',
    () => TransactedListingResultList,
  );

  Navigation.registerComponent(
    'PropertySearchStack.NewLaunchesProjectResult',
    () => NewLaunchesProjectResult,
  );

  Navigation.registerComponent(
    'PropertySearchStack.generalWebScreen',
    () => WebScreen,
  );

  Navigation.registerComponentWithRedux(
    'PropertySearchStack.EnquiryForm',
    () => EnquiryForm,
    Provider,
    store,
  );

  /**
   * Shortlist
   */
  Navigation.registerComponentWithRedux(
    'ShortlistStack.shortlistedPage',
    () => ShortlistedPage,
    Provider,
    store,
  );

  /**
   * X-Value
   */
  //Navigation.registerComponent("xvalueStack.xvalueForm", () => XValueForm);

  Navigation.registerComponentWithRedux(
    'xvalueStack.xvalueForm',
    () => XValueForm,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'xvalueStack.xvalueResult',
    () => XValueResult,
    Provider,
    store,
  );

  /**
   * Concierge
   */

  Navigation.registerComponent(
    'ConciergeStack.homeScreen',
    () => ConciergeHomeScreen,
  );

  Navigation.registerComponent(
    'AgentDirectory.TopBar',
    () => AgentDirectoryTopBar,
  );

  Navigation.registerComponent(
    'ConciergeStack.AgentDirectory',
    () => AgentDirectory,
  );

  Navigation.registerComponentWithRedux(
    'ConciergeStack.AgentCV',
    () => AgentCV,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'ConciergeStack.AgentListingResult',
    () => AgentListingResult,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'ConciergeStack.AgentTransactionsResult',
    () => AgentTransactionsResult,
  );

  Navigation.registerComponent(
    'ConciergeStack.CredentialsAndAwards',
    () => CredentialsAndAwards,
  );

  Navigation.registerComponentWithRedux(
    'ConciergeStack.ConciergeMainForm',
    () => ConciergeMainForm,
    Provider,
    store,
  );

  /**
   * Chat
   */
  Navigation.registerComponentWithRedux(
    'ChatStack.homeScreen',
    () => ChatHomeScreen,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'ChatStack.chatRoom',
    () => ChatRoom,
    Provider,
    store,
  );

  Navigation.registerComponent('ChatRoom.TopBar', () => ChatRoomTopBar);

  Navigation.registerComponent(
    'ChatRoom.PhotoWithCaption',
    () => PhotoWithCaption,
  );

  /**
   * Side Menu
   */

  Navigation.registerComponentWithRedux(
    'SideMainMenu.UpdateProfile',
    () => UpdateProfile,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'SideMainMenu.NeedHelp',
    () => NeedHelp,
    Provider,
    store,
  );

  Navigation.registerComponent('SideMainMenu.Settings', () => Settings);

  Navigation.registerComponentWithRedux(
    'SideMainMenu.VerifyMobileNumber',
    () => VerifyMobileNumber,
    Provider,
    store,
  );
  Navigation.registerComponentWithRedux(
    'SideMainMenu.SwitchServer',
    () => SwitchServerMain,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'UpdateProfile.PasswordModal',
    () => PasswordModal,
  );
  /**

   * Home Screen
   */
  Navigation.registerComponentWithRedux(
    'PropertySearchStack.homeScreen',
    () => HomeScreen,
    Provider,
    store,
  );

  /**
   * Menu
   */
  Navigation.registerComponentWithRedux(
    'MenuList.Main',
    () => MenuList,
    Provider,
    store,
  );

  /**
   * User Profile
   */
  Navigation.registerComponentWithRedux(
    'UserAuth.Profile',
    () => UserDashboard,
    Provider,
    store,
  );

  /**
   * Property News Screen
   */
  Navigation.registerComponent(
    'PropertyNewsStack.PropertyNewsHomeScreen',
    () => PropertyNewsHomeScreen,
  );

  Navigation.registerComponent(
    'PropertyNewsStack.NewsDetails',
    () => NewsDetails,
  );

  Navigation.registerComponent(
    'PropertyNewsStack.NewsDetailsResultList',
    () => NewsDetailsResultList,
  );

  /**
   * My Properties
   */
  Navigation.registerComponentWithRedux(
    'MyProperties.Detail',
    () => MyPropertyDetails,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'MyProperties.AddUpdateMyProperty',
    () => MyPropertyForm,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'MyProperties.VefifyMobile',
    () => MyPropertyVerifyMobile,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'MyProperties.OTPConfirmation',
    () => MyPropertyOTPVerification,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'MyProperties.VerifyOwnershipIntro',
    () => VerifyOwnershipIntro,
  );

  /**
   * Communities
   */
  Navigation.registerComponent(
    'Communities.SignInRegister',
    () => SignInRegisterModal,
  );

  Navigation.registerComponent(
    'Communities.AddProperty',
    () => AddPropertyModal,
  );

  Navigation.registerComponentWithRedux(
    'Communities.CommunitiesPostDetails',
    () => CommunitiesPostDetails,
    Provider,
    store,
  );

  Navigation.registerComponent('Communities.NewPost', () => CommunitiesNewPost);

  Navigation.registerComponent(
    'Communities.SortPostModal',
    () => SortPostModal,
  );

  Navigation.registerComponent('Communities.ShareModal', () => ShareModal);

  Navigation.registerComponentWithRedux(
    'Communities.VerifyOTPModal',
    () => VerifyOTPModal,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'Communities.VerifyMobileModal',
    () => VerifyMobileModal,
  );

  Navigation.registerComponent('Communities.generalWebScreen', () => WebScreen);

  Navigation.registerComponentWithRedux(
    'Communities.CommunitiesModal',
    () => CommunitiesModal,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'Communities.SharePostWithComment',
    () => SharePostWithComment,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'Communities.CommunityPostContact',
    () => CommunityPostContactModal,
  );

  Navigation.registerComponent(
    'Communities.DetailActionOptionsModal',
    () => DetailActionOptionsModal,
  );

  Navigation.registerComponent(
    'Communities.ReportAbuse',
    () => CommunitiesReport,
  );

  Navigation.registerComponentWithRedux(
    'Communities.CommunityOptions',
    () => CommunityOptions,
    Provider,
    store,
  );

  Navigation.registerComponentWithRedux(
    'Communities.CommunitiesIntro',
    () => CommunitiesIntro,
    Provider,
    store,
  );

  Navigation.registerComponent('UploadOptionModal', () => UploadOptionModal);

  Navigation.registerComponentWithRedux(
    'GRC.CommunitiesGRCNewPost',
    () => CommunitiesGRCNewPost,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'GRC.CommunitySelection',
    () => CommunitySelection,
  );

  Navigation.registerComponentWithRedux(
    'GRC.MainScreen',
    () => GRCMainScreen,
    Provider,
    store,
  );

  Navigation.registerComponent(
    'GRC.MainScreen.TopBar',
    () => GRCBusinessTopBar,
  );

  Navigation.registerComponent(
    'Communities.CommunitiesCommentSortModal',
    () => CommunitiesCommentSortModal,
  );
};

const ComponentRegisterHandler = {
  registerComponents,
};

export {ComponentRegisterHandler};
