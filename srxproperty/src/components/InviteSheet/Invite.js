import React, { Component } from "react";
import {
  View,
  TouchableHighlight,
  Animated,
  Easing,
  Linking,
  Platform,
  PermissionsAndroid,
} from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import { Navigation } from "react-native-navigation";
import { ShareDialog } from 'react-native-fbsdk';

import { AppConstant } from "../../constants";
import styles from "./styles";
import { Text } from "..";
import { PermissionUtil } from "../../utils";

const isIOS = Platform.OS === "ios";

class Invite extends Component {

  constructor(props) {
    super(props);
    this.translateY = 66 * 5 + 10;
    this.state = {
      sheetAnim: new Animated.Value(this.translateY),
      activeSections: [0, 2]
    };
  }

  componentDidMount() {
    this.showSheet();
  }

  showSheet = () => {
    Animated.timing(this.state.sheetAnim, {
      toValue: 0,
      duration: 250,
      easing: Easing.out(Easing.ease)
    }).start();
  };

  hideSheet() {
    Animated.timing(this.state.sheetAnim, {
      toValue: this.translateY,
      duration: 100
    }).start(() => {
      Navigation.dismissOverlay(this.props.componentId);
    });
  }

  //Start Invitations
  inviteEmail = () => {
    //need invite description
    const url = "mailto:?subject=SendMail&body=Description";

    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log("Invite Sheet :: cannot handle Email URL " + url);
        } else {
          return Linking.openURL(url);
        }
      })
      .catch(err =>
        console.error("Invite Sheet :: An error occured", err)
      );
  }

  inviteSMS = () => {
    this.sendSMS();
    // if (isIOS) {
    //   this.sendSMS();
    // } else {
    //   this.requestAndroidSMSPermission();
    // }
  }

  sendSMS = () => {
    //need invite description
    const url = "sms:"; //+"&body=xxx"

    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log("Invite Sheet :: cannot handle Email URL " + url);
        } else {
          return Linking.openURL(url);
        }
      })
      .catch(err =>
        console.error("Invite Sheet - An error occurred - sms agent - ", err)
      );
  }

  // requestAndroidSMSPermission = () => {
  //   PermissionUtil.requestAndroidCallPermission().then(granted => {
  //     if (granted === PermissionsAndroid.RESULTS.GRANTED) {
  //       this.sendSMS();
  //     }
  //   });
  // };

  inviteWhatsapp = () => {

    //just added for a while
    var inviteDescription = "Hi! Please download srx property app.."
    var url = "https://api.whatsapp.com/send?text=" + inviteDescription;

    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log("Invite Sheet :: cannot handle Email URL" + url);
        } else {
          return Linking.openURL(url);
        }
      })
      .catch(err =>
        console.error("Invite Sheet - An error occurred - whatsapp agent - ", err)
      );
  }

  inviteFacebook = () => {

    // Build up a shareable link.
    const shareLinkContent = {
      contentType: 'link',
      contentUrl: AppConstant.DownloadSourceURL,
      contentDescription: 'Description : SRX Property app on facebook',
    };

    ShareDialog.canShow(shareLinkContent).then(
      function (canShow) {
        if (canShow) {
          return ShareDialog.show(shareLinkContent);
        }
      }
    ).then(
      function (result) {
        if (result.isCancelled) {
          console.log('Share cancelled');
        } else {
          console.log('Share success with postId: '
            + result.postId);
        }
      },
      function (error) {
        console.log('Share fail with error: ' + error);
      }
    );
    //end of share on facebook
  }

  renderInviteOptions() {
    return (
      <View style={styles.roundedBox}>
        {this.renderEmailOption()}
        {this.renderSMSOption()}
        {this.renderWhatsAppOption()}
        {this.renderFacbookOption()}
      </View>
    )
  }

  renderEmailOption() {
    return (
      <TouchableHighlight onPress={() => this.inviteEmail()}>
        <View style={[styles.buttonBox, { marginTop: 1 }]}>
          <Text style={styles.buttonText}>Email</Text>
        </View>
      </TouchableHighlight>
    )
  }

  renderSMSOption() {
    return (
      <TouchableHighlight onPress={() => this.inviteSMS()}>
        <View style={[styles.buttonBox, { marginTop: 1 }]}>
          <Text style={styles.buttonText}>SMS</Text>
        </View>
      </TouchableHighlight>
    )
  }

  renderWhatsAppOption() {
    return (
      <TouchableHighlight onPress={() => this.inviteWhatsapp()}>
        <View style={[styles.buttonBox, { marginTop: 1 }]}>
          <Text style={styles.buttonText}>WhatsApp</Text>
        </View>
      </TouchableHighlight>
    );
  }

  renderFacbookOption() {
    return (
      <TouchableHighlight onPress={() => this.inviteFacebook()}>
        <View style={[styles.buttonBox, { marginTop: 1 }]}>
          <Text style={styles.buttonText}>Facebook</Text>
        </View>
      </TouchableHighlight>
    )
  }

  renderCancelButton() {
    return (
      <View style={[styles.roundedBox, { marginTop: 3 }]}>
        <TouchableHighlight onPress={() => this.hideSheet()}>
          <View style={styles.cancelButtonBox}>
            <Text style={styles.cancelButtonText}>Cancel</Text>
          </View>
        </TouchableHighlight>
      </View>
    );
  }

  render() {
    const { sheetAnim } = this.state;
    return (
      <SafeAreaView style={styles.overlay}>
        <Animated.View
          style={{
            height: this.translateY,
            transform: [{ translateY: sheetAnim }],
            margin: 8,
            justifyContent: "flex-end"
          }}>
          {this.renderInviteOptions()}
          {this.renderCancelButton()}
        </Animated.View>
      </SafeAreaView>
    );
  }
}

export { Invite };
