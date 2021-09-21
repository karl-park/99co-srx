import React, {Component} from 'react';
import {View, Image, Linking} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {
  Communities_Button_Call,
  Communities_Button_SMS,
  Communities_Button_Whatsapp,
} from '../../../assets';
import {SRXColor, LeadTypes, LeadSources} from '../../../constants';
import {
  Button,
  Heading2,
  Avatar,
  FeatherIcon,
  ExtraSmallBodyText,
} from '../../../components';
import {CommunityPostPO} from '../../../dataObject';
import {Spacing, Typography} from '../../../styles';
import {ObjectUtil, GoogleAnalyticUtil} from '../../../utils';

class CommunityPostContactModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  constructor(props) {
    super(props);

    this.callAgent = this.callAgent.bind(this);
    this.whatsAppAgent = this.whatsAppAgent.bind(this);
    this.smsAgent = this.smsAgent.bind(this);
  }

  onCloseModal = () => {
    return Navigation.dismissModal(this.props.componentId);
  };

  onPressSRXChat = () => {
    const {post, sourceComponentId} = this.props;
    if (!ObjectUtil.isEmpty(post)) {
      let chatUser;
      if (
        !ObjectUtil.isEmpty(post.listing) &&
        !ObjectUtil.isEmpty(post.listing.agentPO)
      ) {
        chatUser = {
          userId: post.listing.agentPO.getAgentId(),
          conversationId: null, //assume it's null
          name: post.listing.agentPO.name,
          photo: post.listing.agentPO.photo,
          mobile: post.listing.agentPO.getMobileNumber(),
          otherUserAgentInd: true,
        };
      } else if (!ObjectUtil.isEmpty(post.user)) {
        chatUser = {
          userId: post.user.id,
          conversationId: null, //assume it's null

          name: post.user.name,
          photo: post.user.photo,
          mobile: post.user.mobileLocalNum,
          agent: post.user.agent,
        };
      }

      if (!ObjectUtil.isEmpty(chatUser)) {
        this.onCloseModal().then(() => {
          Navigation.push(sourceComponentId, {
            component: {
              name: 'ChatStack.chatRoom',
              passProps: {
                agentInfo: chatUser,
              },
            },
          });
        });
      }
    }
  };

  getMobileNumber() {
    const {post} = this.props;

    if (!ObjectUtil.isEmpty(post)) {
      if (
        !ObjectUtil.isEmpty(post.listing) &&
        !ObjectUtil.isEmpty(post.listing.agentPO)
      ) {
        return post.listing.agentPO.getMobileNumber();
      } else if (!ObjectUtil.isEmpty(post.user)) {
        return post.user.mobileLocalNum.toString();
      }
    }
    return null;
  }

  callAgent() {
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.call,
      source: LeadSources.communities,
    });

    const mobileNumber = this.getMobileNumber();
    console.log(mobileNumber);
    if (!ObjectUtil.isEmpty(mobileNumber)) {
      const url = 'tel:' + mobileNumber;

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Communities contact modal - call - Can't handle url: " + url,
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'Communities contact modal - An error occurred - call agent - ',
            err,
          ),
        );
    }
  }

  whatsAppAgent() {
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.whatsapp,
      source: LeadSources.communities,
    });

    const mobileNumber = this.getMobileNumber();

    if (!ObjectUtil.isEmpty(mobileNumber)) {
      var url = 'https://api.whatsapp.com/send?phone=65' + mobileNumber; //+"&text=xxx"

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Communities contact modal - whatsapp - Can't handle url: " + url,
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'Communities contact modal - An error occurred - whatsapp agent - ',
            err,
          ),
        );
    }
  }

  smsAgent() {
    GoogleAnalyticUtil.trackLeads({
      leadType: LeadTypes.sms,
      source: LeadSources.communities,
    });

    const mobileNumber = this.getMobileNumber();

    if (!ObjectUtil.isEmpty(mobileNumber)) {
      var url = 'sms:' + mobileNumber; //+"&body=xxx"

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log(
              "Communities contact modal - sms - Can't handle url: " + url,
            );
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'Communities contact modal - An error occurred - sms agent - ',
            err,
          ),
        );
    }
  }

  renderContactInfoAndChatButton() {
    return (
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        {this.renderContactInfo()}
        {this.renderChatButton()}
      </View>
    );
  }

  renderChatButton() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          alignSelf: 'stretch',
          marginLeft: Spacing.S,
        }}>
        <View
          style={{
            backgroundColor: SRXColor.LightGray,
            alignSelf: 'stretch',
            width: 1,
            marginRight: Spacing.S,
          }}
        />
        <Button
          textStyle={{fontSize: Typography.Body.fontSize}}
          leftView={
            <FeatherIcon
              name={'message-circle'}
              size={24}
              color={SRXColor.Black}
              style={{marginRight: Spacing.XS / 2}}
            />
          }
          onPress={this.onPressSRXChat}>
          SRX Chat
        </Button>
      </View>
    );
  }

  renderContactInfo() {
    const {post} = this.props;
    if (!ObjectUtil.isEmpty(post)) {
      if (
        !ObjectUtil.isEmpty(post.listing) &&
        !ObjectUtil.isEmpty(post.listing.agentPO)
      ) {
        return this.renderAgentInfo(post.listing.agentPO);
      } else {
        return this.renderPostContactInfo(post);
      }
    }
  }

  renderPostContactInfo(post) {
    if (!ObjectUtil.isEmpty(post.user)) {
      return (
        <View style={{flexDirection: 'row', alignItems: 'center', flex: 1}}>
          <Avatar
            name={post.user.name}
            imageUrl={post.user.photo}
            size={40}
            borderColor={'#DCDCDC'}
            textSize={20}
          />
          <Heading2 style={{marginLeft: Spacing.XS}}>{post.user.name}</Heading2>
        </View>
      );
    }
  }

  renderAgentInfo(agent) {
    return (
      <View style={{flexDirection: 'row', alignItems: 'center', flex: 1}}>
        <Avatar
          name={agent.name}
          imageUrl={agent.photo}
          size={40}
          borderColor={'#DCDCDC'}
          textSize={20}
        />
        <View style={{marginLeft: Spacing.XS}}>
          <Heading2>{agent.name}</Heading2>
          {!ObjectUtil.isEmpty(agent.agencyName) ? (
            <ExtraSmallBodyText style={{color: SRXColor.Gray}}>
              {agent.agencyName}
            </ExtraSmallBodyText>
          ) : null}
        </View>
      </View>
    );
  }

  renderContactBtns() {
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
          marginVertical: Spacing.L,
        }}>
        <Button
          textStyle={Styles.contactButtonTextStyle}
          leftView={
            <Image
              source={Communities_Button_Call}
              style={{width: 20, height: 20, marginRight: Spacing.XS}}
              resizeMode={'contain'}
            />
          }
          onPress={this.callAgent}>
          Call
        </Button>
        <Button
          textStyle={Styles.contactButtonTextStyle}
          leftView={
            <Image
              source={Communities_Button_Whatsapp}
              style={{width: 20, height: 20, marginRight: Spacing.XS}}
              resizeMode={'contain'}
            />
          }
          onPress={this.whatsAppAgent}>
          Whatsapp
        </Button>
        <Button
          textStyle={Styles.contactButtonTextStyle}
          leftView={
            <Image
              source={Communities_Button_SMS}
              style={{width: 20, height: 20, marginRight: Spacing.XS}}
              resizeMode={'contain'}
            />
          }
          onPress={this.smsAgent}>
          SMS
        </Button>
      </View>
    );
  }

  render() {
    return (
      <View style={Styles.modalOverlay}>
        <SafeAreaView
          style={{
            backgroundColor: SRXColor.White,
            borderTopLeftRadius: 10,
            borderTopRightRadius: 10,
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.S,
          }}>
          {this.renderContactInfoAndChatButton()}
          {this.renderContactBtns()}
          <Button
            buttonType={Button.buttonTypes.secondary}
            buttonStyle={{justifyContent: 'center'}}
            onPress={this.onCloseModal}>
            Cancel
          </Button>
        </SafeAreaView>
      </View>
    );
  }
}

const Styles = {
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
    justifyContent: 'flex-end',
  },
  contactButtonTextStyle: {
    color: SRXColor.Black,
    fontSize: 18,
  },
};

CommunityPostContactModal.propTypes = {
  sourceComponentId: PropTypes.string,
  post: PropTypes.instanceOf(CommunityPostPO),
};

export {CommunityPostContactModal};
