import React, { Component } from "react";
import {
  Alert,
  Linking,
  Platform,
  View,
  Text,
  TextInput,
  ScrollView,
  PermissionsAndroid,
  Image
} from "react-native";
import { Navigation } from "react-native-navigation";
import SafeAreaView from "react-native-safe-area-view";
import PropTypes from "prop-types";
import {
  Button,
  Separator,
  FeatherIcon,
  TouchableHighlight,
  BodyText
} from "../../../components";
import { SRXColor } from "../../../constants";
import { AddressDetailService } from "../../../services";
import { TopBarStyle, Spacing } from "../../../styles";
import { ObjectUtil, UserUtil, PermissionUtil } from "../../../utils";
import { SuggestionList } from "./SuggestionList";
import { LocationSearchOptions } from "./LocationSearchOptions";
import { PropertyTypeValueSet } from "../Constants";
import {
  PropertySearch_Checked_Radio_Button,
  PropertySearch_Unchecked_Radio_Button
} from "../../../assets";
import Geolocation from '@react-native-community/geolocation';

const isIOS = Platform.OS === "ios";

const LoadingState = {
  Normal: "normal", //default, not update
  Loading: "loading", //sending update request
  Loaded: "loaded"
};

class PropertySearchLocation extends Component {
  static options(passProps) {
    return {
      topBar: {
        visible: false,
        drawBehind: isIOS ? false : true
      }
    };
  }

  static propTypes = {
    onLocationSelected: PropTypes.func,
    /**
     * {
     *   displayText,
     *   searchText: null,
     *   selectedDistrictIds: null,
     *   selectedHdbTownIds: null,
     * }
     * //same as object passing back
     */
    location: PropTypes.object
  };

  state = {
    searchQuery: "",
    suggestion: [],
    recentSearches: [],
    locationOptions: [
      //to hide options, remove from locationOptions
      LocationSearchOptions.currentLocation,
      LocationSearchOptions.mrt,
      LocationSearchOptions.district,
      LocationSearchOptions.hdb_estate,
      LocationSearchOptions.school,
      // LocationSearchOptions.travel_time,
      LocationSearchOptions.recent_search
    ],
    loadingState: LoadingState.Normal,
    selectedBuyRadioButton: true
  };

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
  }

  componentDidMount() {
    const { searchOptions } = this.props;
    UserUtil.retrieveRecentPropertySearch().then(result => {
      this.setState({ recentSearches: result });
    });
    if (searchOptions) {
      if (searchOptions.type == "S") {
        this.setState({ selectedBuyRadioButton: true });
      } else {
        this.setState({ selectedBuyRadioButton: false });
      }
    }
  }

  componentDidAppear(){
    this.searchBar.focus();
  }

  componentWillUnmount() {
    this.searchBar.blur();
  }

  searchBarUpdated({ text, isFocused }) {
    let trimmedText = "";
    if (text) {
      trimmedText = text.trim();
    }

    if (!isFocused) {
      //onEndEditing
      //clean up text
      this.setState({ searchQuery: trimmedText });
    } else {
      //onchangeText

      //do auto complete here
      if (!ObjectUtil.isEmpty(trimmedText) && trimmedText.length > 2) {
        this.setState({ searchQuery: text });
        this.loadSuggestion(trimmedText);
      } else {
        this.setState({ searchQuery: text, suggestion: [] });
      }
    }
  }

  searchBarCancelPressed() {
    this.searchBar.blur();
    Navigation.dismissModal(this.props.componentId);
  }

  onSearchBarReturnKeyPressed() {
    this.searchBar.blur();
    const { searchQuery } = this.state;
    let trimmedText = "";

    if (searchQuery) {
      trimmedText = searchQuery.trim();
    }

    if (ObjectUtil.isEmpty(trimmedText)) {
      this.passBackSelectedLocation({
        addressData: {
          displayText: "Everywhere in Singapore",
          searchText: ""
        },
        updateRecentSearches: true
      });
    } else {
      this.passBackSelectedLocation({
        addressData: {
          displayText: trimmedText,
          searchText: trimmedText
        },
        updateRecentSearches: true
      });
    }
  }

  loadSuggestion(searchQuery) {
    AddressDetailService.getSuggestionSearchResult({
      query: searchQuery,
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

            this.setState({ suggestion: suggestion });
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  onSuggestionSelected = item => {
    console.log(item);
    const { displayText, id, type, latitude, longitude } = item;

    const returningItem = {
      displayText,
      searchText: null,
      selectedAmenitiesIds: null,
      selectedDistrictIds: null,
      selectedHdbTownIds: null,
      latitude: 0,
      longitude: 0,
      locationType: LocationSearchOptions.suggestion,
      suggestionEntryType: "None"
    };

    const hasEntryType = !ObjectUtil.isEmpty(type);

    if (hasEntryType && type.toUpperCase() === "DISTRICT") {
      returningItem.selectedDistrictIds = id;
    } else if (hasEntryType && type.toUpperCase() === "HDB_ESTATE") {
      returningItem.selectedHdbTownIds = id;
    } else if (id && id > 0) {
      returningItem.selectedAmenitiesIds = id;
    } else {
      returningItem.searchText = displayText;
    }

    returningItem.suggestionEntryType = type;
    //by agent request requirement, need to add lat and long
    returningItem.latitude = latitude;
    returningItem.longitude = longitude;

    //To be same with Website, when select Condo or Landed,
    //need to change cdResearchSubTypes to Condo or Landed
    if (type === "CONDO") {
      returningItem.cdResearchSubTypes = Array.from(
        PropertyTypeValueSet.condo
      ).join(",");
    } else if (type === "LANDED") {
      returningItem.cdResearchSubTypes = Array.from(
        PropertyTypeValueSet.landed
      ).join(",");
    }
    console.log("returningItem.......");
    console.log(returningItem);
    this.passBackSelectedLocation({
      addressData: returningItem,
      updateRecentSearches: true
    });
  };

  locationOptionSelected = item => {
    // console.log(item);

    if (item === LocationSearchOptions.currentLocation) {
      //location search
      this.onSelectCurrentLocation();
    } else if (item === LocationSearchOptions.mrt) {
      //direct to mrt view
      this.onSelectMRTOption();
    } else if (item === LocationSearchOptions.district) {
      this.onSelectDistrictOption();
    } else if (item === LocationSearchOptions.hdb_estate) {
      this.onSelectHDBEstates();
    } else if (item === LocationSearchOptions.school) {
      this.onSelectSchoolOption();
    }
    // else if (item === LocationSearchOptions.travel_time) {
    //   this.onSelectTravelTimeOption();
    // }
    //... continue
  };

  onSelectCurrentLocation = () => {
    if (isIOS) {
      //to enable or disable current location options
      this.setState({ loadingState: LoadingState.Loading }, () => {
        this.getCurrentLocation();
      });
    } else {
      this.requestAndroidAccessFineLocation();
    }
  };

  requestAndroidAccessFineLocation = () => {
    PermissionUtil.requestAndroidAccessFineLocation().then(granted => {
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        //to enable or disable current location options
        this.setState({ loadingState: LoadingState.Loading }, () => {
          this.getCurrentLocation();
        });
      }
    });
  };

  getCurrentLocation = () => {
    Geolocation.getCurrentPosition(
      position => {
        const latLongCoord = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        };
        this.loadCurrentLocationAddress(latLongCoord);
      },
      error => {
        //error = > back to normal state
        this.setState({ loadingState: LoadingState.Normal });

        console.log(error);
        if (
          error.message &&
          error.message === "User denied access to location services."
        ) {
          if (Platform.OS === "ios") {
            this.directToiOSSettings();
          }
        }
      },
      {
        enableHighAccuracy: isIOS ? true : false,
        timeout: 20000,
        maximumAge: 5000
      }
      //show error with message: unable to retrieve location
      //if possible, detect if permission given, and send to settings if permission denied
    );
  };

  loadCurrentLocationAddress({ latitude, longitude }) {
    AddressDetailService.findPostalCodeForCurrentLocation({
      latitude,
      longitude
    })
      .then(response => {
        //Loaded state
        this.setState({ loadingState: LoadingState.Loaded });

        if (!ObjectUtil.isEmpty(response)) {
          console.log(response);
          const { po, postalCode } = response;

          const returningItem = {
            displayText: null,
            searchText: null,
            locationType: LocationSearchOptions.currentLocation
          };
          if (!ObjectUtil.isEmpty(po)) {
            returningItem.displayText = po.address;
            returningItem.searchText = po.address;
          }
          //use postal code if possible
          if (!ObjectUtil.isEmpty(postalCode)) {
            returningItem.searchText = postalCode;
            returningItem.suggestionEntryType = "POSTAL";
          }

          this.passBackSelectedLocation({
            addressData: returningItem,
            updateRecentSearches: true
          });
        }
      })
      .catch(error => {
        //Error, back to normal state
        this.setState({ loadingState: LoadingState.Normal });
        console.log(error);
      });
  }

  directToiOSSettings = () => {
    const url = "app-settings:";
    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log("Can't handle url: " + url);
        } else {
          //show alert to ask user to take action
          Alert.alert(
            "Permission Denied",
            "This function required to access your location.\nTo allow, please visit Settings App, and allow 'Location' for SRX Property.",
            [
              {
                text: "Cancel",
                onPress: () => console.log("Cancel Pressed"),
                style: "cancel"
              },
              {
                text: "Settings",
                onPress: () => Linking.openURL(url)
              }
            ],
            { cancelable: false }
          );
        }
      })
      .catch(err => console.error("An error occurred", err));
  };

  //Redirect to Search Options
  //MRT
  onSelectMRTOption = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.MRTSearchOption",
        passProps: {
          onLocationSelected: this.passBackSelectedLocation,
          location: this.props.location
        }
      }
    });
  };

  onSelectDistrictOption = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.DistrictsOption",
        passProps: { onLocationSelected: this.passBackSelectedLocation }
      }
    });
  };

  onSelectHDBEstates = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.HDBEstatesOption",
        passProps: {
          onLocationSelected: this.passBackSelectedLocation,
          location: this.props.location
        }
      }
    });
  };

  onSelectSchoolOption = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.SchoolOption",
        passProps: {
          onLocationSelected: this.passBackSelectedLocation,
          location: this.props.location
        }
      }
    });
  };

  onSelectTravelTimeOption = () => {
    const { onLocationSelected } = this.props;
    Navigation.push(this.props.componentId, {
      component: {
        name: "PropertySearchStack.TravelTimeOption",
        passProps: { onLocationSelected: onLocationSelected }
      }
    });
  };

  passBackSelectedLocation = ({ addressData, updateRecentSearches }) => {
    /*
    addressData in format of
    {
      displayText,
      searchText: null,
      selectedDistrictIds: null,
      selectedHdbTownIds: null,
    }
    */
    const { searchOptions, onUpdateSearchOptions } = this.props;
    const { selectedBuyRadioButton } = this.state;
    const data = {
      displayText: null,
      searchText: null,
      selectedAmenitiesIds: null,
      selectedDistrictIds: null,
      selectedHdbTownIds: null,
      locationType: null,
      suggestionEntryType: null, //type of the auto complete result items
      //set everything to null to clear all the values

      ...addressData
    };

    this.searchBar.blur();
    if (updateRecentSearches) {
      UserUtil.updateRecentPropertySearch(data);
    }

    let type = "S";
    if (selectedBuyRadioButton === false) {
      type = "R";
    }

    var newSearchOptions = {
      ...searchOptions,
      type
    };

    if (onUpdateSearchOptions) {
      onUpdateSearchOptions(newSearchOptions);
    }

    Navigation.dismissModal(this.props.componentId).then(onFulfilled => {
      if (this.props.onLocationSelected)
        this.props.onLocationSelected(data, type);
    });
  };

  onPressType = type => {
    this.setState({
      selectedBuyRadioButton: type == "S" ? true : false
    });
  };

  /*
   *  Screen Rendering
   */

  renderBuyRentRadioButton() {
    const { selectedBuyRadioButton } = this.state;
    return (
      <View
        style={{
          flexDirection: "row",
          paddingHorizontal: Spacing.M,
          marginTop: Spacing.M,
          marginBottom: Spacing.S
        }}
      >
        <Button
          buttonStyle={{ marginRight: Spacing.M, alignItems: "center" }}
          leftView={
            selectedBuyRadioButton === true ? (
              <Image
                source={PropertySearch_Checked_Radio_Button}
                style={{ height: 25, width: 25 }}
                resizeMode={"contain"}
              />
            ) : (
              <Image
                source={PropertySearch_Unchecked_Radio_Button}
                style={{ height: 25, width: 25 }}
                resizeMode={"contain"}
              />
            )
          }
          rightView={
            <BodyText style={{ marginLeft: Spacing.XS / 2 }}>Buy</BodyText>
          }
          onPress={() => this.onPressType("S")}
        />

        <Button
          buttonStyle={{ marginRight: Spacing.M, alignItems: "center" }}
          leftView={
            selectedBuyRadioButton === false ? (
              <Image
                source={PropertySearch_Checked_Radio_Button}
                style={{ height: 25, width: 25 }}
                resizeMode={"contain"}
              />
            ) : (
              <Image
                source={PropertySearch_Unchecked_Radio_Button}
                style={{ height: 25, width: 25 }}
                resizeMode={"contain"}
              />
            )
          }
          rightView={
            <BodyText style={{ marginLeft: Spacing.XS / 2 }}>Rent</BodyText>
          }
          onPress={() => this.onPressType("R")}
        />
        <View
          style={{
            flex: 1,
            justifyContent: "flex-end",
            flexDirection: "row"
          }}
        >
          <Button
            style={{
              padding: 8,
              fontSize: 14,
              fontWeight: "600"
            }}
            textStyle={{ color: SRXColor.TextLink }}
            onPress={() => this.searchBarCancelPressed()}
          >
            Cancel
          </Button>
        </View>
      </View>
    );
  }

  renderSearchBar() {
    const { searchQuery } = this.state;
    return (
      <View style={[TopBarStyle.topBar]}>
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
          onChangeText={text =>
            this.searchBarUpdated({
              text,
              isFocused: this.searchBar.isFocused()
            })
          }
          onEndEditing={() =>
            this.searchBarUpdated({ text: searchQuery, isFocused: false })
          }
          onSubmitEditing={() => this.onSearchBarReturnKeyPressed()}
        />
      </View>
    );
  }

  renderSuggestion() {
    const { suggestion } = this.state;
    return (
      <SuggestionList
        dataArray={suggestion}
        onItemSelected={this.onSuggestionSelected}
        keyboardShouldPersistTaps={"handled"}
        onScroll={() => {
          if (this.searchBar.isFocused()) {
            this.searchBar.blur();
          }
        }}
      />
    );
  }

  renderCurrentLocation() {
    const { locationOptions, loadingState } = this.state;
    const disable = loadingState === LoadingState.Loading;

    if (locationOptions.includes(LocationSearchOptions.currentLocation)) {
      return (
        <TouchableHighlight
          onPress={() =>
            this.locationOptionSelected(LocationSearchOptions.currentLocation)
          }
          disabled={disable}
        >
          <View style={{ paddingHorizontal: Spacing.M }}>
            <View
              style={[Styles.tableCellRow, { paddingVertical: Spacing.XS }]}
            >
              <FeatherIcon
                name="map-pin"
                size={22}
                color={disable ? SRXColor.Gray : SRXColor.Black}
                style={{ alignSelf: "center", marginRight: 8 }}
              />
              <Text
                style={[
                  Styles.tableCellRowTitle,
                  { color: disable ? SRXColor.Gray : SRXColor.Black }
                ]}
              >
                Current Location
              </Text>
            </View>
            <Separator />
          </View>
        </TouchableHighlight>
      );
    }
  }

  renderAmenitiesOptions() {
    const { locationOptions } = this.state;
    const amenitiesOptions = [];
    if (locationOptions.includes(LocationSearchOptions.mrt)) {
      amenitiesOptions.push(LocationSearchOptions.mrt);
    }
    if (locationOptions.includes(LocationSearchOptions.district)) {
      amenitiesOptions.push(LocationSearchOptions.district);
    }
    if (locationOptions.includes(LocationSearchOptions.hdb_estate)) {
      amenitiesOptions.push(LocationSearchOptions.hdb_estate);
    }
    if (locationOptions.includes(LocationSearchOptions.school)) {
      amenitiesOptions.push(LocationSearchOptions.school);
    }
    // for next version
    // if (locationOptions.includes(LocationSearchOptions.travel_time)) {
    //   amenitiesOptions.push(LocationSearchOptions.travel_time);
    // }

    return (
      <View style={{ paddingHorizontal: Spacing.M }}>
        <View
          style={{
            paddingTop: Spacing.M,
            paddingBottom: 6
            //backgroundColor: "#efefef",
            // paddingHorizontal: Spacing.M,
            // paddingTop: Spacing.M,
            // paddingBottom: Spacing.XS
            //paddingBottom: 10,
          }}
        >
          {/* <Text style={Styles.sectionTitle}>Search by</Text> */}
          <View style={{ flexDirection: "row", flexWrap: "wrap" }}>
            {amenitiesOptions.map((item, index) => (
              <Button
                buttonStyle={{
                  paddingHorizontal: Spacing.S,
                  paddingVertical: 4,

                  marginRight: 8,
                  marginBottom: 10,

                  borderRadius: 20,
                  borderWidth: 1,
                  borderColor: "#e0e0e0",
                  backgroundColor: "white"
                }}
                textStyle={{ color: SRXColor.Black, fontSize: 16 }}
                onPress={() => this.locationOptionSelected(item)}
                key={index}
              >
                {item}
              </Button>
            ))}
          </View>
        </View>
        <Separator />
      </View>
    );
  }

  recentSearchItemSelected(recentSearchItem) {
    this.passBackSelectedLocation({
      addressData: recentSearchItem,
      updateRecentSearches: false
    });
  }

  renderRecentSearches() {
    const { locationOptions, recentSearches } = this.state;
    if (
      locationOptions.includes(LocationSearchOptions.recent_search) &&
      !ObjectUtil.isEmpty(recentSearches) &&
      Array.isArray(recentSearches)
    ) {
      return (
        <View style={{ marginTop: Spacing.S }}>
          {/* <View style={{ paddingHorizontal: 15 }}>
            <Text style={Styles.sectionTitle}>Recent Searches</Text>
          </View> */}
          {recentSearches.map((item, index) => (
            <TouchableHighlight
              key={index}
              onPress={() => this.recentSearchItemSelected(item)}
            >
              <View
                style={{
                  paddingHorizontal: Spacing.M,
                  backgroundColor: SRXColor.White
                }}
              >
                <View style={Styles.tableCellRow}>
                  <FeatherIcon
                    name="clock"
                    size={18}
                    color={SRXColor.Black}
                    style={{ alignSelf: "center", marginRight: 8 }}
                  />
                  <Text style={Styles.tableCellRowTitle} numberOfLines={1}>
                    {item.displayText}
                  </Text>
                </View>
                <Separator />
              </View>
            </TouchableHighlight>
          ))}
        </View>
      );
    }
  }

  renderLocationSearchOptions() {
    return (
      <ScrollView
        style={{ flex: 1, backgroundColor: "white" }}
        keyboardShouldPersistTaps={"handled"}
        onScroll={() => {
          if (this.searchBar.isFocused()) {
            this.searchBar.blur();
          }
        }}
      >
        {this.renderCurrentLocation()}
        {this.renderAmenitiesOptions()}
        {this.renderRecentSearches()}
      </ScrollView>
    );
  }

  renderContent() {
    const { searchQuery } = this.state;
    if (!ObjectUtil.isEmpty(searchQuery)) {
      return this.renderSuggestion();
    } else {
      return this.renderLocationSearchOptions();
    }
  }

  render() {
    return (
      <SafeAreaView
        style={{ flex: 1, backgroundColor: SRXColor.White }}
        forceInset={{ bottom: "never" }}
      >
        {this.renderBuyRentRadioButton()}
        {this.renderSearchBar()}
        {this.renderContent()}
      </SafeAreaView>
    );
  }
}

const Styles = {
  tableCellRow: {
    flexDirection: "row",
    // paddingLeft: 15,
    // paddingRight: 25,
    minHeight: 44,
    alignItems: "center",
    backgroundColor: "white"
  },
  tableCellRowTitle: {
    fontSize: 15,
    marginVertical: 8,
    color: SRXColor.Black,
    flex: 1
  },
  sectionTitle: {
    marginVertical: 12,
    fontSize: 15,
    fontWeight: "600"
  },
  buttonContainer: {
    paddingTop: Spacing.M,
    alignItems: "center"
  }
};

const PropertySearchLocationStack = passProps => {
  return {
    stack: {
      // id: "PropertySearchLocationStack",
      children: [
        {
          component: {
            name: "PropertySearchStack.PropertySearchLocation",
            passProps,
            options: {
              modalPresentationStyle: "overFullScreen"
            }

            //   options: {
            //     topBar: {
            //       searchBar: true,
            //       searchBarHiddenWhenScrolling: true,
            //       searchBarPlaceholder: "Search questions..."
            //     }
            //   }
          }
        }
      ]
    }
  };
};

export { PropertySearchLocation, PropertySearchLocationStack };
