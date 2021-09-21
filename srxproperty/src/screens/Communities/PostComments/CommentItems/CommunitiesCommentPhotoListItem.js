import React, {Component} from 'react';
import {View, Image, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';

import {SRXColor} from '../../../../constants';
import {FeatherIcon} from '../../../../components';
import {ObjectUtil} from '../../../../utils';

class CommunitiesCommentPhotoListItem extends Component {
  removePhoto = () => {
    const {photo, onRemove} = this.props;
    if (onRemove) {
      onRemove(photo);
    }
  };

  renderRemoveButton() {
    return (
      <TouchableOpacity
        style={Styles.removeContainer}
        onPress={this.removePhoto}>
        <FeatherIcon name="x" size={18} color={SRXColor.White} />
      </TouchableOpacity>
    );
  }

  render() {
    const {photo} = this.props;
    if (!ObjectUtil.isEmpty(photo)) {
      return (
        <View>
          <Image
            source={{uri: photo.uri}}
            style={Styles.imageStyle}
            resizeMode={'cover'}
          />
          {this.renderRemoveButton()}
        </View>
      );
    }

    return <View />;
  }
}

CommunitiesCommentPhotoListItem.propTypes = {
  photo: PropTypes.object,
  onRemove: PropTypes.func,
};

const Styles = {
  imageStyle: {
    width: 120,
    height: 90,
    marginTop: 10,
    marginRight: 10,
  },
  removeContainer: {
    backgroundColor: SRXColor.Teal,
    width: 28,
    height: 28,
    alignSelf: 'flex-end',
    position: 'absolute',
    borderRadius: 14,
    alignItems: 'center',
    justifyContent: 'center',
  },
};
export {CommunitiesCommentPhotoListItem};
