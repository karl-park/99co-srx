import React, { Component } from "react";
import { View, Image, TouchableOpacity } from "react-native";
import PropTypes from "prop-types";

import { MyProperties_ListedTag } from "../../../../assets";
import { BodyText, FontAwesome5Icon, Button } from "../../../../components";
import { SRXColor, pt_Occupancy } from "../../../../constants";
import { Spacing, Typography } from "../../../../styles";
import { CommonUtil, ObjectUtil } from "../../../../utils";
import { SRXPropertyUserPO } from "../../../../dataObject";

class InviterAgencyInfo extends Component {
  componentDidUpdate(prevProps) {
    if (prevProps.trackerPO !== this.props.trackerPO) {
      // this.updateIndicatorText();
    }
  }

  render() {
    const { trackerPO, chatWithAgent } = this.props;
    if (!ObjectUtil.isEmpty(trackerPO)) {
      const { inviterAgentPO } = trackerPO;
      if (!ObjectUtil.isEmpty(inviterAgentPO)) {
        const { minimalPO } = inviterAgentPO;
        const { name, photo, userId } = minimalPO;
        let imageUri = CommonUtil.handleImageUrl(photo);
        return (
          <View style={{ paddingHorizontal: Spacing.M, marginTop: Spacing.M }}>
            <View style={{ flexDirection: "row" }}>
              <Image
                source={{ uri: imageUri }}
                style={{
                  width: 50,
                  height: 50,
                  borderRadius: 25,
                  borderWidth: 1,
                  borderColor: "#DCDCDC",
                  alignSelf: "center"
                }}
                resizeMode={"cover"}
              />
              <View
                style={{
                  paddingLeft: Spacing.XS,
                  alignSelf: "center",
                  flex: 1
                }}
              >
                <BodyText>Invited By</BodyText>
                <BodyText>{name}</BodyText>
              </View>
              <View style={{ flexDirection: "row", justifyContent: "center" }}>
                <Button
                  textStyle={[Typography.SmallBody, Styles.blueColorText]}
                  leftView={
                    <FontAwesome5Icon
                      name={"comment-alt"}
                      size={20}
                      color={SRXColor.Teal}
                      style={[
                        {
                          transform: [{ rotateY: "180deg" }]
                        }
                      ]}
                    />
                  }
                  onPress={() => chatWithAgent()}
                >
                  Chat with Agent
                </Button>
              </View>
            </View>
          </View>
        );
      }
    }
    return <View />;
  }
}

InviterAgencyInfo.propTypes = {
  trackerPO: PropTypes.instanceOf(SRXPropertyUserPO),
  chatWithAgent: PropTypes.func
  /**function to callback for navigating to ChatRoom */
};

const Styles = {
  blueColorText: {
    fontWeight: "400",
    color: SRXColor.Teal,
    marginLeft: Spacing.XS
  }
};

export { InviterAgencyInfo };
