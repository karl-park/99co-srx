import React, {Component} from 'react';
import {Text, Image, View} from 'react-native';
import {IS_IOS, SRXColor} from '../../constants';
import {CommonUtil, ObjectUtil} from '../../utils';
import {TabBarIcon_XValue_Big} from '../../assets';
import PropTypes from 'prop-types';

class Avatar extends Component {
  constructor(props) {
    super(props);
  }

  stringToColor(str) {
    var hash = 0;
    var colors = [
      '#483387',
      '#6d5c9f',
      '#9185b7',
      '#3f8f90',
      '#4aa8b2',
      '#58c3c4',
      '#ce8900',
      '#f1a600',
      '#ffba00',
      '#a0a4a8',
      '#52575c',
      '#a0cd4b',
      '#ff7881',
    ];
    for (var i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
      hash = hash & hash;
    }
    if (str.length > 1) {
      var color = '#';
      for (var i = 0; i < 3; i++) {
        var value = (hash >> (i * 8)) & 255;
        color += ('00' + value.toString(16)).substr(-2);
      }
      return color;
    } else {
      //For strings that only have 1 characters, otherwise will keep showing similar colours
      // hash = ((hash % colors.length) + colors.length) % colors.length;
      hash = Math.floor(Math.random() * colors.length);
      return colors[hash];
    }
  }

  render() {
    const {
      name,
      size,
      imageUrl,
      borderColor,
      textSize,
      borderLess,
      backgroundColor,
    } = this.props;
    var borderRadius = size / 2;
    var avatarBorderColor = SRXColor.Black;
    if (borderColor) {
      avatarBorderColor = borderColor;
    }
    var borderW = 1;
    if (borderLess) {
      borderW = 0;
    }

    if (imageUrl) {
      let url = CommonUtil.handleImageUrl(imageUrl);
      if (!ObjectUtil.isEmpty(url)) {
        if (IS_IOS) {
          return (
            <Image
              source={{uri: url}}
              style={{
                width: size,
                height: size,
                borderRadius: borderRadius,
                borderWidth: borderW,
                borderColor: avatarBorderColor,
                alignSelf: 'center',
              }}
              resizeMode={'cover'}
            />
          );
        } else {
          return (
            <View
              style={{
                width: size,
                height: size,
                borderRadius: borderRadius,
                borderWidth: borderW,
                borderColor: avatarBorderColor,
                overflow: 'hidden',
              }}>
              <Image
                source={{uri: url}}
                style={{
                  width: size,
                  height: size,
                  borderRadius: borderRadius,
                  borderWidth: borderW,
                  borderColor: avatarBorderColor,
                  alignSelf: 'center',
                }}
                resizeMode={'cover'}
              />
            </View>
          );
        }
      }
    } else if (name) {
      if (name == 'SRX') {
        return (
          <Image
            source={TabBarIcon_XValue_Big}
            style={{
              width: size,
              height: size,
              borderRadius: borderRadius,
              borderWidth: borderW,
              alignSelf: 'center',
              borderColor: SRXColor.LightGray,
            }}
            resizeMode={'cover'}
          />
        );
      }
      var backgroundColorOfName = this.stringToColor(name);
      if (borderLess) {
        return (
          <View
            style={{
              justifyContent: 'center',
              alignSelf: 'center',
              alignItems: 'center',
              width: size,
              height: size,
              backgroundColor: backgroundColor ?? backgroundColorOfName,
              borderRadius: borderRadius,
            }}>
            <Text
              style={[
                {color: SRXColor.White},
                textSize ? {fontSize: textSize} : null,
              ]}>
              {name.charAt(0).toUpperCase()}
            </Text>
          </View>
        );
      } else {
        return (
          <View
            style={{
              justifyContent: 'center',
              alignSelf: 'center',
              alignItems: 'center',
              width: size,
              height: size,
              backgroundColor: backgroundColor ?? backgroundColorOfName,
              borderRadius: borderRadius,
              borderWidth: 1,
              borderColor: avatarBorderColor,
            }}>
            <Text
              style={[
                {color: SRXColor.White},
                textSize ? {fontSize: textSize} : null,
              ]}>
              {name.charAt(0).toUpperCase()}
            </Text>
          </View>
        );
      }
    } else {
      return <View />;
    }
  }
}

Avatar.propTypes = {
  /**Size of the Avatar */
  size: PropTypes.number.isRequired,

  /**Must have either name or imageUrl, if provide both than will use imageUrl*/
  imageUrl: PropTypes.string,
  name: PropTypes.string,

  /**Optional - PropTypes below are optional, will use default setting if not provided*/
  borderColor: PropTypes.string,
  textSize: PropTypes.number,
  borderLess: PropTypes.bool,
  backgroundColor: PropTypes.string,
};
export {Avatar};
