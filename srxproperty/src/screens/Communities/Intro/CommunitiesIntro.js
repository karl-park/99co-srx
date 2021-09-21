import React, {Component} from 'react';
import {
  View,
  Image,
  ScrollView,
  Dimensions,
  ImageBackground,
} from 'react-native';
import {connect} from 'react-redux';
import PageControl from 'react-native-page-control';
import PropTypes from 'prop-types';

import {
  Communities_Intro_Main,
  Communities_Intro_Main_Android,
  Communities_Intro_Slide1,
  Communities_Intro_Slide2,
  Communities_Intro_Slide3,
} from '../../../assets';
import {Spacing} from '../../../styles';
import {SRXColor, IS_IPHONE_X, IS_IOS} from '../../../constants';
import {HorizontalFlatList, Button, FeatherIcon} from '../../../components';
import {Navigation} from 'react-native-navigation';

var {width} = Dimensions.get('window');
var slides = [
  {
    id: 0,
    data: Communities_Intro_Slide1,
  },
  {
    id: 1,
    data: Communities_Intro_Slide2,
  },
  {
    id: 2,
    data: Communities_Intro_Slide3,
  },
];

class CommunitiesIntro extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: true,
        animate: true,
      },
    };
  }

  state = {
    currentIndex: 0,
  };

  constructor(props) {
    super(props);
    this.viewabilityConfig = {
      minimumViewTime: 500,
      viewAreaCoveragePercentThreshold: 70,
    };
    this.dimissIntro = this.dimissIntro.bind(this);
  }

  onViewableItemsChanged = ({viewableItems, changed}) => {
    const {currentIndex} = this.state;
    var newIndex = currentIndex;
    if (viewableItems.length == 1) {
      const firstObject = viewableItems[0];
      newIndex = firstObject.index;
      this.setState({currentIndex: newIndex});
    }
  };

  dimissIntro() {
    const {onCloseIntro} = this.props;
    if (onCloseIntro) {
      onCloseIntro();
    }
    Navigation.dismissModal(this.props.componentId);
  }

  renderItem = ({item, index}) => {
    return (
      <View
        key={index}
        style={{
          width: width - Spacing.XL * 2,
          alignItems: 'center',
          justifyContent: 'center',
          alignContent: 'center',
        }}>
        <Image
          source={item.data}
          style={{aspectRatio: 0.6}}
          resizeMode={'contain'}
        />
      </View>
    );
  };

  renderCloseButton() {
    return (
      <Button
        buttonStyle={{
          position: 'absolute',
          margin: Spacing.L,
          marginTop: IS_IOS ? Spacing.XL : Spacing.L,
        }}
        leftView={<FeatherIcon name="x" size={25} color={SRXColor.Black} />}
        onPress={() => this.dimissIntro()}
      />
    );
  }

  renderPaginatedIntroSlides() {
    const {currentIndex} = this.state;
    return (
      <View
        style={{marginTop: 200, marginStart: Spacing.M, marginEnd: Spacing.M}}>
        <HorizontalFlatList
          data={slides}
          bounces={false}
          renderItem={this.renderItem}
          keyExtractor={item => item.id}
          indicatorStyle={'white'}
          onViewableItemsChanged={this.onViewableItemsChanged}
          getItemLayout={(data, index) => ({
            length: width,
            offset: width * index,
            index,
          })}
          renderItem={this.renderItem}
          viewabilityConfig={this.viewabilityConfig}
          extraData={this.state}
          pagingEnabled={true}
          scrollEnabled={true}
        />
        <PageControl
          numberOfPages={slides.length}
          currentPage={currentIndex}
          pageIndicatorTintColor={SRXColor.White}
          currentPageIndicatorTintColor={SRXColor.Purple}
          indicatorStyle={{
            borderWidth: 1,
            borderColor: SRXColor.White,
          }}
        />
      </View>
    );
  }

  renderCommunityFeedButton() {
    return (
      <View
        style={{
          alignItems: 'center',
          justifyContent: 'center',
          position: 'absolute',
          bottom: 0,
          marginLeft: Spacing.L,
          marginEnd: Spacing.L,
          alignSelf: 'center',
          marginBottom: Spacing.XL,
          width: width - Spacing.XL - Spacing.L,
        }}>
        <Button
          buttonStyle={{
            width: '100%',
            alignItems: 'center',
            justifyContent: 'center',
          }}
          textStyle={{fontWeight: 'bold', color: SRXColor.Purple}}
          buttonType={Button.buttonTypes.primary}
          onPress={() => this.dimissIntro()}>
          See community feeds
        </Button>
      </View>
    );
  }

  render() {
    return (
      <View>
        <ScrollView
          contentContainerStyle={{
            alignItems: 'center',
          }}
          contentInsetAdjustmentBehavior={true}>
          <ImageBackground
            style={{
              width: '100%',
              height: undefined,
              aspectRatio: 5 / 31,
            }}
            resizeMode={'contain'}
            source={
              IS_IOS ? Communities_Intro_Main : Communities_Intro_Main_Android
            }>
            {this.renderPaginatedIntroSlides()}
            {this.renderCommunityFeedButton()}
          </ImageBackground>
        </ScrollView>
        {this.renderCloseButton()}
      </View>
    );
  }
}

CommunitiesIntro.propTypes = {
  onCloseIntro: PropTypes.func,
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(mapStateToProps)(CommunitiesIntro);
