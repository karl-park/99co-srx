import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {TouchableOpacity, View} from 'react-native';

import {SRXColor} from '../../../../../../../constants';
import {CheckboxStyles, Spacing} from '../../../../../../../styles';
import {
  FeatherIcon,
  SmallBodyText,
  Button,
  ExtraSmallBodyText,
} from '../../../../../../../components';
import {Navigation} from 'react-native-navigation';
import {ObjectUtil} from '../../../../../../../utils';

class CommunitiesGRCPostAction extends Component {
  constructor(props) {
    super(props);

    this.state = {
      privacyPolicy: true,
      errorMsg: null,
    };

    this.togglePrivacyPolicy = this.togglePrivacyPolicy.bind(this);
    this.showTermsAndConditions = this.showTermsAndConditions.bind(this);
    this.showTermsOfUse = this.showTermsOfUse.bind(this);
    this.onClickPreviewPost = this.onClickPreviewPost.bind(this);
  }

  togglePrivacyPolicy() {
    const {privacyPolicy} = this.state;
    this.setState({
      privacyPolicy: !privacyPolicy,
      errorMsg: privacyPolicy == true ? null : '',
    });
  }

  showTermsAndConditions() {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/terms-and-conditions',
          screenTitle: 'Terms and Conditions',
        },
      },
    });
  }

  showTermsOfUse() {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'LoginStack.generalWebScreen',
        passProps: {
          url: '/terms-of-use',
          screenTitle: 'Terms of Use',
        },
      },
    });
  }

  onClickPreviewPost() {
    const {privacyPolicy} = this.state;
    const {onPreviewPost} = this.props;
    if (privacyPolicy) {
      onPreviewPost();
    } else {
      this.setState({
        errorMsg: 'Please agree to the Privacy Policy to proceed',
      });
    }
  }

  renderAgreement() {
    const {privacyPolicy, errorMsg} = this.state;
    return (
      <View>
        <View
          style={{
            flexWrap: 'wrap',
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          <TouchableOpacity
            onPress={() => this.togglePrivacyPolicy()}
            style={{padding: 2}}>
            {privacyPolicy ? (
              <View style={CheckboxStyles.checkStyle}>
                <FeatherIcon name={'check'} size={15} color={SRXColor.White} />
              </View>
            ) : (
              <View style={CheckboxStyles.unCheckStyle} />
            )}
          </TouchableOpacity>
          <SmallBodyText style={{marginLeft: Spacing.XS / 2}}>
            I agree to SRX
          </SmallBodyText>
          <TouchableOpacity onPress={() => this.showTermsAndConditions()}>
            <SmallBodyText
              style={{marginLeft: Spacing.XS / 2, color: SRXColor.Teal}}>
              Terms of Conditions
            </SmallBodyText>
          </TouchableOpacity>
          <SmallBodyText style={{marginLeft: Spacing.XS / 2}}>
            and
          </SmallBodyText>
          <TouchableOpacity onPress={() => this.showTermsOfUse()}>
            <SmallBodyText
              style={{marginLeft: Spacing.XS / 2, color: SRXColor.Teal}}>
              Terms of Use
            </SmallBodyText>
          </TouchableOpacity>
        </View>
        {!ObjectUtil.isEmpty(errorMsg) ? (
          <ExtraSmallBodyText style={{color: SRXColor.Red}}>
            {errorMsg}
          </ExtraSmallBodyText>
        ) : null}
      </View>
    );
  }

  renderPreviewPostAction() {
    return (
      <View
        style={{
          marginVertical: Spacing.L,
          alignItems: 'center',
        }}>
        <Button
          buttonType={Button.buttonTypes.primary}
          onPress={() => this.onClickPreviewPost()}>
          Preview Post
        </Button>
      </View>
    );
  }

  render() {
    return (
      <View
        style={{
          padding: Spacing.L,
          borderTopRightRadius: 10,
          borderTopLeftRadius: 10,
          backgroundColor: SRXColor.White,
        }}>
        {this.renderAgreement()}
        {this.renderPreviewPostAction()}
      </View>
    );
  }
}

CommunitiesGRCPostAction.propTypes = {
  onPreviewPost: PropTypes.func.isRequired,
};

export {CommunitiesGRCPostAction};
