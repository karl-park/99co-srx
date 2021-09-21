import React, {Component} from 'react';
import {View, TouchableHighlight} from 'react-native';
import PropTypes from 'prop-types';

import {SRXColor} from '../../../../constants';
import {SmallBodyText} from '../../../../components';

class CommunitiesPostJoinCommunity extends Component {
  render() {
    const {onPressJoinCommunity} = this.props;
    return (
      <TouchableHighlight
        onPress={() => onPressJoinCommunity()}
        style={{backgroundColor: SRXColor.White}}>
        <View
          style={{
            width: '100%',
            height: 40,
            borderColor: SRXColor.Teal,
            borderWidth: 1,
            backgroundColor: '#EEFFFF',
            justifyContent: 'center',
          }}>
          <SmallBodyText style={{alignSelf: 'center', color: SRXColor.Teal}}>
            Join your community and start posting
          </SmallBodyText>
        </View>
      </TouchableHighlight>
    );
  }
}

CommunitiesPostJoinCommunity.propTypes = {
  onPressJoinCommunity: PropTypes.func.isRequired,
};

export {CommunitiesPostJoinCommunity};
