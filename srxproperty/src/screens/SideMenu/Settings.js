import React, { Component } from "react";
import { View, ScrollView, Switch } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import { Navigation } from "react-native-navigation";

import {
  Separator,
  Heading2,
  SmallBodyText,
  FeatherIcon
} from "../../components";
import { Styles } from "./Styles";
import { SRXColor } from "../../constants";
import { LoginService } from "../../services";
import { ObjectUtil, CommonUtil } from "../../utils";

class Settings extends Component {
  state = {
    iseNewsLetter: null,
    isPromotions: null,
    isCommunityMsg: null
  };

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
  }

  componentDidMount() {
    this.setupTopBar();
    this.loadUserSettings();
  }

  setupTopBar() {
    FeatherIcon.getImageSource("x", 25, "black").then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          leftButtons: [
            {
              id: "setting_close",
              icon: icon_close
            }
          ],
          title: {
            text: "Settings",
            alignment: "center"
          }
        }
      });
    });
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "setting_close") {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  onSwitchChangeENewsLetter = value => {
    const { isPromotions, isCommunityMsg } = this.state;
    this.updateUserSettings(value, isPromotions, isCommunityMsg);
  };

  onSwitchChangePromotions = value => {
    const { iseNewsLetter, isCommunityMsg } = this.state;
    this.updateUserSettings(iseNewsLetter, value, isCommunityMsg);
  };

  onSwitchChangeCommunity = value => {
    const { iseNewsLetter, isPromotions } = this.state;
    this.updateUserSettings(iseNewsLetter, isPromotions, value);
  };

  loadUserSettings() {
    LoginService.loadUserSettings()
      .catch(error => {
        console.log(error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            console.log(error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (!ObjectUtil.isEmpty(response.result)) {
                let settingObject = response.result;
                this.setState({
                  iseNewsLetter: settingObject.enewsletterInd,
                  isPromotions: settingObject.promotionsInd,
                  isCommunityMsg: settingObject.communityMessagesInd
                });
              }
            }
          }
        } else {
          console.log(error);
        }
      });
  }

  updateUserSettings(iseNewsLetter, isPromotions, isCommunityMsg) {
    LoginService.updateUserSettings({
      enewsletterInd: iseNewsLetter,
      promotionsInd: isPromotions,
      communityMessagesInd: isCommunityMsg
    })
      .catch(error => {
        console.log(error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            console.log(error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result == "success") {
                this.loadUserSettings();
              }
            }
          }
        } else {
          console.log(error);
        }
      });
  }

  renderSettingListForm() {
    return (
      <View style={{ flex: 1 }}>
        <View
          style={[
            Styles.eachSettingContainer,
            { backgroundColor: SRXColor.White }
          ]}
        >
          <Heading2>Email Notifications</Heading2>
        </View>
        {this.renderSeparator()}
        {this.renderENewsLetter()}
        {this.renderSeparator()}
        {this.renderPromotions()}
        {this.renderSeparator()}
        {this.renderCommunity()}
        {this.renderSeparator()}
      </View>
    );
  }

  renderENewsLetter() {
    const { iseNewsLetter } = this.state;
    if (iseNewsLetter || !iseNewsLetter) {
      return (
        <View
          style={[
            Styles.eachSettingContainer,
            { backgroundColor: SRXColor.AccordionBackground }
          ]}
        >
          <View style={{ flex: 1 }}>
            <SmallBodyText>eNewsletter</SmallBodyText>
          </View>
          <Switch
            onTintColor={iseNewsLetter ? SRXColor.Teal : SRXColor.White}
            thumbTintColor={SRXColor.White}
            value={iseNewsLetter}
            onValueChange={value => this.onSwitchChangeENewsLetter(value)}
          />
        </View>
      );
    }
  }

  renderPromotions() {
    const { isPromotions } = this.state;
    if (isPromotions || !isPromotions) {
      return (
        <View
          style={[
            Styles.eachSettingContainer,
            { backgroundColor: SRXColor.White }
          ]}
        >
          <View style={{ flex: 1 }}>
            <SmallBodyText>Promotions</SmallBodyText>
          </View>
          <Switch
            onTintColor={isPromotions ? SRXColor.Teal : SRXColor.White}
            thumbTintColor={SRXColor.White}
            value={isPromotions}
            onValueChange={value => this.onSwitchChangePromotions(value)}
          />
        </View>
      );
    }
  }

  renderCommunity() {
    const { isCommunityMsg } = this.state;
    if (isCommunityMsg || !isCommunityMsg) {
      return (
        <View
          style={[
            Styles.eachSettingContainer,
            { backgroundColor: SRXColor.AccordionBackground }
          ]}
        >
          <View style={{ flex: 1 }}>
            <SmallBodyText>Community messages</SmallBodyText>
          </View>
          <Switch
            onTintColor={isCommunityMsg ? SRXColor.Teal : SRXColor.White}
            thumbTintColor={SRXColor.White}
            value={isCommunityMsg}
            onValueChange={value => this.onSwitchChangeCommunity(value)}
          />
        </View>
      );
    }
  }

  renderSeparator() {
    return <Separator />;
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.DarkBlue }}
        forceInset={{ bottom: "never" }}
      >
        <SafeAreaView
          style={{ flex: 1, backgroundColor: "white" }}
          forceInset={{ bottom: "never" }}
        >
          <ScrollView>{this.renderSettingListForm()}</ScrollView>
        </SafeAreaView>
      </SafeAreaView>
    );
  }
}

export { Settings };
