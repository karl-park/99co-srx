import React, {Component} from 'react';
import {
  View,
  Image,
  AsyncStorage,
  TouchableOpacity,
  Dimensions,
  Text,
} from 'react-native';
import PropTypes from 'prop-types';
import HTML from 'react-native-render-html';
import {WebView} from 'react-native-webview';
import {
  SmallBodyText,
  ExtraSmallBodyText,
  FeatherIcon,
  LinearGradient,
} from '../../../../../components';
import {IS_IOS, SRXColor} from '../../../../../constants';
import {CommunityPostPO, YoutubePO} from '../../../../../dataObject';
import {Spacing, Typography} from '../../../../../styles';
import {
  ObjectUtil,
  CommonUtil,
  StringUtil,
  YoutubeUtil,
} from '../../../../../utils';
import {CommunitiesConstant} from '../../../Constants';
import {CommunitiesPostPhotos} from '../../../PostDetails/DetailsContent/CommunitiesPostPhotos';

var {width} = Dimensions.get('window');

class CommunitiesPostItemContent extends Component {
  static defaultProps = {
    showDetails: true,
  };

  state = {
    aspectRatio: 16 / 9,
  };

  constructor(props) {
    super(props);

    this.updateImageViewHeight = this.updateImageViewHeight.bind(this);
  }

  /**
   * Save image size in AsyncStorage, might be moving to another utility files if required in the future
   *
   * If can find the size, use set the image view with the size
   * else load the image to get the size, then save AsyncStorage
   *
   * The key is the url
   *
   * saved item is
   * {
   *   size: {
   *     width,
   *     height
   *   }
   * }
   */

  componentDidMount() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.media)) {
      const firstMedia = post.media[0];
      const imageUrl = CommonUtil.handleImageUrl(firstMedia.url);
      AsyncStorage.getItem(imageUrl, (err, result) => {
        if (result) {
          let parseItem = JSON.parse(result);
          const {size} = parseItem;
          if (size) {
            this.updateImageViewHeight({
              width: size.width ?? 1,
              height: size.height ?? 1,
            });
            return;
          }
        }

        Image.getSize(imageUrl, (width, height) => {
          const imageItem = {
            size: {
              width,
              height,
            },
          };
          AsyncStorage.setItem(imageUrl, JSON.stringify(imageItem), err => {
            if (err) console.log(err);
          });

          this.updateImageViewHeight({width, height});
        });
      });
    }
  }

  updateImageViewHeight({width, height}) {
    const imageAspectRatio = height / width;
    if (imageAspectRatio > 1) {
      this.setState({aspectRatio: 1});
    } else if (imageAspectRatio < 9 / 16) {
      this.setState({aspectRatio: 16 / 9});
    } else {
      this.setState({aspectRatio: 1 / imageAspectRatio});
    }
  }

  renderPostImage() {
    const {post} = this.props;
    const {aspectRatio} = this.state;
    if (post.externalUrl !== '') {
      var youTubePO = YoutubeUtil.getValidatedYoutubeLink(post.externalUrl);
      if (!ObjectUtil.isEmpty(youTubePO)) {
        return this.renderYoutube(youTubePO);
      }
    }

    //if no youtube then show images
    if (!ObjectUtil.isEmpty(post.media)) {
      const firstMedia = post.media[0];
      if (firstMedia.type === CommunitiesConstant.mediaType.photo) {
        //Note: sometimes, aspectRatio is NaN and causing crashing issue
        return (
          <Image
            style={{
              backgroundColor: SRXColor.LightGray,
              aspectRatio: isNaN(aspectRatio) ? 16 / 9 : aspectRatio,
            }}
            source={{uri: CommonUtil.handleImageUrl(firstMedia.url)}}
          />
        );
      }
    }
  }

  renderYoutube(youtubeItem) {
    var regex = /^(?:https?:\/\/)?(?:m\.|www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
    let matchArray = youtubeItem.url.match(regex);
    if (!ObjectUtil.isEmpty(matchArray) && matchArray.length > 1) {
      let id = matchArray[1];
      return (
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
          style={{height: 200}}
          cacheEnabled={true}
          allowsInlineMediaPlayback={true}
          mediaPlaybackRequiresUserAction={true}
          scrollEnabled={false}
        />
      );
    }
  }

  renderPostTitleAndDetail() {
    const {contentStyles} = this.props;
    return (
      <View style={[contentStyles]}>
        {this.renderPostTitle()}
        {this.renderPostDetail()}
        {this.renderPostUrl()}
      </View>
    );
  }

  renderPostTitle() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.title)) {
      return (
        <SmallBodyText style={{marginTop: Spacing.XS}} numberOfLines={2}>
          {post.title}
        </SmallBodyText>
      );
    }
  }

  renderPostDetail() {
    const {post, showDetails} = this.props;
    if (showDetails && !ObjectUtil.isEmpty(post.content)) {
      var ignoredTags = [];
      if (!ObjectUtil.isEmpty(post.media)) {
        ignoredTags = ['iframe', 'img'];
      }
      var isHTML = StringUtil.isValidHTMLTag(post.content);
      var content = '';
      if (isHTML) {
        
        content = `<p>${post.content}</p>`;
        return (
          <View style={{marginVertical: Spacing.XS}}>
            <HTML
              containerStyle={{maxHeight: 100, overflow: 'hidden'}}
              imagesMaxWidth={width - 32}
              baseFontStyle={{
                ...Typography.ExtraSmallBody,
                color: SRXColor.Gray,
              }}
              imagesInitialDimensions={{
                width: width,
              }}
              staticContentMaxWidth={width}
              ignoredTags={ignoredTags}
              html={content}
              renderers={
                isHTML
                  ? null
                  : {
                      p: (_, children) => (
                        <Text numberOfLines={2}>{children}</Text>
                      ),
                    }
              }
            />
            <LinearGradient
              style={{
                height: 100,
                top: 0,
                width: '100%',
                position: 'absolute',
                backgroundColor: 'transparent',
              }}
              colors={[
                'rgba(255, 255, 255, 0)',
                'rgba(255, 255, 255, 0)',
                'rgba(255, 255, 255, 0)',
                'rgba(255, 255, 255, 0.6)',
                SRXColor.White,
              ]}
            />
          </View>
        );
      } else {
        content = post.content;
        return (
          <ExtraSmallBodyText
            style={{marginVertical: Spacing.XS, color: SRXColor.Gray}}
            numberOfLines={2}>
            {post.content}
          </ExtraSmallBodyText>
        );
      }
    }
  }

  renderPostUrl() {
    const {post, onLinkPressed} = this.props;
    if (
      post.type === CommunitiesConstant.postType.normal &&
      !ObjectUtil.isEmpty(post.externalUrl)
    ) {
      return (
        <TouchableOpacity onPress={() => onLinkPressed(post.externalUrl)}>
          <View style={[Styles.postUrlContainer, {marginTop: Spacing.XS}]}>
            <FeatherIcon name="link-2" size={20} color={SRXColor.Black} />
            <SmallBodyText
              style={{
                marginLeft: Spacing.XS,
                marginRight: Spacing.XS,
                alignSelf: 'center',
                color: SRXColor.Gray,
              }}
              numberOfLines={1}>
              {post.externalUrl}
            </SmallBodyText>
          </View>
        </TouchableOpacity>
      );
    }
  }

  render() {
    return (
      <View style={{marginBottom: Spacing.XS}}>
        {this.renderPostImage()}
        {this.renderPostTitleAndDetail()}
      </View>
    );
  }
}

const Styles = {
  postUrlContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.S,
    paddingVertical: Spacing.XS,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    borderStyle: 'dashed',
    borderRadius: 4,
  },
};

CommunitiesPostItemContent.propTypes = {
  post: PropTypes.instanceOf(CommunityPostPO),

  /**
   * To indicate whether to show post.content
   * Basically added for News Feed as it doens't show the content
   *
   * Default is `true`
   */
  showDetails: PropTypes.bool,

  /**
   * Styles of the view contains title & details
   */
  contentStyles: PropTypes.object,

  onLinkPressed: PropTypes.func,
};

export {CommunitiesPostItemContent};
