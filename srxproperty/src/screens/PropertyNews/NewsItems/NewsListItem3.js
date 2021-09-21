import React, { Component } from "react";
import { StyleSheet, View, Image, Dimensions } from "react-native";
import { SRXColor } from "../../../constants";
import {
  Heading2,
  TouchableHighlight,
  BodyText,
  FeatherIcon,
  SmallBodyText
} from "../../../components";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { Placeholder_General } from "../../../assets";
import PropTypes from "prop-types";
import { OnlineCommunicationPO } from "../../../dataObject";

var { height, width } = Dimensions.get("window");

class NewsListItem3 extends Component {
  static propTypes = {
    onlineCommunicationPO: PropTypes.instanceOf(OnlineCommunicationPO),
    showNewsDetails: PropTypes.func,
    indexNo: PropTypes.number
  };

  static defaultProps = {
    onlineCommunicationPO: null,
    indexNo: 0
  };

  constructor(props) {
    super(props);

    this.onPressItem = this.onPressItem.bind(this);
  }

  //event press
  onPressItem() {
    const { onlineCommunicationPO, showNewsDetails, indexNo } = this.props;
    if (showNewsDetails) {
      showNewsDetails(onlineCommunicationPO, indexNo);
    }
  }

  //Start Rendering methods
  //News Title
  renderNewsTitle() {
    const { onlineCommunicationPO } = this.props;
    return (
      <Heading2 style={{ lineHeight: 22 }}>
        {onlineCommunicationPO.title}
      </Heading2>
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
          paddingTop: Spacing.S
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

  renderContentShort() {
    const { onlineCommunicationPO } = this.props;
    return (
      <BodyText style={{ lineHeight: 22, marginTop: Spacing.S }}>
        {onlineCommunicationPO.contentShort}
      </BodyText>
    );
  }

  render() {
    const { onlineCommunicationPO } = this.props;
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      return (
        <TouchableHighlight onPress={this.onPressItem} style={styles.container}>
          <View style={styles.subContainer}>
            {this.renderNewsImage()}
            <View
              style={{
                paddingHorizontal: Spacing.S,
                paddingBottom: Spacing.S,
                marginTop: Spacing.XS
              }}
            >
              {this.renderNewsTitle()}
              {this.renderSourceAndDatePosted()}
              {this.renderContentShort()}
            </View>
          </View>
        </TouchableHighlight>
      );
    }
    return <View />;
  }
}

const styles = StyleSheet.create({
  container: {
    marginBottom: Spacing.S,
    borderRadius: 8,
    //Shadow for iOS
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { height: 1, width: 1 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  subContainer: {
    overflow: "hidden",
    //Border
    borderWidth: 1,
    borderColor: "#DCDCDC",
    borderRadius: 8,
    //Background
    backgroundColor: SRXColor.White
  },
  imageStyle: {
    width: "100%",
    height: 184
  }
});

export { NewsListItem3 };
