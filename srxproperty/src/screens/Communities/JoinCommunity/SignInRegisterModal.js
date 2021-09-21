import React, {Component} from 'react';
import {View, TouchableOpacity, ScrollView, Image} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';

import {SRXColor} from '../../../constants';
import {
  FeatherIcon,
  Heading2,
  SmallBodyText,
  Button,
} from '../../../components';
import {Spacing} from '../../../styles';
import {Communities_Welcome} from '../../../assets';
import {LoginStack} from '../../../config';

class SignInRegisterModal extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: isIOS ? false : true,
      },
    };
  }
  state = {
    pressedButton: false,
  };

  constructor(props) {
    super(props);
  }

  componentWillUnmount() {
    const {pressedButton} = this.state;
    if (pressedButton) {
      const passProps = {
        onSuccessSignUp: this.props.onSuccessSignUp,
      };
      LoginStack.showSignInRegisterModal(passProps);
    }
  }

  onCloseModal = () => {
    return Navigation.dismissModal(this.props.componentId);
  };

  directToSignInRegister = () => {
    this.setState({pressedButton: true}, () => this.onCloseModal());
  };

  renderSignInRegisterModal() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          marginTop: Spacing.S,
        }}>
        {this.renderCloseButton()}
        {this.renderWelcomeText()}
        {this.renderButton()}
        {this.renderImage()}
      </View>
    );
  }

  renderCloseButton() {
    return (
      <TouchableOpacity onPress={() => this.onCloseModal()}>
        <FeatherIcon name="x" size={25} color={'black'} />
      </TouchableOpacity>
    );
  }

  renderWelcomeText() {
    return (
      <View style={{marginVertical: Spacing.L, alignItems: 'center'}}>
        <Heading2 style={{marginBottom: Spacing.M, color: SRXColor.Purple}}>
          Welcome to SRX Community
        </Heading2>
        <SmallBodyText>
          Only community members can post,like,comment and share
        </SmallBodyText>
      </View>
    );
  }

  renderButton() {
    return (
      <View style={Styles.buttonContainer}>
        <Button
          buttonStyle={{
            paddingHorizontal: Spacing.L * 2,
          }}
          buttonType={Button.buttonTypes.primary}
          onPress={() => this.directToSignInRegister()}>
          Sign in / Register
        </Button>
      </View>
    );
  }

  renderImage() {
    return (
      <Image
        style={{width: '100%', height: 400}}
        source={Communities_Welcome}
        resizeMode={'contain'}
      />
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <ScrollView>{this.renderSignInRegisterModal()}</ScrollView>
      </SafeAreaView>
    );
  }
}

const Styles = {
  buttonContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: Spacing.M,
  },
};

export {SignInRegisterModal};
