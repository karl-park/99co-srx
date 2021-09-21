import {AsyncStorage} from 'react-native';

retrieveViewedListingIdList = () => {
  return new Promise(function(resolve, reject) {
    AsyncStorage.getItem('@viewListingIdLists', (err, result) => {
      if (err) {
        reject(err);
      } else {
        resolve(JSON.parse(result));
      }
    });
  });
};

saveViewedListingId = ({viewedListings}) => {
  return new Promise(function(resolve, reject) {
    AsyncStorage.setItem(
      '@viewListingIdLists',
      JSON.stringify(viewedListings),
      err => {
        if (err) {
          reject(err);
        } else {
          resolve(viewedListings);
        }
      },
    );
  });
};

const ListingUtil = {
  retrieveViewedListingIdList,
  saveViewedListingId,
};

export {ListingUtil};
