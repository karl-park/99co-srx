import React, {Component} from 'react';
import {View, Alert, Image, ActivityIndicator} from 'react-native';
import {Navigation} from 'react-native-navigation';
import DeviceInfo from 'react-native-device-info';
import {connect} from 'react-redux';
import {
  removeTracker,
  logoutUser,
  loadPropertyTrackers,
  loadUserProfile,
} from '../../actions';
import {
  FeatherIcon,
  Button,
  Heading2,
  Separator,
  SmallBodyText,
  ImagePicker as CommonImagePicker,
} from '../../components';
import ImagePicker from 'react-native-image-crop-picker';
import {SRXColor, IS_IPHONE_X, IS_IOS, LoadingState} from '../../constants';
import {Spacing, TopBarConstants, TopBarStyle, AppTheme} from '../../styles';
import {ObjectUtil, CommonUtil} from '../../utils';
import {MyPropertyListItem, MyPropertiesMap} from '../MyProperties';
import {UserBasicInformation} from './components';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {AppTopBar_BackBtn} from '../../assets';
import {LoginService} from '../../services';

const IOS_11_And_Above = IS_IOS && DeviceInfo.getSystemVersion() >= 11;

const MapViewHeight = 233;

const TransparentTopBarStyle = {
  backgroundColor: 'transparent',
  borderBottomWidth: 0,
};

const ColorTopBarStyle = {
  backgroundColor: AppTheme.topBarBackgroundColor,
  borderBottomWidth: 1,
  borderColor: '#e0e0e0',
};

class UserDashboard extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        noBorder: true,
        animate: false,
        drawBehind: IS_IOS ? false : true,
      },
    };
  }

  state = {
    currentTopBarStyle: TransparentTopBarStyle,
  };

  constructor(props) {
    super(props);

    this.onBackPress = this.onBackPress.bind(this);
    this.onScroll = this.onScroll.bind(this);
    this.chooseImage = this.chooseImage.bind(this);
    this.onDeleteProperty = this.onDeleteProperty.bind(this);
    this.onManageProperty = this.onManageProperty.bind(this);
    this.directToMyProperty = this.directToMyProperty.bind(this);
    this.directToAddUpdateMyPropertyForm = this.directToAddUpdateMyPropertyForm.bind(
      this,
    );
  }

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.userPO !== this.props.userPO) {
      if (ObjectUtil.isEmpty(this.props.userPO)) {
        if (this.props.componentId) {
          Navigation.pop(this.props.componentId);
        }
      }
    }
  }

  onBackPress = () => {
    Navigation.pop(this.props.componentId);
  };

  onEditProfile = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'SideMainMenu.UpdateProfile',
      },
    });
  };

  chooseImage() {
    const options = {
      title: null,
      cancelButtonTitle: 'Dismiss',
      takePhotoButtonTitle: 'Take a photo',
      chooseFromLibraryButtonTitle: null, // as gif failed to upload, temporary use another library
      customButtons: [
        {name: 'library', title: 'Choose a photo'},
        {name: 'remove', title: 'Remove photo'},
      ],
      mediaType: 'photo',
      maxWidth: 1024,
      maxHeight: 1024,
      noData: false, // we use response.data to display gif
      allowsEditing: true,
      storageOptions: {
        skipBackup: true,
        path: 'images',
      },
    };

    CommonImagePicker.showImagePicker(options, response => {
      console.log('Response = ', response);

      if (response.didCancel) {
        console.log('User cancelled image picker');
      } else if (response.error) {
        if (response.error == 'Camera permissions not granted') {
          if (IS_IOS) {
            const title = 'Permission Denied';
            const message =
              "This function required to access your camera.\nTo allow, please visit Settings App, and allow 'Camera' for SRX Property.";
            CommonUtil.directToiOSSettings(title, message);
          }
        } else if (response.error == 'Photo library permissions not granted') {
          if (IS_IOS) {
            const title = 'Permission Denied';
            const message =
              "This function required to access your photos.\nTo allow, please visit Settings App, and allow 'Photos' for SRX Property.";
            CommonUtil.directToiOSSettings(title, message);
          }
        }
        console.log('ImagePicker Error: ', response.error);
      } else if (response.customButton) {
        console.log('User tapped custom button: ', response.customButton);
        //remove
        if (response.customButton == 'remove') {
          this.removePhoto();
        } else if (response.customButton == 'library') {
          this.showImagePicker();
        }
      } else {
        const source = {uri: response.uri};
        var fileName = '';
        if (!ObjectUtil.isEmpty(response.fileName)) {
          fileName = response.fileName;
        }
        let photoItem = {
          uri: response.uri,
          type: 'image/jpeg',
          name: fileName, //a random name for now, it is needed else fetch wont grab the data from the uri
        };
        this.uploadPhoto(photoItem);
      }
    });
  }

  uploadPhoto(photo) {
    const {loadUserProfile, userPO} = this.props;
    LoginService.updateProfilePhoto({photo})
      .then(response => {
        loadUserProfile(userPO);
      })
      .catch(error => {
        console.log(error);
      });
  }

  removePhoto() {
    const {loadUserProfile, userPO} = this.props;
    LoginService.removePhoto()
      .then(response => {
        loadUserProfile(userPO);
      })
      .catch(error => {
        console.log(error);
      });
  }

  showImagePicker() {
    ImagePicker.openPicker({
      multiple: false,
      cropping: true,
      mediaType: 'photo',
      compressImageMaxWidth: 1024,
      compressImageMaxHeight: 1024,
    }).then(image => {
      let photoItem;
      if (IS_IOS) {
        photoItem = {
          uri: image.sourceURL,
          type: image.mime,
          name: image.fileName ?? '', //a random name for now, it is needed else fetch wont grab the data from the uri
        };
      } else {
        //for android
        var fileName = image.path.substring(image.path.lastIndexOf('/') + 1);
        photoItem = {
          uri: image.path,
          type: image.mime,
          name: fileName ?? '',
        };
      }

      this.uploadPhoto(photoItem);
    });
  }

  directToAddUpdateMyPropertyForm() {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
        },
      },
    });
  }

  onDeleteProperty(trackerPO) {
    Alert.alert(
      'Delete Property',
      'Once deleted, you will not be able to retrieve. Confirm to delete?',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Delete',
          onPress: () => this.deleteProperty(trackerPO),
          style: 'destructive',
        },
      ],
    );
  }

  onManageProperty(trackerPO, isCertifyListing) {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
          propertyUserPO: trackerPO,
          certifyListing: isCertifyListing,
        },
      },
    });
  }

  onVerifyOrCertify = trackerPO => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.AddUpdateMyProperty',
        passProps: {
          initiatingComponentId: this.props.componentId,
          propertyUserPO: trackerPO,
          certifyListing: true,
        },
      },
    });
  };

  deleteProperty(trackerPO) {
    this.props.removeTracker(trackerPO);
  }

  directToMyProperty(trackerPO) {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'MyProperties.Detail',
        passProps: {
          initialTrackerPO: trackerPO,
        },
      },
    });
  }

  onScroll = ({
    nativeEvent: {
      contentOffset: {x, y},
    },
  }) => {
    const {currentTopBarStyle} = this.state;
    const topbarHeight =
      2 * TopBarConstants.statusBarHeight + TopBarConstants.topBarHeight; //X 2 bcs added margin top as statusBarHeight
    const imageBottomY = MapViewHeight - topbarHeight;
    if (y > imageBottomY) {
      if (currentTopBarStyle != ColorTopBarStyle) {
        this.setState({
          currentTopBarStyle: ColorTopBarStyle,
        });
      }
    } else {
      if (currentTopBarStyle != TransparentTopBarStyle) {
        this.setState({
          currentTopBarStyle: TransparentTopBarStyle,
        });
      }
    }
  };

  renderUserInformation() {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      return (
        <UserBasicInformation
          userPO={userPO}
          onEditProfile={this.onEditProfile}
          onEditProfilePic={this.chooseImage}
        />
      );
    }
  }

  renderAddPropertyBtn() {
    const {properties, myPropertyLoadingState} = this.props;
    if (
      (ObjectUtil.isEmpty(properties) ||
        (Array.isArray(properties) && properties.length < 3)) &&
      myPropertyLoadingState == LoadingState.Loaded
    ) {
      return (
        <Button
          textStyle={{color: SRXColor.Teal}}
          leftView={
            <FeatherIcon
              name={'plus-square'}
              size={20}
              color={SRXColor.Teal}
              style={{marginRight: Spacing.XS / 2}}
            />
          }
          onPress={this.directToAddUpdateMyPropertyForm}>
          Add Property
        </Button>
      );
    }
  }

  renderMyPropertyContent() {
    const {
      properties,
      myPropertyLoadingState,
      loadPropertyTrackers,
    } = this.props;
    if (myPropertyLoadingState === LoadingState.Loading) {
      return (
        <View style={{paddingVertical: Spacing.L}}>
          <ActivityIndicator />
        </View>
      );
    } else if (myPropertyLoadingState === LoadingState.Failed) {
      return (
        <View
          style={{
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.XL,
            alignItems: 'center',
          }}>
          <Button
            buttonStyle={{padding: Spacing.XS}}
            textStyle={{color: SRXColor.Teal}}
            rightView={
              <FeatherIcon
                name="rotate-cw"
                size={16}
                color={SRXColor.Teal}
                style={{marginLeft: Spacing.XS}}
              />
            }
            onPress={() => loadPropertyTrackers({populateXvalue: true})}>
            Reload
          </Button>
        </View>
      );
    } else if (myPropertyLoadingState === LoadingState.Loaded) {
      return this.renderMyPropertyItems();
    }
  }

  renderMyPropertyItems = () => {
    const {properties} = this.props;
    if (!ObjectUtil.isEmpty(properties)) {
      return properties.map(item => (
        <MyPropertyListItem
          trackerPO={item}
          onItemSelected={this.directToMyProperty}
          onDelete={this.onDeleteProperty}
          onManage={this.onManageProperty}
          onVerifyOrCertify={this.onVerifyOrCertify}
        />
      ));
    } else {
      return (
        <View
          style={{
            paddingHorizontal: Spacing.M,
            paddingVertical: Spacing.XL,
            alignItems: 'center',
          }}>
          <SmallBodyText>You have not added any properties</SmallBodyText>
        </View>
      );
    }
  };

  renderMyPropertiesHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          paddingHorizontal: Spacing.M,
          paddingTop: 20,
          paddingBottom: 28,
        }}>
        <Heading2 style={{flex: 1}}>My Properties</Heading2>
        {this.renderAddPropertyBtn()}
      </View>
    );
  }

  renderMyProperties() {
    return (
      <View style={{backgroundColor: SRXColor.White}}>
        {this.renderMyPropertiesHeader()}
        {this.renderMyPropertyContent()}
      </View>
    );
  }

  renderSeparator() {
    return (
      <Separator
        edgeInset={{top: 0, bottom: 0, left: Spacing.M, right: Spacing.M}}
      />
    );
  }

  renderLogout() {
    if (!ObjectUtil.isEmpty(this.props.userPO)) {
      return (
        <View
          style={{
            paddingHorizontal: Spacing.M,
            paddingTop: 38,
            paddingBottom: 46,
            alignItems: 'flex-start',
          }}>
          <Button
            buttonStyle={{padding: Spacing.XS}}
            textStyle={{fontSize: 16, color: SRXColor.Red}}
            leftView={
              <FeatherIcon
                name="power"
                size={20}
                color={SRXColor.Red}
                style={{marginRight: Spacing.M}}
              />
            }
            onPress={() => {
              this.props.logoutUser();
            }}>
            Sign out
          </Button>
        </View>
      );
    }
  }

  renderCustomTopBar() {
    const {currentTopBarStyle} = this.state;
    return (
      <View
        style={[
          TopBarStyle.topBar,
          {
            height:
              TopBarConstants.statusBarHeight + TopBarConstants.topBarHeight,
            paddingTop: TopBarConstants.statusBarHeight,
            width: '100%',
            position: 'absolute',
            paddingLeft: IS_IOS ? (IOS_11_And_Above ? 15 : 29) : 10,
          },
          currentTopBarStyle,
        ]}>
        <Button
          buttonStyle={TopBarStyle.topBarItem}
          leftView={
            <Image
              style={TopBarStyle.topBarItemIcon}
              source={AppTopBar_BackBtn}
            />
          }
          onPress={this.onBackPress}
        />
      </View>
    );
  }

  render() {
    const {properties} = this.props;
    return (
      <View style={{flex: 1}}>
        <KeyboardAwareScrollView
          style={{flex: 1, marginTop: -TopBarConstants.statusBarHeight}}
          // bounces={false}
          onScroll={this.onScroll}>
          <View
            style={{
              height: MapViewHeight,
            }}
            pointerEvents={'none'}>
            <MyPropertiesMap properties={properties} />
            <View
              style={{
                width: '100%',
                height: '100%',
                position: 'absolute',
              }}
            />
          </View>
          {this.renderUserInformation()}
          {this.renderSeparator()}
          {this.renderMyProperties()}
          {this.renderLogout()}
        </KeyboardAwareScrollView>
        {this.renderCustomTopBar()}
      </View>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    properties: state.myPropertiesData.list,
    myPropertyLoadingState: state.myPropertiesData.loadingState,
  };
};

export default connect(
  mapStateToProps,
  {removeTracker, logoutUser, loadPropertyTrackers, loadUserProfile},
)(UserDashboard);
