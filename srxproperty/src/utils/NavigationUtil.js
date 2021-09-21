import {Navigation} from 'react-native-navigation';
import { ObjectUtil } from './ObjectUtil';

openUrl = ({url, fromComponentId}) => {
  var parse = require('url-parse');
  let parsedURL = parse(url, true);
  
  if (!ObjectUtil.isEmpty(parsedURL)) {
    console.log(parsedURL);
    const {pathname, query, origin} = parsedURL;
    const pathArray = pathname.split('/');
    if (origin.includes('srxtrainer.com') || origin.includes('srx.com.sg')) {
      if (pathArray.length > 1) {
        const firstPath = pathArray[1];
        if (
          firstPath.toLowerCase() === 'l' ||
          firstPath.toLowerCase() === 'listings'
        ) {
          if (pathArray.length > 2) {
            const secondPath = pathArray[2];
            if (!ObjectUtil.isEmpty(secondPath)) {
              Navigation.push(fromComponentId, {
                component: {
                  name: 'PropertySearchStack.ListingDetails',
                  passProps: {
                    listingId: secondPath,
                    refType: 'V',
                  },
                },
              });
              return;
            }
          }
        } else if (firstPath.toLowerCase() === 'community') {
          if (pathArray.length > 3) {
            if (pathArray[2].toLowerCase() === 'posts') {
              Navigation.push(fromComponentId, {
                component: {
                  name: 'Communities.CommunitiesPostDetails',
                  passProps: {
                    postId: pathArray[3],
                  },
                },
              });
              return;
            }
          }
        }
      }
    }
  } 

  Navigation.push(fromComponentId, {
    component: {
      name: 'Communities.generalWebScreen',
      passProps: {
        url: url,
        screenTitle: url,
      },
    },
  });
};

const NavigationUtil = {
  openUrl,
};

export {NavigationUtil};