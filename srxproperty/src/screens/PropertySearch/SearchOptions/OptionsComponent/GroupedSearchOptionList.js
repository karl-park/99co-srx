import React, {Component} from 'react';
import {View, SectionList, TouchableHighlight} from 'react-native';
import PropTypes from 'prop-types';

import {Separator, FeatherIcon, BodyText} from '../../../../components';
import {Styles} from '../Styles';
import {SRXColor} from '../../../../constants';
import {SetUtil} from '../../../../utils';
import {CheckboxStyles, Spacing} from '../../../../styles';

const GroupedSearchOptionListSource = {
  SchoolGroupList: 'SchoolGroupList', //from SchoolGroupList
  CommunitySelection: 'CommunitySelection', //from CommunitySelection
};

class GroupedSearchOptionList extends Component {
  static propTypes = {
    source: PropTypes.oneOf(Object.keys(GroupedSearchOptionListSource)),
  };
  constructor(props) {
    super(props);
  }

  selectHeader = section => {
    const {onHeaderSelected} = this.props;
    if (onHeaderSelected) {
      onHeaderSelected(section);
    }
  };

  selectItem = (index, item, section) => {
    const {onItemSelected} = this.props;
    if (onItemSelected) {
      onItemSelected(index, item, section);
    }
  };

  isHeaderChecked = section => {
    const {selectedArray} = this.props;
    let isHeaderChecked = false;
    if (SetUtil.isSuperset(selectedArray, section.value)) {
      isHeaderChecked = true;
    }

    return isHeaderChecked;
  };

  isItemChecked = item => {
    const {selectedArray} = this.props;
    let isItemChecked = false;
    if (selectedArray.has(item.value)) {
      isItemChecked = true;
    }
    return isItemChecked;
  };

  renderHeader = ({section}) => {
    const {source} = this.props;
    return (
      <View style={{flex: 1}} key={section.value}>
        <TouchableHighlight
          underlayColor={SRXColor.AccordionBackground}
          onPress={() => this.selectHeader(section)}>
          <View style={Styles.itemContainerStyle}>
            <View style={{flex: 1}}>
              {source == GroupedSearchOptionListSource.CommunitySelection ? (
                <BodyText style={{marginVertical: 8, paddingLeft: Spacing.S}}>
                  {section.key}
                </BodyText>
              ) : (
                <BodyText style={{marginVertical: 8}}>{section.key}</BodyText>
              )}
            </View>
            {this.renderHeaderCheckbox(section)}
          </View>
        </TouchableHighlight>
        {source == GroupedSearchOptionListSource.CommunitySelection
          ? this.renderSeparator()
          : null}
      </View>
    );
  };

  renderRow = ({index, item, section}) => {
    const {source} = this.props;
    return (
      <View style={{flex: 1}} key={index + '-' + item.value}>
        <TouchableHighlight
          underlayColor={SRXColor.AccordionBackground}
          onPress={() => this.selectItem(index, item, section)}>
          <View style={Styles.itemContainerStyle}>
            <View style={{flex: 1}}>
              {source == GroupedSearchOptionListSource.CommunitySelection ? (
                <BodyText style={{marginVertical: 8, paddingLeft: Spacing.S}}>
                  {item.key}
                </BodyText>
              ) : (
                <BodyText style={{marginVertical: 8}}>{item.key}</BodyText>
              )}
            </View>
            {this.renderRowCheckbox(item, index)}
          </View>
        </TouchableHighlight>
      </View>
    );
  };

  renderHeaderCheckbox = section => {
    let isChecked = this.isHeaderChecked(section);
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

  renderRowCheckbox = (item, index) => {
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

  renderSeparator = () => {
    return <Separator edgeInset={{left: 15, right: 15}} />;
  };

  render() {
    const {dataArray, source, searchbyQuery} = this.props;
    return (
      <SectionList
        sections={dataArray}
        stickySectionHeadersEnabled={false}
        renderItem={({index, item, section}) =>
          this.renderRow({index, item, section})
        }
        renderSectionHeader={
          source != GroupedSearchOptionListSource.SchoolGroupList &&
          searchbyQuery === false
            ? ({section}) => this.renderHeader({section})
            : null
        }
        ItemSeparatorComponent={() => this.renderSeparator()}
        SectionSeparatorComponent={() => {
          return source !== GroupedSearchOptionListSource.SchoolGroupList &&
            source !== GroupedSearchOptionListSource.CommunitySelection ? (
            <Separator edgeInset={{left: 15, right: 15}} />
          ) : null;
        }}
        keyExtractor={(item, index) => item + index}
      />
    );
  }
}

GroupedSearchOptionList.Sources = GroupedSearchOptionListSource;

export {GroupedSearchOptionList};
