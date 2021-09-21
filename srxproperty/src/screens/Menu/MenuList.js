import React, {Component} from 'react';
import {
  View,
  SafeAreaView,
  SectionList,
  Platform,
  Linking,
  Share,
  BackHandler,
  Image,
} from 'react-native';
import {Navigation} from 'react-native-navigation';
import {connect} from 'react-redux';
import {logoutUser, retrieveDomainUrl} from '../../actions';
import {MenuList_CommunityIcon} from '../../assets';
import {
  FeatherIcon,
  FontAwesome5Icon,
  MaterialIcon,
  MaterialCommunityIcons,
  Separator,
  TouchableHighlight,
  BodyText,
} from '../../components';
import {LoginStack} from '../../config';
import {IS_IOS, SRXColor, AppConstant} from '../../constants';
import {Spacing} from '../../styles';
import {MenuListItem, UserProfileItem} from './components';
import {ObjectUtil} from '../../utils';

const iconSize = 20;

const UserProfileData = [
  {
    key: 'section_user_information',
    data: [
      {
        key: 'row_user_information',
      },
    ],
  },
];

const ActionAvailableAfterLoginForAllUserData = [
  {
    key: 'section_action_available_after_login',
    data: [
      {
        key: 'row_Logout',
        title: 'Sign Out',
        color: SRXColor.Red,
        icon: (
          <FeatherIcon
            name="power"
            size={iconSize}
            color={SRXColor.Red}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
    ],
  },
];

const ActionAvailableAfterLoginForStaffData = [
  {
    key: 'section_action_available_after_login_for_staff',
    data: [
      {
        key: 'row_Switch_Server',
        title: 'Server: ',
      },
    ],
  },
];

//comments some menus since not showing in this phase
//after adding mySGHome, need to uncomment them.
const MenuData = [
  {
    key: 'section_primary_features',
    data: [
      {
        key: 'row_Concierge',
        title: 'Find Agent',
        icon: (
          <FeatherIcon
            name="users"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Valuations',
        title: 'Valuations',
        icon: (
          <FeatherIcon
            name="book"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Loans',
        title: 'Loans',
        icon: (
          <MaterialIcon
            name="attach-money"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Property_News',
        title: 'News',
        icon: (
          <FeatherIcon
            name="file-text"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Community',
        title: 'Community',
        icon: (
          <Image
            style={{width: iconSize, height: iconSize, marginRight: Spacing.M}}
            source={MenuList_CommunityIcon}
            resizeMode={'contain'}
          />
        ),
      },
      {
        key: 'row_Business_Centre',
        title: 'Business Centre',
        icon: (
          <FeatherIcon
            name="settings"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      // {
      //   key: "row_Calculators",
      //   title: "Calculators",
      //   icon: (
      //     <FontAwesome5Icon
      //       name="divide"
      //       size={iconSize}
      //       color={SRXColor.Black}
      //       style={{ marginRight: Spacing.M }}
      //     />
      //   )
      // },
      // {
      //   key: "row_Electricity",
      //   title: "Electricity"
      //   //icon: some icon
      // },
      // {
      //   key: "row_Insurance",
      //   title: "Insurance",
      //   icon: (
      //     <MaterialCommunityIcons
      //       name="shield-home-outline"
      //       size={iconSize}
      //       color={SRXColor.Black}
      //       style={{ marginRight: Spacing.M }}
      //     />
      //   )
      // },
      // {
      //   key: "row_Storage",
      //   title: "Storage",
      //   icon: (
      //     <FeatherIcon
      //       name="box"
      //       size={iconSize}
      //       color={SRXColor.Black}
      //       style={{ marginRight: Spacing.M }}
      //     />
      //   )
      // },
      // {
      //   key: "row_Community",
      //   title: "Community"
      //   //icon: some icon
      // },
      // {
      //   key: "row_New_Launches",
      //   title: "BTO"
      //   //icon: some icon
      // }
    ],
  },
  {
    key: 'section_secondary_features',
    data: [
      {
        key: 'row_Help_Suport',
        title: 'Help & Support',
        icon: (
          <FeatherIcon
            name="help-circle"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Invite_Friends',
        title: 'Invite Friends',
        icon: (
          <FeatherIcon
            name="user-plus"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Feedback',
        title: 'Give us Feedback',
        icon: (
          <FeatherIcon
            name="edit"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Privacy_Policy',
        title: 'Privacy Statement',
        icon: (
          <FeatherIcon
            name="shield"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
      {
        key: 'row_Use_Terms',
        title: 'Terms of Use',
        icon: (
          <FeatherIcon
            name="file"
            size={iconSize}
            color={SRXColor.Black}
            style={{marginRight: Spacing.M}}
          />
        ),
      },
    ],
  },
];

class MenuList extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: true,
      },
    };
  }

  static defaultProps = {};

  state = {
    sections: [...UserProfileData, ...MenuData],
  };

  constructor(props) {
    super(props);

    // this.renderUserProfile = this.renderUserProfile.bind(this);
    this.renderItem = this.renderItem.bind(this);
    this.renderSectionSeparator = this.renderSectionSeparator.bind(this);
    Navigation.events().bindComponent(this);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.userPO !== this.props.userPO) {
      let sections = [...UserProfileData, ...MenuData];

      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        if (
          !ObjectUtil.isEmpty(this.props.userPO.email) &&
          typeof this.props.userPO.email === 'string' &&
          this.props.userPO.email.includes('streetsine.com')
        ) {
          sections = [...sections, ...ActionAvailableAfterLoginForStaffData];
        }
        sections = [...sections, ...ActionAvailableAfterLoginForAllUserData];
      }
      console.log(sections);
      this.setState({sections});
    }
  }

  componentDidAppear() {
    if (!IS_IOS) {
      //for android
      this.backHandler = BackHandler.addEventListener(
        'hardwareBackPress',
        () => {
          Navigation.mergeOptions(this.props.componentId, {
            bottomTabs: {
              currentTabIndex: 0,
            },
          });
          return true;
        },
      );
    }
  }

  componentDidDisappear() {
    //Remove all listeners
    if (!IS_IOS) {
      this.backHandler.remove();
    }
  }

  /**
   * User Profile
   */

  onViewProfile = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'UserAuth.Profile',
      },
    });
  };

  directToLogin = () => {
    LoginStack.showSignInRegisterModal();
  };

  /**
   * First Section
   */
  directToPropertyNews = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertyNewsStack.PropertyNewsHomeScreen',
      },
    });
  };

  directToCommunity = () => {
    Navigation.mergeOptions(this.props.componentId, {
      bottomTabs: {
        currentTabId: 'PropertySearchTab',
        children: [
          {
            component: {
              id: 'HomeScreenId',
              name: 'PropertySearchStack.homeScreen',
              passProps: {
                selectedTab: 2,
              },
            },
          },
        ],
      },
    });
  };

  directToBusinessCentre = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'GRC.MainScreen',
      },
    });
  };

  directToFindAgent = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.homeScreen',
      },
    });
  };

  directToMortgagesPage = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/mortgages',
          screenTitle: 'Mortgages',
        },
      },
    });
  };

  directToValuationsPage = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/srx-valuations',
          screenTitle: 'Valuations',
        },
      },
    });
  };

  /**
   * Second Section
   */

  inviteFriends = () => {
    let message =
      'Hey there! The new SRX Property App is great! You should check it out! ( ' +
      AppConstant.DownloadSourceWebURL +
      ' )';

    const content = {message};
    const options = {
      dialogTitle: 'Share Listing', //iOS only
    };

    Share.share(content, options);
  };

  directToNeedHelpScreen = () => {
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'SideMainMenu.NeedHelp',
              options: {
                modalPresentationStyle: 'overFullScreen',
              },
            },
          },
        ],
      },
    });
  };

  //Give us Feedback
  directToAppStoreOrPlayStore = () => {
    var feedbackURL = AppConstant.DownloadSourceURL;
    if (Platform.OS === 'ios') {
      feedbackURL = feedbackURL + '&action=write-review';
    }
    //couldn't find android review url

    //Direct to App or Play Store
    Linking.canOpenURL(feedbackURL)
      .then(supported => {
        if (supported) {
          return Linking.openURL(feedbackURL);
        } else {
          console.log('Cannot handle the App or Playstore URL ' + feedbackURL);
        }
      })
      .catch(error => console.log('Error occured' + error));
  };

  directToPrivacyPolicyPage = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/privacy-policy',
          screenTitle: 'Privacy Policy',
        },
      },
    });
  };

  directToTermsOfServicePage = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/terms-of-use',
          screenTitle: 'Terms of Use',
        },
      },
    });
  };

  /**
   * Staff Only
   */

  directToSwitchServer = () => {
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'SideMainMenu.SwitchServer',
              options: {
                modalPresentationStyle: 'overFullScreen',
              },
            },
          },
        ],
      },
    });
  };

  /**
   * Logout and other actions after user login
   */

  onLogout = () => {
    const {logoutUser} = this.props;
    logoutUser();
  };

  /**
   * item selected
   */

  onItemSelected = item => {
    switch (
      item.key //
    ) {
      case 'row_Concierge':
        this.directToFindAgent();
        break;

      case 'row_Valuations':
        this.directToValuationsPage();
        break;

      case 'row_Loans':
        this.directToMortgagesPage();
        break;

      case 'row_Property_News':
        this.directToPropertyNews();
        break;

      case 'row_Community':
        this.directToCommunity();
        break;

      case 'row_Business_Centre':
        if (!ObjectUtil.isEmpty(this.props.userPO)) {
          this.directToBusinessCentre();
        } else {
          this.directToLogin();
        }
        break;

      case 'row_Help_Suport':
        this.directToNeedHelpScreen();
        break;

      case 'row_Invite_Friends':
        this.inviteFriends();
        break;

      case 'row_Feedback':
        this.directToAppStoreOrPlayStore();
        break;

      case 'row_Privacy_Policy':
        this.directToPrivacyPolicyPage();
        break;

      case 'row_Use_Terms':
        this.directToTermsOfServicePage();
        break;

      case 'row_Switch_Server':
        this.directToSwitchServer();
        break;

      case 'row_Logout':
        this.onLogout();
        break;

      default:
        break;
    }
  };

  renderItem = ({item, index, section}) => {
    if (item.key === 'row_user_information') {
      const {userPO} = this.props;
      if (!ObjectUtil.isEmpty(userPO)) {
        return (
          <UserProfileItem
            userPO={userPO}
            onItemSelected={this.onViewProfile}
          />
        );
      } else {
        return this.renderSignInItem();
      }
    } else {
      const sectionItemStyle = {backgroundColor: SRXColor.White};
      if (section.key !== 'section_primary_features') {
        sectionItemStyle.backgroundColor = SRXColor.AccordionBackground;
      }

      if (item.key === 'row_Switch_Server') {
        const {serverDomain, retrieveDomainUrl} = this.props;
        retrieveDomainUrl();
        item.title = 'Server: ';
        if (!ObjectUtil.isEmpty(serverDomain)) {
          item.title += serverDomain;
        }
      }

      return (
        <MenuListItem
          item={item}
          containerStyle={sectionItemStyle}
          onItemSelected={this.onItemSelected}
        />
      );
    }
  };

  renderSignInItem() {
    return (
      <TouchableHighlight onPress={this.directToLogin}>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
            padding: Spacing.M,
            backgroundColor: SRXColor.White,
            minHeight: 51,
          }}>
          <BodyText style={{flex: 1, color: SRXColor.TextLink}}>
            Sign In
          </BodyText>
          <FeatherIcon
            name="chevron-right"
            size={20}
            color={SRXColor.Black}
            style={{marginLeft: Spacing.XS}}
          />
        </View>
      </TouchableHighlight>
    );
  }

  renderItemSeparator({highlighted, leadingItem, section}) {
    if (section.key === 'section_primary_features') {
      return (
        <Separator
          edgeInset={{
            top: 0,
            bottom: 0,
            left: leadingItem.icon ? Spacing.M + 20 + Spacing.M : Spacing.M,
            right: Spacing.M,
          }}
        />
      );
    } else if (section.key !== 'section_primary_features') {
      return <Separator />;
    }
    return null;
  }

  renderSectionSeparator({section}) {
    if (section.key === 'section_user_information') {
      return (
        <Separator
          edgeInset={{top: 0, bottom: 0, left: Spacing.M, right: Spacing.M}}
        />
      );
    } else if (section.key === 'section_primary_features') {
      return <Separator />;
    }
    return null;
  }

  render() {
    const {sections} = this.state;

    return (
      <SafeAreaView style={{flex: 1}}>
        <SectionList
          style={{flex: 1}}
          sections={sections}
          renderItem={this.renderItem}
          ItemSeparatorComponent={this.renderItemSeparator}
          renderSectionFooter={this.renderSectionSeparator} //use this to draw separator
        />
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    serverDomain: state.serverDomain.domainURL,
  };
};

export default connect(
  mapStateToProps,
  {logoutUser, retrieveDomainUrl},
)(MenuList);
