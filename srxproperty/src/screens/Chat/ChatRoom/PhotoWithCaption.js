import React, {Component} from 'react';
import {
  View,
  TouchableOpacity,
  StyleSheet,
  Image,
  TextInput,
  KeyboardAvoidingView,
} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import PropTypes from 'prop-types';
import {FeatherIcon, Separator} from '../../../components';
import {AppTopBar_BackBtn} from '../../../assets';
import {SRXColor, IS_IOS, IS_IPHONE_X} from '../../../constants';
import {Spacing, AppTheme} from '../../../styles';

import {Navigation} from 'react-native-navigation';

const TransparentTopBarStyle = {
  background: {
    color: 'transparent',
  },
  backButton: {
    color: AppTheme.topBarBackButtonColor,
    icon: AppTopBar_BackBtn,
    title: '',
  },
  leftButtonColor: SRXColor.Teal,
  rightButtonColor: SRXColor.Teal,
  elevation: 0,
};

class PhotoWithCaption extends Component {
  static options(passProps) {
    return {
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: IS_IOS ? false : true,
      },
      topBar: {
        visible: true,
        drawBehind: true,
        ...TransparentTopBarStyle,
      },
    };
  }

  static propTypes = {
    photoItem: PropTypes.object,
    sendImage: PropTypes.func,
  };

  static defaultProps = {
    photoItem: null,
  };

  constructor(props) {
    super(props);
    this.state = {
      caption: '',
    };
  }

  sendImage = () => {
    const {sendImage, photoItem} = this.props;
    const {caption} = this.state;
    if (sendImage) {
      Navigation.pop(this.props.componentId);
      let newPhotoItem = {...photoItem, caption};
      sendImage(newPhotoItem);
    }
  };

  onContentSizeChange = contentSize => {
    this.setState({screenHeight: contentSize.height});
  };

  renderBottomView() {
    return (
      <View style={{backgroundColor: SRXColor.White}}>
        <Separator />
        {this.renderCaption()}
      </View>
    );
  }

  renderCaption() {
    const {screenHeight, caption} = this.state;
    let restrictHeight = 4 * 25;
    let compareHeight = screenHeight > restrictHeight;
    return (
      <View
        style={{
          padding: Spacing.XS,
          flexDirection: 'row',
          alignItems: 'center',
        }}>
        <View style={{padding: 1, flex: 1}}>
          <TextInput
            style={[
              styles.textInputStyle,
              {
                height:
                  screenHeight < 50
                    ? 40
                    : compareHeight
                    ? restrictHeight
                    : null,
              },
            ]}
            autoCorrect={false}
            multiline={true}
            onContentSizeChange={e =>
              this.onContentSizeChange(e.nativeEvent.contentSize)
            }
            value={caption}
            placeholder={'Add a caption...'}
            onChangeText={newCaption =>
              this.setState({
                caption: newCaption,
              })
            }
          />
        </View>
        <TouchableOpacity
          style={{marginLeft: Spacing.XS}}
          onPress={() => this.sendImage()}>
          <View
            style={[
              {
                backgroundColor: SRXColor.Orange,
                width: 50,
                marginRight: Spacing.XS,
                justifyContent: 'center',
              },
              {borderRadius: 17, height: 34},
            ]}>
            <FeatherIcon
              name={'send'}
              size={20}
              color={SRXColor.Black}
              style={[
                {
                  alignSelf: 'center',
                  transform: [{rotate: '45deg'}],
                },
              ]}
            />
          </View>
        </TouchableOpacity>
      </View>
    );
  }

  render() {
    const {photoItem} = this.props;
    let imageUrl = photoItem.uri;
    if (IS_IOS) {
      return (
        <SafeAreaView style={{flex: 1}}>
          <KeyboardAvoidingView style={{flex: 1}} behavior="padding">
            <View style={styles.container}>
              <Image
                style={styles.image}
                source={{uri: imageUrl}}
                resizeMode={'cover'}
              />
              {this.renderBottomView()}
            </View>
          </KeyboardAvoidingView>
        </SafeAreaView>
      );
    } else {
      return (
        <View style={styles.container}>
          <Image
            style={styles.image}
            source={{uri: imageUrl}}
            resizeMode={'cover'}
          />
          {this.renderBottomView()}
        </View>
      );
    }
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: SRXColor.White,
  },
  image: {
    flexGrow: 1,
    height: null,
    width: null,
    alignItems: 'center',
    justifyContent: 'center',
  },
  textInputStyle: {
    borderWidth: 1,
    borderColor: SRXColor.LightGray,
    borderRadius: 20,
    marginLeft: Spacing.XS,
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    fontSize: 14,
    paddingTop: IS_IOS ? 10 : null,
  },
});

export {PhotoWithCaption};
