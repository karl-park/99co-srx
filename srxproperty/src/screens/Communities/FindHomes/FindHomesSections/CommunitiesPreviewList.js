import React, {Component} from 'react';
import {View, Dimensions} from 'react-native';
import {Spacing} from '../../../../styles';
import {HorizontalFlatList} from '../../../../components';
import PageControl from 'react-native-page-control';
import {SRXColor} from '../../../../constants';
import {CommunitiesPreviewListItem} from '../../Feeds/ListItem';
import PropTypes from 'prop-types';

var {height, width} = Dimensions.get('window');

class CommunitiesPreviewList extends Component {
  static propTypes = {
    postSelected: PropTypes.func,
    onPressSeeAll: PropTypes.func,
  };

  constructor(props) {
    super(props);
    this.viewabilityConfig = {itemVisiblePercentThreshold: 70};
  }

  state = {
    currentIndex: 0,
  };

  postSelected = postId => {
    const {postSelected} = this.props;
    if (postSelected) {
      postSelected(postId);
    }
  };

  onPressSeeAll = () => {
    const {onPressSeeAll} = this.props;
    if (onPressSeeAll) {
      onPressSeeAll();
    }
  };

  onViewableItemsChanged = ({viewableItems, changed}) => {
    const {currentIndex} = this.state;
    var newIndex = currentIndex;
    if (viewableItems.length == 1) {
      const firstObject = viewableItems[0];
      newIndex = firstObject.index;
      this.setState({currentIndex: newIndex});
    }
  };

  renderItem = ({item, index}) => {
    return (
      <CommunitiesPreviewListItem
        communityFeedPO={item}
        postSelected={this.postSelected}
        onPressSeeAll={this.onPressSeeAll}
      />
    );
  };

  render() {
    const {communityFeedList} = this.props;
    const {currentIndex} = this.state;
    return (
      <View>
        <HorizontalFlatList
          data={communityFeedList}
          bounces={false}
          keyExtractor={item => item.id}
          indicatorStyle={'white'}
          onViewableItemsChanged={this.onViewableItemsChanged}
          getItemLayout={(data, index) => ({
            length: width,
            offset: width * index + (index > 0 ? 15 * (index - 1) : 0),
            index,
          })}
          viewabilityConfig={this.viewabilityConfig}
          renderItem={this.renderItem}
          extraData={this.state}
          pagingEnabled={true}
          scrollEnabled={true}
        />
        <PageControl
          style={{marginBottom: Spacing.XS / 2}}
          numberOfPages={communityFeedList.length}
          currentPage={currentIndex}
          pageIndicatorTintColor={SRXColor.Purple}
          currentPageIndicatorTintColor={SRXColor.Teal}
          indicatorStyle={{borderWidth: 1, borderColor: 'white'}}
        />
      </View>
    );
  }
}

export {CommunitiesPreviewList};
