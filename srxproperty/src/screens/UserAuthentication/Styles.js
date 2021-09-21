import {SRXColor} from '../../constants';
import {Spacing} from '../../styles';
import {Dimensions} from 'react-native';

var {width} = Dimensions.get('window');

const Styles = {
  //UserAuth Dialog Style
  userAuthOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  userAuthDialigMainContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  userAuthDialogSubContainer: {
    width: '90%',
    opacity: 1,
    borderRadius: 10,
    backgroundColor: SRXColor.White,
  },
  closeBtnContainer: {
    alignItems: 'flex-end',
    marginTop: Spacing.XS,
    marginRight: Spacing.XS,
  },
  userAuthBtnStyle: {
    width: 260,
    height: 45,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    borderColor: SRXColor.TextLink,
    borderRadius: 22.5,
    borderWidth: 1,
    marginTop: Spacing.M,
  },
  userAuthBtnTextStyle: {
    color: 'black',
    marginLeft: Spacing.XS,
  },
  sgHomeAuthContainer: {
    //alignItems: "center",
    padding: Spacing.M,
    borderBottomLeftRadius: 10,
    borderBottomRightRadius: 10,
    backgroundColor: SRXColor.AccordionBackground,
  },
  srxImageContainer: {
    alignItems: 'center',
    marginVertical: Spacing.XS,
    paddingHorizontal: Spacing.S,
    flex: 2,
  },
  halfSeparatorStyle: {
    width: width * 0.33,
    height: 1,
    backgroundColor: '#e0e0e0',
  },

  //Account Details Form
  accountDetailsContainer: {
    flex: 1,
    padding: 16,
    marginTop: Spacing.S,
    backgroundColor: SRXColor.White,
  },
  modalTitleStyle: {
    marginTop: Spacing.M,
    lineHeight: 28,
  },

  //Input Format
  inputContainer: {
    paddingTop: Spacing.M,
    paddingBottom: Spacing.S,
  },
  inputBoxStyle: {
    borderWidth: 1,
    borderColor: '#8DABC4',
    height: 40,
    backgroundColor: '#FAFBFC',
    paddingRight: 12,
    marginTop: 8,
    flex: 2,
    shadowColor: 'rgba(110, 129, 154, 0.32)',
    shadowOffset: {width: 1, height: 2},
    shadowOpacity: 0.5,
  },

  //Action Button Style
  actionBtnStyle: {
    width: 134,
    height: 40,
    marginTop: 24,
    marginBottom: 5,
    borderRadius: 10,
    backgroundColor: SRXColor.Orange,
  },
  actionBtnTextStyle: {
    fontSize: 16,
    lineHeight: 40,
    textAlign: 'center',
    fontWeight: '600',
    color: 'white',
  },

  //Separator Style
  separatorContainerStyle: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    flexDirection: 'row',
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
  },
  separatorLineStyle: {
    backgroundColor: SRXColor.Gray,
    height: 0.3,
    flex: 1,
    marginLeft: Spacing.M,
    marginRight: Spacing.M,
  },

  //Facebook Sign up container
  facebookSignUpContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    flex: 1,
  },
  facebookIconStyle: {
    width: 45,
    height: 45,
  },
  facebookSignUpTextStyle: {
    textAlign: 'center',
    color: SRXColor.Teal,
    marginTop: Spacing.XS / 2,
  },

  //PDPA style
  pdpaContainerStyle: {
    marginTop: Spacing.XS,
  },

  pdpaCheckStyle: {
    width: 25,
    height: 25,
  },
  errorMessage: {
    marginTop: Spacing.XS,
    fontSize: 14,
    color: '#FF151F',
    lineHeight: 20,
  },
  signUpTextContainer: {
    marginTop: 24,
    marginBottom: 16,
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  registrationAgreementStyle: {
    marginLeft: Spacing.XS,
  },

  //SignInPage
  continer: {
    borderRadius: Spacing.XS,
    //Shadow for iOS
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {height: 1, width: 1},
    shadowOpacity: 0.32,
    shadowRadius: 1,
    marginBottom: Spacing.S,
  },
  itemContainer: {
    overflow: 'hidden',
    //Padding
    padding: Spacing.S,
    //Border
    borderRadius: Spacing.XS,
    borderWidth: 1,
    borderColor: '#e0e0e0',
    //background
    backgroundColor: SRXColor.White,
  },
};

export {Styles};
