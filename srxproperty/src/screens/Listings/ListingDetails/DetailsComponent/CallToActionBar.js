import React, {Component} from 'react';
import {
  View,
  StyleSheet,
  TouchableOpacity,
  Image,
  PermissionsAndroid,
  SafeAreaView,
  ActionSheetIOS,
} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {
  BodyText,
  OcticonsIcon,
  FeatherIcon,
  AndroidOptionDialog,
} from '../../../../components';
import {
  IS_IOS,
  SRXColor,
  LeadTypes,
  LeadSources,
  PHONE_OPTIONS,
  PhoneOptions,
} from '../../../../constants';
import {Spacing} from '../../../../styles';
import {Listing_WhatsApp} from '../../../../assets';
import {ListingPO} from '../../../../dataObject';
import {
  CommonUtil,
  ObjectUtil,
  PermissionUtil,
  GoogleAnalyticUtil,
} from '../../../../utils';
import {Navigation} from 'react-native-navigation';
import {LoginStack} from '../../../../config';
import {TrackingService} from '../../../../services';

class CallToActionBar extends Component {
  static propTypes = {
    /***
     * Get Agent Information to calls
     */
    listingPO: PropTypes.instanceOf(ListingPO),
  };

  constructor(props) {
    super(props);

    this.onClickPhone = this.onClickPhone.bind(this);
    this.onClickWhatsApp = this.onClickWhatsApp.bind(this);
    this.onClickSRXChat = this.onClickSRXChat.bind(this);
    this.onSelectOption = this.onSelectOption.bind(this);
    this.openSMS = this.openSMS.bind(this);
  }

  trackContactAgentActionsForListing(type) {
    /**
     * type
     * C - call, S - Sms, W - whatsapp
     */
    const {listingPO} = this.props;
    if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.getListingId())
    ) {
      TrackingService.trackListingCallSMSOrWhatsapp({
        listingId: listingPO.getListingId(),
        type,
      }); //no need response
    }
  }

  onSelectOption(item) {
    if (!ObjectUtil.isEmpty(item)) {
      if (item.value == PhoneOptions.Call) {
        if (IS_IOS) {
          this.dialPhoneNumber();
        } else {
          this.requestAndroidCallPermission();
        }
      } else if (item.value == PhoneOptions.Sms) {
        this.openSMS();
      }
    }
  }

  onClickPhone() {
    if (IS_IOS) {
      ActionSheetIOS.showActionSheetWithOptions(
        {
          options: ['Cancel', 'SMS', 'Call'],
          cancelButtonIndex: 0,
        },
        buttonIndex => {
          if (buttonIndex === 1) {
            this.openSMS();
          } else if (buttonIndex === 2) {
            this.dialPhoneNumber();
          }
        },
      );
    } else {
      AndroidOptionDialog.show({
        options: PHONE_OPTIONS,
        onSelectOption: this.onSelectOption,
      });
    }
  }

  requestAndroidCallPermission = () => {
    PermissionUtil.requestAndroidCallPermission().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.dialPhoneNumber();
      }
    });
  };

  openSMS() {
    const {listingPO} = this.props;
    var mobileNumber = '';
    var msgTemplate = '';

    if (!ObjectUtil.isEmpty(listingPO)) {
      msgTemplate = listingPO.getSMSMessageTemplate();
      const {agentPO} = listingPO;
      if (!ObjectUtil.isEmpty(agentPO)) {
        mobileNumber = agentPO.getMobileNumber();
      }
      //Google Tracking
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.sms,
        source: LeadSources.listingDetails,
      });

      this.trackContactAgentActionsForListing('S'); //For SMS

      CommonUtil.openSMS({
        phoneNumber: mobileNumber,
        message: msgTemplate,
      });
    } //end
  }

  dialPhoneNumber() {
    const {listingPO} = this.props;
    var mobileNumber = '';
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {agentPO} = listingPO;
      if (!ObjectUtil.isEmpty(agentPO)) {
        mobileNumber = agentPO.getMobileNumber();
      }
      //tracking for call
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.call,
        source: LeadSources.listingDetails,
      });
      this.trackContactAgentActionsForListing('C'); //For Calls
      //Dialing phone number
      CommonUtil.dialPhoneNumber({phoneNumber: mobileNumber});
    } //end of dialing phone number
  }

  onClickWhatsApp() {
    const {listingPO} = this.props;
    var msgTemplate = '';
    var mobileNumber = '';
    if (!ObjectUtil.isEmpty(listingPO)) {
      msgTemplate = listingPO.getSMSMessageTemplate();
      const {agentPO} = listingPO;
      if (!ObjectUtil.isEmpty(agentPO)) {
        mobileNumber = agentPO.getMobileNumber();
      }

      //For tracking of whatsApp
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.whatsapp,
        source: LeadSources.agentCV,
      });
      this.trackContactAgentActionsForListing('W');

      //Open WhatsApp
      CommonUtil.openWhatsApp({
        phoneNumber: mobileNumber,
        message: msgTemplate,
      });
    } //end of listingPO check
  }

  onClickSRXChat() {
    const {listingPO, userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      if (!ObjectUtil.isEmpty(listingPO)) {
        const {agentPO} = listingPO;
        if (!ObjectUtil.isEmpty(agentPO)) {
          //Tracking for srx agent
          GoogleAnalyticUtil.trackLeads({
            leadType: LeadTypes.SSM,
            source: LeadSources.listingDetails,
          });
          Navigation.push(this.props.componentId, {
            component: {
              name: 'ChatStack.chatRoom',
              passProps: {
                agentInfo: agentPO,
                msgTemplate: listingPO.getSMSMessageTemplate(),
                listingPO: listingPO,
              },
            },
          });
        }
      }
    } else {
      LoginStack.showSignInRegisterModal();
    }
  }

  renderPhone() {
    return (
      <View style={{flex: 1}}>
        <TouchableOpacity
          style={style.btnContainer}
          onPress={this.onClickPhone}>
          <FeatherIcon name={'smartphone'} size={24} color={SRXColor.White} />
          <BodyText style={style.textStyle}>Phone</BodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderWhatsApp() {
    return (
      <View style={{flex: 1}}>
        <TouchableOpacity
          style={style.btnContainer}
          onPress={this.onClickWhatsApp}>
          <Image
            style={{height: 24, width: 24}}
            resizeMode={'contain'}
            source={Listing_WhatsApp}
          />
          <BodyText style={style.textStyle}>WhatsApp</BodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderSRXChat() {
    return (
      <View style={{flex: 1, alignItems: 'flex-end'}}>
        <TouchableOpacity
          style={style.btnContainer}
          onPress={this.onClickSRXChat}>
          <OcticonsIcon
            name={'comment'}
            size={24}
            color={SRXColor.White}
            style={[
              {
                transform: [{rotateY: '180deg'}],
                marginTop: Spacing.XS / 4,
              },
            ]}
          />
          <BodyText style={style.textStyle}>SRX Chat</BodyText>
        </TouchableOpacity>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={style.safeAreaContainer}>
        <View style={style.container}>
          {this.renderPhone()}
          {this.renderWhatsApp()}
          {this.renderSRXChat()}
        </View>
      </SafeAreaView>
    );
  }
}

const style = StyleSheet.create({
  safeAreaContainer: {
    backgroundColor: SRXColor.Purple,
  },
  container: {
    paddingTop: Spacing.L,
    paddingBottom: IS_IOS ? Spacing.M : Spacing.L,
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    flexDirection: 'row',
  },
  btnContainer: {
    flexDirection: 'row',
  },
  textStyle: {
    color: SRXColor.White,
    marginLeft: Spacing.XS,
  },
});

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(CallToActionBar);
