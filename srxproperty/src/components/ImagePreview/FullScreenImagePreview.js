import React, {Component} from 'react';
import {
  Image,
  FlatList,
  View,
  Dimensions,
  Animated,
  Platform,
  TouchableHighlight,
  TouchableWithoutFeedback,
  SafeAreaView,
  Easing,
} from 'react-native';
import {Navigation} from 'react-native-navigation';
import {WebView} from 'react-native-webview';
import PropTypes from 'prop-types';
import {MediaItem} from './MediaItem';
import {FeatherIcon, ImageZoom} from '../../components';
import {
  ListingPhotoPO,
  VirtualTourPO,
  DroneViewPO,
  YoutubePO,
  AgentPO,
  ListingPO,
} from '../../dataObject';
import {ObjectUtil} from '../../utils';
import {SRXColor} from '../../constants';
import {ImagePreviewSource} from './ImagePreviewSource';
import {CallToActionBar} from '../../screens/Listings/ListingDetails/DetailsComponent';
import {Provider} from 'react-redux';
import {store} from '../../store';

var {height, width} = Dimensions.get('window');
const isIOS = Platform.OS === 'ios';
var parse = require('url-parse');

class Preview extends Component {
  static propTypes = {
    mediaItems: PropTypes.arrayOf(MediaItem),
    startIndex: PropTypes.number,
    shouldShowThumbnails: PropTypes.bool,
    /**
     * returning an object containing last viewed index
     * { index: X }
     */
    onDismiss: PropTypes.func,

    /**
     * Add call to action bar in Full Screen Image Preview
     *
     */
    imagePreviewSource: PropTypes.string,
    listingPO: PropTypes.instanceOf(ListingPO),
    msgTemplate: PropTypes.string,
  };

  static defaultProps = {
    mediaItems: [],
    startIndex: 0,
    shouldShowThumbnails: true,
  };

  static options(passProps) {
    return {
      topBar: {
        title: {
          color: SRXColor.White,
        },
      },
    };
  }

  isFullScreen = false;

  constructor(props) {
    super(props);

    const {startIndex} = props;

    this.translateY = 60 + 4 * 2 + 50; //height of flatlist + padding vertical
    this.state = {
      contentItemSize: {width: width, height: height},
      thumbnailAnim: new Animated.Value(0),
      currentIndex: startIndex,
    };

    Navigation.events().bindComponent(this);

    this.viewabilityConfig = {viewAreaCoveragePercentThreshold: 50};

    this.renderItem = this.renderItem.bind(this);
    this.renderThumbnailItem = this.renderThumbnailItem.bind(this);
    this.onViewableItemChanged = this.onViewableItemChanged.bind(this);
  }

  componentDidMount() {
    this.addLeftButtons();
    // this.addRightButtons();
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'fullScreenImage_close') {
      this.hide();
    }
  }

  addLeftButtons() {
    const componentId = this.props.componentId;

    FeatherIcon.getImageSource('x', 24, 'white').then(closeIcon => {
      Navigation.mergeOptions(componentId, {
        topBar: {
          leftButtons: [
            {
              id: 'fullScreenImage_close',
              icon: closeIcon,
              color: 'white',
            },
          ],
        },
      });
    });
  }

  addRightButtons() {
    const componentId = this.props.componentId;

    FeatherIcon.getImageSource(isIOS ? 'share' : 'share-2', 24, 'white').then(
      shareIcon => {
        Navigation.mergeOptions(componentId, {
          topBar: {
            rightButtons: [
              {
                id: 'fullScreenImage_share',
                icon: shareIcon,
                color: 'white',
              },
            ],
          },
        });
      },
    );
  }

  onViewableItemChanged({viewableItems}) {
    if (!ObjectUtil.isEmpty(viewableItems) && viewableItems.length == 1) {
      const firstItem = viewableItems[0];
      const {index, item} = firstItem;
      console.log(firstItem);

      this.setState({currentIndex: index});

      this.thumbnailList.scrollToIndex({
        animated: true,
        index,
        viewPosition: 0.5,
      });

      this.updateTopBarTitle(index);
    }
  }

  updateTopBarTitle(index) {
    const componentId = this.props.componentId;

    var title = index + 1 + ' of ' + this.props.mediaItems.length;

    Navigation.mergeOptions(componentId, {
      topBar: {
        title: {
          text: title,
        },
      },
    });
  }

  hide() {
    if (this.props.onDismiss)
      this.props.onDismiss({index: this.state.currentIndex});

    Navigation.dismissModal(this.props.componentId);
  }

  toggleFullScreen() {
    /*
    this.isFullScreen = !this.isFullScreen;
    this.navigationBarHidden(this.isFullScreen);
    if (this.isFullScreen) {
      this.hideThumbnails();
    } else {
      this.showThumbnails();
    }
    */
  }

  navigationBarHidden(hidden) {
    const componentId = this.props.componentId;

    Navigation.mergeOptions(componentId, {
      topBar: {
        visible: !hidden,
        animate: true,
        // drawBehind: true,
        // translucent: true
      },
    });
  }

  showThumbnails = () => {
    Animated.timing(this.state.thumbnailAnim, {
      toValue: 0,
      duration: 250,
      easing: Easing.out(Easing.ease),
    }).start();
  };

  hideThumbnails() {
    Animated.timing(this.state.thumbnailAnim, {
      toValue: this.translateY,
      duration: 100,
    }).start();
  }

  thumbnailSelected({item}) {
    const {mediaItems} = this.props;

    const index = mediaItems.indexOf(item);
    if (index >= 0) {
      this.imageList.scrollToIndex({index, animated: false});
    }
  }

  renderItem({item}) {
    if (item.isWebContent) {
      var regex = /^(?:https?:\/\/)?(?:m\.|www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
      let matchArray = item.url.match(regex);
      if (!ObjectUtil.isEmpty(matchArray) && matchArray.length > 1) {
        let id = matchArray[1];
        return (
          // <TouchableWithoutFeedback onPress={() => this.toggleFullScreen()}>

          <WebView
            source={{
              html:
                `<html><body>
          <!-- 1. The <iframe> (and video player) will replace this <div> tag. -->
          <div id="player"></div>
      
          <script>
            // 2. This code loads the IFrame Player API code asynchronously.
            var tag = document.createElement('script');
      
            tag.src = "https://www.youtube.com/iframe_api";
            var firstScriptTag = document.getElementsByTagName('script')[0];
            firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
      
            // 3. This function creates an <iframe> (and YouTube player)
            //    after the API code downloads.
            var player;
            function onYouTubeIframeAPIReady() {
              player = new YT.Player('player', {
                height: '100%',
                width: '100%',
                videoId: '` +
                id +
                `',
                playerVars: { 'autoplay': 1, 'controls': 1, 'loop': 1, 'modestbranding': 1, 'autohide':0, 'showinfo':0 },
                events: {
                  'onReady': onPlayerReady,
                  'onStateChange': onPlayerStateChange
                }
              });
            }
      
            // 4. The API will call this function when the video player is ready.
            function onPlayerReady(event) {
              event.target.setLoop(true);
              event.target.playVideo();
            }
      
            // 5. The API calls this function when the player's state changes.
            //    The function indicates that when playing a video (state=1),
            //    the player should play for six seconds and then stop.
            var done = false;
            function onPlayerStateChange(event) {
              if (event.data == YT.PlayerState.PLAYING && !done) {
                done = true;
              }else if(event.data == YT.PlayerState.ENDED) {
                event.target.playVideo();
              }
            }
            function stopVideo() {
              player.stopVideo();
            }
          </script>
        </body>
        </html>`,
            }}
            style={{width: width, height: '100%'}}
            cacheEnabled={true}
            allowsInlineMediaPlayback={true}
            mediaPlaybackRequiresUserAction={false}
          />
          // </TouchableWithoutFeedback>
        );
      } else {
        return (
          <WebView
            source={{uri: item.url}}
            style={{width: width, height: '100%'}}
            cacheEnabled={true}
          />
        );
      }
    } else {
      const {contentItemSize} = this.state;
      if (isIOS) {
        return (
          // <TouchableWithoutFeedback onPress={() => this.toggleFullScreen()}>
          <ImageZoom
            cropWidth={contentItemSize.width}
            cropHeight={contentItemSize.height}
            imageWidth={contentItemSize.width}
            imageHeight={contentItemSize.height}>
            <Image
              source={{uri: item.url}}
              style={{width: width, flex: 1}}
              resizeMode={'contain'}
            />
          </ImageZoom>
          // </TouchableWithoutFeedback>
        );
      } else {
        return (
          <Image
            source={{uri: item.url}}
            style={contentItemSize}
            resizeMode={'contain'}
          />
        );
      }
    }
  }

  renderMainContent() {
    const {mediaItems, startIndex} = this.props;
    const {currentIndex} = this.state;
    let scrollable = true;

    /**
     * If it is android,
     * While showing drone view (except youtube video of drone view) & v360
     * disable the scrolling of flatlist
     */
    if (!isIOS) {
      if (!ObjectUtil.isEmpty(mediaItems) && currentIndex < mediaItems.length) {
        const currentItem = mediaItems[currentIndex];
        if (
          (currentItem instanceof DroneViewPO && currentItem.type !== 3) ||
          currentItem instanceof VirtualTourPO
        ) {
          scrollable = false;
        }
      }
    }

    return (
      <FlatList
        ref={list => (this.imageList = list)}
        style={[
          {
            flex: 1,
          },
        ]}
        horizontal={true}
        pagingEnabled={true}
        data={mediaItems}
        maxToRenderPerBatch={3}
        keyExtractor={(item, index) => 'main_' + index + item}
        renderItem={this.renderItem}
        scrollEnabled={scrollable}
        initialScrollIndex={startIndex}
        onViewableItemsChanged={this.onViewableItemChanged}
        viewabilityConfig={this.viewabilityConfig}
        getItemLayout={(data, index) => ({
          length: width,
          offset: width * index,
          index,
        })}
        onScrollToIndexFailed={err => {
          console.log(err);
        }}
        onLayout={({
          nativeEvent: {
            layout: {x, y, width, height},
          },
        }) => this.setState({contentItemSize: {width, height}})}
      />
    );
  }

  renderThumbnailItem({item, index}) {
    const {shouldShowThumbnails} = this.props;
    if (shouldShowThumbnails) {
      return (
        <TouchableHighlight
          style={
            index == this.state.currentIndex
              ? {borderColor: SRXColor.Teal, borderWidth: 1}
              : null
          }
          onPress={() => this.thumbnailSelected({item})}>
          <View style={{justifyContent: 'center', alignItems: 'center'}}>
            <Image
              source={{uri: item.thumbnailUrl}}
              style={{width: 60, height: 60}}
              resizeMode={'cover'}
            />
            {item.getOverlayIcon ? (
              <Image
                source={item.getOverlayIcon()}
                style={{height: 37, width: 37, position: 'absolute'}}
              />
            ) : null}
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderThumbnailList() {
    const {mediaItems, startIndex} = this.props;
    const {thumbnailAnim} = this.state;
    return (
      <SafeAreaView
        style={{
          // position: "absolute",
          backgroundColor: 'black',
          paddingVertical: 4,
        }}
        onLayout={({
          nativeEvent: {
            layout: {x, y, width, height},
          },
        }) => (this.translateY = height)}>
        <Animated.View
          style={{
            transform: [{translateY: thumbnailAnim}],
          }}>
          <FlatList
            ref={list => (this.thumbnailList = list)}
            horizontal={true}
            data={mediaItems}
            initialScrollIndex={startIndex}
            keyExtractor={(item, index) => 'thumbnail_' + index + item}
            renderItem={this.renderThumbnailItem}
            ItemSeparatorComponent={() => (
              <View style={{width: 4, backgroundColor: 'transparent'}} />
            )}
            getItemLayout={(data, index) => ({
              length: 60,
              offset: 60 * index + (index > 0 ? 4 * (index - 1) : 0),
              index,
            })}
          />
        </Animated.View>
      </SafeAreaView>
    );
  }

  renderCallActions() {
    const {imagePreviewSource, listingPO} = this.props;
    if (imagePreviewSource == ImagePreviewSource.ListingDetails) {
      return (
        <Provider store={store}>
          <CallToActionBar
            componentId={this.props.componentId}
            listingPO={listingPO}
          />
        </Provider>
      );
    }
  }

  render() {
    /*
     * SafeAreaView is not used to allow the image to zoom to the edge
     */
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: 'rgba(0, 0, 0, 1.0)',
          justifyContent: 'flex-end',
        }}>
        {this.renderMainContent()}
        {this.renderThumbnailList()}
        {this.renderCallActions()}
      </View>
    );
  }
}

Navigation.registerComponent('ImagePreview.fullScreen', () => Preview);

const ImagePreviewStack = passProps => {
  return {
    stack: {
      id: 'ImagePreviewStack',
      children: [
        {
          component: {
            name: 'ImagePreview.fullScreen',
            passProps,
            options: {
              modalPresentationStyle:
                Platform.OS === 'ios' ? 'overFullScreen' : 'overCurrentContext',
              // animations: {
              //     showModal: {
              //         enable: false
              //     },
              //     dismissModal: {
              //         enable: false
              //     }
              // },
              topBar: {
                background: {
                  color: 'black',
                  // translucent: true,
                  // blur: true,
                  // drawBehind: true
                },
              },
              layout: {
                backgroundColor: 'black',
              },
            },
          },
        },
      ],
    },
  };
};

const show = passProps => {
  Navigation.showModal(ImagePreviewStack(passProps));
};

const FullScreenImagePreview = {
  show,
};

export {FullScreenImagePreview};
