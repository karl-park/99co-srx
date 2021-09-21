import React, {Component} from 'react';
import {
  Alert,
  Linking,
  Platform,
  View,
  ScrollView,
  TouchableHighlight,
  FlatList,
} from 'react-native';
import {connect} from 'react-redux';
import {updateDomainUrl} from '../../actions';
import {InputStyles} from '../../styles/InputStyles';
import {
  SRXColor,
  AppConstant,
  ConstantList,
  AlertMessage,
} from '../../constants';
import {Navigation} from 'react-native-navigation';
import SafeAreaView from 'react-native-safe-area-view';
import {ChatDatabaseUtil, DebugUtil} from '../../utils';
import {Spacing} from '../../styles';

import {Button, FeatherIcon, Text, TextInput, BodyText} from '../../components';

class SwitchServerMain extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: 'Switch Server',
          alignment: 'center',
        },
      },
    };
  }

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
  }

  state = {
    serverList: '',
    constantList: ConstantList,
    urlInput: 'https://',
  };

  setupTopBar() {
    FeatherIcon.getImageSource('x', 25, 'black').then(icon_close => {
      Navigation.mergeOptions(this.props.componentId, {
        topBar: {
          leftButtons: [
            {
              id: 'setting_close',
              icon: icon_close,
            },
          ],
        },
      });
    });
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'setting_close') {
      Navigation.dismissModal(this.props.componentId);
    }
  }

  componentDidMount() {
    this.setupTopBar();
  }

  onEnterURL(url) {
    const {updateDomainUrl} = this.props;
    ChatDatabaseUtil.deleteAll();
    updateDomainUrl({newDomainUrl: url});

    Alert.alert(AlertMessage.SuccessMessageTitle, 'Domain URL changed', [
      {
        text: 'OK',
        onPress: () => Navigation.dismissModal(this.props.componentId),
      },
    ]);
  }

  renderServerList({item, index}) {
    return (
      <TouchableHighlight onPress={() => this.onEnterURL(item)}>
        <View style={{padding: 20, borderWidth: 1}}>
          <BodyText style={{textAlign: 'center'}}>
            {index == 0 ? 'Default Server: ' : null}
            {item}
          </BodyText>
        </View>
      </TouchableHighlight>
    );
  }

  render() {
    const {urlInput, constantList} = this.state;
    return (
      <SafeAreaView
        style={{flex: 1, backgroundColor: SRXColor.DarkBlue}}
        forceInset={{bottom: 'never'}}>
        <SafeAreaView
          style={{flex: 1, backgroundColor: SRXColor.LightGray, padding: 10}}
          forceInset={{bottom: 'never'}}>
          <View style={{flexDirection: 'row'}}>
            <View style={[{flex: 1}, InputStyles.container]}>
              <TextInput
                defaultValue={urlInput}
                onChangeText={newText => this.setState({urlInput: newText})}
              />
            </View>
            <Button
              style={{
                padding: 10,
                color: SRXColor.Teal,
                fontSize: 14,
                fontWeight: '600',
              }}
              onPress={() => this.onEnterURL(urlInput)}>
              Select
            </Button>
          </View>
          <View style={{paddingTop: Spacing.S}}>
            <FlatList
              data={constantList}
              extraData={this.state}
              keyExtractor={item => item.key}
              renderItem={({item, index}) =>
                this.renderServerList({item, index})
              }
            />
          </View>
        </SafeAreaView>
      </SafeAreaView>
    );
  }
}

export default connect(
  null,
  {updateDomainUrl},
)(SwitchServerMain);
