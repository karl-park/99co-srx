import React, { Component } from "react";
import { StyleSheet, View, Image } from "react-native";
import { SRXColor } from "../../../constants";
import {
  Heading2,
  TouchableHighlight,
  FeatherIcon,
  SmallBodyText
} from "../../../components";
import { Spacing } from "../../../styles";
import { ObjectUtil } from "../../../utils";
import { Placeholder_General } from "../../../assets";
import PropTypes from "prop-types";
import { OnlineCommunicationPO } from "../../../dataObject";

class NewsListItem4 extends Component {
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
  renderNewsImageAndInfo() {
    return (
      <View style={{ flexDirection: "row", flex: 1 }}>
        {this.renderNewsImage()}
        {this.renderNewsTitleDatePostedAndSource()}
      </View>
    );
  }

  //News Image
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

  renderNewsTitleDatePostedAndSource() {
    const { onlineCommunicationPO } = this.props;
    return (
      <View
        style={{
          flex: 1,
          flexDirection: "column",
          justifyContent: "space-between"
        }}
      >
        {this.renderNewsTitle()}
        {this.renderDatePosted()}
        {this.renderNewsSource()}
      </View>
    );
  }

  //News Title
  renderNewsTitle() {
    const { onlineCommunicationPO } = this.props;
    return (
      <Heading2
        style={{
          lineHeight: 22,
          flex: 1,
          paddingHorizontal: Spacing.XS,
          paddingTop: Spacing.XS
        }}
      >
        {onlineCommunicationPO.title}
      </Heading2>
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
          justifyContent: "flex-end",
          paddingRight: Spacing.S,
          paddingTop: Spacing.XS
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

  //News Source
  renderNewsSource() {
    const { onlineCommunicationPO } = this.props;
    return (
      <View
        style={{
          flex: 1,
          flexDirection: "row",
          justifyContent: "flex-end",
          paddingRight: Spacing.XS
        }}
      >
        <SmallBodyText
          numberOfLines={1}
          style={{ color: SRXColor.Gray, lineHeight: 22 }}
        >
          {onlineCommunicationPO.source}
        </SmallBodyText>
      </View>
    );
  }

  render() {
    const { onlineCommunicationPO, indexNo } = this.props;
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      return (
        <TouchableHighlight
          style={styles.container}
          onPress={this.onPressItem}
          key={indexNo}
        >
          <View style={styles.subContainer}>
            {this.renderNewsImageAndInfo()}
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
    width: 127,
    height: "100%"
  }
});

export { NewsListItem4 };
