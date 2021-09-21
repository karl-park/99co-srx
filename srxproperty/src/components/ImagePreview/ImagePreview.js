import React, {Component} from 'react';
import {
  Image,
  FlatList,
  View,
  Dimensions,
  TouchableWithoutFeedback,
} from 'react-native';
import PropTypes from 'prop-types';
import {WebView} from 'react-native-webview';
import {TopBarConstants} from '../../styles';
import {ObjectUtil} from '../../utils';
import {MediaItem} from './MediaItem';
import {FullScreenImagePreview} from './FullScreenImagePreview';
import {ListingPO} from '../../dataObject';

var {height, width} = Dimensions.get('window');

const itemSize = {
  width: width,
  height:
    (width / 375) *
    (285 + TopBarConstants.statusBarHeight + TopBarConstants.topBarHeight),
};

class ImagePreview extends Component {
  static propTypes = {
    mediaItems: PropTypes.arrayOf(MediaItem),
    /**
     * Trigger when displaying fullscreen
     */
    onDisplayFullScreen: PropTypes.func,

    /**
     * Image preview source
     */
    imagePreviewSource: PropTypes.string,
    /**
     * To pass mobile number to call, whatsApp and srx chat
     */
    listingPO: PropTypes.instanceOf(ListingPO),
  };

  static defaultProps = {
    mediaItems: [],
  };

  constructor(props) {
    super(props);

    this.currentIndex = 0;

    this.onFullScreenDismiss = this.onFullScreenDismiss.bind(this);
    this.onViewableItemChanged = this.onViewableItemChanged.bind(this);
    this.renderItem = this.renderItem.bind(this);

    this.viewabilityConfig = {
      viewAreaCoveragePercentThreshold: 50,
    };
  }

  onFullScreenDismiss({index}) {
    if (index >= 0 && index < this.props.mediaItems.length) {
      this.imageList.scrollToIndex({index, animated: false});
    }
  }

  onViewableItemChanged({viewableItems}) {
    if (!ObjectUtil.isEmpty(viewableItems) && viewableItems.length == 1) {
      const firstItem = viewableItems[0];
      const {index} = firstItem;
      //temporary set the index here until require more work to be done
      this.currentIndex = index;
    }
  }

  renderItem({item}) {
    const {
      mediaItems,
      onDisplayFullScreen,
      imagePreviewSource,
      listingPO,
    } = this.props;
    if (item.isWebContent) {
      return (
        <TouchableWithoutFeedback
          onPress={() => {
            if (onDisplayFullScreen) {
              onDisplayFullScreen();
            }
            FullScreenImagePreview.show({
              mediaItems: mediaItems,
              startIndex: this.currentIndex,
              imagePreviewSource: imagePreviewSource,
              listingPO: listingPO,
              onDismiss: this.onFullScreenDismiss,
            });
          }}>
          <View>
            <WebView
              source={{uri: item.url}}
              style={itemSize}
              cacheEnabled={true}
              mediaPlaybackRequiresUserAction={false}
            />
            <View style={[itemSize, {position: 'absolute'}]} />
          </View>
        </TouchableWithoutFeedback>
      );
    }
    return (
      <TouchableWithoutFeedback
        onPress={() => {
          if (onDisplayFullScreen) {
            onDisplayFullScreen();
          }
          FullScreenImagePreview.show({
            mediaItems: mediaItems,
            startIndex: this.currentIndex,
            imagePreviewSource: imagePreviewSource,
            listingPO: listingPO,
            onDismiss: this.onFullScreenDismiss,
          });
        }}>
        <View>
          <Image
            source={{uri: item.url}}
            style={itemSize}
            resizeMode={'cover'}
          />
        </View>
      </TouchableWithoutFeedback>
    );
  }

  render() {
    console.log('this item height is ~~ ' + itemSize.height);
    const {mediaItems} = this.props;
    return (
      <View style={[{alignItems: 'flex-end'}, itemSize]}>
        <FlatList
          ref={list => (this.imageList = list)}
          style={[
            {
              backgroundColor: 'black',
            },
          ]}
          horizontal={true}
          pagingEnabled={true}
          data={mediaItems}
          maxToRenderPerBatch={3}
          keyExtractor={(item, index) => 'preview_' + index + item}
          renderItem={this.renderItem}
          onViewableItemsChanged={this.onViewableItemChanged}
          viewabilityConfig={this.viewabilityConfig}
          getItemLayout={(data, index) => ({
            length: width,
            offset: width * index,
            index,
          })}
        />
      </View>
    );
  }
}

export {ImagePreview};
