import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {ImagePreview} from '../../../../components';
import {
  ListingPhotoPO,
  DroneViewPO,
  VirtualTourPO,
  YoutubePO,
  AgentPO,
  ListingPO,
} from '../../../../dataObject';
import {GoogleAnalyticUtil} from '../../../../utils';
import {ListingDetailsViewingItems} from '../../../../constants';
import {ImagePreviewSource} from '../../../../components/ImagePreview/ImagePreviewSource';

class ListingPhotos extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO),
    listingMedias: PropTypes.arrayOf(PropTypes.instanceOf(ListingPhotoPO)),
    droneViews: PropTypes.arrayOf(PropTypes.instanceOf(DroneViewPO)),
    virtualTours: PropTypes.arrayOf(PropTypes.instanceOf(VirtualTourPO)),
    videoPOs: PropTypes.arrayOf(PropTypes.instanceOf(YoutubePO)),
  };

  static defaultProps = {
    listingMedias: [],
    droneViews: [],
    virtualTours: [],
    videoPOs: [],
  };

  onDisplayFullScreen = () => {
    GoogleAnalyticUtil.trackListingDetailsUserActions({
      viewingItem: ListingDetailsViewingItems.listingMedias,
    });
  };

  render() {
    const {
      listingMedias,
      droneViews,
      virtualTours,
      videoPOs,
      listingPO,
    } = this.props;
    const combinedItems = [
      ...droneViews,
      ...virtualTours,
      ...videoPOs,
      ...listingMedias,
    ];
    return (
      <ImagePreview
        mediaItems={combinedItems}
        listingPO={listingPO}
        imagePreviewSource={ImagePreviewSource.ListingDetails}
        onDisplayFullScreen={this.onDisplayFullScreen}
      />
    );
  }
}

export {ListingPhotos};
