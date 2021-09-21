import React, { Component } from "react";
import { View, FlatList, Dimensions } from "react-native";
import { BodyText } from "../../components";
import PropTypes from "prop-types";
import { TabView, TabBar } from "react-native-tab-view";
import { SRXColor } from "../../constants";
import { AmenityPO } from "../../dataObject";
import { AmenityCustomLocation } from "./AmenityCustomLocation";
import { AmenityCategoryItemList } from "./AmenityCategoryItemList";
import { TravelTypes, AmenityTypes, AmenitiesOptions } from "./constant";
import { AmenityCategoryTabBar } from "./AmenityCategoryTabBar";
import { ObjectUtil } from "../../utils";
import { Spacing } from "../../styles";

var { height, width } = Dimensions.get("window");

const AmenitiesSource = {
  ListingDetails: "ListingDetails",
  MyPropertyDetails: "MyPropertyDetails"
};

class AmenityOptions extends Component {
  static defaultProps = {
    nearbyAmenities: [],
    trainAmenities: [],
    busStopAmenities: [],
    schoolAmenities: [],
    retailAmenities: [],
    foodBeverageAmenities: [],
    hospitalAmenities: [],
    worshipAmenities: [],
    otherAmenities: [],
    category: AmenityTypes.Others
  };

  constructor(props) {
    super(props);

    this.renderItem = this.renderItem.bind(this);
    this.renderScene = this.renderScene.bind(this);
    let data;
    if(props.source==AmenitiesSource.MyPropertyDetails){
      data=AmenitiesOptions.slice(1);
      //NearBy Owner tab has been temporarily removed
    }else{
      data=AmenitiesOptions;
    }
    this.state = {
      data: data,
      index: 0,
      routes: [
        { key: "Others", title: "Your Location" },
        { key: "MRT", title: "MRT" },
        { key: "Bus", title: "Bus" },
        { key: "Schools", title: "Schools" },
        { key: "Retail", title: "Retail" },
        { key: "Restaurant", title: "F&B" },
        { key: "Medical", title: "Hospital" },
        { key: "Worship", title: "Places Of Worship" }
      ]
    };
  }
  

  componentDidUpdate(prevProps, prevState) {
    if (prevProps.category !== this.props.category) {
      this.scrollToCategory(this.props.category);
    }
  }

  getCategoryIndex(type) {
    const { data } = this.state;
    const index = data.findIndex(function({ key }) {
      return key === type;
    });

    return index;
  }

  getAmenitiesOfType(type) {
    const {
      trainAmenities,
      busStopAmenities,
      schoolAmenities,
      retailAmenities,
      foodBeverageAmenities,
      hospitalAmenities,
      worshipAmenities
    } = this.props;

    switch (type) {
      case AmenityTypes.MRT:
        return trainAmenities;
      case AmenityTypes.Bus:
        return busStopAmenities;
      case AmenityTypes.Schools:
        return schoolAmenities;
      case AmenityTypes.Retail:
        return retailAmenities;
      case AmenityTypes.FoodAndBeverage:
        return foodBeverageAmenities;
      case AmenityTypes.Hospital:
        return hospitalAmenities;
      case AmenityTypes.Worship:
        return worshipAmenities;
      default:
        return null;
    }
  }

  scrollToCategory(type) {
    const { onTabChanged } = this.props;

    const index = this.getCategoryIndex(type);
    if (index >= 0) {
      this.flatList.scrollToIndex({ index });

      onTabChanged(type);
    }
  }

  renderOtherOptions() {
    const {
      otherAmenities,
      selectedAmenity,
      selectedTravelMethod,
      onTravelModeSelected,
      onAmenityUpdated,
      source,
      nearbyUsers
    } = this.props;

    if (source == AmenitiesSource.ListingDetails) {
      let customLocation;
      if (!ObjectUtil.isEmpty(otherAmenities)) {
        customLocation = otherAmenities[0];
      }

      return (
        <AmenityCustomLocation
          toLocation={customLocation}
          onAmenityUpdated={onAmenityUpdated}
          travelMethod={selectedTravelMethod}
          onTravelModeSelected={onTravelModeSelected}
        />
      );
    } 
    //Temporary remove due to design not completed
    // else if (source == AmenitiesSource.MyPropertyDetails) {
    //   return (
    //     <View style={{ padding: Spacing.M }}>
    //       <BodyText>{nearbyUsers.length} Nearby properties tracked</BodyText>
    //     </View>
    //   );
    // }
  }

  renderAmenitiesLists(type) {
    const {
      selectedAmenity,
      selectedTravelMethod,
      onTravelModeSelected,
      onAmenitySelected
    } = this.props;

    const amenities = this.getAmenitiesOfType(type);

    return (
      <AmenityCategoryItemList
        category={type}
        amenities={amenities}
        selectedAmenity={selectedAmenity}
        selectedTravelMethod={selectedTravelMethod}
        onTravelModeSelected={onTravelModeSelected}
        onAmenitySelected={onAmenitySelected}
      />
    );
  }

  renderScene = key => {
    switch (key) {
      case AmenityTypes.Others:
        return this.renderOtherOptions();
      default:
        return this.renderAmenitiesLists(key);
    }
  };

  renderItem({ item, index }) {
    return <View style={{ width }}>{this.renderScene(item.key)}</View>;
  }

  onViewableItemsChanged = ({ viewableItems, changed }) => {
    const { onTabChanged } = this.props;
    if (viewableItems.length == 1) {
      const firstObject = viewableItems[0];
      const { item } = firstObject;
      onTabChanged(item.key);
    }
  };

  render() {
    const { data } = this.state;
    const { category, onTabChanged, amenitiesOptions } = this.props;

    console.log("amenity option category - ");
    console.log(category);
    const initialIndex = this.getCategoryIndex(category);

    return (
      <View
        style={{
          // flex: 1,
          height: "50%",
          backgroundColor: "white",
          shadowColor: "rgb(110,129,154)",
          shadowOffset: { width: 1, height: -4 },
          shadowOpacity: 0.32,
          shadowRadius: 3
        }}
      >
        <AmenityCategoryTabBar
          showIndicator={true}
          category={category}
          onTypeSelected={({ type }) => onTabChanged(type)}
          amenitiesOptions={amenitiesOptions}
        />
        <FlatList
          ref={component => (this.flatList = component)}
          style={{ flex: 1 }}
          pagingEnabled
          horizontal
          showsHorizontalScrollIndicator={false}
          data={data}
          renderItem={this.renderItem}
          initialScrollIndex={initialIndex >= 0 ? initialIndex : 0}
          getItemLayout={(data, index) => ({
            length: width,
            offset: width * index,
            index
          })}
          onViewableItemsChanged={this.onViewableItemsChanged}
          // onScrollEndDrag={event => console.log(event)}
        />
      </View>
    );
  }
}

AmenityOptions.propTypes = {
  trainAmenities: PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
  busStopAmenities: PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
  schoolAmenities: PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
  retailAmenities: PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
  foodBeverageAmenities: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
    PropTypes.string
  ]),
  hospitalAmenities: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
    PropTypes.string
  ]),
  worshipAmenities: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.instanceOf(AmenityPO)),
    PropTypes.string
  ]),
  selectedAmenity: PropTypes.instanceOf(AmenityPO),
  selectedTravelMethod: PropTypes.oneOf(Object.keys(TravelTypes)),
  onTabChanged: PropTypes.func,
  onAmenitySelected: PropTypes.func,
  onTravelModeSelected: PropTypes.func, //refer to AmenityItem,
  onAmenityUpdated: PropTypes.func, //For AmenityCustomLocation
  amenitiesOptions: PropTypes.arrayOf(PropTypes.object),
  source: PropTypes.string,
  nearbyUsers: PropTypes.arrayOf(PropTypes.object)
};

AmenityOptions.Sources = AmenitiesSource;

export { AmenityOptions };
