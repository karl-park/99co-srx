import React, { Component } from "react";
import { Image, View, TouchableHighlight } from "react-native";
import PropTypes from "prop-types";

import { Placeholder_General } from "../../../assets";
import { BodyText, Subtext, Heading2_Currency } from "../../../components";
import { SRXColor } from "../../../constants";
import { ProjectDetailPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { Styles } from "../Styles";

class NewLaunchesCardItem extends Component {
  static propTypes = {
    projectDetailPO: PropTypes.instanceOf(ProjectDetailPO)
  };

  renderListingImage = () => {
    const { projectDetailPO } = this.props;

    let imageUrl = projectDetailPO.getProjectImageUrl();

    return (
      <Image
        style={Styles.imageStyle}
        defaultSource={Placeholder_General}
        source={{ uri: imageUrl }}
        resizeMode={"cover"}
      />
    );
  };

  render() {
    const { projectDetailPO, onSelected } = this.props;
    if (!ObjectUtil.isEmpty(projectDetailPO)) {
      return (
        <TouchableHighlight
          style={[Styles.containerStyle, Styles.containerBorder]}
          onPress={() => {
            if (onSelected) onSelected(projectDetailPO);
          }}
        >
          <View
            style={{ backgroundColor: SRXColor.White, padding: Spacing.XS }}
          >
            <View style={{ marginBottom: Spacing.XS }}>
              {this.renderListingImage()}
            </View>
            <View>
              <BodyText numberOfLines={1}>
                {projectDetailPO.getProjectName()}
              </BodyText>
              <Subtext numberOfLines={1}>{projectDetailPO.getProjectTypeDescription()}</Subtext>
              <Heading2_Currency numberOfLines={1}>
                {projectDetailPO.getProjectPriceRange()}
              </Heading2_Currency>
            </View>
          </View>
        </TouchableHighlight>
      );
    } else {
      return <View />;
    }
  }
}

export { NewLaunchesCardItem };