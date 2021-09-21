import { SRXColor } from "../../../constants";
import { Spacing } from "../../../styles";

const Styles = {
  //Search Options Each Item Style
  itemContainerStyle: {
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    minHeight: 45,
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center"
  },

  //MRT List Container
  mrtImageStyle: {
    width: 22,
    height: 22,
    marginRight: Spacing.S
  },

  //Travel time style
  travelTimeContainer: {
    flex: 1,
    padding: 15,
    justifyContent: "center",
    backgroundColor: "#FFFFFF"
  },
  locationContainer: {
    flex: 1,
    padding: 15,
    justifyContent: "center",
    backgroundColor: SRXColor.LightGray
  },
  travelTimeHeaderStyle: {
    marginVertical: 8,
    alignSelf: "center",
    fontWeight: "500"
  },
  sliderThumbStyle: {
    width: 20,
    height: 20,
    borderColor: SRXColor.Black,
    borderWidth: 1
  },
  locationHeaderStyle: {
    marginVertical: 8,
    fontWeight: "500"
  }
};

export { Styles };
