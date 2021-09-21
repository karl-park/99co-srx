import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';

import {ImagePreview} from '../../../../components';
import {CommunityPostMediaPO, YoutubePO} from '../../../../dataObject';

class CommunitiesPostPhotos extends Component {
  render() {
    const {medias, youtube} = this.props;
    const combinedItems = [...youtube, ...medias];
    return <ImagePreview mediaItems={combinedItems} />;
  }
}

CommunitiesPostPhotos.propTypes = {
  medias: PropTypes.arrayOf(PropTypes.instanceOf(CommunityPostMediaPO)),
  youtube: PropTypes.arrayOf(PropTypes.instanceOf(YoutubePO)),
};

export {CommunitiesPostPhotos};
