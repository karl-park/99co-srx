import {AsyncStorage} from 'react-native';
import {Navigation} from 'react-native-navigation';

import Intro from './Intro';

const storageKey = '@Display_Introduction_V1';

const show = passProps => {
  AsyncStorage.getItem(storageKey, (err, result) => {
    AsyncStorage.setItem(storageKey, 'true');
    Navigation.showModal({
      stack: {
        id: 'component-introduction',
        children: [
          {
            component: {
              name: 'SplashScreen.Intro',
              passProps: {
                ...passProps,
                hideIntro: hide,
              },
              options: {
                topBar: {
                  visible: false,
                },
                animations: {
                  showModal: {
                    enable: false,
                  },
                  dismissModal: {
                    enable: true,
                  },
                },
                modalPresentationStyle: 'overFullScreen',
              },
            },
          },
        ],
      },
    });
  });
};

const hide = () => {
  Navigation.dismissModal('component-introduction');
};

const IntroSplashScreen = {
  show,
};

export {IntroSplashScreen, Intro};
