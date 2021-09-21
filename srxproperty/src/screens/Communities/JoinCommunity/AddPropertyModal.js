import React, {Component} from 'react';
import {View, TouchableOpacity, ScrollView, Image} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';

import {SRXColor} from '../../../constants';
import {
  FeatherIcon,
  Heading2,
  SmallBodyText,
  Button,
} from '../../../components';
import {Spacing} from '../../../styles';
import {Communities_Welcome} from '../../../assets';
import PropTypes from 'prop-types';

class AddPropertyModal extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: isIOS ? false : true,
      },
    };
  }

  state = {
    pressedAddProperty: false,
  };

  constructor(props) {
    super(props);
  }

  componentWillUnmount() {
    const {pressedAddProperty} = this.state;
    if (pressedAddProperty) {
      this.onAddPropertyPressed();
    }
  }

  onAddPropertyPressed = () => {
    const {onAddPropertyPressed} = this.props;
    if (onAddPropertyPressed) {
      onAddPropertyPressed();
    }
  };

  onCloseModal = () => {
    return Navigation.dismissModal(this.props.componentId);
  };

  onPressAddProperty = () => {
    this.setState({pressedAddProperty: true}, () => this.onCloseModal());
  };

  renderAddPropertyModal() {
    return (
      <View
        style={{
          paddingHorizontal: Spacing.M,
          marginTop: Spacing.S,
        }}>
        {this.renderCloseButton()}
        {this.renderWelcomeText()}
        {this.renderAddProperty()}
        {this.renderImage()}
      </View>
    );
  }

  renderCloseButton() {
    return (
      <TouchableOpacity onPress={() => this.onCloseModal()}>
        <FeatherIcon name="x" size={25} color={'black'} />
      </TouchableOpacity>
    );
  }

  renderWelcomeText() {
    return (
      <View style={{marginVertical: Spacing.L, alignItems: 'center'}}>
        <Heading2 style={{color: SRXColor.Purple}}>
          Welcome to SRX Community
        </Heading2>
        <SmallBodyText style={{marginTop: Spacing.M, marginBottom: Spacing.S}}>
          Your community is waiting for you
        </SmallBodyText>
        <SmallBodyText>Add your residence to join them!</SmallBodyText>
      </View>
    );
  }

  renderAddProperty() {
    return (
      <View style={Styles.addPropertyContainer}>
        <Heading2>Your mySG Home properties</Heading2>
        <Button
          buttonStyle={{
            flex: 1,
            alignItems: 'center',
            justifyContent: 'center',
            marginTop: Spacing.L,
            marginBottom: Spacing.M,
          }}
          textStyle={{
            fontSize: 14,
            color: SRXColor.Teal,
            paddingRight: Spacing.XS / 2,
          }}
          leftView={<FeatherIcon name="plus" size={20} color={'#8DABC4'} />}
          onPress={() => this.onPressAddProperty()}>
          Add Property
        </Button>
      </View>
    );
  }

  renderImage() {
    return (
      <Image
        style={{width: '100%', height: 400}}
        source={Communities_Welcome}
        resizeMode={'contain'}
      />
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <ScrollView>{this.renderAddPropertyModal()}</ScrollView>
      </SafeAreaView>
    );
  }
}

const Styles = {
  addPropertyContainer: {
    borderRadius: Spacing.XS,
    borderWidth: 1,
    borderColor: '#e0e0e0',
    padding: Spacing.M,
  },
};

AddPropertyModal.propTypes = {
  onAddPropertyPressed: PropTypes.func,
};

export {AddPropertyModal};
