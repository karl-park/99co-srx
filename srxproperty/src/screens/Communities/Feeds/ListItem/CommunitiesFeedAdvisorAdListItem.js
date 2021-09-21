import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';
import {SRXColor, IS_IOS} from '../../../../constants';
import {
  ExtraSmallBodyText,
  Avatar,
  Separator,
  Button,
  OcticonsIcon,
  SmallBodyText,
  TouchableHighlight,
  BodyText,
  Heading2,
  FeatherIcon,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import {ObjectUtil, CommonUtil} from '../../../../utils';
import {CommunityPostPO, AgentPO} from '../../../../dataObject';
import {CommunitiesFeedReaction} from './CommunitiesFeedReaction';
import {
  CommunitiesPostItemContent,
  CommunitiesPostItemCreator,
} from './Contents';

class CommunitiesFeedAdvisorAdListItem extends Component {
  renderCellHeader() {
    const {post} = this.props;
    return (
      <View style={{marginBottom: Spacing.S}}>
        <ExtraSmallBodyText
          numberOfLines={2}
          style={{color: SRXColor.Purple, fontWeight: '600'}}>
          {post.msg}
        </ExtraSmallBodyText>
      </View>
    );
  }

  renderContents() {
    return (
      <View style={{flex: 1, flexDirection: 'row', marginBottom: Spacing.XS/2}}>
        {this.renderLeftContent()}
        {this.renderRightContent()}
      </View>
    );
  }

  renderRightContent() {
    const {onChatSelected, post} = this.props;
    return (
      <Button
        buttonStyle={{
          // paddingVertical: Spacing.XS,
          marginLeft: Spacing.M,
        }}
        onPress={() => {
          onChatSelected(post);
        }}
        leftView={
          <FeatherIcon
            name={'message-circle'}
            size={20}
            color={SRXColor.Black}
            style={{marginRight: 2}}
          />
        }>
        Chat Now
      </Button>
    );
  }

  renderLeftContent() {
    const {post, onAgentSelected} = this.props;
    const {agentPO} = post;
    return (
      <TouchableHighlight
        style={{
          flex: 1,
        }}
        onPress={() => {
          if (onAgentSelected) {
            onAgentSelected(post);
          }
        }}>
        <View
          style={{
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.XS,
            // width: 260,
            borderRadius: 5,
            borderColor: SRXColor.LightGray,
            borderWidth: IS_IOS ? 0 : 1,
            shadowColor: 'rgb(110,129,154)',
            shadowOffset: {width: 0, height: 0},
            shadowOpacity: 0.32,
            shadowRadius: 3,
            elevation: 2,
            flexDirection: 'row',
            paddingVertical: Spacing.S,
            flex: 1,
            alignItems: 'center',
          }}>
          <Avatar
            size={36}
            name={post.name}
            borderLess={true}
            imageUrl={CommonUtil.handleImageUrl(agentPO.photo)}
          />
          <View style={{paddingHorizontal: Spacing.XS}}>
            <SmallBodyText style={{fontWeight: 'bold'}}>{agentPO.name}</SmallBodyText>
            <ExtraSmallBodyText style={{color: SRXColor.Gray}}>{agentPO.agency}</ExtraSmallBodyText>
          </View>
        </View>
      </TouchableHighlight>
    );
  }
  render() {
    return (
      <TouchableHighlight
        style={{
          borderRadius: 10,
          backgroundColor: SRXColor.White,
          marginBottom: Spacing.XS,
          overflow: 'hidden',
        }}
        // onPress={() => this.onSelectPost(post)}
      >
        <View
          style={{
            // borderRadius: 10,
            backgroundColor: SRXColor.White,
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.M,
          }}>
          {this.renderCellHeader()}

          {this.renderContents()}
          {/*this.renderSeparator()}
          {this.renderActions()} */}
        </View>
      </TouchableHighlight>
    );
  }
}
CommunitiesFeedAdvisorAdListItem.propTypes = {
  onChatSelected: PropTypes.func,
  post: PropTypes.instanceOf(AgentPO),
  onAgentSelected: PropTypes.func
};

export {CommunitiesFeedAdvisorAdListItem};
