import React, { Component } from "react";
import {
  ActivityIndicator,
  TouchableHighlight,
  SafeAreaView
} from "react-native";
import { View, Platform } from "react-native";
import {
  BodyText,
  EntypoIcon,
  EnquirySheet,
  EnquirySheetSource,
  FeatherIcon,
  Heading2,
  Separator
} from "../../components";
import { SRXColor } from "../../constants";
import { Navigation } from "react-native-navigation";
import { AgentPO } from "../../dataObject";
import { ObjectUtil } from "../../utils";
import { Spacing } from "../../styles";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { AgentCvService, ChatService } from "../../services";
import PropTypes from "prop-types";
import {
  AboutMe,
  CredentialsAndAwards,
  AgentTestimonialList
} from "./AgentCvContent";

import { AgentCvProfile } from "./AgentCvComponent";
import { EnquiryForm } from "../Enquiry";
import { connect } from "react-redux";

const isIOS = Platform.OS === "ios";
const propertyType_HDB = "HDB";
const propertyType_PRIVATE = "Private";

const SECTION_TYPE = {
  AboutMe: "About me",
  ViewSaleListings: "View all listings for Sale",
  ViewRentListings: "View all listings for Rent",
  CredentialsAndAwards: "Credentials and Awards",
  MyTransactions: "My Transactions",
  Testimonials: "Testimonials",
  ContactAgent: "Contact Agent"
};

class AgentCV extends Component {
  static propTypes = {
    agentUserId: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
  };

  state = {
    isLoading: true,
    sections: [],
    conversationId: null
  };

  static options(passProps) {
    return {
      //remove bottom bar in agent CV screen
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true
      }
    };
  }

  addRightChatButton() {
    const componentId = this.props.componentId;
    return new Promise(function(resolve, reject) {
      EntypoIcon.getImageSource("chat", 25, "white")
        .then(chat => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              title: {
                text: "Agent CV"
              },
              rightButtons: [
                {
                  id: "agentCv_chatBtn",
                  icon: chat
                }
              ]
            }
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        });
    }); //end of promise
  }

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
    this.addRightChatButton();
  }

  componentDidMount() {
    const { agentUserId, userPO } = this.props;
    this.loadUserAgentWithCv({ agentUserId });
    if (!ObjectUtil.isEmpty(userPO)) {
      this.findConversationId(agentUserId);
    }
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "agentCv_chatBtn") {
      this.chatAgent();
    }
  }

  findConversationId = agentId => {
    const userIdList = [agentId.toString()];
    ChatService.findConversationIds({
      otherUserIds: JSON.stringify(userIdList)
    }).then(secondResponse => {
      if (!ObjectUtil.isEmpty(secondResponse)) {
        const { conversationIds } = secondResponse;
        if (!ObjectUtil.isEmpty(conversationIds)) {
          let conversationIdList = Object.values(conversationIds);
          this.setState({ conversationId: conversationIdList[0] });
        }
      }
    });
  };

  chatAgent = () => {
    const passData = {
      enquiryCallerComponentId: this.props.componentId
    };
    const { agentPO, conversationId } = this.state;
    {
      if (!ObjectUtil.isEmpty(agentPO)) {
        passData.agentPO = agentPO;
        passData.source = EnquirySheetSource.AgentCV;
      }
      if (conversationId != null) {
        passData.conversationId = conversationId;
      }
    }
    EnquirySheet.show(passData);
  };

  //api
  loadUserAgentWithCv = ({ agentUserId }) => {
    if (agentUserId) {
      AgentCvService.loadUserAgentWithCv({ userId: agentUserId })
        .then(response => {
          if (!ObjectUtil.isEmpty(response)) {
            const { result } = response;
            if (!ObjectUtil.isEmpty(result)) {
              const agentPO = new AgentPO(result);
              const sections = [];

              //About Me
              if (!ObjectUtil.isEmpty(agentPO)) {
                if (!ObjectUtil.isEmpty(agentPO.agentCvPO)) {
                  if (!ObjectUtil.isEmpty(agentPO.agentCvPO.aboutMe)) {
                    sections.push(SECTION_TYPE.AboutMe);
                  }
                }
              }

              //ViewSaleListings
              if (
                !ObjectUtil.isEmpty(agentPO) &&
                agentPO.listingSummary.saleTotal > 0 &&
                agentPO.agentCvPO.showListings
              ) {
                sections.push(SECTION_TYPE.ViewSaleListings);
              }

              //ViewRentListings
              if (
                !ObjectUtil.isEmpty(agentPO) &&
                agentPO.listingSummary.rentTotal > 0 &&
                agentPO.agentCvPO.showListings
              ) {
                sections.push(SECTION_TYPE.ViewRentListings);
              }

              //CredentialsAndAwards
              if (!ObjectUtil.isEmpty(agentPO)) {
                if (!ObjectUtil.isEmpty(agentPO.agentCvPO)) {
                  if (!ObjectUtil.isEmpty(agentPO.agentCvPO.awards)) {
                    sections.push(SECTION_TYPE.CredentialsAndAwards);
                  }
                }
              }

              //My Transactions
              if (
                !ObjectUtil.isEmpty(agentPO) &&
                !ObjectUtil.isEmpty(agentPO.agentCvPO) &&
                agentPO.agentCvPO.showTransactions
              ) {
                if (!ObjectUtil.isEmpty(agentPO.transactionSummary)) {
                  if (
                    agentPO.transactionSummary.rentHdbTotal > 0 ||
                    agentPO.transactionSummary.rentPrivateTotal > 0 ||
                    agentPO.transactionSummary.saleHdbTotal > 0 ||
                    agentPO.transactionSummary.salePrivateTotal > 0
                  ) {
                    sections.push(SECTION_TYPE.MyTransactions);
                  }
                }
              }

              //Testimonials
              if (!ObjectUtil.isEmpty(agentPO)) {
                if (!ObjectUtil.isEmpty(agentPO.agentCvPO)) {
                  if (!ObjectUtil.isEmpty(agentPO.agentCvPO.testimonials)) {
                    sections.push(SECTION_TYPE.Testimonials);
                  }
                }
              }

              //ContactAgent
              sections.push(SECTION_TYPE.ContactAgent);

              this.setState(
                {
                  agentPO,
                  sections: sections,
                  isLoading: false
                },
                () => {
                  //load listings and transactions
                }
              );
            }
          }
        })
        .catch(error => {
          console.log(error);
        });
    }
  };

  onSelectAgentSaleListingResult = () => {
    const { agentPO } = this.state;
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      agentPO.listingSummary.saleTotal > 0 &&
      agentPO.agentCvPO.showListings
    ) {
      Navigation.push(this.props.componentId, {
        component: {
          name: "ConciergeStack.AgentListingResult",
          passProps: {
            agentPO: agentPO,
            agentUserId: agentPO.getAgentId(),
            type: "S"
          }
        }
      });
    }
  };

  onSelectAgentRentListingResult = () => {
    const { agentPO } = this.state;
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      agentPO.listingSummary.rentTotal > 0 &&
      agentPO.agentCvPO.showListings
    ) {
      Navigation.push(this.props.componentId, {
        component: {
          name: "ConciergeStack.AgentListingResult",
          passProps: {
            agentPO: agentPO,
            agentUserId: agentPO.getAgentId(),
            type: "R"
          }
        }
      });
    }
  };

  onSelectAgentSaleTransactionResult = propertyType => {
    const { agentPO } = this.state;
    const { agentCvPO } = this.state.agentPO;
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentCvPO) &&
      agentCvPO.showTransactions &&
      (agentPO.transactionSummary.saleHdbTotal > 0 ||
        agentPO.transactionSummary.salePrivateTotal > 0)
    ) {
      Navigation.push(this.props.componentId, {
        component: {
          name: "ConciergeStack.AgentTransactionsResult",
          passProps: {
            agentPO: agentPO,
            agentUserId: agentPO.getAgentId(),
            type: "S",
            propertyType:
              propertyType == propertyType_HDB
                ? propertyType_HDB
                : propertyType_PRIVATE
          }
        }
      });
    }
  };

  onSelectAgentRentTransactionResult = propertyType => {
    const { agentPO } = this.state;
    const { agentCvPO } = this.state.agentPO;
    if (
      (!ObjectUtil.isEmpty(agentPO) &&
        !ObjectUtil.isEmpty(agentCvPO) &&
        agentCvPO.showTransactions &&
        agentPO.transactionSummary.rentHdbTotal > 0) ||
      agentPO.transactionSummary.rentPrivateTotal > 0
    ) {
      Navigation.push(this.props.componentId, {
        component: {
          name: "ConciergeStack.AgentTransactionsResult",
          passProps: {
            agentPO: agentPO,
            agentUserId: agentPO.getAgentId(),
            type: "R",
            propertyType:
              propertyType == propertyType_HDB
                ? propertyType_HDB
                : propertyType_PRIVATE
          }
        }
      });
    }
  };

  backgroundStyleForSection(section) {
    return section % 2 == 0
      ? Styles.evenSectionBackground
      : Styles.oddSectionBackground;
  }

  renderAboutMe() {
    const { sections } = this.state;
    const { agentCvPO } = this.state.agentPO;
    let sectionIndex = sections.indexOf(SECTION_TYPE.AboutMe);
    if (!ObjectUtil.isEmpty(agentCvPO)) {
      if (!ObjectUtil.isEmpty(agentCvPO.aboutMe)) {
        return (
          <AboutMe
            aboutMe={agentCvPO.aboutMe}
            style={this.backgroundStyleForSection(sectionIndex)}
          />
        );
      }
    }
  }

  renderAgentCvProfile() {
    const { agentPO } = this.state;
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.agentCvPO)
    ) {
      return <AgentCvProfile agentPO={agentPO} />;
    }
  }

  renderAgentSaleListing() {
    const { agentPO, sections } = this.state;
    sectionIndex = sections.indexOf(SECTION_TYPE.ViewSaleListings);
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      agentPO.listingSummary.saleTotal > 0 &&
      agentPO.agentCvPO.showListings
    ) {
      return (
        <TouchableHighlight onPress={this.onSelectAgentSaleListingResult}>
          <View style={{ backgroundColor: SRXColor.White }}>
            <View
              style={[
                {
                  padding: Spacing.M,
                  flexDirection: "row",
                  alignItems: "center",
                  backgroundColor: SRXColor.White
                },
                this.backgroundStyleForSection(sectionIndex)
              ]}
            >
              <BodyText style={{ flex: 1, marginRight: Spacing.XS }}>
                {SECTION_TYPE.ViewSaleListings}
                <BodyText style={{ color: SRXColor.Teal }}>
                  {" ("}
                  {agentPO.listingSummary.saleTotal}
                  {")"}
                </BodyText>
              </BodyText>
              <FeatherIcon
                name="chevron-right"
                size={20}
                color={SRXColor.Black}
              />
            </View>
            <Separator />
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }

  renderAgentRentListing() {
    const { agentPO, sections } = this.state;
    sectionIndex = sections.indexOf(SECTION_TYPE.ViewRentListings);
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      agentPO.listingSummary.rentTotal > 0 &&
      agentPO.agentCvPO.showListings
    ) {
      return (
        <TouchableHighlight onPress={this.onSelectAgentRentListingResult}>
          <View style={{ backgroundColor: SRXColor.White }}>
            <View
              style={[
                {
                  padding: Spacing.M,
                  flexDirection: "row",
                  alignItems: "center",
                  backgroundColor: SRXColor.White
                },
                this.backgroundStyleForSection(sectionIndex)
              ]}
            >
              <BodyText style={{ flex: 1, marginRight: Spacing.XS }}>
                {SECTION_TYPE.ViewRentListings}
                <BodyText style={{ color: SRXColor.Teal }}>
                  {" ("}
                  {agentPO.listingSummary.rentTotal}
                  {")"}
                </BodyText>
              </BodyText>
              <FeatherIcon
                name="chevron-right"
                size={20}
                color={SRXColor.Black}
              />
            </View>
            <Separator />
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }

  renderCredentialsAndAwards() {
    const { sections, agentPO } = this.state;
    const { agentCvPO } = this.state.agentPO;
    sectionIndex = sections.indexOf(SECTION_TYPE.CredentialsAndAwards);
    if (!ObjectUtil.isEmpty(agentPO)) {
      if (!ObjectUtil.isEmpty(agentCvPO)) {
        if (!ObjectUtil.isEmpty(agentCvPO.credentialAwards)) {
          return (
            <View
              style={[
                {
                  paddingTop: Spacing.M
                },
                this.backgroundStyleForSection(sectionIndex)
              ]}
            >
              <Heading2 style={{ flex: 1, paddingLeft: Spacing.M }}>
                {SECTION_TYPE.CredentialsAndAwards}
              </Heading2>
              <CredentialsAndAwards
                credentialAwards={agentCvPO.credentialAwards}
                style={this.backgroundStyleForSection(sectionIndex)}
              />
            </View>
          );
        }
      }
    }
  }

  renderMyTransactions() {
    const { sections } = this.state;
    const { transactionSummary, agentCvPO } = this.state.agentPO;
    sectionIndex = sections.indexOf(SECTION_TYPE.MyTransactions);
    if (
      !ObjectUtil.isEmpty(transactionSummary) &&
      !ObjectUtil.isEmpty(agentCvPO) &&
      agentCvPO.showTransactions
    ) {
      if (
        transactionSummary.rentHdbTotal > 0 ||
        transactionSummary.rentPrivateTotal > 0 ||
        transactionSummary.saleHdbTotal > 0 ||
        transactionSummary.salePrivateTotal > 0
      ) {
        return (
          <View>
            <View
              style={[
                {
                  paddingHorizontal: Spacing.M,
                  paddingTop: Spacing.M,
                  paddingBottom: Spacing.S
                },
                this.backgroundStyleForSection(sectionIndex)
              ]}
            >
              <Heading2 style={{ flex: 1, marginRight: Spacing.XS }}>
                {SECTION_TYPE.MyTransactions}
              </Heading2>
              <View style={{ marginTop: Spacing.L }}>
                {this.renderHDBTransactions()}
                {this.renderHdbPrivateSeparator()}
                {this.renderPrivateTransactions()}
              </View>
            </View>
            <Separator />
          </View>
        );
      }
    } else {
      return <View />;
    }
  }

  renderTestimonial() {
    const { sections } = this.state;
    const { agentCvPO } = this.state.agentPO;
    let sectionIndex = sections.indexOf(SECTION_TYPE.Testimonials);
    if (!ObjectUtil.isEmpty(agentCvPO)) {
      if (!ObjectUtil.isEmpty(agentCvPO.testimonials)) {
        const { testimonials } = this.state.agentPO.agentCvPO;
        return (
          <AgentTestimonialList
            testimonials={testimonials}
            style={this.backgroundStyleForSection(sectionIndex)}
          />
        );
      }
    }
  }

  renderContactAgent() {
    const { sections, agentPO } = this.state;
    const { loginData } = this.props;
    let agentPOs = [agentPO];
    sectionIndex = sections.indexOf(SECTION_TYPE.ContactAgent);
    return (
      <View>
        <View
          style={[
            {
              padding: Spacing.M,
              backgroundColor: SRXColor.White
            },
            this.backgroundStyleForSection(sectionIndex)
          ]}
        >
          <Heading2>{SECTION_TYPE.ContactAgent}</Heading2>
          <View style={{ marginHorizontal: -Spacing.M }}>
            <EnquiryForm
              agentPOs={agentPOs}
              source={EnquiryForm.Sources.AgentCV}
            />
          </View>
        </View>
        <Separator />
      </View>
    );
  }

  renderHDBTransactions() {
    const {
      saleHdbTotal,
      rentHdbTotal
    } = this.state.agentPO.transactionSummary;
    if (saleHdbTotal || rentHdbTotal) {
      return (
        <View>
          <Heading2 style={{ textAlign: "center" }}>HDB</Heading2>
          <View
            style={[{ marginBottom: Spacing.L }, Styles.soldAndRentContainer]}
          >
            {saleHdbTotal == 0 ? (
              <View
                style={[
                  { borderRightWidth: 0 },
                  Styles.soldAndRentSubContainer
                ]}
              >
                <BodyText style={{ textAlign: "center" }}>
                  {saleHdbTotal} Sold
                </BodyText>
              </View>
            ) : (
              <TouchableHighlight
                onPress={() =>
                  this.onSelectAgentSaleTransactionResult(propertyType_HDB)
                }
              >
                <View
                  style={[
                    { borderRightWidth: 0, backgroundColor: SRXColor.White },
                    Styles.soldAndRentSubContainer
                  ]}
                >
                  <BodyText style={{ textAlign: "center" }}>
                    {saleHdbTotal} Sold
                  </BodyText>
                </View>
              </TouchableHighlight>
            )}
            {rentHdbTotal == 0 ? (
              <View
                style={[{ borderLeftWidth: 1 }, Styles.soldAndRentSubContainer]}
              >
                <BodyText style={{ textAlign: "center" }}>
                  {rentHdbTotal} Rented
                </BodyText>
              </View>
            ) : (
              <TouchableHighlight
                onPress={() =>
                  this.onSelectAgentRentTransactionResult(propertyType_HDB)
                }
              >
                <View
                  style={[
                    { borderLeftWidth: 1, backgroundColor: SRXColor.White },
                    Styles.soldAndRentSubContainer
                  ]}
                >
                  <BodyText style={{ textAlign: "center" }}>
                    {rentHdbTotal} Rented
                  </BodyText>
                </View>
              </TouchableHighlight>
            )}
          </View>
        </View>
      );
    }
  }

  renderPrivateTransactions() {
    const {
      salePrivateTotal,
      rentPrivateTotal
    } = this.state.agentPO.transactionSummary;
    if (salePrivateTotal || rentPrivateTotal) {
      return (
        <View style={{ marginTop: Spacing.S }}>
          <Heading2 style={{ textAlign: "center" }}>Private</Heading2>
          <View style={Styles.soldAndRentContainer}>
            {salePrivateTotal == 0 ? (
              <View
                style={[
                  { borderRightWidth: 0 },
                  Styles.soldAndRentSubContainer
                ]}
              >
                <BodyText style={{ textAlign: "center" }}>
                  {salePrivateTotal} Sold
                </BodyText>
              </View>
            ) : (
              <TouchableHighlight
                onPress={() =>
                  this.onSelectAgentSaleTransactionResult(propertyType_PRIVATE)
                }
              >
                <View
                  style={[
                    { borderRightWidth: 0, backgroundColor: SRXColor.White },
                    Styles.soldAndRentSubContainer
                  ]}
                >
                  <BodyText style={{ textAlign: "center" }}>
                    {salePrivateTotal} Sold
                  </BodyText>
                </View>
              </TouchableHighlight>
            )}
            {rentPrivateTotal == 0 ? (
              <View
                style={[{ borderLeftWidth: 1 }, Styles.soldAndRentSubContainer]}
              >
                <BodyText style={{ textAlign: "center" }}>
                  {rentPrivateTotal} Rented
                </BodyText>
              </View>
            ) : (
              <TouchableHighlight
                onPress={() =>
                  this.onSelectAgentRentTransactionResult(propertyType_PRIVATE)
                }
              >
                <View
                  style={[
                    { borderLeftWidth: 1, backgroundColor: SRXColor.White },
                    Styles.soldAndRentSubContainer
                  ]}
                >
                  <BodyText style={{ textAlign: "center" }}>
                    {rentPrivateTotal} Rented
                  </BodyText>
                </View>
              </TouchableHighlight>
            )}
          </View>
        </View>
      );
    }
  }

  renderHdbPrivateSeparator() {
    const {
      saleHdbTotal,
      rentHdbTotal,
      salePrivateTotal,
      rentPrivateTotal
    } = this.state.agentPO.transactionSummary;
    if (
      (saleHdbTotal || rentHdbTotal) &&
      (salePrivateTotal || rentPrivateTotal)
    ) {
      return <Separator />;
    }
  }

  render() {
    const { agentPO, isLoading, sections } = this.state;
    if (isLoading) {
      return (
        <View
          style={{ flex: 1, alignItems: "center", justifyContent: "center" }}
        >
          <ActivityIndicator />
        </View>
      );
    } else {
      if (
        !isLoading &&
        !ObjectUtil.isEmpty(agentPO) &&
        !ObjectUtil.isEmpty(agentPO.agentCvPO)
      ) {
        return (
          <SafeAreaView style={{ flex: 1 }}>
            {this.renderAgentCvProfile()}
            <KeyboardAwareScrollView
              style={{ flex: 1 }}
              keyboardShouldPersistTaps={"always"}
            >
              {this.renderAboutMe()}
              {this.renderAgentSaleListing()}
              {this.renderAgentRentListing()}
              {this.renderCredentialsAndAwards()}
              {this.renderMyTransactions()}
              {this.renderTestimonial()}
              {this.renderContactAgent()}
            </KeyboardAwareScrollView>
          </SafeAreaView>
        );
      } else {
        return (
          <View
            style={{
              flex: 1,
              justifyContent: "center",
              alignItems: "center",
              padding: Spacing.M
            }}
          >
            <BodyText
              style={{
                color: SRXColor.Gray,
                lineHeight: 22,
                textAlign: "center"
              }}
            >
              Agent did not activate Agent CV feature. Contact Agent to find out
              more.
            </BodyText>
          </View>
        );
      }
    }
  }
}

const Styles = {
  evenSectionBackground: {
    backgroundColor: SRXColor.White
  },
  oddSectionBackground: {
    backgroundColor: SRXColor.AccordionBackground
  },
  soldAndRentContainer: {
    flexDirection: "row",
    flex: 1,
    justifyContent: "center",
    marginTop: Spacing.XS
  },
  soldAndRentSubContainer: {
    borderColor: "#e0e0e0",
    borderWidth: 1,
    borderColor: "#e0e0e0",
    borderWidth: 1,
    width: 116,
    height: 50,
    justifyContent: "center"
  }
};

const mapStateToProps = state => {
  return { loginData: state.loginData, userPO: state.loginData.userPO };
};

export default connect(mapStateToProps)(AgentCV);
