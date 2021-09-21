import {Navigation} from 'react-native-navigation';
import {Platform} from 'react-native';

showSGHomeSignInModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'SGHomeSignInStack',
      children: [
        {
          component: {
            name: 'LoginStack.sgHomeSignInScreen',
            passProps,
          },
        },
      ],
    },
  });
};

showSGHomeUpdateDetailsModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'SGHomeUpdateDetails',
      children: [
        {
          component: {
            name: 'LoginStack.sgHomeUpdateDetailScreen',
            passProps,
          },
        },
      ],
    },
  });
};

showSignInRegisterModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'SignInRegisterStack',
      children: [
        {
          component: {
            name: 'LoginStack.signInRegister',
            passProps,
            options: {
              modalPresentationStyle: 'overFullScreen',
            },
          },
        },
      ],
    },
  });
};

const LoginStack = {
  showSGHomeSignInModal,
  showSGHomeUpdateDetailsModal,
  showSignInRegisterModal,
};

export {LoginStack};
