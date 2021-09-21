import { Platform } from "react-native";
import Enquiry from "./EnquirySheet";
import { Navigation } from "react-native-navigation";
import { EnquirySheetSource } from "./EnquirySheetSource";

const show = passProps => {
  Navigation.showOverlay({
    component: {
      name: 'Enquiry.contactSheet',
      passProps,
      options: {
        layout: {
          componentBackgroundColor: 'transparent',
        },
        overlay: {
          interceptTouchOutside: true,
        },
        // modalPresentationStyle: Platform.OS === 'ios' ? "overFullScreen" : "overCurrentContext",
        // layout: {
        //   backgroundColor: 'transparent',
        // },
        // animations: {
        //   showModal: {
        //     enable: false
        //   },
        //   dismissModal: {
        //     enable: false
        //   }
        // }
      },
    },
  });
};

const EnquirySheet = {
  show
};

// EnquirySheet.Sources = EnquirySheetSource;

export { EnquirySheet, Enquiry, EnquirySheetSource };
