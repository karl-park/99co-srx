import React, { Component } from "react";
import {
  View,
  Platform,
  Alert,
  FlatList,
  StyleSheet,
  ActivityIndicator
} from "react-native";
import { connect } from "react-redux";
import { Navigation } from "react-native-navigation";
import SafeAreaView from "react-native-safe-area-view";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import PropTypes from "prop-types";

import { Styles } from "./Styles";
import {
  SRXColor,
  AppConstant,
  pt_Occupancy,
  AlertMessage
} from "../../constants";
import {
  Button,
  FeatherIcon,
  Ionicons,
  TextInput,
  LargeTitleComponent,
  BodyText,
  SmallBodyText,
  Separator
} from "../../components";
import { updateEmailAndPassword } from "../../actions";
import { ObjectUtil, CommonUtil } from "../../utils";
import { Spacing } from "../../styles";
import { PropertyTrackerService } from "../../services";
import { SRXPropertyUserPO } from "../../dataObject";
import { MyPropertyUpdateListItem } from "../MyProperties";

isIOS = Platform.OS === "ios";

const UpdateState = {
  Normal: "normal", //default, not update
  Updating: "updating", //sending update request
  Updated: "updated"
};

//loading for load properties tracker
const LoadingState = {
  Normal: "normal",
  Loading: "loading",
  Loaded: "loaded"
};

class SgHomeUpdateDetails extends LargeTitleComponent {
  state = {
    updateState: UpdateState.Normal,
    loadingState: LoadingState.Normal,
    updateUserDetails: {
      email: "",
      password: "",
      errMsgEmail: "",
      errMsgPassword: ""
    },
    properties: [],
    isShowPwd: false
  };

  constructor(props) {
    super(props);

    //Set options here
    this.setupTopBar();
    Navigation.events().bindComponent(this);

    //bind
    this.toggleShowHidePassword = this.toggleShowHidePassword.bind(this);
    this.onPressNeedHelp = this.onPressNeedHelp.bind(this);
    this.onPressUpdate = this.onPressUpdate.bind(this);
  }

  componentDidMount() {
    //after mysghome sign in, call loadpropertyTackers to get properties lists
    const { userPO } = this.props;
    const { updateUserDetails } = this.state;

    if (!ObjectUtil.isEmpty(userPO)) {
      this.setState(
        {
          loadingState: LoadingState.Loading,
          updateUserDetails: { ...updateUserDetails, email: userPO.email } //get email from userPO
        },
        () => {
          this.loadPropertyTrackers();
        }
      );
    }
  }

  setupTopBar() {
    FeatherIcon.getImageSource("x", 25, "black").then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          leftButtons: [
            {
              id: "updateDetail_close",
              icon: icon_close
            }
          ]
        }
      });
    });
  }

  isValidate() {
    const { updateUserDetails, properties } = this.state;
    if (!ObjectUtil.isEmpty(updateUserDetails)) {
      const { email, password } = updateUserDetails;
      let isAllValidate = true;
      let errorMessage = { email: "", password: "" };

      //email
      if (ObjectUtil.isEmpty(email) || email === "") {
        isAllValidate = false;
        this.scrollView.props.scrollToPosition(0, 50);
        errorMessage.email = "Required field";
      }

      //password
      if (ObjectUtil.isEmpty(password) || password == "") {
        isAllValidate = false;
        this.scrollView.props.scrollToPosition(0, 50);
        errorMessage.password = "Required field";
      } else if (password.length < 6) {
        isAllValidate = false;
        this.scrollView.props.scrollToPosition(0, 50);
        errorMessage.password = "minimum of 6 characters";
      }

      properties.map(item => {
        if (!ObjectUtil.isEmpty(item.srxPropertyUserPO)) {
          //occupancy
          if (!item.srxPropertyUserPO.occupancy) {
            isAllValidate = false;
            item.error = {
              ...item.error,
              occupancyError: "Required field"
            };
          }

          //purpose
          if (
            !item.srxPropertyUserPO.purpose &&
            item.srxPropertyUserPO.occupancy === pt_Occupancy.Own
          ) {
            isAllValidate = false;
            item.error = {
              ...item.error,
              purposeError: "Required field"
            };
          }
        }
      });

      //change state
      this.setState({
        properties,
        updateUserDetails: {
          ...updateUserDetails,
          errMsgEmail: errorMessage.email,
          errMsgPassword: errorMessage.password
        }
      });

      return isAllValidate;
    } else {
      return false;
    }
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "updateDetail_close") {
      this.onPressCloseModal();
    }
  }

  toggleShowHidePassword() {
    const { isShowPwd } = this.state;
    if (isShowPwd) {
      this.setState({ isShowPwd: false });
    } else {
      this.setState({ isShowPwd: true });
    }
  }

  onPressCloseModal() {
    Navigation.dismissModal(this.props.componentId);
  }

  //API calls
  loadPropertyTrackers() {
    PropertyTrackerService.loadPropertyTrackers({
      populateXvalue: false
    })
      .catch(error => {
        this.setState({ loadingState: LoadingState.Normal }, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({ loadingState: LoadingState.Normal }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const { result } = response;
              if (!ObjectUtil.isEmpty(result)) {
                const trackProperties = [];
                result.map(item => {
                  trackProperties.push({
                    srxPropertyUserPO: new SRXPropertyUserPO(item),
                    error: { occupancyError: null, purposeError: null }
                  });
                });
                //set properties to state
                this.setState({
                  properties: trackProperties,
                  loadingState: LoadingState.Loaded
                });
              }
            }
          }
        } else {
          this.setState({ loadingState: LoadingState.Normal }, () => {
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              "An error occurred unexpectedly. Please try again later."
            );
          });
        }
      });
  }

  //api call to update password
  securePropertyUser() {
    const { password, email } = this.state.updateUserDetails;
    const { properties } = this.state;
    const { srxPropertyUserPO, token, userPO } = this.props;

    PropertyTrackerService.securePropertyUser({
      ptUserId: srxPropertyUserPO.ptUserId,
      userId: srxPropertyUserPO.userId,
      password,
      token
    })
      .catch(error => {
        this.setState({ updateState: UpdateState.Normal }, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({ updateState: UpdateState.Normal }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const { result } = response;
              if (result === "success") {
                //to update user po if user changes email and password
                const newUserPO = {
                  ...userPO,
                  email
                };

                //save email and password in asyncStorage
                this.props.updateEmailAndPassword(email, password, newUserPO);

                //start calling updatePropertyUser by array
                if (!ObjectUtil.isEmpty(properties)) {
                  if (Array.isArray(properties) && properties.length > 0) {
                    var count = 0;
                    properties.map((item, index) => {
                      if (!ObjectUtil.isEmpty(item)) {
                        if (!ObjectUtil.isEmpty(item.srxPropertyUserPO)) {
                          if (
                            item.srxPropertyUserPO.occupancy > 0 ||
                            item.srxPropertyUserPO.purpose > 0
                          ) {
                            this.updatePropertyUser(item.srxPropertyUserPO);
                          }
                        }
                      }
                      count++;
                    });
                  }

                  if (count === properties.length) {
                    this.setState({ updateState: UpdateState.Updated }, () => {
                      //close modal
                      this.onPressCloseModal();
                    });
                  }
                } //end of updated properties
              }
            }
          }
        } else {
          this.setState({ updateState: UpdateState.Normal }, () => {
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              "An error occurred unexpectedly. Please try again later."
            );
          });
        }
      });
  }

  //api call to update password
  updatePropertyUser(srxPropertyUserPO) {
    PropertyTrackerService.updatePropertyUser({
      data: JSON.stringify(srxPropertyUserPO) //pass srx property user po with JSON string
    })
      .catch(error => {
        this.setState({ updateState: UpdateState.Normal }, () => {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        });
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            this.setState({ updateState: UpdateState.Normal }, () => {
              Alert.alert(AlertMessage.ErrorMessageTitle, error);
            });
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const { result } = response;
              if (!ObjectUtil.isEmpty(result)) {
                console.log(result);
                console.log("Successfully updated properties");
                Alert.alert(
                  AlertMessage.SuccessMessageTitle,
                  "Successfully updated"
                );
              }
            }
          }
        } else {
          this.setState({ updateState: UpdateState.Normal }, () => {
            Alert.alert(
              AlertMessage.ErrorMessageTitle,
              "An error occurred unexpectedly. Please try again later."
            );
          });
        }
      });
  }

  onPressNeedHelp() {
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: "SideMainMenu.NeedHelp",
              options: {
                modalPresentationStyle: 'overFullScreen',
              },
            }
          }
        ]
      }
    });
  }

  //on Update
  onPressUpdate() {
    if (this.isValidate()) {
      this.setState({ updateState: UpdateState.Updating }, () => {
        //start calling api to update
        this.securePropertyUser();
      });
    }
  }

  //update srx property po
  onUpdateSRXPropertyPO = (srxPropertyUserPO, error) => {
    const { properties } = this.state;
    if (
      !ObjectUtil.isEmpty(properties) &&
      !ObjectUtil.isEmpty(srxPropertyUserPO)
    ) {
      //Find Index
      var selectedSRXPropertyPOIndex = properties.findIndex(
        item => item.srxPropertyUserPO.ptUserId === srxPropertyUserPO.ptUserId
      );

      if (!ObjectUtil.isEmpty(selectedSRXPropertyPOIndex.toString())) {
        properties[selectedSRXPropertyPOIndex] = {
          srxPropertyUserPO: new SRXPropertyUserPO(srxPropertyUserPO),
          error
        };
      }

      this.setState({ properties });
    }
  };

  //start rendering methods
  renderSuccessfulSignIn() {
    return (
      <View
        style={{
          marginTop: Spacing.S,
          paddingHorizontal: Spacing.M,
          backgroundColor: SRXColor.SuccessBackground,
          height: 45,
          flexDirection: "row",
          alignItems: "center"
        }}
      >
        <Ionicons
          name="ios-checkmark-circle-outline"
          size={25}
          color={SRXColor.Green}
        />
        <SmallBodyText
          style={{ color: SRXColor.Green, marginLeft: Spacing.XS }}
        >
          You have successfully signed in
        </SmallBodyText>
      </View>
    );
  }

  rendermySgHomeDescription() {
    return (
      <View style={{ marginTop: Spacing.S, paddingHorizontal: Spacing.M }}>
        <SmallBodyText style={{ fontWeight: "500" }}>
          mySG Home access will no longer be via postcode and mobile number.
          Please verify your email and create a password
        </SmallBodyText>
      </View>
    );
  }

  renderUpdateDetailsForm() {
    const { updateUserDetails, isShowPwd } = this.state;
    return (
      <View style={{ padding: Spacing.M }}>
        {/* Email */}
        <View style={[Styles.inputContainer]}>
          <TextInput
            placeholder={"E-mail address"}
            error={updateUserDetails.errMsgEmail}
            defaultValue={updateUserDetails.email}
            onChangeText={newUserEmail =>
              this.setState({
                updateUserDetails: {
                  ...updateUserDetails,
                  email: newUserEmail,
                  errMsgEmail: ""
                }
              })
            }
          />
        </View>

        {/* Password */}
        <View style={[Styles.inputContainer, { marginTop: Spacing.M }]}>
          <TextInput
            placeholder={"Password"}
            error={updateUserDetails.errMsgPassword}
            secureTextEntry={isShowPwd ? false : true}
            onChangeText={newPassword =>
              this.setState({
                updateUserDetails: {
                  ...updateUserDetails,
                  password: newPassword,
                  errMsgPassword: ""
                }
              })
            }
            rightView={
              <Button
                buttonStyle={{ alignItems: "center", justifyContent: "center" }}
                onPress={this.toggleShowHidePassword}
                leftView={
                  isShowPwd ? (
                    <FeatherIcon name="eye" size={25} color={"#858585"} />
                  ) : (
                    <FeatherIcon name="eye-off" size={25} color={"#858585"} />
                  )
                }
              />
            }
          />
        </View>
      </View>
    );
  }

  renderMyPropertiesDescription() {
    return (
      <View
        style={{
          marginBottom: Spacing.S,
          paddingHorizontal: Spacing.M
        }}
      >
        <BodyText
          style={{
            fontWeight: "700",

            lineHeight: 22
          }}
        >
          My Properties
        </BodyText>
        <SmallBodyText
          style={{
            fontWeight: "500"
          }}
        >
          Select why you are tracking these properties to help us customise your
          experience
        </SmallBodyText>
      </View>
    );
  }
  renderMyPropertiesList() {
    const { properties, loadingState } = this.state;
    if (loadingState === LoadingState.Loading) {
      return (
        <View style={styles.activityIndicatorContainer}>
          <ActivityIndicator />
        </View>
      );
    } else {
      return (
        <View style={{ marginVertical: Spacing.XS }}>
          {this.renderMyPropertiesDescription()}

          <Separator />

          {/* show Properties list from loadPropertyTrackers */}
          <FlatList
            keyExtractor={item => item.key}
            extraData={this.state}
            data={properties}
            renderItem={({ item, index }) => this.renderItem({ item, index })}
          />
        </View>
      );
    }
  }

  renderUpdateActionButton() {
    //update button
    const { updateState } = this.state;
    var buttonTitle = "Update";
    if (updateState === UpdateState.Updating) {
      buttonTitle = "Updating";
    }
    return (
      <View style={styles.updateAndNeedHelpContainer}>
        <Button
          buttonType={Button.buttonTypes.primary}
          isSelected={updateState !== UpdateState.Normal}
          onPress={this.onPressUpdate}
        >
          {buttonTitle}
        </Button>
      </View>
    );
  }

  //need help
  renderNeedHelp() {
    return (
      <View
        style={[styles.updateAndNeedHelpContainer, { marginBottom: Spacing.M }]}
      >
        <Button
          textStyle={{ fontSize: 14, fontWeight: "400", color: SRXColor.Teal }}
          onPress={this.onPressNeedHelp}
        >
          Need help?
        </Button>
      </View>
    );
  }

  renderItem = ({ item, index }) => {
    return (
      <MyPropertyUpdateListItem
        key={index}
        error={item.error}
        srxPropertyUserPO={item.srxPropertyUserPO}
        containerStyle={{
          backgroundColor:
            index % 2 == 0 ? SRXColor.AccordionBackground : SRXColor.White
        }}
        onUpdateSRXPropertyPO={this.onUpdateSRXPropertyPO}
      />
    );
  };

  render() {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
        <KeyboardAwareScrollView
          onScroll={this.onScroll}
          style={{ flex: 1 }}
          keyboardShouldPersistTaps={"always"}
          innerRef={component => {
            this.scrollView = component;
          }}
        >
          {/* title for sign in screen */}
          {this.renderLargeTitle("Account Details")}
          {this.renderSuccessfulSignIn()}
          {this.rendermySgHomeDescription()}
          {this.renderUpdateDetailsForm()}
          {this.renderMyPropertiesList()}
          {this.renderUpdateActionButton()}
          {this.renderNeedHelp()}
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

SgHomeUpdateDetails.propTypes = {
  //get ptuser id
  srxPropertyUserPO: PropTypes.object,
  token: PropTypes.string
};

const styles = StyleSheet.create({
  updateAndNeedHelpContainer: {
    flex: 1,
    marginTop: Spacing.L,
    alignItems: "center",
    justifyContent: "center"
  },
  activityIndicatorContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    marginTop: Spacing.L
  }
});

const mapStateToProps = state => {
  return {
    errorMessage: state.loginData.errorMessage,
    userPO: state.loginData.userPO
  };
};

export default connect(
  mapStateToProps,
  { updateEmailAndPassword }
)(SgHomeUpdateDetails);
