import { SRXColor } from "../../../constants";
import { Spacing, Typography } from "../../../styles";
import { Platform } from "react-native";

const isIOS = Platform.OS === "ios";

const Styles = {
  //General Styles for SearchResult Screen
  subContainer: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center"
  },
  blueColorText: {
    fontWeight: "400",
    color: SRXColor.Teal,
    marginLeft: 3
  },

  filterAndSortContainer: {
    flexDirection: "row",
    height: 45,
    justifyContent: "space-between"
  },
  propertiesAndMapContainer: {
    flexDirection: "row",
    minHeight: 45,
    justifyContent: "space-between",
    alignItems: "center"
  },
  countContainer: {
    marginLeft: Spacing.XS / 2,
    width: 20,
    height: 20,
    borderRadius: 10,
    borderColor: SRXColor.LightGray,
    borderWidth: 1,
    alignItems: "center",
    justifyContent: "center"
  },

  //Enquire & Saved shortlisted
  generalFeedbackContainer: {
    flexDirection: "row",
    alignItems: "center",
    height: 60,
    backgroundColor: SRXColor.GeneralFeedBackMessages,
    paddingHorizontal: Spacing.XL,
    borderTopWidth: 1,
    borderTopColor: SRXColor.Teal
  },

  //Search top bar,
  topBarBackButton: {
    height: "100%",
    alignItems: "center"
  },
  topBarTextContainerStyle: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    marginLeft: Spacing.XS,
    height: "100%"
  }
};

export { Styles };
