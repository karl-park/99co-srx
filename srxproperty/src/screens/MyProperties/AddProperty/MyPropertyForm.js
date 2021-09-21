import React, {Component} from 'react';
import {
  View,
  FlatList,
  Image,
  SafeAreaView,
  TouchableOpacity,
  Platform,
  Alert,
} from 'react-native';
import {ListingPO} from '../../../dataObject';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import Moment from 'moment';
import {connect} from 'react-redux';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {loadPropertyTrackers, updateTracker} from '../../../actions';
import {
  Avatar,
  Text,
  Heading2,
  FeatherIcon,
  Picker,
  TextInput,
  BodyText,
  Button,
  LargeTitleComponent,
  SmallBodyText,
} from '../../../components';
import {CertifiedListingService} from '../../../services';
import {
  SRXColor,
  pt_Occupancy,
  LoadingState,
  AppConstant,
  AlertMessage,
} from '../../../constants';
import {SRXPropertyUserPO} from '../../../dataObject';
import {
  AddressAutoComplete,
  AutoCompletePurposes,
  CertifyListingListItem,
} from '../../../screens';
import {AddressDetailService, PropertyTrackerService} from '../../../services';
import {Spacing, InputStyles} from '../../../styles';
import {
  ObjectUtil,
  PropertyOptionsUtil,
  CommonUtil,
  PropertyTypeUtil,
  StringUtil,
} from '../../../utils';
import {MyPropertyOccupancyAndPurpose} from './MyPropertyOccupancyAndPurpose';
import {MyPropertyOccupancy, MyPropertyPurpose} from '../components';

const isIOS = Platform.OS === 'ios';

class MyPropertyForm extends LargeTitleComponent {
  static defaultProps = {};

  state = {
    /**
     * Using this to indicate if this is for updating or adding new property
     * To be changed to enum if there is more types
     */
    isUpdate: false,
    signUpPO: {
      address: null,
      postalCode: null,
      buildingNumber: null,
      streetId: null,
      floorNum: null,
      unitNum: null,
      size: null,
      cdResearchSubtype: null,
      landType: null,
      tenure: null,
      lastConstructed: null,
      occupancy: null,
      purpose: null,
      ownerNric: '',
    },
    floorEdited: false,
    error: {
      subTypeError: null,
      occupancyError: null,
      purposeError: null,
    },
    certifyViewIndicator: true, //flag to update empty view for scroll to bottom function
    scrollCall: true,
    propertyTypeOptions: PropertyOptionsUtil.getPropertyTypesArrayForSubtype(),
    typeOfAreaOptions: PropertyOptionsUtil.getTypesOfAreas(),
    tenureOptions: PropertyOptionsUtil.getTenures(),
    builtYearOptions: PropertyOptionsUtil.getBuiltYears(),
    certifyListingArray: [],
  };

  constructor(props) {
    super(props);

    this.renderCertifyListingItem = this.renderCertifyListingItem.bind(this);
    this.onUnitFloorNumEndEditing = this.onUnitFloorNumEndEditing.bind(this);
    this.onSelectOccupancyAndPurpose = this.onSelectOccupancyAndPurpose.bind(
      this,
    );
    this.onAddOrUpdateBtnPress = this.onAddOrUpdateBtnPress.bind(this);
    this.confirmSignUp = this.confirmSignUp.bind(this);
    this.onPropertySuccessfullyAdded = this.onPropertySuccessfullyAdded.bind(
      this,
    );

    const {propertyUserPO} = this.props;
    if (ObjectUtil.isEmpty(propertyUserPO)) {
      this.showAddressAutoComplete();
    }
  }

  componentDidMount() {
    const {propertyUserPO, certifyListing} = this.props;
    if (!ObjectUtil.isEmpty(propertyUserPO)) {
      this.setState(
        {
          isUpdate: true,
          signUpPO: {
            address: propertyUserPO.address,
            postalCode: propertyUserPO.postalCode,
            buildingNumber: propertyUserPO.buildingNumber,
            streetId: propertyUserPO.streetId,
            floorNum: propertyUserPO.unitFloor,
            unitNum: propertyUserPO.unitNo,
            cdResearchSubtype: propertyUserPO.cdResearchSubtype,
            size: propertyUserPO.size,
            landType: propertyUserPO.landType,
            tenure: propertyUserPO.tenure,
            lastConstructed: propertyUserPO.builtYear,
            occupancy: propertyUserPO.occupancy,
            purpose: propertyUserPO.purpose,
            dateOwnerNricVerified: propertyUserPO.dateOwnerNricVerified,
          },
        },
        () => {
          console.log('Date Verified NRIC');
          console.log(this.state.signUpPO.dateOwnerNricVerified);
          if (certifyListing) {
            if (
              !ObjectUtil.isEmpty(this.state.signUpPO.dateOwnerNricVerified)
            ) {
              this.retrieveCertifyListing();
            } else {
              setTimeout(() => {
                this.scrollView.props.scrollToEnd();
                this.setState({certifyViewIndicator: false});
              }, 300);
            }
          } else {
            if (
              !ObjectUtil.isEmpty(this.state.signUpPO.dateOwnerNricVerified)
            ) {
              this.retrieveCertifyListing();
            }
          }
        },
      );
    }
  }

  retrieveCertifyListing = () => {
    const {propertyUserPO} = this.props;
    console.log('ptuserId');
    console.log(propertyUserPO.ptUserId);
    CertifiedListingService.findListedListings({
      ptUserId: propertyUserPO.ptUserId,
    }).then(response => {
      console.log('retrieve certify listing');
      console.log(response);
      if (!ObjectUtil.isEmpty(response)) {
        const {rentListings, saleListings} = response;
        if (
          !ObjectUtil.isEmpty(rentListings) ||
          !ObjectUtil.isEmpty(saleListings)
        ) {
          var newList = rentListings.concat(saleListings);
          this.setState(
            {
              certifyListingArray: newList,
              certifyViewIndicator: false,
            },
            () => {
              if (this.props.certifyListing) {
                setTimeout(() => {
                  this.scrollView.props.scrollToEnd();
                }, 300);
              }
            },
          );
        } else {
          this.setState({
            certifyListingArray: [],
            certifyViewIndicator: false,
          });
        }
      }
    });
  };

  showAddressAutoComplete = () => {
    AddressAutoComplete.show({
      onSuggestionSelected: this.closeAddressAutoComplete,
      purpose: AutoCompletePurposes.xValue,
    });
  };

  closeAddressAutoComplete = selectedDict => {
    if (!ObjectUtil.isEmpty(selectedDict)) {
      AddressDetailService.getAddressByPostalCode({
        postalCode: selectedDict.postalCode,
        skipCommercial: 'Y',
      })
        .catch(error => {
          this.setState({error: {}});
        })
        .then(response => {
          var propertySubType = 0,
            propertyType = 0;
          var typeOfAreaToUpdate = '';
          var possiblyNoUnit = false;

          if (!ObjectUtil.isEmpty(response.data)) {
            const {data} = response;

            if (data.propertySubType > 0) {
              propertySubType = data.propertySubType;
            }
            if (data.propertyType > 0) {
              propertyType = data.propertyType;
            }

            possiblyNoUnit = data.possiblyNoUnit;

            //type of area
            if (PropertyTypeUtil.isLanded(propertySubType)) {
              if (!ObjectUtil.isEmpty(data.typeOfArea)) {
                const typeOfAreaUpperCase = data.typeOfArea.toUpperCase();
                if (typeOfAreaUpperCase == 'STRATA') {
                  typeOfAreaToUpdate = 'S';
                } else if (typeOfAreaUpperCase == 'LAND') {
                  typeOfAreaToUpdate = 'L';
                }
              }
            }
          }

          var propertyTypesArray = [];
          if (propertySubType > 0) {
            propertyTypesArray = PropertyOptionsUtil.getPropertyTypesArrayForSubtype(
              propertySubType,
            );
          } else if (propertyType > 0) {
            propertyTypesArray = PropertyOptionsUtil.getPropertyTypesArrayForType(
              propertyType,
            );
          } else {
            propertyTypesArray = PropertyOptionsUtil.getPropertyTypesArrayForType();
          }

          var selectedPropertyType = ''; //to get string value
          if (propertySubType > 0) {
            if (isNaN(propertySubType)) {
              selectedPropertyType = propertySubType;
            } else {
              selectedPropertyType = propertySubType.toString();
            }

            if (PropertyTypeUtil.isLanded(selectedPropertyType)) {
              const {floorNum, unitNum} = this.state.signUpPO;
              this.getPropertyInfo({
                propertySubType: selectedPropertyType,
                postalCode: selectedDict.postalCode,
                possiblyNoUnit,
              }); //exclude unit number at this stage, it should be cleared
            }
          }

          //update state
          this.setState({
            error: {},
            signUpPO: {
              ...this.state.signUpPO,
              address: selectedDict.address,
              postalCode: selectedDict.postalCode,
              buildingNumber: selectedDict.buildingNum,
              propertyType,
              cdResearchSubtype: selectedPropertyType,
              landType: typeOfAreaToUpdate,
              possiblyNoUnit,

              //clear values
              size: null,
              streetId: null,
              gfa: null,
              pesSize: null,
              tenure: null,
              lastConstructed: null,
              unitNum: null,
              floorNum: null,
            },
            propertyTypeOptions: propertyTypesArray,
          });
        });
    }
  };

  onPropertyTypeSelected = itemValue => {
    this.setState({
      signUpPO: {
        ...this.state.signUpPO,
        cdResearchSubtype: isIOS ? itemValue : itemValue.value,
      },
    });

    if (PropertyTypeUtil.isLanded(itemValue)) {
      const {
        postalCode,
        floorNum,
        unitNum,
        possiblyNoUnit,
      } = this.state.signUpPO;
      this.getPropertyInfo({
        postalCode,
        floorNum,
        unitNum,
        propertySubType: itemValue,
        possiblyNoUnit,
      });
    }
  };

  getPropertyInfo({
    propertySubType,
    postalCode,
    floorNum,
    unitNum,
    possiblyNoUnit,
  }) {
    // if (propertySubType && postalCode && (PropertyTypeUtil.isLanded(propertySubType) || possiblyNoUnit || (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum)))) {
    AddressDetailService.getProject({
      // postal, type, floor, unit, typeOfArea, buildingNum
      postal: postalCode,
      type: propertySubType,
      floor: floorNum,
      unit: unitNum,
    })
      .catch(error => {})
      .then(response => {
        if (response.error) {
        } else if (response.Error) {
        } else {
          if (
            !ObjectUtil.isEmpty(response) &&
            !ObjectUtil.isEmpty(response.data) &&
            Array.isArray(response.data)
          ) {
            const {data} = response;

            var correspondingResponse;
            if (data.length > 1) {
              for (var i = 0; i < data.length; i++) {
                const item = data[i];
                if (item.type == propertySubType) {
                  correspondingResponse = item;
                  break;
                }
              }
            }

            if (!correspondingResponse) {
              correspondingResponse = data[0];
            }

            //PES
            var pesSizeToUpdate;
            if (correspondingResponse.pesSize > 0) {
              if (PropertyTypeUtil.isHDB(propertySubType)) {
                pesSizeToUpdate = CommonUtil.convertFeetToMetre(
                  pesSizeToUpdate,
                );
              } else {
                pesSizeToUpdate = correspondingResponse.pesSize;
              }
            }

            //tenure
            var tenureValueToUpdate;
            if (!ObjectUtil.isEmpty(correspondingResponse.tenure)) {
              const tenureUpperCase = correspondingResponse.tenure.toUpperCase();

              if (tenureUpperCase == 'LEASE') tenureValueToUpdate = '2';
              else if (
                tenureUpperCase == 'FREEHOLD' ||
                tenureUpperCase == 'FREE'
              ) {
                tenureValueToUpdate = '1';
              } else {
                tenureValueToUpdate = '';
              }
            }

            //last constructed
            //Show the built year
            Moment.locale('en');
            var builtYrToUpdate = '';
            if (!ObjectUtil.isEmpty(correspondingResponse.lastConstructed)) {
              builtYrToUpdate = Moment(
                correspondingResponse.lastConstructed,
              ).format('YYYY');
            }

            if (correspondingResponse.size <= 0) {
              Alert.alert(
                AlertMessage.SuccessMessageTitle,
                'We are unable to default the size because we do not have that information. Please key in the size if you know.',
              );
            }

            this.setState({
              error: {...this.state.error, sizeError: null},
              signUpPO: {
                ...this.state.signUpPO,
                size:
                  correspondingResponse.size > 0
                    ? correspondingResponse.size
                    : 'N.A.',
                streetId: correspondingResponse.streetId,
                gfa:
                  correspondingResponse.gfa > 0 &&
                  PropertyTypeUtil.isLanded(propertySubType)
                    ? correspondingResponse.gfa
                    : null,
                pesSize: pesSizeToUpdate,
                tenure: tenureValueToUpdate,
                lastConstructed: builtYrToUpdate,
              },
            });
          } else {
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              'No project found or Wrong property type',
            );
          }
        }
      });
    // }
  }

  getSizeClicked() {
    const {
      cdResearchSubtype,
      postalCode,
      floorNum,
      unitNum,
      possiblyNoUnit,
    } = this.state.signUpPO;
    if (
      cdResearchSubtype &&
      postalCode &&
      (PropertyTypeUtil.isLanded(cdResearchSubtype) ||
        possiblyNoUnit ||
        (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum)))
    ) {
      this.getPropertyInfo({
        propertySubType: cdResearchSubtype,
        postalCode,
        floorNum,
        unitNum,
        possiblyNoUnit,
      });
    } else {
      if (cdResearchSubtype <= 0) {
      } else if (!PropertyTypeUtil.isLanded(cdResearchSubtype)) {
      }
    }
  }

  onUnitFloorNumEndEditing() {
    const {signUpPO} = this.state;
    const {
      cdResearchSubtype,
      postalCode,
      floorNum,
      unitNum,
      possiblyNoUnit,
    } = signUpPO;

    if (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum)) {
      this.getPropertyInfo({
        propertySubType: cdResearchSubtype,
        postalCode,
        floorNum,
        unitNum,
        possiblyNoUnit,
      });
    }
  }

  onSelectOccupancyAndPurpose({occupancy, purpose}) {
    this.setState({
      signUpPO: {
        ...this.state.signUpPO,
        occupancy,
        purpose,
        ownerNric:
          occupancy === pt_Occupancy.Own ? this.state.signUpPO.ownerNric : null,
      },
      error: {
        ...this.state.error,
        occupancyError: null,
        purposeError: null,
      },
    });
  }

  onAddOrUpdateBtnPress() {
    this.setState(
      {
        buttonState: LoadingState.Loading,
      },
      () => {
        const {signUpPO, isUpdate} = this.state;

        const {
          postalCode,
          unitNum,
          floorNum,
          possiblyNoUnit,
          cdResearchSubtype,
          size,
          streetId,
          landType,
          tenure,
          lastConstructed,
          occupancy,
          purpose,
        } = signUpPO;

        const error = {};

        if (!isUpdate) {
          if (
            (isNaN(postalCode) && ObjectUtil.isEmpty(postalCode)) ||
            postalCode <= 0
          ) {
            error.addressError = 'Required field';
          }
          if (
            (isNaN(cdResearchSubtype) &&
              ObjectUtil.isEmpty(cdResearchSubtype)) ||
            cdResearchSubtype <= 0
          ) {
            error.subTypeError = 'Required field';
          }

          if (PropertyTypeUtil.isLanded(cdResearchSubtype)) {
            if (ObjectUtil.isEmpty(landType)) {
              error.landTypeError = 'Required field';
            }
            if (ObjectUtil.isEmpty(tenure)) {
              error.tenureError = 'Required field';
            }
            if (lastConstructed <= 0) {
              error.lastConstructError = 'Required field';
            }
          }
        }

        if (
          !(possiblyNoUnit || PropertyTypeUtil.isLanded(cdResearchSubtype)) &&
          ObjectUtil.isEmpty(floorNum) &&
          ObjectUtil.isEmpty(unitNum)
        ) {
          error.unitNumError = 'Required field';
        }
        if (!(size > 0)) {
          error.sizeError = 'Required field';
        }

        if (!occupancy) {
          error.occupancyError = 'Required field';
        }

        if (occupancy === pt_Occupancy.Own) {
          if (!purpose) {
            error.purposeError = 'Required field';
          }
        }

        if (!ObjectUtil.isEmpty(error)) {
          this.setState({error, buttonState: LoadingState.Normal});
        } else {
          this.setState({error, buttonState: LoadingState.Loading}, () => {
            if (isUpdate) {
              this.updateProperty();
            } else {
              const {userPO} = this.props;
              /**
               * In this class it is assume userPO is not empty
               */
              if (!ObjectUtil.isEmpty(userPO)) {
                const {mobileLocalNum} = userPO;
                if (!ObjectUtil.isEmpty(mobileLocalNum) || mobileLocalNum > 0) {
                  this.addProperty();
                } else {
                  //go to mobile verification screen to enter mobile number
                  this.setState(
                    {
                      buttonState: LoadingState.Normal,
                    },
                    () => this.directToVerifyMobile(this.getSignUpParams()),
                  );
                }
              }
            }
          });
        }
      },
    );
  }

  getSignUpParams() {
    /**
     * returning object of the parameters required to add property
     * exclude mobile number
     */

    const {signUpPO} = this.state;

    const {
      postalCode,
      unitNum,
      floorNum,
      buildingNumber,
      cdResearchSubtype,
      size,
      streetId,
      landType,
      tenure,
      lastConstructed,
      purpose,
      occupancy,
      ownerNric,
    } = signUpPO;

    return {
      postalCode,
      buildingNumber,
      cdResearchSubtype,
      size,
      streetId,
      unitFloor: floorNum,
      unitNo: unitNum,
      tenure,
      landType,
      builtYear: lastConstructed,
      purpose,
      occupancy,
      ownerNric,
    };
  }

  addProperty() {
    const {userPO, invitationInfo} = this.props;
    let parameters = this.getSignUpParams();
    if (!ObjectUtil.isEmpty(userPO)) {
      parameters = {
        ...parameters,
        mobile: userPO.mobileLocalNum,
      };
    }
    if (!ObjectUtil.isEmpty(invitationInfo)) {
      parameters = {
        ...parameters,
        inviteId: invitationInfo.invitedId,
        source: invitationInfo.source,
      };
    }
    PropertyTrackerService.signUp(parameters)
      .catch(error => {
        this.setState(
          {
            buttonState: LoadingState.Normal,
          },
          () => {
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              'Error encountered. Please try again.',
            );
          },
        );
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(errorMsg)) {
            this.setState(
              {
                buttonState: LoadingState.Normal,
              },
              () => {
                Alert.alert(AlertMessage.ErrorMessageTitle, errorMsg);
              },
            );
          } else {
            const {result, isOtpVerified} = response;
            if (!ObjectUtil.isEmpty(result) || result > 0) {
              //according to documentation, result is ptIserId
              if (isOtpVerified) {
                //call confirmSignup
                this.confirmSignUp(result, userPO.mobileLocalNum);
              } else {
                //direct to otp
                this.setState(
                  {
                    buttonState: LoadingState.Normal,
                  },
                  () => {
                    //going to otp page directly, ,skipping the mobile input screen
                    //the mobileLocalNum is existed to enter addProperty method
                    this.directToOTPVerification(result, userPO.mobileLocalNum);
                  },
                );
              }
            } else {
              this.setState({
                buttonState: LoadingState.Normal,
              });
            }
          }
        }
      });
  }

  confirmSignUp(ptUserId, mobile) {
    PropertyTrackerService.confirmSignup({
      ptUserId,
      mobile,
      hasAgreedToPDPA: true,
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(response);
        if (!ObjectUtil.isEmpty(errorMsg)) {
          Alert.alert(AlertMessage.ErrorMessageTitle, errorMsg);
        } else {
          const {result} = response;
          if (result === 'success') {
            //reload tracker array
            this.onPropertySuccessfullyAdded();
          }
        }
      }
    });
  }

  updateProperty() {
    const {
      propertyUserPO,
      updateTracker,
      initiatingComponentId,
      onUpdateSRXPropertyPO,
    } = this.props;
    const {signUpPO, floorEdited} = this.state;

    const {size, floorNum, unitNum, purpose, occupancy, ownerNric} = signUpPO;

    let data = {...propertyUserPO, saleXvalue: null, rentXvalue: null};
    if (size > 0) {
      data.size = size;
    }
    if (!ObjectUtil.isEmpty(floorNum)) {
      data.unitFloor = floorNum;
    }
    if (!ObjectUtil.isEmpty(unitNum)) {
      data.unitNo = unitNum;
    }
    if (occupancy > 0) {
      data.occupancy = occupancy;
    }
    if (purpose > 0) {
      data.purpose = purpose;
    }
    if (!ObjectUtil.isEmpty(ownerNric)) {
      data.ownerNric = ownerNric;
    }

    if (floorEdited) {
      Alert.alert(
        'SRX',
        'By changing your address, you need to verify your home ownership',
        [
          {text: 'Cancel'},
          {
            text: 'Update Now',
            onPress: () => {
              this.setState({certifyListingArray: []});
              this.updateUserProperty(data);
            },
          },
        ],
      );
    } else {
      this.updateUserProperty(data);
    }
  }

  updateUserProperty(data) {
    const {updateTracker} = this.props;
    PropertyTrackerService.updatePropertyUserV2({
      srxPropertyUserPO: data,
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const errorMsg = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(errorMsg)) {
            this.setState(
              {
                buttonState: LoadingState.Normal,
              },
              () => {
                Alert.alert(AlertMessage.ErrorMessageTitle, errorMsg);
              },
            );
          } else {
            const {result} = response;
            let newPO;
            if (!ObjectUtil.isEmpty(result)) {
              newPO = new SRXPropertyUserPO(result);
            }

            this.setState(
              {
                signUpPO: {
                  address: newPO.address,
                  postalCode: newPO.postalCode,
                  buildingNumber: newPO.buildingNumber,
                  streetId: newPO.streetId,
                  floorNum: newPO.unitFloor,
                  unitNum: newPO.unitNo,
                  cdResearchSubtype: newPO.cdResearchSubtype,
                  size: newPO.size,
                  landType: newPO.landType,
                  tenure: newPO.tenure,
                  lastConstructed: newPO.builtYear,
                  occupancy: newPO.occupancy,
                  purpose: newPO.purpose,
                  dateOwnerNricVerified: newPO.dateOwnerNricVerified,
                  ownerNric: '',
                },
                floorEdited: false,
                buttonState: LoadingState.Normal,
              },
              () => {
                if (!ObjectUtil.isEmpty(newPO)) {
                  updateTracker({
                    originalTrackerPO: this.props.propertyUserPO,
                    newTrackerPO: newPO,
                  });
                  this.retrieveCertifyListing();
                  // Navigation.popTo(initiatingComponentId).then(() => {
                  //   if (onUpdateSRXPropertyPO) {
                  //     onUpdateSRXPropertyPO(newPO);
                  //   }
                  // });
                }
              },
            );
          }
        } else {
          this.setState({
            buttonState: LoadingState.Normal,
          });
        }
      })
      .catch(error => {
        this.setState(
          {
            buttonState: LoadingState.Normal,
          },
          () => {
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              'Error encountered. Please try again.',
            );
          },
        );
      });
  }

  directToVerifyMobile(signUpPO) {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.VefifyMobile',
        passProps: {
          trackerSignUpPO: signUpPO,
          onVerified: this.onPropertySuccessfullyAdded,
        },
      },
    });
  }

  directToOTPVerification(ptUserId, mobileNum) {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.OTPConfirmation',
        passProps: {
          ptUserId,
          mobile: mobileNum,
          onVerified: this.onPropertySuccessfullyAdded,
        },
      },
    });
  }

  showVerifyOwnershipIntro() {
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'MyProperties.VerifyOwnershipIntro',
              options: {
                modalPresentationStyle: 'overFullScreen',
              },
            },
          },
        ],
      },
    });
  }

  onPropertySuccessfullyAdded() {
    this.props.loadPropertyTrackers();
    Navigation.popTo(this.props.initiatingComponentId);
  }

  renderAddress() {
    const {signUpPO, isUpdate} = this.state;
    return (
      <View style={styles.inputContainer}>
        <TouchableOpacity
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            InputStyles.containerPadding,
            InputStyles.containerAlign,
            InputStyles.singleLineTextHeight,
            isUpdate ? {opacity: 0.5} : null,
          ]}
          disabled={isUpdate}
          onPress={() => this.showAddressAutoComplete()}>
          {ObjectUtil.isEmpty(signUpPO.address) ? (
            <Text style={[InputStyles.text, {color: '#8DABC4'}]}>
              Enter your address or postal code
            </Text>
          ) : (
            <Text style={[InputStyles.text, {color: SRXColor.Black}]}>
              {signUpPO.address}
            </Text>
          )}
          <FeatherIcon
            name="chevron-down"
            size={24}
            color={SRXColor.Black}
            style={{alignSelf: 'center', marginLeft: Spacing.XS}}
          />
        </TouchableOpacity>
      </View>
    );
  }

  renderSubtype() {
    const {signUpPO, error, propertyTypeOptions, isUpdate} = this.state;
    if (!ObjectUtil.isEmpty(signUpPO.postalCode) || signUpPO.postalCode > 0) {
      return (
        <View style={styles.inputContainer}>
          {isUpdate ? (
            <BodyText
              style={[
                InputStyles.container,
                ObjectUtil.isEmpty(error.subTypeError)
                  ? InputStyles.containerBorder
                  : InputStyles.containerBorder_Error,
                InputStyles.containerPadding,
                InputStyles.singleLineTextHeight,
                {paddingRight: Spacing.XS, opacity: 0.5},
                isIOS ? {paddingTop: Spacing.XS} : {paddingTop: Spacing.S},
              ]}>
              {PropertyTypeUtil.getPropertyTypeDescription(
                signUpPO.cdResearchSubtype,
              )}
            </BodyText>
          ) : (
            <Picker
              style={[
                InputStyles.container,
                ObjectUtil.isEmpty(error.subTypeError)
                  ? InputStyles.containerBorder
                  : InputStyles.containerBorder_Error,
                InputStyles.containerPadding,
                InputStyles.singleLineTextHeight,
                {paddingRight: Spacing.XS},
              ]}
              disabled={isUpdate}
              data={propertyTypeOptions}
              mode={isIOS ? null : 'dialog'}
              useCustomPicker={true}
              selectedValue={signUpPO.cdResearchSubtype}
              titleOfItem={(item, itemIndex, componentIndex) => item.key}
              valueOfItem={(item, itemIndex, componentIndex) => item.value}
              onSubmit={itemValue => this.onPropertyTypeSelected(itemValue)}
              renderRightAccessory={() => {
                return (
                  <FeatherIcon
                    name="chevron-down"
                    size={24}
                    color={SRXColor.Black}
                    style={{alignSelf: 'center', marginLeft: Spacing.XS}}
                  />
                );
              }}
            />
          )}

          {/* {this.renderErrorMessageForPicker(error.subTypeError)} */}
        </View>
      );
    } else {
      return null;
    }
  }

  renderFloorNum() {
    const {signUpPO, error, isUpdate} = this.state;
    if (
      (!ObjectUtil.isEmpty(signUpPO.postalCode) || signUpPO.postalCode > 0) &&
      !PropertyTypeUtil.isCommercial(signUpPO.cdResearchSubtype)
    ) {
      return (
        <View style={styles.inputContainer}>
          <View style={[{flexDirection: 'row'}]}>
            <TextInput
              style={{flex: 1}}
              keyboardType={isIOS ? 'numbers-and-punctuation' : 'default'}
              placeholder={'Floor'}
              autoCorrect={false}
              clearButtonMode={'while-editing'}
              returnKeyType={'done'}
              // editable={!isUpdate}
              value={signUpPO.floorNum}
              error={
                !ObjectUtil.isEmpty(error.unitNumError) &&
                ObjectUtil.isEmpty(signUpPO.floorNum)
                  ? error.unitNumError
                  : null
              }
              onChangeText={newText =>
                this.setState({
                  signUpPO: {...signUpPO, floorNum: newText, ownerNric: ''},
                  floorEdited: true,
                })
              }
              onEndEditing={this.onUnitFloorNumEndEditing}
            />
            <View>
              <View style={{justifyContent: 'center', height: 40}}>
                <BodyText
                  style={{marginLeft: Spacing.XS, marginRight: Spacing.XS}}>
                  -
                </BodyText>
              </View>
            </View>
            <TextInput
              style={[{flex: 1}]}
              keyboardType={isIOS ? 'numbers-and-punctuation' : 'default'}
              placeholder={'Unit'}
              autoCorrect={false}
              clearButtonMode={'while-editing'}
              returnKeyType={'done'}
              // editable={!isUpdate}
              value={signUpPO.unitNum}
              error={
                !ObjectUtil.isEmpty(error.unitNumError) &&
                ObjectUtil.isEmpty(signUpPO.unitNum)
                  ? error.unitNumError
                  : null
              }
              onChangeText={newText =>
                this.setState({
                  signUpPO: {...signUpPO, unitNum: newText},
                  floorEdited: true,
                })
              }
              onEndEditing={this.onUnitFloorNumEndEditing}
            />
          </View>
        </View>
      );
    }
  }

  renderSize() {
    // this value is allowed to update
    const {signUpPO, error} = this.state;
    const {
      cdResearchSubtype,
      postalCode,
      floorNum,
      unitNum,
      size,
      possiblyNoUnit,
    } = signUpPO;

    if (
      (!ObjectUtil.isEmpty(postalCode) || postalCode > 0) &&
      !PropertyTypeUtil.isCommercial(cdResearchSubtype)
    ) {
      return (
        <View style={styles.inputContainer}>
          <TextInput
            style={{flex: 2}}
            inputContainerStyle={{
              paddingRight: 0,
              backgroundColor: SRXColor.SmallBodyBackground,
              borderColor: SRXColor.SmallBodyBackground,
            }}
            keyboardType={'number-pad'}
            placeholder={
              PropertyTypeUtil.isHDB(cdResearchSubtype) ? 'Sqm' : 'Sqft'
            }
            placeholderTextColor={SRXColor.Black}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={size > 0 ? StringUtil.formatThousand(size) : ''}
            error={error.sizeError}
            onChangeText={newText =>
              this.setState({
                signUpPO: {
                  ...signUpPO,
                  size: StringUtil.decimalValue(newText, 0),
                },
              })
            }
            // onFocus={() => {
            //   if (ObjectUtil.isEmpty(size)) {
            //     this.getPropertyInfo({
            //       propertySubType: cdResearchSubtype,
            //       postalCode,
            //       floorNum,
            //       unitNum,
            //       possiblyNoUnit
            //     });
            //   }
            // }}
            rightView={
              <Button
                buttonStyle={[
                  styles.inputBoxStyle,
                  InputStyles.container,
                  {
                    // flex: 1,
                    borderColor: SRXColor.Teal,
                    marginRight: -1,
                    marginVertical: -1,
                    alignSelf: 'stretch',
                    backgroundColor: SRXColor.White,
                  },
                ]}
                textStyle={{
                  fontSize: 14,
                  color: SRXColor.Black,
                  textAlign: 'center',
                }}
                onPress={() => this.getSizeClicked()}>
                Get Size
              </Button>
            }
          />
        </View>
      );
    }
  }

  renderTypeOfArea() {
    const {isUpdate, signUpPO, error, typeOfAreaOptions} = this.state;
    if (PropertyTypeUtil.isLanded(signUpPO.cdResearchSubtype) && !isUpdate) {
      return (
        <View style={styles.inputContainer}>
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(error.landTypeError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={typeOfAreaOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            selectedValue={signUpPO.landType || ''}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue =>
              this.setState({
                signUpPO: {
                  ...signUpPO,
                  landType: isIOS ? itemValue : itemValue.value,
                },
              })
            }
            renderRightAccessory={() => (
              <FeatherIcon
                name="chevron-down"
                size={24}
                color={SRXColor.Black}
                style={{alignSelf: 'center', marginLeft: Spacing.XS}}
              />
            )}
          />
          {this.renderErrorMessageForPicker(error.landTypeError)}
        </View>
      );
    }
  }

  renderTenure() {
    const {isUpdate, signUpPO, error, tenureOptions} = this.state;
    if (PropertyTypeUtil.isLanded(signUpPO.cdResearchSubtype) && !isUpdate) {
      return (
        <View style={styles.inputContainer}>
          {/* <Text style={InputStyles.label}>Tenure</Text> */}
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(error.tenureError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={tenureOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            error={error.tenureError}
            selectedValue={signUpPO.tenure || ''}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue =>
              this.setState({
                signUpPO: {
                  ...signUpPO,
                  tenure: isIOS ? itemValue : itemValue.value.toString(),
                },
              })
            }
            renderRightAccessory={() => (
              <FeatherIcon
                name="chevron-down"
                size={24}
                color={SRXColor.Black}
                style={{alignSelf: 'center', marginLeft: Spacing.XS}}
              />
            )}
          />
          {this.renderErrorMessageForPicker(error.tenureError)}
        </View>
      );
    }
  }

  renderBuiltSize() {
    const {isUpdate, signUpPO} = this.state;
    if (PropertyTypeUtil.isLanded(signUpPO.cdResearchSubtype) && !isUpdate) {
      const {gfa} = signUpPO;

      return (
        <View style={styles.inputContainer}>
          <TextInput
            keyboardType={'number-pad'}
            underlineColorAndroid="transparent"
            placeholder={'Built Size (GFA) (sqft)'}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={StringUtil.formatThousand(gfa)}
            onChangeText={newText =>
              this.setState({
                signUpPO: {
                  ...signUpPO,
                  gfa: StringUtil.decimalValue(newText, 0),
                },
              })
            }
          />
        </View>
      );
    }
  }

  renderBuiltYear() {
    const {isUpdate, signUpPO, error, builtYearOptions} = this.state;
    if (PropertyTypeUtil.isLanded(signUpPO.cdResearchSubtype) && !isUpdate) {
      return (
        <View style={styles.inputContainer}>
          {/* <Text style={InputStyles.label}>Built Year</Text> */}
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(error.lastConstructError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={builtYearOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            height={isIOS ? null : '50%'}
            selectedValue={signUpPO.lastConstructed || ''}
            error={error.lastConstructError}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue =>
              this.setState({
                signUpPO: {
                  ...signUpPO,
                  lastConstructed: isIOS ? itemValue : itemValue.value,
                },
              })
            }
            renderRightAccessory={() => (
              <FeatherIcon
                name="chevron-down"
                size={24}
                color={SRXColor.Black}
                style={{alignSelf: 'center', marginLeft: Spacing.XS}}
              />
            )}
          />
          {this.renderErrorMessageForPicker(error.lastConstructError)}
        </View>
      );
    }
  }

  renderErrorMessageForPicker(error) {
    if (!ObjectUtil.isEmpty(error)) {
      return <Text style={[InputStyles.errorMessage, {flex: 1}]}>{error}</Text>;
    }
  }

  renderOccupancyAndPurpose() {
    const {signUpPO, error} = this.state;
    const {postalCode, cdResearchSubtype, occupancy, purpose} = signUpPO;

    if (
      (!ObjectUtil.isEmpty(postalCode) || postalCode > 0) &&
      !PropertyTypeUtil.isCommercial(cdResearchSubtype)
    ) {
      return (
        <View
          style={{
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.S,
            paddingBottom: Spacing.S,
          }}>
          <MyPropertyOccupancyAndPurpose
            initialOccupancy={occupancy}
            initialPurpose={purpose}
            error={error}
            onValueUpdated={this.onSelectOccupancyAndPurpose}
          />
        </View>
      );
    }
  }

  renderOwnerNric() {
    const {occupancy, ownerNric, dateOwnerNricVerified} = this.state.signUpPO;
    const {floorEdited} = this.state;

    if (
      !ObjectUtil.isEmpty(dateOwnerNricVerified) &&
      dateOwnerNricVerified != '' &&
      occupancy === pt_Occupancy.Own
    ) {
      return (
        <View style={{flexDirection: 'row', alignItems: 'center'}}>
          <BodyText
            style={{color: SRXColor.Purple, paddingHorizontal: Spacing.S}}>
            Ownership Verified
          </BodyText>
        </View>
      );
    } else if (
      occupancy === pt_Occupancy.Own &&
      dateOwnerNricVerified == '' &&
      floorEdited == false
    ) {
      return (
        <View style={styles.inputContainer}>
          <TextInput
            style={{flex: 1}}
            placeholder={'NRIC/Passport no.'}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={ownerNric}
            onChangeText={newText =>
              this.setState({
                signUpPO: {
                  ...this.state.signUpPO,
                  ownerNric: newText,
                },
              })
            }
          />
          <Button
            buttonStyle={{marginTop: Spacing.S}}
            textStyle={{fontSize: 14}}
            onPress={this.showVerifyOwnershipIntro}>
            Why NRIC/Passport no?
          </Button>
        </View>
      );
    }
  }

  renderAddOrUpdateButton() {
    const {isUpdate, buttonState} = this.state;
    const {postalCode, cdResearchSubtype} = this.state.signUpPO;
    const isLoading = buttonState === LoadingState.Loading;
    let buttonText;
    if (isUpdate) {
      if (isLoading) {
        buttonText = 'Updating...';
      } else {
        buttonText = 'Update';
      }
    } else {
      if (isLoading) {
        buttonText = 'Adding Property...';
      } else {
        buttonText = 'Add Property';
      }
    }
    if (
      (!ObjectUtil.isEmpty(postalCode) || postalCode > 0) &&
      !PropertyTypeUtil.isCommercial(cdResearchSubtype)
    ) {
      return (
        <View
          style={{
            marginTop: Spacing.XL,
            marginBottom: 50,
            alignItems: 'center',
          }}>
          <Button
            buttonType={Button.buttonTypes.primary}
            onPress={this.onAddOrUpdateBtnPress}
            isSelected={isLoading}>
            {buttonText}
          </Button>
        </View>
      );
    }
  }

  certifyList = listingId => {
    const {propertyUserPO, loadPropertyTrackers} = this.props;
    let ptUserId = propertyUserPO.ptUserId.toString();
    console.log('listingID');
    let listingIds = listingId.toString();
    // console.log("pt");
    // console.log(ptUserId);

    CertifiedListingService.certifyListings({
      listingIds,
      ptUserId,
    }).then(response => {
      console.log('Check certify result');
      console.log(response);
      if (!ObjectUtil.isEmpty(response)) {
        if (!ObjectUtil.isEmpty(response.result)) {
          this.retrieveCertifyListing();
          loadPropertyTrackers();
          // this.setState({certifyListingArray: response.result});
        }
      }
    });
  };

  directListing = listingPO => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.ListingDetails',
        passProps: {
          listingId: listingPO.getListingId(),
          refType: listingPO.listingType,
        },
      },
    });
  };

  renderCertifyListingItem({item, index}) {
    let imageURL = CommonUtil.handleImageUrl(item.listingPhoto);
    let listingPO = new ListingPO(item);
    const {ownerCertifiedInd} = item;
    return (
      <CertifyListingListItem
        listingPO={listingPO}
        isCertifiedListing={ownerCertifiedInd}
        certifyList={this.certifyList}
        directListing={this.directListing}
      />
      // <View style={{marginBottom:Spacing.M}}>
      //   <View style={{ marginBottom: Spacing.M }}>
      //   <View style={{ flexDirection: "row" }}>
      //     <Image
      //       style={{ height: 80, width: 120, marginRight: Spacing.M }}
      //       source={{ uri: imageURL }}
      //       resizeMode={"cover"}
      //     />
      //     <View>
      //       <BodyText style={{ fontWeight: "bold" }}>{item.address}</BodyText>
      //       <BodyText style={{ color: SRXColor.Green, fontWeight: "bold" }}>
      //         {item.askingPrice}
      //       </BodyText>
      //       <View style={{ flexDirection: "row" }}>
      //         <SmallBodyText style={{ paddingRight: Spacing.M }}>
      //           Posted by{" "}
      //         </SmallBodyText>
      //         <Avatar
      //           size={24}
      //           imageUrl={item.agentPO.photo}
      //           borderLess={true}
      //         />
      //         <SmallBodyText style={{ paddingLeft: Spacing.M }}>
      //           {item.agentPO.name}
      //         </SmallBodyText>
      //       </View>
      //     </View>
      //   </View>
      // </View>
      // <View style={{borderWidth:1,borderRadius:16, borderColor:SRXColor.LightGray}}>
      //   {ownerCertifiedInd?<BodyText>Certified</BodyText>
      //           :
      //           <View style={{flexDirection:"row"}}>
      //             <BodyText style={{flex:1, margin:Spacing.M}}>Certify this listing?</BodyText>

      //       <FeatherIcon
      //       style={{ alignSelf: "center", paddingRight:Spacing.M }}
      //         name={"x"}
      //         size={30}
      //         color={SRXColor.Black}
      //       />
      //       <FeatherIcon
      //             style={{ alignSelf: "center", paddingRight:Spacing.M }}
      //         name={"check"}
      //         size={30}
      //         color={SRXColor.Black}
      //       />

      //             </View>

      //           }

      // </View>
      // </View>
    );
  }

  renderCertifyListing() {
    const {certifyListingArray} = this.state;
    if (!ObjectUtil.isEmpty(certifyListingArray)) {
      return (
        <View>
          <Heading2
            style={{paddingHorizontal: Spacing.M, paddingBottom: Spacing.M}}>
            Your home in SRX listings
          </Heading2>
          {certifyListingArray.map((item, index) => {
            return <View>{this.renderCertifyListingItem({item, index})}</View>;
          })}
        </View>
      );
    }
  }

  render() {
    const {certifyViewIndicator} = this.state;
    const {certifyListing} = this.props;
    return (
      <SafeAreaView style={{flex: 1}}>
        <KeyboardAwareScrollView
          style={{flex: 1, backgroundColor: SRXColor.White}}
          keyboardShouldPersistTaps={'always'}
          onScroll={this.onScroll}
          innerRef={component => {
            this.scrollView = component;
          }}>
          {this.renderLargeTitle('My Property')}
          <View style={{paddingHorizontal: Spacing.M}}>
            {this.renderAddress()}
            {this.renderSubtype()}
            {this.renderFloorNum()}
            {this.renderSize()}
            {this.renderTypeOfArea()}
            {this.renderTenure()}
            {this.renderBuiltSize()}
            {this.renderBuiltYear()}
          </View>
          {this.renderOccupancyAndPurpose()}
          <View
            style={{
              paddingHorizontal: Spacing.M,
            }}>
            {this.renderOwnerNric()}
          </View>
          {this.renderAddOrUpdateButton()}
          <View
            style={
              certifyViewIndicator && certifyListing
                ? {height: 300, width: 50}
                : null
            }
          />
          {this.renderCertifyListing()}
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

const styles = {
  inputContainer: {
    flexDirection: 'column',
    paddingTop: Spacing.S,
    paddingBottom: Spacing.S,
  },
  inputBoxStyle: {
    borderWidth: 1,
    borderColor: '#DCDCDC',
    backgroundColor: 'white',
    paddingRight: 12,
    paddingLeft: 12,
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

MyPropertyForm.propTypes = {
  /**
   * component id of the screen that directed to this screen
   */
  initiatingComponentId: PropTypes.string,

  /**
   * ** only for UPDATE my property
   *
   * if provided this, means this screen is open for editing, not adding
   */
  propertyUserPO: PropTypes.instanceOf(SRXPropertyUserPO),
  /**
   * ** only for UPDATE my property
   *
   * return updated SRXPropertyUserPO with callback
   * optional
   * only required if the callback screen required to display the object directly
   * if not, can get from the properties list as it will be updated immediately updates to server is successful
   */
  onUpdateSRXPropertyPO: PropTypes.func,
  certifyListing: PropTypes.bool,
};

export default connect(
  mapStateToProps,
  {loadPropertyTrackers, updateTracker},
)(MyPropertyForm);
