import React, {Component} from 'react';
import {View, Image} from 'react-native';

import {Communities_EmptyComment_Icon} from '../../../../assets';
import {Subtext} from '../../../../components';
import {SRXColor} from '../../../../constants';
import {Spacing} from '../../../../styles';

class CommunitiesCommentEmptyTemplate extends Component {
  render() {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: SRXColor.White,
          paddingTop: Spacing.L,
          paddingBottom: Spacing.L,
          borderTopLeftRadius: 10,
          borderTopRightRadius: 10,
          alignItems: 'center',
          justifyContent: 'center',
        }}>
        <Image
          style={{
            height: 85,
            aspectRatio: 206 / 182,
          }}
          source={Communities_EmptyComment_Icon}
        />
        <Subtext>No comments yet</Subtext>
      </View>
    );
  }
}

export {CommunitiesCommentEmptyTemplate};
