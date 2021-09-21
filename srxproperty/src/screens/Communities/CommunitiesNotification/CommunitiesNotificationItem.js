import React, {Component} from 'react';
import {
  View,
  TouchableHighlight,
  Platform,
  Image,
  Alert,
  StyleSheet,
} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {Placeholder_Agent} from '../../../assets';
import {
  Avatar,
  Heading2,
  ExtraSmallBodyText,
  BodyText,
  Text,
  SmallBodyText,
  FeatherIcon,
  Separator,
} from '../../../components';
import Moment from 'moment';
import {Concierge_Expert} from '../../../assets';
import {AgentPO} from '../../../dataObject';
import {SRXColor, AppConstant} from '../../../constants';
import {ObjectUtil, CommonUtil} from '../../../utils';
import {Spacing} from '../../../styles';

class CommunitiesNotificationItem extends Component {
  parseStringTemplate(str, obj) {
    if (!ObjectUtil.isEmpty(str) && !ObjectUtil.isEmpty(obj)) {
      let parts = str.split(/\$\{(?!\d)[\wæøåÆØÅ]*\}/);
      let args = str.match(/[^{\}]+(?=})/g) || [];
      let parameters = args.map(
        argument => (
          <BodyText
            style={
              argument === 'commenterName' || argument === 'postOwnerName'
                ? {color: SRXColor.Teal, fontWeight: 'bold'}
                : {color: SRXColor.Gray}
            }>
            {obj[argument]}
          </BodyText>
        ),
        // obj[argument] || (obj[argument] === undefined ? '' : obj[argument]),
      );
      var wholeMsg = (
        <BodyText>
          {parts.map((item, index) => {
            if (index === parts.length - 1) {
              return <BodyText style={{color: SRXColor.Gray}}>{item}</BodyText>;
            } else {
              return (
                <BodyText style={{color: SRXColor.Gray}}>
                  {item}
                  {parameters[index]}
                </BodyText>
              );
            }
          })}
        </BodyText>
      );
      return wholeMsg;
    }
    return <Text />;
  }

  viewPost = () => {
    const {msgObj, navigatePost} = this.props;
    if (msgObj && navigatePost) {
      navigatePost(msgObj);
    }
  };

  compareDate() {
    const {msgObj} = this.props;
    var today = Moment();
    var msgDate = Moment.unix(msgObj.notificationDate);
    var numOfDay = today.diff(msgDate, 'days');
    if (numOfDay === 0) {
      var numOfSec = today.diff(msgDate, 'seconds');
      if (numOfSec >= 60) {
        var numOfMin = today.diff(msgDate, 'minutes');
        if (numOfMin >= 60) {
          var numOfHr = today.diff(msgDate, 'hours');
          return numOfHr + 'h';
        }
        return numOfMin + 'm';
      }
      return numOfSec + 's';
    }
    if (numOfDay >= 7) {
      var numOfWeek = today.diff(msgDate, 'weeks');
      if (numOfWeek > 4) {
        var numOfMonth = today.diff(msgDate, 'months');
        if (numOfMonth >= 12) {
          var numOfYear = today.diff(msgDate, 'years');
          return numOfYear + 'yr';
        }
        return numOfMonth + ' mon';
      }

      return numOfWeek + 'w';
    }

    return numOfDay + 'd';
  }

  render() {
    const {msgObj} = this.props;
    var commenterName = msgObj.commenterName;

    var dateDiffString = this.compareDate();
    // var testMsg = this.parseStringTemplate(msgObj.templateMsg, msgObj);
    return (
      <TouchableHighlight
        onPress={() => {
          this.viewPost();
        }}>
        <View
          style={{
            flex: 1,
            flexDirection: 'row',
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.S,
            backgroundColor: msgObj.read ? SRXColor.White : '#F3F3F3',
          }}>
          <View style={{flex: 1, paddingTop: Spacing.XS}}>
            <Avatar
              size={28}
              name={msgObj.commenterName}
              imageUrl={
                msgObj.commenterPhoto !== '' ? msgObj.commenterPhoto : null
              }
              borderLess={true}
            />
          </View>
          <View
            style={{
              flex: 9,
              flexDirection: 'row',
              borderBottomColor: SRXColor.LightGray,
              borderBottomWidth: 1,
              paddingBottom: Spacing.M,
              paddingLeft: Spacing.XS,
            }}>
            <View>
              {this.parseStringTemplate(msgObj.templateMsg, msgObj)}
              <SmallBodyText style={{color: SRXColor.Gray}} numberOfLines={1}>
                {dateDiffString}
                {msgObj.comment === '' ? '' : ' ∙ "' + msgObj.comment + '"'}
              </SmallBodyText>
            </View>
          </View>
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesNotificationItem.propTypes = {
  msgObj: PropTypes.object,
  navigatePost: PropTypes.func,
};

export default CommunitiesNotificationItem;
