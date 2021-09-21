import { Platform } from "react-native";
import { Invite } from "./Invite";
import { Navigation } from "react-native-navigation";

Navigation.registerComponent("Invite.inviteSheet", () => Invite);

const show = (passProps) => {
  Navigation.showOverlay({
    component: {
      name: "Invite.inviteSheet",
      passProps,
      options: {
        overlay: {
          interceptTouchOutside: true
        }
        // modalPresentationStyle: Platform.OS === 'ios' ? "overFullScreen" : "overCurrentContext",
        // animations: {
        //   showModal: {
        //     enable: false
        //   },
        //   dismissModal: {
        //     enable: false
        //   }
        // }
      }
    }
  });
};

const InviteSheet = {
  show
};

export { InviteSheet };
