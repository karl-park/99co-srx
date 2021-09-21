import {SRXColor} from '../../../constants';
import {Spacing} from '../../../styles';

const Styles = {
  whiteBackgroundContainer: {
    flex: 1,
    // flexDirection: "row",
    // alignItems: "center",
    padding: Spacing.M,
    backgroundColor: SRXColor.White,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  accordionBackgroundContainer: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    padding: Spacing.M,
    backgroundColor: SRXColor.White,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  eachOptionContainer: {
    flex: 1,
    padding: Spacing.M,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  roomListsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    flex: 1,
  },
  roomsContainer: {
    flex: 1,
    borderWidth: 1,
    borderColor: SRXColor.LightGray,
    paddingTop: Spacing.XS,
    paddingBottom: Spacing.XS,
    paddingLeft: Spacing.XS,
    paddingRight: Spacing.XS,
    marginTop: 5,
    justifyContent: 'center',
  },
  pickerContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },

  //Text Style
  bodyTextExtraStyle: {
    lineHeight: 22,
    fontWeight: '600',
  },
  itemContainerStyle: {
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    minHeight: 44,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: SRXColor.White,
  },

  //MRT List Container
  mrtHeaderStyle: {
    width: 25,
    height: 25,
    marginRight: 5,
  },

  //Travel time style
  travelTimeContainer: {
    flex: 1,
    padding: 15,
    justifyContent: 'center',
    backgroundColor: '#FFFFFF',
  },
  locationContainer: {
    flex: 1,
    padding: 15,
    justifyContent: 'center',
    backgroundColor: SRXColor.LightGray,
  },
  travelTimeHeaderStyle: {
    marginVertical: 8,
    alignSelf: 'center',
    fontWeight: '500',
  },
  sliderThumbStyle: {
    width: 20,
    height: 20,
    borderColor: SRXColor.Black,
    borderWidth: 1,
  },
  locationHeaderStyle: {
    marginVertical: 8,
    fontWeight: '500',
  },

  applyFilterContainerStyle: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 75,
    backgroundColor: SRXColor.White,
    paddingLeft: Spacing.XL,
    paddingRight: Spacing.XL,
    borderTopWidth: 1,
    borderTopColor: '#e0e0e0',
  },
  moreOptionsContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 100,
    marginTop: Spacing.L,
  },

  headerContainerStyle: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    padding: Spacing.M,
    justifyContent: 'center',
    backgroundColor: SRXColor.White,
  },
  newTextContainer: {
    borderRadius: 3,
    backgroundColor: '#FE7881',
    paddingHorizontal: Spacing.XS,
    justifyContent: 'center',
    marginVertical: Spacing.XS / 2,
  },
};

export {Styles};
