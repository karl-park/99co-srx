import { IS_IOS, IS_IPHONE_X } from "../constants";

const BottomTabsConstants = {
  bottomTabsHeight: IS_IOS? (IS_IPHONE_X?83:44): 56,
}

//for style of custom bottom tabs

export { BottomTabsConstants };