import {
  ChatStack,
  PropertySearchStack,
  ShortlistStack,
  XValueStack,
  MenuStack,
} from './';

const MainScreenTab = () => {
  return {
    bottomTabs: {
      children: [
        PropertySearchStack(),
        ShortlistStack(),
        XValueStack(),
        ChatStack(),
        MenuStack(),
      ],
    },
  };
};

export {MainScreenTab};
