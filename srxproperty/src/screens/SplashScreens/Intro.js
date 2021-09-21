import React, {Component} from 'react';
import {View, Animated, Dimensions, Platform} from 'react-native';
import {Navigation} from 'react-native-navigation';
import SafeAreaView from 'react-native-safe-area-view';
import SplashScreen from 'react-native-splash-screen';
import {connect} from 'react-redux';
import {Splash_Screen_Content} from '../../assets';
import {LoginStack, CommunitiesStack} from '../../config';
import {SRXColor} from '../../constants';
import {Spacing} from '../../styles';
import {ObjectUtil, CommunitiesUtil, UserUtil} from '../../utils';
import {SignInRegisterSource} from '../UserAuthentication/Constants/UserAuthenticationConstants';
import {SignInRegister} from '../UserAuthentication';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
var {height, width} = Dimensions.get('window');
isIOS = Platform.OS === 'ios';
class Intro extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: isIOS ? false : true,
      },
    };
  }

  state = {
    scaleAnim: new Animated.Value(0),
    fadeAnim: new Animated.Value(0),
    topAnim: new Animated.Value(height * 0.3485),
    pressedForgotPassword: false,
    presssedOK: false,
  };

  constructor(props) {
    super(props);
    this.closeIntro = this.closeIntro.bind(this);
  }

  componentDidMount() {
    setTimeout(() => {
      this.renderAnimation();
    }, 300);
  }

  componentDidUpdate(prevProps) {
    if (
      prevProps.userPO != this.props.userPO &&
      !ObjectUtil.isEmpty(this.props.userPO)
    ) {
      // console.log("user just logged in with FB");
      // Navigation.dismissModal(this.props.componentId);
      this.closeIntro();
    }
  }
  
  renderAnimation() {
    SplashScreen.hide();
    setTimeout(() => {
      Animated.sequence([
        Animated.parallel([
          Animated.timing(this.state.scaleAnim, {
            duration: 300,
            toValue: isIOS ? 1 : 0.25,
            useNativeDriver: true,
          }),
          Animated.timing(this.state.topAnim, {
            duration: 300,
            useNativeDriver: true,
          }),
        ]),
        Animated.timing(this.state.fadeAnim, {
          toValue: 1,
          duration: 300,
          useNativeDriver: true,
        }),
      ]).start();
    }, 500);
  }

  componentWillUnmount() {
    const {pressedForgotPassword, presssedOK} = this.state;
    if (pressedForgotPassword) {
      this.showForgotPasswordModal();
    }
    if (presssedOK) {
      LoginStack.showSignInRegisterModal();
    }
  }

  closeIntro = () => {
    this.props.hideIntro();
  };

  showForgotPasswordModalfromIntro = () => {
    this.setState({pressedForgotPassword: true}, () => this.closeIntro());
  };

  showForgotPasswordModal = () => {
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'LoginStack.forgotPassword',
              options: {
                modalPresentationStyle: 'overFullScreen',
              },
            },
          },
        ],
      },
    });
  };

  onPressOKfromIntro = () => {
    this.setState({presssedOK: true}, () => this.closeIntro());
  };

  onClickTermsOfUsefromIntro = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/terms-of-use',
          screenTitle: 'Terms of Use',
        },
      },
    });
  };

  onClickPrivacyPolicyfromIntro = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/privacy-policy',
          screenTitle: 'Privacy Policy',
        },
      },
    });
  };

  renderLoginOptions() {
    return (
      <KeyboardAwareScrollView extraScrollHeight={100} bounces={false}>
        <Animated.View
          style={{
            marginTop: Spacing.M,
            bottom: Spacing.XS,
            justifyContent: 'flex-end',
            opacity: this.state.fadeAnim,
          }}>
          <SignInRegister
            source={SignInRegisterSource.SplashScreen}
            closeIntro={this.closeIntro}
            showForgotPasswordModalfromIntro={
              this.showForgotPasswordModalfromIntro
            }
            onPressOKfromIntro={this.onPressOKfromIntro}
            onClickTermsOfUsefromIntro={this.onClickTermsOfUsefromIntro}
            onClickPrivacyPolicyfromIntro={this.onClickPrivacyPolicyfromIntro}
          />
        </Animated.View>
      </KeyboardAwareScrollView>
    );
  }

  renderMainContent() {
    /**
     * The image size is constant
     * keep it in ratio 75:38
     *  and stick to the left and right
     *
     * The top of the image is height of screen * 0.3845 (initial)
     *
     * This screen have to scyn with ios LaunchScreen.xib & splash screen
     * in android scyn with launch_screen.xml & splash screen
     */
    const {topAnim} = this.state;
    return (
      <View style={{marginTop: -20, marginBottom: -60}}>
        <Animated.Image
          source={Splash_Screen_Content}
          style={[
            {
              // top: topAnim,
              width: width,
              height: (width * 38) / 75,
            },
            {
              transform: [
                {
                  translateY: topAnim,
                },
                {
                  scaleX: this.state.scaleAnim.interpolate({
                    inputRange: [0, 1],
                    outputRange: isIOS ? [1, 0.7] : [1, 0],
                  }),
                },
                {
                  scaleY: this.state.scaleAnim.interpolate({
                    inputRange: [0, 1],
                    outputRange: isIOS ? [1, 0.7] : [1, 0],
                  }),
                },
              ],
            },
          ]}
        />
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
        {this.renderMainContent()}
        {this.renderLoginOptions()}
      </SafeAreaView>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};
export default connect(mapStateToProps)(Intro);
