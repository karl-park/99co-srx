import React, { Component } from "react";
import {
  SafeAreaView,
  View,
  FlatList,
  StyleSheet,
  Platform,
  TextInput
} from "react-native";
import { Picker, FeatherIcon, SmallBodyText } from "../../../components";
import { SRXColor } from "../../../constants";
import { Typography, Spacing, InputStyles } from "../../../styles";
import {
  CategoryTypeValue,
  CategoryTypeDescription,
  CATEGORY_TYPE_ARRAY
} from "../Constants";
import { ObjectUtil } from "../../../utils";
import { NewsSearchManager } from "../Manager";
import { NewsListItem1, NewsListItem2 } from "../NewsItems";
import { LoadMoreIndicator } from "../../../components";
import { Navigation } from "react-native-navigation";

const isIOS = Platform.OS === "ios";

class PropertyNewsHomeScreen extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: "Property News"
        }
      }
    };
  }

  state = {
    isLoading: true,
    categoryType: CategoryTypeValue.all,
    categoryTypeDescription: CategoryTypeDescription.all
  };

  newsSearchOption = { defaultCategory: "" };

  componentDidMount() {
    this.onCallSearchManager(this.newsSearchOption);
  }

  onCallSearchManager(searchOptions) {
    this.searchManager = new NewsSearchManager(searchOptions);
    this.searchManager.register(this.onNewsListLoaded.bind(this));
    this.searchManager.search();
  }

  loadMore = () => {
    if (this.searchManager) {
      this.searchManager.loadMore();
    }
  };

  onNewsListLoaded({ allResults, allNewsList, error, manager }) {
    if (manager === this.searchManager) {
      this.setState({ newsList: allResults, isLoading: false });
    }
  }

  //Category Type
  onSelectCategoryType = item => {
    const { isLoading, categoryType } = this.state;
    this.setState({ isLoading: true });
    if (!ObjectUtil.isEmpty(item)) {
      this.setState({
        categoryType: item.value,
        categoryTypeDescription: item.key
      });
      const newNewsSearchOption = {
        ...this.newsSearchOption,
        defaultCategory: item.value
      };
      this.newsSearchOption = newNewsSearchOption;
      this.onCallSearchManager(newNewsSearchOption);
    }
  };

  //Search by keyword
  searchBarUpdated = ({ text }) => {
    if (
      (!ObjectUtil.isEmpty(text) && text.length > 2) ||
      ObjectUtil.isEmpty(text)
    ) {
      this.searchNewsList(text);
    }
    this.setState({ searchQuery: text });
  };

  searchNewsList(text) {
    const newNewsSearchOption = {
      ...this.newsSearchOption,
      searchText: text
    };

    this.setState({ searchQuery: text, isLoading: true }, () => {
      this.newsSearchOption = newNewsSearchOption;
      this.onCallSearchManager(newNewsSearchOption);
    });
  }

  //go to news details
  showNewsDetails = (
    onlineCommunicationPO,
    indexNo,
    newsSearchOption,
    categoryTypeDescription
  ) => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertyNewsStack.NewsDetailsResultList",
        passProps: {
          onlineCommunicationPO: onlineCommunicationPO,
          indexNo: indexNo,
          newsSearchOption: newsSearchOption,
          screenTitle: categoryTypeDescription
        }
      }
    });
  };

  //Start Rendering methods
  renderSearchBar() {
    const { categoryType, searchQuery } = this.state;
    return (
      <View
        style={[styles.searchBarContainer, InputStyles.singleLineTextHeight]}
      >
        <Picker
          data={CATEGORY_TYPE_ARRAY}
          style={styles.categoryTypeContainer}
          inputStyle={[Typography.Body, { color: SRXColor.Gray }]}
          mode={"dialog"}
          prompt={"Select Category"}
          useCustomPicker={true}
          selectedValue={categoryType}
          titleOfItem={(item, index) => item.key}
          valueOfItem={(item, index) => item.value}
          onSubmit={item => this.onSelectCategoryType(item)}
          renderRightAccessory={this.renderDropDownIcon}
        />
        <TextInput
          ref={component => (this.searchBar = component)}
          style={styles.searchTextContainer}
          autoCorrect={false}
          autoFocus={false}
          clearButtonMode="while-editing"
          underlineColorAndroid="transparent"
          returnKeyType={"search"}
          value={searchQuery}
          onChangeText={text =>
            this.searchBarUpdated({
              text
            })
          }
        />
      </View>
    );
  }

  renderDropDownIcon() {
    return (
      <FeatherIcon
        name="chevron-down"
        size={24}
        style={{ alignSelf: "center", marginLeft: Spacing.XS }}
      />
    );
  }

  renderPropertyNewsResultList() {
    const { isLoading, newsList } = this.state;
    if (isLoading) {
      return (
        <View style={styles.container}>
          <LoadMoreIndicator />
        </View>
      );
    } else {
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
      } else {
        return (
          <View style={styles.container}>
            <SmallBodyText style={{ color: SRXColor.Gray }}>
              No record found
            </SmallBodyText>
          </View>
        );
      }
    }
  }

  renderPropertyNewsItem({ item, index }) {
    const { newsList, categoryTypeDescription } = this.state;
    if (index < 1) {
      // NewsListItem1 => news title, big url image, news source and date posted
      return (
        <NewsListItem1
          key={item.getNewsId()}
          onlineCommunicationPO={item}
          newsList={newsList}
          indexNo={index}
          newsSearchOption={this.newsSearchOption}
          categoryTypeDescription={categoryTypeDescription}
          showNewsDetails={this.showNewsDetails}
        />
      );
    } else {
      return (
        //NewsListItem2 => small url image, news title, short content, news source and date posted
        <NewsListItem2
          key={item.getNewsId()}
          containerStyle={{
            backgroundColor:
              index % 2 == 0 ? SRXColor.AccordionBackground : SRXColor.White
          }}
          onlineCommunicationPO={item}
          newsList={newsList}
          indexNo={index}
          newsSearchOption={this.newsSearchOption}
          categoryTypeDescription={categoryTypeDescription}
          showNewsDetails={this.showNewsDetails}
        />
      );
    }
  }

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
        {this.renderSearchBar()}
        {this.renderPropertyNewsResultList()}
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
    justifyContent: "center",
    flex: 1
  },
  searchBarContainer: {
    overflow: "hidden",
    flexDirection: "row",
    margin: Spacing.XS,
    borderWidth: 1,
    borderRadius: isIOS ? 20 : 22.5,
    borderColor: SRXColor.SmallBodyBackground
  },
  categoryTypeContainer: {
    flex: 1,
    borderRightWidth: 1,
    borderColor: SRXColor.SmallBodyBackground,
    backgroundColor: SRXColor.White,
    paddingHorizontal: Spacing.S
  },
  searchTextContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    paddingHorizontal: 8,
    color: SRXColor.Gray
  }
});

export { PropertyNewsHomeScreen };
