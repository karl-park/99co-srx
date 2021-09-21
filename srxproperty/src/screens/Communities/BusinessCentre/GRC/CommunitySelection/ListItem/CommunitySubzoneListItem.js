import React, {Component} from 'react';
import {View, TouchableHighlight} from 'react-native';
import PropTypes from 'prop-types';

import {FeatherIcon, BodyText} from '../../../../../../components';
import {SRXColor} from '../../../../../../constants';
import {CheckboxStyles, Spacing} from '../../../../../../styles';
import {ObjectUtil} from '../../../../../../utils';

class CommunitySubzoneListItem extends Component {
  constructor(props) {
    super(props);
  }

  selectItem = item => {
    const {onItemSelected} = this.props;
    if (onItemSelected) {
      onItemSelected(item);
    }
  };

  isItemChecked = item => {
    const {selectedArray} = this.props;
    let isItemChecked = false;
    if (selectedArray.has(item.value)) {
      isItemChecked = true;
    }
    return isItemChecked;
  };

  renderRow() {
    const {communitySubzone} = this.props;
    if (!ObjectUtil.isEmpty(communitySubzone)) {
      return (
        <View style={{flex: 1}}>
          <TouchableHighlight
            underlayColor={SRXColor.AccordionBackground}
            onPress={() => this.selectItem(communitySubzone)}>
            <View style={Styles.itemContainerStyle}>
              <View style={{flex: 1}}>
                <BodyText style={{marginVertical: 8, paddingLeft: Spacing.XS}}>
                  {communitySubzone.key}
                </BodyText>
              </View>
              {this.renderRowCheckbox(communitySubzone)}
            </View>
          </TouchableHighlight>
        </View>
      );
    } else {
      return <View />;
    }
  }

  renderRowCheckbox = item => {
    let isChecked = this.isItemChecked(item);
    if (isChecked) {
      return (
        <View style={CheckboxStyles.checkStyle}>
          <FeatherIcon name={'check'} size={15} color={'white'} />
        </View>
      );
    } else {
      return <View style={CheckboxStyles.unCheckStyle} />;
    }
  };

  render() {
    return <View style={{flex: 1}}>{this.renderRow()}</View>;
  }
}

CommunitySubzoneListItem.propTypes = {
  selectedArray: PropTypes.array,
  communitySubzone: PropTypes.object,

  onItemSelected: PropTypes.func,
};

const Styles = {
  itemContainerStyle: {
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    minHeight: 45,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
};

export {CommunitySubzoneListItem};
