import React, {Component} from 'react';
import {View, Share, SafeAreaView, Animated, Easing} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';

//Custom Components
import {Button, FeatherIcon} from '../../../components';
import {Spacing, Typography} from '../../../styles';
import {SRXColor} from '../../../constants';
import {CommunitiesStack} from '../../../config';
import {CommunitiesConstant} from '../Constants';
import {CommunityPostPO} from '../../../dataObject';
import {ObjectUtil} from '../../../utils';
import {CommunitiesService} from '../../../services';

class ShareModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }
  constructor(props) {
    super(props);

    this.translateY = 200;
    this.state = {
      sheetAnim: new Animated.Value(this.translateY),
    };
    this.onCloseModal = this.onCloseModal.bind(this);
    this.hideSheet = this.hideSheet.bind(this);
    this.onPressShare = this.onPressShare.bind(this);
    this.onPressShareViaChat = this.onPressShareViaChat.bind(this);
    this.onPressMoreOptions = this.onPressMoreOptions.bind(this);
  }

  state = {
    newsPostUrl: '',
  };

  componentDidMount() {
    this.showSheet();
    this.getPostUrl();
  }

  getPostUrl() {
    const {post} = this.props;
    let postId = !ObjectUtil.isEmpty(post.parentPost)
      ? post.parentPost.id
      : post.id;
    CommunitiesService.getPostUrl({
      id: postId,
    })
      .then(response => {
        const {result, url} = response;
        if (result == 'success') {
          this.setState({newsPostUrl: url});
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  onCloseModal = () => {
    return this.hideSheet();
  };

  showSheet = () => {
    Animated.timing(this.state.sheetAnim, {
      toValue: 0,
      duration: 250,
      easing: Easing.out(Easing.ease),
    }).start();
  };

  hideSheet() {
    const {componentId} = this.props;
    const {sheetAnim} = this.state;
    const translateValue = this.translateY;
    return new Promise(function(resolve, reject) {
      Animated.timing(sheetAnim, {
        toValue: translateValue,
        duration: 100,
      }).start(() => {
        Navigation.dismissModal(componentId)
          .then(() => {
            resolve();
          })
          .catch(error => {
            reject(error);
          });
      });
    });
  }

  onPressShare = () => {
    const {post, showShareModal} = this.props;

    //Note: if there is parent post under post, use parent post id else use post id
    const passProps = {
      postId: !ObjectUtil.isEmpty(post.parentPost)
        ? post.parentPost.id
        : post.id,
    };

    if (showShareModal) {
      showShareModal(passProps);
    } else {
      this.onCloseModal().then(() => {
        CommunitiesStack.showSharePostModal(passProps);
      });
    }
  };

  onPressShareViaChat = () => {
    const {post, previousComponentId} = this.props;
    const user = post.user;
    const tempAgentInfo = {
      userId: user.id,
      conversationId: null, //assume it's null
      name: user.name,
      photo: user.photo,
      mobile: user.mobileLocalNum,
      agent: user.agent,
    };

    this.onCloseModal().then(() => {
      Navigation.push(previousComponentId, {
        component: {
          name: 'ChatStack.chatRoom',
          passProps: {
            agentInfo: tempAgentInfo,
          },
        },
      });
    });
  };

  onPressMoreOptions = () => {
    const {post} = this.props;

    //Share external link
    var tempMessage = post.externalUrl;
    
    if (ObjectUtil.isEmpty(tempMessage)) {
      //todo: not sure what to share if external link is empty.
      //check with them temp one
      tempMessage = 'Share communities www.srx.com.sg';
    }

    const content = {
      message: tempMessage,
    };

    const options = {
      dialogTitle: 'Share News', //iOS only
    };

    Share.share(content, options);
  };

  renderShareModal() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          paddingTop: Spacing.M,
          paddingBottom: Spacing.S,
        }}>
        {this.renderShare()}
        {/* {this.renderShareViaChat()} */}
        {this.renderMoreOptions()}
        {this.renderButton()}
      </View>
    );
  }

  renderShare() {
    return (
      <View style={{marginVertical: Spacing.S}}>
        <Button
          textStyle={[Typography.Body, {marginLeft: Spacing.S}]}
          leftView={
            <FeatherIcon name={'edit-3'} size={24} color={SRXColor.Black} />
          }
          onPress={() => this.onPressShare()}>
          Share
        </Button>
      </View>
    );
  }

  renderShareViaChat() {
    //Note: currently show share via chat for listing
    //and transacted listing post type first
    const {post} = this.props;
    if (
      post.type === CommunitiesConstant.postType.listing ||
      post.type === CommunitiesConstant.postType.transactedListing
    ) {
      return (
        <View style={{marginVertical: Spacing.S}}>
          <Button
            textStyle={[Typography.Body, {marginLeft: Spacing.S}]}
            leftView={
              <FeatherIcon
                name={'message-circle'}
                size={24}
                color={SRXColor.Black}
              />
            }
            onPress={() => this.onPressShareViaChat()}>
            Share via SRX Chat
          </Button>
        </View>
      );
    }
  }

  renderMoreOptions() {
    const {post} = this.props;
    if (
      !ObjectUtil.isEmpty(post) &&
      post.type !== CommunitiesConstant.postType.normal
    ) {
      return (
        <View style={{marginVertical: Spacing.S}}>
          <Button
            textStyle={[Typography.Body, {marginLeft: Spacing.S}]}
            leftView={
              <FeatherIcon
                name={'more-horizontal'}
                size={24}
                color={SRXColor.Black}
              />
            }
            onPress={() => this.onPressMoreOptions()}>
            More Options
          </Button>
        </View>
      );
    }
  }

  renderButton() {
    return (
      <Button
        buttonStyle={{
          alignItems: 'center',
          justifyContent: 'center',
          marginTop: Spacing.XS,
        }}
        buttonType={Button.buttonTypes.secondary}
        onPress={() => this.onCloseModal()}>
        Cancel
      </Button>
    );
  }

  render() {
    const {sheetAnim} = this.state;
    return (
      <View style={Styles.modalOverlay}>
        <Animated.View
          style={{
            // height: this.translateY,
            transform: [{translateY: sheetAnim}],
            justifyContent: 'flex-end',
          }}>
          <SafeAreaView style={Styles.container}>
            {this.renderShareModal()}
          </SafeAreaView>
        </Animated.View>
      </View>
    );
  }
}

ShareModal.propTypes = {
  previousComponentId: PropTypes.string.isRequired,

  post: PropTypes.instanceOf(CommunityPostPO).isRequired,

  /***
   * Optional. method to show ShareModal from parent component
   */
  showShareModal: PropTypes.func,
};

const Styles = {
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    justifyContent: 'flex-end',
  },
  container: {
    backgroundColor: SRXColor.White,
    borderTopLeftRadius: 10,
    borderTopRightRadius: 10,
  },
};

export {ShareModal};
