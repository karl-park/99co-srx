import React, {Component} from 'react';
import {Image} from 'react-native';
import PropTypes from 'prop-types';
import {
  Button,
  FeatherIcon,
  ExtraSmallBodyText,
  SmallBodyText,
  Subtext,
} from '../../../../components';
import {SRXColor} from '../../../../constants';
import {Spacing, Typography} from '../../../../styles';
import {ObjectUtil, StringUtil} from '../../../../utils';
import {CommunityPostReactionPO} from '../../../../dataObject';

class CommunitiesFeedReaction extends Component {
  constructor(props) {
    super(props);

    this.onSelect = this.onSelect.bind(this);
    this.renderIcon = this.renderIcon.bind(this);
  }

  onSelect() {
    const {onReactionSelected, reaction} = this.props;
    onReactionSelected(reaction);
  }

  renderIcon() {
    const {reaction} = this.props;
    if (!ObjectUtil.isEmpty(reaction.iconUrl)) {
      return (
        <Image
          source={{uri: reaction.iconUrl}}
          style={{
            width: 16,
            height: 16,
            marginRight: Spacing.XS / 2,
            // tintColor: reaction.reacted ? SRXColor.Teal : SRXColor.Black,
          }}
          resizeMode={'contain'}
        />
      );
    } else {
      return null;
    }
  }

  renderCount() {
    const {reaction} = this.props;
    if (reaction.count > 0) {
      return (
        <Subtext
          style={{
            marginLeft: Spacing.XS / 2,
            color: reaction.reacted ? SRXColor.Teal : Typography.Subtext.color,
          }}>
          {StringUtil.formatThousandWithAbbreviation(reaction.count)}
        </Subtext>
      );
    }
  }

  render() {
    const {reaction} = this.props;
    return (
      <Button
        leftView={this.renderIcon()}
        rightView={this.renderCount()}
        buttonStyle={{
          paddingVertical: Spacing.XS,
          marginRight: Spacing.L,
        }}
        textStyle={{
          color: reaction.reacted ? SRXColor.Teal : SRXColor.Black,
        }}
        onPress={this.onSelect}>
        {reaction.name ?? ''}
      </Button>
    );
  }
}

CommunitiesFeedReaction.propTypes = {
  reaction: PropTypes.instanceOf(CommunityPostReactionPO),

  onReactionSelected: PropTypes.func,
};

export {CommunitiesFeedReaction};
