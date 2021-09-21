import React, {Component} from 'react';
import {View, Alert} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {
  Heading2,
  Subtext,
  Button,
  TextInput,
  BodyText,
  Avatar,
} from '../../../../components';
import {AgentPO} from '../../../../dataObject';
import {Spacing, Typography} from '../../../../styles';
import {ObjectUtil, CommonUtil} from '../../../../utils';
import {
  SRXColor,
  LeadSources,
  LeadTypes,
  IS_IOS,
  SubmissionState,
  AlertMessage,
} from '../../../../constants';
import {EnquiryService} from '../../../../services';

class AgentInformation extends Component {
  static propTypes = {
    agentPO: PropTypes.instanceOf(AgentPO),
    templateMessage: PropTypes.string,
  };

  static defaultProps = {
    agentPO: null,
    templateMessage: null,
  };

  constructor(props) {
    super(props);

    this.state = {
      name: props.userPO ? props.userPO.name : '',
      mobile: props.userPO ? props.userPO.mobileLocalNum : '',
      email: props.userPO ? props.userPO.email : '',
      message: props.templateMessage ? props.templateMessage : '',
      error: null,
      submissionState: SubmissionState.Normal,
    };

    this.viewAgentCV = this.viewAgentCV.bind(this);
    this.onClickEnquiry = this.onClickEnquiry.bind(this);
  }

  viewAgentCV() {
    const {componentId, agentPO} = this.props;

    // XValueService.trackAgentAdViewMobileInXValue({
    //   agentPO: agentPO,
    //   source: "A",
    //   viewId: 230 //230 = x-value
    // }); //no need response
    if (componentId) {
      Navigation.push(this.props.componentId, {
        component: {
          name: 'ConciergeStack.AgentCV',
          passProps: {
            agentUserId: agentPO.getAgentId(),
          },
        },
      });
    }
  }

  onClickEnquiry() {
    if (this.isValidated()) {
      const {agentPO} = this.props;
      if (!ObjectUtil.isEmpty(agentPO)) {
        this.setState({submissionState: SubmissionState.Submitting}, () => {
          this.performSubmitSrxAgentEnquiryForm();
        });
      }
    } //end of validation
  }

  performSubmitSrxAgentEnquiryForm() {
    const {agentPO, userPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      const {name, mobile, email, message} = this.state;
      EnquiryService.submitSrxAgentEnquiryV2({
        agentUserId: agentPO.userId,
        fullName: name,
        countryCode: userPO.countryCode,
        mobileLocalNumber: mobile,
        email: email,
        message: message,
      })
        .then(response => {
          var error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
            this.setState({submissionState: SubmissionState.Failed});
          } else {
            const {result} = response;
            console.log(result);
            this.setState({submissionState: SubmissionState.Submitted});
          }
        })
        .catch(error => {
          console.log(error);
          this.setState({submissionState: SubmissionState.Failed});
        });
    }
  }

  isValidated() {
    const {name, mobile, email, message} = this.state;
    var error = {
      nameError: '',
      mobileError: '',
      emailError: '',
      messageError: '',
    };

    var hasError = false;
    if (ObjectUtil.isEmpty(name)) {
      error.nameError = 'Required field';
      hasError = true;
    }

    if (ObjectUtil.isEmpty(mobile.toString())) {
      error.mobileError = 'Required field';
      hasError = true;
    }

    if (ObjectUtil.isEmpty(email)) {
      error.emailError = 'Required field';
      hasError = true;
    }

    if (ObjectUtil.isEmpty(message)) {
      error.messageError = 'Required field';
      hasError = true;
    }

    this.setState({error: {...this.state.error, ...error}});
    return !hasError;
  }

  renderAgentName() {
    const {agentPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      return <Heading2 style={{marginBottom: 4}}>{agentPO.name}</Heading2>;
    }
  }

  renderCeaNo() {
    const {agentPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO) && !ObjectUtil.isEmpty(agentPO.ceaRegNo)) {
      return <Subtext>CEA: {agentPO.ceaRegNo}</Subtext>;
    }
  }

  renderAgentContent() {
    return (
      <View>
        {this.renderAgentName()}
        {this.renderCeaNo()}
      </View>
    );
  }

  renderAgentPhoto() {
    const {agentPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      return (
        <View style={{marginRight: Spacing.XS}}>
          <Avatar
            name={agentPO.name}
            imageUrl={agentPO.photo}
            size={45}
            borderColor={'#DCDCDC'}
            textSize={25}
          />
        </View>
      );
    }
  }

  renderInformation() {
    return (
      <View
        style={{
          flexDirection: 'row',
          flex: 1,
        }}>
        {this.renderAgentPhoto()}
        {this.renderAgentContent()}
      </View>
    );
  }

  /*
   * onPress is pending for Agent CV
   */
  renderViewAgentCV() {
    const {agentPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      return (
        <Button
          textStyle={[Typography.Subtext, {color: SRXColor.Teal}]}
          onPress={this.viewAgentCV}>
          View Agent CV
        </Button>
      );
    }
  }

  renderUserName() {
    const {name, error} = this.state;
    return (
      <View style={{marginBottom: Spacing.L}}>
        <TextInput
          value={name}
          error={error ? error.nameError : ''}
          placeholder={'Name'}
          onChangeText={text => {
            this.setState({name: text, error: {...error, nameError: ''}});
          }}
        />
      </View>
    );
  }

  renderUserMobile() {
    const {mobile, error} = this.state;
    return (
      <View style={{marginBottom: Spacing.L}}>
        <TextInput
          value={mobile.toString()}
          error={error ? error.mobileError : ''}
          placeholder={'Mobile'}
          keyboardType={IS_IOS ? 'number-pad' : 'numeric'}
          onChangeText={text =>
            this.setState({mobile: text, error: {...error, mobileError: ''}})
          }
        />
      </View>
    );
  }

  renderUserEmail() {
    const {email, error} = this.state;
    return (
      <View style={{marginBottom: Spacing.L}}>
        <TextInput
          value={email}
          error={error ? error.emailError : ''}
          placeholder={'Email'}
          onChangeText={text =>
            this.setState({email: text, error: {...error, emailError: ''}})
          }
        />
      </View>
    );
  }

  renderMessage() {
    const {message, error} = this.state;
    return (
      <View style={{marginBottom: Spacing.XL}}>
        <TextInput
          error={error ? error.messageError : ''}
          value={message}
          multiline={true}
          placeholder={'Message'}
          onChangeText={text =>
            this.setState({message: text, error: {...error, messageError: ''}})
          }
        />
      </View>
    );
  }

  renderAgentEnquiryForm() {
    const {submissionState} = this.state;
    if (submissionState === SubmissionState.Submitted) {
      return (
        <View
          style={{
            justifyContent: 'center',
            alignItems: 'center',
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M,
            paddingBottom: Spacing.XL,
          }}>
          <BodyText style={{textAlign: 'center'}}>
            Thank you for your message.{'\n'}The agent will be informed of your
            request.
          </BodyText>
        </View>
      );
    } else {
      return (
        <View>
          {this.renderUserName()}
          {this.renderUserMobile()}
          {this.renderUserEmail()}
          {this.renderMessage()}
        </View>
      );
    }
  }

  renderAgentInfo() {
    return (
      <View
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
        }}>
        {this.renderInformation()}
        {this.renderViewAgentCV()}
      </View>
    );
  }

  renderEnquiryActionButton() {
    const {submissionState} = this.state;
    var buttonTitle = 'Enquire Now';
    if (submissionState === SubmissionState.Submitting) {
      buttonTitle = 'Submitting...';
    }
    if (submissionState !== SubmissionState.Submitted) {
      return (
        <View style={{marginTop: Spacing.L}}>
          <Button
            buttonType={Button.buttonTypes.primary}
            buttonStyle={{
              paddingHorizontal: Spacing.XL,
              justifyContent: 'center',
            }}
            onPress={this.onClickEnquiry}
            isSelected={submissionState === SubmissionState.Submitting}>
            {buttonTitle}
          </Button>
        </View>
      );
    }
  }

  render() {
    const {style} = this.props;
    return (
      <View
        style={[
          {
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M,
            paddingBottom: Spacing.L,
          },
          style,
        ]}>
        <Heading2 style={{marginBottom: Spacing.L}}>Contact Agent</Heading2>
        {this.renderAgentEnquiryForm()}
        {this.renderAgentInfo()}
        {this.renderEnquiryActionButton()}
      </View>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(AgentInformation);
