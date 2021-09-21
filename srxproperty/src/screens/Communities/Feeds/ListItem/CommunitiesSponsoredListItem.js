import React, {Component} from 'react';
import {View, Image, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {SRXColor} from '../../../../constants';
import {
  ExtraSmallBodyText,
  Avatar,
  BodyText,
  Separator,
  Button,
  FeatherIcon,
  OcticonsIcon,
  SmallBodyText,
  TouchableHighlight,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import {ObjectUtil} from '../../../../utils';
import {CommunitiesConstant} from '../../Constants';
import {CommunityPostPO} from '../../../../dataObject';
import {
  CommunitiesPostItemContent,
  CommunitiesPostItemCreator,
} from './Contents';
import {CommunitiesFeedReaction} from './CommunitiesFeedReaction';

class CommunitiesSponsoredListItem extends Component {
  constructor(props) {
    super(props);

    this.onSharePost = this.onSharePost.bind(this);
    this.onEnquiry = this.onEnquiry.bind(this);
    this.onSelectReaction = this.onSelectReaction.bind(this);
    this.onSelectPost = this.onSelectPost.bind(this);
    this.onPressPostCreator = this.onPressPostCreator.bind(this);
    this.onCommentPressed = this.onCommentPressed.bind(this);
  }

  onPressPostCreator() {
    const {onPostCreatorPressed, post} = this.props;
    if (onPostCreatorPressed) {
      onPostCreatorPressed(post);
    }
  }

  onSelectPost() {
    const {onPostSelected, post} = this.props;
    onPostSelected(post);
  }

  onSharePost() {
    const {onSharePressed, post} = this.props;
    onSharePressed(post);
  }

  onEnquiry() {
    const {onEnquiryPressed, post} = this.props;

    if (onEnquiryPressed) {
      onEnquiryPressed(post);
    }
  }

  onSelectReaction(reaction) {
    const {onReactionSelected, post} = this.props;

    onReactionSelected({post, reaction});
  }

  onCommentPressed() {
    const {onCommentPressed, post} = this.props;
    if (onCommentPressed) {
      onCommentPressed(post);
    }
  }

  renderCellHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          marginBottom: Spacing.S,
        }}>
        {this.renderPostCreator()}
        <ExtraSmallBodyText
          style={{marginLeft: Spacing.XS, color: SRXColor.Gray}}>
          Sponsored
        </ExtraSmallBodyText>
      </View>
    );
  }

  renderPostCreatorImage() {
    const {post} = this.props;
    if (
      !ObjectUtil.isEmpty(post.user) &&
      !ObjectUtil.isEmpty(post.user.photo)
    ) {
      return (
        <View style={{marginRight: Spacing.XS / 2}}>
          <Avatar
            name={post.user.name}
            imageUrl={post.user.photo}
            size={24}
            borderColor={'#DCDCDC'}
            textSize={12}
          />
        </View>
      );
    }
  }

  renderPostCreator() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.user)) {
      return (
        <View style={{flexDirection: 'row', alignItems: 'center', flex: 1}}>
          <TouchableOpacity
            style={{flexDirection: 'row', alignItems: 'center'}}
            onPress={this.onPressPostCreator}>
            <CommunitiesPostItemCreator post={post} />
          </TouchableOpacity>
        </View>
      );
    }
  }

  renderActions() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
        {this.renderActionsOnLeft()}
        <Button
          buttonStyle={{
            paddingVertical: Spacing.XS,
          }}
          onPress={this.onSharePost}>
          Share
        </Button>
      </View>
    );
  }

  renderActionsOnLeft() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
        }}>
        {this.renderReactions()}
        {this.renderEnquireNowBtn()}
      </View>
    );
  }

  renderEnquireNowBtn() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.sponsored)) {
      if (
        post.sponsored.sponsoredType ===
        CommunitiesConstant.sponsoredType.listing
      ) {
        return (
          <Button
            buttonStyle={{
              paddingVertical: Spacing.XS,
              marginRight: Spacing.L,
            }}
            onPress={this.onEnquiry}>
            Enquire now
          </Button>
        );
      } else if (
        post.sponsored.sponsoredType ===
        CommunitiesConstant.sponsoredType.business
      ) {
        return (
          <Button
            leftView={
              <OcticonsIcon
                name={'comment'}
                size={20}
                color={'black'}
                style={{
                  marginRight: Spacing.XS / 2,
                  transform: [{rotateY: '180deg'}],
                }}
              />
            }
            buttonStyle={{
              paddingVertical: Spacing.XS,
              marginRight: Spacing.L,
            }}
            textStyle={{
              color: SRXColor.Gray,
            }}
            onPress={this.onCommentPressed}>
            {post.commentsTotal > 0 ? post.commentsTotal.toString() : ''}
          </Button>
        );
      }
    } //end of post sponsored
  }

  renderReactions() {
    const {post} = this.props;
    if (
      !ObjectUtil.isEmpty(post.reactions) &&
      !ObjectUtil.isEmpty(post.sponsored) &&
      post.sponsored.sponsoredType ===
        CommunitiesConstant.sponsoredType.business
    ) {
      return (
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          {post.reactions.map((item, componentIndex) => {
            return (
              <CommunitiesFeedReaction
                reaction={item}
                key={item.cdReaction}
                onReactionSelected={this.onSelectReaction}
              />
            );
          })}
        </View>
      );
    }
  }

  render() {
    const {post} = this.props;
    return (
      <TouchableHighlight
        style={{
          borderRadius: 10,
          backgroundColor: SRXColor.White,
          marginBottom: Spacing.XS,
          overflow: 'hidden',
        }}
        onPress={this.onSelectPost}>
        <View
          style={{
            backgroundColor: SRXColor.White,
            paddingTop: Spacing.S,
            paddingBottom: Spacing.XS,
            paddingHorizontal: Spacing.M,
          }}>
          {this.renderCellHeader()}
          <CommunitiesPostItemContent post={post} />
          <Separator />
          {this.renderActions()}
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesSponsoredListItem.propTypes = {
  post: PropTypes.instanceOf(CommunityPostPO),

  onPostSelected: PropTypes.func,
  /**
   * callback when Share button pressed, will pass Community in the method
   */
  onSharePressed: PropTypes.func,

  /**
   * callback when Enquiry button pressed, will pass CommunityPostPO in the method
   */
  onEnquiryPressed: PropTypes.func,

  /**
   * callback when press agent's photo or name, will pass CommunityPostPO in the method
   */
  onPostCreatorPressed: PropTypes.func,

  /**
   *  props to call back when a reaction selected, will pass back the CommunityPostPO & CommunityPostReactionPO as {post, reaction}
   */
  onReactionSelected: PropTypes.func,

  /**
   *  props to call back when a reaction selected, will pass back the CommunityPostPO
   */
  onCommentPressed: PropTypes.func,
};

export {CommunitiesSponsoredListItem};
