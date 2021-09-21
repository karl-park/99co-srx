import React, {Component} from 'react';
import {
  View,
  Image,
  TouchableOpacity,
  ActivityIndicator,
  Share,
  PermissionsAndroid,
  Alert,
} from 'react-native';
import Geolocation from '@react-native-community/geolocation';
import CookieManager from '@react-native-community/cookies';
import {Spacing, Typography} from '../../../styles';
import {SRXColor, IS_IOS, LoadingState, AppConstant} from '../../../constants';
import {
  SmallBodyText,
  BodyText,
  FeatherIcon,
  Button,
  Avatar,
  Separator,
} from '../../../components';
import {Communities_Welcome} from '../../../assets';
import {CommunitiesService} from '../../../services';
import {ObjectUtil, PermissionUtil, CommonUtil} from '../../../utils';
import {CommunityPO} from '../../../dataObject';
import {connect} from 'react-redux';
import {CommunitiesStack} from '../../../config';
import {getCommunities, clearCommunities} from '../../../actions';

class CommunityInvitation extends Component {
  constructor(props) {
    super(props);
    this.onSuccessSignUp = this.onSuccessSignUp.bind(this);
  }

  state = {
    communityPO: null,
    communityLoadingState: LoadingState.Normal,
    hasNoProperty: true,
  };

  componentDidMount() {
    this.loadCommunities();
  }

  loadCommunities() {
    const {userPO, getCommunities, communities, serverDomain} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      CookieManager.get(serverDomain.domainURL).then(cookies => {
        const userSessionTokenCookie = cookies['userSessionToken'];
        if (!ObjectUtil.isEmpty(userSessionTokenCookie)) {
          getCommunities();
        }
      });
      if (
        ObjectUtil.isEmpty(communities) &&
        Array.isArray(communities) &&
        communities.length == 0
      ) {
        this.onCallCurrentLocation();
      }
    } else {
      this.onCallCurrentLocation();
    }
  }

  componentDidUpdate(prevProps) {
    const {
      userPO,
      getCommunities,
      clearCommunities,
      communities,
      serverDomain,
    } = this.props;
    if (prevProps.userPO !== userPO) {
      if (!ObjectUtil.isEmpty(userPO)) {
        CookieManager.get(serverDomain.domainURL).then(cookies => {
          const userSessionTokenCookie = cookies['userSessionToken'];
          if (!ObjectUtil.isEmpty(userSessionTokenCookie)) {
            getCommunities();
          }
        });
        if (
          ObjectUtil.isEmpty(communities) &&
          Array.isArray(communities) &&
          communities.length == 0
        ) {
          this.onCallCurrentLocation();
        }
      } else {
        clearCommunities();
        this.onCallCurrentLocation();
      }
    }
    if (
      prevProps.properties !== this.props.properties &&
      !ObjectUtil.isEmpty(this.props.properties) &&
      this.props.properties.length === 1
    ) {
      this.setState({hasNoProperty: false});
    }
  }

  onCallCurrentLocation = () => {
    if (IS_IOS) {
      //to enable or disable current location options
      this.setState({communityLoadingState: LoadingState.Loading}, () => {
        this.getCurrentLocation();
      });
    } else {
      this.requestAndroidAccessFineLocation();
    }
  };

  requestAndroidAccessFineLocation = () => {
    PermissionUtil.requestAndroidAccessFineLocation().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        //to enable or disable current location options
        this.setState({communityLoadingState: LoadingState.Loading}, () => {
          this.getCurrentLocation();
        });
      } else {
        this.setState({communityLoadingState: LoadingState.Loading}, () => {
          this.getSampleCommunity();
        });
      }
    });
  };

  getCurrentLocation = () => {
    Geolocation.getCurrentPosition(
      position => {
        const latLongCoord = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        };
        this.getCommunityByLocation(latLongCoord);
      },
      error => {
        console.log(error);
        if (
          error.message &&
          (error.message === 'User denied access to location services.' ||
            error.message === 'Location services disabled.')
        ) {
          if (IS_IOS) {
            this.getSampleCommunity();
          }
        }
      },
    );
  };

  getCommunityByLocation({latitude, longitude}) {
    CommunitiesService.getCommunityByLocation({
      latitude,
      longitude,
    })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {result, community} = response;

          if (result === 'success') {
            if (!ObjectUtil.isEmpty(community)) {
              this.setState({
                communityPO: new CommunityPO(community),
                communityLoadingState: LoadingState.Loaded,
              });
            } else {
              this.getSampleCommunity();
            }
          }
        }
      })
      .catch(error => {
        this.setState({communityLoadingState: LoadingState.Normal});
        console.log(error);
      });
  }

  getSampleCommunity() {
    CommunitiesService.getSampleCommunity()
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {result, community} = response;

          if (result === 'success' && !ObjectUtil.isEmpty(community)) {
            this.setState({
              communityPO: new CommunityPO(community),
              communityLoadingState: LoadingState.Loaded,
            });
          }
        }
      })
      .catch(error => {
        this.setState({communityLoadingState: LoadingState.Normal});
        console.log(error);
      });
  }

  onPressJoinCommunity = () => {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      this.directToAddPropertyModal();
    } else {
      this.directToSignInRegisterModal();
    }
  };

  directToSignInRegisterModal = () => {
    const passProps = {
      onSuccessSignUp: this.onSuccessSignUp,
    };
    CommunitiesStack.showSignInRegisterModal(passProps);
  };

  onSuccessSignUp = () => {
    this.directToAddPropertyModal();
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

  inviteNeighbours = () => {
    const {serverDomain} = this.props;
    let msg =
      'Come join me in the SRX community and get connected to your neighbourhood!😊';
    const url = serverDomain.domainURL + '/community';
    msg = msg + ' ' + url;
    const content = {
      message: msg,
    };
    const options = {
      dialogTitle: 'Invite Neighbours', //iOS only
    };

    Share.share(content, options);
  };

  renderbeforeJoinCommunityInvitation() {
    return (
      <View
        style={[
          Styles.container,
          {flex: 1, flexDirection: 'row', padding: Spacing.S},
        ]}>
        {this.renderInfoLayout()}
        {this.renderImageLayout()}
      </View>
    );
  }

  renderafterJoinCommunityInvitation() {
    return (
      <View
        style={[
          Styles.container,
          {paddingHorizontal: Spacing.M, paddingVertical: Spacing.XS},
        ]}>
        {this.renderText()}
        {this.renderInviteNeighbours()}
        {this.renderUpdateCommunityName()}
      </View>
    );
  }

  renderText() {
    return (
      <View
        style={[
          Styles.buttonContainer,
          {backgroundColor: SRXColor.Purple, borderColor: SRXColor.Purple},
        ]}>
        <TouchableOpacity
          onPress={this.props.onNewPostInitiate}
          style={{flexDirection: 'row'}}>
          <FeatherIcon name={'edit-3'} size={20} color={'white'} />
          <BodyText style={{color: SRXColor.White, marginLeft: Spacing.XS}}>
            What's happening in your community?
          </BodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderInviteNeighbours() {
    return (
      <TouchableOpacity
        style={{marginTop: Spacing.XS}}
        onPress={() => this.inviteNeighbours()}>
        <BodyText style={{color: SRXColor.Teal, textAlign: 'center'}}>
          Invite Neighbours
        </BodyText>
      </TouchableOpacity>
    );
  }

  renderInfoLayout() {
    return (
      <View style={{alignItems: 'center', flex: 4, marginTop: Spacing.XS}}>
        {this.renderMember()}
        {this.renderJoinCommunity()}
      </View>
    );
  }

  renderImageLayout() {
    return (
      <View style={{alignItems: 'center', flex: 1}}>
        <Image
          source={Communities_Welcome}
          resizeMode={'cover'}
          style={{width: 90, height: 80}}
        />
      </View>
    );
  }

  renderMember() {
    const {communityPO} = this.state;
    if (!ObjectUtil.isEmpty(communityPO)) {
      return (
        <SmallBodyText style={{textAlign: 'center', color: SRXColor.Purple}}>
          {communityPO.membersTotal}{' '}
          <SmallBodyText style={{color: SRXColor.Gray}}>
            members in
          </SmallBodyText>{' '}
          <SmallBodyText
            style={{color: SRXColor.Black, textTransform: 'capitalize'}}>
            {communityPO.name}
          </SmallBodyText>
        </SmallBodyText>
      );
    }
  }

  renderJoinCommunity() {
    const {userPO} = this.props;
    return (
      <View style={[Styles.buttonContainer, {justifyContent: 'center'}]}>
        <TouchableOpacity onPress={() => this.onPressJoinCommunity()}>
          <SmallBodyText style={{color: SRXColor.Teal}}>
            {!ObjectUtil.isEmpty(userPO)
              ? 'Add your property to get started'
              : 'Join your community and start posting!'}
          </SmallBodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderUpdateCommunityName() {
    const {userPO, onUpdateCommunityName} = this.props;
    if (
      !ObjectUtil.isEmpty(userPO) &&
      ObjectUtil.isEmpty(userPO.communityAlias) &&
      onUpdateCommunityName
    ) {
      return (
        <View style={{marginTop: Spacing.XS}}>
          <Separator />
          <TouchableOpacity
            style={{paddingTop: Spacing.XS}}
            onPress={() => onUpdateCommunityName()}>
            <View
              style={{
                flexDirection: 'row',
                justifyContent: 'center',
              }}>
              <Avatar
                name={userPO.name}
                imageUrl={CommonUtil.handleImageUrl(userPO.photo)}
                size={20}
                textSize={12}
                borderLess={true}
                backgroundColor={SRXColor.Purple}
              />
              <SmallBodyText style={{marginLeft: Spacing.XS / 2}}>
                Update your{' '}
                <SmallBodyText style={{color: SRXColor.Purple}}>
                  display name
                </SmallBodyText>
                {' and '}
                <SmallBodyText style={{color: SRXColor.Purple}}>
                  picture
                </SmallBodyText>
              </SmallBodyText>
            </View>
          </TouchableOpacity>
        </View>
      );
    }
  }

  render() {
    const {
      communities,
      loadingState,
      hasCommunities,
      reloadCommunityCount,
    } = this.props;
    const {communityLoadingState, hasNoProperty} = this.state;

    if (
      ((loadingState === LoadingState.Loading && hasNoProperty) ||
        communityLoadingState === LoadingState.Loading) &&
      reloadCommunityCount <= 1
    ) {
      return (
        <View
          style={[
            Styles.container,
            Styles.activityIndicatorContainer,
            {padding: Spacing.M},
          ]}>
          <ActivityIndicator />
        </View>
      );
    } else {
      if (
        (!ObjectUtil.isEmpty(communities) &&
          Array.isArray(communities) &&
          communities.length > 0 &&
          (loadingState === LoadingState.Loaded ||
            communityLoadingState === LoadingState.Loaded)) ||
        hasCommunities == true
      ) {
        return <View>{this.renderafterJoinCommunityInvitation()}</View>;
      } else {
        return <View>{this.renderbeforeJoinCommunityInvitation()}</View>;
      }
    }
  }
}

const Styles = {
  container: {
    backgroundColor: SRXColor.White,
    borderBottomLeftRadius: 10,
    borderBottomRightRadius: 10,
  },
  buttonContainer: {
    marginTop: Spacing.XS,
    borderRadius: IS_IOS ? 20 : 22.5,
    borderColor: SRXColor.Teal,
    borderWidth: 1,
    alignItems: 'center',
    height: IS_IOS ? 40 : 45,
    flexDirection: 'row',
    paddingHorizontal: Spacing.S,
  },
  activityIndicatorContainer: {
    alignItems: 'center',
    flex: 1,
    height: 100,
    justifyContent: 'center',
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    properties: state.myPropertiesData.list,
    loadingState: state.communitiesData.loadingState,
    communities: state.communitiesData.list,
    serverDomain: state.serverDomain,
  };
};

export default connect(
  mapStateToProps,
  {getCommunities, clearCommunities},
)(CommunityInvitation);
