import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';
import {SRXColor} from '../../../../constants';
import {
  ExtraSmallBodyText,
  Avatar,
  Separator,
  Button,
  OcticonsIcon,
  SmallBodyText,
  TouchableHighlight,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import {ObjectUtil} from '../../../../utils';
import {CommunityPostPO} from '../../../../dataObject';
import {CommunitiesFeedReaction} from './CommunitiesFeedReaction';
import {
  CommunitiesPostItemContent,
  CommunitiesPostItemCreator,
} from './Contents';
import {CommunitiesConstant} from '../../Constants';

class CommunitiesUserPostListItem extends Component {
  constructor(props) {
    super(props);

    this.onSelectPost = this.onSelectPost.bind(this);
    this.onSelectReaction = this.onSelectReaction.bind(this);
    this.onSelectComment = this.onSelectComment.bind(this);
    this.onSharePost = this.onSharePost.bind(this);
    this.onPressPostCreator = this.onPressPostCreator.bind(this);
  }

  onSelectPost(post) {
    const {onPostSelected} = this.props;
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

  onPressPostCreator(post) {
    const {onPostCreatorPressed} = this.props;
    if (onPostCreatorPressed) {
      onPostCreatorPressed(post);
    }
  }

  renderCellHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: Spacing.XS,
        }}>
        {this.renderPostCreator()}
        {this.renderPostDate()}
      </View>
    );
  }

  renderPostCreator() {
    const {post} = this.props;
    return (
      <CommunitiesPostItemCreator
        post={post}
        onPress={() => this.onPressPostCreator(post)}
      />
    );
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

  renderContents() {
    const {post, onLinkPressed} = this.props;
    if (!ObjectUtil.isEmpty(post.parentPost)) {
      return this.renderSharedPostContents();
    } else {
      return (
        <CommunitiesPostItemContent post={post} onLinkPressed={onLinkPressed} />
      );
    }
  }

  renderSharedPostContents() {
    return (
      <View>
        {this.renderPostDetail()}
        {this.renderSharedPost()}
      </View>
    );
  }

  renderPostDetail() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post.content)) {
      return (
        <SmallBodyText style={{marginVertical: Spacing.XS}}>
          {post.content}
        </SmallBodyText>
      );
    }
  }

  renderSharedPost() {
    const {post, onLinkPressed} = this.props;
    if (!ObjectUtil.isEmpty(post.parentPost)) {
      return (
        <TouchableHighlight
          style={{
            borderRadius: 10,
            borderWidth: 1,
            borderColor: SRXColor.LightGray,
            overflow: 'hidden',
          }}
          onPress={() => this.onSelectPost(post.parentPost)}>
          <View style={{backgroundColor: SRXColor.White}}>
            <View
              style={{
                paddingHorizontal: Spacing.XS,
                paddingTop: Spacing.S,
                paddingBottom: Spacing.XS,
              }}>
              <CommunitiesPostItemCreator
                post={post.parentPost}
                onPress={() => this.onPressPostCreator(post.parentPost)}
              />
            </View>
            <CommunitiesPostItemContent
              post={post.parentPost}
              showDetails={
                post.parentPost.type != CommunitiesConstant.postType.news
              }
              contentStyles={{paddingHorizontal: Spacing.S}}
              onLinkPressed={onLinkPressed}
            />
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderSeparator() {
    const {post} = this.props;
    if (ObjectUtil.isEmpty(post.parentPost)) {
      return <Separator />;
    }
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
          {this.renderReactions()}
          {this.renderCommentBtn()}
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
        onPress={() => this.onSelectPost(post)}>
        <View
          style={{
            // borderRadius: 10,
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.XS,
          }}>
          {this.renderCellHeader()}
          {this.renderContents()}
          {this.renderSeparator()}
          {this.renderActions()}
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesUserPostListItem.propTypes = {
  post: PropTypes.instanceOf(CommunityPostPO),

  /**
   *  props to call back when row selected, will pass back the ComminityPostPO
   */
  onPostSelected: PropTypes.func,
  onSharePressed: PropTypes.func,
  onCommentPressed: PropTypes.func,

  /**
   *  props to call back when link selected, will pass back the url string
   */
  onLinkPressed: PropTypes.func,
  /**
   *  props to call back when a reaction selected, will pass back the CommunityPostPO & CommunityPostReactionPO as {post, reaction}
   */
  onReactionSelected: PropTypes.func,

  /**
   * callback when press agent's photo or name, will pass CommunityPostPO in the method
   */
  onPostCreatorPressed: PropTypes.func,
};

export {CommunitiesUserPostListItem};
