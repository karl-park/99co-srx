import React, { Component } from "react";
import { TouchableHighlight, View, Image, StyleSheet } from "react-native";

import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";
import { Placeholder_Agent } from "../../../assets";
import { ExtraSmallBodyText, SmallBodyText } from "../../../components";
import { ObjectUtil, StringUtil } from "../../../utils";

class AgentCardItem2 extends Component {
  onSelectConciergeAgent() {
    const { onSelectAgentItem, agentPO } = this.props;
    if (onSelectAgentItem) {
      onSelectAgentItem(agentPO);
    }
  }

  //Image
  renderAgentImage() {
    const { agentPO } = this.props;
    var agentImageUrl = agentPO.getAgentPhoto();
    return (
      <Image
        style={styles.imageContainer}
        defaultSource={Placeholder_Agent}
        source={{ uri: agentImageUrl }}
        resizeMode={"cover"}
      />
    );
  }

  //Name
  renderAgentName() {
    const { agentPO, selectedAgentId } = this.props;
    return (
      <SmallBodyText
        style={[
          styles.agentName,
          {
            color:
              selectedAgentId === agentPO.id ? SRXColor.White : SRXColor.Black
          }
        ]}
        numberOfLines={1}
      >
        {agentPO.name}
      </SmallBodyText>
    );
  }

  //Number of Sales
  renderTransactionSummary() {
    const { agentPO, selectedAgentId, isSale } = this.props;
    const { transactionSummary } = this.props.agentPO;
    if (!ObjectUtil.isEmpty(transactionSummary)) {
      var totalTransaction = "";
      var highestPrice = "";

      if (isSale) {
        //FOR SALE
        if (transactionSummary.saleTotal) {
          totalTransaction = transactionSummary.saleTotal + " sales";
        }
        if (transactionSummary.saleHighest) {
          highestPrice =
            "Highest Sale : " +
            StringUtil.formatCurrency(transactionSummary.saleHighest);
        }
      } else {
        //FOR RENT
        if (transactionSummary.rentTotal) {
          totalTransaction = transactionSummary.rentTotal + " rents";
        }
        if (transactionSummary.rentHighest) {
          highestPrice =
            "Highest Rent : " +
            StringUtil.formatCurrency(transactionSummary.rentHighest);
        }
      }
      return (
        <View style={{ alignItems: "center", justifyContent: "center" }}>
          <ExtraSmallBodyText
            style={{
              color:
                selectedAgentId === agentPO.id ? SRXColor.White : SRXColor.Gray
            }}
            numberOfLines={1}
          >
            {totalTransaction}
          </ExtraSmallBodyText>
          <ExtraSmallBodyText
            style={{
              color:
                selectedAgentId === agentPO.id ? SRXColor.White : SRXColor.Gray
            }}
            numberOfLines={1}
          >
            {highestPrice}
          </ExtraSmallBodyText>
        </View>
      );
    }
  }

  //Agency Logo And CEA Number
  renderAgencyLogAndCEANumber() {
    const { agentPO, selectedAgentId } = this.props;

    var agencyImageURL = agentPO.getAgencyLogoURL();
    var ceaRegNo = "CEA " + agentPO.ceaRegNo;

    return (
      <View
        style={{
          flexDirection: "row",
          alignItems: "flex-end",
          position: "absolute",
          bottom: Spacing.XS
        }}
      >
        <View style={{ flex: 1 }}>
          <Image
            style={{ height: 35, width: 50 }}
            defaultSource={Placeholder_Agent}
            source={{ uri: agencyImageURL }}
            resizeMode={"contain"}
          />
        </View>
        <ExtraSmallBodyText
          style={{
            color:
              selectedAgentId === agentPO.id ? SRXColor.White : SRXColor.Gray
          }}
        >
          {ceaRegNo}
        </ExtraSmallBodyText>
      </View>
    );
  }

  render() {
    const { agentPO, selectedAgentId } = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      return (
        <TouchableHighlight onPress={() => this.onSelectConciergeAgent()}>
          <View style={{ backgroundColor: SRXColor.White }}>
            <View
              style={[
                styles.container,
                {
                  backgroundColor:
                    selectedAgentId === agentPO.id
                      ? SRXColor.Teal
                      : SRXColor.White
                }
              ]}
            >
              {this.renderAgentImage()}
              {this.renderAgentName()}
              {this.renderTransactionSummary()}
              {this.renderAgencyLogAndCEANumber()}
            </View>
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

const styles = StyleSheet.create({
  container: {
    width: 180,
    height: 205,
    alignItems: "center",
    borderWidth: 1,
    borderRadius: 5,
    borderColor: SRXColor.LightGray,
    padding: Spacing.XS,
    shadowColor: "rgba(110,129,154,0.32)",
    shadowRadius: 2,
    shadowOpacity: 0.8
  },
  imageContainer: {
    height: 75,
    width: 75,
    borderRadius: 75 / 2,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    backgroundColor: SRXColor.White
  },
  agentName: {
    fontWeight: "600",
    lineHeight: 22,
    textAlign: "center",
    marginTop: Spacing.XS / 2
  }
});

export { AgentCardItem2 };
