import React, {Component} from 'react';
import {
  View,
  TouchableHighlight,
  TouchableOpacity,
  Dimensions,
  Platform,
  StyleSheet,
  Image,
  Linking,
} from 'react-native';
import PropTypes from 'prop-types';
import {
  Avatar,
  Button,
  Ionicons,
  BodyText,
  Text,
  FeatherIcon,
} from '../../../../components';
import {AppTopBar_BackBtn} from '../../../../assets';
import {SRXColor} from '../../../../constants';
import {TopBarStyle, Spacing} from '../../../../styles';
import {CommonUtil, ObjectUtil} from '../../../../utils';

import {Navigation} from 'react-native-navigation';
import {UserPO} from '../../../../dataObject';

var {height, width} = Dimensions.get('window');

const isIOS = Platform.OS === 'ios';

class ChatRoomTopBar extends Component {
  static defaultProps = {};
  constructor(props) {
    super(props);
    this.state = {};
  }

  callAgent() {
    const {agentInfo} = this.props;
    const url = 'tel:' + agentInfo.mobile;
    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log("ChatRoom - call - Can't handle url: " + url);
        } else {
          return Linking.openURL(url);
        }
      })
      .catch(err =>
        console.error('ChatRoom - An error occurred - call agent - ', err),
      );
  }

  render() {
    const {agentInfo, onPressAgent, onBackPressed, userPO} = this.props;
    const {mobile, otherUserAgentInd, agent} = agentInfo;

    var isOtherAgent = false;
    if (!ObjectUtil.isEmpty(userPO) && !ObjectUtil.isEmpty(agentInfo)) {
      isOtherAgent = userPO.id !== agentInfo.userId;
    }
    
    let isContactable =
      mobile != '-' &&
      mobile != '' &&
      (otherUserAgentInd || agent || isOtherAgent);
    let imageUri = CommonUtil.handleImageUrl(agentInfo.photo);

    return (
      <View
        style={[
          TopBarStyle.topBar,
          {
            height: 40,
            // justifyContent: "space-between",
            paddingLeft: 0,
            paddingRight: 0,
            width: width - 2 * Spacing.XS,
            // marginLeft: isIOS ? 0 : -8,
            backgroundColor: SRXColor.White,
          },
        ]}>
        <View style={[styles.topBarSubStyle, {paddingLeft: 0}]}>
          <Button
            buttonStyle={{height: '100%', alignItems: 'center'}}
            leftView={
              <Image source={AppTopBar_BackBtn} resizeMode={'contain'} />
            }
            onPress={onBackPressed}
          />
          <TouchableOpacity
            style={{flexDirection: 'row', alignItems: 'center', flex: 1}}
            onPress={isContactable ? () => onPressAgent() : null}
            disabled={!isContactable}>
            <View
              style={[
                styles.userInitialNameCircle,
                isIOS ? {width: 30, height: 30, borderRadius: 15} : null,
              ]}>
              <Avatar
                size={isIOS ? 30 : 40}
                imageUrl={imageUri}
                name={agentInfo.name}
                borderColor={'#DCDCDC'}
              />
            </View>
            <BodyText style={{flex: 1}} numberOfLines={1}>
              {agentInfo.name}
            </BodyText>
          </TouchableOpacity>
          {isContactable ? (
            <TouchableOpacity
              style={{justifyContent: 'flex-end'}}
              onPress={() => this.callAgent()}>
              <FeatherIcon
                name={'phone'}
                size={25}
                color={SRXColor.Black}
                style={{marginRight: Spacing.XS}}
              />
            </TouchableOpacity>
          ) : null}
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  topBarSubStyle: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    height: isIOS ? 30 : 36,
    paddingHorizontal: 4,
    borderRadius: 5,
  },
  topBarBackButton: {
    width: 25,
    height: '100%',
    alignItems: 'center',
    justifyContent: 'center',
  },
  topBarTextContainerStyle: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: Spacing.M,
    height: '100%',
  },
  userInitialNameCircle: {
    justifyContent: 'center',
    alignSelf: 'center',
    alignItems: 'center',
    width: 40,
    height: 40,
    backgroundColor: '#F0E68C',
    borderRadius: 30,
    marginRight: Spacing.XS,
    marginLeft: Spacing.XS,
  },
});

ChatRoomTopBar.propTypes = {
  agentInfo: PropTypes.object.isRequired,
  onPressAgent: PropTypes.func.isRequired,

  userPO: PropTypes.instanceOf(UserPO),
};
export {ChatRoomTopBar};
