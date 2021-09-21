import React, {Component} from 'react';
import {View, SafeAreaView, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {Subtext, Button} from '../../../../../../../components';

import {LoadingState, SRXColor} from '../../../../../../../constants';
import {Spacing} from '../../../../../../../styles';
import {CommunitiesGRCSponsoredPostItem} from '../GRCPostItem';
import {CommunityPO, CommunityPostPO} from '../../../../../../../dataObject';
import {ObjectUtil} from '../../../../../../../utils';

class CommunitiesGRCPreviewPost extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loadingState: props.submitBtnState,
    };
  }

  componentDidUpdate(prevProps) {
    if (prevProps.submitBtnState !== this.props.submitBtnState) {
      this.setState({loadingState: this.props.submitBtnState});
    }
  }

  renderGRCPostItem() {
    const {communityPostPO} = this.props;
    if (!ObjectUtil.isEmpty(communityPostPO)) {
      return (
        <CommunitiesGRCSponsoredPostItem communityPostPO={communityPostPO} />
      );
    }
    return <View />;
  }

  renderPostDescription() {
    const {propertyType, selectedCommunities, community} = this.props;
    if (!ObjectUtil.isEmpty(propertyType)) {
      var selectedGRCAndZones = propertyType.key + ' residents';
      selectedGRCAndZones += !ObjectUtil.isEmpty(community)
        ? ' in ' + community.name
        : '';
      if (
        Array.isArray(selectedCommunities) &&
        selectedCommunities.length > 0
      ) {
        if (!ObjectUtil.isEmpty(community)) {
          selectedGRCAndZones +=
            ' and ' + selectedCommunities.length + ' other zones';
        } else {
          selectedGRCAndZones += ' in ' + selectedCommunities.length + ' zones';
        }
      }
      return (
        <View
          style={{
            flexDirection: 'row',
            paddingLeft: Spacing.M,
            paddingRight: Spacing.M,
            flex: 1,
          }}>
          <Subtext style={{marginRight: Spacing.XS / 2}}>
            {'Your post will be targeting '}
            <Subtext style={{color: SRXColor.Purple}}>
              {selectedGRCAndZones}
            </Subtext>
          </Subtext>
        </View>
      );
    }
    return <View />;
  }

  renderActionButtons() {
    const {changeEditMode, createNewPost} = this.props;
    const {loadingState} = this.state;
    var buttonDisabled = loadingState == LoadingState.Loading;
    return (
      <View
        style={{
          marginVertical: Spacing.L,
          alignItems: 'center',
        }}>
        <Button
          disabled={buttonDisabled}
          buttonType={Button.buttonTypes.primary}
          buttonStyle={{width: 215, justifyContent: 'center'}}
          onPress={() => createNewPost()}>
          {buttonDisabled ? 'Submitting...' : 'Submit'}
        </Button>
        <TouchableOpacity
          onPress={() => changeEditMode()}
          style={{marginTop: Spacing.L}}>
          <Subtext style={{color: SRXColor.Teal}}>Make changes</Subtext>
        </TouchableOpacity>
      </View>
    );
  }

  render() {
    return (
      <View style={{flex: 1}}>
        {this.renderGRCPostItem()}
        {this.renderPostDescription()}
        {this.renderActionButtons()}
      </View>
    );
  }
}

CommunitiesGRCPreviewPost.propTypes = {
  communityPostPO: PropTypes.instanceOf(CommunityPostPO),

  propertyType: PropTypes.object,

  community: PropTypes.instanceOf(CommunityPO),

  selectedCommunities: PropTypes.arrayOf(PropTypes.instanceOf(CommunityPO)),

  submitBtnState: PropTypes.string,

  changeEditMode: PropTypes.func.isRequired,

  createNewPost: PropTypes.func.isRequired,
};
export {CommunitiesGRCPreviewPost};
