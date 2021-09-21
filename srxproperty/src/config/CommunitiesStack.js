import {Navigation} from 'react-native-navigation';
import {IS_IOS} from '../constants';

showNewPostModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'NewCommunitiesPost',
      children: [
        {
          component: {
            name: 'Communities.NewPost',
            passProps,
          },
        },
      ],
    },
  });
};

showCommunityOptions = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunityOptions',
      children: [
        {
          component: {
            name: 'Communities.CommunityOptions',
            passProps,
          },
        },
      ],
    },
  });
};

showCommunitiesModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunitiesModal',
      children: [
        {
          component: {
            name: 'Communities.CommunitiesModal',
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

showSortModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunitiesSortModal',
      children: [
        {
          component: {
            name: 'Communities.SortPostModal',
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

showAddPropertyModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'AddProperty',
      children: [
        {
          component: {
            name: 'Communities.AddProperty',
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

showSignInRegisterModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'SignInRegister',
      children: [
        {
          component: {
            name: 'Communities.SignInRegister',
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

showSharePostModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'SharePostWithComment',
      children: [
        {
          component: {
            name: 'Communities.SharePostWithComment',
            passProps,
          },
        },
      ],
    },
  });
};

showMobileVerificationModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'VerifyMobileModal',
      children: [
        {
          component: {
            name: 'Communities.VerifyMobileModal',
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

showOTPVerificationModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'VerifyOTPModal',
      children: [
        {
          component: {
            name: 'Communities.VerifyOTPModal',
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

showPostContact = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunityPostContact',
      children: [
        {
          component: {
            name: 'Communities.CommunityPostContact',
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

showShareOptionsModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'ShareOptionsModal',
      children: [
        {
          component: {
            name: 'Communities.ShareModal',
            passProps,
            options: modalOptions,
          },
        },
      ],
    },
  });
};

showDetailActionOptionsModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'DetailsOptionsModal',
      children: [
        {
          component: {
            name: 'Communities.DetailActionOptionsModal',
            passProps,
            options: modalOptions,
          },
        },
      ],
    },
  });
};

showReportAbuse = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunitiesReportAbuse',
      children: [
        {
          component: {
            name: 'Communities.ReportAbuse',
            passProps,
          },
        },
      ],
    },
  });
};

showCommunitiesIntro = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunitiesIntro',
      children: [
        {
          component: {
            name: 'Communities.CommunitiesIntro',
            passProps,
            options: {
              modalPresentationStyle: 'overFullScreen',
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

showGRCNewPost = passProps => {
  Navigation.showModal({
    stack: {
      id: 'GRCNewPost',
      children: [
        {
          component: {
            name: 'GRC.CommunitiesGRCNewPost',
            passProps,
          },
        },
      ],
    },
  });
};

showCommentSortOptionsModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommentSortOptionsModal',
      children: [
        {
          component: {
            name: 'Communities.CommunitiesCommentSortModal',
            passProps,
            options: modalOptions,
          },
        },
      ],
    },
  });
};

//Note: most of modal has same options
const modalOptions = {
  modalPresentationStyle: IS_IOS ? 'overFullScreen' : 'overCurrentContext',
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
};

showCommunitySelectionModal = passProps => {
  Navigation.showModal({
    stack: {
      id: 'CommunitySelection',
      children: [
        {
          component: {
            name: 'GRC.CommunitySelection',
            passProps,
          },
        },
      ],
    },
  });
};

const CommunitiesStack = {
  showNewPostModal,
  showCommunitiesModal,
  showSortModal,
  showAddPropertyModal,
  showSignInRegisterModal,
  showSharePostModal,
  showMobileVerificationModal,
  showOTPVerificationModal,
  showPostContact,
  showShareOptionsModal,
  showDetailActionOptionsModal,
  showReportAbuse,
  showCommunityOptions,
  showCommunitiesIntro,
  showGRCNewPost,
  showCommunitySelectionModal,
  showCommentSortOptionsModal,
};

export {CommunitiesStack};
