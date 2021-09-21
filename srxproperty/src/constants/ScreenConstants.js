import { Dimensions, Platform } from 'react-native';

const IS_IOS = Platform.OS === "ios";

const dimen = Dimensions.get('window');

const IS_IPHONE_X = (
  IS_IOS &&
  !Platform.isPad &&
  !Platform.isTVOS &&
  ((dimen.height === 812 || dimen.width === 812) || (dimen.height === 896 || dimen.width === 896))
);

export { IS_IOS, IS_IPHONE_X };