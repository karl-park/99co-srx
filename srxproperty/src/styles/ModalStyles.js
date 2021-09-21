import {StyleSheet} from 'react-native';
import {SRXColor} from '../constants';
import {Spacing} from '../styles';

const ModalStyles = StyleSheet.create({
  overlay: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.3)',
  },
  mainContainer: {
    flex: 1,
    backgroundColor: SRXColor.Transparent,
    overflow: 'hidden',
    alignItems: 'center',
    justifyContent: 'center',
  },
  subContainer: {
    width: '90%',
    opacity: 1,
    borderRadius: 10,
    padding: Spacing.M,
    backgroundColor: SRXColor.White,
  },
});

export {ModalStyles};
