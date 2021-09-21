import React, { Component } from "react";
import { View, SafeAreaView, Alert, ScrollView, Image } from "react-native";
import { Navigation } from "react-native-navigation";
import { MyProperties_Condo, Listing_CertifiedIcon } from "../../../assets";
import {
  LargeTitleComponent,
  SmallBodyText,
  BodyText,
  FeatherIcon,
  OcticonsIcon,
  Subtext
} from "../../../components";
import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";

const list = [
  {
    key: "point-1",
    text: "Certify listing of your property posted by agents on SRX"
  },
  {
    key: "point-2",
    text: "Certified listings get higher ranking"
  },
  {
    key: "point-3",
    text: "Be notified of interest in your property (coming soon)"
  },
  {
    key: "point-4",
    text: "Access unique financial products (coming soon)"
  },
  {
    key: "point-5",
    text: "And much more!"
  }
];

class VerifyOwnershipIntro extends LargeTitleComponent {
  constructor(props) {
    super(props);

    this.addRightButton();

    Navigation.events().bindComponent(this);
  }

  addRightButton() {
    FeatherIcon.getImageSource("x", 25, "black").then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          rightButtons: [
            {
              id: "dismiss",
              icon: icon_close
            }
          ]
        }
      });
    });
  }

  navigationButtonPressed({ buttonId }) {
    if (buttonId === "dismiss") {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  renderListItem(item) {
    return (
      <View
        style={{
          flexDirection: "row",
          paddingBottom: Spacing.L
        }}
      >
        <Image
          style={{ height: 18, width: 18 }}
          resizeMode={"contain"}
          source={Listing_CertifiedIcon}
        />
        <BodyText style={{ flex: 1 }}>{item.text}</BodyText>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1 }}>
        <ScrollView
          scrollEventThrottle={1}
          style={{ backgroundColor: SRXColor.White, flex: 1 }}
          onScroll={this.onScroll}
        >
          {this.renderLargeTitle("Verify Ownership")}
          <View style={{ marginTop: Spacing.L, paddingHorizontal: Spacing.M }}>
            <BodyText style={{ marginBottom: Spacing.M }}>
              Verifying that you own the property gives you exclusive access to
              new and upcoming features and insights.
            </BodyText>
            <View
              style={{
                backgroundColor: SRXColor.White,
                borderRadius: 5,
                borderWidth: 1,
                borderColor: "#e0e0e0",
                paddingVertical: Spacing.L,
                paddingHorizontal: 20,
                shadowColor: "rgb(110,129,154)",
                shadowOffset: { width: 1, height: 2 },
                shadowOpacity: 0.32,
                shadowRadius: 1
              }}
            >
              <View style={{ alignItems: "center", marginBottom: 27 }}>
                <View style={{ flexDirection: "row" }}>
                  <Image
                    source={MyProperties_Condo}
                    style={{
                      width: 26,
                      height: 26,
                      marginLeft: 18 + Spacing.XS,
                      marginRight: Spacing.XS
                    }}
                    resizeMode={"contain"}
                  />
                  <Image
                    style={{ height: 18, width: 18 }}
                    resizeMode={"contain"}
                    source={Listing_CertifiedIcon}
                  />
                </View>
              </View>
              {list.map(item => this.renderListItem(item))}
            </View>
            <Subtext style={{ fontStyle: "italic", marginTop: 19 }}>
              Your NRIC/passport is only used to verify your ownership. We don’t
              store it or share it anywhere.
            </Subtext>
          </View>
        </ScrollView>
      </SafeAreaView>
    );
  }
}

export { VerifyOwnershipIntro };
