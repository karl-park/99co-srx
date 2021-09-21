import React, { Component } from "react";
import { StyleSheet, Image, View, TouchableHighlight } from "react-native";
import PropTypes from "prop-types";
import { Placeholder_General } from "../../../assets";
import { BodyText, Subtext, Heading2_Currency } from "../../../components";
import { SRXColor } from "../../../constants";
import { ListingPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";

class ListingCardItem extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO)
  };

  renderListingImage = () => {
    const { listingPO } = this.props;
    let imageUrl = listingPO.getListingImageUrl();
    return (
      <Image
        style={styles.imageStyle}
        defaultSource={Placeholder_General}
        source={{ uri: imageUrl }}
        resizeMode={"cover"}
      />
    );
  };

  render() {
    const { listingPO, onSelected } = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      return (
        <TouchableHighlight
          style={[styles.containerStyle, styles.containerBorder]}
          onPress={() => {
            if (onSelected) onSelected(listingPO);
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
                {listingPO.getListingName()}
              </BodyText>
              <Subtext numberOfLines={1}>{listingPO.getPropertyType()}</Subtext>
              <Heading2_Currency numberOfLines={1}>
                {listingPO.getAskingPrice()}
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

const styles = StyleSheet.create({
  containerStyle: {
    backgroundColor: SRXColor.White,
    overflow: 'hidden',
    width: 136 + Spacing.XS * 2,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  },
  imageStyle: {
    borderRadius: 5,
    width: 136,
    height: 88
  }
});

export { ListingCardItem };
