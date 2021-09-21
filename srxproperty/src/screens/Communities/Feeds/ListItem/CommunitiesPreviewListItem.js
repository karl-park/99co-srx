import React, {Component} from 'react';
import {View, Image, Dimensions, Alert} from 'react-native';
import {SRXColor} from '../../../../constants';
import {
  BodyText,
  Separator,
  FeatherIcon,
  SmallBodyText,
  TouchableHighlight,
  Button,
  Heading2,
  ExtraSmallBodyText,
} from '../../../../components';
import {Spacing, Typography} from '../../../../styles';
import {ObjectUtil, StringUtil} from '../../../../utils';
import {CommunitiesConstant} from '../../Constants';
import PropTypes from 'prop-types';
import {Communities_Icon_New} from '../../../../assets';

var {height, width} = Dimensions.get('window');

class CommunitiesPreviewListItem extends Component {
  static propTypes = {
    postSelected: PropTypes.func,
    onSelectPost: PropTypes.func,
    onPressSeeAll: PropTypes.func,
  };

  constructor(props) {
    super(props);
    this.onSelectPost = this.onSelectPost.bind(this);
  }

  onSelectPost() {
    const {postSelected, communityFeedPO} = this.props;
    if (postSelected) {
      postSelected(communityFeedPO.id);
    }
  }

  onPressSeeAllFeeds() {
    const {onPressSeeAll} = this.props;
    if (onPressSeeAll) {
      onPressSeeAll();
    }
  }

  renderCellHeader() {
    return (
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginHorizontal: Spacing.S,
        }}>
        {this.renderLeftSide()}
        <View style={{flex: 1}} />
        {this.renderRightSide()}
      </View>
    );
  }

  renderLeftSide() {
    return (
      <View style={{flexDirection: 'row'}}>
        <Heading2 style={{alignSelf: 'center'}}>Community feed</Heading2>
        <Image
          source={Communities_Icon_New}
          style={{width: 35, height: 35, marginLeft: Spacing.XS}}
          resizeMode={'contain'}
        />
      </View>
    );
  }

  renderRightSide() {
    return (
      <View style={{flexDirection: 'row', marginRight: -Spacing.XS}}>
        <Button
          textStyle={[
            Typography.SmallBody,
            {color: SRXColor.Teal, alignSelf: 'center'},
          ]}
          rightView={
            <FeatherIcon
              name="chevron-right"
              size={24}
              color={SRXColor.Gray}
              style={{alignSelf: 'center'}}
            />
          }
          onPress={() => this.onPressSeeAllFeeds()}>
          See all feeds
        </Button>
      </View>
    );
  }

  renderPostDate() {
    const {communityFeedPO} = this.props;
    const {community} = communityFeedPO;

    let communityName = '';
    if (!ObjectUtil.isEmpty(community) && !ObjectUtil.isEmpty(community.name)) {
      communityName = ' • ' + community.name;
    }
    return (
      <ExtraSmallBodyText
        style={{
          color: SRXColor.Gray,
        }}>
        {communityFeedPO.getDateDisplay()}
        <ExtraSmallBodyText
          style={{
            textTransform: 'capitalize',
            color: SRXColor.Gray,
          }}>
          {communityName}
        </ExtraSmallBodyText>
      </ExtraSmallBodyText>
    );
  }

  renderContent() {
    return (
      <View
        style={{
          marginBottom: Spacing.XS,
          marginHorizontal: Spacing.S,
          flexDirection: 'row',
        }}>
        {this.renderPostInfo()}
        {this.renderPostImage()}
      </View>
    );
  }

  renderPostImage() {
    const {communityFeedPO} = this.props;
    if (!ObjectUtil.isEmpty(communityFeedPO.media)) {
      const firstMedia = communityFeedPO.media[0];
      if (firstMedia.type === CommunitiesConstant.mediaType.photo) {
        return (
          <View style={{flex: 1, marginTop: Spacing.S}}>
            <Image
              style={{
                flex: 1,
              }}
              source={{uri: firstMedia.url}}
              resizeMode={'cover'}
            />
          </View>
        );
      }
    }
  }

  renderPostInfo() {
    return (
      <View style={{flex: 3}}>
        {this.renderPostTitleAndDetail()}
        {this.renderPostDate()}
      </View>
    );
  }

  renderPostTitleAndDetail() {
    const {communityFeedPO} = this.props;
    return (
      <View style={{paddingRight: Spacing.XS}}>
        <BodyText style={{marginTop: Spacing.S}} numberOfLines={2}>
          {StringUtil.capitalize(communityFeedPO.title)}
        </BodyText>
        <ExtraSmallBodyText
          style={{marginVertical: Spacing.XS, color: SRXColor.Gray}}
          numberOfLines={2}>
          {communityFeedPO.content}
        </ExtraSmallBodyText>
      </View>
    );
  }

  render() {
    return (
      <View style={{backgroundColor: SRXColor.White}}>
        <TouchableHighlight
          style={styles.container}
          onPress={this.onSelectPost}>
          <View style={styles.subContainer}>
            {this.renderCellHeader()}
            <Separator />
            {this.renderContent()}
          </View>
        </TouchableHighlight>
      </View>
    );
  }
}

const styles = {
  container: {
    borderRadius: 10,
    //Shadow for iOS
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {height: 1, width: 1},
    shadowOpacity: 0.32,
    shadowRadius: 1,
    marginRight: Spacing.M,
  },
  subContainer: {
    overflow: 'hidden',
    //Border
    borderWidth: 1,
    borderColor: '#DCDCDC',
    borderRadius: 8,
    //Background
    backgroundColor: SRXColor.White,
    width: width - Spacing.XL,
  },
  newTextContainer: {
    borderWidth: 1,
    backgroundColor: '#FE7F7F',
    borderColor: SRXColor.Transparent,
    marginLeft: Spacing.S,
    marginVertical: Spacing.XS / 2,
    paddingHorizontal: Spacing.XS / 2,
  },
};

export {CommunitiesPreviewListItem};
