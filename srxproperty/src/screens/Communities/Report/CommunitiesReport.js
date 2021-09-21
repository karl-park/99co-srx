import React, {Component} from 'react';
import {View, TextInput, Alert} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';
import {BodyText, ExtraSmallBodyText, FeatherIcon} from '../../../components';
import {Spacing} from '../../../styles';
import {SRXColor, AlertMessage} from '../../../constants';
import {ObjectUtil, CommonUtil} from '../../../utils';
import {CommunitiesService} from '../../../services';
import { CommunitiesConstant } from '../Constants';

class CommunitiesReport extends Component {
  state = {
    maxReasonChar: 500,
    reason: '',
  };

  static options(passProps) {
    const {post} = passProps;
    var title = 'Report Post';
    if (post.type === CommunitiesConstant.postType.comment) {
      title = 'Report Comment';
    }
    return {
      topBar: {
        title: {
          text: title,
        },
        visible: true,
        animate: true,
      },
    };
  }

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.setNavigationOptions();
  }

  componentDidUpdate(prevProps, prevState) {
    this.updatePostButton(prevState, this.state);
  }

  setNavigationOptions() {
    const componentId = this.props.componentId;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'blue')
        .then(icon_close => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              leftButtons: [
                {
                  id: 'btn_close',
                  icon: icon_close,
                },
              ],
              rightButtons: [
                {
                  id: 'btn_Send',
                  text: 'Send',
                  color: SRXColor.TextLink,
                  enabled: false,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        });
    }); //end of promise
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_Send') {
      this.sendReport();
    } else if (buttonId === 'btn_close') {
      Navigation.dismissModal('CommunitiesReportAbuse');
    }
  }

  updatePostButton(prevState, currentState) {
    const isReasonEmpty = ObjectUtil.isEmpty(currentState.reason);

    const canSend = !isReasonEmpty;
    this.setSendButton(canSend, null);
  }

  setSendButton(enabled, title) {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        rightButtons: [
          {
            id: 'btn_Send',
            text: title ?? 'Send',
            color: SRXColor.TextLink,
            enabled: enabled,
          },
        ],
      },
    });
  }

  sendReport() {
    const {post} = this.props;
    const {reason} = this.state;

    CommunitiesService.reportAbuse({postId: post.id, reason})
      .then(response => {
        const error = CommonUtil.getErrorMessageFromSRXResponse(response);
        if (ObjectUtil.isEmpty(error)) {
          //no error
          Alert.alert(
            AlertMessage.SuccessMessageTitle,
            'Report sent successfully',
            [
              {
                text: 'OK',
                onPress: () => {
                  Navigation.dismissModal('CommunitiesReportAbuse');
                },
              },
            ],
          );
        } else {
          //handle error
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
          this.setSendButton(true);
        }
      })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
        this.setSendButton(true);
      });
  }

  renderReport() {
    const {post} = this.props;
    const {maxReasonChar, reason} = this.state;
    return (
      <View>
        <View
          style={{
            borderBottomColor: SRXColor.LightGray,
            borderBottomWidth: 1,
            paddingTop: Spacing.S,
          }}>
          <TextInput
            style={{
              paddingTop: Spacing.XS,
              paddingBottom: Spacing.M,
              fontSize: 16,
              height: 180,
              textAlignVertical: 'top',
            }}
            underlineColorAndroid="transparent"
            placeholder={
              'Please provide a description of the reported post or comment'
            }
            placeholderTextColor={SRXColor.Gray}
            multiline={true}
            maxLength={maxReasonChar}
            value={reason}
            onChangeText={text => {
              this.setState({reason: text});
            }}
          />
          <ExtraSmallBodyText
            style={{
              color: SRXColor.Gray,
              position: 'absolute',
              bottom: 0,
              right: 0,
            }}>
            {maxReasonChar - reason.length}
          </ExtraSmallBodyText>
        </View>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <KeyboardAwareScrollView style={{flex: 1}}>
          <View
            style={{
              paddingHorizontal: Spacing.M,
              paddingVertical: Spacing.M,
            }}>
            {this.renderReport()}
          </View>
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

export {CommunitiesReport};
