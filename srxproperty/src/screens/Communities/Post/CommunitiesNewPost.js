import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {
  View,
  SafeAreaView,
  ScrollView,
  TextInput,
  Image,
  TouchableOpacity,
  ActionSheetIOS,
} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import ImagePicker from 'react-native-image-crop-picker';
import {
  BodyText,
  Button,
  FeatherIcon,
  ExtraSmallBodyText,
  MultiImagePicker,
} from '../../../components';
import {Spacing} from '../../../styles';
import {SRXColor, IS_IOS} from '../../../constants';
import {CommonUtil, ObjectUtil, GoogleAnalyticUtil} from '../../../utils';
import {Navigation} from 'react-native-navigation';
import {CommunityPostPO} from '../../../dataObject';
import {CommunitiesService} from '../../../services';
import {CommunitiesStack} from '../../../config';
import {CommunitiesConstant} from '../Constants';
import {CommunitiesNewPostAction} from './CommunitiesNewPostAction';
import {FullScreenImagePreview} from '../../../components/ImagePreview/FullScreenImagePreview';

class CommunitiesNewPost extends Component {
  state = {
    selectedCommunity: CommunitiesConstant.singaporeCommunity,
    isLinkSelected: false,
    maxTitleChar: 70,
    maxDetailChar: 500,
    postTitle: '',
    postDetail: '',
    postLink: '',
    photos: [],

    initialEditCommunityPost: null,
  };

  static options(passProps) {
    return {
      topBar: {
        title: {
          text:
            !ObjectUtil.isEmpty(passProps.action) &&
            passProps.action == CommunitiesNewPostAction.EditPost
              ? 'Edit Post'
              : 'New Post',
        },
        visible: true,
        animate: true,
      },
    };
  }

  constructor(props) {
    super(props);

    if (
      !ObjectUtil.isEmpty(props) &&
      !ObjectUtil.isEmpty(props.initialCommunity)
    ) {
      this.state.selectedCommunity = props.initialCommunity;
    }

    if (!ObjectUtil.isEmpty(props) && !ObjectUtil.isEmpty(props.action)) {
      if (
        props.action === CommunitiesNewPostAction.EditPost &&
        !ObjectUtil.isEmpty(props.communityPostPO)
      ) {
        const {communityPostPO} = props;
        this.state.initialEditCommunityPost = communityPostPO;
        this.state.postTitle = communityPostPO.title;
        this.state.postDetail = communityPostPO.content;

        if (
          !ObjectUtil.isEmpty(communityPostPO.externalUrl) &&
          communityPostPO.externalUrl.length > 0
        ) {
          this.state.postLink = communityPostPO.externalUrl;
          this.state.isLinkSelected = true;
        }

        if (
          !ObjectUtil.isEmpty(communityPostPO.media) &&
          Array.isArray(communityPostPO.media) &&
          communityPostPO.media.length > 0
        ) {
          var tempPhotos = [];
          communityPostPO.media.map((item, index) => {
            tempPhotos.push({
              uri: item.url,
              type: item.mediaType,
              id: item.id, //to check photo is existing one or not
            });
          });
          this.state.photos = tempPhotos;
        }
      }
    }

    Navigation.events().bindComponent(this);

    this.setNavigationOptions();

    this.onLinkSelected = this.onLinkSelected.bind(this);
    this.chooseImage = this.chooseImage.bind(this);
    this.onChooseCommunity = this.onChooseCommunity.bind(this);
    this.onCommunitySelected = this.onCommunitySelected.bind(this);
    this.showImagePicker = this.showImagePicker.bind(this);
    this.showCamera = this.showCamera.bind(this);
  }

  componentDidUpdate(prevProps, prevState) {
    this.updatePostButton(prevState, this.state);
  }

  setNavigationOptions() {
    const {componentId, action} = this.props;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'blue')
        .then(icon_close => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              leftButtons: [
                {
                  id: 'btn_close',
                  icon: icon_close,
                },
              ],
              rightButtons: [
                {
                  id: 'btn_Post',
                  text:
                    !ObjectUtil.isEmpty(action) &&
                    action == CommunitiesNewPostAction.EditPost
                      ? 'Update'
                      : 'Post',
                  color: SRXColor.TextLink,
                  enabled: false,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    }); //end of promise
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_Post') {
      const {action} = this.props;
      if (action == CommunitiesNewPostAction.EditPost) {
        this.updatePost();
      } else if (action == CommunitiesNewPostAction.CreateNewPost) {
        this.post();
      }
    } else if (buttonId === 'btn_close') {
      Navigation.dismissModal('NewCommunitiesPost');
    }
  }

  updatePostButton(prevState, currentState) {
    const isTitleEmpty = ObjectUtil.isEmpty(currentState.postTitle);
    const isDetailEmpty = ObjectUtil.isEmpty(currentState.postDetail);
    const isLinkEmpty = ObjectUtil.isEmpty(currentState.postLink);
    const isPhotoEmpty = ObjectUtil.isEmpty(currentState.photos);

    const canPost = !(
      isTitleEmpty &&
      isDetailEmpty &&
      isLinkEmpty &&
      isPhotoEmpty
    );
    this.setPostButton(canPost, null);
  }

  setPostButton(enabled, title) {
    const {action} = this.props;
    var buttonText =
      !ObjectUtil.isEmpty(action) && action == CommunitiesNewPostAction.EditPost
        ? 'Update'
        : ' Post';
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        rightButtons: [
          {
            id: 'btn_Post',
            text: title ?? buttonText,
            color: SRXColor.TextLink,
            enabled: enabled,
          },
        ],
      },
    });
  }

  onChooseCommunity() {
    const {selectedCommunity} = this.state;
    // CommunitiesStack.showCommunitiesModal({
    //   title: 'Who can see your posts',
    //   selectedCommunity: selectedCommunity,
    //   onCommunitySelected: this.onCommunitySelected,
    // });
    CommunitiesStack.showCommunityOptions({
      selectedCommunity: selectedCommunity,
      onCommunitySelected: this.onCommunitySelected,
    });
  }

  onCommunitySelected(community) {
    //the reason not directly updated the selectedCommunity in redux-state is because the post might not be posted. So updating should be done after posting
    this.setState({
      selectedCommunity: community,
    });
  }

  onLinkSelected() {
    const {isLinkSelected} = this.state;
    this.setState({
      isLinkSelected: !isLinkSelected,
    });
  }

  chooseImage() {
    if (IS_IOS) {
      ActionSheetIOS.showActionSheetWithOptions(
        {
          message: 'Upload Image/Photo',
          options: ['cancel', 'Take Photo', 'Upload Photo'],
          cancelButtonIndex: 0,
        },
        buttonIndex => {
          if (buttonIndex === 1) {
            this.showCamera();
          } else if (buttonIndex === 2) {
            this.showImagePicker();
          }
        },
      );
    } else {
      MultiImagePicker.showMultiUploadOptions({
        onCameraSelect: this.showCamera,
        onUploadSelected: this.showImagePicker,
      });
    }
  }

  showImagePicker() {
    ImagePicker.openPicker({
      multiple: true,
      mediaType: 'photo',
      compressImageMaxWidth: 1024,
      compressImageMaxHeight: 1024,
    }).then(images => {
      if (IS_IOS) {
        let converted = images.map(item => {
          let photoItem = {
            uri: item.sourceURL,
            type: item.mime,
            name: item.fileName ?? '', //a random name for now, it is needed else fetch wont grab the data from the uri
          };
          return photoItem;
        });

        this.setState({
          photos: [...this.state.photos, ...converted],
        });
      } else {
        //for android
        let converted = images.map(item => {
          var fileName = item.path.substring(item.path.lastIndexOf('/') + 1);
          let photoItem = {
            uri: item.path,
            type: item.mime,
            name: fileName ?? '',
          };
          return photoItem;
        });
        this.setState({
          photos: [...this.state.photos, ...converted],
        });
      }
    });
  }

  showCamera() {
    ImagePicker.openCamera({
      mediaType: 'photo',
      compressImageMaxWidth: 1024,
      compressImageMaxHeight: 1024,
    }).then(image => {
      if (IS_IOS) {
        console.log(image);
        let photoItem = {
          uri: image.sourceURL,
          type: image.mime,
          name: image.fileName ?? '', //a random name for now, it is needed else fetch wont grab the data from the uri
        };

        this.setState({
          photos: [...this.state.photos, photoItem],
        });
      } else {
        var fileName = image.path.substring(image.path.lastIndexOf('/') + 1);
        let photoItem = {
          uri: image.path,
          type: image.mime,
          name: fileName ?? '',
        };
        this.setState({
          photos: [...this.state.photos, photoItem],
        });
      }
    });
  }

  removePhoto(item, index) {
    const {photos} = this.state;
    if (index < photos.length) {
      if (!item.id) {
        photos.splice(index, 1);
        this.setState({photos});
      } else {
        this.deleteMedia(item, index);
      }
    }
  }

  post() {
    const {
      postTitle,
      postDetail,
      postLink,
      photos,
      selectedCommunity,
    } = this.state;
    const {onPosted} = this.props;
    var hasText = 'false';
    var hasImage = 'false';
    var hasGIF = 'false';
    if (postDetail !== '') {
      hasText = 'true';
    }
    const newPost = new CommunityPostPO({
      title: postTitle,
      content: postDetail,
    });
    if (!ObjectUtil.isEmpty(postLink)) {
      newPost.externalUrl = postLink;
    }

    let uploads = [];
    if (!ObjectUtil.isEmpty(photos)) {
      uploads = photos.map((currentValue, index) => {
        const fileName = 'filename' + index;
        const mediaItem = new Object();
        mediaItem[fileName] = currentValue;
        console.log(currentValue);
        if (currentValue.name.includes('gif')) {
          hasGIF = 'true';
        } else {
          hasImage = 'true';
        }
        return mediaItem;
      });
    }

    GoogleAnalyticUtil.trackCommunityActivity({
      parameters: {type: 'post', hasGIF, hasText, hasImage},
    });

    this.setPostButton(false, 'Posting');
    CommunitiesService.addUpdatePost({
      post: newPost,
      community: selectedCommunity,
      photos: uploads,
    })
      .then(response => {
        const {post} = response;
        let postedPO;
        if (!ObjectUtil.isEmpty(post)) {
          postedPO = new CommunityPostPO(post);
        }

        Navigation.dismissModal('NewCommunitiesPost').then(() => {
          if (onPosted) {
            onPosted({
              post: postedPO,
              inCommunity: selectedCommunity,
            });
          }
        });
      })
      .catch(error => {
        this.setPostButton(true, 'Post');
      });
  }

  deleteMedia(item, index) {
    const {initialEditCommunityPost, photos} = this.state;
    if (
      !ObjectUtil.isEmpty(initialEditCommunityPost) &&
      !ObjectUtil.isEmpty(initialEditCommunityPost.media)
    ) {
      var mediaToRemove = initialEditCommunityPost.media.find(
        mediaItem => mediaItem.id === item.id,
      );

      if (!ObjectUtil.isEmpty(mediaToRemove)) {
        CommunitiesService.deleteMedia({
          media: mediaToRemove,
        })
          .then(response => {
            //remove front end list
            photos.splice(index, 1);
            this.setState({photos});
          })
          .catch(error => {
            console.log(error);
          });
      }
    }
  }

  showFullScreenImages(startIndex) {
    const {photos} = this.state;
    const medias = photos.map(item => {
      return {url: item.uri};
    });
    FullScreenImagePreview.show({
      mediaItems: medias,
      startIndex: startIndex ?? 0,
      shouldShowThumbnails: false,
    });
  }

  updatePost() {
    const {
      postTitle,
      postDetail,
      postLink,
      photos,
      selectedCommunity,
      initialEditCommunityPost,
    } = this.state;
    const {onPosted} = this.props;

    if (!ObjectUtil.isEmpty(initialEditCommunityPost)) {
      initialEditCommunityPost.title = postTitle;
      initialEditCommunityPost.content = postDetail;
      initialEditCommunityPost.externalUrl = postLink;

      //removed existing photos from photos
      var newPhotosToUpload = [];
      if (!ObjectUtil.isEmpty(photos)) {
        photos.map((photo, index) => {
          //note: if photo id is undefined,assume it's new photo
          if (!photo.id) {
            const fileName = 'filename' + index;
            const mediaItem = new Object();
            mediaItem[fileName] = photo;
            newPhotosToUpload.push(mediaItem);
          }
        }); //end of map
      } //end of if condition

      this.setPostButton(false, 'Updating');

      CommunitiesService.addUpdatePost({
        post: initialEditCommunityPost,
        community: selectedCommunity,
        photos: newPhotosToUpload,
      })
        .then(response => {
          const {post} = response;
          let postedPO;
          if (!ObjectUtil.isEmpty(post)) {
            postedPO = new CommunityPostPO(post);
          }

          Navigation.dismissModal('NewCommunitiesPost').then(() => {
            if (onPosted) {
              onPosted({
                post: postedPO,
                inCommunity: selectedCommunity,
              });
            }
          });
        })
        .catch(error => {
          this.setPostButton(true, 'Update');
        });
    }
  }

  renderTitleSection() {
    const {postTitle, maxTitleChar, selectedCommunity} = this.state;
    return (
      <View>
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
          }}>
          <BodyText>Title</BodyText>
          <Button
            textStyle={{
              fontSize: 12,
              marginHorizontal: 2,
            }}
            leftView={
              <FeatherIcon name={'users'} size={16} color={SRXColor.Black} />
            }
            rightView={
              <FeatherIcon
                name={'chevron-down'}
                size={16}
                color={SRXColor.Teal}
              />
            }
            onPress={this.onChooseCommunity}>
            {' ' + selectedCommunity.name}
          </Button>
        </View>
        <View
          style={{
            borderBottomColor: SRXColor.LightGray,
            borderBottomWidth: 1,
            paddingTop: Spacing.S,
          }}>
          <TextInput
            style={{
              paddingTop: Spacing.XS,
              paddingBottom: Spacing.M,
              fontSize: 16,
            }}
            underlineColorAndroid="transparent"
            placeholder={'Tell your neighbours what you are up to'}
            placeholderTextColor={SRXColor.Gray}
            maxLength={maxTitleChar}
            value={postTitle}
            onChangeText={text => {
              this.setState({postTitle: text});
            }}
          />
          <ExtraSmallBodyText
            style={{
              color: SRXColor.Gray,
              position: 'absolute',
              bottom: 0,
              right: 0,
            }}>
            {maxTitleChar - postTitle.length}
          </ExtraSmallBodyText>
        </View>
      </View>
    );
  }

  renderDetailsSection() {
    const {postDetail, maxDetailChar} = this.state;
    return (
      <View
        style={{
          borderBottomColor: SRXColor.LightGray,
          borderBottomWidth: 1,
          paddingTop: Spacing.L,
        }}>
        <TextInput
          style={{
            paddingTop: Spacing.XS,
            paddingBottom: Spacing.M,
            fontSize: 16,
            height: 140,
            textAlignVertical: 'top',
          }}
          underlineColorAndroid="transparent"
          placeholder={'Type in detail'}
          placeholderTextColor={SRXColor.Gray}
          multiline={true}
          maxLength={maxDetailChar}
          value={postDetail}
          onChangeText={text => {
            this.setState({postDetail: text});
          }}
        />
        <ExtraSmallBodyText
          style={{
            color: SRXColor.Gray,
            position: 'absolute',
            bottom: 0,
            right: 0,
          }}>
          {maxDetailChar - postDetail.length}
        </ExtraSmallBodyText>
      </View>
    );
  }

  renderMedia() {
    return (
      <View style={{paddingVertical: Spacing.S}}>
        {this.renderMediaOptions()}
        {this.renderLinkInput()}
        {this.renderUploadedPhotos()}
      </View>
    );
  }

  renderMediaOptions() {
    const {isLinkSelected, photos} = this.state;
    const hasPhotos = !ObjectUtil.isEmpty(photos);
    return (
      <View style={{flexDirection: 'row', marginBottom: Spacing.M}}>
        <Button
          buttonStyle={{
            height: 25,
            borderRadius: 12.5,
            borderWidth: 1,
            paddingHorizontal: Spacing.S,
            borderColor: SRXColor.Teal,
            marginRight: Spacing.S,
          }}
          selectedButtonStyle={{
            backgroundColor: SRXColor.Teal,
          }}
          textStyle={{fontSize: 14}}
          selectedTextStyle={{
            color: SRXColor.White,
          }}
          leftView={
            <FeatherIcon
              name={'link'}
              size={14}
              color={isLinkSelected ? SRXColor.White : SRXColor.Teal}
              style={{marginRight: Spacing.XS / 2}}
            />
          }
          isSelected={isLinkSelected}
          onPress={this.onLinkSelected}>
          Link
        </Button>
        <Button
          buttonStyle={{
            height: 25,
            borderRadius: 12.5,
            borderWidth: 1,
            paddingHorizontal: Spacing.S,
            borderColor: SRXColor.Teal,
          }}
          selectedButtonStyle={{
            backgroundColor: SRXColor.Teal,
          }}
          textStyle={{fontSize: 14}}
          selectedTextStyle={{
            color: SRXColor.White,
          }}
          leftView={
            <FeatherIcon
              name={'camera'}
              size={14}
              color={hasPhotos ? SRXColor.White : SRXColor.Teal}
              style={{marginRight: Spacing.XS / 2}}
            />
          }
          isSelected={hasPhotos}
          onPress={this.chooseImage}>
          Photos
        </Button>
      </View>
    );
  }

  renderLinkInput() {
    const {isLinkSelected, postLink} = this.state;
    if (isLinkSelected) {
      return (
        <View>
          <TextInput
            style={{
              paddingTop: Spacing.XS,
              paddingBottom: Spacing.XS,
              fontSize: 16,
              borderBottomColor: SRXColor.LightGray,
              borderBottomWidth: 1,
            }}
            underlineColorAndroid="transparent"
            placeholder={'Paste your link here'}
            placeholderTextColor={SRXColor.Gray}
            value={postLink}
            onChangeText={text => {
              this.setState({postLink: text});
            }}
          />
        </View>
      );
    }
  }

  renderUploadedPhotos() {
    const {photos} = this.state;
    if (!ObjectUtil.isEmpty(photos)) {
      return (
        <View
          style={{
            flexDirection: 'row',
            flexWrap: 'wrap',
            marginTop: Spacing.S,
            marginRight: -Spacing.XS,
          }}>
          {photos.map((items, componentIndex) => {
            return this.renderPhoto(items, componentIndex);
          })}
        </View>
      );
    }
  }

  renderPhoto(item, index) {
    console.log(item);
    return (
      <View style={{marginRight: Spacing.XS, marginBottom: Spacing.S}}>
        <TouchableOpacity
          onPress={() => this.showFullScreenImages(index)}
          style={{marginTop: Spacing.XS, marginRight: Spacing.XS}}>
          <Image
            style={{
              width: 100,
              height: 86,
              borderRadius: 10,
              backgroundColor: SRXColor.LightGray,
            }}
            source={{uri: item.uri}}
          />
        </TouchableOpacity>
        <Button
          onPress={() => this.removePhoto(item, index)}
          buttonStyle={{
            backgroundColor: SRXColor.Teal,
            padding: 6,
            borderRadius: 13,
            borderColor: SRXColor.White,
            borderWidth: 1,
            position: 'absolute',
            right: 0,
            top: 0,
          }}
          leftView={<FeatherIcon name={'x'} size={12} color={SRXColor.White} />}
        />
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <KeyboardAwareScrollView style={{flex: 1}}>
          <View
            style={{
              paddingHorizontal: Spacing.M,
              paddingVertical: Spacing.M,
            }}>
            {this.renderTitleSection()}
            {this.renderDetailsSection()}
            {this.renderMedia()}
          </View>
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

CommunitiesNewPost.propTypes = {
  /**
   * Optional
   * Callback to notified the post is posted, will include the new CommunityPostPO & community posted in as {post, inCommunity}
   */
  onPosted: PropTypes.func,

  /**
   * It is to differentiate Edit existing post or Add new post
   * can find action from CommunitiesNewPostAction
   */
  action: PropTypes.string,

  /** Optional
   *  CommunityPostPO for edit action
   * */
  communityPostPO: PropTypes.instanceOf(CommunityPostPO),
};

export {CommunitiesNewPost};
