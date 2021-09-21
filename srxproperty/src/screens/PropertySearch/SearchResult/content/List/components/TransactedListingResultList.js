import React, {Component} from 'react';
import {
  View,
  ActivityIndicator,
  SectionList,
  RefreshControl,
} from 'react-native';
import PropTypes from 'prop-types';
import {Navigation} from 'react-native-navigation';
import {
  LoadMoreIndicator,
  Separator,
  Heading2,
} from '../../../../../../components';
import {ListingPO} from '../../../../../../dataObject';
import {ObjectUtil} from '../../../../../../utils';
import {SRXColor} from '../../../../../../constants';
import {Spacing} from '../../../../../../styles';
import {TransactedListingListItem} from '../../../../../Listings';
import {SearchFeaturedAgents, NoSearchResult} from '../../../components';
import {NoResultPlaceholder} from '../../../../../General';

const TransListingResultListSource = {
  MyPropertyDetails: 'MyPropertyDetails',
};

class TransactedListingResultList extends Component {
  static options(passProps) {
    let title;
    if (!ObjectUtil.isEmpty(passProps)) {
      if (!ObjectUtil.isEmpty(passProps.title)) {
        title = passProps.title;
      }
    }
    return {
      topBar: {
        title: {
          text: title || '',
        },
      },
    };
  }

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
    onListItemPressed = this.onListItemPressed.bind(this);
  }

  directToAgentCV(agentPO) {
    if (!ObjectUtil.isEmpty(agentPO)) {
      const {directToAgentCV, componentId} = this.props;
      if (directToAgentCV) {
        directToAgentCV(agentPO);
      } else if (componentId) {
        Navigation.push(this.props.componentId, {
          component: {
            name: 'ConciergeStack.AgentCV',
            passProps: {
              agentUserId: agentPO.getAgentId(),
            },
          },
        });
      }
    }
  }

  //select featured agent item to direct to Agent CV
  onFeaturedAgentItemSelected = agentPO => {
    this.directToAgentCV(agentPO);
  };

  //select transacted list item to direct to Agent CV
  onListItemPressed = listingPO => {
    let agentPO = listingPO.getAgentPO();
    this.directToAgentCV(agentPO);
  };

  //start rendering method
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
      source,
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
              source === TransListingResultListSource.MyPropertyDetails
                ? 0
                : 90,
          }}
          contentOffset={{
            x: 0,
            y:
              source === TransListingResultListSource.MyPropertyDetails
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
    //only show transacted listing, no featured agent to be shown
    if (item instanceof ListingPO) {
      //Show listing item view if item is instance of ListingPO
      return this.renderListingItem({item, index, section});
    } else if (!ObjectUtil.isEmpty(item.featuredAgents)) {
      //Show featured agents if there is data in transacted listing search
      return (
        <SearchFeaturedAgents
          key={section.key}
          data={item.featuredAgents}
          title={item.featuredAgentsTitle}
          onSelectFeaturedAgentItem={this.onFeaturedAgentItemSelected}
        />
      );
    } else if (section.key === 'no_srxStp_searchTerm_Result') {
      /**Show no properties handler if no search result for
       * srxStpArray & mclpSearchTermArray in transacted listing search */
      return (
        <View key={index}>
          {this.renderListEmptyComponent()}
          <Separator />
        </View>
      );
    } else {
      //Show no views for else condition
      return <View />;
    }
  };

  renderListingItem = ({item, index, section}) => {
    return (
      <TransactedListingListItem
        key={index}
        containerStyle={{
          backgroundColor:
            index % 2 == 0 ? SRXColor.White : SRXColor.AccordionBackground,
        }}
        listingPO={item}
        onPress={() => this.onListItemPressed(item)}
      />
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

TransactedListingResultList.Sources = TransListingResultListSource;

TransactedListingResultList.propTypes = {
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
  onEndReached: PropTypes.func,
  source: PropTypes.string,

  /**direct to agent cv */
  directToAgentCV: PropTypes.func,

  /**
   * Rendered when the list is empty. Can be a React Component Class, a render function
   * Note: This item is not added directly to the section list props "ListEmptyComponent"
   */
  renderListEmptyComponent: PropTypes.func,
};

export {TransactedListingResultList};
