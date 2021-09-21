import React, {Component} from 'react';
import {View, Alert, KeyboardAvoidingView} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';

//Custom Components
import {Heading2, TextInput, Button} from '../../../components';
import {Spacing} from '../../../styles';
import {SRXColor, AlertMessage, IS_IOS} from '../../../constants';
import {CommunitiesStack} from '../../../config';
import {LoginService} from '../../../services';
import {ObjectUtil, CommonUtil, StringUtil} from '../../../utils';

class VerifyMobileModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  state = {
    mobileNum: '',
    closeModal: false,
  };

  constructor(props) {
    super(props);
  }

  componentWillUnmount() {
    const {closeModal} = this.state;
    if (closeModal) {
      this.directToVerfiyOTPModal();
    }
  }

  directToVerfiyOTPModal = () => {
    const {mobileNum} = this.state;
    const passProps = {
      mobile: mobileNum,
    };
    CommunitiesStack.showOTPVerificationModal(passProps);
  };

  onCloseModal = () => {
    return Navigation.dismissModal(this.props.componentId);
  };

  updateMobileNumber(mobile) {
    let newMobile = StringUtil.decimalValue(mobile, 0);
    if (newMobile == '0') {
      this.setState({
        mobileNum: '',
      });
    } else {
      this.setState({
        mobileNum: newMobile,
      });
    }
  }

  onPressAddButton = () => {
    const {mobileNum} = this.state;

    LoginService.requestMobileVerification({
      countryCode: '+65',
      mobile: mobileNum,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === 'success') {
                this.setState({closeModal: true}, () => this.onCloseModal());
              }
            }
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  };

  renderVerifyMobileModal() {
    return (
      <View>
        {this.renderText()}
        {this.renderButton()}
      </View>
    );
  }

  renderText() {
    const {mobileNum} = this.state;
    return (
      <View>
        <Heading2 style={{lineHeight: 22}}>
          You need to add your mobile number to engage in community feeds
        </Heading2>
        <View style={{marginVertical: Spacing.M}}>
          <TextInput
            keyboardType={'phone-pad'}
            placeholder={'Enter your mobile number'}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={mobileNum}
            onChangeText={text => this.updateMobileNumber(text)}
          />
        </View>
      </View>
    );
  }

  renderButton() {
    const {mobileNum} = this.state;
    const canVerify = !ObjectUtil.isEmpty(mobileNum) && mobileNum.length >= 8;
    return (
      <View style={{flexDirection: 'row', marginVertical: Spacing.M}}>
        <View style={{flex: 1, marginRight: Spacing.S}}>
          <Button
            buttonStyle={{
              alignItems: 'center',
              justifyContent: 'center',
            }}
            buttonType={Button.buttonTypes.secondary}
            onPress={() => this.onCloseModal()}>
            Cancel
          </Button>
        </View>

        <View style={{flex: 1, marginLeft: Spacing.S}}>
          <Button
            buttonStyle={{
              alignItems: 'center',
              justifyContent: 'center',
            }}
            buttonType={Button.buttonTypes.primary}
            disabled={!canVerify}
            onPress={() => this.onPressAddButton()}>
            Add
          </Button>
        </View>
      </View>
    );
  }

  render() {
    if (IS_IOS) {
      return (
        <SafeAreaView
          style={Styles.modalOverlay}
          forceInset={{bottom: 'never'}}>
          <KeyboardAvoidingView
            style={{flex: 1, justifyContent: 'flex-end'}}
            behavior="padding">
            <View style={Styles.container}>
              {this.renderVerifyMobileModal()}
            </View>
          </KeyboardAvoidingView>
        </SafeAreaView>
      );
    } else {
      return (
        <View style={Styles.modalOverlay}>
          <KeyboardAwareScrollView
            style={{position: 'absolute', bottom: 0, left: 0, right: 0}}
            keyboardShouldPersistTaps={'always'}>
            <View style={Styles.container}>
              {this.renderVerifyMobileModal()}
            </View>
          </KeyboardAwareScrollView>
        </View>
      );
    }
  }
}

const Styles = {
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  container: {
    paddingHorizontal: Spacing.M,
    paddingTop: Spacing.L,
    backgroundColor: SRXColor.White,
    borderTopLeftRadius: 10,
    borderTopRightRadius: 10,
  },
};

export {VerifyMobileModal};
