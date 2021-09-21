import React, {Component} from 'react';
import {View, FlatList, SectionList, TouchableHighlight} from 'react-native';
import {CommunitySubzoneListItem} from './ListItem';
import {Separator, BodyText, FeatherIcon} from '../../../../../components';
import PropTypes from 'prop-types';
import {SRXColor} from '../../../../../constants';
import {ObjectUtil, SetUtil} from '../../../../../utils';
import {Spacing, CheckboxStyles} from '../../../../../styles';

class CommunitySubzonesList extends Component {
  constructor(props) {
    super(props);
    this.selectItem = this.selectItem.bind(this);
  }

  selectItem = item => {
    const {onItemSelected} = this.props;
    if (onItemSelected) {
      onItemSelected('', item, '');
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

  selectHeader = section => {
    const {onHeaderSelected} = this.props;
    if (onHeaderSelected) {
      onHeaderSelected(section.data[0]);
    }
  };

  renderResultList() {
    const {communitySubzoneArray} = this.props;

    return (
      <SectionList
        sections={communitySubzoneArray}
        stickySectionHeadersEnabled={false}
        renderItem={({index, item, section}) =>
          this.renderContent({index, item, section})
        }
        renderSectionHeader={({section, index}) =>
          this.renderHeader({section, index})
        }
        SectionSeparatorComponent={() => {
          return <View />;
        }}
        keyExtractor={(item, index) => item + index}
      />
    );
  }

  renderHeader = ({section}) => {
    if (!ObjectUtil.isEmpty(section) && section.showSubzonesHeader === true) {
      return (
        <View style={{flex: 1}}>
          <TouchableHighlight
            underlayColor={SRXColor.AccordionBackground}
            onPress={() => this.selectHeader(section)}>
            <View style={Styles.itemContainerStyle}>
              <View style={{flex: 1}}>
                <BodyText style={{marginVertical: 8, paddingLeft: Spacing.XS}}>
                  {section.data[0].name}
                </BodyText>
              </View>
              {this.renderHeaderCheckbox(section.data[0])}
            </View>
          </TouchableHighlight>
          <Separator edgeInset={{left: 15, right: 15}} />
        </View>
      );
    }
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

  renderContent = ({index, item, section}) => {
    if (!ObjectUtil.isEmpty(section) && section.showSubzonesHeader === false) {
      let subzoneArray = [];

      for (let i = 0; i < section.data[0].data.length; i++) {
        subzoneArray.push(section.data[0].data[i]);
      }
      return (
        <FlatList
          data={subzoneArray}
          extraData={this.state}
          keyExtractor={item => item.value}
          renderItem={({item, index}) => this.renderItem({item, index})}
        />
      );
    }
  };

  renderItem = ({item, index}) => {
    const {selectedArray} = this.props;
    return (
      <View>
        <CommunitySubzoneListItem
          key={index}
          communitySubzone={item}
          selectedArray={selectedArray}
          onItemSelected={this.selectItem}
        />
        <Separator edgeInset={{left: 15, right: 15}} />
      </View>
    );
  };

  render() {
    return (
      <View style={{flex: 1, backgroundColor: SRXColor.LightGray}}>
        <Separator />
        <View style={{backgroundColor: SRXColor.White}}>
          {this.renderResultList()}
        </View>
      </View>
    );
  }
}

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

CommunitySubzonesList.propTypes = {
  communitySubzoneArray: PropTypes.array,
  selectedArray: PropTypes.array,

  onItemSelected: PropTypes.func,
};

export {CommunitySubzonesList};
