import { Spacing } from "./Spacing";
import { SRXColor } from "../constants";

const CheckboxStyles = {
  checkStyle: {
    width: 18,
    height: 18,
    backgroundColor: SRXColor.TextLink,
    borderWidth: 1,
    borderRadius: 2,
    borderColor: SRXColor.TextLink,
    justifyContent: "center",
    alignItems: "center"
  },
  unCheckStyle: {
    width: 18,
    height: 18,
    backgroundColor: "#FAFBFC",
    borderWidth: 1,
    borderRadius: 2,
    padding: 3,
    borderColor: "#8DABC4"
    // shadowColor: "rgba(110,129,154,0.32)",
    // shadowRadius: 2,
    // shadowOpacity: 0.8,
  }
};

export { CheckboxStyles };
