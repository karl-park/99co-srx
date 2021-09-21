import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';
import {Button, SmallBodyText} from '../../../../../../components';
import {SRXColor} from '../../../../../../constants';
import {CommunityItem} from '../../../../../../dataObject';
import {Spacing} from '../../../../../../styles';
import {ObjectUtil, StringUtil} from '../../../../../../utils';

class GRCCreatePostView extends Component {
  renderNumMemberLbl() {
    const {community} = this.props;
    if (!ObjectUtil.isEmpty(community)) {
      return (
        <SmallBodyText style={{marginBottom: Spacing.M}}>
          There are{' '}
          <SmallBodyText style={{color: SRXColor.Primary}}>
            {StringUtil.formatThousand(community.membersTotal ?? 0)} SRX members
          </SmallBodyText>{' '}
          in your GRC
        </SmallBodyText>
      );
    }
    return null;
  }

  render() {
    const {onCreatePost} = this.props;
    return (
      <View
        style={{
          backgroundColor: SRXColor.White,
          alignItems: 'center',
          padding: Spacing.M,
        }}>
        {this.renderNumMemberLbl()}
        <Button buttonType={Button.buttonTypes.primary} onPress={onCreatePost}>
          {' '}
          Create new post
        </Button>
      </View>
    );
  }
}

GRCCreatePostView.propTypes = {
  community: PropTypes.instanceOf(CommunityItem),
  /**
   * Function called while user pressing create new post.
   */
  onCreatePost: PropTypes.func,
};

export {GRCCreatePostView};
