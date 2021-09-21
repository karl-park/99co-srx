import { Spacing } from "./Spacing";
import { SRXColor } from "../constants";

const CommonButtonStyle = {
  container: {
    borderRadius: 20,
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: Spacing.L
  },
  title: {
    fontWeight: "600",
    fontSize: 16,
    lineHeight: 40,
    textAlign: "center"
  }
};

const ButtonStyles = {
  /*
   * Primary Button
   */
  primary_Container: {
    ...CommonButtonStyle.container,
    backgroundColor: "#FFba00"
  },
  primary_Title: {
    ...CommonButtonStyle.title,
    color: "#25282b"
  },
  primary_Container_Highlighted: {
    ...CommonButtonStyle.container,
    backgroundColor: "#FFAD33"
  },

  /*
   * Secondary Button
   */
  secondary_Container: {
    ...CommonButtonStyle.container,
    backgroundColor: "white",
    borderWidth: 1,
    borderColor: "#58c3c4"
  },
  secondary_Title: {
    ...CommonButtonStyle.title,
    color: "#25282b"
  },
  secondary_Container_Highlighted: {
    ...CommonButtonStyle.container,
    backgroundColor: "white",
    borderWidth: 1,
    borderColor: "#8ad5d6"
  },

  /*
   * Big Button
   */
  big_Container: {
    padding: Spacing.XS,
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "white", //it will be covered by a gradient view
    borderWidth: 1,
    borderColor: "#E3E3E3"
  },
  big_Title: {
    ...CommonButtonStyle.title,
    color: "#333333"
  },
  big_Container_Highlighted: {
    padding: Spacing.XS,
    flexDirection: "row",
    alignItems: "center",
    // backgroundColor: "#619DFD",
    backgroundColor: SRXColor.Teal,
    borderWidth: 1,
    borderColor: "#E3E3E3"
  },
  big_Title_Highlighted: {
    ...CommonButtonStyle.title,
    color: "white"
  }
};

export { ButtonStyles };
