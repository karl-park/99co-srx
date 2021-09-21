import React, {Component} from 'react';
import {View, SafeAreaView, Alert} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {FeatherIcon} from '../../../../../../components';
import {
  AlertMessage,
  LoadingState,
  SRXColor,
} from '../../../../../../constants';
import {
  CommunityPO,
  CommunityPostMediaPO,
  CommunityPostPO,
  CommunityPostBusinessSponsoredPO,
} from '../../../../../../dataObject';
import {
  CommunitiesPostBusinessService,
  CommunitiesBusinessService,
} from '../../../../../../services';
import {Spacing} from '../../../../../../styles';
import {ObjectUtil} from '../../../../../../utils';
import {GRCNewPostConstant} from './Constants';
import {
  CommunitiesGRCPostAction,
  CommunitiesGRCPostDetail,
  CommunitiesGRCPostSelection,
  CommunitiesGRCPreviewPost,
} from './PostCreationContent';
import {BusinessPO} from '../../../../../../dataObject/BusinessPO';
import {CommunitiesConstant} from '../../../../Constants';

const Mode = {
  Create: 'CREATE',
  Edit: 'EDIT',
  Preview: 'PREVIEW',
};

class CommunitiesGRCNewPost extends Component {
  static options(passProps) {
    console.log(passProps);
    return {
      topBar: {
        title: {
          text: 'Create New Post',
        },
        visible: true,
        animate: true,
      },
    };
  }

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.setNavigationOptions();

    this.state = {
      showAllMembersInGRC: false,
      selectedCommunities: [],
      selectedCommunitiesError: '',
      propertyType: GRCNewPostConstant.allResidentialType,

      details: {
        title: '',
        description: '',
        externalLink: '',
      },
      detailErrors: {
        errorTitle: '',
        errorDescription: '',
      },
      photos: [],

      mode: Mode.Create,
      communityPostPO: null,

      submitBtnState: LoadingState.Normal,

      communitiesArray: [],
    };

    this.onClickPreviewPost = this.onClickPreviewPost.bind(this);
    this.onUpdateDetails = this.onUpdateDetails.bind(this);
    this.onChangeSwitch = this.onChangeSwitch.bind(this);
    this.onUpdatePropertyType = this.onUpdatePropertyType.bind(this);
    this.changeEditMode = this.changeEditMode.bind(this);
    this.createNewPost = this.createNewPost.bind(this);
    this.onUpdatePhotos = this.onUpdatePhotos.bind(this);
    this.onUpdateSelectedCommunities = this.onUpdateSelectedCommunities.bind(
      this,
    );
    this.performAddBusinessSponsoredPost = this.performAddBusinessSponsoredPost.bind(
      this,
    );
    this.onDismissModal = this.onDismissModal.bind(this);
  }

  componentDidMount() {
    this.getCommunities();
  }

  getCommunities = () => {
    let communityLevels =
      CommunitiesConstant.communityLevel.subzone +
      ',' +
      CommunitiesConstant.communityLevel.planningArea;
    CommunitiesBusinessService.getCommunities(communityLevels)
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const {communities} = response;
          if (!ObjectUtil.isEmpty(communities) && Array.isArray(communities)) {
            this.setState({communitiesArray: communities});
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
  };

  setNavigationOptions() {
    const {componentId} = this.props;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'black')
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
          reject(error);
        });
    }); //end of promise
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_close') {
      this.onDismissModal();
    }
  }

  onDismissModal() {
    Navigation.dismissModal('GRCNewPost');
  }

  isValidate() {
    const {
      details,
      detailErrors,
      selectedCommunities,
      showAllMembersInGRC,
    } = this.state;
    var isAllValidate = true;
    if (
      !ObjectUtil.isEmpty(details) &&
      details.title == '' &&
      details.description == ''
    ) {
      detailErrors.errorTitle = 'Required field';
      detailErrors.errorDescription = 'Required field';
      isAllValidate = false;
    }

    var communitiesError = '';
    if (
      Array.isArray(selectedCommunities) &&
      selectedCommunities.length == 0 &&
      showAllMembersInGRC == false
    ) {
      communitiesError = 'Please choose all in GRC or a particular location';
      isAllValidate = false;
    }

    this.setState({detailErrors, selectedCommunitiesError: communitiesError});

    return isAllValidate;
  }

  onClickPreviewPost() {
    if (this.isValidate()) {
      const {business, userPO, community} = this.props;
      const {
        details,
        selectedCommunities,
        showAllMembersInGRC,
        photos,
      } = this.state;

      //media
      var medias = [];
      photos.map(item => {
        var mediaItem = {
          id: null,
          type: null,
          mediaType: item.type,
          url: item.uri,
          thumbnailUrl: item.uri,
          dateCreate: null,
          postId: null,
        };
        medias.push(new CommunityPostMediaPO(mediaItem));
      });

      //business
      var businessPO = null;
      if (!ObjectUtil.isEmpty(business)) {
        businessPO = new BusinessPO(business);
        if (showAllMembersInGRC) {
          businessPO.status = 1;
        } else {
          businessPO.status = 0;
        }
      }

      //communities
      var communities = [];
      if (!ObjectUtil.isEmpty(community)) {
        if (showAllMembersInGRC) {
          communities.push(community);
        } else {
          communities = [];
        }
      }

      selectedCommunities.map(item => {
        var community = new CommunityPO(item);
        communities.push(community);
      });

      const newPost = new CommunityPostPO({
        title: details ? details.title : '',
        content: details ? details.description : '',
        externalUrl: details ? details.externalLink : '',
        communities: communities,
        business: businessPO,
        user: userPO,
        media: medias,
      });

      this.setState({mode: Mode.Preview, communityPostPO: newPost});
    }
  }

  onUpdateDetails(details) {
    if (!ObjectUtil.isEmpty(details)) {
      const {detailErrors} = this.state;
      if (details.title !== '' || details.description !== '') {
        detailErrors.errorTitle = '';
        detailErrors.errorDescription = '';
      }
      this.setState({details, detailErrors});
    }
  }

  onUpdatePhotos(photos) {
    this.setState({photos});
  }

  onUpdateSelectedCommunities(selectedCommunities) {
    this.setState({selectedCommunities});
  }

  onChangeSwitch(value) {
    this.setState({showAllMembersInGRC: value});
  }

  onUpdatePropertyType(value) {
    this.setState({propertyType: value});
  }

  changeEditMode() {
    this.setState({mode: Mode.Edit});
  }

  createNewPost() {
    const {propertyType, communityPostPO, photos} = this.state;

    var sponsoredPostPO = new CommunityPostBusinessSponsoredPO({
      post: communityPostPO,
      cdResearchSubtypes: propertyType.subTypes,
    });

    var items = photos.map((item, index) => {
      var tempItem = {
        key: item.name + index,
        value: item,
      };
      return tempItem;
    });

    this.setState({submitBtnState: LoadingState.Loading}, () => {
      this.performAddBusinessSponsoredPost(sponsoredPostPO, items);
    });
  }

  performAddBusinessSponsoredPost(businessPostSponsoredPO, photos) {
    const {onSponsoredPostCreated} = this.props;
    CommunitiesPostBusinessService.addBusinessSponsoredPost({
      postBusinessSponsored: businessPostSponsoredPO,
      photos: photos,
    })
      .then(response => {
        const {error} = response;
        if (!ObjectUtil.isEmpty(error)) {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
          this.setState({submitBtnState: LoadingState.Failed});
        } else {
          const {result} = response;
          if (result === 'success') {
            if (onSponsoredPostCreated) {
              onSponsoredPostCreated();
            }
            this.onDismissModal();
          }
          this.setState({submitBtnState: LoadingState.Loaded});
        }
      })
      .catch(error => {
        console.log(error);
        this.setState({submitBtnState: LoadingState.Failed});
      });
  }

  //rendering method
  renderGRCPostSelection() {
    const {
      showAllMembersInGRC,
      selectedCommunities,
      propertyType,
      selectedCommunitiesError,
      communitiesArray,
    } = this.state;
    const {community} = this.props;
    return (
      <View style={{backgroundColor: SRXColor.LightGray}}>
        <CommunitiesGRCPostSelection
          community={community}
          showAllMembersInGRC={showAllMembersInGRC}
          selectedCommunities={selectedCommunities}
          selectedCommunitiesError={selectedCommunitiesError}
          propertyType={propertyType}
          onChangeSwitch={this.onChangeSwitch}
          onUpdatePropertyType={this.onUpdatePropertyType}
          onUpdateSelectedCommunities={this.onUpdateSelectedCommunities}
          communitiesArray={communitiesArray}
        />
      </View>
    );
  }

  renderGRCPostDetail() {
    const {details, photos, detailErrors} = this.state;
    return (
      <View
        style={{paddingTop: Spacing.M, backgroundColor: SRXColor.LightGray}}>
        <CommunitiesGRCPostDetail
          details={details}
          detailErrors={detailErrors}
          photos={photos}
          onUpdateDetails={this.onUpdateDetails}
          onUpdatePhotos={this.onUpdatePhotos}
        />
      </View>
    );
  }

  renderActionButton() {
    return (
      <View
        style={{paddingTop: Spacing.M, backgroundColor: SRXColor.LightGray}}>
        <CommunitiesGRCPostAction
          componentId={this.props.componentId}
          onPreviewPost={this.onClickPreviewPost}
        />
      </View>
    );
  }

  renderGRCPostPreview() {
    const {
      communityPostPO,
      propertyType,
      selectedCommunities,
      submitBtnState,
      showAllMembersInGRC,
    } = this.state;
    const {community} = this.props;
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: SRXColor.White,
          paddingTop: Spacing.L,
          paddingBottom: Spacing.L,
        }}>
        <CommunitiesGRCPreviewPost
          communityPostPO={communityPostPO}
          propertyType={propertyType}
          community={showAllMembersInGRC ? community : null}
          selectedCommunities={selectedCommunities}
          submitBtnState={submitBtnState}
          changeEditMode={this.changeEditMode}
          createNewPost={this.createNewPost}
        />
      </View>
    );
  }

  render() {
    const {mode} = this.state;
    if (mode === Mode.Create || mode === Mode.Edit) {
      return (
        <SafeAreaView style={{flex: 1}}>
          <KeyboardAwareScrollView
            style={{flex: 1, backgroundColor: SRXColor.White}}>
            <View>
              {this.renderGRCPostSelection()}
              {this.renderGRCPostDetail()}
              {this.renderActionButton()}
            </View>
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    } else if (mode === Mode.Preview) {
      return (
        <SafeAreaView style={{flex: 1}}>
          <KeyboardAwareScrollView
            style={{flex: 1, backgroundColor: SRXColor.White}}>
            {this.renderGRCPostPreview()}
          </KeyboardAwareScrollView>
        </SafeAreaView>
      );
    }
    return <View />;
  }
}

CommunitiesGRCNewPost.propTypes = {
  //todo; to change instance of object
  business: PropTypes.object.isRequired,

  community: PropTypes.instanceOf(CommunityPO).isRequired,

  onSponsoredPostCreated: PropTypes.func,
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(CommunitiesGRCNewPost);
