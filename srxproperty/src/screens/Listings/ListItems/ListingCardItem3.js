import React, { Component } from "react";
import { StyleSheet, Image, View } from "react-native";
import PropTypes from "prop-types";
import { Placeholder_General } from "../../../assets";
import {
  BodyText,
  Subtext,
  Heading2_Currency,
  TouchableHighlight
} from "../../../components";
import { SRXColor } from "../../../constants";
import { ListingPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";

class ListingCardItem3 extends Component {
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
          style={styles.containerStyle}
          onPress={() => {
            if (onSelected) onSelected(listingPO);
          }}
        >
          <View style={styles.subContainer}>
            {this.renderListingImage()}
            <View style={{ paddingVertical: Spacing.XS }}>
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
    width: 136 + Spacing.XS * 2,
    borderRadius: 5,
    //Shadow
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { height: 1, width: 1 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  },
  subContainer: {
    flex: 1,
    overflow: "hidden",
    padding: Spacing.XS,
    //Border
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0",
    //alignment
    // justifyContent: "center",
    // alignItems: "center",
    //background
    backgroundColor: SRXColor.White
  },
  imageStyle: {
    borderRadius: 5,
    width: 136,
    height: 88
  }
});

export { ListingCardItem3 };
