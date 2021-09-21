import React, { Component } from "react";
import { View, SafeAreaView, StyleSheet, Linking } from "react-native";
import { TestimonialPO } from "../../../dataObject";
import { Spacing } from "../../../styles";
import PropTypes from "prop-types";
import { BodyText, Button, FontAwesomeIcon } from "../../../components";
import { SRXColor } from "../../../constants";
import HTML from "react-native-render-html";
import { ObjectUtil } from "../../../utils";

class AgentTestimonialCardItem extends Component {
  static propTypes = {
    testimonialPO: PropTypes.instanceOf(TestimonialPO)
  };

  static defaultProps = {
    testimonialPO: null
  };

  //initial heights below refers to the heights that are retrieved after first render, which is the full size of the view.
  //The current design need to be changed if the feature is shown immediately after render(you can see it without scrolling)
  state = {
    expandTestimonial: false,
    initialExternalHeight: 0,
    externalHeight: 0
  };

  onSelectShowHideText = () => {
    this.setState({ expandTestimonial: !this.state.expandTestimonial });
  };

  setExternalHeight = height => {
    if (this.state.initialExternalHeight == 0) {
      this.setState({ initialExternalHeight: height });
    } else {
      this.setState({ externalHeight: height });
    }
  };

  render() {
    const { testimonialPO } = this.props;
    const {
      expandTestimonial,
      externalHeight,
      initialExternalHeight
    } = this.state;
    //isInitialHeightChange is used to check whether the initial view height is retrieved and is the height larger than the restricted height
    const isInitialHeightChange = initialExternalHeight > externalHeight;
    return (
      <View style={styles.testimonialStyle}>
        <View style={styles.quoteLeftBox}>
          <FontAwesomeIcon
            name={"quote-left"}
            size={25}
            color={SRXColor.White}
            style={{ alignSelf: "center" }}
          />
        </View>
        <View style={{ margin: Spacing.XS }}>
          <BodyText style={styles.textStyle}>
            {testimonialPO.clientName}
          </BodyText>
          <View
            style={[
              initialExternalHeight == 0
                ? null
                : expandTestimonial
                ? null
                : { height: 70 },
              {
                width: 200,
                overflow: "hidden",
                alignSelf: "center"
              }
            ]}
            onLayout={({
              nativeEvent: {
                layout: { x, y, width, height }
              }
            }) => this.setExternalHeight(height)}
          >
            <View style={{ marginBottom: Spacing.XS }}>
              <HTML
                html={testimonialPO.testimonial}
                onLinkPress={(event, href) => {
                  Linking.openURL(href);
                }}
                baseFontStyle={{
                  fontSize: 16,
                  color: SRXColor.Black,
                  textAlign: "center"
                }}
              />
            </View>
          </View>
          <View style={{ alignItems: "center", margin: Spacing.S }}>
            {isInitialHeightChange || expandTestimonial ? (
              <Button
                style={{ color: SRXColor.Teal }}
                onPress={() => this.onSelectShowHideText()}
              >
                {expandTestimonial ? "View Less" : "View More"}
              </Button>
            ) : null}
          </View>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  testimonialStyle: {
    width: 270,
    elevation: 2,
    shadowColor: SRXColor.Black,
    shadowOffset: { width: 2, height: 2 },
    shadowRadius: 2,
    shadowOpacity: 0.5,
    borderWidth: 1,
    borderColor: SRXColor.LightGray,
    borderRadius: 5,
    marginRight: 15,
    backgroundColor: SRXColor.White
  },
  quoteLeftBox: {
    margin: 10,
    width: 40,
    height: 40,
    backgroundColor: SRXColor.Teal,
    justifyContent: "center",
    borderRadius: 5
  },
  textStyle: {
    textAlign: "center",
    margin: Spacing.XS
  }
});

export { AgentTestimonialCardItem };
