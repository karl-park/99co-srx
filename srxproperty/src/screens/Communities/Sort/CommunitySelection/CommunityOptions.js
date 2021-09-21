import React, {Component} from 'react';
import {View, TouchableOpacity, ScrollView, Alert} from 'react-native';
import {connect} from 'react-redux';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {FeatherIcon} from '../../../../components';
import {SRXColor} from '../../../../constants';
import {CommunityItem} from '../../../../dataObject';
import {Spacing} from '../../../../styles';
import {CommunitiesUtil, ObjectUtil, CommonUtil} from '../../../../utils';
import {CommunitiesConstant} from '../../Constants';
import {CommunityOptionsList} from './CommunityOptionsList';
import CommunityOptionsMap from './CommunityOptionsMap';

class CommunityOptions extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: 'Your communities',
        },
        rightButtons: [
          {
            id: 'btn_Done',
            text: 'Done',
            color: SRXColor.TextLink,
          },
        ],
      },
    };
  }

  static defaultProps = {
    initialSelectedCommunity: CommunitiesConstant.singaporeCommunity,
    allowingCommunities: [],
  };

  constructor(props) {
    super(props);

    if (
      !ObjectUtil.isEmpty(props) &&
      !ObjectUtil.isEmpty(props.initialSelectedCommunity)
    ) {
      this.state = {
        selectedCommunity: props.initialSelectedCommunity,
      };
    }

    Navigation.events().bindComponent(this);

    this.setNavigationOptions();
  }

  setNavigationOptions() {
    const componentId = this.props.componentId;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'blue')
        .then(icon_close => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              leftButtons: [
                {
                  id: 'btn_close',
                  icon: icon_close,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          console.log(error);
          reject(error);
        });
    }); //end of promise
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_Done') {
      //update selection
      this.updateSelectedCommunity();
    } else if (buttonId === 'btn_close') {
      Navigation.dismissModal('CommunityOptions');
    }
  }

  updateSelectedCommunity() {
    const {onCommunitySelected} = this.props;
    if (onCommunitySelected) {
      const {selectedCommunity} = this.state;
      Navigation.dismissModal('CommunityOptions').then(() => {
        onCommunitySelected(selectedCommunity);
      });
    }
  }

  renderMap() {
    const {communities} = this.props;
    const {selectedCommunity} = this.state;
    console.log(communities);
    return (
      <CommunityOptionsMap
        community={selectedCommunity}
        communities={communities}
      />
    );
  }

  renderList() {
    const {communities, allowingCommunities} = this.props;
    const {selectedCommunity} = this.state;
    var displayingCommunities = [];
    if (!ObjectUtil.isEmpty(allowingCommunities)) {
      displayingCommunities = allowingCommunities;
    } else if (!ObjectUtil.isEmpty(communities)) {
      displayingCommunities = communities;
    }

    if (
      Array.isArray(displayingCommunities) &&
      displayingCommunities.length > 0
    ) {
      return (
        <CommunityOptionsList
          communities={displayingCommunities}
          selectedCommunity={selectedCommunity}
          onCommunitySelected={item => this.setState({selectedCommunity: item})}
        />
      );
    }
  }

  render() {
    return (
      <View style={{flex: 1}}>
        {this.renderMap()}
        {this.renderList()}
      </View>
    );
  }
}

CommunityOptions.propTypes = {
  /**
   * Communities allowing for select. Will be using this rather than `communities` if this is provided
   */
  allowingCommunities: PropTypes.arrayOf(PropTypes.instanceOf(CommunityItem)),

  /**
   * CommunityItem that are selected
   */
  initialSelectedCommunity: PropTypes.oneOfType([
    PropTypes.instanceOf(CommunityItem),
    PropTypes.object,
  ]),

  /**
   * Function to handle when a communityItem is selected. Will passback the selectedItem.
   */
  onCommunitySelected: PropTypes.func,
};

const mapStateToProps = state => {
  return {
    communities: CommunitiesUtil.convertCommunityTopDownPOArray(
      state.communitiesData.list,
    ),
  };
};

export default connect(
  mapStateToProps,
  null,
)(CommunityOptions);
