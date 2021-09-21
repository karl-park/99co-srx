import React, {Component} from 'react';
import {View, TouchableOpacity, ScrollView, Alert} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {SRXColor} from '../../../constants';
import {
  FeatherIcon,
  Separator,
  Heading2,
  SmallBodyText,
  Button,
  TouchableHighlight,
  BodyText,
} from '../../../components';
import {Spacing, Typography} from '../../../styles';
import {ObjectUtil, CommunitiesUtil} from '../../../utils';
import {CommunitiesConstant} from '../Constants';
import {CommunityItem} from '../../../dataObject';
import {CommunitiesStack} from '../../../config';

class CommunitiesModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  static defaultProps = {
    title: 'Select Community',
    selectedCommunity: CommunitiesConstant.singaporeCommunity,
    allowingCommunities: [],
  };

  constructor(props) {
    super(props);
    this.selectCommunity = this.selectCommunity.bind(this);
  }

  onCloseModal = () => {
    return Navigation.dismissModal(this.props.componentId);
  };

  selectCommunity(communityItem) {
    const {onCommunitySelected} = this.props;
    if (onCommunitySelected) {
      onCommunitySelected(communityItem);
      this.onCloseModal();
    }
  }

  onPressJoinCommunity = () => {
    const {userPO} = this.props;
    this.onCloseModal().then(() => {
      if (!ObjectUtil.isEmpty(userPO)) {
        this.directToAddPropertyModal();
      } else {
        this.directToSignInRegisterModal();
      }
    });
  };

  directToSignInRegisterModal = () => {
    CommunitiesStack.showSignInRegisterModal();
  };

  directToAddPropertyModal = () => {
    const passProps = {
      onAddPropertyPressed: this.onAddPropertyPressed.bind(this),
    };

    CommunitiesStack.showAddPropertyModal(passProps);
  };

  onAddPropertyPressed = () => {
    const {onAddPropertyPressed} = this.props;

    if (onAddPropertyPressed) {
      onAddPropertyPressed();
    }
  };

  renderCommunitiesModal() {
    const {communities, allowingCommunities} = this.props;
    const haveCommunityList = !(
      ObjectUtil.isEmpty(communities) && ObjectUtil.isEmpty(allowingCommunities)
    );
    return (
      <View style={Styles.communitiesModalSubContainer}>
        {this.renderModalHeader()}
        <Separator />
        <ScrollView bounces={false}>
          {haveCommunityList
            ? this.renderCommunityOptions()
            : this.renderSGCommunity()}
        </ScrollView>
      </View>
    );
  }

  renderModalHeader() {
    const {title} = this.props;
    return (
      <View style={Styles.communitiesRowContainer}>
        <Heading2 style={{flex: 1}}>{title}</Heading2>
        <Button
          leftView={<FeatherIcon name="x" size={24} color={'gray'} />}
          onPress={() => this.onCloseModal()}
        />
      </View>
    );
  }

  renderSingaporeCommunityRow() {
    return this.renderCommunityRow(CommunitiesConstant.singaporeCommunity, 0);
  }

  renderCommunityOptions() {
    const {communities, allowingCommunities} = this.props;
    var displayingCommunities = [];
    if (!ObjectUtil.isEmpty(allowingCommunities)) {
      displayingCommunities = allowingCommunities;
    } else if (!ObjectUtil.isEmpty(communities)) {
      displayingCommunities = communities;
    }

    return (
      <View>
        {displayingCommunities.map((item, index) => {
          //special logic for Singapore Community if it is showing in lowest level
          if (item.id == CommunitiesConstant.singaporeCommunity.id) {
            return (
              <View key={item.id.toString() + ' & ' + item.name}>
                {this.renderSingaporeCommunityRow()}
                {item.childs.map(subItem => {
                  return this.renderCommunityRow(subItem, 0);
                })}
              </View>
            );
          } else {
            return this.renderCommunityRow(item, 0);
          }
        })}
      </View>
    );
  }

  renderCommunityRow(community, indent) {
    const {selectedCommunity} = this.props;
    if (!ObjectUtil.isEmpty(community)) {
      return (
        <View key={community.id.toString()}>
          <TouchableHighlight onPress={() => this.selectCommunity(community)}>
            <View
              style={{
                flexDirection: 'row',
                backgroundColor: SRXColor.White,
                paddingVertical: Spacing.S,
                paddingRight: Spacing.M,
                paddingLeft: Spacing.S + indent * Spacing.M,
              }}>
              <BodyText
                style={{
                  flex: 1,
                  minHeight: 20,
                }}>
                {community.name}
              </BodyText>
              {!ObjectUtil.isEmpty(selectedCommunity) &&
              community.id == selectedCommunity.id ? (
                <FeatherIcon name="check" size={20} color={SRXColor.Teal} />
              ) : null}
            </View>
          </TouchableHighlight>
          {community.childs.map((item, index) => {
            return this.renderCommunityRow(item, indent + 1);
          })}
          {indent === 0 ? (
            <Separator edgeInset={{left: Spacing.M, right: Spacing.M}} />
          ) : null}
        </View>
      );
    }
  }

  renderSGCommunity() {
    return (
      <View
        style={{
          marginBottom: Spacing.S,
        }}>
        {this.renderSingaporeCommunityRow()}
        {this.renderButtonSection()}
      </View>
    );
  }

  renderButtonSection() {
    return (
      <View style={{alignItems: 'center', marginVertical: Spacing.L}}>
        <SmallBodyText style={{color: SRXColor.Gray}}>
          You are not part of any community yet!
        </SmallBodyText>
        <Button
          textStyle={[Typography.SmallBody, {color: SRXColor.Teal}]}
          buttonStyle={{
            alignItems: 'center',
            justifyContent: 'center',
            marginTop: Spacing.S,
          }}
          buttonType={Button.buttonTypes.secondary}
          onPress={() => this.onPressJoinCommunity()}>
          Join community
        </Button>
      </View>
    );
  }

  render() {
    return (
      <SafeAreaView style={Styles.communitiesOverlay}>
        <View style={Styles.communitiesModalMainContainer}>
          {this.renderCommunitiesModal()}
        </View>
      </SafeAreaView>
    );
  }
}

const Styles = {
  communitiesOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  communitiesModalMainContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  communitiesModalSubContainer: {
    width: '90%',
    opacity: 1,
    borderRadius: 10,
    backgroundColor: SRXColor.White,
    overflow: 'hidden',
  },
  communitiesRowContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginVertical: Spacing.XS,
    paddingHorizontal: Spacing.S,
  },
};

CommunitiesModal.propTypes = {
  /**
   * Title of the modal, if not provided, will show 'Select Community'
   */
  title: PropTypes.string,

  /**
   * Communities allowing for select. Will be using this rather than `communities` if this is provided
   */
  allowingCommunities: PropTypes.arrayOf(PropTypes.instanceOf(CommunityItem)),

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

  onAddPropertyPressed: PropTypes.func,
};

const mapStateToProps = state => {
  return {
    communities: CommunitiesUtil.convertCommunityTopDownPOArray(
      state.communitiesData.list,
    ),
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  null,
)(CommunitiesModal);
