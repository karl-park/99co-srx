import {TabBarIcon_Chat} from '../assets';
import {IS_IOS} from '../constants';
import {AppTheme} from '../styles';
import DeviceInfo from 'react-native-device-info';

const systemVersion = DeviceInfo.getSystemVersion();

const ChatStack = () => ({
  stack: {
    id: 'ChatTab',
    children: [
      {
        component: {
          name: 'ChatStack.homeScreen',
        },
      },
    ],
    options: {
      modalPresentationStyle: "overFullScreen",
      bottomTab: {
        icon: TabBarIcon_Chat, //need to be replaced with new icon, if make changes, please changes ChatHomeScreen updateUnReadConverstationBadge method together
        textColor: AppTheme.bottomTabTextColor,
        selectedTextColor: AppTheme.bottomTabSelectedTextColor,
        iconColor: AppTheme.bottomTabIconColor,
        selectedIconColor: AppTheme.bottomTabSelectedIconColor,
        iconInsets:
          IS_IOS && systemVersion >= 11
            ? {top: 0, left: 0, bottom: -8, right: 0}
            : {top: 0, left: 0, bottom: 0, right: 0},
      },
    },
  },
});

export {ChatStack};
