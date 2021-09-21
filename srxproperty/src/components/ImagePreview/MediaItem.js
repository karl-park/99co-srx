import PropTypes from "prop-types";

const MediaItem = PropTypes.shape({
  thumbnailUrl: PropTypes.string.isRequired,
  url: PropTypes.string.isRequired,
  /*
   * if true, will display url in webview
   */
  isWebContent: PropTypes.bool,
  /*
   * Only works if isWebContent = true
   * When embedInIframe = true, this item will be shown in iframe with the provided url
   */
  embedInIframe: PropTypes.bool,
  getOverlayIcon: PropTypes.func
});

export { MediaItem };
