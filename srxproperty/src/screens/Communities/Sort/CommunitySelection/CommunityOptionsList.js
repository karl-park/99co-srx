import React, {Component} from 'react';
import {View, ScrollView, Image} from 'react-native';
import PropTypes from 'prop-types';

import {CommunityItem} from '../../../../dataObject';
import {
  BodyText,
  FeatherIcon,
  TouchableHighlight,
} from '../../../../components';
import {Spacing} from '../../../../styles';
import {SRXColor} from '../../../../constants';
import {CommunitiesConstant} from '../../Constants';
import {Communities_Icon_Condo} from '../../../../assets';
import {ObjectUtil} from '../../../../utils';

class CommunityOptionsList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedCommunity: props.selectedCommunity,
    };
  }

  selectCommunity(communityItem) {
    this.setState({selectedCommunity: communityItem}, () => {
      const {onCommunitySelected} = this.props;

      if (onCommunitySelected) {
        onCommunitySelected(communityItem);
      }
    });
  }

  renderSingaporeCommunityRow(item) {
    const {selectedCommunity} = this.state;
    if (!ObjectUtil.isEmpty(selectedCommunity) && !ObjectUtil.isEmpty(item)) {
      return (
        <TouchableHighlight
          onPress={() => this.selectCommunity(item)}
          style={{
            marginBottom: Spacing.XS,
            borderRadius: 10,
            marginTop: Spacing.XS,
          }}>
          <View
            style={[
              Styles.singaporeCommunityContainer,
              {
                borderColor:
                  selectedCommunity.id == item.id
                    ? SRXColor.Teal
                    : SRXColor.Transparent,
              },
            ]}>
            {this.renderCommunityIcon(item)}
            {this.renderCommunityName(
              CommunitiesConstant.singaporeCommunity.name,
              selectedCommunity.id == item.id,
            )}
            {this.renderCheckIcon(item)}
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderCommunityRow(item, indent) {
    return (
      <View>
        {this.renderCommunityRowLayout(item, indent)}
        {item.childs.map((item, index) => {
          return this.renderCommunityRow(item, indent + 1);
        })}
      </View>
    );
  }

  renderCommunityRowLayout(item, indent) {
    const {selectedCommunity} = this.state;
    if (!ObjectUtil.isEmpty(selectedCommunity) && !ObjectUtil.isEmpty(item)) {
      return (
        <TouchableHighlight
          onPress={() => this.selectCommunity(item)}
          style={{marginTop: indent > 0 ? Spacing.XS : 0}}>
          <View style={{backgroundColor: SRXColor.White}}>
            <View
              style={[
                Styles.rowCommunitySubContainer,
                {
                  paddingLeft: Spacing.S + indent * Spacing.XS,
                  paddingRight: Spacing.S,
                  borderColor:
                    selectedCommunity.id === item.id
                      ? SRXColor.Teal
                      : SRXColor.Transparent,
                },
              ]}>
              {this.renderCommunityIcon(item)}
              {this.renderCommunityName(
                item.name,
                selectedCommunity.id === item.id,
              )}
              {this.renderCheckIcon(item)}
            </View>
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderCommunityIcon(item) {
    if (item.cdCommunityLevel === CommunitiesConstant.communityLevel.country) {
      return <FeatherIcon name="globe" size={20} color={SRXColor.Purple} />;
    } else if (
      item.cdCommunityLevel ===
        CommunitiesConstant.communityLevel.planningArea ||
      item.cdCommunityLevel === CommunitiesConstant.communityLevel.subzone
    ) {
      return <FeatherIcon name="map" size={20} color={SRXColor.Purple} />;
    } else {
      //private, postal and estate
      return (
        <Image
          source={Communities_Icon_Condo}
          style={{height: 22, width: 22}}
          resizeMode={'contain'}
        />
      );
    }
  }

  renderCommunityName(communityName, isSelected) {
    return (
      <BodyText
        style={[
          Styles.communityNameStyle,
          {
            color: isSelected ? SRXColor.Teal : SRXColor.Black,
          },
        ]}
        numberOfLines={1}>
        {communityName}
      </BodyText>
    );
  }

  renderCheckIcon(item) {
    const {selectedCommunity} = this.state;
    if (
      !ObjectUtil.isEmpty(selectedCommunity) &&
      !ObjectUtil.isEmpty(item) &&
      selectedCommunity.id == item.id
    ) {
      return (
        <FeatherIcon
          name="check"
          size={22}
          color={SRXColor.Teal}
          style={{alignItems: 'flex-end'}}
        />
      );
    }
  }

  render() {
    const {communities} = this.props;
    if (Array.isArray(communities) && communities.length > 0) {
      return (
        <View style={{flex: 1, backgroundColor: SRXColor.SmallBodyBackground}}>
          <ScrollView bounces={false} style={{flex: 1}}>
            {communities.map((item, index) => {
              if (item.id == CommunitiesConstant.singaporeCommunity.id) {
                return this.renderSingaporeCommunityRow(item);
              } else {
                return (
                  <View
                    style={[Styles.rowCommunityContainer]}
                    key={item.id.toString() + ' & ' + item.name}>
                    {this.renderCommunityRow(item, 0)}
                  </View>
                );
              }
            })}
          </ScrollView>
        </View>
      );
    }
  }
}

CommunityOptionsList.propTypes = {
  /**
   * Communities allowing for select
   */
  communities: PropTypes.arrayOf(PropTypes.instanceOf(CommunityItem)),

  /**
   * CommunityItem that are selected
   */
  selectedCommunity: PropTypes.oneOfType([
    PropTypes.instanceOf(CommunityItem),
    PropTypes.object,
  ]),

  /**
   * Function to handle when a communityItem is selected. Will passback the selectedItem.
   */
  onCommunitySelected: PropTypes.func,
};

const Styles = {
  singaporeCommunityContainer: {
    borderRadius: 10,
    borderWidth: 1,
    borderColor: SRXColor.AccordionBackground,

    height: 40,
    alignItems: 'center',
    flexDirection: 'row',
    backgroundColor: SRXColor.White,

    paddingLeft: Spacing.L - Spacing.XS / 2,
    paddingEnd: Spacing.L,
  },

  rowCommunityContainer: {
    borderRadius: 10,
    borderWidth: 1,
    borderColor: SRXColor.AccordionBackground,

    backgroundColor: SRXColor.White,

    paddingTop: Spacing.S,
    paddingBottom: Spacing.S,
    marginBottom: Spacing.XS,
  },

  rowCommunitySubContainer: {
    flexDirection: 'row',
    height: 40,
    alignItems: 'center',

    marginLeft: Spacing.XS,
    marginRight: Spacing.XS,

    borderRadius: 10,
    borderWidth: 1,
  },

  communityNameStyle: {
    marginLeft: Spacing.XS,
    marginRight: Spacing.XS,
    flex: 1,
  },
};

export {CommunityOptionsList};
