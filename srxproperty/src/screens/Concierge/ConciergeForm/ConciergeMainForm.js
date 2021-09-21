import React, {Component} from 'react';
import {
  Alert,
  View,
  SafeAreaView,
  StyleSheet,
  Platform,
  PermissionsAndroid,
  Text,
} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';
import {connect} from 'react-redux';

//Import Custom Created Components
import {
  Button,
  FeatherIcon,
  SmallBodyText,
  LargeTitleComponent,
} from '../../../components';
import {
  ConciergeContact,
  ConciergePropertyInfo,
  ConciergeAgentList,
} from '../Components';
import {
  ObjectUtil,
  CommonUtil,
  StringUtil,
  NumberUtil,
  PermissionUtil,
  GoogleAnalyticUtil,
} from '../../../utils';
import {
  SRXColor,
  District,
  UserType,
  AppConstant,
  AlertMessage,
} from '../../../constants';
import {Spacing, CheckboxStyles, Typography} from '../../../styles';
import {
  Concierge_Menu,
  Concierge_Title,
  Concierge_PropertyType_Options,
} from '../Constants';
import {AgentSearchService, EnquiryService} from '../../../services';
import {LoginStack} from '../../../config';

isIOS = Platform.OS === 'ios';

const SubmissionState = {
  Normal: 'normal', //default, not submitting
  Submitting: 'submitting', //sending requests
  Submitted: 'submitted',
};

class ConciergeMainForm extends LargeTitleComponent {
  state = {
    submissionState: SubmissionState.Normal,
    conciergeFormInfo: {
      name: '',
      email: '',
      userType: '1', //default value
      mobileNum: '',
      propertyType: '1',
      districtTownId: '',
      keywords: '',
      locationType: '',
      minPrice: '',
      maxPrice: '',
      privacyPolicy: false,
      agentUserId: '',
      countryCode: '',
      remark: '',
    },
    errorMessage: {
      errMsgName: '',
      errMsgEmail: '',
      errMsgPhone: '',
      errMsgLocation: '',
      errMsgPriceRange: '',
      errMsgSelectedAgent: '',
      errMsgPDPA: '',
    },
    errorMessagePosition: {
      locationErrMsgY: 0,
      priceRangeErrMsgY: 0,
      matchedAgentErrMsgY: 0,
    },
  };

  componentDidMount() {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      this.handleUserInformation();
    }
  }

  componentDidUpdate(prevProps) {
    if (prevProps.userPO !== this.props.userPO) {
      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        this.handleUserInformation();
      }
    }
  }

  trackFormSubmissionByGoogleAnalytic(conciergeMenu) {
    const {
      userType,
      propertyType,
      districtTownId,
      keywords,
    } = this.state.conciergeFormInfo;

    var propertyTypeOption = Concierge_PropertyType_Options.find(
      item => item.value === propertyType,
    );
    GoogleAnalyticUtil.trackConciergeSubmission({
      parameters: {
        conciergeMenu,
        userType,
        propertyType: propertyTypeOption ? propertyTypeOption.key : '',
        districtTownId,
        keywords,
      },
    });
  }

  handleUserInformation() {
    //If user is already logged in, get user information
    const {userPO} = this.props;
    const {conciergeFormInfo} = this.state;
    this.setState({
      conciergeFormInfo: {
        ...conciergeFormInfo,
        name: userPO.name ? userPO.name : conciergeFormInfo.name,
        email: userPO.email ? userPO.email : conciergeFormInfo.email,
        mobileNum: userPO.mobileLocalNum
          ? userPO.mobileLocalNum.toString()
          : conciergeFormInfo.mobileNum,
      },
    });
  }

  //Generate Message for Enquiry
  generateMessageForAgentEnquiry() {
    const {
      userType,
      districtTownId,
      minPrice,
      maxPrice,
      name,
      email,
      mobileNum,
      remark,
    } = this.state.conciergeFormInfo;
    var message = '';

    message = 'An SRX user is looking to ';
    message += UserType.getUserTypeDescription(userType) + ' ';
    message += District.getDistrictDescription(districtTownId);
    message += '\nTheir indicated price range is ';

    if (minPrice) {
      message += StringUtil.formatCurrency(minPrice);
    }
    if (maxPrice) {
      message += ' to ' + StringUtil.formatCurrency(maxPrice);
    }
    message +=
      ' \nPlease follow up as soon as possible.\nThe details are as follows..';

    if (remark) {
      message += '\nRemarks : ' + remark;
    }

    //Name
    if (name) {
      message += '\n\nName: ' + name;
    }
    //Email
    if (email) {
      message += '\nEmail: ' + email;
    }
    //mobileNum
    if (mobileNum) {
      message += '\nVerify Mobile :' + mobileNum;
    }

    message += '\n\nAll The Best!\nSRX myProperty Concierge';

    return message;
  }

  goBack = () => {
    return Navigation.pop(this.props.componentId);
  };

  showVerifyMobileModal = () => {
    const {name, mobileNum} = this.state.conciergeFormInfo;
    this.setState({submissionState: SubmissionState.Submitting}, () => {
      //Change button state
      Navigation.showModal({
        stack: {
          children: [
            {
              component: {
                name: 'SideMainMenu.VerifyMobileNumber',
                passProps: {
                  name,
                  mobile: mobileNum,
                  closeVerifyMobileModal: this.closeVerifyMobileModal.bind(
                    this,
                  ),
                  showSuccessfulMessage: this.onSubmitAPICalled.bind(this),
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
    });
  };

  onSubmitAPICalled() {
    const {selectedMenu} = this.props;
    if (selectedMenu === Concierge_Menu.findAgent) {
      this.sendRequest();
    } else if (selectedMenu === Concierge_Menu.ownAgent) {
      this.submitSrxAgentEnquiry();
    }
  }

  closeVerifyMobileModal() {
    this.setState({submissionState: SubmissionState.Normal});
  }

  onChangeUserInfo = (newUserInfo, newErrorMessage) => {
    const {conciergeFormInfo, errorMessage} = this.state;
    this.setState({
      conciergeFormInfo: {
        ...conciergeFormInfo,
        name: newUserInfo.name,
        email: newUserInfo.email,
        mobileNum: newUserInfo.mobileNum,
        remark: newUserInfo.remark,
      },
      errorMessage: {
        ...errorMessage,
        ...newErrorMessage,
      },
    });
  };

  onChangePropertyInfo = (newPropertyInfo, newErrorMessage) => {
    const {conciergeFormInfo, errorMessage} = this.state;
    const {
      userType,
      propertyType,
      minPrice,
      maxPrice,
      districtTownId,
      keywords,
      locationType,
    } = newPropertyInfo;

    this.setState({
      conciergeFormInfo: {
        ...conciergeFormInfo,
        userType,
        propertyType,
        minPrice,
        maxPrice,
        districtTownId,
        keywords,
        locationType,
      },
      errorMessage: {
        ...errorMessage,
        ...newErrorMessage,
      },
    });
  };

  //get Y position of error message to scroll up
  getViewsPosition = viewPosition => {
    const {errorMessagePosition} = this.state;
    const {
      locationErrMsgY,
      priceRangeErrMsgY,
      matchedAgentErrMsgY,
    } = viewPosition;

    //get position of error message
    this.setState({
      errorMessagePosition: {
        ...errorMessagePosition,
        locationErrMsgY,
        priceRangeErrMsgY,
        matchedAgentErrMsgY,
      },
    });
  };

  onSelectOwnAgent = (newConciergeFormInfo, newErrorMessage) => {
    const {conciergeFormInfo, errorMessage} = this.state;
    const {agentUserId} = newConciergeFormInfo;

    this.setState({
      conciergeFormInfo: {
        ...conciergeFormInfo,
        agentUserId,
      },
      errorMessage: {
        ...errorMessage,
        ...newErrorMessage,
      },
    });
  };

  onTogglePrivacyPolicy = () => {
    const {errorMessage, conciergeFormInfo} = this.state;
    const {privacyPolicy} = this.state.conciergeFormInfo;

    this.setState({
      conciergeFormInfo: {
        ...conciergeFormInfo,
        privacyPolicy: !privacyPolicy,
      },
      errorMessage: {
        ...errorMessage,
        errMsgPDPA: '',
      },
    });
  };

  onClickTermsOfUse = () => {
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

  directToPrivacyPolicy = () => {
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

  // requestAndroidReceiveSMSPermission = () => {
  //   PermissionUtil.requestAndroidReceiveSMSPermission().then(granted => {
  //     if (granted === PermissionsAndroid.RESULTS.GRANTED) {
  //       this.showVerifyMobileModal();
  //     }
  //   });
  // };

  //Click Submit for Application Submitting
  onSubmit = () => {
    const {userPO} = this.props;
    if (this.isAllInputsValidate()) {
      if (!ObjectUtil.isEmpty(userPO)) {
        //Submitting State
        this.setState({submissionState: SubmissionState.Submitting}, () => {
          this.mobileVerifyConsumerConcierge();
        });
      } else {
        const {name, email} = this.state.conciergeFormInfo;
        //show sign up modal
        const passProps = {
          conciergeName: name,
          conciergeEmail: email,
          onSuccessSignUp: this.onSuccessSignUp.bind(this),
          type: 'R',
        };
        LoginStack.showSignInRegisterModal(passProps);
      }
    }
  };

  onSuccessSignUp() {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      //Submitting State
      this.setState({submissionState: SubmissionState.Submitting}, () => {
        this.mobileVerifyConsumerConcierge();
      });
    }
  }

  //For Validation
  isAllInputsValidate() {
    const {conciergeFormInfo} = this.state;
    const {selectedMenu} = this.props;

    if (!ObjectUtil.isEmpty(conciergeFormInfo)) {
      let isAllValidate = true;
      let scrollToPosition = 0;
      let newErrorMessage = {
        name: '',
        email: '',
        mobileNum: '',
        location: '',
        priceRange: '',
        selectedAgent: '',
        pdpa: '',
      };
      const {
        locationErrMsgY,
        matchedAgentErrMsgY,
        priceRangeErrMsgY,
      } = this.state.errorMessagePosition;

      //Name
      if (
        ObjectUtil.isEmpty(conciergeFormInfo.name) ||
        conciergeFormInfo.name === ''
      ) {
        isAllValidate = false;
        newErrorMessage.name = 'Required field';
      }

      //Email
      if (
        ObjectUtil.isEmpty(conciergeFormInfo.email) ||
        conciergeFormInfo.email === ''
      ) {
        isAllValidate = false;
        newErrorMessage.email = 'Required field';
      } else if (!StringUtil.validateEmailFormat(conciergeFormInfo.email)) {
        isAllValidate = false;
        newErrorMessage.email = 'Please enter a valid email';
      }

      //Mobile Num
      if (
        ObjectUtil.isEmpty(conciergeFormInfo.mobileNum.toString()) ||
        conciergeFormInfo.mobileNum === ''
      ) {
        isAllValidate = false;
        newErrorMessage.mobileNum = 'Required field';
      } else if (conciergeFormInfo.mobileNum.length !== 8) {
        isAllValidate = false;
        newErrorMessage.mobileNum = 'Please enter a valid mobile number';
      }

      //SelectedAgent for Find Own Agent Only
      if (selectedMenu === Concierge_Menu.ownAgent) {
        if (
          ObjectUtil.isEmpty(conciergeFormInfo.agentUserId.toString()) ||
          conciergeFormInfo.agentUserId === ''
        ) {
          isAllValidate = false;
          scrollToPosition = matchedAgentErrMsgY;
          newErrorMessage.selectedAgent = 'Please choose one agent';
        }
      }

      //PriceRange
      //Min and Max Price
      if (
        !ObjectUtil.isEmpty(conciergeFormInfo.minPrice) &&
        !ObjectUtil.isEmpty(conciergeFormInfo.maxPrice)
      ) {
        if (
          NumberUtil.intValue(conciergeFormInfo.minPrice) >
          NumberUtil.intValue(conciergeFormInfo.maxPrice)
        ) {
          isAllValidate = false;
          scrollToPosition = priceRangeErrMsgY;
          newErrorMessage.priceRange =
            'Minium Price must be less than Maximum Price';
        }
      }

      //Max Price
      if (
        ObjectUtil.isEmpty(conciergeFormInfo.maxPrice) ||
        conciergeFormInfo.maxPrice === ''
      ) {
        isAllValidate = false;
        scrollToPosition = priceRangeErrMsgY;
        newErrorMessage.priceRange = 'Required field';
      }

      //Min Price
      if (
        ObjectUtil.isEmpty(conciergeFormInfo.minPrice) ||
        conciergeFormInfo.minPrice === ''
      ) {
        isAllValidate = false;
        scrollToPosition = priceRangeErrMsgY;
        newErrorMessage.priceRange = 'Required field';
      }

      //Location
      if (
        ObjectUtil.isEmpty(conciergeFormInfo.keywords) ||
        conciergeFormInfo.keywords === ''
      ) {
        isAllValidate = false;
        scrollToPosition = locationErrMsgY;
        newErrorMessage.location = 'Required field';
      }

      if (!conciergeFormInfo.privacyPolicy) {
        isAllValidate = false;
        newErrorMessage.pdpa = 'Please agree to the Privacy Policy to proceed';
      }

      this.setState({
        errorMessage: {
          ...this.state.errorMessageObj,
          errMsgName: newErrorMessage.name,
          errMsgEmail: newErrorMessage.email,
          errMsgPhone: newErrorMessage.mobileNum,
          errMsgLocation: newErrorMessage.location,
          errMsgPriceRange: newErrorMessage.priceRange,
          errMsgSelectedAgent: newErrorMessage.selectedAgent,
          errMsgPDPA: newErrorMessage.pdpa,
        },
      });

      if (scrollToPosition > 0) {
        this.scrollView.props.scrollToPosition(0, scrollToPosition);
      }

      return isAllValidate;
    } else {
      return false;
    }
  }

  //API lists
  mobileVerifyConsumerConcierge() {
    const {name, email, mobileNum} = this.state.conciergeFormInfo;
    const {selectedMenu} = this.props;
    const section = selectedMenu === Concierge_Menu.findAgent ? '1' : '2';

    AgentSearchService.mobileVerifyConsumerConcierge({
      name,
      email,
      mobileNum,
      section,
    })
      .catch(error => {
        this.setState({submissionState: SubmissionState.Normal}, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({submissionState: SubmissionState.Normal}, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
              console.log(error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const {isOTPVerified, result, userId} = response;
              if (result === 'success') {
                //If mobile number is already verified,
                //no need to show verify mobile number screen
                if (isOTPVerified && userId) {
                  this.onSubmitAPICalled();
                } else {
                  this.showVerifyMobileModal();
                  // if (isIOS) {
                  //   this.showVerifyMobileModal();
                  // } else {
                  //   this.requestAndroidReceiveSMSPermission();
                  // }
                }
              }
            }
          }
        } else {
          this.setState({submissionState: SubmissionState.Normal}, () => {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          });
        }
      });
  }

  //For Find Agent Form
  sendRequest() {
    const {
      name,
      email,
      userType,
      mobileNum,
      propertyType,
      districtTownId,
      keywords,
      locationType,
    } = this.state.conciergeFormInfo;

    AgentSearchService.sendRequest({
      name,
      email,
      userType,
      mobileNum,
      propertyType,
      districtTownId,
      keywords,
      locationType,
    })
      .catch(error => {
        this.setState({submissionState: SubmissionState.Normal}, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({submissionState: SubmissionState.Normal}, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
              console.log(error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const {result} = response;
              if (result === 'success') {
                //track successful submission
                this.trackFormSubmissionByGoogleAnalytic(
                  Concierge_Menu.findAgent,
                );
                this.setState(
                  {submissionState: SubmissionState.Submitted},
                  () => {
                    Alert.alert(
                      AlertMessage.SuccessMessageTitle,
                      'Thank you. SRX Concierge will be in touch shortly',
                      [
                        {
                          text: 'OK',
                          onPress: this.goBack,
                        },
                      ],
                    );
                  },
                );
              }
            }
          }
        } else {
          this.setState({submissionState: SubmissionState.Normal}, () => {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          });
        }
      });
  }

  //For Choose Own Agent form
  submitSrxAgentEnquiry() {
    const {name, email, mobileNum, agentUserId} = this.state.conciergeFormInfo;
    const message = this.generateMessageForAgentEnquiry();

    EnquiryService.submitSrxAgentEnquiryV2({
      listingId: '',
      agentUserId,
      fullName: name,
      countryCode: '+65',
      mobileLocalNumber: mobileNum,
      email,
      message,
      enquirySource: 'O', //from Concierge
    })
      .catch(error => {
        this.setState({submissionState: SubmissionState.Normal}, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({submissionState: SubmissionState.Normal}, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
              console.log(error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const {result} = response;
              if (result === 'success') {
                //tracking successful submission
                this.trackFormSubmissionByGoogleAnalytic(
                  Concierge_Menu.ownAgent,
                );
                this.setState(
                  {submissionState: SubmissionState.Submitted},
                  () => {
                    Alert.alert(
                      AlertMessage.SuccessMessageTitle,
                      'Thank you. Your connected agent will be in touch shortly',
                      [
                        {
                          text: 'OK',
                          onPress: this.goBack,
                        },
                      ],
                    );
                  },
                );
              }
            }
          }
        } else {
          this.setState({submissionState: SubmissionState.Normal}, () => {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          });
        }
      });
  }

  //Start Rendering Methods
  renderPropertyInfoForm() {
    const {selectedMenu} = this.props;

    const {conciergeFormInfo, errorMessage, errorMessagePosition} = this.state;

    const selectedMenuTitle =
      selectedMenu === Concierge_Menu.findAgent
        ? Concierge_Title.findAgentTitle
        : Concierge_Title.ownAgentTitle;

    return (
      <ConciergePropertyInfo
        title={selectedMenuTitle}
        errorMessage={errorMessage}
        propertyInfo={conciergeFormInfo}
        viewsPosition={errorMessagePosition}
        onChangePropertyInfo={this.onChangePropertyInfo}
        getViewsPosition={this.getViewsPosition}
      />
    );
  }

  renderConciergeAgentList() {
    //show this component only For Own Agent Menu
    const {conciergeFormInfo, errorMessage, errorMessagePosition} = this.state;

    const {selectedMenu} = this.props;

    if (selectedMenu === Concierge_Menu.ownAgent) {
      return (
        <ConciergeAgentList
          conciergeFormInfo={conciergeFormInfo}
          errorMessage={errorMessage}
          viewsPosition={errorMessagePosition}
          onSelectOwnAgent={this.onSelectOwnAgent}
          getViewsPosition={this.getViewsPosition}
        />
      );
    }
  }

  renderConciergeContact() {
    const {conciergeFormInfo, errorMessage} = this.state;
    return (
      <ConciergeContact
        userInfo={conciergeFormInfo}
        errorMessage={errorMessage}
        onChangeUserInfo={this.onChangeUserInfo}
      />
    );
  }

  //Privacy Policy
  renderPrivacyPolicy() {
    const {privacyPolicy} = this.state.conciergeFormInfo;
    const {errMsgPDPA} = this.state.errorMessage;

    return (
      <View style={styles.privacyPolicyContainer}>
        <Button
          onPress={() => this.onTogglePrivacyPolicy()}
          textStyle={{
            color: SRXColor.Gray,
            marginLeft: Spacing.S,
            fontSize: 14,
          }}
          leftView={
            privacyPolicy ? (
              <View style={CheckboxStyles.checkStyle}>
                <FeatherIcon name={'check'} size={15} color={'white'} />
              </View>
            ) : (
              <View style={CheckboxStyles.unCheckStyle} />
            )
          }>
          I agree to SRX{' '}
          <SmallBodyText
            onPress={() => this.onClickTermsOfUse()}
            style={{color: SRXColor.Teal, marginRight: 3}}>
            Terms of Use
          </SmallBodyText>{' '}
          and{' '}
          <SmallBodyText
            onPress={() => this.directToPrivacyPolicy()}
            style={{color: SRXColor.Teal, marginRight: 3}}>
            Privacy Policy
          </SmallBodyText>
        </Button>
        {errMsgPDPA ? (
          <SmallBodyText style={[styles.errorMessage, {flex: 1}]}>
            {errMsgPDPA}
          </SmallBodyText>
        ) : null}
      </View>
    );
  }

  renderSubmitButton() {
    const {submissionState} = this.state;
    var buttonTitle = 'Submit';
    if (submissionState === SubmissionState.Submitting) {
      buttonTitle = 'Submitting';
    }
    return (
      <View style={{alignItems: 'center', justifyContent: 'center', flex: 1}}>
        <Button
          buttonType={Button.buttonTypes.primary}
          buttonStyle={{marginVertical: 25}}
          isSelected={submissionState === SubmissionState.Submitting}
          onPress={() => this.onSubmit()}>
          {buttonTitle}
        </Button>
      </View>
    );
  }

  render() {
    //populate title
    const {selectedMenu} = this.props;
    const title =
      selectedMenu === Concierge_Menu.findAgent
        ? 'Help me Find my Agent'
        : 'Choose my own Agent';

    return (
      <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
        <KeyboardAwareScrollView
          style={{flex: 1}}
          onScroll={this.onScroll}
          innerRef={component => {
            this.scrollView = component;
          }}>
          {/* title for concierge form */}
          {this.renderLargeTitle(title)}
          {this.renderPropertyInfoForm()}
          {this.renderConciergeAgentList()}
          {this.renderConciergeContact()}
          {this.renderPrivacyPolicy()}
          {this.renderSubmitButton()}
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  registrationAgreementStyle: {
    flex: 1,
    flexWrap: 'wrap',
    flexDirection: 'row',
    marginLeft: Spacing.S,
  },
  privacyPolicyContainer: {
    flex: 1,
    marginTop: Spacing.M,
    marginHorizontal: Spacing.M,
  },
  errorMessage: {
    marginTop: Spacing.XS,
    color: '#FF151F',
    lineHeight: 20,
  },
});

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(ConciergeMainForm);
