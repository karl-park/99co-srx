import React, { Component } from "react";
import {
  SafeAreaView,
  View,
  FlatList,
  StyleSheet,
  Platform
} from "react-native";
import { CategoryTypeValue } from "../Constants";
import { Navigation } from "react-native-navigation";
import { NewsSearchManager } from "../Manager";
import { NewsDetails } from "../NewsDetails";
import { LoadMoreIndicator, SmallBodyText } from "../../../components";
import { SRXColor } from "../../../constants";
import { ObjectUtil, NumberUtil } from "../../../utils";
import PropTypes from "prop-types";
import { OnlineCommunicationPO } from "../../../dataObject";

class NewsDetailsResultList extends Component {
  static propTypes = {
    indexNo: PropTypes.number,
    newsSearchOption: PropTypes.object,
    screenTitle: PropTypes.string
  };

  static defaultProps = {
    indexNo: 0,
    newsSearchOption: null,
    screenTitle: ""
  };

  static options(passProps) {
    return {
      topBar: {
        title: {
          text: passProps.screenTitle ? passProps.screenTitle : null
        }
      }
    };
  }

  state = {
    newsList: [],
    categoryType: CategoryTypeValue.all
  };

  componentDidMount() {
    const { newsSearchOption, onlineCommunicationPO, indexNo } = this.props;
    const pageNo = Math.floor(indexNo / 10) + 1;
    this.setState(
      { newsList: [...this.state.newsList, onlineCommunicationPO] },
      () => this.onCallSearchManager(newsSearchOption, pageNo)
    );
  }

  onCallSearchManager(searchOptions, pageNo) {
    this.searchManager = new NewsSearchManager(searchOptions);
    this.searchManager.register(this.onNewsListLoaded.bind(this));
    this.searchManager.search(pageNo);
  }

  loadMore = () => {
    if (this.searchManager) {
      this.searchManager.loadMore();
    }
  };

  onNewsListLoaded({ allResults, error, manager }) {
    const { indexNo } = this.props;
    const modulusOfIndexNo = indexNo % 10;
    const array = [];
    if (manager === this.searchManager) {
      if (!ObjectUtil.isEmpty(allResults)) {
        for (let i = modulusOfIndexNo; i < allResults.length; i++) {
          array.push(allResults[i]);
        }
      }

      this.setState({
        newsList: array
      });
    }
  }

  //Start Rendering methods
  renderPropertyNewsDetailsResultList() {
    const { isLoading, newsList } = this.state;
    if (Array.isArray(newsList) && newsList.length > 0) {
      return (
        <FlatList
          data={newsList}
          extraData={this.state}
          keyExtractor={item => item.getNewsId()}
          renderItem={({ item, index }) =>
            this.renderPropertyNewsItem({ item, index })
          }
          onEndReached={this.loadMore}
          ListFooterComponent={this.renderFooter}
        />
      );
    }
  }

  renderPropertyNewsItem({ item, index }) {
    return (
      <NewsDetails
        key={item.getNewsId()}
        onlineCommunicationPO={item}
        directWebScreen={this.directWebScreen}
      />
    );
  }

  directWebScreen = url => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.generalWebScreen",
        passProps: {
          url: url,
          screenTitle:url
        }
      }
    });
  };

  renderFooter = () => {
    if (this.searchManager) {
      if (this.searchManager.canLoadMore()) {
        return <LoadMoreIndicator />;
      }
    }
    return null;
  };

  render() {
    return (
      <SafeAreaView style={{ flex: 1 }}>
        {this.renderPropertyNewsDetailsResultList()}
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
    justifyContent: "center",
    flex: 1
  }
});

export { NewsDetailsResultList };
