import {Navigation} from 'react-native-navigation';
import {IS_IOS} from '../../constants';
import {OptionsSelectionModal} from './OptionsSelectionModal';

const show = passProps => {
  Navigation.showModal({
    stack: {
      id: 'OptionsSelectionModal',
      children: [
        {
          component: {
            name: 'AndroidOptionsDialog.SelectionModal',
            passProps,
            options: {
              modalPresentationStyle: IS_IOS
                ? 'overFullScreen'
                : 'overCurrentContext',
              topBar: {
                visible: false,
                drawBehind: true,
              },
              layout: {
                componentBackgroundColor: 'transparent',
              },
              animations: {
                showModal: IS_IOS
                  ? {enabled: false}
                  : {
                      alpha: {
                        from: 0,
                        to: 1,
                        duration: 300,
                        interpolation: 'accelerate',
                      },
                    },
                dismissModal: IS_IOS
                  ? {enabled: false}
                  : {
                      alpha: {
                        from: 1,
                        to: 0,
                        duration: 300,
                        interpolation: 'deaccelerate',
                      },
                    },
              },
            },
          },
        },
      ],
    },
  });
};

const AndroidOptionDialog = {
  show,
};

export {AndroidOptionDialog, OptionsSelectionModal};
