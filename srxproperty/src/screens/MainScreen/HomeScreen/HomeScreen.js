import React, {Component} from 'react';
import {
  View,
  ScrollView,
  SafeAreaView,
  AppState,
  Linking,
  Platform,
  Alert,
  Text,
  Animated,
} from 'react-native';
import CookieManager from '@react-native-community/cookies';
import {
  SRXColor,
  AppConstant,
  AlertMessage,
  LoadingState,
} from '../../../constants';
import {connect} from 'react-redux';
import DeviceInfo from 'react-native-device-info';

import {
  login,
  fblogin,
  clearAllShortlist,
  retrieveDomainUrl,
  clearDomainUrl,
  loadPropertyTrackers,
  clearPropertyTrackers,
  findShortlistedItems,
  loadShortlistedItems,
  notLoggedIn,
  shortlistListing,
  loadUserProfile,
  clearCommunities,
} from '../../../actions';
import {Button} from '../../../components';
import {
  ObjectUtil,
  UserUtil,
  PropertyTypeUtil,
  DebugUtil,
} from '../../../utils';
import {Navigation} from 'react-native-navigation';
import SplashScreen from 'react-native-splash-screen';
import {IntroSplashScreen} from '../../SplashScreens';
import {GeneralService} from '../../../services';
import {GreetingAndSignIn, HomeScreenTabs} from '../MainScreenSection';
import {FindHomes, Community} from '../../Communities';
import {PropertyTypeValueSet} from '../../PropertySearch/Constants';
import {NotificationListener} from '../../../listener';
import {LoginStack, CommunitiesStack} from '../../../config';

const isIOS = Platform.OS === 'ios';

var parse = require('url-parse');

const HomeScreenTab = {
  FindHome: 0,
  HomeValue: 1,
  Community: 2,
  Services: 3,
};

class HomeScreen extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: true,
      },
      layout: {
        backgroundColor: 'white',
      },
      modalPresentationStyle: 'none',
    };
  }

  /**
   * appState is not set in "state" as re-rendering is not required
   */
  appState = AppState.currentState;
  /**
   * a temporary boolean argument to indicate whether to prevent user continue using until update
   * will be moved to another location if found a better implementation
   */
  forceUpdate = false;

  /**
   * to store any task that required user po
   * and handle after loaded user po
   *
   * currently only handled for only 1 action
   * for multiple actions to queue
   * please think of a way to do that
   *
   * example of action object
   * {
   *   type: "applink",
   *   url: "https://www.srx.com.sg/dashboard?ptUserId=<id>"
   * }
   *
   * ** haven't came out example for notification or other cases
   */
  pendingAction = null;
  uniqueShortlistArray = [];
  applinkEvent = null;

  haveDomainURL = false;
  contentOffSetY = 0;

  /**
   * to store feedlist & total
   */
  feedList = [];
  postTotal = 0;

  state = {
    /**
     * initial Search Options Object
     */
    searchOptions: {
      type: 'S',
      displayText: 'Everywhere in Singapore',
      propertyType: PropertyTypeValueSet.allResidential,
      cdResearchSubTypes: Array.from(PropertyTypeValueSet.allResidential).join(
        ',',
      ),
    },
    isResidential: true,
    resultArray: [],
    hideComponents: false,
    selectedTab: 0,
    movableTop: new Animated.Value(0),
    containerHeight: 30,

    /**
     * reload community or not
     */
    reloadCommunityCount: 0,
  };

  constructor(props) {
    super(props);
    const {loadUserProfile, serverDomain} = this.props;

    Navigation.events().bindComponent(this);

    //show introduction (1st time only)
    // SplashScreen.show();
    this.checkCommunitiesIntroIndicator();

    Text.defaultProps = Text.defaultProps || {};
    Text.defaultProps.allowFontScaling = false;
    this.props.retrieveDomainUrl();
    DebugUtil.getChosenAppDomainURL().then(result => {
      if (result) {
        UserUtil.getDataForLogin().then(
          ({username, password, facebookId, userPO, appdomain}) => {
            CookieManager.get(result).then(cookies => {
              const userSessionTokenCookie = cookies['userSessionToken'];
              if (
                !ObjectUtil.isEmpty(userPO) &&
                !ObjectUtil.isEmpty(userSessionTokenCookie)
              ) {
                loadUserProfile(userPO);
                if (appdomain && appdomain !== AppConstant.DomainUrl) {
                  this.haveDomainURL = true;
                }
              } else {
                if (
                  !ObjectUtil.isEmpty(facebookId) &&
                  !ObjectUtil.isEmpty(username)
                ) {
                  props.fblogin({
                    facebookId: facebookId,
                    username: username,
                  });
                } else if (
                  !ObjectUtil.isEmpty(username) &&
                  !ObjectUtil.isEmpty(password)
                ) {
                  props.login({
                    username: username,
                    password: password,
                  });
                } else {
                  props.notLoggedIn();
                  this.props.retrieveDomainUrl();
                }
              }
            });
          },
        );
      }
    });

    this.showCommunity = this.showCommunity.bind(this);
    this.showCommunitiesIntro = this.showCommunitiesIntro.bind(this);
    this.showFindHomeTab = this.showFindHomeTab.bind(this);

    this.saveFeedList = this.saveFeedList.bind(this);
  }

  componentDidMount() {
    //Applink and Universal link
    AppState.addEventListener('change', this.handleAppStateChange);

    /**
     * 1st checking if the app is opened by a link
     *  if not show splashscreen and intro (IntroSplashScreen)
     * else
     *  check the url
     *    if it is listing url
     *      hide splashscreen, and direct user to listing details
     *    else
     *      add to pending actions, wait for login checking and continue
     */

    // if (!isIOS) {
    Linking.getInitialURL().then(url => {
      let listingId = null;
      let shortlistIds = null;
      if (url) {
        let parsedURL = parse(url, true);
        if (!ObjectUtil.isEmpty(parsedURL)) {
          const {pathname, query} = parsedURL;
          if (!ObjectUtil.isEmpty(pathname)) {
            const pathArray = pathname.split('/');
            /**
             * As the pathname start with "/",
             * after spliting, the 1st object will be an empty string
             */
            if (pathArray.length > 1) {
              const firstPath = pathArray[1];
              if (
                firstPath.toLowerCase() === 'l' ||
                firstPath.toLowerCase() === 'listings'
              ) {
                if (pathArray.length > 2) {
                  const secondPath = pathArray[2];
                  if (!ObjectUtil.isEmpty(secondPath)) {
                    listingId = secondPath;
                  }
                }
              } else if (firstPath.toLowerCase() === 'shortlist') {
                shortlistIds = query;
              }
            }
          }
        }
      }

      if (listingId) {
        SplashScreen.hide();
        if (!isIOS) {
          Navigation.push(this.props.componentId, {
            component: {
              name: 'PropertySearchStack.ListingDetails',
              passProps: {
                listingId: listingId,
                refType: 'V',
              },
            },
          });
        }
      } else if (shortlistIds) {
        SplashScreen.hide();
        if (!isIOS) {
          this.handleOpenURL(url);
        }
      } else {
        IntroSplashScreen.show();
        if (url && !isIOS) {
          this.handleOpenURL(url);
        }
      }
    });
    // }
    Linking.addEventListener('url', this.handleOpenURLForAppLink);
    // Linking.addEventListener('url', this.handleAppLink);
    //check updates
    this.checkAppUpdates();
  }

  handleAppLink = event => {
    this.handleOpenURL(event.url);
    // this.applinkEvent = event;
  };

  clearAppLink = () => {
    this.applinkEvent = null;
  };

  // componentDidDisappear() {
  //   this.setState({hideComponents: true});
  // }
  // componentDidAppear() {
  //   this.setState({hideComponents: false});
  // }

  componentDidUpdate(prevProps) {
    const {
      clearAllShortlist,
      loadPropertyTrackers,
      clearPropertyTrackers,
      clearCommunities,
      selectedTab,
    } = this.props;
    //User has logged in
    if (prevProps.userPO !== this.props.userPO) {
      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        //if just login or change user then reload
        if (
          ObjectUtil.isEmpty(prevProps.userPO) ||
          (!ObjectUtil.isEmpty(prevProps.userPO) &&
            prevProps.userPO.encryptedUserId !==
              this.props.userPO.encryptedUserId)
        ) {
          if (!ObjectUtil.isEmpty(this.pendingAction)) {
            if (this.pendingAction.type === 'applink') {
              this.handleOpenURL(this.pendingAction.url);
            } else if (
              this.pendingAction.type === 'shortlist' &&
              !ObjectUtil.isEmpty(this.uniqueShortlistArray)
            ) {
              this.addShortlistFromApplink(this.uniqueShortlistArray);
            }
          }
          loadPropertyTrackers();
        }
      } else {
        //logout
        //Uncomment the method below if want to uncomment the method below
        //clearDomainUrl();
        clearCommunities();
        clearAllShortlist();
        clearPropertyTrackers();
        // this.setState({selectedTab: 0}, () => {
        Navigation.mergeOptions(this.props.componentId, {
          bottomTabs: {
            currentTabIndex: 0,
          },
        });
        // });
      }
    } else if (prevProps.selectedTab !== this.props.selectedTab) {
      this.setState({selectedTab: this.props.selectedTab});
    }

    //for shortlists
    if (prevProps.loadingState !== this.props.loadingState) {
      if (this.props.loadingState === LoadingState.Loaded) {
        if (!ObjectUtil.isEmpty(this.pendingAction)) {
          if (this.pendingAction.type === 'shortlist') {
            this.handleShortlistAppLink(this.pendingAction.query);
          }
        }
      }
    }
    
    //switch server
    // if (
    //   prevProps.serverDomain.domainURL !== this.props.serverDomain.domainURL &&
    //   !ObjectUtil.isEmpty(this.props.userPO)
    // ) {
    //   Navigation.mergeOptions(this.props.componentId, {
    //     bottomTabs: {
    //       currentTabIndex: 0,
    //     },
    //   });
    //   loadPropertyTrackers({populateXvalue: true});
    //   this.haveDomainURL = false;
    // }
  } //end of component did update

  componentWillUnmount() {
    AppState.removeEventListener('change', this.handleAppStateChange);
    // Linking.removeEventListener('url', this.handleOpenURLForAppLink);
  }

  checkCommunitiesIntroIndicator(isVersionUpdated) {
    if (isVersionUpdated) {
      this.showCommunitiesIntro();
    } else {
      UserUtil.retrieveCommunitiesIntroIndicator().then(result => {
        console.log('Retrieve Communities Intro Indicator ' + result);
        if (result !== true) {
          this.showCommunitiesIntro();
        }
      });
    }
  }

  showCommunitiesIntro() {
    CommunitiesStack.showCommunitiesIntro({onCloseIntro: this.closeIntro});
  }

  closeIntro = () => {
    UserUtil.saveCommunitiesIntroIndicator(true);
    Navigation.dismissModal('component-introduction');
    this.showCommunity();
  };

  checkAppUpdates() {
    GeneralService.getUpdateAppStatus()
      .then(response => {
        const {result} = response;
        if (!ObjectUtil.isEmpty(result)) {
          const {update, forceUpdate} = result;
          if (update == true) {
            if (forceUpdate == true) {
              //do something
              this.forceUpdate = true;
              this.forceUserToUpdate();
            } else {
              Alert.alert(
                AlertMessage.SuccessMessageTitle,
                'New version is available. Do you want to update?',
                [
                  {
                    text: 'Cancel',
                    onPress: this.checkCommunitiesIntroIndicator(true),
                  },
                  {
                    text: 'Update Now',
                    onPress: this.directToAppStoreOrPlayStore,
                  },
                ],
              );
            }
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  handleAppStateChange = nextAppState => {
    if (
      this.appState.match(/inactive|background/) &&
      nextAppState === 'active'
    ) {
      if (this.forceUpdate) {
        this.forceUserToUpdate();
      }
    }
    this.appState = nextAppState;
  };

  handleOpenURLForAppLink = event => {
    if (event.url) {
      let url = event.url;
      if (url.startsWith('fb')) {
        let decodedUrl = decodeURIComponent(url).split('target_url=')[1];
        this.handleOpenURL(decodedUrl);
      } else {
        this.handleOpenURL(url);
      }
    }
  };

  handleOpenURL = event => {
    this.pendingAction = null;
    const {userPO, loadingState} = this.props;
    let parsedURL = parse(event, true);
    if (!ObjectUtil.isEmpty(parsedURL)) {
      const {pathname, query} = parsedURL;
      //TODO: switch server according to hostname, but make sure checking is required

      if (!ObjectUtil.isEmpty(pathname)) {
        const pathArray = pathname.split('/');
        /**
         * As the pathname start with "/",
         * after spliting, the 1st object will be an empty string
         */
        if (pathArray.length > 1) {
          const firstPath = pathArray[1];
          if (
            firstPath.toLowerCase() === 'l' ||
            firstPath.toLowerCase() === 'listings'
          ) {
            if (pathArray.length > 2) {
              const secondPath = pathArray[2];
              if (!ObjectUtil.isEmpty(secondPath)) {
                Navigation.mergeOptions(this.props.componentId, {
                  bottomTabs: {
                    currentTabIndex: 0,
                  },
                });
                Navigation.push(this.props.componentId, {
                  component: {
                    name: 'PropertySearchStack.ListingDetails',
                    passProps: {
                      listingId: secondPath,
                      refType: 'V',
                    },
                  },
                });
              }
            }
          } else if (firstPath.toLowerCase() === 'dashboard') {
            if (
              !ObjectUtil.isEmpty(query) &&
              (!ObjectUtil.isEmpty(query.ptUserId) || query.ptUserId > 0)
            ) {
              if (!ObjectUtil.isEmpty(userPO)) {
                Navigation.push(this.props.componentId, {
                  component: {
                    name: 'MyProperties.Detail',
                    passProps: {
                      ptUserId: query.ptUserId,
                    },
                  },
                });
              } else {
                LoginStack.showSignInRegisterModal();
                this.pendingAction = {
                  type: 'applink',
                  url: event,
                };
              }
            }
          } else if (firstPath.toLowerCase() === 'mysg-home') {
            this.pendingAction = {
              type: 'mysg-home',
              query,
            };
            if (this.props.loginData.loadingState !== LoadingState.Normal) {
              this.handlemySgHomeAppLink();
            }
          } else if (firstPath.toLowerCase() === 'shortlist') {
            // if (
            //   !ObjectUtil.isEmpty(userPO) &&
            //   loadingState === LoadingState.Loaded
            // ) {
            //   this.handleShortlistAppLink(query);
            // } else {
            //   this.pendingAction = {
            //     type: 'shortlist',
            //     url: event,
            //     query,
            //   };
            // }
            Navigation.mergeOptions(this.props.componentId, {
              bottomTabs: {
                currentTabIndex: 1,
              },
            });

            this.pendingAction = {
              type: 'shortlist',
              url: event,
              query,
            };
            this.handleShortlistAppLink(query);
          } else if (firstPath.toLowerCase() === 'community') {
            if (pathArray.length > 3) {
              if (pathArray[2].toLowerCase() === 'posts') {
                this.pendingAction = {
                  type: 'community',
                  postId: pathArray[3],
                };
              }
            }
          }
        }
      }
    }
  };

  handlemySgHomeAppLink() {
    const {userPO, properties} = this.props;
    if (ObjectUtil.isEmpty(userPO) && !ObjectUtil.isEmpty(this.pendingAction)) {
      if (this.pendingAction.type === 'mysg-home') {
        LoginStack.showSignInRegisterModal();
      }
    } else if (
      !ObjectUtil.isEmpty(userPO) &&
      !ObjectUtil.isEmpty(this.pendingAction) &&
      !ObjectUtil.isEmpty(properties)
    ) {
      if (userPO.encryptedUserId === this.pendingAction.query.userId) {
        let ind = -1;
        properties.map((item, index) => {
          if (item.postalCode === this.pendingAction.query.postalCode) {
            ind = index;
          }
        });
        if (ind >= 0) {
          this.directToMyProperty(properties[ind]);
        } else if (properties.length == 3) {
          Alert.alert('Add Properties limit exceeded');
        } else {
          this.directToAddMyProperty();
        }
        /** Conditions else if properties.length == 3 and else are backup check as webview flow and mobile flow differs*/
        this.pendingAction = null;
        console.log(ind);
      } else if (!ObjectUtil.isEmpty(this.pendingAction.query)) {
        if (properties.length == 3) {
          Alert.alert('Add Properties limit exceeded');
        } else {
          this.directToAddMyProperty(this.pendingAction.query);
        }
        this.pendingAction = null;
      } else {
        Alert.alert(
          'Account mismatched',
          'Please logout before accessing the link',
        );
        this.pendingAction = null;
      }
    }
  }

  handleShortlistAppLink(query) {
    const {shortlistedItems, loadingState} = this.props;
    if (loadingState === LoadingState.Loaded) {
      if (!ObjectUtil.isEmpty(query)) {
        var listingCount = 0;
        var msg = '';

        //split listing id to array
        var shortlistArray = query.listingIds.split(',');

        // shortlistArray => from applink, shortlistedItems => redux
        shortlistArray.forEach(value => {
          const result = shortlistedItems.find(
            item => item.encryptedRefId === value,
          );
          if (ObjectUtil.isEmpty(result)) {
            this.uniqueShortlistArray.push(value);
            listingCount++;
          }
        });

        if (listingCount > 1) {
          msg = listingCount + ' listings will be added to your shortlist.';
        } else {
          msg = listingCount + ' listing will be added to your shortlist.';
        }

        Alert.alert(AlertMessage.SuccessMessageTitle, msg, [
          {text: 'Cancel', onPress: () => (this.uniqueShortlistArray = [])},
          {
            text: 'OK',
            onPress: () =>
              this.addShortlistFromApplink(this.uniqueShortlistArray),
          },
        ]);
      }
    }
  }

  addShortlistFromApplink = shortlistArray => {
    const {userPO, shortlistListing} = this.props;
    if (!ObjectUtil.isEmpty(shortlistArray)) {
      if (!ObjectUtil.isEmpty(userPO)) {
        this.uniqueShortlistArray = [];
        shortlistArray.map(item => {
          shortlistListing({
            encryptedListingId: item,
            listingType: 'V',
          });
        });
      } else {
        LoginStack.showSignInRegisterModal();
      }
    }
  };

  directToAppStoreOrPlayStore() {
    Linking.canOpenURL(AppConstant.DownloadSourceURL)
      .then(supported => {
        if (supported) {
          return Linking.openURL(AppConstant.DownloadSourceURL);
        } else {
          console.log(
            'Cannot handle the App or Playstore URL ' +
              AppConstant.DownloadSourceURL,
          );
        }
      })
      .catch(error => console.log('Error occured' + error));
  }

  forceUserToUpdate() {
    Alert.alert(
      'Please update your app to continue',
      'This version of the app is no longer supported',
      [
        {
          text: 'Update Now',
          onPress: this.directToAppStoreOrPlayStore,
        },
      ],
      {cancelable: false},
    );
  }

  showCommunity() {
    this.updateTabButton(HomeScreenTab.Community);
  }

  // onUpdateSearchOptions = searchOptions => {
  //   if (!ObjectUtil.isEmpty(searchOptions)) {
  //     this.setState({searchOptions});
  //   }
  // };

  // isResidentialOrCommercial = selectedText => {
  //   const {searchOptions} = this.state;
  //   if (selectedText === 'Residential') {
  //     var newSearchOptions = {
  //       ...searchOptions,
  //       cdResearchSubTypes: Array.from(
  //         PropertyTypeValueSet.allResidential,
  //       ).join(','),
  //     };
  //     this.setState({isResidential: true, searchOptions: newSearchOptions});
  //   } else {
  //     var newSearchOptions = {
  //       ...searchOptions,
  //       cdResearchSubTypes: Array.from(PropertyTypeValueSet.commercial).join(
  //         ',',
  //       ),
  //     };
  //     this.setState({isResidential: false, searchOptions: newSearchOptions});
  //   }
  // };

  //Go to listing results
  // showListingResult = searchOptions => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'PropertySearchStack.searchResult',
  //       passProps: {
  //         initialSearchOptions: searchOptions,
  //         onUpdateSearchOptions: this.onUpdateSearchOptions,
  //       },
  //     },
  //   });
  // };

  //go to find agent module
  // viewFindAgent = () => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'ConciergeStack.homeScreen',
  //     },
  //   });
  // };

  //go to valuations web page
  // directToValuationsPage = () => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'LoginStack.generalWebScreen',
  //       passProps: {
  //         url: '/srx-valuations',
  //         screenTitle: 'Valuations',
  //       },
  //     },
  //   });
  // };

  //go to loan web page
  // directToMortgagesPage = () => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'LoginStack.generalWebScreen',
  //       passProps: {
  //         url: '/mortgages',
  //         screenTitle: 'Mortgages',
  //       },
  //     },
  //   });
  // };

  //go to property news
  // directToPropertyNews = () => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'PropertyNewsStack.PropertyNewsHomeScreen',
  //     },
  //   });
  // };

  //select on view all
  // onViewAllListing = searchOptions => {
  //   const {properties, userPO} = this.props;
  //   const {isResidential} = this.state;
  //   var newSearchOptions = {
  //     ...searchOptions,
  //     type: 'S',
  //     displayText: 'Everywhere in Singapore',
  //     cdResearchSubTypes: isResidential
  //       ? Array.from(PropertyTypeValueSet.allResidential).join(',')
  //       : Array.from(PropertyTypeValueSet.commercial).join(','),
  //   };

  //   if (isResidential) {
  //     if (!ObjectUtil.isEmpty(userPO) && properties.length > 0) {
  //       if (PropertyTypeUtil.isHDB(properties[0].cdResearchSubtype)) {
  //         propertyType = PropertyTypeValueSet.hdb;
  //       } else if (PropertyTypeUtil.isCondo(properties[0].cdResearchSubtype)) {
  //         propertyType = PropertyTypeValueSet.condo;
  //       } else if (PropertyTypeUtil.isLanded(properties[0].cdResearchSubtype)) {
  //         propertyType = PropertyTypeValueSet.landed;
  //       }

  //       //recommended listings
  //       newSearchOptions = {
  //         ...newSearchOptions,
  //         cdResearchSubTypes: Array.from(propertyType).join(','),
  //         searchText: properties[0].address,
  //         displayText: properties[0].address,
  //       };
  //     } else {
  //       //featured listings
  //       newSearchOptions = {
  //         ...newSearchOptions,
  //         cdResearchSubTypes: Array.from(
  //           PropertyTypeValueSet.allResidential,
  //         ).join(','),
  //         vt360Filter: true,
  //       };
  //     }
  //   } else {
  //     //commercial listings
  //     newSearchOptions = {
  //       ...newSearchOptions,
  //       cdResearchSubTypes: Array.from(PropertyTypeValueSet.commercial).join(
  //         ',',
  //       ),
  //     };
  //   }
  //   this.showListingResult(newSearchOptions);
  // };

  //Go to listing details
  // showListingDetails = ({listingPO}) => {
  //   if (!ObjectUtil.isEmpty(listingPO)) {
  //     Navigation.push(this.props.componentId, {
  //       component: {
  //         name: 'PropertySearchStack.ListingDetails',
  //         passProps: {
  //           listingId: listingPO.getListingId(),
  //           refType: listingPO.listingType,
  //         },
  //       },
  //     });
  //   }
  // };

  //Hot Project Event Section
  // directToHotProjectWebScreen = projectDetailPO => {
  //   if (!ObjectUtil.isEmpty(projectDetailPO)) {
  //     //get URL and Name
  //     let projectURL = projectDetailPO.getProjectUrl();
  //     let projectName = projectDetailPO.getProjectName();
  //     //navigate to hot project web detail screen
  //     Navigation.push(this.props.componentId, {
  //       component: {
  //         name: 'PropertySearchStack.generalWebScreen',
  //         passProps: {
  //           url: projectURL,
  //           screenTitle: projectName,
  //         },
  //       },
  //     }); //end of navigation push
  //   }
  // };

  //direct to hot project lists
  // viewAllHotProjects = (latestProjects, featuredProjects) => {
  //   //get latest and featured projects
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'PropertySearchStack.NewLaunchesProjectResult',
  //       passProps: {
  //         newLaunchLatestProjects: latestProjects,
  //         newLaunchFeaturedProjects: featuredProjects,
  //       },
  //     },
  //   });
  // };

  //direct to user profile and my property list page
  // directToUserProfile = () => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'UserAuth.Profile',
  //     },
  //   });
  // };

  // directToAddMyProperty = invitationInfo => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'MyProperties.AddUpdateMyProperty',
  //       passProps: {
  //         initiatingComponentId: this.props.componentId,
  //         invitationInfo,
  //       },
  //     },
  //   });
  // };

  // go to property details
  // directToMyProperty = (srxPropertyUserPO, certifyListing) => {
  //   //certify can be nullable
  //   if (!ObjectUtil.isEmpty(srxPropertyUserPO)) {
  //     Navigation.push(this.props.componentId, {
  //       component: {
  //         name: 'MyProperties.Detail',
  //         passProps: {
  //           initialTrackerPO: srxPropertyUserPO,
  //           certifyListing,
  //         },
  //       },
  //     });
  //   }
  // };

  //go to news details
  // showNewsDetails = (
  //   onlineCommunicationPO,
  //   indexNo,
  //   newsSearchOption,
  //   categoryTypeDescription,
  // ) => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'PropertyNewsStack.NewsDetailsResultList',
  //       passProps: {
  //         onlineCommunicationPO: onlineCommunicationPO,
  //         indexNo: indexNo,
  //         newsSearchOption: newsSearchOption,
  //         screenTitle: categoryTypeDescription,
  //       },
  //     },
  //   });
  // };

  //go to property news home screen
  // directToPropertyNews = () => {
  //   Navigation.push(this.props.componentId, {
  //     component: {
  //       name: 'PropertyNewsStack.PropertyNewsHomeScreen',
  //     },
  //   });
  // };

  //START rendering methods
  renderGreetingandSignIn() {
    return <GreetingAndSignIn directToUserProfile={this.directToUserProfile} />;
  }

  // renderPropertySearchAndShortcutAccess() {
  //   const {searchOptions} = this.state;
  //   return (
  //     <PropertySearchAndShortcutAccess
  //       componentId={this.props.componentId}
  //       searchOptions={searchOptions}
  //       onUpdateSearchOptions={this.onUpdateSearchOptions}
  //       isResidentialOrCommercial={this.isResidentialOrCommercial}
  //       showListingResult={this.showListingResult}
  //     />
  //   );
  // }

  // renderMyPropertyAtAGlance() {
  //   const {isResidential} = this.state;
  //   if (isResidential) {
  //     return (
  //       <MyPropertyAtAGlance
  //         onViewAllMyProperty={this.directToUserProfile}
  //         directToMyProperty={this.directToMyProperty}
  //         onAddPropertyPressed={this.directToAddMyProperty}
  //       />
  //     );
  //   }
  // }

  // renderCommercialSurvey() {
  //   const {isResidential} = this.state;
  //   return <CommercialSurvey isResidential={isResidential} />;
  // }

  // renderServicesShortcut() {
  //   const {isResidential} = this.state;
  //   if (isResidential) {
  //     return (
  //       <ServicesShortcut
  //         viewFindAgent={this.viewFindAgent}
  //         directToValuationsPage={this.directToValuationsPage}
  //         directToMortgagesPage={this.directToMortgagesPage}
  //         directToPropertyNews={this.directToPropertyNews}
  //       />
  //     );
  //   } else {
  //     return <View />;
  //   }
  // }

  // renderHotProjects() {
  //   return (
  //     <HotProjects
  //       onPressHotProjectItem={this.directToHotProjectWebScreen}
  //       onPressViewAll={this.viewAllHotProjects}
  //     />
  //   );
  // }

  // renderFeaturedListings() {
  //   const {isResidential} = this.state;
  //   return (
  //     <FeaturedListings
  //       showListingDetails={this.showListingDetails}
  //       onViewAllListing={this.onViewAllListing}
  //       isResidential={isResidential}
  //     />
  //   );
  // }

  // renderMarketWatch() {
  //   return <MarketWatch />;
  // }

  // renderPropertyNews() {
  //   return (
  //     <PropertyNews
  //       showNewsDetails={this.showNewsDetails}
  //       directToPropertyNews={this.directToPropertyNews}
  //     />
  //   );
  // }

  //this method currently only handle navigation to chat tab, to reduce the time consume when user press the notification
  handleNavigation = () => {
    try {
      Navigation.mergeOptions(this.props.componentId, {
        bottomTabs: {
          currentTabIndex: 3,
        },
      });
    } catch (e) {
      console.log(e);
    }
  };

  updateTabButton = index => {
    this.setState({
      selectedTab: index,
      reloadCommunityCount:
        index === HomeScreenTab.Community
          ? this.state.reloadCommunityCount + 1
          : this.state.reloadCommunityCount,
    });
  };

  renderHomeScreenTabs() {
    const {selectedTab, movableTop} = this.state;
    return (
      <Animated.View
        style={{
          position: 'absolute',
          top: 0,
          width: '100%',
          overflow: 'hidden',

          transform: [
            {
              translateY: movableTop,
            },
          ],
        }}>
        <HomeScreenTabs
          updateTabButton={this.updateTabButton}
          selectedTab={selectedTab}
        />
      </Animated.View>
    );
  }

  renderFindHomesScreen() {
    return (
      <FindHomes
        style={{flex: 1}}
        navigateBottomTab={this.navigateBottomTab}
        componentId={this.props.componentId}
        navigatePush={this.navigatePush}
        //appLink={this.applinkEvent}
        clearAppLink={this.clearAppLink}
        onPressSeeAll={this.showCommunity}
      />
    );
  }

  onContentScroll = e => {
    const {containerHeight, movableTop} = this.state;

    const currentY = e.nativeEvent.contentOffset.y;
    const previousY = this.contentOffSetY;

    let top = 0;

    if (currentY > previousY && currentY >= containerHeight) {
      top = -50;
    } else if (currentY < previousY) {
      top = 0;
    }

    if (top > 0) {
      top = 0;
    }

    this.contentOffSetY = currentY;

    Animated.timing(movableTop, {
      toValue: top,
      duration: 40,
      useNativeDriver: true,
    }).start();
  };

  renderCommunityScreen() {
    const {reloadCommunityCount} = this.state;
    return (
      <Community
        componentId={this.props.componentId}
        previousPosts={{feedList: this.feedList, total: this.postTotal}}
        reloadCommunityCount={reloadCommunityCount}
        saveFeedList={this.saveFeedList}
        showFindHomeTab={this.showFindHomeTab}
      />
    );
  }

  saveFeedList(posts, total) {
    this.feedList = posts;
    this.postTotal = total;
  }

  showFindHomeTab() {
    this.updateTabButton(HomeScreenTab.FindHome);
  }

  renderScreenByTab() {
    const {selectedTab} = this.state;
    if (selectedTab === 0) {
      return this.renderFindHomesScreen();
    } else if (selectedTab === 1) {
      return (
        <View>
          <Text>test select tab</Text>
        </View>
      );
    } else if (selectedTab === 2) {
      return this.renderCommunityScreen();
    } else {
      return this.renderFindHomesScreen();
    }
    // var selectedIndex = selectedTab.toString();
    // switch(selectedIndex){
    //   case "0":
    //    return <TestEmpty/>;
    //     break;
    //   case "1":
    //     return <TestEmpty/>;
    //     break;
    //   default:
    //    return this.renderFindHomesScreen();
    //     break;
    // }
  }

  render() {
    const {hideComponents} = this.state;

    return (
      <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
        <NotificationListener
          componentId={this.props.componentId}
          handleNavigation={this.handleNavigation}
        />
        {this.renderGreetingandSignIn()}

        <View
          style={{
            flex: 1,
            overflow: 'hidden',
            backgroundColor: SRXColor.White,
          }}>
          {/* <ScrollView style={{flex: 1}} onScroll={this.onContentScroll}> */}
          <View
            style={{backgroundColor: SRXColor.SmallBodyBackground, flex: 1}}>
            <View
              style={{
                width: '100%',
                height: 40,
                backgroundColor: SRXColor.White,
              }}
            />
            {this.renderScreenByTab()}
            {/* {this.renderFindHomesScreen()} */}
            {/* {this.renderPropertySearchAndShortcutAccess()} */}
            {/* {this.renderMyPropertyAtAGlance()} */}
            {/* {this.renderCommercialSurvey()} */}
            {/* {hideComponents == false ? this.renderServicesShortcut() : null} */}
            {/* {this.renderHotProjects()} */}
            {/* {this.renderFeaturedListings()} */}
            {/* {hideComponents == false ? (
              <View>
                {this.renderMarketWatch()}
                {this.renderPropertyNews()}
              </View>
            ) : null} */}
          </View>
          {/* </ScrollView> */}
          {this.renderHomeScreenTabs()}
        </View>
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    loginData: state.loginData,
    shortlistedItems: state.shortlistData.shortlistedItems,
    loadingState: state.shortlistData.loadingState,
    properties: state.myPropertiesData.list,
    serverDomain: state.serverDomain,
  };
};

export default connect(
  mapStateToProps,
  {
    login,
    fblogin,
    clearAllShortlist,
    retrieveDomainUrl,
    clearDomainUrl,
    loadPropertyTrackers,
    clearPropertyTrackers,
    findShortlistedItems,
    loadShortlistedItems,
    notLoggedIn,
    shortlistListing,
    loadUserProfile,
    clearCommunities,
  },
)(HomeScreen);
