import React, {Component} from 'react';
import {
  View,
  TouchableHighlight,
  Animated,
  Easing,
  Linking,
  Platform,
  PermissionsAndroid,
  Alert,
} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import styles from './styles';
import {Navigation} from 'react-native-navigation';
import {Text, Accordion} from '..';
import {SRXColor, AppConstant, LeadTypes, LeadSources} from '../../constants';
import {AgentPO, ListingPO} from '../../dataObject';
import {Spacing} from '../../styles';
import {ObjectUtil, PermissionUtil, GoogleAnalyticUtil} from '../../utils';
import {FeatherIcon} from '../Icons';
import {TrackingService} from '../../services';
import EnquiryForm from '../../screens/Enquiry/EnquiryForm';
import {ChatRoom} from '../../screens';
import {EnquirySheetSource} from './EnquirySheetSource';

const isIOS = Platform.OS === 'ios';

class Enquiry extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  static propTypes = {
    agentPO: PropTypes.instanceOf(AgentPO),
    listingPO: PropTypes.instanceOf(ListingPO),
    enquiryCallerComponentId: PropTypes.string.isRequired,
    onEnquirySubmitted: PropTypes.func, //functional, call back when enquiry is submitted
    source: PropTypes.oneOf(Object.keys(EnquirySheetSource)),
    conversationId: PropTypes.number,
  };

  static defaultProps = {
    agentPO: null,
    listingPO: null,
    enquiryCallerComponentId: null,
  };

  constructor(props) {
    super(props);
    this.translateY = 80 * 4 + 8;
    this.state = {
      sheetAnim: new Animated.Value(this.translateY),
      activeSections: [0, 2, 3],
    };
  }

  componentDidMount() {
    this.showSheet();
  }

  showSheet = () => {
    Animated.timing(this.state.sheetAnim, {
      toValue: 0,
      duration: 250,
      easing: Easing.out(Easing.ease),
    }).start();
  };

  hideSheet() {
    Animated.timing(this.state.sheetAnim, {
      toValue: this.translateY,
      duration: 100,
    }).start(() => {
      Navigation.dismissOverlay(this.props.componentId);
    });
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

  trackContactAgentActionsForAgentCV({
    subject,
    leadSource,
    feature,
    medium,
    encryptedToUserId,
  }) {
    TrackingService.leadTracking({
      subject,
      leadSource,
      feature,
      medium,
      encryptedToUserId,
    });
  }

  getAgentPO() {
    const {agentPO, listingPO} = this.props;

    if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.agentPO)
    ) {
      return listingPO.agentPO;
    }
    return agentPO;
  }

  callAgentClicked() {
    if (isIOS) {
      this.callAgent();
    } else {
      this.requestAndroidCallPermission();
    }
  }

  callAgent() {
    const {source} = this.props;
    const agentPO = this.getAgentPO();
    if (source == EnquirySheetSource.AgentCV) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.call,
        source: LeadSources.agentCV,
      });
      this.trackContactAgentActionsForAgentCV({
        subject: 12,
        leadSource: 8,
        feature: 12,
        medium: 3,
        encryptedToUserId: agentPO.getAgentId(),
      });
    } else if (source == EnquirySheetSource.ListingDetails) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.call,
        source: LeadSources.listingDetails,
      });
      this.trackContactAgentActionsForListing('C');
    } else if (
      source === EnquirySheetSource.ListingSearch ||
      source === EnquirySheetSource.AgentListings ||
      source === EnquirySheetSource.Shortlist
    ) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.call,
        source: LeadSources.listings, //listing search
      });
      this.trackContactAgentActionsForListing('C');
    }
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      const url = 'tel:' + agentPO.getMobileNumber();

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log("EnquirySheet - call - Can't handle url: " + url);
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'EnquirySheet - An error occurred - call agent - ',
            err,
          ),
        );
    }
    this.hideSheet();
  }

  requestAndroidCallPermission = () => {
    PermissionUtil.requestAndroidCallPermission().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        this.callAgent();
      }
    });
  };

  smsAgentClicked() {
    this.smsAgent();
    // if (isIOS) {
    //   this.smsAgent();
    // } else {
    //   this.requestAndroidSMSPermission();
    // }
  }

  smsAgent() {
    const {listingPO, source} = this.props;
    const agentPO = this.getAgentPO();
    if (source == EnquirySheetSource.AgentCV) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.sms,
        source: LeadSources.agentCV,
      });
      this.trackContactAgentActionsForAgentCV({
        subject: 12,
        leadSource: 8,
        feature: 12,
        medium: 1,
        encryptedToUserId: agentPO.getAgentId(),
      });
    } else if (source == EnquirySheetSource.ListingDetails) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.sms,
        source: LeadSources.listingDetails,
      });
      this.trackContactAgentActionsForListing('S');
    } else if (
      source === EnquirySheetSource.ListingSearch ||
      source === EnquirySheetSource.AgentListings ||
      source === EnquirySheetSource.Shortlist
    ) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.sms,
        source: LeadSources.listings, //listing search
      });
      this.trackContactAgentActionsForListing('S');
    }
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      var url = 'sms:' + agentPO.getMobileNumber(); //+"&body=xxx"
      if (isIOS) {
        url += '&body=';
      } else {
        url += '?body=';
      }

      // const { listingPO, source } = this.props;
      var msgTemplate = '';
      if (!ObjectUtil.isEmpty(listingPO)) {
        msgTemplate = listingPO.getSMSMessageTemplate();
      } else if (
        !ObjectUtil.isEmpty(agentPO) &&
        source == EnquirySheetSource.AgentCV
      ) {
        msgTemplate =
          'Hi ' +
          agentPO.name +
          ', I saw your Agent CV on SRX Property App and wanted to see if you could help me. Thank you.';
      }
      url += msgTemplate;

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log("EnquirySheet - sms - Can't handle url: " + url);
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error('EnquirySheet - An error occurred - sms agent - ', err),
        );
    }
    this.hideSheet();
  }

  // requestAndroidSMSPermission = () => {
  //   PermissionUtil.requestAndroidCallPermission().then(granted => {
  //     if (granted === PermissionsAndroid.RESULTS.GRANTED) {
  //       this.smsAgent();
  //     }
  //   });
  // };

  chatAgent() {
    const {listingPO, conversationId, source} = this.props;
    let agentInfo = this.getAgentPO();
    var msgTemplate = '';

    if (source == EnquirySheetSource.AgentCV) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.SSM,
        source: LeadSources.agentCV,
      });
    } else if (source == EnquirySheetSource.ListingDetails) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.SSM,
        source: LeadSources.listingDetails,
      });
    } else if (
      source === EnquirySheetSource.ListingSearch ||
      source === EnquirySheetSource.AgentListings ||
      source === EnquirySheetSource.Shortlist
    ) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.SSM,
        source: LeadSources.listings, //listing search
      });
      1;
    }

    if (conversationId != null) {
      agentInfo.conversationId = conversationId;
    }

    if (!ObjectUtil.isEmpty(listingPO)) {
      msgTemplate = listingPO.getSMSMessageTemplate();
    }

    Navigation.push(this.props.enquiryCallerComponentId, {
      component: {
        name: 'ChatStack.chatRoom',
        passProps: {
          agentInfo,
          source: ChatRoom.Sources.EnquirySheet,
          msgTemplate,
          listingPO, //to show preview listing
        },
      },
    });
    this.hideSheet();
  }

  whatsAppAgent() {
    const {listingPO, source} = this.props;
    const agentPO = this.getAgentPO();
    if (source == EnquirySheetSource.AgentCV) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.whatsapp,
        source: LeadSources.agentCV,
      });
      this.trackContactAgentActionsForAgentCV({
        subject: 12,
        leadSource: 8,
        feature: 12,
        medium: 6,
        encryptedToUserId: agentPO.getAgentId(),
      });
    } else if (source == EnquirySheetSource.ListingDetails) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.whatsapp,
        source: LeadSources.listingDetails,
      });
      this.trackContactAgentActionsForListing('W');
    } else if (
      source === EnquirySheetSource.ListingSearch ||
      source === EnquirySheetSource.AgentListings ||
      source === EnquirySheetSource.Shortlist
    ) {
      GoogleAnalyticUtil.trackLeads({
        leadType: LeadTypes.whatsapp,
        source: LeadSources.listings, //listing search
      });
      this.trackContactAgentActionsForListing('W');
    }
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      var url =
        'https://api.whatsapp.com/send?phone=65' + agentPO.getMobileNumber(); //+"&text=xxx"

      var msgTemplate = '';
      if (!ObjectUtil.isEmpty(listingPO)) {
        msgTemplate = listingPO.getSMSMessageTemplate();
      } else if (
        !ObjectUtil.isEmpty(agentPO) &&
        source == EnquirySheetSource.AgentCV
      ) {
        msgTemplate =
          'Hi ' +
          agentPO.name +
          ', I saw your Agent CV on SRX Property App and wanted to see if you could help me. Thank you.';
      }

      url += '&text=' + msgTemplate;

      Linking.canOpenURL(url)
        .then(supported => {
          if (!supported) {
            console.log("EnquirySheet - whatsapp - Can't handle url: " + url);
          } else {
            return Linking.openURL(url);
          }
        })
        .catch(err =>
          console.error(
            'EnquirySheet - An error occurred - whatsapp agent - ',
            err,
          ),
        );
    }
    this.hideSheet();
  }

  enquireAgentOrListing = () => {
    this.hideSheet();
    const {agentPO, listingPO, onEnquirySubmitted, source} = this.props;
    const passProps = {
      onSuccess: onEnquirySubmitted,
    };
    if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.agentPO) &&
      source == EnquirySheetSource.ListingDetails
    ) {
      passProps.listingPOs = [listingPO];
      passProps.source = EnquiryForm.Sources.ListingDetails;
    } else if (
      !ObjectUtil.isEmpty(listingPO) &&
      !ObjectUtil.isEmpty(listingPO.agentPO)
    ) {
      passProps.listingPOs = [listingPO];
    } else if (
      !ObjectUtil.isEmpty(agentPO) &&
      source == EnquirySheetSource.AgentCV
    ) {
      passProps.agentPOs = [agentPO];
      passProps.source = EnquiryForm.Sources.AgentCV;
    } else if (!ObjectUtil.isEmpty(agentPO)) {
      passProps.agentPOs = [agentPO];
    }
    Navigation.push(this.props.enquiryCallerComponentId, {
      component: {
        name: 'PropertySearchStack.EnquiryForm',
        passProps: passProps,
      },
    });
  };

  renderEnquiryOptions() {
    return (
      <View style={[styles.roundedBox, {marginBottom: Spacing.XS}]}>
        <Accordion
          expandMultiple={true}
          activeSections={this.state.activeSections}
          sections={['Section 1', 'Section 2', 'Section 3', 'Section 4']}
          renderHeader={(content, index, isActive, sections) =>
            this.renderHeader({content, index, isActive, sections})
          }
          renderContent={(content, index, isActive, sections) =>
            this.renderContent({content, index, isActive, sections})
          }
          sectionContainerStyle={{marginBottom: 1}}
          onChange={newActive => this.sectionChange(newActive)}
          // underlayColor={'black'}
        />
      </View>
    );
  }

  sectionChange(newActiveSections) {
    if (newActiveSections.includes(1)) {
      this.setState({activeSections: [1]});
    } else {
      this.setState({activeSections: [0, 2, 3]});
    }
  }

  renderHeader({content, index, isActive, sections}) {
    if (index == 1) {
      return this.renderMessageOptions(isActive);
    }
    return <View />;
  }

  renderContent({content, index, isActive, sections}) {
    if (index == 0) {
      return this.renderCallOption();
    } else if (index == 1) {
      return (
        <View>
          {this.renderTextMessageOption()}
          {this.renderSRXEnquiryOption()}
        </View>
      );
    } else if (index == 2) {
      return this.renderWhatsAppOption();
    } else if (index == 3) {
      return this.renderChatOption();
    }
  }

  renderCallOption() {
    const agentPO = this.getAgentPO();
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      return (
        <TouchableHighlight onPress={() => this.callAgentClicked()}>
          <View style={styles.buttonBox}>
            <Text style={styles.buttonText}>Call Agent</Text>
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderMessageOptions(isActive) {
    return (
      <View style={[styles.buttonBox, {flexDirection: 'row'}]}>
        <FeatherIcon
          name={'chevron-up'}
          size={20}
          color={SRXColor.Transparent}
          style={{alignSelf: 'center', marginRight: Spacing.XS}}
        />
        <Text style={[styles.buttonText, {flex: 1}]}>Message</Text>
        <FeatherIcon
          name={isActive ? 'chevron-up' : 'chevron-down'}
          size={20}
          color={SRXColor.Black}
          style={{alignSelf: 'center', marginLeft: Spacing.XS}}
        />
      </View>
    );
  }

  renderTextMessageOption() {
    const agentPO = this.getAgentPO();
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      return (
        <TouchableHighlight onPress={() => this.smsAgentClicked()}>
          <View style={[styles.buttonBox, {marginTop: 1}]}>
            <Text style={styles.buttonText}>Text message</Text>
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderSRXEnquiryOption() {
    return (
      <TouchableHighlight onPress={() => this.enquireAgentOrListing()}>
        <View style={[styles.buttonBox, {marginTop: 1}]}>
          <Text style={styles.buttonText}>SRX app enquiry</Text>
        </View>
      </TouchableHighlight>
    );
  }

  renderWhatsAppOption() {
    const agentPO = this.getAgentPO();
    if (
      !ObjectUtil.isEmpty(agentPO) &&
      !ObjectUtil.isEmpty(agentPO.getMobileNumber())
    ) {
      return (
        <TouchableHighlight onPress={() => this.whatsAppAgent()}>
          <View style={styles.buttonBox}>
            <Text style={styles.buttonText}>WhatsApp</Text>
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderChatOption() {
    const agentPO = this.getAgentPO();
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO) && !ObjectUtil.isEmpty(userPO)) {
      return (
        <TouchableHighlight onPress={() => this.chatAgent()}>
          <View style={styles.buttonBox}>
            <Text style={styles.buttonText}>SRX Chat</Text>
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderCancelButton() {
    return (
      <View style={styles.roundedBox}>
        <TouchableHighlight onPress={() => this.hideSheet()}>
          <View style={styles.cancelButtonBox}>
            <Text style={styles.cancelButtonText}>Cancel</Text>
          </View>
        </TouchableHighlight>
      </View>
    );
  }

  render() {
    const {sheetAnim} = this.state;
    return (
      <SafeAreaView style={styles.overlay}>
        <Animated.View
          style={{
            height: this.translateY,
            transform: [{translateY: sheetAnim}],
            margin: 8,
            justifyContent: 'flex-end',
          }}>
          {this.renderEnquiryOptions()}
          {this.renderCancelButton()}
        </Animated.View>
      </SafeAreaView>
    );
  }
}

// const Enquiry.Sources = EnquirySheetSource;

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  null,
)(Enquiry);
