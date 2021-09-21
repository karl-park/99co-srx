import React, { Component } from "react";
import { View, TextInput } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import { Navigation } from "react-native-navigation";
import PropTypes from "prop-types";

import { AddressDetailService } from "../../services";
import { SuggestionList } from "../PropertySearch/Location";
import { SRXColor, IS_IOS } from "../../constants";
import { Button } from "../../components";
import { TopBarStyle, Spacing } from "../../styles";
import { ObjectUtil } from "../../utils";

class SuggestionAutoComplete extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: IS_IOS ? false : true
      }
    };
  }

  state = {
    suggestion: []
  };

  constructor(props) {
    super(props);

    this.searchBarUpdated = this.searchBarUpdated.bind(this);
  }

  searchBarCancelPressed() {
    this.searchBar.blur();
    Navigation.dismissModal(this.props.componentId);
  }

  searchBarUpdated = ({ query }) => {
    var finalQuery = query.trim();

    if (finalQuery.length >= 3) {
      AddressDetailService.getSuggestionSearchResult({
        query: finalQuery,
        source: "listingSearch"
      })
        .then(response => {
          if (!ObjectUtil.isEmpty(response)) {
            //error is ignore
            const { entries } = response;
            if (!ObjectUtil.isEmpty(entries) && Array.isArray(entries)) {
              /*
               * object in suggestion { sectionTitle: "type", sectionEntries: [...] }
               */
              var suggestion = [];

              entries.map(entry => {
                const { type } = entry;
                if (!ObjectUtil.isEmpty(type)) {
                  var addedEntry = false;

                  for (let i = 0; i < suggestion.length; i++) {
                    arrayType = suggestion[i];
                    const { sectionTitle, data } = arrayType;
                    if (sectionTitle === type) {
                      data.push(entry);
                      addedEntry = true;
                      break;
                    }
                  }

                  if (!addedEntry) {
                    var newSec = { sectionTitle: type, data: [entry] };
                    suggestion.push(newSec);
                  }
                }
              });

              this.setState({ suggestion });
            }
          }
        })
        .catch(error => {
          console.log(error);
        });
    }
  };

  onItemSelected = item => {
    this.props.onSuggestionSelected(item);
    this.searchBarCancelPressed();
  };

  renderSearchBar() {
    const { searchQuery } = this.state;

    return (
      <View style={[TopBarStyle.topBar, { height: 50 }]}>
        <TextInput
          ref={component => (this.searchBar = component)}
          style={[
            {
              flex: 1,
              borderRadius: 20,
              height: 40,
              backgroundColor: SRXColor.Purple,
              margin: 8,
              paddingHorizontal: Spacing.M,
              color: SRXColor.White
            }
          ]}
          selectionColor={SRXColor.White}
          autoCorrect={false}
          autoFocus={true}
          clearButtonMode="while-editing"
          underlineColorAndroid="transparent"
          returnKeyType={"search"}
          value={searchQuery}
          onChangeText={text => this.searchBarUpdated({ query: text })}
        />
        <Button
          style={{
            padding: 8,
            color: SRXColor.Teal,
            fontSize: 14,
            fontWeight: "600"
          }}
          onPress={() => this.searchBarCancelPressed()}
        >
          Cancel
        </Button>
      </View>
    );
  }

  renderContent() {
    const { suggestion } = this.state;
    if (!ObjectUtil.isEmpty(suggestion)) {
      if (suggestion.length > 0) {
        return (
          <SuggestionList
            style={{ flex: 1 }}
            dataArray={this.state.suggestion}
            keyExtractor={item => item.address}
            keyboardShouldPersistTaps={"handled"}
            onItemSelected={this.onItemSelected}
            onScroll={() => this.searchBar.blur()}
          />
        );
      }
    }

    return <View style={{ flex: 1, backgroundColor: SRXColor.White }} />;
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.White }}
        forceInset={{ bottom: "never" }}
      >
        <SafeAreaView style={{ flex: 1 }} forceInset={{ bottom: "never" }}>
          {this.renderSearchBar()}
          {this.renderContent()}
        </SafeAreaView>
      </SafeAreaView>
    );
  }
}

SuggestionAutoComplete.propTypes = {
  onSuggestionSelected: PropTypes.func
};

export { SuggestionAutoComplete };
