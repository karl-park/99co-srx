import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {
  View,
  Image,
  Switch,
  ProgressBarAndroid,
  TouchableOpacity,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import {ProgressView} from '@react-native-community/progress-view';
import {
  BodyText,
  FeatherIcon,
  Heading1,
  Separator,
  Subtext,
} from '../../../../../../../components';
import {Communities_Icon_GRC_Addresses} from '../../../../../../../assets';
import {InputStyles, Spacing} from '../../../../../../../styles';
import {IS_IOS, LoadingState, SRXColor} from '../../../../../../../constants';
import {CommunitiesBusinessService} from '../../../../../../../services';
import {ObjectUtil, StringUtil} from '../../../../../../../utils';
import {GRCNewPostConstant} from '../Constants';
import {CommunitiesStack} from '../../../../../../../config';
import {CommunityPO} from '../../../../../../../dataObject';

class CommunitiesGRCPostSelection extends Component {
  static options(passProps) {
    return {
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true,
      },
    };
  }

  constructor(props) {
    super(props);

    this.state = {
      showAllMembers: props.showAllMembersInGRC,
      selectedPropertyType:
        props.propertyType ?? GRCNewPostConstant.allResidentialType,
      selectedCommunities: props.selectedCommunities ?? [],
      selectedMemberCount: 0,
      totalMemberCount: 0,
      selectedCommunitiesError: props.selectedCommunitiesError ?? '',
      xResidentsLoadingState: LoadingState.Normal,
    };

    this.onSwitchChange = this.onSwitchChange.bind(this);
    this.showCommunities = this.showCommunities.bind(this);
    this.onSelectCommunities = this.onSelectCommunities.bind(this);
    this.onRemoveCommunity = this.onRemoveCommunity.bind(this);
  }

  componentDidMount() {
    this.getSubzoneMemberCount();
  }

  componentDidUpdate(prevProps) {
    if (
      prevProps.selectedCommunitiesError !== this.props.selectedCommunitiesError
    ) {
      this.setState({
        selectedCommunitiesError: this.props.selectedCommunitiesError,
      });
    }
  }

  getSubzoneMemberCount() {
    const {community} = this.props;
    const {
      selectedCommunities,
      selectedPropertyType,
      showAllMembers,
    } = this.state;

    var communitiesIds = '';
    var cdResearchSubtypes = '';

    if (showAllMembers) {
      communitiesIds = community ? community.id + ',' : '';
    }

    //for selected communities
    if (Array.isArray(selectedCommunities) && selectedCommunities.length > 0) {
      communitiesIds += selectedCommunities.map(item => {
        return item.id;
      });
      cdResearchSubtypes = selectedPropertyType
        ? selectedPropertyType.subTypes
        : '';
    }

    if (communitiesIds) {
      this.setState({xResidentsLoadingState: LoadingState.Loading}, () => {
        this.getMemberCount(communitiesIds, cdResearchSubtypes);
      });
    } else {
      this.setState({
        selectedMemberCount: 0,
        totalMemberCount: 0,
      });
    }
  }

  getMemberCount(communitiesIds, cdResearchSubtypes) {
    CommunitiesBusinessService.getMemberCount({
      communityIds: communitiesIds.toString(),
      cdResearchSubtypes: cdResearchSubtypes.toString(),
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {result, selected, total} = response;
          if (result == 'success') {
            this.setState({
              selectedMemberCount: selected,
              totalMemberCount: total,
              xResidentsLoadingState: LoadingState.Loaded,
            });
          } else {
            this.setState({xResidentsLoadingState: LoadingState.Failed});
          }
        }
      })
      .catch(error => {
        console.log(error);
        this.setState({xResidentsLoadingState: LoadingState.Failed});
      });
  }

  onSwitchChange(value) {
    this.setState({showAllMembers: value}, () => {
      this.getSubzoneMemberCount();
      const {onChangeSwitch} = this.props;
      if (onChangeSwitch) {
        onChangeSwitch(value);
      }
    });
  }

  onUpdatePropertyType(item) {
    const {selectedPropertyType} = this.state;
    if (
      !ObjectUtil.isEmpty(selectedPropertyType) &&
      !ObjectUtil.isEmpty(item) &&
      selectedPropertyType.value !== item.value
    ) {
      this.setState({selectedPropertyType: item}, () => {
        //note: update audience count everytime updates propertytype
        this.getSubzoneMemberCount();

        const {onUpdatePropertyType} = this.props;
        if (onUpdatePropertyType) {
          onUpdatePropertyType(item);
        }
      });
    }
  }

  showCommunities() {
    const {selectedCommunities} = this.state;
    const {communitiesArray} = this.props;
    var communitiesIds = selectedCommunities.map(item => {
      return item.id;
    });
    CommunitiesStack.showCommunitySelectionModal({
      onSelectCommunities: this.onSelectCommunities,
      selectedCommunities: communitiesIds,
      communitiesArray: communitiesArray,
    });
  }

  onSelectCommunities(items) {
    const {selectedCommunitiesError} = this.state;
    var error = selectedCommunitiesError;
    if (Array.isArray(items) && items.length > 0) {
      error = '';
    }
    this.setState(
      {selectedCommunities: items, selectedCommunitiesError: error},
      () => {
        this.updateCommunitiesAndXResidentBar();
      },
    );
  }

  onRemoveCommunity(community) {
    const {selectedCommunities} = this.state;
    var tempCommunities = selectedCommunities.filter(
      item => item.value !== community.value,
    );
    this.setState({selectedCommunities: tempCommunities}, () => {
      this.updateCommunitiesAndXResidentBar();
    });
  }

  updateCommunitiesAndXResidentBar() {
    this.getSubzoneMemberCount();

    const {onUpdateSelectedCommunities} = this.props;
    if (onUpdateSelectedCommunities) {
      onUpdateSelectedCommunities(this.state.selectedCommunities);
    }
  }

  //start
  renderToggleMemberSelection() {
    const {showAllMembers} = this.state;
    const {community} = this.props;
    if (!ObjectUtil.isEmpty(community)) {
      return (
        <View
          style={{
            flexDirection: 'row',
            marginTop: Spacing.L,
            marginHorizontal: Spacing.L,
          }}>
          <BodyText>
            {'All members in '}
            {community.name}
          </BodyText>
          <View
            style={{
              alignItems: 'flex-end',
              justifyContent: 'flex-end',
              flex: 1,
            }}>
            <Switch
              trackColor={{
                true: SRXColor.TextLink,
                false: SRXColor.LightGray,
              }}
              thumbColor={IS_IOS ? null : SRXColor.White}
              value={showAllMembers}
              onValueChange={value => this.onSwitchChange(value)}
            />
          </View>
        </View>
      );
    }

    return <View />;
  }

  renderCommunityName() {
    const {selectedCommunitiesError} = this.state;
    return (
      <View style={{marginVertical: Spacing.M, marginHorizontal: Spacing.L}}>
        <TouchableOpacity onPress={() => this.showCommunities()}>
          <View
            style={[
              InputStyles.singleLineTextHeight,
              {
                borderRadius: IS_IOS ? 20 : 22.5,
                flexDirection: 'row',
                alignItems: 'center',
                borderColor: SRXColor.LightGray,
                borderWidth: 1,
              },
            ]}>
            <FeatherIcon
              name="search"
              size={20}
              color={SRXColor.Gray}
              style={{alignSelf: 'center', marginHorizontal: Spacing.XS}}
            />
            <BodyText style={[InputStyles.text, {color: SRXColor.Gray}]}>
              Enter or choose planning area/subzone
            </BodyText>
          </View>
        </TouchableOpacity>
        {selectedCommunitiesError != '' ? (
          <Subtext style={{color: SRXColor.Red, marginTop: Spacing.XS / 2}}>
            {selectedCommunitiesError}
          </Subtext>
        ) : null}
      </View>
    );
  }

  renderSelectedCommunities() {
    const {selectedCommunities} = this.state;
    if (
      !ObjectUtil.isEmpty(selectedCommunities) &&
      selectedCommunities.length > 0
    ) {
      return (
        <View
          style={{
            flexDirection: 'row',
            flexWrap: 'wrap',
            marginBottom: Spacing.XS,
            paddingTop: Spacing.XS,
            paddingBottom: Spacing.XS,
          }}>
          <ScrollView horizontal={true} showsHorizontalScrollIndicator={false}>
            {selectedCommunities.map((item, index) => {
              return this.renderEachCommunity(item, index);
            })}
          </ScrollView>
        </View>
      );
    }
  }

  renderEachCommunity(item, index) {
    return (
      <View
        style={{
          borderColor: SRXColor.Teal,
          borderWidth: 1,
          borderRadius: 20,
          paddingLeft: Spacing.S,
          paddingRight: Spacing.S,
          paddingTop: Spacing.XS / 2,
          paddingBottom: Spacing.XS / 2,
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'center',
          marginRight: Spacing.XS,
          marginLeft: index == 0 ? Spacing.L : 0,
        }}>
        <Subtext>{item.name}</Subtext>
        <TouchableOpacity
          onPress={() => this.onRemoveCommunity(item)}
          style={{marginLeft: Spacing.XS}}>
          <FeatherIcon name={'x-circle'} size={16} color={SRXColor.Teal} />
        </TouchableOpacity>
      </View>
    );
  }

  renderPropertyType() {
    const {selectedCommunities} = this.state;
    if (
      !ObjectUtil.isEmpty(selectedCommunities) &&
      Array.isArray(selectedCommunities) &&
      selectedCommunities.length > 0
    ) {
      return (
        <View style={{marginHorizontal: Spacing.L}}>
          <BodyText style={{fontWeight: 'bold'}}>Property type</BodyText>
          <View style={{flexDirection: 'row', marginVertical: Spacing.M}}>
            {GRCNewPostConstant.propertyTypes.map(item =>
              this.renderPropertyTypeItem(item),
            )}
          </View>
        </View>
      );
    }
  }

  renderPropertyTypeItem(item) {
    const {selectedPropertyType} = this.state;
    if (
      !ObjectUtil.isEmpty(item) &&
      !ObjectUtil.isEmpty(selectedPropertyType)
    ) {
      return (
        <TouchableOpacity
          onPress={() => this.onUpdatePropertyType(item)}
          style={{
            borderRadius: item.value === selectedPropertyType.value ? 10 : 0,
            borderWidth: item.value === selectedPropertyType.value ? 1 : 0,
            borderColor:
              item.value === selectedPropertyType.value
                ? SRXColor.Teal
                : SRXColor.White,
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.XS,
            marginRight: Spacing.S,
            alignItems: 'center',
          }}>
          <Image source={item.icon} style={{width: 30, height: 30}} />
          <BodyText>{item.key}</BodyText>
        </TouchableOpacity>
      );
    }
  }

  renderSelectedAudience() {
    const {
      selectedMemberCount,
      totalMemberCount,
      xResidentsLoadingState,
    } = this.state;
    if (xResidentsLoadingState === LoadingState.Loading) {
      return (
        <View
          style={{height: 90, alignItems: 'center', justifyContent: 'center'}}>
          <ActivityIndicator size={'small'} />
        </View>
      );
    } else {
      var progress = 0.0;
      if (totalMemberCount > 0) {
        progress = parseFloat(
          (selectedMemberCount / totalMemberCount).toFixed(1),
        );
      }
      return (
        <View style={{marginHorizontal: Spacing.L}}>
          {this.renderSeparater()}
          <BodyText style={{fontWeight: 'bold'}}>
            Your selected audience includes
          </BodyText>
          <View
            style={{
              flexDirection: 'row',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 'bold',
              marginTop: Spacing.M,
            }}>
            <Image
              source={Communities_Icon_GRC_Addresses}
              style={{width: 26, height: 26, marginRight: Spacing.XS}}
              resizeMode={'contain'}
            />
            <Heading1 style={{color: SRXColor.Purple, fontWeight: 'bold'}}>
              {StringUtil.formatThousand(selectedMemberCount)} addresses
            </Heading1>
          </View>
          {IS_IOS ? (
            <ProgressView
              progressTintColor={SRXColor.Purple}
              progress={progress}
              style={{transform: [{scaleX: 1.0}, {scaleY: 1.5}], height: 25}}
            />
          ) : (
            <ProgressBarAndroid
              styleAttr="Horizontal"
              indeterminate={false}
              progress={progress}
              color={SRXColor.Purple}
              progressTintColor={SRXColor.Purple}
            />
          )}

          <View style={{flexDirection: 'row', alignItems: 'flex-end'}}>
            <Subtext style={{color: SRXColor.Purple}}>0</Subtext>
            <View style={{alignItems: 'flex-end', flex: 1}}>
              <Subtext style={{color: SRXColor.Purple}}>
                {StringUtil.formatThousand(totalMemberCount)}
              </Subtext>
            </View>
          </View>
        </View>
      );
    }
  }

  renderSeparater() {
    return <Separator edgeInset={{top: Spacing.M, bottom: Spacing.M}} />;
  }

  render() {
    return (
      <View
        style={{
          paddingTop: Spacing.L,
          paddingBottom: Spacing.L,
          borderBottomLeftRadius: 10,
          borderBottomRightRadius: 10,
          backgroundColor: SRXColor.White,
        }}>
        <BodyText style={{fontWeight: 'bold', marginHorizontal: Spacing.L}}>
          Select audience
        </BodyText>
        <Subtext
          style={{marginTop: Spacing.XS / 2, marginHorizontal: Spacing.L}}>
          You can choose all in GRC, or type a particular location
        </Subtext>
        {this.renderToggleMemberSelection()}
        <View style={{marginHorizontal: Spacing.L}}>
          {this.renderSeparater()}
        </View>
        {this.renderCommunityName()}
        {this.renderSelectedCommunities()}
        {this.renderPropertyType()}
        {this.renderSelectedAudience()}
      </View>
    );
  }
}

CommunitiesGRCPostSelection.propTypes = {
  community: PropTypes.instanceOf(CommunityPO).isRequired,

  showAllMembersInGRC: PropTypes.bool,

  selectedCommunities: PropTypes.arrayOf(CommunityPO),

  selectedCommunitiesError: PropTypes.string,

  propertyType: PropTypes.object,

  onChangeSwitch: PropTypes.func,

  onUpdatePropertyType: PropTypes.func,

  onUpdateSelectedCommunities: PropTypes.func,
};

export {CommunitiesGRCPostSelection};
