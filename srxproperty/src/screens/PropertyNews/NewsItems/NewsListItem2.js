import React, { Component } from "react";
import { StyleSheet, View, Image } from "react-native";
import { SRXColor } from "../../../constants";
import {
  Separator,
  Heading2,
  BodyText,
  SmallBodyText,
  FeatherIcon,
  TouchableHighlight
} from "../../../components";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { Placeholder_General } from "../../../assets";
import PropTypes from "prop-types";
import { OnlineCommunicationPO } from "../../../dataObject";

class NewsListItem2 extends Component {
  static propTypes = {
    onlineCommunicationPO: PropTypes.instanceOf(OnlineCommunicationPO)
  };

  static defaultProps = {
    onlineCommunicationPO: null
  };

  constructor(props) {
    super(props);
    this.showNewsDetails = this.showNewsDetails.bind(this);
  }

  showNewsDetails = () => {
    const {
      showNewsDetails,
      onlineCommunicationPO,
      indexNo,
      newsSearchOption,
      categoryTypeDescription
    } = this.props;
    if (showNewsDetails) {
      showNewsDetails(
        onlineCommunicationPO,
        indexNo,
        newsSearchOption,
        categoryTypeDescription
      );
    }
  };

  //Start Rendering methods
  renderLeftAndRightContent() {
    const { onlineCommunicationPO } = this.props;
    let imageUrl = onlineCommunicationPO.getImageSmallUrl();
    if (!ObjectUtil.isEmpty(imageUrl)) {
      return (
        <View style={{ flexDirection: "row", flex: 1 }}>
          {this.renderLeftContent()}
          {this.renderRightContent()}
        </View>
      );
    } else {
      // there is no image ( imageUrl )=> show news title and content short
      return <View>{this.renderNewsTitleAndContentShort()}</View>;
    }
  }

  //Left Content
  renderLeftContent() {
    return (
      <View
        style={[
          styles.imageContainerStyle,
          styles.imageContainerBorder,
          { backgroundColor: SRXColor.White, padding: Spacing.XS }
        ]}
      >
        {this.renderNewsImage()}
      </View>
    );
  }

  //Listing Image
  renderNewsImage() {
    const { onlineCommunicationPO } = this.props;
    let imageUrl = onlineCommunicationPO.getImageSmallUrl();

    if (!ObjectUtil.isEmpty(imageUrl)) {
      return (
        <Image
          style={styles.imageStyle}
          defaultSource={Placeholder_General}
          source={{ uri: imageUrl }}
          resizeMode={"cover"}
        />
      );
    }
  }

  renderNewsTitleAndContentShort() {
    return (
      <View style={{ flex: 1 }}>
        {this.renderNewsTitle()}
        {this.renderContentShort()}
      </View>
    );
  }

  //Right Content
  renderRightContent() {
    return (
      <View style={{ flex: 1, paddingLeft: Spacing.XS }}>
        {this.renderNewsTitle()}
        {this.renderContentShort()}
      </View>
    );
  }

  //News Title
  renderNewsTitle() {
    const { onlineCommunicationPO } = this.props;
    return (
      <Heading2 style={{ lineHeight: 22 }}>
        {onlineCommunicationPO.title}
      </Heading2>
    );
  }

  //Content Short
  renderContentShort() {
    const { onlineCommunicationPO } = this.props;
    return (
      <View style={{ marginTop: Spacing.XS }}>
        <BodyText style={{ lineHeight: 22 }} numberOfLines={3}>
          {onlineCommunicationPO.contentShort}
        </BodyText>
      </View>
    );
  }

  renderSourceAndDatePosted() {
    return (
      <View style={{ flex: 1, flexDirection: "row", marginTop: Spacing.XS }}>
        {this.renderNewsSource()}
        {this.renderDatePosted()}
      </View>
    );
  }

  //News Source
  renderNewsSource() {
    const { onlineCommunicationPO } = this.props;
    return (
      <SmallBodyText
        numberOfLines={1}
        style={{ color: SRXColor.Gray, lineHeight: 22 }}
      >
        {onlineCommunicationPO.source}
      </SmallBodyText>
    );
  }

  //News Date Posted
  renderDatePosted() {
    const { onlineCommunicationPO } = this.props;
    return (
      <View
        style={{
          flex: 1,
          flexDirection: "row",
          justifyContent: "flex-end"
        }}
      >
        <FeatherIcon name={"clock"} size={20} color={"black"} />
        <SmallBodyText
          numberOfLines={1}
          style={{
            color: SRXColor.Gray,
            lineHeight: 22,
            paddingLeft: Spacing.XS
          }}
        >
          {onlineCommunicationPO.datePosted}
        </SmallBodyText>
      </View>
    );
  }

  render() {
    const { onlineCommunicationPO, containerStyle } = this.props;
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      return (
        <TouchableHighlight onPress={() => this.showNewsDetails()}>
          <View style={{ backgroundColor: SRXColor.White }}>
            <View style={[styles.mainContainer, containerStyle]}>
              {this.renderLeftAndRightContent()}
              {this.renderSourceAndDatePosted()}
            </View>
            <Separator />
          </View>
        </TouchableHighlight>
      );
    }
    return <View />;
  }
}

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
    paddingVertical: Spacing.S,
    paddingHorizontal: Spacing.M
  },
  imageContainerStyle: {
    backgroundColor: SRXColor.White,
    overflow: "hidden",
    width: 90 + Spacing.XS * 2,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  imageContainerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  },
  imageStyle: {
    borderRadius: 5,
    width: 90,
    height: "100%"
  }
});

export { NewsListItem2 };
