import React, {Component} from 'react';
import {
  View,
  ActivityIndicator,
  SectionList,
  Dimensions,
  RefreshControl,
} from 'react-native';
import PropTypes from 'prop-types';
import {
  LoadMoreIndicator,
  Separator,
  Heading2,
} from '../../../../../../components';
import {ObjectUtil} from '../../../../../../utils';
import {SRXColor} from '../../../../../../constants';
import {Spacing} from '../../../../../../styles';
import {TransactedListingGridItem} from '../../../../../Listings';
import {SearchFeaturedAgents} from '../../../components';
import {NoResultPlaceholder} from '../../../../../General';

var {width} = Dimensions.get('window');

const TransListingSearchResultGridViewSource = {
  MyPropertyDetails: 'MyPropertyDetails',
};

class TransactedListingSearchResultGridView extends Component {
  state = {
    selectedListings: [],
  };

  static defaultProps = {
    sections: [],
    isLoading: false,
    isLoadingMore: false,
    onEndReached: null,
  };

  constructor(props) {
    super(props);
    this.onListItemPressed = this.onListItemPressed.bind(this);
  }

  //direct to agent cv for transacted listing
  onListItemPressed = listingPO => {
    let agentPO = listingPO.getAgentPO();
    if (!ObjectUtil.isEmpty(agentPO)) {
      const {directToAgentCV} = this.props;
      if (directToAgentCV) {
        directToAgentCV(agentPO);
      }
    }
  };

  //direct to agent from featured agent
  onSelectFeaturedAgentItem = agentPO => {
    const {directToAgentCV} = this.props;
    if (directToAgentCV) {
      directToAgentCV(agentPO);
    }
  };

  onResetSearchOptions = () => {
    const {onResetSearchOptions} = this.props;
    if (onResetSearchOptions) {
      onResetSearchOptions();
    }
  };

  renderListEmptyComponent() {
    if (this.props.renderListEmptyComponent) {
      return this.props.renderListEmptyComponent();
    } else {
      return <NoResultPlaceholder />;
    }
  }

  renderResultList() {
    const {
      sections,
      onEndReached,
      contentContainerStyle,
      onMomentumScrollBegin,
      onMomentumScrollEnd,
      onScroll,
      onScrollEndDrag,
      onRefresh,
      refreshing,
      directorySource,
    } = this.props;
    if (!ObjectUtil.isEmpty(sections)) {
      return (
        <SectionList
          style={{flex: 1}}
          sections={sections}
          stickySectionHeadersEnabled={false}
          renderItem={({index, item, section}) =>
            this.renderItem({index, item, section})
          }
          renderSectionHeader={({section}) => this.renderHeader({section})}
          keyExtractor={(item, index) => item + index}
          onEndReached={onEndReached}
          ListFooterComponent={this.renderFooter}
          refreshControl={
            <RefreshControl
              refreshing={refreshing}
              onRefresh={onRefresh}
              progressViewOffset={90}
            />
          }
          contentInset={{
            top:
              directorySource ===
              TransListingSearchResultGridViewSource.MyPropertyDetails
                ? 0
                : 90,
          }}
          contentOffset={{
            x: 0,
            y:
              directorySource ===
              TransListingSearchResultGridViewSource.MyPropertyDetails
                ? 0
                : -90,
          }}
          //
          contentContainerStyle={contentContainerStyle}
          onMomentumScrollBegin={onMomentumScrollBegin}
          onMomentumScrollEnd={onMomentumScrollEnd}
          onScroll={onScroll}
          onScrollEndDrag={onScrollEndDrag}
          removeClippedSubviews={true}
        />
      );
    } else {
      return this.renderListEmptyComponent();
    }
  }

  renderHeader = ({section}) => {
    if (!ObjectUtil.isEmpty(section)) {
      if (!ObjectUtil.isEmpty(section.headerTitle) && section.data.length > 0) {
        return (
          <View style={{flex: 1, backgroundColor: SRXColor.White}}>
            <Heading2
              style={{
                paddingHorizontal: Spacing.M,
                marginVertical: Spacing.S,
              }}>
              {section.headerTitle}
            </Heading2>
            <Separator />
          </View>
        );
      }
    }
  };

  renderItem = ({item, index, section}) => {
    if (section.key === 'no_srxStp_searchTerm_Result') {
      /**Show no properties handler if there is no search result
       * for srxStpArray & mclpSearchTermArray */
      return (
        <View key={index}>
          {this.renderListEmptyComponent()}
          <Separator />
        </View>
      );
    } else if (!ObjectUtil.isEmpty(item.featuredAgents)) {
      //Show featured agents if data in transacted result gridview
      return (
        <SearchFeaturedAgents
          key={index}
          data={item.featuredAgents}
          title={item.featuredAgentsTitle}
          onSelectFeaturedAgentItem={this.onSelectFeaturedAgentItem}
        />
      );
    } else if (Array.isArray(item)) {
      /**Change items to array because of gridview &
       * Show listing item view
       */
      return this.renderListingItem({item, index, section});
    } else {
      //Show no views for else condition
      return <View />;
    }
  };

  renderVerticalGridLineSeparator() {
    return <View style={{width: 1, backgroundColor: '#e0e0e0'}} />;
  }

  renderListingItem = ({item, index, section}) => {
    return (
      <View style={{flex: 1}}>
        <View style={{flexDirection: 'row'}}>
          {/* divide screen width by 2 to show two listings per rows*/}
          {item.map((item, i) => (
            <View style={{flexDirection: 'row'}}>
              <View
                key={index + i}
                style={{
                  justifyContent: 'center',
                  width: Math.floor(width / 2),
                }}>
                <TransactedListingGridItem
                  key={index}
                  containerStyle={{
                    backgroundColor:
                      index % 2 == 0
                        ? SRXColor.White
                        : SRXColor.AccordionBackground,
                  }}
                  listingPO={item}
                  onPress={() => this.onListItemPressed(item)}
                />
              </View>
              {this.renderVerticalGridLineSeparator()}
            </View>
          ))}
        </View>
        <Separator />
      </View>
    );
  };

  renderFooter = () => {
    const {isLoadingMore} = this.props;
    if (isLoadingMore) {
      return (
        <View style={{backgroundColor: SRXColor.White}}>
          <LoadMoreIndicator />
        </View>
      );
    }
    return null;
  };

  render() {
    const {isLoading} = this.props;
    if (isLoading) {
      return (
        <View
          style={{
            flex: 1,
            alignItems: 'center',
            justifyContent: 'center',
            backgroundColor: SRXColor.White,
          }}>
          <ActivityIndicator />
        </View>
      );
    } else {
      return <View style={{flex: 1}}>{this.renderResultList()}</View>;
    }
  }
}

TransactedListingSearchResultGridView.Sources = TransListingSearchResultGridViewSource;

TransactedListingSearchResultGridView.propTypes = {
  /**
   * array of objects containing section data
   * {
   *   data: [ class object listingPO ],
   *   key : string represent this section,
   *   headerTitle: title of section, optional, if you want to show title for the section
   * }
   */
  sections: PropTypes.array.isRequired,
  isLoading: PropTypes.bool,
  isLoadingMore: PropTypes.bool,
  directorySource: PropTypes.string,
  onEndReached: PropTypes.func,

  /** direct to agent cv from featured agent item and listing grid item */
  directToAgentCV: PropTypes.func,

  /**
   * Rendered when the list is empty. Can be a React Component Class, a render function
   * Note: This item is not added directly to the section list props "ListEmptyComponent"
   */
  renderListEmptyComponent: PropTypes.func,
};

export {TransactedListingSearchResultGridView};
