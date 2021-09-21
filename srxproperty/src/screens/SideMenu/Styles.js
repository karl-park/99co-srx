import { SRXColor } from "../../constants";
import { Spacing } from "../../styles";

const Styles = {
  //Common Styles
  bodyTextNormalStyle: {
    fontSize: Spacing.M,
    color: "#333333"
  },

  userInitialNameCirlce: {
    justifyContent: "center",
    alignItems: "center",
    width: 40,
    height: 40,
    backgroundColor: "#F0E68C",
    borderRadius: 20
  },
  //userProfile Styles
  userProfileContainerStyle: {
    backgroundColor: SRXColor.DarkBlue,
    paddingBottom: 24,
    paddingTop: Spacing.XL
  },

  //Upcoming Appointment style
  appointmentContainerStyle: {
    backgroundColor: "#E7F1FF",
    paddingBottom: Spacing.M,
    paddingTop: Spacing.M,
    justifyContent: "center",
    alignItems: "center",
    flexDirection: "row"
  },

  //Side Menu Item Styles.
  sideMenuContainerStyle: {
    flex: 1,
    paddingHorizontal: Spacing.S
  },
  sideMenuSubContainerStyle: {
    flexDirection: "row",
    justifyContent: "center",
    paddingVertical: Spacing.S
  },
  sideMenuIconContainerStyle: {
    alignItems: "flex-end",
    flex: 1
  },

  //Update Profile
  accountDetailsContainer: {
    flex: 1,
    padding: Spacing.M,
    backgroundColor: SRXColor.White
  },
  inputContainer: {
    paddingVertical: Spacing.XS,
  },
  actionBtnStyle: {
    width: 134,
    height: 40,
    marginTop: 24,
    marginBottom: 5,
    borderRadius: 10,
    backgroundColor: SRXColor.Orange
  },
  actionBtnTextStyle: {
    fontSize: 16,
    lineHeight: 40,
    textAlign: "center",
    fontWeight: "600",
    color: "white"
  },

  eachSettingContainer: {
    flex: 1,
    height: 55,
    alignItems: "center",
    paddingHorizontal: Spacing.M,
    flexDirection: "row"
  },
  logoutContainer: {
    flex: 1,
    justifyContent: "flex-end",
    padding: Spacing.S,
    height: 150
  },

  maskPasswordFieldContainer: {
    flex: 1,
    flexDirection: "row",
    paddingHorizontal: Spacing.S,
    marginTop: Spacing.S,
    borderWidth: 1,
    borderColor: "#8DABC4",
    alignItems: "center",
    justifyContent: "center"
  },
  //OTP
  vertifyContainerSytle: {
    flex: 1,
    padding: Spacing.M,
    backgroundColor: SRXColor.White
  },
  verifySubContainerStyle: {
    flex: 1,
    alignItems: "center",
    marginTop: 70,
    padding: Spacing.M
  },
  otpScreenInfoTextStyle: {
    color: SRXColor.Black,
    textAlign: "center",
    lineHeight: 22
  },
  codeInputStyle: {
    color: SRXColor.Black,
    fontWeight: "600",
    backgroundColor: SRXColor.SmallBodyBackground
  }
};

export { Styles };
