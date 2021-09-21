import { SRXColor } from "../../constants";
import { Spacing } from "../../styles";

const Styles = {
  filterAndSortContainer: {
    flexDirection: "row",
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    minHeight: 50,
    backgroundColor: SRXColor.White
  },
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

  // New Launches Items
  //1) New Launches Card Items
  containerStyle: {
    backgroundColor: SRXColor.White,
    overflow: "hidden",
    width: 136 + Spacing.XS * 2,
    shadowColor: "rgb(110,129,154)",
    shadowOffset: { width: 1, height: 2 },
    shadowOpacity: 0.32,
    shadowRadius: 1
  },
  containerBorder: {
    borderRadius: 5,
    borderWidth: 1,
    borderColor: "#e0e0e0"
  },
  imageStyle: {
    borderRadius: 5,
    width: 136,
    height: 88
  },

  //2) New Launches Large Item
  largeItemSubContainerStyle: {
    borderWidth: 1,
    borderColor: "#E5E5E5",
    backgroundColor: SRXColor.AccordionBackground,
    padding: Spacing.S,

    shadowRadius: 4,
    shadowOpacity: 1,
    shadowOffset: { width: 1, height: 2 },
    shadowColor: "rgba(110, 129, 154, 0.32)"
  },
  largeImageStyle: {
    width: "100%",
    height: 163
  },
  largeEachRowContainerStyle: {
    flex: 1,
    flexDirection: "row"
  },

  //3) New Launches Small Item
  droneContainer: {
    borderRadius: 15,
    borderWidth: 1,
    borderColor: "rgba(0,0,0,0.5)",
    backgroundColor: "rgba(0,0,0,0.5)",
    width: 30,
    height: 30,
    alignItems: "center",
    justifyContent: "center"
  }
};

export { Styles };
