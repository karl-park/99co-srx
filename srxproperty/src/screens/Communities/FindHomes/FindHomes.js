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
} from 'react-native';
import {
  SRXColor,
  AppConstant,
  AlertMessage,
  LoadingState,
} from '../../../constants';
import {connect} from 'react-redux';
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
import {
  ObjectUtil,
  UserUtil,
  PropertyTypeUtil,
  GoogleAnalyticUtil,
} from '../../../utils';
import {Navigation} from 'react-native-navigation';
import {GeneralService} from '../../../services';
import {
  PropertySearchAndShortcutAccess,
  CommercialSurvey,
  ServicesShortcut,
  MyPropertyAtAGlance,
  FeaturedListings,
  MarketWatch,
  HotProjects,
  PropertyNews,
  HomeScreenTabs,
} from './FindHomesSections';
import {PropertyTypeValueSet} from '../../PropertySearch/Constants';
import {NotificationListener} from '../../../listener';
import {LoginStack} from '../../../config';
import {Button} from '../../../components';
import {Typography} from '../../../styles';
import PropTypes from 'prop-types';

const isIOS = Platform.OS === 'ios';
var parse = require('url-parse');

class FindHomes extends Component {
  static propTypes = {
    onPressSeeAll: PropTypes.func,
  };

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
  };

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);
    this.eventSubscription = Navigation.events().registerBottomTabSelectedListener(
      this.tabChanged,
    );

    this.showListingResult = this.showListingResult.bind(this);
    this.viewAllHotProjects = this.viewAllHotProjects.bind(this);
    this.tabChanged = this.tabChanged.bind(this);
  }

  componentDidMount() {
    //Applink and Universal link
    // if (!isIOS) {
    //   Linking.getInitialURL().then(url => {
    //     if (url) {
    //       this.handleOpenURL(url);
    //     }
    //   });
    // }
    // Linking.addEventListener('url', this.handleOpenURLForAppLink);
    const {userPO, properties, loadPropertyTrackers} = this.props;
    if (!ObjectUtil.isEmpty(userPO) && ObjectUtil.isEmpty(properties)) {
      loadPropertyTrackers({populateXvalue: true});
    }
  }

  componentDidDisappear() {
    this.setState({hideComponents: true});
  }
  componentDidAppear() {
    this.setState({hideComponents: false});
  }

  componentWillUnmount() {
    if (this.eventSubscription) {
      this.eventSubscription.remove();
    }
    // Linking.removeEventListener('url', this.handleOpenURLForAppLink);
  }

  tabChanged = ({selectedTabIndex, unselectedTabIndex}) => {
    if (selectedTabIndex === 0 && this.scrollView) {
      this.scrollView.scrollTo({x: 0, y: 0, animated: true});
    }
  };

  componentDidUpdate(prevProps) {
    const {
      clearAllShortlist,
      loadPropertyTrackers,
      clearPropertyTrackers,
      clearDomainUrl,
      properties,
      loginData,
      appLink,
      clearCommunities,
    } = this.props;

    if (appLink) {
      if (!isIOS) {
        this.handleOpenURL(appLink);
      }

      this.handleOpenURLForAppLink(appLink);
    }

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

          if (!this.haveDomainURL) {
            loadPropertyTrackers({populateXvalue: true});
          }
        }
      } else {
        //logout
        this.pendingAction = null;
        //Uncomment the method below if want to uncomment the method below
        //clearDomainUrl();
        // clearAllShortlist();
        // clearPropertyTrackers();

        // Navigation.mergeOptions(this.props.componentId, {
        //   bottomTabs: {
        //     currentTabIndex: 0,
        //   },
        // });
      }
    }

    //User has logged in, updated properties and there is pendingAction
    if (
      prevProps.properties !== this.props.properties &&
      !ObjectUtil.isEmpty(this.pendingAction)
    ) {
      if (this.pendingAction.type === 'mysg-home') {
        if (
          this.props.userPO.encryptedUserId === this.pendingAction.query.userId
        ) {
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

    //Applink to community post, currently assume need to join community for applink
    if (
      prevProps.communities !== this.props.communities &&
      !ObjectUtil.isEmpty(this.pendingAction)
    ) {
      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        if (this.props.communities.length > 0) {
          this.directToCommunitiesPostDetails(this.pendingAction.postId);
        }
        this.pendingAction = null;
      }
    }

    //User has not logged in and there is pending action
    if (
      loginData.loadingState == LoadingState.Failed &&
      !ObjectUtil.isEmpty(this.pendingAction) &&
      prevProps.loginData.loadingState !== this.props.loginData.loadingState
    ) {
      if (this.pendingAction.type === 'mysg-home') {
        LoginStack.showSignInRegisterModal();
      }
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
    if (
      prevProps.serverDomain.domainURL !== this.props.serverDomain.domainURL &&
      !ObjectUtil.isEmpty(this.props.userPO)
    ) {
      Navigation.mergeOptions(this.props.componentId, {
        bottomTabs: {
          currentTabIndex: 0,
        },
      });
      loadPropertyTrackers({populateXvalue: true});
      this.haveDomainURL = false;
    }
  }

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
    const {userPO, clearAppLink} = this.props;
    console.log('HandleOpen');
    let parsedURL = parse(event, true);

    console.log(parsedURL);
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

    if (clearAppLink) {
      clearAppLink();
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

  //Go to listing results
  showListingResult = searchOptions => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.searchResult',
        passProps: {
          initialSearchOptions: searchOptions,
          onUpdateSearchOptions: this.onUpdateSearchOptions,
        },
      },
    });
  };

  //direct to hot project lists
  viewAllHotProjects = (latestProjects, featuredProjects) => {
    //get latest and featured projects
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.NewLaunchesProjectResult',
        passProps: {
          newLaunchLatestProjects: latestProjects,
          newLaunchFeaturedProjects: featuredProjects,
        },
      },
    });
  };

  directToCommunitiesPostDetails(postId) {
    GoogleAnalyticUtil.trackCommunityActivity({parameters: {type: 'detail'}});
    Navigation.push('HomeScreenId', {
      component: {
        name: 'Communities.CommunitiesPostDetails',
        passProps: {
          postId,
        },
      },
    });
  }

  navigateScreen() {
    const {navigateBottomTab} = this.props;
    if (navigateBottomTab) {
      navigateBottomTab(3);
    }
  }

  onUpdateSearchOptions = searchOptions => {
    if (!ObjectUtil.isEmpty(searchOptions)) {
      this.setState({searchOptions});
    }
  };

  isResidentialOrCommercial = selectedText => {
    const {searchOptions} = this.state;
    if (selectedText === 'Residential') {
      var newSearchOptions = {
        ...searchOptions,
        cdResearchSubTypes: Array.from(
          PropertyTypeValueSet.allResidential,
        ).join(','),
      };
      this.setState({isResidential: true, searchOptions: newSearchOptions});
    } else {
      var newSearchOptions = {
        ...searchOptions,
        cdResearchSubTypes: Array.from(PropertyTypeValueSet.commercial).join(
          ',',
        ),
      };
      this.setState({isResidential: false, searchOptions: newSearchOptions});
    }
  };

  //Go to listing results
  showListingResult = searchOptions => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.searchResult',
        passProps: {
          initialSearchOptions: searchOptions,
          onUpdateSearchOptions: this.onUpdateSearchOptions,
        },
      },
    });
  };

  onPressSeeAll = () => {
    const {onPressSeeAll} = this.props;
    if (onPressSeeAll) {
      onPressSeeAll();
    }
  };

  renderPropertySearchAndShortcutAccess() {
    const {searchOptions} = this.state;
    return (
      <PropertySearchAndShortcutAccess
        componentId={this.props.componentId}
        searchOptions={searchOptions}
        onUpdateSearchOptions={this.onUpdateSearchOptions}
        isResidentialOrCommercial={this.isResidentialOrCommercial}
        showListingResult={this.showListingResult}
        postSelected={this.directToCommunitiesPostDetails}
        onPressSeeAll={this.onPressSeeAll}
        onAddPropertyPressed={this.directToAddMyProperty}
      />
    );
  }

  renderMyPropertyAtAGlance() {
    const {isResidential} = this.state;
    if (isResidential) {
      return (
        <MyPropertyAtAGlance
          onViewAllMyProperty={this.directToUserProfile}
          directToMyProperty={this.directToMyProperty}
          onAddPropertyPressed={this.directToAddMyProperty}
          verifyOrCertifyPressed={this.directToAddMyPropertyForCertifyListing}
        />
      );
    }
  }

  //direct to user profile and my property list page
  directToUserProfile = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'UserAuth.Profile',
      },
    });
  };

  directToAddMyProperty = invitationInfo => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
          invitationInfo,
        },
      },
    });
  };

  directToAddMyPropertyForCertifyListing = srxPropertyUserPO => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
          propertyUserPO: srxPropertyUserPO,
          certifyListing: true,
        },
      },
    });
  };

  // go to property details
  directToMyProperty = (srxPropertyUserPO, certifyListing) => {
    //certify can be nullable
    if (!ObjectUtil.isEmpty(srxPropertyUserPO)) {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'MyProperties.Detail',
          passProps: {
            initialTrackerPO: srxPropertyUserPO,
            certifyListing,
          },
        },
      });
    }
  };

  renderCommercialSurvey() {
    const {isResidential} = this.state;
    return <CommercialSurvey isResidential={isResidential} />;
  }

  renderServicesShortcut() {
    const {isResidential} = this.state;
    if (isResidential) {
      return (
        <ServicesShortcut
          viewFindAgent={this.viewFindAgent}
          directToValuationsPage={this.directToValuationsPage}
          directToMortgagesPage={this.directToMortgagesPage}
          directToPropertyNews={this.directToPropertyNews}
        />
      );
    } else {
      return <View />;
    }
  }

  viewFindAgent = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.homeScreen',
      },
    });
  };

  //go to valuations web page
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

  //go to loan web page
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

  //go to property news
  directToPropertyNews = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertyNewsStack.PropertyNewsHomeScreen',
      },
    });
  };

  renderHotProjects() {
    return (
      <HotProjects
        onPressHotProjectItem={this.directToHotProjectWebScreen}
        onPressViewAll={this.viewAllHotProjects}
      />
    );
  }

  //Hot Project Event Section
  directToHotProjectWebScreen = projectDetailPO => {
    if (!ObjectUtil.isEmpty(projectDetailPO)) {
      //get URL and Name
      let projectURL = projectDetailPO.getProjectUrl();
      let projectName = projectDetailPO.getProjectName();
      //navigate to hot project web detail screen
      Navigation.push(this.props.componentId, {
        component: {
          name: 'PropertySearchStack.generalWebScreen',
          passProps: {
            url: projectURL,
            screenTitle: projectName,
          },
        },
      }); //end of navigation push
    }
  };

  renderFeaturedListings() {
    const {isResidential} = this.state;
    return (
      <FeaturedListings
        showListingDetails={this.showListingDetails}
        onViewAllListing={this.onViewAllListing}
        isResidential={isResidential}
      />
    );
  }

  //Go to listing details
  showListingDetails = ({listingPO}) => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'PropertySearchStack.ListingDetails',
          passProps: {
            listingId: listingPO.getListingId(),
            refType: listingPO.listingType,
          },
        },
      });
    }
  };

  //select on view all
  onViewAllListing = searchOptions => {
    const {properties, userPO} = this.props;
    const {isResidential} = this.state;
    var newSearchOptions = {
      ...searchOptions,
      type: 'S',
      displayText: 'Everywhere in Singapore',
      cdResearchSubTypes: isResidential
        ? Array.from(PropertyTypeValueSet.allResidential).join(',')
        : Array.from(PropertyTypeValueSet.commercial).join(','),
    };

    if (isResidential) {
      if (!ObjectUtil.isEmpty(userPO) && properties.length > 0) {
        if (PropertyTypeUtil.isHDB(properties[0].cdResearchSubtype)) {
          propertyType = PropertyTypeValueSet.hdb;
        } else if (PropertyTypeUtil.isCondo(properties[0].cdResearchSubtype)) {
          propertyType = PropertyTypeValueSet.condo;
        } else if (PropertyTypeUtil.isLanded(properties[0].cdResearchSubtype)) {
          propertyType = PropertyTypeValueSet.landed;
        }

        //recommended listings
        newSearchOptions = {
          ...newSearchOptions,
          cdResearchSubTypes: Array.from(propertyType).join(','),
          searchText: properties[0].address,
          displayText: properties[0].address,
        };
      } else {
        //featured listings
        newSearchOptions = {
          ...newSearchOptions,
          cdResearchSubTypes: Array.from(
            PropertyTypeValueSet.allResidential,
          ).join(','),
          vt360Filter: true,
        };
      }
    } else {
      //commercial listings
      newSearchOptions = {
        ...newSearchOptions,
        cdResearchSubTypes: Array.from(PropertyTypeValueSet.commercial).join(
          ',',
        ),
      };
    }
    this.showListingResult(newSearchOptions);
  };

  renderMarketWatch() {
    return <MarketWatch />;
  }

  renderPropertyNews() {
    return (
      <PropertyNews
        showNewsDetails={this.showNewsDetails}
        directToPropertyNews={this.directToPropertyNews}
      />
    );
  }

  showNewsDetails = (
    onlineCommunicationPO,
    indexNo,
    newsSearchOption,
    categoryTypeDescription,
  ) => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertyNewsStack.NewsDetailsResultList',
        passProps: {
          onlineCommunicationPO: onlineCommunicationPO,
          indexNo: indexNo,
          newsSearchOption: newsSearchOption,
          screenTitle: categoryTypeDescription,
        },
      },
    });
  };

  render() {
    const {hideComponents} = this.state;
    return (
      <ScrollView
        ref={component => (this.scrollView = component)}
        style={{flex: 1, backgroundColor: SRXColor.White}}>
        {/* // contentInset={{top: -1000}}
        // contentOffset={{y: 1000}}>
        // {isIOS ? (
          <View
            style={{
              height: 1000,
              width: '100%',
              backgroundColor: SRXColor.White,
            }}
          />
        ) : null} */}
        <View style={{flex: 1, backgroundColor: SRXColor.LightGray}}>
          {this.renderPropertySearchAndShortcutAccess()}
          {this.renderMyPropertyAtAGlance()}
          {this.renderCommercialSurvey()}
          {hideComponents == false ? this.renderServicesShortcut() : null}
          {this.renderHotProjects()}
          {this.renderFeaturedListings()}
          {hideComponents == false ? (
            <View>
              {this.renderMarketWatch()}
              {this.renderPropertyNews()}
            </View>
          ) : null}
        </View>
      </ScrollView>
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
    communities: state.communitiesData.list,
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
)(FindHomes);
