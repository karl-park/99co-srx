import React, { Component } from "react";
import { View, StyleSheet, ActivityIndicator } from "react-native";
import PropTypes from "prop-types";

import { SRXColor } from "../../../constants";
import {
  HorizontalFlatList,
  BodyText,
  SmallBodyText
} from "../../../components";
import { AgentCardItem2 } from "../AgentItems";
import { AgentSearchService } from "../../../services";
import { ObjectUtil, CommonUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { AgentPO } from "../../../dataObject";
import { Sale_Options } from "../Constants";

const LoadingState = {
  normal: "normal",
  loading: "loading",
  loaded: "loaded"
};

class ConciergeAgentList extends Component {
  static propTypes = {
    conciergeFormInfo: PropTypes.object
  };

  static defaultProps = {
    conciergeFormInfo: null
  };

  state = {
    agents: [],
    loadingState: LoadingState.loading
  };

  componentDidMount() {
    this.loadConciergeAgents();
  }

  componentDidUpdate(prevProps) {
    if (prevProps.conciergeFormInfo !== this.props.conciergeFormInfo) {
      const prevConciergeFormInfo = prevProps.conciergeFormInfo;
      const { conciergeFormInfo, onSelectOwnAgent } = this.props;
      let callAPI = false;

      if (
        prevConciergeFormInfo.propertyType !== conciergeFormInfo.propertyType ||
        prevConciergeFormInfo.userType !== conciergeFormInfo.userType ||
        prevConciergeFormInfo.districtTownId !==
          conciergeFormInfo.districtTownId ||
        prevConciergeFormInfo.locationType !== conciergeFormInfo.locationType ||
        prevConciergeFormInfo.keywords !== conciergeFormInfo.keywords
      ) {
        callAPI = true;
      }

      if (callAPI) {
        this.setState(
          { loadingState: LoadingState.loading, agents: [] },
          () => {
            this.loadConciergeAgents();
          }
        );
      }
    }
  }

  //Call apis
  loadConciergeAgents() {
    const {
      propertyType,
      userType,
      districtTownId,
      locationType,
      keywords
    } = this.props.conciergeFormInfo;

    AgentSearchService.loadConciergeAgents({
      propertyType,
      userType,
      districtId: districtTownId,
      townId: "",
      locationType,
      location: keywords
    })
      .catch(error => {
        //back to normal state
        this.setState({ loadingState: LoadingState.normal });
        console.log(error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            //back to normal state
            this.setState({ loadingState: LoadingState.normal });
            console.log(error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const { result } = response;
              if (!ObjectUtil.isEmpty(result)) {
                const tempAgentList = [];
                result.map(item => {
                  tempAgentList.push(new AgentPO(item));
                });
                this.setState({ agents: tempAgentList });
              }
            }
            //loaded state
            this.setState({ loadingState: LoadingState.loaded });
          }
        } else {
          //back to normal state
          this.setState({ loadingState: LoadingState.normal });
          console.log(error);
        }
      });
  }

  //Select Agent PO
  onSelectAgentItem = agentPO => {
    const { conciergeFormInfo, onSelectOwnAgent, errorMessage } = this.props;

    const newConciergeFormInfo = {
      ...conciergeFormInfo,
      agentUserId: agentPO.id
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgSelectedAgent: ObjectUtil.isEmpty(
        newConciergeFormInfo.agentUserId.toString()
      )
        ? "Please choose one agent"
        : ""
    };

    if (onSelectOwnAgent) {
      onSelectOwnAgent(newConciergeFormInfo, newErrorMessage);
    }
  };

  getMatchedAgentListLayoutPosition = layout => {
    const { y } = layout;
    const { viewsPosition, getViewsPosition } = this.props;
    const newPosition = {
      ...viewsPosition,
      matchedAgentErrMsgY: y
    };

    if (getViewsPosition) {
      getViewsPosition(newPosition);
    }
  };

  //Start Rendering methods
  renderHeader() {
    return (
      <View style={{ marginHorizontal: Spacing.M }}>
        <BodyText style={{ fontWeight: "600", lineHeight: 24 }}>
          Your matched agents
        </BodyText>
        <SmallBodyText
          style={{
            lineHeight: 24,
            color: SRXColor.Gray,
            marginBottom: Spacing.S
          }}
        >
          Select your preferred agents to be connected
        </SmallBodyText>
      </View>
    );
  }

  //Agent lists
  renderConciergeAgentList() {
    const { agents, loadingState } = this.state;
    const { errMsgSelectedAgent } = this.props.errorMessage;

    //loaded or normal state
    if (loadingState !== LoadingState.loading) {
      if (Array.isArray(agents) && agents.length > 0) {
        return (
          <View>
            <HorizontalFlatList
              data={agents}
              showsHorizontalScrollIndicator={false}
              extraData={this.state}
              indicatorStyle={"white"}
              keyExtractor={item => item.toString()}
              renderItem={({ item, index }) =>
                this.renderConciergeAgentCardItem({ item, index })
              }
            />
            {errMsgSelectedAgent ? (
              <SmallBodyText
                style={[styles.errorMessage, { marginHorizontal: Spacing.M }]}
              >
                {errMsgSelectedAgent}
              </SmallBodyText>
            ) : null}
          </View>
        );
      } else {
        return (
          <View style={styles.indicatorContainer}>
            <SmallBodyText style={{ color: SRXColor.Gray }}>
              No agents found
            </SmallBodyText>
          </View>
        );
      }
    } else {
      //loading state
      return (
        <View style={styles.indicatorContainer}>
          <ActivityIndicator />
        </View>
      );
    }
  }

  //Render each agent item
  renderConciergeAgentCardItem({ item, index }) {
    const { agentUserId, userType } = this.props.conciergeFormInfo;
    const isSale = Sale_Options.has(userType) ? true : false;

    return (
      <AgentCardItem2
        key={index}
        agentPO={item}
        isSale={isSale}
        selectedAgentId={agentUserId}
        onSelectAgentItem={this.onSelectAgentItem.bind(this)}
      />
    );
  }

  render() {
    return (
      <View
        style={styles.container}
        onLayout={event => {
          this.getMatchedAgentListLayoutPosition(event.nativeEvent.layout);
        }}
      >
        {this.renderHeader()}
        {this.renderConciergeAgentList()}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: Spacing.L,
    backgroundColor: SRXColor.AccordionBackground,
    borderTopWidth: 1,
    borderBottomWidth: 1,
    borderTopColor: "#e0e0e0",
    borderBottomColor: "#e0e0e0",
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.XL
  },
  indicatorContainer: {
    height: 200,
    alignItems: "center",
    justifyContent: "center"
  },
  errorMessage: {
    marginTop: Spacing.XS,
    color: "#FF151F",
    lineHeight: 20
  }
});

export { ConciergeAgentList };
