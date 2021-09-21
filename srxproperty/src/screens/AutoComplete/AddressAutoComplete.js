import React, { Component } from "react";
import { View, TextInput, TouchableHighlight, SectionList } from "react-native";
import SafeAreaView from "react-native-safe-area-view";
import { Navigation } from "react-native-navigation";
import PropTypes from "prop-types";
import { SRXColor, IS_IOS } from "../../constants";
import { Button, BodyText, Heading2 } from "../../components";
import { AddressDetailService } from "../../services";
import { TopBarStyle, Spacing } from "../../styles";
import { ObjectUtil, CommonUtil } from "../../utils";

const AutoCompletePurposes = {
  default: "default", //same as xValue, calling searchWithWalkup
  xValue: "xValue",
  Amenities: "Amenities"
};

class AddressAutoComplete extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: IS_IOS ? false : true
      }
    };
  }

  static defaultProps = {
    purpose: AutoCompletePurposes.default
  };

  state = {
    suggestionData: []
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
    if (!ObjectUtil.isEmpty(query) && typeof query === "string") {
      var finalQuery = query.trim();

      if (finalQuery.length >= 3) {
        this.searchQuery({ query });
      }
    }
  };

  searchQuery = ({ query }) => {
    //query is expected to be trimmed

    const { purpose } = this.props;

    if (purpose === AutoCompletePurposes.Amenities) {
      AddressDetailService.getSuggestionSearchResult({
        query,
        source: "buildingSearch"
      }).then(response => {
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

            this.setState(
              { suggestionData: suggestion }
              // () => {
              //   this.suggestionList.scrollToLocation({
              //     viewPosition: 0,
              //     sectionIndex: 0,
              //     itemIndex: 0,
              //     viewOffset: 48, //estimated value, change according to header
              //     animated: false
              //   });
              // }
            );
          }
        }
      });
    } else {
      //default or xValue
      AddressDetailService.searchWithWalkup({
        query: query
      })
        .then(response => {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            //do nothing
            console.error(error);
          } else {
            if (!ObjectUtil.isEmpty(response) && Array.isArray(response.data)) {
              this.setState(
                { suggestionData: [{ data: response.data }] }
                /**
                 * removing the auto scrollinng
                 * bcs for some search the result doesn't scrolled correctly
                 * e.g. 560434,
                 * the only result will be scrolled up, behide the searchbar
                 *
                 * Keeping the code, for retest after updating react native
                 */
                // () => {
                //   this.suggestionList.scrollToLocation({
                //     viewPosition: 0,
                //     sectionIndex: 0,
                //     itemIndex: 0,
                //     animated: false
                //   });
                // }
              );
            }
          }
        })
        .catch(error => {
          console.error(error);
        });
    }
  };

  onItemSelected(item) {
    this.props.onSuggestionSelected(item);
    this.searchBarCancelPressed();
  }

  sectionTitle = ({ section }) => {
    if (section.sectionTitle === "HDB_ESTATE") {
      return "Area";
    }
    return section.sectionTitle;
  };

  renderListItem = item => {
    return (
      <TouchableHighlight onPress={() => this.onItemSelected(item)}>
        <View
          style={{
            paddingLeft: Spacing.L,
            paddingRight: 8,
            minHeight: 44,
            justifyContent: "center",
            backgroundColor: "white"
          }}
        >
          <BodyText style={{ marginVertical: Spacing.S }}>
            {item.address}
          </BodyText>
        </View>
      </TouchableHighlight>
    );
  };

  renderSeparator = () => {
    return (
      <View
        style={{
          height: 1,
          backgroundColor: "#CED0CE",
          marginLeft: 15
        }}
      />
    );
  };

  renderSearchBar() {
    const { searchQuery } = this.state;

    return (
      <View style={[TopBarStyle.topBar]}>
        <TextInput
          ref={component => (this.searchBar = component)}
          style={[
            {
              flex: 1,
              height: 40,
              borderRadius: 20,
              backgroundColor: SRXColor.Purple,
              margin: Spacing.XS,
              paddingRight: Spacing.S,
              paddingLeft: 20,
              color: SRXColor.White
            }
          ]}
          selectionColor={SRXColor.White}
          placeholderTextColor={SRXColor.LightGray}
          autoCorrect={false}
          autoFocus={true}
          clearButtonMode="while-editing"
          underlineColorAndroid="transparent"
          placeholder={"Enter your address or postal code"}
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

  renderSuggestion() {
    const { purpose } = this.props;
    if (purpose === AutoCompletePurposes.Amenities) {
      return (
        <SectionList
          style={{
            flex: 1,
            backgroundColor: SRXColor.White
          }}
          ref={component => (this.suggestionList = component)}
          keyboardShouldPersistTaps={"always"}
          stickySectionHeadersEnabled={false}
          sections={this.state.suggestionData}
          renderItem={({ index, item, section }) => (
            <TouchableHighlight
              key={index}
              onPress={() => this.onItemSelected(item)}
            >
              <View
                style={{
                  paddingLeft: Spacing.M,
                  paddingRight: 8,
                  minHeight: 44,
                  justifyContent: "center",
                  backgroundColor: SRXColor.White
                }}
              >
                <BodyText style={{ marginVertical: Spacing.S }}>
                  {item.displayText}
                </BodyText>
              </View>
            </TouchableHighlight>
          )}
          renderSectionHeader={({ section }) => (
            <View
              style={{
                backgroundColor: SRXColor.White,
                paddingLeft: Spacing.M,
                paddingRight: Spacing.XS,
                paddingTop: Spacing.L,
                paddingBottom: Spacing.XS
              }}
            >
              <Heading2>{this.sectionTitle({ section })}</Heading2>
            </View>
          )}
          // onScroll={() => this.searchBar.blur()}
          onScrollToIndexFailed={err => {
            console.log(err);
          }}
        />
      );
    } else {
      return (
        <SectionList
          style={{
            flex: 1,
            backgroundColor: SRXColor.White
          }}
          sections={this.state.suggestionData}
          keyExtractor={item => item.address}
          keyboardShouldPersistTaps={"always"}
          renderItem={({ item }) => this.renderListItem(item)}
          renderSectionHeader={() => <View style={{ height: 8 }} />}
          // onScroll={() => this.searchBar.blur()}
          ref={component => (this.suggestionList = component)}
          onScrollToIndexFailed={err => {
            console.log(err);
          }}
        />
      );
    }
  }

  render() {
    return (
      <SafeAreaView
        style={{
          flex: 1,
          backgroundColor: SRXColor.White
        }}
      >
        {this.renderSearchBar()}
        {this.renderSuggestion()}
      </SafeAreaView>
    );
  }
}

AddressAutoComplete.propTypes = {
  /**
   * callback function to passback selected address object
   * onSuggestionSelected(dict)
   */
  onSuggestionSelected: PropTypes.func,

  /**
   *
   */
  purpose: PropTypes.oneOf(Object.keys(AutoCompletePurposes))
};

export { AddressAutoComplete, AutoCompletePurposes };
