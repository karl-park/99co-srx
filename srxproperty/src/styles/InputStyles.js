import { Platform } from "react-native";
import { Spacing } from "./Spacing";
import { SRXColor } from "../constants";

const isIOS = Platform.OS === "ios";

const inputHeight = isIOS ? 20 : 22.5;

const InputStyles = {
  container: {
    backgroundColor: "#FAFBFC",
    borderRadius: inputHeight
  },
  containerBorder: {
    borderWidth: 1,
    borderColor: SRXColor.SubText,
  },
  containerBorder_Focus: {
    borderWidth: 1,
    borderColor: SRXColor.Teal
  },
  containerBorder_Error: {
    borderWidth: 1,
    borderColor: "#FF151F"
  },
  containerBorder_SystemPopulated: {
    borderWidth: 0,
    borderColor: "#F5F5F5"
  },
  containerPadding: {
    paddingHorizontal: Spacing.S
  },
  containerAlign: {
    flexDirection: "row",
    justifyContent: "flex-start",
    alignItems: "center"
  },
  text: {
    flex: 1,
    fontSize: 16,
    color: "#333333"
  },
  singleLineTextHeight: {
    height: isIOS ? 40 : 45
  },
  multilineTextHeight: {
    height: 100
  },
  errorMessage: {
    marginTop: Spacing.XS,
    fontSize: 14,
    color: "#FF151F",
    lineHeight: 20
  },
  label: {
    fontSize: 16,
    color: "#333333",
    fontWeight: "600",
    lineHeight: 24,
    marginBottom: Spacing.XS
  }
};

export { InputStyles };
