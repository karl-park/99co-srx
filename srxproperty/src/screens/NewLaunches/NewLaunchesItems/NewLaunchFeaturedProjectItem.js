import React, { Component } from "react";
import { Image, View, TouchableHighlight } from "react-native";
import PropTypes from "prop-types";
import { Navigation } from "react-native-navigation";

import {
  Placeholder_General,
  Listings_XDroneIcon_Blue,
  Listings_V360Icon_Blue
} from "../../../assets";
import { Heading2, SmallBodyText } from "../../../components";
import { ProjectDetailPO } from "../../../dataObject";
import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";
import { Styles } from "../Styles";

class NewLaunchFeaturedProjectItem extends Component {
  static propTypes = {
    projectDetailPO: PropTypes.instanceOf(ProjectDetailPO)
  };

  viewProjectDetail = () => {
    const { projectDetailPO } = this.props;
    let projectURL = projectDetailPO.getProjectUrl();
    let projectName = projectDetailPO.getProjectName();

    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.generalWebScreen",
        passProps: {
          url: projectURL,
          screenTitle: projectName
        }
      }
    });
  };

  renderProjectImage() {
    const { projectDetailPO } = this.props;
    let imageUrl = projectDetailPO.getProjectImageUrl();

    return (
      <Image
        style={Styles.largeImageStyle}
        defaultSource={Placeholder_General}
        source={{ uri: imageUrl }}
        resizeMode={"cover"}
      />
    );
  }

  renderProjectNameAndPrice() {
    const { projectDetailPO } = this.props;
    return (
      <View style={Styles.largeEachRowContainerStyle}>
        <Heading2 style={{ lineHeight: 22 }}>
          {projectDetailPO.getProjectName()}
        </Heading2>
        <View
          style={[
            Styles.largeEachRowContainerStyle,
            { justifyContent: "flex-end" }
          ]}
        >
          <Heading2 style={{ color: SRXColor.Purple, lineHeight: 22 }}>
            {projectDetailPO.getProjectPriceRange()}
          </Heading2>
        </View>
      </View>
    );
  }

  renderProjectTypeAndIcon() {
    const { projectDetailPO } = this.props;
    return (
      <View style={Styles.largeEachRowContainerStyle}>
        <SmallBodyText style={{ lineHeight: 22, color: SRXColor.Gray }}>
          {projectDetailPO.getProjectTypeDescription()}
        </SmallBodyText>
        <View
          style={[
            Styles.largeEachRowContainerStyle,
            { justifyContent: "flex-end" }
          ]}
        >
          <Image
            style={{ width: 26, height: 25 }}
            source={Listings_XDroneIcon_Blue}
            resizeMode={"contain"}
          />
        </View>
      </View>
    );
  }

  renderProjectAddressAndIcon() {
    const { projectDetailPO } = this.props;
    return (
      <View style={Styles.largeEachRowContainerStyle}>
        <SmallBodyText style={{ lineHeight: 22, color: SRXColor.Gray }}>
          {projectDetailPO.getProjectAddress()}
        </SmallBodyText>
        <View
          style={[
            Styles.largeEachRowContainerStyle,
            { justifyContent: "flex-end" }
          ]}
        >
          <Image
            style={{ width: 26, height: 25 }}
            source={Listings_V360Icon_Blue}
            resizeMode={"contain"}
          />
        </View>
      </View>
    );
  }

  render() {
    return (
      <TouchableHighlight
        style={{ flex: 1 }}
        underlayColor={SRXColor.AccordionBackground}
        onPress={() => this.viewProjectDetail()}
      >
        <View
          style={{
            flex: 1,
            margin: Spacing.M
          }}
        >
          {this.renderProjectImage()}
          <View style={Styles.largeItemSubContainerStyle}>
            {this.renderProjectNameAndPrice()}
            {this.renderProjectTypeAndIcon()}
            {this.renderProjectAddressAndIcon()}
          </View>
        </View>
      </TouchableHighlight>
    );
  }
}

export { NewLaunchFeaturedProjectItem };
