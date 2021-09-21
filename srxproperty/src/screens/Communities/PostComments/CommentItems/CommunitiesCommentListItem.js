import React, {Component} from 'react';
import {
  View,
  Image,
  Dimensions,
  Linking,
  TouchableWithoutFeedback,
  TouchableOpacity,
} from 'react-native';
import PropTypes from 'prop-types';
import Autolink from 'react-native-autolink';
import {connect} from 'react-redux';

import {Avatar, BodyText, Separator} from '../../../../components';
import {SRXColor} from '../../../../constants';
import {Spacing, Typography} from '../../../../styles';
import {CommunitiesConstant} from '../../Constants';
import {CommunityPostPO} from '../../../../dataObject';
import {ObjectUtil} from '../../../../utils';
import {FullScreenImagePreview} from '../../../../components/ImagePreview/FullScreenImagePreview';

var {width} = Dimensions.get('window');

class CommunitiesCommentListItem extends Component {
  constructor(props) {
    super(props);

    this.state = {
      photoWidth: 0,
      photoHeight: 0,

      photoMaxWidth: width - 100,
      photoMaxHeight: 200,
    };

    this.onClickComment = this.onClickComment.bind(this);
    this.reportComment = this.reportComment.bind(this);
    this.showPostContact = this.showPostContact.bind(this);
    this.deleteComment = this.deleteComment.bind(this);
    this.showFullScreenimage = this.showFullScreenimage.bind(this);
  }

  onClickComment() {
    const {userPO, comment} = this.props;
    const isOwnComment =
      !ObjectUtil.isEmpty(userPO) &&
      userPO.encryptedUserId === comment.user.encryptedUserId;
    if (isOwnComment) {
      this.deleteComment();
    } else {
      this.reportComment();
    }
  }

  reportComment() {
    const {comment, reportComment} = this.props;
    if (reportComment) {
      reportComment(comment);
    }
  }

  showPostContact() {
    const {comment, showPostCommentContact} = this.props;
    if (showPostCommentContact) {
      showPostCommentContact(comment);
    }
  }

  deleteComment() {
    const {comment, deleteComment} = this.props;
    if (deleteComment) {
      deleteComment(comment);
    }
  }

  showFullScreenimage(photo) {
    FullScreenImagePreview.show({
      mediaItems: [photo],
      startIndex: 0,
      shouldShowThumbnails: false,
    });
  }

  componentDidMount() {
    const {comment} = this.props;
    if (Array.isArray(comment.media) && comment.media.length > 0) {
      var photoUrl = comment.media[0].url;
      Image.getSize(photoUrl, (width, height) => {
        const {photoMaxWidth, photoMaxHeight} = this.state;

        //reduce original image sizes
        var photoWidth = width / 3;
        var photoHeight = height / 3;

        this.setState({
          photoWidth: photoWidth > photoMaxWidth ? photoMaxWidth : photoWidth,
          photoHeight:
            photoHeight > photoMaxHeight ? photoMaxHeight : photoHeight,
        });
      });
    }
  }

  renderUserAvator() {
    const {user} = this.props.comment;
    const name = user ? user.getCommunityPostUserName() : '';
    const photoURL = user ? user.photo : '';
    return (
      <View>
        <Avatar
          name={name}
          imageUrl={photoURL}
          size={35}
          borderColor={'#DCDCDC'}
          textSize={18}
        />
      </View>
    );
  }

  renderNameAndComment() {
    const {user} = this.props.comment;
    if (!ObjectUtil.isEmpty(user)) {
      return (
        <BodyText
          style={{
            color: SRXColor.Gray,
            marginTop: Spacing.XS / 2,
            marginBottom: Spacing.XS,
            marginLeft: Spacing.S,
            maxWidth: 200,
          }}
          numberOfLines={1}>
          {user.getCommunityPostUserName()}
        </BodyText>
      );
    }
  }

  renderDateDisplay() {
    const {comment} = this.props;
    if (!ObjectUtil.isEmpty(comment)) {
      var date = ' • ' + comment.getDateDisplay();
      return (
        <BodyText
          style={{
            color: SRXColor.Gray,
            marginTop: Spacing.XS / 2,
            marginBottom: Spacing.XS,
          }}>
          {date}
        </BodyText>
      );
    }
  }

  renderCommentPhoto() {
    const {photoWidth, photoHeight} = this.state;
    const {comment} = this.props;
    if (Array.isArray(comment.media) && comment.media.length > 0) {
      var photo = comment.media[0];
      return (
        <TouchableOpacity onPress={() => this.showFullScreenimage(photo)}>
          <Image
            source={{uri: photo.url}}
            style={{
              width: photoWidth,
              height: photoHeight,
            }}
          />
        </TouchableOpacity>
      );
    }
  }

  renderDescription() {
    const {content} = this.props.comment;
    const {directWebScreen} = this.props;
    if (!ObjectUtil.isEmpty(content)) {
      return (
        <Autolink
          text={content}
          phone={true}
          email={true}
          stripPrefix={false}
          linkStyle={[
            Typography.Body,
            {color: SRXColor.Teal, textDecorationLine: 'underline'},
          ]}
          renderText={text => (
            <BodyText
              style={{
                color: SRXColor.Black,
                lineHeight: 22,
              }}>
              {text}
            </BodyText>
          )}
          onPress={(url, match) => {
            console.log(url);
            console.log(match.getType());
            switch (match.getType()) {
              case 'url':
                console.log('check url');
                if (directWebScreen) {
                  directWebScreen(url);
                }
                break;
              default:
                Linking.openURL(url);
            }
          }}
        />
      );
    }
  }

  renderSeparator() {
    const {index, commentsTotal} = this.props;
    if (index + 1 != commentsTotal) {
      return <Separator />;
    }
    return null;
  }

  renderContent() {
    const {index, itemStyle} = this.props;
    //Index check needs
    if (index == 0) {
      return (
        <View style={{backgroundColor: SRXColor.LightGray}}>
          <View
            style={{
              paddingLeft: Spacing.M,
              paddingRight: Spacing.M,
              backgroundColor: SRXColor.White,
              ...itemStyle,
            }}>
            <View style={Styles.mainContainer}>
              <View style={{flexDirection: 'row', flexWrap: 'wrap'}}>
                <TouchableOpacity onPress={() => this.showPostContact()}>
                  <View style={{flexDirection: 'row'}}>
                    {this.renderUserAvator()}
                    {this.renderNameAndComment()}
                  </View>
                </TouchableOpacity>
                {this.renderDateDisplay()}
              </View>

              {/* Note: 35 means avator size + margin */}
              <View style={{marginLeft: 35 + Spacing.S}}>
                {this.renderCommentPhoto()}
                {this.renderDescription()}
              </View>
            </View>
            {this.renderSeparator()}
          </View>
        </View>
      );
    } else {
      return (
        <View
          style={{
            paddingLeft: Spacing.M,
            paddingRight: Spacing.M,
            backgroundColor: SRXColor.White,
          }}>
          <View style={Styles.mainContainer}>
            <View style={{flexDirection: 'row', flexWrap: 'wrap'}}>
              {/*//todo to use showPostContent() while merging branch */}
              <TouchableOpacity onPress={() => this.showPostContact()}>
                <View style={{flexDirection: 'row'}}>
                  {this.renderUserAvator()}
                  {this.renderNameAndComment()}
                </View>
              </TouchableOpacity>
              {this.renderDateDisplay()}
            </View>

            {/* Note: 35 means avator size + margin */}
            <View style={{marginLeft: 35 + Spacing.S}}>
              {this.renderCommentPhoto()}
              {this.renderDescription()}
            </View>
          </View>
          {this.renderSeparator()}
        </View>
      );
    }
  }

  render() {
    const {comment} = this.props;
    if (comment.type == CommunitiesConstant.postType.comment) {
      return (
        <TouchableWithoutFeedback onLongPress={() => this.onClickComment()}>
          {this.renderContent()}
        </TouchableWithoutFeedback>
      );
    }
    return <View />;
  }
}

CommunitiesCommentListItem.propTypes = {
  index: PropTypes.number,
  comment: PropTypes.instanceOf(CommunityPostPO),
  commentsTotal: PropTypes.number,
  directWebScreen: PropTypes.func,
  /**
   * A function to report a post, will pass back the comment (CommunityPostPO)
   */
  reportComment: PropTypes.func,
  /**
   * function to show user post contact
   */
  showPostCommentContact: PropTypes.func,
  /*
   * A function to delete a comment, will pass back the comment (CommunityPostPO)
   */
  deleteComment: PropTypes.func,

  itemStyle: PropTypes.object,
};

const Styles = {
  mainContainer: {
    marginTop: Spacing.XS,
    marginBottom: Spacing.XS,
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
  };
};

export default connect(
  mapStateToProps,
  null,
)(CommunitiesCommentListItem);
