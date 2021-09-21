import React, { Component } from "react";
import { View, Dimensions, Platform, TextInput, Image } from "react-native";
import PropTypes from "prop-types";

import { Button, Ionicons } from "../../../components";
import { SRXColor } from "../../../constants";
import { TopBarStyle, Spacing } from "../../../styles";
import { InputStyles } from "../../../styles";
import { AppTopBar_BackBtn } from "../../../assets";

var { width } = Dimensions.get("window");

const isIOS = Platform.OS === "ios";

const DirectorySource = {
  ChatHome: "ChatHome",
  ConciergeHome: "ConciergeHome"
};

class AgentDirectoryTopBar extends Component {
  constructor(props) {
    super(props);
  }

  //updated search bar
  searchBarUpdated = ({ text }) => {
    const { searchBarUpdated } = this.props;
    if (searchBarUpdated) {
      searchBarUpdated({ text });
    }
  };

  componentDidMount() {
    const { source } = this.props;
    if (source === DirectorySource.ChatHome) {
      this.searchBar.focus();
    }
  }

  render() {
    const { onBackPressed } = this.props;
    return (
      <View
        style={[
          TopBarStyle.topBar,
          {
            height: 50,
            width: width - 2 * Spacing.XS,
            paddingLeft: 0
          }
        ]}
      >
        {/* Back Button */}
        <Button
          buttonStyle={styles.buttonContainer}
          leftView={<Image source={AppTopBar_BackBtn} resizeMode={"contain"} />}
          onPress={onBackPressed}
        />

        <TextInput
          ref={component => (this.searchBar = component)}
          style={[
            InputStyles.singleLineTextHeight,
            {
              flex: 1,
              borderRadius: 20,
              //color
              backgroundColor: SRXColor.Purple,
              color: SRXColor.White,
              //margin
              marginLeft: Spacing.XS / 2,
              //padding
              paddingRight: Spacing.S,
              paddingLeft: 20
            }
          ]}
          selectionColor={SRXColor.White}
          placeholder={
            "Enter Agent Name, Mobile No, Email or CEA Registration No."
          }
          placeholderTextColor={SRXColor.LightGray}
          autoCorrect={false}
          autoFocus={false}
          clearButtonMode="while-editing"
          underlineColorAndroid="transparent"
          returnKeyType={"search"}
          onChangeText={text =>
            this.searchBarUpdated({
              text
            })
          }
        />
      </View>
    );
  }
}

const styles = {
  buttonContainer: {
    height: "100%",
    alignItems: "center"
  }
};
AgentDirectoryTopBar.propTypes = {
  /**
   * action while back is pressed
   */
  onBackPressed: PropTypes.func,

  /** change search bar text  */
  searchBarUpdated: PropTypes.func
};

export { AgentDirectoryTopBar };
