import React, { Component } from "react";
import { Image, View, StyleSheet } from "react-native";
import PropTypes from "prop-types";

import { Placeholder_General } from "../../../assets";
import { BodyText, TouchableHighlight } from "../../../components";
import { ProjectDetailPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { SRXColor } from "../../../constants";

class HotProjectsCardItem extends Component {
  static defaultProps = {
    projectDetailPO: null
  };

  constructor(props) {
    super(props);
    this.onPressItem = this.onPressItem.bind(this);
  }

  //event press
  onPressItem() {
    const { projectDetailPO, onPressHotProjectItem } = this.props;
    if (onPressHotProjectItem) {
      onPressHotProjectItem(projectDetailPO);
    }
  }

  //start rendering methods
  renderHotProjectImage() {
    const { projectDetailPO } = this.props;
    let imageUrl = projectDetailPO.getProjectImageUrl();
    return (
      <Image
        style={{ width: "100%", height: 100 }}
        defaultSource={Placeholder_General}
        source={{ uri: imageUrl }}
        resizeMode={"cover"}
      />
    );
  }

  renderHotProjectName() {
    const { projectDetailPO } = this.props;
    return (
      <BodyText
        numberOfLines={2}
        style={{
          flex: 1,
          overflow: "hidden",
          lineHeight: 20,
          marginHorizontal: Spacing.XS,
          marginVertical: Spacing.S,
          textAlign: "center"
        }}
      >
        {projectDetailPO.getProjectName()}
      </BodyText>
    );
  }

  render() {
    const { projectDetailPO, containerStyle } = this.props;
    if (!ObjectUtil.isEmpty(projectDetailPO)) {
      return (
        <TouchableHighlight
          style={[styles.container, containerStyle]}
          onPress={this.onPressItem}
        >
          <View style={[styles.subContainer]}>
            {this.renderHotProjectImage()}
            {this.renderHotProjectName()}
          </View>
        </TouchableHighlight>
      );
    }
    return <View />;
  }
}

HotProjectsCardItem.propTypes = {
  /**project details PO */
  projectDetailPO: PropTypes.instanceOf(ProjectDetailPO),

  /**container Style */
  containerStyle: PropTypes.oneOf([PropTypes.object, PropTypes.array]),

  /** select hot project item to direct to
   * New Launches Web Screen */
  onPressHotProjectItem: PropTypes.func
};

const styles = StyleSheet.create({
  container: {
    marginTop: Spacing.M,
    borderRadius: 8,
    //Shadow for iOS
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { height: 1, width: 1 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  subContainer: {
    flex: 1,
    overflow: "hidden",
    //Border
    borderRadius: 8,
    borderWidth: 1,
    borderColor: "#e0e0e0",
    //alignment
    justifyContent: "center",
    alignItems: "center",
    //background
    backgroundColor: SRXColor.White
  }
});

export { HotProjectsCardItem };
