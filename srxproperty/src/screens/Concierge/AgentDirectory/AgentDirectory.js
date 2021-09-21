import React, { Component } from "react";
import {
  StyleSheet,
  SafeAreaView,
  SectionList,
  View,
  Platform
} from "react-native";
import { Navigation } from "react-native-navigation";

import {
  Ionicons,
  Separator,
  LoadMoreIndicator,
  SmallBodyText
} from "../../../components";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { AgentListItem } from "../AgentItems";
import { AgentFilterOptions } from "../Components";
import { AgentSearchManager } from "../Manager";
import { SRXColor, LoadingState } from "../../../constants";

const isIOS = Platform.OS === "ios";

const DirectorySource = {
  ChatHome: "ChatHome",
  ConciergeHome: "ConciergeHome"
};

class AgentDirectory extends Component {
  //options for Agent Directory
  static options(passProps) {
    console.log(passProps);
    return {
      topBar: {
        title: {
          component: {
            id: "agentDirectoryTopBar",
            name: "AgentDirectory.TopBar"
          }
        },
        visible: true,
        animate: true,
        backButton: {
          visible: false
        },
        noBorder: true,
        elevation: 0 //for android
      },
      statusBar: {
        hideWithTopBar: false
      },
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true
      }
    };
  }

  constructor(props) {
    super(props);

    //added agent directory topbar
    this.updateAgentDirectoryTopBar();
  }

  state = {
    agentList: [],
    loadingState: LoadingState.Normal
  };

  agentSearchOption = { searchText: "" };

  componentDidMount() {
    //start calling API
    this.setState({ loadingState: LoadingState.Loading }, () => {
      this.onCallSearchManager(this.agentSearchOption);
    });
  }

  //Update Agent Directory top bar
  updateAgentDirectoryTopBar() {
    const { source, componentId } = this.props;
    Navigation.mergeOptions(componentId, {
      topBar: {
        title: {
          component: {
            id: "agentDirectoryTopBar",
            name: "AgentDirectory.TopBar",
            passProps: {
              searchBarUpdated: this.searchBarUpdated,
              onBackPressed: this.onPressBackBtn,
              source: source
            }
          }
        }
      }
    });
  }

  onCallSearchManager(searchOptions) {
    this.searchManager = new AgentSearchManager(searchOptions);
    this.searchManager.register(this.onAgentListLoaded.bind(this));
    this.searchManager.search();
  }

  loadMore = () => {
    if (this.searchManager) {
      this.searchManager.loadMore();
    }
  };

  onAgentListLoaded({
    newAgentList,
    featuredAgentList,
    allAgentList,
    error,
    totalAgentList,
    manager
  }) {
    if (manager === this.searchManager) {
      const sectionArray = [];
      if (!ObjectUtil.isEmpty(featuredAgentList)) {
        const newSection = {
          data: featuredAgentList,
          key: "FeaturedAgent"
        };
        sectionArray.push(newSection);
      }

      if (!ObjectUtil.isEmpty(allAgentList)) {
        const newSection = {
          data: allAgentList,
          key: "General"
        };
        sectionArray.push(newSection);
      }

      this.setState({
        agentList: sectionArray,
        loadingState: LoadingState.Loaded //loaded state
      });
    }
  }

  //agent directory top bar updated
  searchBarUpdated = ({ text }) => {
    //call API for empty text.if there is no data for parameters,
    //backend return agent lists which is same logic from loadInitialAgents
    if (
      (!ObjectUtil.isEmpty(text) && text.length > 2) ||
      ObjectUtil.isEmpty(text)
    ) {
      this.searchAgentList(text);
    }
    this.setState({ searchQuery: text });
  };

  searchAgentList(text) {
    const newAgentSearchOption = {
      ...this.agentSearchOption,
      searchText: text
    };

    this.setState(
      { searchQuery: text, loadingState: LoadingState.Loading },
      () => {
        this.agentSearchOption = newAgentSearchOption;
        this.onCallSearchManager(newAgentSearchOption);
      }
    );
  }

  //back button
  onPressBackBtn = () => {
    const { source, componentId } = this.props;
    if (source == DirectorySource.ChatHome) {
      Navigation.dismissModal(componentId);
    } else {
      Navigation.pop(componentId);
    }
  };

  //Search from agent filters
  searchAgentsByPropertyTypeAndArea = selectedOptions => {
    console.log("triggered- searchAgentsByPropertyTypeAndArea ");
    let newAgentSearchOption = null;

    this.setState({ loadingState: LoadingState.Loading }, () => {
      //In setState callback
      if (!ObjectUtil.isEmpty(selectedOptions)) {
        newAgentSearchOption = {
          ...this.agentSearchOption,
          ...selectedOptions
        };

        this.agentSearchOption = newAgentSearchOption;
        this.onCallSearchManager(newAgentSearchOption);
      } else {
        //Clear Filters
        newAgentSearchOption = {
          ...this.agentSearchOption,
          searchText: "",
          selectedDistrictIds: "",
          selectedHdbTownIds: "",
          selectedAreaSpecializations: ""
        };

        this.agentSearchOption = newAgentSearchOption;
        this.onCallSearchManager(newAgentSearchOption);
      }
    });
  };

  //direct to agent cv from Agent List Item
  viewAgentCv = agentInfo => {
    const { source } = this.props;
    if (source == DirectorySource.ChatHome) {
      // const { userPO } = this.props;
      // let backIcon;
      // if (isIOS) {
      //   backIcon = await Ionicons.getImageSource(
      //     "ios-arrow-back",
      //     20,
      //     SRXColor.Teal
      //   );
      // } else {
      //   backIcon = await Ionicons.getImageSource(
      //     "md-arrow-back",
      //     20,
      //     SRXColor.Teal
      //   );
      // }
      Navigation.push(this.props.componentId, {
        component: {
          name: "ChatStack.chatRoom",
          passProps: {
            agentInfo
            // userPO
          }
          // options: {
          //   topBar: {
          //     leftButtons: [
          //       {
          //         id: "backButton",
          //         icon: backIcon
          //       }
          //     ]
          //   },
          //   bottomTabs: { visible: false, drawBehind: true, animate: true }
          // }
        }
      });
    }

    if (source == DirectorySource.ConciergeHome) {
      Navigation.push(this.props.componentId, {
        component: {
          name: "ConciergeStack.AgentCV",
          passProps: {
            agentUserId: agentInfo.getAgentId()
          }
        }
      });
    }
  };

  //Start Rendering Methods
  renderFilter() {
    return (
      <AgentFilterOptions
        onSearch={this.searchAgentsByPropertyTypeAndArea}
        componentId={this.props.componentId}
      />
    );
  }

  renderItem = ({ index, item, section }) => {
    //change background color by section key
    var itemBackgroundColor =
      index % 2 == 0 ? SRXColor.AccordionBackground : SRXColor.White;
    if (!ObjectUtil.isEmpty(section)) {
      if (section.key === "FeaturedAgent") {
        itemBackgroundColor = SRXColor.PurpleShade;
      }
    }
    return (
      <View style={{ flex: 1 }} key={item.id}>
        <AgentListItem
          contentStyle={{
            backgroundColor: itemBackgroundColor,
            borderTopLeftRadius: index === 0 ? 10 : 0,
            borderTopRightRadius: index === 0 ? 10 : 0
          }}
          agentPO={item}
          viewAgentCv={this.viewAgentCv}
        />
        <Separator />
      </View>
    );
  };

  renderFooter = () => {
    if (this.searchManager) {
      if (this.searchManager.canLoadMore()) {
        return <LoadMoreIndicator />;
      }
    }
    return null;
  };

  renderAgentList() {
    //Show loadmoreindicator in Loading State
    //Show agent list in Loaded state
    //Show nothing in Normal state
    const { loadingState, agentList } = this.state;
    if (loadingState === LoadingState.Loading) {
      return (
        <View style={styles.container}>
          <LoadMoreIndicator />
        </View>
      );
    } else if (loadingState === LoadingState.Loaded) {
      if (Array.isArray(agentList) && agentList.length > 0) {
        return (
          <SectionList
            style={{ flex: 1 }}
            sections={agentList}
            stickySectionHeadersEnabled={false}
            renderItem={({ index, item, section }) =>
              this.renderItem({ index, item, section })
            }
            keyExtractor={(item, index) => item + index}
            onEndReached={this.loadMore}
            ListFooterComponent={this.renderFooter}
          />
        );
      } else {
        return (
          <View style={styles.container}>
            <SmallBodyText style={{ color: SRXColor.Gray }}>
              No agents found
            </SmallBodyText>
          </View>
        );
      }
    } else {
      return <View />;
    } //normal state
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1 }}>
        <View
          style={{ backgroundColor: SRXColor.SmallBodyBackground, flex: 1 }}
        >
          {this.renderFilter()}
          <View style={styles.agentListContainer}>
            {this.renderAgentList()}
          </View>
        </View>
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: Spacing.S,
    alignItems: "center",
    justifyContent: "center"
  },
  agentListContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    marginTop: Spacing.S,
    borderTopLeftRadius: 10,
    borderTopRightRadius: 10
  }
});

AgentDirectory.Sources = DirectorySource;

export default AgentDirectory;
