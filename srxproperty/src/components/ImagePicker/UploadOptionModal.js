import React, {Component} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  TouchableHighlight,
  TouchableWithoutFeedback,
  Modal,
  FlatList,
} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import PropTypes from 'prop-types';
import {Separator} from '../../components';
import {SRXColor} from '../../constants';
import {Spacing} from '../../styles';
import {Navigation} from 'react-native-navigation';

class UploadOptionModal extends Component {
  static options(passProps) {
    return {
      layout: {
        backgroundColor: SRXColor.Transparent,
      },
    };
  }

  options = [
    {
      key: 'Camera',
      title: 'Take Photo',
    },
    {
      key: 'Upload',
      title: 'Upload Photo',
    },
    {
      key: 'Cancel',
      title: 'Cancel',
    },
  ];

  onValueSelected(item, index) {
    const {onCameraSelect, onUploadSelected} = this.props;
    if (item.key === 'Camera') {
      Navigation.dismissModal(this.props.componentId).then(() => {
        onCameraSelect();
      });
    } else if (item.key === 'Upload') {
      Navigation.dismissModal(this.props.componentId).then(() => {
        onUploadSelected();
      });
    } else {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  renderDialogItem = ({item, index}) => {
    console.log('rendering renderDialogItem');
    return (
      <TouchableHighlight
        key={index}
        underlayColor={'#DDDDDD'}
        onPress={() => this.onValueSelected(item, index)}>
        <View
          style={{
            height: 40,
            justifyContent: 'center',
            paddingLeft: Spacing.S,
          }}>
          <Text style={{fontSize: 16}}>{item.title}</Text>
        </View>
      </TouchableHighlight>
    );
  };

  renderDialog() {
    console.log('rendering renderDialog');
    return (
      <View
        style={{
          marginHorizontal: 15,
          marginTop: 20,
          marginBottom: 20,
          backgroundColor: 'white',
          paddingLeft: 15,
          paddingTop: 10,
          shadowColor: 'black',
          shadowOffset: {width: 3, height: 3},
          shadowOpacity: 0.5,
          shadowRadius: 3,
        }}>
        <View style={{justifyContent: 'center', height: 40}}>
          <Text style={{fontSize: 16, fontWeight: 'bold'}}>
            Upload Image/Photo
          </Text>
        </View>

        <FlatList
          data={this.options}
          renderItem={this.renderDialogItem}
          style={{paddingRight: 15, paddingBottom: 15}}
          ItemSeparatorComponent={() => <Separator />}
        />
      </View>
    );
  }

  render() {
    console.log('rendering UploadOptionModal');
    return (
      <View style={{flex: 1, backgroundColor: 'rgba(0, 0, 0, 0.3)'}}>
        <SafeAreaView style={{flex: 1, justifyContent: 'center'}}>
          {this.renderDialog()}
        </SafeAreaView>
      </View>
    );
  }
}

export {UploadOptionModal};
