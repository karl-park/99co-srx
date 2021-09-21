/*
    Display logic of commercial survey section
    
    Only show this section if the user is:
    1. Logged in
    2. Have not entered a selection before
    If entered a selection, show "Thank you for your vote!"
    Next time user comes in app, no longer show this section

*/

import React, {Component} from 'react';
import {View} from 'react-native';
import {ObjectUtil, StringUtil, CommonUtil} from '../../../../utils';
import {Spacing, Typography} from '../../../../styles';
import {BodyText, Button, Subtext, MaterialIcon} from '../../../../components';
import {MainScreenService} from '../../../../services';
import {connect} from 'react-redux';
import {SRXColor, AlertMessage} from '../../../../constants';
import PropTypes from 'prop-types';

class CommercialSurvey extends Component {
  static propTypes = {
    isResidential: PropTypes.bool,
  };

  static defaultProps = {
    isResidential: true,
  };

  state = {
    commericalVoting: null,
    beforeAnswerSurvey: true, // show commercial survey form or thank you text
    answeredSurvey: false, // show commercial survey section or hide
  };

  componentDidUpdate(prevProps) {
    if (
      prevProps.userPO !== this.props.userPO ||
      prevProps.serverDomain !== this.props.serverDomain
    ) {
      if (!ObjectUtil.isEmpty(this.props.userPO)) {
        this.findCommercialAppSurveyResult();
      }
    }
  }

  onPressAnswer = selectedCommericalVoting => {
    const {commericalVoting} = this.state;

    this.setState({commericalVoting: selectedCommericalVoting}, () => {
      var answer = selectedCommericalVoting ? 1 : 2;
      this.submitCommercialAppSurvey(answer);
    });
  };

  submitCommercialAppSurvey = answer => {
    MainScreenService.submitCommercialAppSurvey({
      answer,
    }).then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const error = CommonUtil.getErrorMessageFromSRXResponse(response);
        if (!ObjectUtil.isEmpty(error)) {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        } else {
          if (!ObjectUtil.isEmpty(response)) {
            if (response.result === 'success') {
              this.setState({beforeAnswerSurvey: false});
            }
          }
        }
      } else {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      }
    });
  };

  findCommercialAppSurveyResult() {
    MainScreenService.findCommercialAppSurveyResult().then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const error = CommonUtil.getErrorMessageFromSRXResponse(response);
        if (!ObjectUtil.isEmpty(error)) {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        } else {
          if (!ObjectUtil.isEmpty(response)) {
            const {totalCount, userIdCount} = response;

            let commercialVoteCount = 0;
            if (totalCount && totalCount > 0) {
              commercialVoteCount = totalCount;
            }
            let answeredSurvey = false;
            if (userIdCount && userIdCount > 0) {
              answeredSurvey = true;
            }

            this.setState({
              commercialVoteCount,
              answeredSurvey,
            });
          }
        }
      } else {
        Alert.alert(AlertMessage.ErrorMessageTitle, 'Error');
      }
    });
  }

  //Start Rendering Methods
  renderCommercialSurvey() {
    const {beforeAnswerSurvey} = this.state;
    if (beforeAnswerSurvey) {
      return this.renderBeforeAnswerSurvey();
    } else {
      return this.renderAfterAnswerSurvey();
    }
  }

  renderBeforeAnswerSurvey() {
    const {answeredSurvey, commercialVoteCount, commericalVoting} = this.state;
    if (answeredSurvey === false) {
      return (
        <View style={styles.surveryContainer}>
          <BodyText>
            Do you think SRX Commercial should be a separate app?
          </BodyText>

          <Button
            buttonStyle={styles.buttonContainer}
            onPress={() => this.onPressAnswer(true)}
            leftView={
              commericalVoting === true ? (
                <MaterialIcon
                  name={'radio-button-checked'}
                  size={20}
                  color={SRXColor.Teal}
                />
              ) : (
                <MaterialIcon
                  name={'radio-button-unchecked'}
                  size={23}
                  color={SRXColor.Teal}
                />
              )
            }
            textStyle={[Typography.Body, {marginLeft: Spacing.XS}]}>
            {'Yes'}
          </Button>

          <Button
            buttonStyle={styles.buttonContainer}
            onPress={() => this.onPressAnswer(false)}
            leftView={
              commericalVoting === false ? (
                <MaterialIcon
                  name={'radio-button-checked'}
                  size={20}
                  color={SRXColor.Teal}
                />
              ) : (
                <MaterialIcon
                  name={'radio-button-unchecked'}
                  size={23}
                  color={SRXColor.Teal}
                />
              )
            }
            textStyle={[Typography.Body, {marginLeft: Spacing.XS}]}>
            {'No'}
          </Button>

          <Subtext style={{paddingTop: Spacing.M}}>
            {StringUtil.formatThousand(commercialVoteCount)}
            {commercialVoteCount > 1 ? ' Votes' : ' Vote'}
          </Subtext>
        </View>
      );
    } else {
      //answeredSurvery True
      return <View />;
    }
  }

  renderAfterAnswerSurvey() {
    return (
      <View style={styles.surveryContainer}>
        <BodyText style={{textAlign: 'center'}}>
          Thank you for your vote!
        </BodyText>
      </View>
    );
  }

  render() {
    const {userPO, isResidential} = this.props;
    if (!ObjectUtil.isEmpty(userPO) && isResidential === false) {
      return this.renderCommercialSurvey();
    } else {
      return <View />;
    }
  }
}

const styles = {
  surveryContainer: {
    flex: 1,
    overflow: 'hidden',
    borderRadius: 10,
    padding: Spacing.M, //padding
    backgroundColor: SRXColor.White,
    marginBottom: Spacing.S, // space between section
  },
  buttonContainer: {
    paddingTop: Spacing.M,
    alignItems: 'center',
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    serverDomain: state.serverDomain.domainURL,
  };
};

export default connect(mapStateToProps)(CommercialSurvey);
