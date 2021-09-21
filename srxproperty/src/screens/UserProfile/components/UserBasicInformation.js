import React, {Component} from 'react';
import {View, ActionSheetIOS, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {
  Text,
  BodyText,
  Subtext,
  Button,
  Avatar,
  FeatherIcon,
  ImagePicker,
} from '../../../components';
import {SRXColor, IS_IOS} from '../../../constants';
import {Spacing} from '../../../styles';
import {ObjectUtil, CommonUtil} from '../../../utils';
import {LoginService} from '../../../services';

class UserBasicInformation extends Component {
  onEditPressed = () => {
    const {onEditProfile} = this.props;
    if (onEditProfile) {
      onEditProfile();
    }
  };

  onEditProfilePicPressed = () => {
    const {onEditProfilePic} = this.props;
    if (onEditProfilePic) {
      onEditProfilePic();
    }
  };

  renderUserIcon() {
    const {userPO} = this.props;
    console.log(userPO);
    let firstLetter = '';
    if (!ObjectUtil.isEmpty(userPO.getCommunityPostUserName())) {
      firstLetter = userPO.getCommunityPostUserName().charAt(0).toUpperCase();
    }
    return (
      <View
        style={{
          height: 75,
          width: 75,
          borderRadius: 37.5,
          backgroundColor: SRXColor.White,
          top: -37.5,
          position: 'absolute',
          alignSelf: 'center',
        }}>
        <TouchableOpacity
          style={{
            height: 75,
            width: 75,
            // backgroundColor: '#e3c464',
            // borderRadius: 37.5,
            // top: -37.5,
            justifyContent: 'center',
            alignItems: 'center',
          }}
          onPress={this.onEditProfilePicPressed}>
          <Avatar
            backgroundColor={'#e3c464'}
            size={75}
            textSize={25}
            imageUrl={CommonUtil.handleImageUrl(userPO.photo)}
            name={firstLetter}
            borderColor={'#DCDCDC'}
          />
          <FeatherIcon
            name={'camera'}
            size={12}
            color={SRXColor.Black}
            style={{
              backgroundColor: SRXColor.Tertiary,
              padding: Spacing.XS / 2,
              borderRadius: 10,
              overflow: 'hidden',
              position: 'absolute',
              bottom: Spacing.XS / 2,
              right: Spacing.XS / 2,
            }}
          />
        </TouchableOpacity>
      </View>
    );
  }

  renderUserInfo() {
    return (
      <View style={{alignItems: 'center', marginTop: 40}}>
        {this.renderUserName()}
        {this.renderUserEmail()}
        {this.renderUserMobile()}
      </View>
    );
  }

  renderUserName() {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO.getCommunityPostUserName())) {
      return <BodyText>{userPO.getCommunityPostUserName()}</BodyText>;
    }
  }

  renderUserEmail() {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO.email)) {
      return (
        <Subtext style={{marginTop: Spacing.XS / 2}}>{userPO.email}</Subtext>
      );
    }
  }

  renderUserMobile() {
    const {userPO} = this.props;
    if (userPO.mobileLocalNum && userPO.mobileLocalNum > 0) {
      return (
        <Subtext style={{marginTop: Spacing.XS}}>
          {userPO.mobileLocalNum}
        </Subtext>
      );
    }
  }

  renderEditButton() {
    return (
      <Button
        buttonStyle={{
          alignSelf: 'flex-end',
          position: 'absolute',
          padding: Spacing.XS,
          top: Spacing.S - Spacing.XS,
          right: Spacing.M - Spacing.XS,
        }}
        textStyle={{fontSize: 16}}
        onPress={this.onEditPressed}>
        Edit
      </Button>
    );
  }

  render() {
    return (
      <View
        style={{
          paddingVertical: Spacing.S,
          paddingHorizontal: Spacing.M,
          backgroundColor: SRXColor.White,
        }}>
        {this.renderEditButton()}
        {this.renderUserInfo()}
        {this.renderUserIcon()}
      </View>
    );
  }
}

UserBasicInformation.propTypes = {
  userPO: PropTypes.object.isRequired,
  onEditProfile: PropTypes.func,
  onEditProfilePic: PropTypes.func,
};

export {UserBasicInformation};
