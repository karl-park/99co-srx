import {Navigation} from 'react-native-navigation';
import {IS_IOS} from '../../constants';

showMultiUploadOptions = passProps => {
  console.log("show modal");
  Navigation.showModal({
    stack: {
      id: 'UploadOptionModal',
      children: [
        {
          component: {
            name: 'UploadOptionModal',
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

const MultiImagePicker = {
  showMultiUploadOptions,
};

export {MultiImagePicker};
