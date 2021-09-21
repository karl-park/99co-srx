import React, {Component} from 'react';
import {View, Dimensions} from 'react-native';
import {Avatar, Heading2} from '../../../../../../components';
import {IS_IOS, SRXColor} from '../../../../../../constants';
import {Spacing, TopBarStyle} from '../../../../../../styles';
import {CommonUtil, ObjectUtil} from '../../../../../../utils';

var {height, width} = Dimensions.get('window');

class GRCBusinessTopBar extends Component {
  render() {
    const {business} = this.props;
    if (!ObjectUtil.isEmpty(business)) {
      return (
        <View
          style={[
            TopBarStyle.topBar,
            {
              flex: 1,
              alignSelf: 'flex-start',
              width: width - 55,//55 is a random number
            },
          ]}>
          <Avatar
            size={IS_IOS ? 30 : 40}
            imageUrl={CommonUtil.handleImageUrl(business.logoUrl)}
            borderColor={'#DCDCDC'}
            backgroundColor={SRXColor.LightGray}
          />
          <Heading2 style={{marginLeft: Spacing.XS}}>{business.name}</Heading2>
        </View>
      );
    }
    return null;
  }
}

export {GRCBusinessTopBar};
