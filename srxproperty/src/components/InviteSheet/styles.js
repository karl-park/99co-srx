import { SRXColor } from "../../constants";
import { Spacing } from "../../styles";

export default {
  overlay: {
    flex: 1,
    backgroundColor: "rgba(0, 0, 0, 0.3)",
    justifyContent: "flex-end"
  },
  cancelButtonText: {
    color: "#007AFF",
    fontSize: 23,
    fontWeight: "bold",
    textAlign: "center"
  },
  buttonText: {
    color: SRXColor.Black,
    fontSize: 23,
    textAlign: "center"
  },
  cancelButtonBox: {
    height: 66,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255, 255, 255, 1.0)"
  },
  buttonBox: {
    height: 65,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(255, 255, 255, 1.0)",
    paddingHorizontal: Spacing.S
  },
  roundedBox: {
    backgroundColor: "transparent",
    borderRadius: 8,
    overflow: "hidden"
  },
  sheetContainer: {
    margin: 8
  }
};
