import React, { Component } from "react";
import { View, SafeAreaView, Image, Alert, Platform } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { connect } from "react-redux";
import PropTypes from "prop-types";
import { Placeholder_Agent } from "../../assets";
import {
  TextInput,
  Heading2,
  Button,
  BodyText,
  SmallBodyText,
  Separator
} from "../../components";
import {
  SRXColor,
  AppConstant,
  LeadSources,
  LeadTypes,
  AlertMessage
} from "../../constants";
import { ListingPO, AgentPO } from "../../dataObject";
import { EnquiryService } from "../../services";
import { Spacing } from "../../styles";
import {
  ObjectUtil,
  CommonUtil,
  SetUtil,
  GoogleAnalyticUtil
} from "../../utils";
import { Navigation } from "react-native-navigation";

const SubmissionState = {
  Normal: "normal", //default, not submitting
  Submitting: "submitting", //sending requests
  Submitted: "submitted"
};

const MessagePlaceHolder =
  "Hi, I'm interested in your listing. Please give me a call.";

const isIOS = Platform.OS === "ios";

const EnquirySource = {
  Listings: "Listings", //from Listings
  AgentCV: "AgentCV", //from AgentCV
  ListingDetails: "ListingDetails", //from ListingDetails
  Shortlist: "Shortlist", // from Shortlist
  Communities: "Communities",
};

class EnquiryForm extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text:
            passProps.source === EnquirySource.AgentCV
              ? "Agent Enquiry"
              : "Enquire Listings"
        }
      },
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: isIOS ? false : true
      }
    };
  }

  static propTypes = {
    /**
     * Do not extract the agentPO out from listingPO
     * This form will send enquiry for each listings and each agents
     * Extracting out the agentPO will causing duplicate requests
     */
    agentPOs: PropTypes.arrayOf(PropTypes.instanceOf(AgentPO)),
    listingPOs: PropTypes.arrayOf(PropTypes.instanceOf(ListingPO)),
    onSuccess: PropTypes.func,
    source: PropTypes.oneOf(Object.keys(EnquirySource))
  };

  static defaultProps = {
    agentPOs: [],
    listingPOs: []
  };

  state = {
    defaultPlaceHolderMessage: MessagePlaceHolder,
    enquiringAgentPOs: [],
    enquiringListingPOs: [],
    submissionState: SubmissionState.Normal, //separated this boolean from enquiry, as it is used to indicate sending for multiple enquiries, not submitting for only 1 enquiry,
    enquiry: { countryCode: 65 },
    error: {
      fullNameError: "",
      mobileError: "",
      emailError: "",
      messageError: ""
    }
  };

  constructor(props) {
    super(props);

    this.onSubmitPressed = this.onSubmitPressed.bind(this);
  }

  async componentDidMount() {
    //convert userPO to enquiry
    const { loginData, listingPOs, agentPOs, source } = this.props;
    let newEnquiry = { ...this.state.enquiry };

    if (!ObjectUtil.isEmpty(loginData)) {
      const { userPO } = loginData;
      if (!ObjectUtil.isEmpty(userPO)) {
        newEnquiry = {
          ...newEnquiry,
          fullName: userPO.name,
          mobileLocalNumber: (userPO.mobileLocalNum || "") + "",
          email: userPO.email
        };
      }
    }

    //copy item to enquiry, for adding a parameter "enquired": Boolean, without changing the sources
    const copiedListingPOs = [],
      copiedAgentPOs = [];

    if (!ObjectUtil.isEmpty(listingPOs)) {
      listingPOs.map(item => {
        copiedListingPOs.push(new ListingPO(item));
      });
    }
    if (!ObjectUtil.isEmpty(agentPOs)) {
      agentPOs.map(item => {
        copiedAgentPOs.push(new AgentPO(item));
      });
    }

    let message = MessagePlaceHolder;

    if (source === EnquirySource.AgentCV) {
      if (!ObjectUtil.isEmpty(copiedAgentPOs)) {
        if (copiedAgentPOs.length == 1) {
          const copiedAgentPO = copiedAgentPOs[0];

          message =
            "Hi " +
            copiedAgentPO.name +
            ", I saw your Agent CV on SRX Property App and wanted to see if you could help me. Thank you.";
        } else {
          message =
            "Hi " +
            ", I saw your Agent CV on SRX Property App and wanted to see if you could help me. Thank you.";
        }
      }
    } else {
      if (!ObjectUtil.isEmpty(copiedListingPOs)) {
        if (copiedListingPOs.length == 1) {
          const listingPO = copiedListingPOs[0];
          message = await listingPO.getEnquiryMessageTemplate();
        } else {
          message =
            "Hi, I found your property listing on www.srx.com.sg and would like more information. Please send me more details. Thank you.";
        }
      }
    }

    newEnquiry = {
      ...newEnquiry,
      message: message
    };

    this.setState({
      enquiry: newEnquiry,
      enquiringListingPOs: copiedListingPOs,
      enquiringAgentPOs: copiedAgentPOs,
      defaultPlaceHolderMessage: message
    });
  }

  getUserInfo() {
    const { loginData } = this.props;
    if (!ObjectUtil.isEmpty(loginData)) {
      const { userPO } = loginData;
      if (!ObjectUtil.isEmpty(userPO)) {
        this.setState({
          enquiry: {
            ...this.state.enquiry,
            fullName: userPO.name,
            mobileLocalNumber: (userPO.mobileLocalNum || "") + "",
            email: userPO.email
          }
        });
      }
    }
  }

  fieldValidated() {
    const { enquiry } = this.state;
    const error = {
      fullNameError: "",
      mobileError: "",
      emailError: "",
      messageError: ""
    };

    var hasError = false;
    if (ObjectUtil.isEmpty(enquiry.fullName)) {
      error.fullNameError = "Required field";
      hasError = true;
    }
    if (ObjectUtil.isEmpty(enquiry.mobileLocalNumber)) {
      error.mobileError = "Required field";
      hasError = true;
    }
    if (ObjectUtil.isEmpty(enquiry.email)) {
      error.emailError = "Required field";
      hasError = true;
    }
    if (ObjectUtil.isEmpty(enquiry.message)) {
      error.messageError = "Required field";
      hasError = true;
    }

    this.setState({ error: { ...this.state.error, ...error } });
    return !hasError;
  }

  onSubmitPressed() {
    if (this.fieldValidated()) {
      const { source } = this.props;
      const { enquiringListingPOs, enquiringAgentPOs } = this.state;
      const allListingIds = new Set();
      const allAgentIds = new Set();
      const submittedListingIds = new Set();
      const submittedAgentIds = new Set();
      const errors = new Set();

      let googleAnalyticLeadSource;
      if (source === EnquirySource.AgentCV) {
        googleAnalyticLeadSource = LeadSources.agentCV;
      } else if (source === EnquirySource.Listings) {
        googleAnalyticLeadSource = LeadSources.listings;
      } else if (source === EnquirySource.ListingDetails) {
        googleAnalyticLeadSource = LeadSources.listingDetails;
      } else if (source === EnquirySource.Shortlist) {
        googleAnalyticLeadSource = LeadSources.Shortlist;
      } else if (source === EnquirySource.Communities) {
        googleAnalyticLeadSource = LeadSources.communities;
      }

      if (enquiringListingPOs.length > 0 || enquiringAgentPOs.length > 0) {
        this.setState({ submissionState: SubmissionState.Submitting });

        enquiringListingPOs.map(item => {
          allListingIds.add(item.getListingId());
          this.submitEnquiry({ listingId: item.getListingId() })
            .then(response => {
              const error = CommonUtil.getErrorMessageFromSRXResponse(response);
              if (!ObjectUtil.isEmpty(error)) {
                errors.add(error);
                item.enquired = false;
              } else {
                item.enquired = true;
                //track on success
                GoogleAnalyticUtil.trackLeads({
                  leadType: LeadTypes.EnquirySubmission,
                  source: googleAnalyticLeadSource
                });
              }
            })
            .catch(error => {
              //havent decide
            })
            .finally(() => {
              submittedListingIds.add(item.getListingId());
              this.handleEnquiryResponse({
                allListingIds,
                allAgentIds,
                submittedListingIds,
                submittedAgentIds,
                errors
              });
            });
        });

        enquiringAgentPOs.map(item => {
          allAgentIds.add(item.userId);
          this.submitEnquiry({ agentUserId: item.userId })
            .then(response => {
              const error = CommonUtil.getErrorMessageFromSRXResponse(response);
              if (!ObjectUtil.isEmpty(error)) {
                errors.add(error);
                item.enquired = false;
              } else {
                item.enquired = true;

                GoogleAnalyticUtil.trackLeads({
                  leadType: LeadTypes.EnquirySubmission,
                  source: googleAnalyticLeadSource
                });
              }
            })
            .catch(error => {
              //havent decide
            })
            .finally(() => {
              submittedAgentIds.add(item.userId);
              this.handleEnquiryResponse({
                allListingIds,
                allAgentIds,
                submittedListingIds,
                submittedAgentIds,
                errors
              });
            });
        });
      }
    }
  }

  handleEnquiryResponse({
    allListingIds,
    allAgentIds,
    submittedListingIds,
    submittedAgentIds,
    errors
  }) {
    const listingIdsDiff = SetUtil.difference(
      allListingIds,
      submittedListingIds
    );
    const agentIdsDiff = SetUtil.difference(allAgentIds, submittedAgentIds);

    if (listingIdsDiff.size == 0 && agentIdsDiff.size == 0) {
      //all request counted

      const { enquiringListingPOs, enquiringAgentPOs } = this.state;

      const listingFailedToEnquired = [];
      const agentFailedToEnquired = [];

      enquiringListingPOs.map(item => {
        if (!item.enquired) {
          listingFailedToEnquired.push(item);
        }
      });
      enquiringAgentPOs.map(item => {
        if (!item.enquired) {
          agentFailedToEnquired.push(item);
        }
      });

      if (
        !ObjectUtil.isEmpty(listingFailedToEnquired) ||
        !ObjectUtil.isEmpty(agentFailedToEnquired)
      ) {
        this.setState({
          submissionState: SubmissionState.Normal,
          enquiringListingPOs: listingFailedToEnquired,
          enquiringAgentPOs: agentFailedToEnquired
        });
        if (errors.size > 0) {
          Alert.alert(
            AlertMessage.ErrorMessageTitle,
            Array.from(errors).join("\n")
          );
        }
      } else {
        const { onSuccess } = this.props;
        this.setState(
          {
            submissionState: SubmissionState.Submitted,
            enquiringListingPOs: listingFailedToEnquired,
            enquiringAgentPOs: agentFailedToEnquired
          },
          () => {
            //if this is a sub component of a screen, pop should not be triggered
            if (this.props.componentId) {
              Navigation.pop(this.props.componentId).then(() => {
                if (onSuccess) {
                  onSuccess();
                }
              });
            } else {
              if (onSuccess) {
                onSuccess();
              }
            }
          }
        );
      }
    }
  }

  /*
   * Either provide listingId or agentUserId,
   * if both provided listingId will be used
   */
  submitEnquiry({ listingId, agentUserId }) {
    const data = {
      ...this.state.enquiry
    };
    if (listingId) {
      data.listingId = listingId;
    }
    if (agentUserId) {
      data.agentUserId = agentUserId;
    }

    return new Promise(function(resolve, reject) {
      EnquiryService.submitSrxAgentEnquiryV2(data)
        .then(response => {
          resolve(response);
        })
        .catch(error => {
          reject(error);
        });
    });
  }

  renderForm() {
    const { enquiry, error } = this.state;
    return (
      <KeyboardAwareScrollView style={{ flex: 1 }}>
        <View style={{ padding: Spacing.M }}>
          <View style={Styles.textInputContainer}>
            <TextInput
              title={"Name"}
              value={enquiry.fullName}
              onChangeText={text =>
                this.setState({
                  enquiry: { ...enquiry, fullName: text },
                  error: { ...error, fullNameError: "" }
                })
              }
              error={error.fullNameError}
            />
          </View>
          <View style={Styles.textInputContainer}>
            <TextInput
              title={"Mobile"}
              value={enquiry.mobileLocalNumber}
              onChangeText={text =>
                this.setState({
                  enquiry: { ...enquiry, mobileLocalNumber: text },
                  error: { ...error, mobileError: "" }
                })
              }
              error={error.mobileError}
            />
          </View>
          <View style={Styles.textInputContainer}>
            <TextInput
              title={"Email"}
              value={enquiry.email}
              onChangeText={text =>
                this.setState({
                  enquiry: { ...enquiry, email: text },
                  error: { ...error, emailError: "" }
                })
              }
              error={error.emailError}
            />
          </View>
          <View style={Styles.textInputContainer}>
            <TextInput
              title={"Message"}
              multiline={true}
              value={enquiry.message}
              placeholder={MessagePlaceHolder}
              onChangeText={text =>
                this.setState({
                  enquiry: { ...enquiry, message: text },
                  error: { ...error, messageError: "" }
                })
              }
              error={error.messageError}
            />
          </View>
        </View>
      </KeyboardAwareScrollView>
    );
  }

  renderEnquiryAgentView(agentPO) {
    const agentPhotoURL = agentPO.getAgentPhoto();
    return (
      <View
        style={{
          flexDirection: "row",
          alignItems: "center",
          flex: 1
        }}
      >
        <Image
          style={{ height: 40, width: 40, borderRadius: 20 }}
          defaultSource={Placeholder_Agent}
          source={{ uri: agentPhotoURL }}
        />
        <Heading2
          style={{
            marginHorizontal: Spacing.XS,
            flex: 1
          }}
          numberOfLines={2}
        >
          {agentPO.name}
        </Heading2>
      </View>
    );
  }

  renderEnquirySubjects() {
    const { enquiringListingPOs, enquiringAgentPOs } = this.state;

    if (!ObjectUtil.isEmpty(enquiringListingPOs)) {
      if (enquiringListingPOs.length == 1) {
        const listingPO = enquiringListingPOs[0];
        if (!ObjectUtil.isEmpty(listingPO.getAgentPO())) {
          return this.renderEnquiryAgentView(listingPO.getAgentPO());
        }
      }
      return (
        <Heading2 style={{ flex: 1 }}>
          Send to{" "}
          {enquiringListingPOs.length == 1
            ? enquiringListingPOs.length + " listing"
            : enquiringListingPOs.length + " listings"}
        </Heading2>
      );
    } else if (!ObjectUtil.isEmpty(enquiringAgentPOs)) {
      if (enquiringAgentPOs.length == 1) {
        const agentPO = enquiringAgentPOs[0];
        if (!ObjectUtil.isEmpty(agentPO)) {
          return this.renderEnquiryAgentView(agentPO);
        }
      }
      return (
        <Heading2 style={{ flex: 1 }}>
          Send to{" "}
          {enquiringAgentPOs.length == 1
            ? enquiringAgentPOs.length + " agent"
            : enquiringAgentPOs.length + " agents"}
        </Heading2>
      );
    } else {
      return <View style={{ flex: 1 }} />;
    }
  }

  renderSubmitButton = () => {
    const { submissionState } = this.state;
    var buttonTitle = "Submit";
    if (submissionState === SubmissionState.Submitting) {
      buttonTitle = "Submitting...";
    }
    return (
      <Button
        buttonType={Button.buttonTypes.primary}
        buttonStyle={{ paddingHorizontal: Spacing.XL }}
        onPress={this.onSubmitPressed}
        isSelected={submissionState === SubmissionState.Submitting}
      >
        {buttonTitle}
      </Button>
    );
  };

  renderBottomActionBar() {
    const { listingPOs, agentPOs, source } = this.props;
    if (!ObjectUtil.isEmpty(listingPOs) || !ObjectUtil.isEmpty(agentPOs)) {
      if (source === EnquirySource.AgentCV) {
        return (
          <View
            style={{
              alignItems: "center",
              paddingBottom: Spacing.XS
            }}
          >
            {this.renderSubmitButton()}
          </View>
        );
      } else {
        return (
          //by new design requirement
          <SafeAreaView style={{ backgroundColor: SRXColor.White }}>
            <Separator />
            <View
              style={{
                padding: Spacing.M,
                flexDirection: "row",
                alignItems: "center"
              }}
            >
              {this.renderEnquirySubjects()}
              {this.renderSubmitButton()}
            </View>
          </SafeAreaView>
        );
      }
    } else {
      return <View />;
    }
  }

  renderSubmittedView() {
    return (
      <View
        style={{
          flex: 1,
          justifyContent: "center",
          alignItems: "center",
          paddingHorizontal: Spacing.L,
          paddingVertical: Spacing.M
        }}
      >
        <BodyText style={{ textAlign: "center" }}>
          Thank you for your message.{"\n"}The agent will be informed of your
          request.
        </BodyText>
      </View>
    );
  }

  render() {
    const { submissionState } = this.state;
    if (submissionState === SubmissionState.Submitted) {
      return this.renderSubmittedView();
    } else {
      return (
        <View style={{ flex: 1 }}>
          {this.renderForm()}
          {this.renderBottomActionBar()}
        </View>
      );
    }
  }
}

EnquiryForm.Sources = EnquirySource;

const Styles = {
  textInputContainer: {
    paddingVertical: Spacing.XS
  }
};

const mapStateToProps = state => {
  return { loginData: state.loginData };
};

export default connect(mapStateToProps)(EnquiryForm);
