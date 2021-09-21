import React, { Component } from "react";
import {
  View,
  TextInput,
  StyleSheet,
  ViewPropTypes,
  Platform
} from "react-native";
import PropTypes from "prop-types";

isIOS = Platform.OS === "ios";

//For confirmation otp inputs
class ConfirmationCodeInput extends Component {
  static propTypes = {
    /**For Auto Filling verfication codes */
    verificationCode: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

    /*Length of confirmation code or number of input cells*/
    codeLength: PropTypes.number,

    /*Size of input cell */
    width: PropTypes.number,

    height: PropTypes.number,

    /*Space between two cells*/
    space: PropTypes.number,

    /*Width of cell borders*/
    cellBorderWidth: PropTypes.number,

    /*Color of cells when active*/
    activeColor: PropTypes.string,

    /*Color of cells when inactive*/
    inactiveColor: PropTypes.string,

    /*Code Input Style */
    codeInputStyle: TextInput.propTypes.style,

    /*Container Style */
    containerStyle: ViewPropTypes.style,

    /*Callback function*/
    onFulfill: PropTypes.func
  };

  //Default Value of propsTypes
  static defaultProps = {
    verificationCode: "",
    codeLength: 6,
    width: 40,
    height: 40,
    space: 8,
    cellBorderWidth: 1,
    activeColor: "rgba(255, 255, 255, 1)",
    inactiveColor: "rgba(255, 255, 255, 0.2)"
  };

  constructor(props) {
    super(props);

    this.state = {
      codeArr: new Array(this.props.codeLength).fill(""),
      currentIndex: 0
    };

    this.codeInputRefs = [];
  }

  componentDidUpdate(prevProps) {
    if (prevProps.verificationCode !== this.props.verificationCode) {
      const { verificationCode } = this.props;
      if (verificationCode) {
        this.setState({ codeArr: this.autoFillVerificationCode() }, () => {
          this.onFocus(this.state.codeArr.length - 1, true);
        });
      }
    }
  }

  //Auto Filling Verification Code
  autoFillVerificationCode(fullNewCode) {
    const { codeLength, verificationCode } = this.props;

    let returedCodeArray = new Array(this.props.codeLength).fill("");

    let autoFilledCode = "";
    if (verificationCode) {
      autoFilledCode = verificationCode;
    } else {
      autoFilledCode = fullNewCode;
    }

    if (autoFilledCode) {
      if (autoFilledCode.length === codeLength) {
        returedCodeArray = autoFilledCode.split("");

        console.log("ReturnedArray is :: " + returedCodeArray);
      }
    }
    return returedCodeArray;
  }

  //Clear Array
  clear() {
    this.setState({
      codeArr: new Array(this.props.codeLength).fill(""),
      currentIndex: 0
    });

    this.setFocus(0);
  }

  //Focus on code input refs
  setFocus(index) {
    this.codeInputRefs[index].focus();
  }

  blur(index) {
    this.codeInputRefs[index].blur();
  }

  onFocus(index, autoFilled) {
    const { codeArr, codeLength } = this.state;
    let newCodeArr = [...Array.from(codeArr)];

    const currentEmptyIndex = newCodeArr.findIndex(c => !c);

    console.log(currentEmptyIndex);
    console.log("Current Index empty is...");
    if (currentEmptyIndex !== -1 && currentEmptyIndex < index) {
      return this.setFocus(currentEmptyIndex);
    } else if (currentEmptyIndex === -1 && autoFilled) {
      //It is for auto filled codes
      const code = newCodeArr.join("");
      this.onSubmitVerificationCode(code);
      return;
    }

    //for clearing all arrays
    if (!autoFilled) {
      for (const i in newCodeArr) {
        if (i >= index) {
          newCodeArr[i] = "";
        }
      }
    }

    this.setState({
      codeArr: newCodeArr,
      currentIndex: index
    });
  }

  onInputCode(character, index) {
    const { codeLength } = this.props;
    const { codeArr } = this.state;

    if (character.length === codeLength) {
      this.setState(
        {
          codeArr: this.autoFillVerificationCode(character)
        },
        () => {
          this.onFocus(this.state.codeArr.length - 1, true);
        }
      );
    } else if (character.length === 1) {
      let newCodeArr = [...Array.from(codeArr)];
      newCodeArr[index] = character;

      if (index == codeLength - 1) {
        const code = newCodeArr.join("");
        this.blur(this.state.currentIndex);
        this.onSubmitVerificationCode(code);
      } else {
        this.setFocus(this.state.currentIndex + 1);
      }

      this.setState(prevState => {
        return {
          codeArr: newCodeArr,
          currentIndex: prevState.currentIndex + 1
        };
      });
    }
  }

  onKeyPress(e) {
    if (e.nativeEvent.key === "Backspace") {
      const { currentIndex } = this.state;
      const nextIndex = currentIndex > 0 ? currentIndex - 1 : 0;
      this.setFocus(nextIndex);
    }
  }

  //When Return key pressed,
  onSubmitVerificationCode = code => {
    const { onFulfill, codeLength } = this.props;
    if (code.length === codeLength) {
      if (onFulfill) {
        onFulfill(code);
      }
    }
  };

  getEachTextInputBoxStyle(active) {
    const { space, activeColor, cellBorderWidth, inactiveColor } = this.props;

    let classStyle = {
      //center
      marginRight: space / 2,
      marginLeft: space / 2,

      //Active Color
      color: activeColor,

      //Box Style with full border
      borderWidth: cellBorderWidth,
      borderColor: active ? activeColor : inactiveColor
    };

    return classStyle;
  }

  getContainerStyle() {
    const { width, height } = this.props;
    let classStyle = {
      height,
      // width,
      justifyContent: "center"
    };
    return classStyle;
  }

  render() {
    const {
      codeLength,
      codeInputStyle,
      containerStyle,
      width,
      height,
      activeColor
    } = this.props;

    const initialCodeInputStyle = {
      width,
      height
    };

    let codeInputs = [];
    for (let i = 0; i < codeLength; i++) {
      const id = i;
      codeInputs.push(
        <TextInput
          {...this.props}
          key={id}
          ref={ref => (this.codeInputRefs[id] = ref)}
          style={[
            styles.codeInput,
            initialCodeInputStyle,
            this.getEachTextInputBoxStyle(this.state.currentIndex == id),
            codeInputStyle
          ]}
          underlineColorAndroid="transparent"
          selectionColor={activeColor}
          keyboardType={isIOS ? "number-pad" : "numeric"}
          returnKeyType={"done"}
          autoFocus={true}
          onFocus={() => this.onFocus(id)}
          value={
            this.state.codeArr[id] ? this.state.codeArr[id].toString() : ""
          }
          maxLength={isIOS ? codeLength : 1}
          onChangeText={text => this.onInputCode(text, id)}
          onKeyPress={e => this.onKeyPress(e)}
          textContentType="oneTimeCode"
        />
      );
    }
    //
    return (
      <View
        style={[styles.container, this.getContainerStyle(), containerStyle]}
      >
        {codeInputs}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row"
  },
  codeInput: {
    backgroundColor: "transparent",
    textAlign: "center",
    padding: 0
  }
});

export { ConfirmationCodeInput };
