import React, {Component} from 'react';
import {
  View,
  TouchableOpacity,
  Image,
  Text,
  Platform,
  ActivityIndicator,
} from 'react-native';
import {Spacing, InputStyles, TopBarStyle} from '../../../../styles';
import {loadPreviewPosts} from '../../../../actions';
import {
  BodyText,
  ExtraSmallBodyText,
  SmallBodyText,
  FeatherIcon,
} from '../../../../components';
import {SRXColor, LoadingState, IS_IOS} from '../../../../constants';
import {Navigation} from 'react-native-navigation';
import {PropertySearchLocationStack} from '../../../PropertySearch/Location';
import {PropertyTypeValueSet} from '../../../PropertySearch/Constants';
import {
  PropertySearch_Condo,
  PropertySearch_Hdb,
  PropertySearch_Newlaunches,
  PropertySearch_Factory,
  PropertySearch_Land,
  PropertySearch_Landed,
  PropertySearch_Office,
  PropertySearch_Retail,
  PropertySearch_Shophouse,
  PropertySearch_Shophouse_Hdb,
  PropertySearch_Warehouse,
} from '../../../../assets';
import PropTypes from 'prop-types';
import {CommunitiesPreviewList} from './CommunitiesPreviewList';
import {CommunitiesService} from '../../../../services';
import {ObjectUtil} from '../../../../utils';
import {CommunityPostPO} from '../../../../dataObject';
import {connect} from 'react-redux';
import {CommunitiesStack} from '../../../../config';
import {CommunitiesConstant} from '../../../Communities/Constants';

class PropertySearchAndShortcutAccess extends Component {
  static propTypes = {
    searchOptions: PropTypes.object,
    onUpdateSearchOptions: PropTypes.func,
    isResidentialOrCommercial: PropTypes.func,
    showListingResult: PropTypes.func,
    postSelected: PropTypes.func,
    onPressSeeAll: PropTypes.func,
    onAddPropertyPressed: PropTypes.func,
  };

  static defaultProps = {
    searchOptions: null,
  };

  constructor(props) {
    super(props);
    this.onSuccessSignUp = this.onSuccessSignUp.bind(this);
  }

  state = {
    showResiShortcut: true,
    communityFeedList: [],
    communityLoadingState: LoadingState.Normal,
  };

  componentDidMount() {
    const {loadPreviewPosts} = this.props;
    loadPreviewPosts();
    // this.setState({communityLoadingState: LoadingState.Loading}, () =>
    //   this.loadPosts(0, 5),
    // );
  }

  loadPosts(startIndex, maxResults) {
    let postType =
      CommunitiesConstant.postType.normal +
      ',' +
      CommunitiesConstant.postType.listing +
      ',' +
      CommunitiesConstant.postType.news;
    CommunitiesService.findPosts({
      startIndex: startIndex,
      maxResults: maxResults,
      removeHtml: true,
      types: postType,
    })
      .then(response => {
        const {posts} = response;
        const newPosts = [];

        if (!ObjectUtil.isEmpty(posts)) {
          posts.map(item => {
            newPosts.push(new CommunityPostPO(item));
          });
        }
        this.setState({
          communityFeedList: newPosts,
          communityLoadingState: LoadingState.Loaded,
        });
      })
      .catch(error => {
        this.setState({communityLoadingState: LoadingState.Normal});
        console.log(error);
      });
  }

  //show location modal
  showAddressModal() {
    const {onUpdateSearchOptions, searchOptions} = this.props;
    var newSearchOptions = {
      ...searchOptions,
      type: 'S',
    };
    Navigation.showModal(
      PropertySearchLocationStack({
        onLocationSelected: this.onLocationSelected,
        onUpdateSearchOptions: onUpdateSearchOptions,
        searchOptions: newSearchOptions,
      }),
    );
  }

  onLocationSelected = addressData => {
    const {onUpdateSearchOptions, searchOptions} = this.props;
    const {showResiShortcut} = this.state;

    var newSearchOptions = {
      ...searchOptions,
      ...addressData,
      projectLaunchStatus: 2,
      //reset to all residential or commerical subtypes
      cdResearchSubTypes: showResiShortcut
        ? Array.from(PropertyTypeValueSet.allResidential).join(',')
        : Array.from(PropertyTypeValueSet.commercial).join(','),
    };

    if (onUpdateSearchOptions) {
      //update search Options
      onUpdateSearchOptions(newSearchOptions);

      //show listing result
      this.showListingResult(newSearchOptions);
    }
  };

  //Go to listing results
  showListingResult = searchOptions => {
    const {showListingResult} = this.props;
    if (showListingResult) {
      showListingResult(searchOptions);
    }
  };

  postSelected = postId => {
    const {postSelected} = this.props;
    if (postSelected) {
      postSelected(postId);
    }
  };

  onPressSeeAll = () => {
    const {onPressSeeAll} = this.props;
    if (onPressSeeAll) {
      onPressSeeAll();
    }
  };

  onAddPropertyPressed = () => {
    const {onAddPropertyPressed} = this.props;
    if (onAddPropertyPressed) {
      onAddPropertyPressed();
    }
  };

  onClickJoinCommunity = () => {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      //after user logged in
      const passProps = {
        onAddPropertyPressed: this.onAddPropertyPressed.bind(this),
      };
      CommunitiesStack.showAddPropertyModal(passProps);
    } else {
      //if not logged in
      const passProps = {
        onSuccessSignUp: this.onSuccessSignUp,
      };
      CommunitiesStack.showSignInRegisterModal(passProps);
    }
  };

  onSuccessSignUp = () => {
    const passProps = {
      onAddPropertyPressed: this.onAddPropertyPressed.bind(this),
    };
    CommunitiesStack.showAddPropertyModal(passProps);
  };

  //property type (HDB,Condo,etc.,)
  onSelectPropertyType = selectedPropertyType => {
    const {searchOptions, onUpdateSearchOptions} = this.props;
    var selectedPropertyType;

    switch (selectedPropertyType) {
      case 'HDB':
        {
          propertyType = PropertyTypeValueSet.hdb;
        }
        break;

      case 'Condo':
        {
          propertyType = PropertyTypeValueSet.condo;
        }
        break;
      case 'Landed':
        {
          propertyType = PropertyTypeValueSet.landed;
        }
        break;

      case 'New_Launches':
        {
          propertyType = PropertyTypeValueSet.newlaunches;
        }
        break;

      case 'Retail':
        {
          propertyType = PropertyTypeValueSet.retail;
        }
        break;

      case 'Office':
        {
          propertyType = PropertyTypeValueSet.office;
        }
        break;
      case 'Factory':
        {
          propertyType = PropertyTypeValueSet.factory;
        }
        break;

      case 'Warehouse':
        {
          propertyType = PropertyTypeValueSet.warehouse;
        }
        break;
      case 'Land':
        {
          propertyType = PropertyTypeValueSet.land;
        }
        break;
      case 'HDB_Shophouse':
        {
          propertyType = PropertyTypeValueSet.hdbshophouse;
        }
        break;
      case 'Shophouse':
        {
          propertyType = PropertyTypeValueSet.shophouse;
        }
        break;
      default:
        break;
    }

    cdResearchSubTypes = Array.from(propertyType).join(',');

    //  projectLaunchStatus 1 - New launch, 2 - Resale

    if (selectedPropertyType == 'New_Launches') {
      projectLaunchStatus = 1;
    } else {
      projectLaunchStatus = 2;
    }

    //update property type, cdresearchsubtypes and projectlaunchstatus in search options object
    var newSearchOptions = {
      type: 'S',
      displayText: 'Everywhere in Singapore',
      cdResearchSubTypes,
      projectLaunchStatus,
    };

    if (onUpdateSearchOptions) {
      //call props function
      onUpdateSearchOptions(newSearchOptions);
      //show listing result modal
      this.showListingResult(newSearchOptions);
    }
  };

  //select Residential or commercial text
  onSelectResiCommercialText = selectedText => {
    const {isResidentialOrCommercial} = this.props;

    this.setState(
      {
        showResiShortcut: selectedText == 'Residential' ? true : false,
      },
      () => {
        isResidentialOrCommercial(selectedText);
      },
    );
  };

  renderAddress() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          paddingTop: Spacing.M,
          backgroundColor: SRXColor.White,
        }}>
        <TouchableOpacity onPress={() => this.showAddressModal()}>
          <View
            style={[
              InputStyles.singleLineTextHeight,
              {
                borderRadius: IS_IOS ? 20 : 22.5,
                backgroundColor: SRXColor.Purple,
                flexDirection: 'row',
                alignItems: 'center',
              },
            ]}>
            <FeatherIcon
              name="search"
              size={20}
              color={'white'}
              style={{alignSelf: 'center', marginHorizontal: Spacing.XS}}
            />
            <Text style={[InputStyles.text, {color: SRXColor.White}]}>
              Begin your search
            </Text>
          </View>
        </TouchableOpacity>
      </View>
    );
  }

  renderResidential() {
    return (
      <View
        style={[
          Styles.resiCommShortcutsContainer,
          {flex: 1, flexDirection: 'row', justifyContent: 'center'},
        ]}>
        <TouchableOpacity
          style={[
            Styles.resiCommShortcutContainer,
            {paddingHorizontal: Spacing.M},
          ]}
          onPress={() => this.onSelectPropertyType('HDB')}>
          <Image
            source={PropertySearch_Hdb}
            style={{marginTop: Spacing.XS, height: 25, width: 25}}
            resizeMode={'contain'}
          />
          <SmallBodyText style={{color: SRXColor.Purple}}>HDB</SmallBodyText>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            Styles.resiCommShortcutContainer,
            {paddingHorizontal: Spacing.M},
          ]}
          onPress={() => this.onSelectPropertyType('Condo')}>
          <Image
            source={PropertySearch_Condo}
            style={{marginTop: Spacing.XS, height: 25, width: 25}}
            resizeMode={'contain'}
          />
          <SmallBodyText style={{color: SRXColor.Purple}}>Condo</SmallBodyText>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            Styles.resiCommShortcutContainer,
            {paddingHorizontal: Spacing.M},
          ]}
          onPress={() => this.onSelectPropertyType('Landed')}>
          <Image
            source={PropertySearch_Landed}
            style={{marginTop: Spacing.XS, height: 27, width: 27}}
            resizeMode={'contain'}
          />
          <SmallBodyText style={{color: SRXColor.Purple}}>Landed</SmallBodyText>
        </TouchableOpacity>

        <TouchableOpacity
          style={[
            Styles.resiCommShortcutContainer,
            {paddingHorizontal: Spacing.S},
          ]}
          onPress={() => this.onSelectPropertyType('New_Launches')}>
          <Image
            source={PropertySearch_Newlaunches}
            style={{marginTop: Spacing.XS, height: 30, width: 30}}
            resizeMode={'contain'}
          />
          <SmallBodyText style={{color: SRXColor.Purple}}>
            New Launches
          </SmallBodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderCommercialText() {
    return (
      <TouchableOpacity
        style={Styles.resiCommTextAndIconContainer}
        onPress={() => this.onSelectResiCommercialText('Commercial')}>
        <SmallBodyText
          style={{color: SRXColor.TextLink, paddingRight: Spacing.XS / 2}}>
          Commercial
        </SmallBodyText>
        <FeatherIcon name="external-link" size={20} color={'#8DABC4'} />
      </TouchableOpacity>
    );
  }

  renderCommercial() {
    return (
      <View
        style={[
          Styles.resiCommShortcutsContainer,
          {paddingHorizontal: Spacing.M},
        ]}>
        <View
          style={{
            flex: 1,
            flexDirection: 'row',
            justifyContent: 'center',
          }}>
          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {paddingTop: 15, paddingHorizontal: Spacing.L},
            ]}
            onPress={() => this.onSelectPropertyType('Retail')}>
            <Image
              source={PropertySearch_Retail}
              style={{height: 25, width: 25}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Retail
            </ExtraSmallBodyText>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {paddingTop: 14, paddingHorizontal: Spacing.M},
            ]}
            onPress={() => this.onSelectPropertyType('Office')}>
            <Image
              source={PropertySearch_Office}
              style={{height: 27, width: 27}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Office
            </ExtraSmallBodyText>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {paddingTop: 14, paddingHorizontal: Spacing.M},
            ]}
            onPress={() => this.onSelectPropertyType('Factory')}>
            <Image
              source={PropertySearch_Factory}
              style={{height: 27, width: 27}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Factory
            </ExtraSmallBodyText>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {paddingTop: Spacing.S, paddingHorizontal: Spacing.L},
            ]}
            onPress={() => this.onSelectPropertyType('Warehouse')}>
            <Image
              source={PropertySearch_Warehouse}
              style={{height: 30, width: 30}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Warehouse
            </ExtraSmallBodyText>
          </TouchableOpacity>
        </View>
        <View
          style={{
            flex: 1,
            flexDirection: 'row',
            paddingTop: Spacing.M,
            justifyContent: 'center',
          }}>
          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {
                paddingTop: Spacing.XS,
                paddingLeft: Spacing.XL + Spacing.S,
                paddingRight: Spacing.M,
              },
            ]}
            onPress={() => this.onSelectPropertyType('Land')}>
            <Image
              source={PropertySearch_Land}
              style={{height: 30, width: 30}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Land
            </ExtraSmallBodyText>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {
                paddingTop: 11,
                paddingHorizontal: Spacing.S,
              },
            ]}
            onPress={() => this.onSelectPropertyType('HDB_Shophouse')}>
            <Image
              source={PropertySearch_Shophouse_Hdb}
              style={{height: 27, width: 27}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              HDB Shophouse
            </ExtraSmallBodyText>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              Styles.resiCommShortcutContainer,
              {
                paddingTop: Spacing.S,
                paddingLeft: Spacing.M,
                paddingRight: Spacing.XL,
              },
            ]}
            onPress={() => this.onSelectPropertyType('Shophouse')}>
            <Image
              source={PropertySearch_Shophouse}
              style={{height: 25, width: 25}}
              resizeMode={'contain'}
            />
            <ExtraSmallBodyText style={{color: SRXColor.Purple}}>
              Shophouse
            </ExtraSmallBodyText>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  renderResidentialText() {
    return (
      <TouchableOpacity
        style={Styles.resiCommTextAndIconContainer}
        onPress={() => this.onSelectResiCommercialText('Residential')}>
        <SmallBodyText
          style={{color: SRXColor.TextLink, paddingRight: Spacing.XS / 2}}>
          Residential
        </SmallBodyText>
        <FeatherIcon name="external-link" size={20} color={'#8DABC4'} />
      </TouchableOpacity>
    );
  }

  renderResidentialandCommercialText() {
    return (
      <View>
        {this.renderResidential()}
        {this.renderCommercialText()}
        {this.renderCommunityFeed()}
        {this.renderJoinCommunityAndPosting()}
      </View>
    );
  }

  renderCommunityFeed() {
    const {previewList, previewLoadingState} = this.props;
    // const {communityFeedList, communityLoadingState} = this.state;
    if (
      previewLoadingState === LoadingState.Loading &&
      ObjectUtil.isEmpty(previewList)
    ) {
      return (
        <View style={{padding: Spacing.M}}>
          <ActivityIndicator />
        </View>
      );
    } else {
      return (
        <CommunitiesPreviewList
          communityFeedList={previewList}
          postSelected={this.postSelected}
          onPressSeeAll={this.onPressSeeAll}
        />
      );
    }
  }

  renderJoinCommunity() {
    const {userPO} = this.props;
    return (
      <View style={Styles.buttonContainer}>
        <TouchableOpacity onPress={() => this.onClickJoinCommunity()}>
          <SmallBodyText style={{color: SRXColor.Teal}}>
            {!ObjectUtil.isEmpty(userPO)
              ? 'Add your property to get started'
              : 'Join your community and start posting!'}
          </SmallBodyText>
        </TouchableOpacity>
      </View>
    );
  }

  renderJoinCommunityAndPosting() {
    const {userPO, communities, loadingState} = this.props;
    if (loadingState === LoadingState.Loading) {
      return (
        <View style={{padding: Spacing.M}}>
          <ActivityIndicator />
        </View>
      );
    } else {
      if (ObjectUtil.isEmpty(userPO)) {
        return this.renderJoinCommunity();
      } else if (
        ObjectUtil.isEmpty(communities) ||
        (Array.isArray(communities) &&
          communities.length == 0 &&
          loadingState === LoadingState.Loaded)
      ) {
        return this.renderJoinCommunity();
      }
      return <View style={{height: Spacing.M}} />;
    }
  }

  renderCommercialandResidentialText() {
    return (
      <View>
        {this.renderCommercial()}
        {this.renderResidentialText()}
      </View>
    );
  }

  renderPropertySearchAndShortcutAccess() {
    const {showResiShortcut} = this.state;
    return (
      <View
        style={{
          backgroundColor: SRXColor.White,
          borderBottomLeftRadius: 10,
          borderBottomRightRadius: 10,
          overflow: 'hidden',
          marginBottom: Spacing.S,
        }}>
        {/* {this.renderBuyRent()} */}
        {this.renderAddress()}
        {showResiShortcut
          ? this.renderResidentialandCommercialText()
          : this.renderCommercialandResidentialText()}
      </View>
    );
  }

  render() {
    return (
      <View style={{flex: 1}}>
        {this.renderPropertySearchAndShortcutAccess()}
      </View>
    );
  }
}

const Styles = {
  buyRentTabBarStyle: {
    width: 42,
    borderColor: SRXColor.TextLink,
    borderWidth: 2,
    justifyContent: 'center',
    marginTop: Spacing.XS / 2,
  },
  resiCommTextAndIconContainer: {
    flex: 1,
    flexDirection: 'row',
    paddingVertical: Spacing.M,
    justifyContent: 'center',
    backgroundColor: SRXColor.White,
    alignItems: 'center',
  },
  resiCommShortcutsContainer: {
    backgroundColor: SRXColor.White,
  },
  resiCommShortcutContainer: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  buttonContainer: {
    borderRadius: IS_IOS ? 17.5 : 20,
    borderColor: SRXColor.Teal,
    borderWidth: 1,
    alignItems: 'center',
    height: IS_IOS ? 35 : 40,
    justifyContent: 'center',
    margin: Spacing.M,
    width: '80%',
    alignSelf: 'center',
  },
};

const mapStateToProps = state => {
  return {
    communities: state.communitiesData.list,
    userPO: state.loginData.userPO,
    loadingState: state.communitiesData.loadingState,
    previewList: state.communitiesData.previewList,
    previewLoadingState: state.communitiesData.previewLoadingState,
  };
};

export default connect(
  mapStateToProps,
  {loadPreviewPosts},
)(PropertySearchAndShortcutAccess);
