import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';

import {Button, FeatherIcon} from '../../../../components';
import {Spacing, Typography} from '../../.././../styles';
import {SRXColor} from '../../.././../constants';
import {ObjectUtil} from '../../../../utils';
import {CommunitiesCommentConstant} from '../../Constants';
import {CommunitiesStack} from '../../../../config';

class CommunitiesCommentSortOptions extends Component {
  constructor(props) {
    super(props);

    const {sortType} = this.props;

    this.state = {
      initialSortType:
        sortType ?? CommunitiesCommentConstant.defaultCommentSort,
    };

    this.showCommentSortModal = this.showCommentSortModal.bind(this);
    this.onUpdateSortOption = this.onUpdateSortOption.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.sortType !== this.props.sortType) {
      this.setState({initialSortType: this.props.sortType});
    }
  }

  showCommentSortModal() {
    const {initialSortType} = this.state;
    const {section} = this.props;
    if (!ObjectUtil.isEmpty(section.data) && section.data.length > 0) {
      CommunitiesStack.showCommentSortOptionsModal({
        selectedSortOption: initialSortType,
        onSelectSortOption: this.onUpdateSortOption,
      });
    }
  }

  onUpdateSortOption(sortType) {
    const {onSortTypeUpdate} = this.props;
    if (onSortTypeUpdate) {
      onSortTypeUpdate(sortType);
    }
  }

  render() {
    const {initialSortType} = this.state;
    const {section} = this.props;
    if (!ObjectUtil.isEmpty(initialSortType)) {
      return (
        <View
          style={{
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.M,
            backgroundColor: SRXColor.LightGray,
            flexDirection: 'row',
            alignItems: 'center',
          }}>
          <Button
            disabled={
              !(!ObjectUtil.isEmpty(section.data) && section.data.length > 0)
            }
            textStyle={{
              fontSize: Typography.ExtraSmallBody.fontSize,
              color: SRXColor.Black,
              marginRight: Spacing.XS / 2,
            }}
            rightView={
              <FeatherIcon
                name={'chevron-down'}
                size={16}
                color={SRXColor.Black}
              />
            }
            onPress={() => this.showCommentSortModal()}>
            {initialSortType.key}
          </Button>
        </View>
      );
    }
    return <View />;
  }
}

CommunitiesCommentSortOptions.propTypes = {
  /**
   * comment section to get comment list length
   */
  section: PropTypes.object.isRequired,
  /*
   * initial sort type
   */
  sortType: PropTypes.object.isRequired,
  /**
   * fun to open comment sort options
   */
  onSortTypeUpdate: PropTypes.func.isRequired,
};

export {CommunitiesCommentSortOptions};
