import React, { Component } from "react";
import { StyleSheet, View, Image, Dimensions } from "react-native";
import { SRXColor } from "../../../constants";
import {
  Separator,
  Heading2,
  SmallBodyText,
  FeatherIcon,
  TouchableHighlight
} from "../../../components";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { Placeholder_General } from "../../../assets";
import PropTypes from "prop-types";
import { OnlineCommunicationPO } from "../../../dataObject";

var { height, width } = Dimensions.get("window");

class NewsListItem1 extends Component {
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
  //News Title
  renderNewsTitle() {
    const { onlineCommunicationPO } = this.props;
    return (
      <View style={styles.mainContainer}>
        <Heading2 style={{ lineHeight: 22 }} numberOfLines={2}>
          {onlineCommunicationPO.title}
        </Heading2>
      </View>
    );
  }

  //News Image
  renderNewsImage() {
    const { onlineCommunicationPO } = this.props;
    let imageUrl = onlineCommunicationPO.getImageUrl();

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

  renderSourceAndDatePosted() {
    return (
      <View
        style={{
          flex: 1,
          flexDirection: "row",
          paddingVertical: Spacing.S,
          paddingHorizontal: Spacing.M
        }}
      >
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
    const { onlineCommunicationPO } = this.props;
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      return (
        <TouchableHighlight onPress={() => this.showNewsDetails()}>
          <View style={{ backgroundColor: SRXColor.White, flex: 1 }}>
            {this.renderNewsTitle()}
            {this.renderNewsImage()}
            {this.renderSourceAndDatePosted()}
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
  imageStyle: {
    width: "100%",
    height: 214
  }
});

export { NewsListItem1 };
