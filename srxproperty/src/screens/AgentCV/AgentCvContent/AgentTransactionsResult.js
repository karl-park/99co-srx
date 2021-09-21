import React, { Component } from "react";
import { View, SafeAreaView, ActivityIndicator } from "react-native";
import { AgentCvProfile } from "../AgentCvComponent";
import { ObjectUtil, PropertyTypeUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { SRXColor } from "../../../constants";
import { BodyText } from "../../../components";
import PropTypes from "prop-types";
import { ListingResultPO, AgentPO } from "../../../dataObject";
import { AgentTransactionsResultList } from "../AgentCvContent";
import { AgentCvService } from "../../../services";

const propertyType_HDB = "HDB";
const propertyType_PRIVATE = "Private";

class AgentTransactionsResult extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: "My Transactions"
        }
      }
    };
  }

  static propTypes = {
    agentPO: PropTypes.instanceOf(AgentPO),
    agentUserId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    type: PropTypes.string,
    propertyType: PropTypes.string
  };

  static defaultProps = {
    agentPO: null,
    agentUserId: "",
    type: "",
    propertyType: ""
  };

  state = {
    isLoading: true
  };

  componentDidMount() {
    const { agentUserId, type } = this.props;
    this.findActiveAndTransactedListingsByUserId({ agentUserId, type });
  }

  findActiveAndTransactedListingsByUserId = ({ agentUserId, type }) => {
    if (agentUserId) {
      AgentCvService.findActiveAndTransactedListingsByUserId({
        userId: agentUserId
      })
        .then(response => {
          const newtransactedListings = [];
          let transactionsResultPO = null;

          if (!ObjectUtil.isEmpty(response)) {
            const { transactedListingPOs } = response;

            if (!ObjectUtil.isEmpty(transactedListingPOs)) {
              transactedListingPOs.map(item => {
                newtransactedListings.push(new ListingResultPO(item));
              });

              if (type == "S") {
                transactionsResultPO = newtransactedListings.find(
                  item => item.type == "sale"
                );
              } else {
                transactionsResultPO = newtransactedListings.find(
                  item => item.type == "rent"
                );
              }
            }

            this.setState({
              transactionsResultPO,
              isLoading: false
            });
          }
        })
        .catch(error => {
          console.log(error);
        });
    }
  };

  //render
  renderAgentCvProfile() {
    const { agentPO } = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      return <AgentCvProfile agentPO={agentPO} />;
    }
  }

  renderNoOfTransactedListingAndResultList() {
    const { isLoading, transactionsResultPO } = this.state;
    if (!isLoading && !ObjectUtil.isEmpty(transactionsResultPO)) {
      return (
        <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
          {this.renderNoOfTransactedListingProperties()}
          {this.renderResultList()}
        </SafeAreaView>
      );
    } else {
      return (
        <View
          style={{ flex: 1, alignItems: "center", justifyContent: "center" }}
        >
          <ActivityIndicator />
        </View>
      );
    }
  }

  renderNoOfTransactedListingProperties() {
    const { agentPO, type, propertyType } = this.props;
    const { transactionsResultPO } = this.state;
    let noOfProperty = 0;

    if (!ObjectUtil.isEmpty(transactionsResultPO)) {
      if (propertyType == propertyType_HDB) {
        noOfProperty =
          type === "S"
            ? agentPO.transactionSummary.saleHdbTotal
            : agentPO.transactionSummary.rentHdbTotal;
      } else {
        noOfProperty =
          type === "S"
            ? agentPO.transactionSummary.salePrivateTotal
            : agentPO.transactionSummary.rentPrivateTotal;
      }
    }

    return (
      <View style={Styles.noOfListingPropertiesContainer}>
        <BodyText>
          {noOfProperty} {noOfProperty > 1 ? "properties" : "property"}
        </BodyText>
      </View>
    );
  }

  renderResultList() {
    const { type, propertyType } = this.props;
    const { transactionsResultPO } = this.state;
    let transactionsResultPOs = [];

    if (!ObjectUtil.isEmpty(transactionsResultPO)) {
      const { listingPOs } = transactionsResultPO;

      transactionsHDBResultPOs = listingPOs.filter(item =>
        PropertyTypeUtil.isHDB(item.cdResearchSubType)
      );

      transactionsPrivateResultPOs = listingPOs.filter(item =>
        PropertyTypeUtil.isPrivate(item.cdResearchSubType)
      );

      transactionsResultPOs =
        propertyType === propertyType_HDB
          ? transactionsHDBResultPOs
          : transactionsPrivateResultPOs;
    }
    return <AgentTransactionsResultList listingPOs={transactionsResultPOs} />;
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
        {this.renderAgentCvProfile()}
        {this.renderNoOfTransactedListingAndResultList()}
      </SafeAreaView>
    );
  }
}

const Styles = {
  noOfListingPropertiesContainer: {
    flexDirection: "row",
    paddingHorizontal: Spacing.M,
    minHeight: 45,
    backgroundColor: SRXColor.White,
    borderBottomWidth: 1,
    borderBottomColor: "#e0e0e0",
    alignItems: "center"
  }
};

export { AgentTransactionsResult };
