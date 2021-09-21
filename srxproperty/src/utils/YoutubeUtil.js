import {YoutubePO} from '../dataObject';
import {StringUtil} from './StringUtil';

function getValidatedYoutubeLink(link) {
  var match = StringUtil.validateYoutubeLink(link);
  if (match && match[2].length == 11) {
    var url = 'https://www.youtube.com/embed/' + match[2];
    var thumbnailUrl =
      'https://img.youtube.com/vi/' + match[2] + '/default.jpg';
    return new YoutubePO({url, thumbnailUrl, id: 'youtubeVideo'});
  }
  return null;
}

const YoutubeUtil = {
  getValidatedYoutubeLink,
};

export {YoutubeUtil};
