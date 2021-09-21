import React, { Component } from "react";
import { View, Image } from "react-native";
import PropTypes from "prop-types";
import { TouchableHighlight, BodyText } from "../../../components";
import { ObjectUtil } from "../../../utils";
import { Spacing } from "../../../styles";
import { SRXColor } from "../../../constants";

class MenuListItem extends Component {
  constructor(props) {
    super(props);

    this.onSelected = this.onSelected.bind(this);
  }

  onSelected() {
    const { item, onItemSelected } = this.props;
    if (onItemSelected) {
      onItemSelected(item);
    }
  }

  renderImage() {
    const { item } = this.props;
    if (!ObjectUtil.isEmpty(item.icon)) {
      if (React.isValidElement(item.icon)) {
        return item.icon;
      } else {
        return (
          <Image
            style={{ height: 20, width: 20, marginRight: Spacing.M }}
            source={item.icon}
            resizeMode={"contain"}
          />
        );
      }
    }
  }
  renderTitle() {
    const { item, titleStyle } = this.props;
    const additionalTitleStyle = {};
    if (!ObjectUtil.isEmpty(item.title)) {
      if (!ObjectUtil.isEmpty(item.color)) {
        additionalTitleStyle.color = item.color;
      }
      return (
        <BodyText style={[additionalTitleStyle, titleStyle]}>
          {item.title}
        </BodyText>
      );
    }
  }

  render() {
    const { containerStyle } = this.props;
    return (
      <TouchableHighlight onPress={this.onSelected}>
        <View
          style={[
            {
              flex: 1,
              flexDirection: "row",
              alignItems: "center",
              padding: Spacing.M,
              backgroundColor: SRXColor.White,
              minHeight: 51
            },
            containerStyle
          ]}
        >
          {this.renderImage()}
          {this.renderTitle()}
        </View>
      </TouchableHighlight>
    );
  }
}

MenuListItem.propTypes = {
  item: PropTypes.object.isRequired,
  /**
   * style of item content
   */
  containerStyle: PropTypes.object,
  /**
   * Style of title text component
   */
  titleStyle: PropTypes.object,

  onItemSelected: PropTypes.func
};

export { MenuListItem };
