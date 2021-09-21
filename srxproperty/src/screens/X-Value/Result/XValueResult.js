import React, {Component} from 'react';
import {
  StyleSheet,
  View,
  Dimensions,
  ActivityIndicator,
  FlatList,
  SafeAreaView,
  Alert,
} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {
  Button,
  Text,
  Separator,
  LargeTitleComponent,
  Heading2,
} from '../../../components';
import {LoginStack} from '../../../config';
import {SRXColor, AlertMessage} from '../../../constants';
import {
  ObjectUtil,
  PropertyTypeUtil,
  CommonUtil,
  StringUtil,
  XValueUtil,
} from '../../../utils';
import {
  XValueService,
  SearchPropertyService,
  LoginService,
} from '../../../services';
import {ListingPO, AgentPO} from '../../../dataObject';
import {ListingsCardPreview} from '../../Listings';
import {AddressInfo, XValueInfo, FeaturedAgents} from './Components';

import {XTrend} from '../XTrend';
import {Spacing} from '../../../styles';
import {UpdateProfile} from '../../SideMenu';
import {SignInRegisterSource} from '../../UserAuthentication/Constants/UserAuthenticationConstants';

var {width} = Dimensions.get('window');

const SECTION_TYPE = {
  AddressInfo: 'AddressInfo',
  ValueResults: 'ValueResults',
  FeaturedAgents: 'FeaturedAgents',
  XTrend: 'XTrend',
  NearbyListings: 'NearbyListings',
};

const SOURCE = {
  SignIn: 'SignIn',
  SignUp: 'SignUp',
};

class XValueResult extends LargeTitleComponent {
  /**
   *
   * if required to update options,
   * must add ...super.options(passProps)
   *
   * The following commented code is an example
   */
  // static options(passProps) {
  //   return {
  //     ...super.options(passProps),
  //     bottomTabs: {
  //       visible: false
  //     }
  //   };
  // }

  state = {
    sections: [
      SECTION_TYPE.AddressInfo,
      SECTION_TYPE.ValueResults,
      SECTION_TYPE.FeaturedAgents,
      SECTION_TYPE.XTrend,
      // SECTION_TYPE.NearbyListings
    ],
    xValueResultPO: null, //state: 1) "loading", 2) null where no result, 3) {...} response object with parameters, 4) "error"
    featuredAgents: null,
    nearbyListings: null,
    projectDetail: null,
    xValueTrend: null,
  };

  constructor(props) {
    super(props);

    this.viewAgentCV = this.viewAgentCV.bind(this);
    this.onSuccessSignIn = this.onSuccessSignIn.bind(this);
    this.onSuccessSignUp = this.onSuccessSignUp.bind(this);
    this.mobileVerifyXValue = this.mobileVerifyXValue.bind(this);
  }

  componentDidMount() {
    var xValueResultPO = null;
    if (!ObjectUtil.isEmpty(this.props.initialXValueResultPO)) {
      xValueResultPO = XValueUtil.decryptXValueObj(
        this.props.initialXValueResultPO.xValueObj,
      );
    }

    if (!ObjectUtil.isEmpty(this.props.xValuePO)) {
      const {postalCode, streetId} = this.props.xValuePO;

      if (
        ((!ObjectUtil.isEmpty(postalCode) || postalCode > 0) &&
          !ObjectUtil.isEmpty(streetId)) ||
        streetId > 0
      ) {
        if (ObjectUtil.isEmpty(xValueResultPO)) {
          this.loadXValue();
        }
        this.loadXValueTrend();
        this.loadFeaturedAgent();
        this.loadNearbyListings();
        this.loadProjectDetails();
      } else {
        this.forceUpdate();
      }
    }

    this.setState({
      xValueResultPO,
    });
  }

  componentDidUpdate(prevProps) {
    if (prevProps.userPO !== this.props.userPO) {
      if (ObjectUtil.isEmpty(this.props.userPO)) {
        Navigation.popToRoot('XValueFormId');
      } else {
        this.setState(
          {
            sections: [
              SECTION_TYPE.AddressInfo,
              SECTION_TYPE.ValueResults,
              SECTION_TYPE.FeaturedAgents,
              SECTION_TYPE.XTrend,
              // SECTION_TYPE.NearbyListings
            ],
          },
          () => {
            this.loadXValue();
            this.loadXValueTrend();
            this.loadFeaturedAgent();
            this.loadNearbyListings();
          },
        );
      }
    }
  }

  mobileVerifyXValue = source => {
    const {userPO} = this.props;
    if (userPO.mobileVerified === true) {
      //If mobile number is already verified,
      //no need to show verify mobile number screen
      this.loadXValue();
    } else {
      if (source === SOURCE.SignIn) {
        if (userPO.mobileLocalNum) {
          this.requestMobileVerification();
        } else {
          Alert.alert(
            AlertMessage.SuccessMessageTitle,
            'Mobile Number needs to be verified before proceeding',
            [
              {text: 'Cancel', style: 'cancel'},
              {
                text: 'OK',
                onPress: () => this.goToUpdateProfile(),
              },
            ],
          );
        }
      } else if (source === SOURCE.SignUp) {
        if (userPO.mobileLocalNum) {
          this.requestMobileVerification();
        }
      }
    }
  };

  //requestMobileVerification API
  requestMobileVerification() {
    const {mobileLocalNum} = this.props.userPO;

    LoginService.requestMobileVerification({
      countryCode: '+65', //default
      mobile: mobileLocalNum,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === 'success') {
                this.directToVerfiyMobileNumber();
              }
            }
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  }

  goToUpdateProfile = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'SideMainMenu.UpdateProfile',
        passProps: {
          source: UpdateProfile.Sources.XValueResult,
          updateXValueResult: this.updateXValueResult.bind(this),
        },
      },
    });
  };

  directToVerfiyMobileNumber = () => {
    const {userPO} = this.props;
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'SideMainMenu.VerifyMobileNumber',
              passProps: {
                name: userPO.name,
                mobile: userPO.mobileLocalNum,
                updateXValueResult: this.updateXValueResult.bind(this),
              },
              options: {
                modalPresentationStyle: 'overFullScreen',
                topBar: {
                  visible: false,
                  drawBehind: true,
                },
              },
            },
          },
        ],
      },
    });
  };

  getScreenTitle() {
    const {xValuePO} = this.props;
    var addressString = '';
    if (!ObjectUtil.isEmpty(xValuePO)) {
      const {address, subType, floorNum, unitNum} = xValuePO;

      addressString = address;

      if (
        (PropertyTypeUtil.isCondo(subType) ||
          PropertyTypeUtil.isHDB(subType)) &&
        !ObjectUtil.isEmpty(floorNum) &&
        !ObjectUtil.isEmpty(unitNum)
      ) {
        addressString = addressString + ', #' + floorNum + '-' + unitNum;
      }
    }

    return addressString;
  }

  loadXValue() {
    const {
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      subType,
      landType,
      size,
      streetId,
      gfa,
      pesSize,
      tenure,
      lastConstructed,
    } = this.props.xValuePO;
    this.setState({
      xValueResultPO: 'loading',
    });
    var sizeInSqft = size;
    if (PropertyTypeUtil.isHDB(subType)) {
      sizeInSqft = CommonUtil.convertMetreToFeet(size);
    }
    XValueService.promotionGetXValue({
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      subType,
      landType,
      streetId,
      size: sizeInSqft,
      builtSizeGfa: gfa,
      pesSize,
      tenure,
      builtYear: lastConstructed,
    })
      .catch(error => {
        this.setState({
          xValueResultPO: 'error',
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({
              xValueResultPO: 'error',
            });
          } else {
            let newResult = XValueUtil.decryptXValueObj(response.xValueObj);
            this.setState({
              xValueResultPO: newResult,
            });
          }
        } else {
          this.setState({
            xValueResultPO: null,
          });
        }
      });
  }

  loadXValueTrend() {
    const {
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      subType,
      landType,
      size,
      streetId,
      gfa,
      pesSize,
      tenure,
      lastConstructed,
    } = this.props.xValuePO;
    this.setState({
      xValueTrend: 'loading',
    });
    var sizeInSqft = size;
    if (PropertyTypeUtil.isHDB(subType)) {
      sizeInSqft = CommonUtil.convertMetreToFeet(size);
    }
    XValueService.promotionGetXValueTrend({
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      subType,
      landType,
      streetId,
      size: sizeInSqft,
      builtSizeGfa: gfa,
      pesSize,
      tenure,
      builtYear: lastConstructed,
    })
      .catch(error => {
        this.setState({
          xValueTrend: 'error',
        });
      })
      .then(response => {
        if (
          !ObjectUtil.isEmpty(response) &&
          !ObjectUtil.isEmpty(response.xValueObjTrend)
        ) {
          let newResult = XValueUtil.decryptXValueObj(response.xValueObjTrend);
          this.setState({
            xValueTrend: newResult,
          });
        } else {
          this.setState({
            xValueTrend: [],
          });
          this.removeSection(SECTION_TYPE.XTrend);
        }
      });
  }

  loadFeaturedAgent() {
    const {postalCode, streetId} = this.props.xValuePO;
    this.setState({
      featuredAgents: 'loading',
    });
    XValueService.getXValueFeaturedAgent({
      postalCode,
      crsId: streetId,
    })
      .catch(error => {
        this.setState({
          featuredAgents: 'error',
        });
      })
      .then(response => {
        var agentResults = [];
        if (
          !ObjectUtil.isEmpty(response) &&
          !ObjectUtil.isEmpty(response.result)
        ) {
          const {result} = response;
          if (
            !ObjectUtil.isEmpty(result.agentPOs) &&
            Array.isArray(result.agentPOs)
          ) {
            result.agentPOs.map(item => {
              agentResults.push(new AgentPO(item));
            });
          }
        }

        if (ObjectUtil.isEmpty(agentResults)) {
          this.removeSection(SECTION_TYPE.FeaturedAgents);
        } else {
          this.setState({featuredAgents: agentResults});
        }
      });
  }

  loadNearbyListings() {
    const {subType, streetId, type} = this.props.xValuePO;

    this.setState({
      nearbyListings: 'loading',
    });
    XValueService.loadXValueProjectListings({
      projectId: streetId,
      isSale: type == 'S',
      cdResearchSubTypes: subType,
    })
      .catch(error => {
        this.setState({
          nearbyListings: 'error',
        });
      })
      .then(response => {
        var listings = null;

        if (
          !ObjectUtil.isEmpty(response) &&
          !ObjectUtil.isEmpty(response.listings) &&
          !ObjectUtil.isEmpty(response.listings.srxListings)
        ) {
          const {listingPOs} = response.listings.srxListings;
          if (Array.isArray(listingPOs)) {
            // listings = listingPOs;
            listings = [];
            listingPOs.map((item, index) => {
              listings.push(new ListingPO({...item, key: index}));
            });
          }
        }

        if (ObjectUtil.isEmpty(listings)) {
          this.removeSection(SECTION_TYPE.NearbyListings);
        } else {
          this.setState({
            nearbyListings: listings,
          });
        }
      });
  }

  loadProjectDetails() {
    const {streetId, postalCode} = this.props.xValuePO;

    this.setState({
      projectDetail: 'loading',
    });
    SearchPropertyService.loadFullProjectDetails({
      crsId: streetId,
      postalCode,
    })
      .catch(error => {
        this.setState({
          projectDetail: 'error',
        });
      })
      .then(response => {
        var project = null;
        if (
          !ObjectUtil.isEmpty(response) &&
          !ObjectUtil.isEmpty(response.project)
        ) {
          project = response.project;
        }

        this.setState({
          projectDetail: project,
        });
      });
  }

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

  viewAgentCV(item) {
    XValueService.trackAgentAdViewMobileInXValue({
      agentPO: item,
      source: 'A',
      viewId: 230, //230 = x-value
    }); //no need response

    Navigation.push(this.props.componentId, {
      component: {
        name: 'ConciergeStack.AgentCV',
        passProps: {
          agentUserId: item.getAgentId(),
        },
      },
    });
  }

  signUpUser = () => {
    const passProps = {
      source: SignInRegisterSource.XValueResult,
      onSuccessSignUp: this.onSuccessSignUp,
      type: 'R',
    };
    LoginStack.showSignInRegisterModal(passProps);
  };

  onSuccessSignUp = () => {
    this.mobileVerifyXValue(SOURCE.SignUp);
  };

  signInUser = () => {
    const passProps = {
      source: SignInRegisterSource.XValueResult,
      onSuccessSignIn: this.onSuccessSignIn,
      type: 'S',
    };
    LoginStack.showSignInRegisterModal(passProps);
  };

  onSuccessSignIn = () => {
    this.mobileVerifyXValue(SOURCE.SignIn);
  };

  updateXValueResult = () => {
    const {userPO} = this.props;
    if (userPO.mobileVerified) {
      this.loadXValue();
    }
  };

  removeSection(sectionItem) {
    const {sections} = this.state;
    const index = sections.indexOf(sectionItem);
    if (index >= 0) {
      const newSection = [...sections];
      newSection.splice(index, 1);

      this.setState({sections: newSection});
    }
  }

  renderAddressBar(sectionIndex) {
    var projectDetail;
    if (
      !ObjectUtil.isEmpty(this.state.projectDetail) &&
      typeof this.state.projectDetail !== 'string'
    ) {
      projectDetail = this.state.projectDetail;
    }
    return (
      <AddressInfo
        xValuePO={this.props.xValuePO}
        projectDetail={projectDetail}
        style={this.backgroundStyleForSection(sectionIndex)}
      />
    );
  }

  renderValues(sectionIndex) {
    const {xValueTrend, xValueResultPO} = this.state;
    const {userPO} = this.props;
    const userSignedIn = !ObjectUtil.isEmpty(userPO);
    const concealValue = !ObjectUtil.isEmpty(userPO)
      ? userPO.mobileVerified
      : null;
    return (
      <View style={this.backgroundStyleForSection(sectionIndex)}>
        <XValueInfo
          xValueResultPO={xValueResultPO}
          xValueTrend={xValueTrend}
          concealValue={!concealValue}
        />
        {!userSignedIn ? this.renderLoginContainer() : null}
      </View>
    );
  }

  renderFeaturedAgents(sectionIndex) {
    const {featuredAgents} = this.state;
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO) && userPO.mobileVerified === true) {
      if (featuredAgents == 'loading') {
        return (
          <View style={this.backgroundStyleForSection(sectionIndex)}>
            <View
              style={{
                alignItems: 'center',
                height: 100,
                justifyContent: 'center',
              }}>
              <ActivityIndicator />
            </View>
          </View>
        );
      } else if (
        !ObjectUtil.isEmpty(featuredAgents) &&
        featuredAgents.length > 0 &&
        Array.isArray(featuredAgents) &&
        !ObjectUtil.isEmpty(userPO) &&
        userPO.mobileVerified === true
      ) {
        return (
          <View style={this.backgroundStyleForSection(sectionIndex)}>
            <FeaturedAgents
              featuredAgentPOs={featuredAgents}
              onAgentSelected={selectedAgent => this.viewAgentCV(selectedAgent)}
            />
          </View>
        );
      } else {
        return null;
      }
    }
  }

  renderNearbyListings(sectionIndex) {
    const {nearbyListings} = this.state;
    const {userPO} = this.props;

    const sectionTitle =
      'Similar Properties for ' +
      (this.props.xValuePO.type == 'S' ? 'Sale' : 'Rent');

    if (
      nearbyListings == 'loading' ||
      (!ObjectUtil.isEmpty(nearbyListings) &&
        nearbyListings.length > 0 &&
        !ObjectUtil.isEmpty(userPO) &&
        userPO.mobileVerified === true)
    ) {
      return (
        <ListingsCardPreview
          data={nearbyListings}
          title={sectionTitle}
          onListingSelected={selectedListing =>
            this.showListingDetails({listingPO: selectedListing})
          }
          style={this.backgroundStyleForSection(sectionIndex)}
        />
      );
    } else {
      return null;
    }
  }

  renderXTrendGraph(sectionIndex) {
    const {xValuePO, userPO} = this.props;
    if (!ObjectUtil.isEmpty(xValuePO)) {
      const {
        type,
        postalCode,
        floorNum,
        unitNum,
        buildingNum,
        subType,
        landType,
        size,
        streetId,
        gfa,
        pesSize,
        tenure,
        lastConstructed,
      } = this.props.xValuePO;

      const parameters = {
        postalCode,
        cdResearchSubtype: subType,
        unitFloor: floorNum,
        unitNo: unitNum,
        sizeInSqm: PropertyTypeUtil.isHDB(subType)
          ? size
          : CommonUtil.convertFeetToMetre(size),
        isSale: type == 'S',
        buildingNum,
        streetId,
        builtSizeGfa: gfa,
        pesSize,
        tenure,
        builtYear: lastConstructed,
        landType,
      };
      if (!ObjectUtil.isEmpty(userPO) && userPO.mobileVerified === true) {
        return (
          <XTrend
            xTrendParameters={parameters}
            showValues={false}
            style={this.backgroundStyleForSection(sectionIndex)}
            concealValue={ObjectUtil.isEmpty(userPO)}
          />
        );
      }
    }
  }

  renderContentItems({item, index}) {
    if (item === SECTION_TYPE.AddressInfo) {
      return this.renderAddressBar(index);
    } else if (item === SECTION_TYPE.ValueResults) {
      return this.renderValues(index);
    } else if (item === SECTION_TYPE.FeaturedAgents) {
      return this.renderFeaturedAgents(index);
    } else if (item === SECTION_TYPE.XTrend) {
      return this.renderXTrendGraph(index);
    } else if (item === SECTION_TYPE.NearbyListings) {
      return this.renderNearbyListings(index);
    } else {
      return <View />;
    }
  }

  backgroundStyleForSection(section) {
    return section % 2 == 0
      ? styles.evenSectionBackground
      : styles.oddSectionBackground;
  }

  renderLoginContainer() {
    return (
      <View style={{alignItems: 'center'}}>
        <Heading2 style={{marginBottom: Spacing.S}}>
          X-Value is only available to registered users
        </Heading2>
        <Button
          buttonType={Button.buttonTypes.primary}
          buttonStyle={{marginBottom: Spacing.S}}
          onPress={this.signUpUser}>
          Sign up
        </Button>
        <Button
          buttonStyle={{marginBottom: Spacing.S}}
          onPress={this.signInUser}>
          I have an account, sign me in
        </Button>
      </View>
    );
  }

  render() {
    const {sections} = this.state;
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: 'white'}}>
        <FlatList
          onScroll={this.onScroll}
          style={{flex: 1}}
          data={sections}
          renderItem={({item, index}) => this.renderContentItems({item, index})}
          keyExtractor={(item, index) => item}
          ItemSeparatorComponent={() => <Separator />}
          ListHeaderComponent={() =>
            this.renderLargeTitle(this.getScreenTitle())
          }
        />
      </SafeAreaView>
    );
  }
}

XValueResult.propTypes = {
  xValuePO: PropTypes.object,
  /**
   * xvalue result
   * passing from previous screen
   * if not provided then will call "loadXValue" in this screen
   */
  initialXValueResultPO: PropTypes.object,
};

const styles = StyleSheet.create({
  valueContainer: {
    alignItems: 'center',
  },
  valueStyle: {
    fontSize: 23,
    color: 'white',
    fontWeight: 'bold',
    textAlign: 'center',
  },
  valueDescStyle: {
    fontSize: 12,
    color: 'white',
    textAlign: 'center',
  },
  projectInfoRow: {
    flexDirection: 'row',
    alignItems: 'stretch',
  },
  projectInfoItemContainer: {
    padding: 5,
    paddingBottom: 3,
  },
  projectInfoTitle: {
    fontSize: 12,
    color: SRXColor.Gray,
  },
  projectInfoValue: {
    fontSize: 12,
    color: SRXColor.Black,
  },
  evenSectionBackground: {
    backgroundColor: SRXColor.White,
  },
  oddSectionBackground: {
    backgroundColor: SRXColor.AccordionBackground,
  },
});

const mapStateToProps = state => {
  return {shortlistData: state.shortlistData, userPO: state.loginData.userPO};
};

export default connect(mapStateToProps)(XValueResult);
