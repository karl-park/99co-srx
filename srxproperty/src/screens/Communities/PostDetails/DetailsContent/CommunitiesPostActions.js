import React, {Component} from 'react';
import {View, Image} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {Spacing, Typography} from '../../../../styles';
import {SRXColor} from '../../../../constants';
import {Button, OcticonsIcon, SmallBodyText} from '../../../../components';
import {CommunityPostPO, CommunityPostReactionPO} from '../../../../dataObject';
import {CommunitiesService} from '../../../../services';
import {StringUtil, ObjectUtil} from '../../../../utils';
import {CommunitiesStack} from '../../../../config';
import {CommunitiesConstant} from '../../Constants';

class CommunitiesPostActions extends Component {
  constructor(props) {
    super(props);
    const {communityPostPO} = props;
    this.state = {
      reactionsState: communityPostPO.reactions,
    };
  }

  onReact = reaction => {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      if (userPO.mobileVerified) {
        this.onSelectReaction(reaction);
      } else {
        //mobile not verified
        CommunitiesStack.showMobileVerificationModal();
      }
    } else {
      //not logged in
      CommunitiesStack.showSignInRegisterModal();
    }
  };

  onSelectReaction(reaction) {
    const {reactionsState} = this.state;
    if (!ObjectUtil.isEmpty(reaction)) {
      var selectedReaction = reactionsState.find(item => item.reacted == true);

      if (!ObjectUtil.isEmpty(selectedReaction)) {
        if (selectedReaction.cdReaction === reaction.cdReaction) {
          //unlike from like
          reaction.count -= 1;
          reaction.reacted = false;
        } else {
          reactionsState.map(item => {
            if (item.cdReaction == selectedReaction.cdReaction) {
              item.count -= 1;
              item.reacted = false;
            } else if (item.cdReaction == reaction.cdReaction) {
              item.count += 1;
              item.reacted = true;
            }
          });
        }
      } else {
        reactionsState.map(item => {
          if (item.cdReaction === reaction.cdReaction) {
            item.count += 1;
            item.reacted = true;
          }
        });
      }
      this.forceUpdate();

      this.onPostReactionCalled(reaction.cdReaction);
    }
  }

  onPostReactionCalled = reactionId => {
    const {communityPostPO} = this.props;
    CommunitiesService.postReaction({
      id: communityPostPO.id,
      reactionId: reactionId,
    })
      .then(response => {
        if (response) {
          const {reactions} = response;
          this.setState({reactionsState: reactions}, () => {
            const {onUpdateReactions} = this.props;
            if (onUpdateReactions) {
              onUpdateReactions(this.state.reactionsState);
            }
          });
        }
      })
      .catch(error => {
        const {data} = error;
        console.log(data ? data.error : 'Error');
      });
  };

  renderReactionCount(reaction) {
    if (!ObjectUtil.isEmpty(reaction) && reaction.count > 0) {
      return (
        <SmallBodyText
          style={{color: reaction.reacted ? SRXColor.Teal : SRXColor.Gray}}>
          {StringUtil.formatThousandWithAbbreviation(reaction.count)}
        </SmallBodyText>
      );
    }
  }

  renderReactions() {
    const {reactionsState} = this.state;
    return (
      <View
        style={{
          flex: 2,
          flexDirection: 'row',
          alignItems: 'space-between',
        }}>
        {reactionsState.map((item, componentIndex) => {
          return (
            <Button
              buttonStyle={{flex: 1}}
              textStyle={[
                Typography.SmallBody,
                Styles.buttonTextStyle,
                {color: item.reacted ? SRXColor.Teal : SRXColor.Black},
              ]}
              rightView={this.renderReactionCount(item)}
              leftView={
                //TODO: to change image color
                <Image
                  source={{uri: item.iconUrl}}
                  style={{
                    width: 18,
                    height: 18,
                  }}
                  resizeMode={'cover'}
                />
              }
              onPress={() => this.onReact(item)}>
              {item ? item.name : ''}
            </Button>
          );
        })}
      </View>
    );
  }

  renderComment() {
    const {onPressComment, communityPostPO} = this.props;
    //Note: News post & sponored business type are not allowed to comment

    var isSponsoredListingType =
      !ObjectUtil.isEmpty(communityPostPO) &&
      !ObjectUtil.isEmpty(communityPostPO.sponsored) &&
      communityPostPO.sponsored.sponsoredType ===
        CommunitiesConstant.sponsoredType.listing;
    if (
      !ObjectUtil.isEmpty(communityPostPO) &&
      !(communityPostPO.type === CommunitiesConstant.postType.news) &&
      !isSponsoredListingType
    ) {
      return (
        <View
          style={{
            flex: 1,
            justifyContent: 'center',
          }}>
          <Button
            onPress={onPressComment}
            textStyle={[Typography.SmallBody, Styles.buttonTextStyle]}
            leftView={
              <OcticonsIcon
                name={'comment'}
                size={18}
                color={SRXColor.Black}
                style={[
                  {
                    transform: [{rotateY: '180deg'}],
                  },
                ]}
              />
            }
            rightView={this.renderCommentCount()}>
            Comment
          </Button>
        </View>
      );
    }
  }

  renderCommentCount() {
    const {communityPostPO} = this.props;
    if (
      !ObjectUtil.isEmpty(communityPostPO) &&
      communityPostPO.commentsTotal > 0
    ) {
      return (
        <SmallBodyText style={{color: SRXColor.Gray}}>
          {StringUtil.formatThousandWithAbbreviation(
            communityPostPO.commentsTotal,
          )}
        </SmallBodyText>
      );
    }
  }

  render() {
    return (
      //for rounder corner
      <View style={{backgroundColor: SRXColor.LightGray}}>
        <View style={Styles.mainContainer}>
          {this.renderReactions()}
          {this.renderComment()}
        </View>
      </View>
    );
  }
}

CommunitiesPostActions.propTypes = {
  /**
   * community post po to check community post type
   */
  communityPostPO: PropTypes.instanceOf(CommunityPostPO).isRequired,

  postOrCommentId: PropTypes.number.isRequired,

  reactions: PropTypes.arrayOf(PropTypes.instanceOf(CommunityPostReactionPO)),

  commentCount: PropTypes.string,

  onPressComment: PropTypes.func,

  onUpdateReactions: PropTypes.func,
};

const Styles = {
  mainContainer: {
    width: '100%',
    flexDirection: 'row',
    padding: Spacing.M,
    justifyContent: 'space-between',
    borderBottomLeftRadius: 10,
    borderBottomRightRadius: 10,
    backgroundColor: SRXColor.White,
  },
  buttonTextStyle: {
    marginLeft: Spacing.XS / 2,
    marginRight: Spacing.XS / 2,
    lineHeight: 16,
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  null,
)(CommunitiesPostActions);
