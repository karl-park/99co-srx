import React, { Component } from "react";
import {
  StyleSheet,
  View,
  Dimensions,
  Linking,
  Platform,
  Share,
  TouchableOpacity
} from "react-native";
import { SRXColor } from "../../../constants";
import {
  Heading2,
  SmallBodyText,
  FeatherIcon,
  Html
} from "../../../components";
import { Spacing, Typography } from "../../../styles";
import { ObjectUtil, StringUtil } from "../../../utils";
import PropTypes from "prop-types";
import { OnlineCommunicationPO } from "../../../dataObject";

const isIOS = Platform.OS === "ios";
var { height, width } = Dimensions.get("window");

class NewsDetails extends Component {
  static propTypes = {
    onlineCommunicationPO: PropTypes.instanceOf(OnlineCommunicationPO)
  };

  static defaultProps = {
    onlineCommunicationPO: null
  };

  constructor(props) {
    super(props);
  }

  shareNews = () => {
    const { onlineCommunicationPO } = this.props;
    let msg =
      "Hey there! Check out this news I found on SRX… what do you think?😊";
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      const url = onlineCommunicationPO.getNewsURL();
      if (!ObjectUtil.isEmpty(url)) {
        msg = msg + " " + url;
      }
    }

    const content = {
      message: msg
    };
    const options = {
      dialogTitle: "Share News" //iOS only
    };

    Share.share(content, options);
  };

  //Start Rendering methods
  //News Title
  renderNewsTitle() {
    const { onlineCommunicationPO } = this.props;
    return (
      <View style={[styles.mainContainer, { flexDirection: "row" }]}>
        <View style={{ flex: 12 }}>
          <Heading2 style={{ lineHeight: 22 }}>
            {onlineCommunicationPO.title}
          </Heading2>
        </View>
        <View
          style={{
            justifyContent: "flex-end",
            flex: 1
          }}
        >
          <TouchableOpacity onPress={this.shareNews}>
            {isIOS ? (
              <FeatherIcon name={"share"} size={24} color={"black"} />
            ) : (
              <FeatherIcon name={"share-2"} size={24} color={"black"} />
            )}
          </TouchableOpacity>
        </View>
      </View>
    );
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

  
  renderNewsContent() {
    const { content } = this.props.onlineCommunicationPO;
    const contentText = StringUtil.replace(content, "</p>", "<br>");
    return (
      <View>
        <View style={styles.mainContainer}>
          <Html
            imagesMaxWidth={width - 32}
            html={contentText}
            tagsStyles={styles}
            onLinkPress={(event, href) => {
              this.props.directWebScreen(href);
              // Linking.openURL(href);
            }}
          />
        </View>
        <View
          style={{
            marginLeft: 0,
            marginRight: 0,
            marginTop: 0,
            marginBottom: 0,
            height: 10,
            backgroundColor: "#e0e0e0"
          }}
        />
      </View>
    );
  }

  render() {
    const { onlineCommunicationPO } = this.props;
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      return (
        <View style={{ backgroundColor: SRXColor.White }}>
          {this.renderNewsTitle()}
          {this.renderSourceAndDatePosted()}
          {this.renderNewsContent()}
        </View>
      );
    }
    return <View />;
  }
}

const styles = StyleSheet.create({
  mainContainer: {
    paddingVertical: Spacing.S,
    paddingHorizontal: Spacing.M
  },

  p: {
    lineHeight: 26,
    ...Typography.Body
  },
  div: {
    lineHeight: 26,
    ...Typography.Body
  },
  img: {
    height: 214
  }
});

export { NewsDetails };
