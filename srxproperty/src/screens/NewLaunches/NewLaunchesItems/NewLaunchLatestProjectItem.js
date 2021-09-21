import React, { Component } from "react";
import { ImageBackground, View, TouchableHighlight, Image } from "react-native";
import PropTypes from "prop-types";
import { Navigation } from "react-native-navigation";

import {
  Placeholder_General,
  Listings_XDroneIcon_Blue,
  Listings_V360Icon_Blue
} from "../../../assets";
import { ProjectDetailPO } from "../../../dataObject";
import { Heading2, SmallBodyText, Separator } from "../../../components";
import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";
import { Styles } from "../Styles";

class NewLaunchLatestProjectItem extends Component {
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

  //Render Image
  renderImageLayout() {
    const { projectDetailPO } = this.props;
    let imageUrl = projectDetailPO.getProjectImageUrl();
    return (
      <View style={{ flex: 1 }}>
        <ImageBackground
          style={{ width: "100%", height: 108 }}
          defaultSource={Placeholder_General}
          source={{ uri: imageUrl }}
          resizeMode={"cover"}
        >
          {this.renderV360DroneRow()}
        </ImageBackground>
      </View>
    );
  }

  renderV360DroneRow() {
    return (
      <View
        style={[Styles.droneContainer, { margin: 5, alignSelf: "flex-end" }]}
      >
        <Image
          style={{ height: 20, width: 20 }}
          resizeMode={"contain"}
          source={Listings_XDroneIcon_Blue}
        />
      </View>
    );
  }

  renderProjectInfoLayout() {
    return (
      <View
        style={{ flex: 2, paddingLeft: Spacing.XS, paddingRight: Spacing.M }}
      >
        {this.renderProjectName()}
        {this.renderProjectType()}
        {this.renderProjectAddress()}
        {this.renderProjectPrice()}
      </View>
    );
  }

  renderProjectName() {
    const { projectDetailPO } = this.props;
    return (
      <Heading2 style={{ lineHeight: 22 }}>
        {projectDetailPO.getProjectName()}
      </Heading2>
    );
  }

  renderProjectType() {
    const { projectDetailPO } = this.props;
    return (
      <SmallBodyText style={{ lineHeight: 22, color: SRXColor.Gray }}>
        {projectDetailPO.getProjectTypeDescription()}
      </SmallBodyText>
    );
  }

  renderProjectAddress() {
    const { projectDetailPO } = this.props;
    return (
      <SmallBodyText style={{ lineHeight: 22, color: SRXColor.Gray }}>
        {projectDetailPO.getProjectAddress()}
      </SmallBodyText>
    );
  }

  renderProjectPrice() {
    const { projectDetailPO } = this.props;
    return (
      <Heading2 style={{ color: SRXColor.Purple, lineHeight: 22 }}>
        {projectDetailPO.getProjectPriceRange()}
      </Heading2>
    );
  }

  renderSeparator() {
    return <Separator />;
  }

  render() {
    return (
      <TouchableHighlight
        style={{ flex: 1 }}
        underlayColor={SRXColor.AccordionBackground}
        onPress={() => this.viewProjectDetail()}
      >
        <View style={{ flex: 1 }}>
          <View
            style={{
              flex: 1,
              flexDirection: "row",
              margin: Spacing.M
            }}
          >
            {this.renderImageLayout()}
            {this.renderProjectInfoLayout()}
          </View>
          {this.renderSeparator()}
        </View>
      </TouchableHighlight>
    );
  }
}

export { NewLaunchLatestProjectItem };
