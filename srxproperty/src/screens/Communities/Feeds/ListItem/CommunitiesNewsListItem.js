import React, {Component} from 'react';
import {View, Image} from 'react-native';
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
import {CommunityPostPO} from '../../../../dataObject';
import {Spacing} from '../../../../styles';
import {ObjectUtil} from '../../../../utils';
import {CommunitiesFeedReaction} from './CommunitiesFeedReaction';
import {CommunitiesConstant} from '../../Constants';
import {
  CommunitiesPostItemContent,
  CommunitiesPostItemCreator,
} from './Contents';

class CommunitiesNewsListItem extends Component {
  constructor(props) {
    super(props);

    this.onSelectPost = this.onSelectPost.bind(this);
    this.onSelectReaction = this.onSelectReaction.bind(this);
    this.onSelectComment = this.onSelectComment.bind(this);
    this.onSharePost = this.onSharePost.bind(this);
  }

  onSelectPost() {
    const {onPostSelected, post} = this.props;
    onPostSelected(post);
  }

  onSelectReaction(reaction) {
    const {onReactionSelected, post} = this.props;
    onReactionSelected({post, reaction});
  }

  onSelectComment() {
    const {onCommentPressed, post} = this.props;
    onCommentPressed(post);
  }

  onSharePost() {
    const {onSharePressed, post} = this.props;
    onSharePressed(post);
  }

  renderCellHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: Spacing.S,
        }}>
        {this.renderPostCreator()}
        {this.renderPostDate()}
      </View>
    );
  }

  renderPostCreator() {
    const {post} = this.props;
    return <CommunitiesPostItemCreator post={post} />;
  }

  renderPostDate() {
    const {post} = this.props;
    const {community} = post;

    let communityName = '';
    if (!ObjectUtil.isEmpty(community) && !ObjectUtil.isEmpty(community.name)) {
      communityName = community.name + ' · ';
    }
    return (
      <ExtraSmallBodyText
        style={{
          marginLeft: Spacing.XS,
          color: SRXColor.Gray,
        }}>
        <ExtraSmallBodyText
          style={{
            marginLeft: Spacing.XS,
            textTransform: 'capitalize',
            color: SRXColor.Gray,
          }}>
          {communityName}
        </ExtraSmallBodyText>
        {post.getDateDisplay()}
      </ExtraSmallBodyText>
    );
  }

  renderReactions() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.reactions)) {
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

  renderCommentBtn() {
    const {post} = this.props;
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
        onPress={this.onSelectComment}>
        {post.commentsTotal > 0 ? post.commentsTotal.toString() : ''}
      </Button>
    );
  }

  renderActions() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
        <View
          style={{
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          {}
          {this.renderReactions()}
          {/* {this.renderCommentBtn()} */}
        </View>
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
            paddingTop:
              !ObjectUtil.isEmpty(post.user) &&
              post.user.id ==
                CommunitiesConstant.srxInfoUserIds.newPostUserId &&
              post.user.name == 'SRX'
                ? Spacing.XS
                : Spacing.M,
            paddingBottom: Spacing.XS,
            paddingHorizontal: Spacing.M,
          }}>
          {this.renderCellHeader()}
          <CommunitiesPostItemContent post={post} showDetails={true} />
          <Separator />
          {this.renderActions()}
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesNewsListItem.propTypes = {
  post: PropTypes.instanceOf(CommunityPostPO),

  /**
   *  props to call back when row selected, will pass back the ComminityPostPO
   */
  onPostSelected: PropTypes.func,
  onSharePressed: PropTypes.func,
  onCommentPressed: PropTypes.func,
  /**
   *  props to call back when a reaction selected, will pass back the CommunityPostPO & CommunityPostReactionPO as {post, reaction}
   */
  onReactionSelected: PropTypes.func,
};

export {CommunitiesNewsListItem};
