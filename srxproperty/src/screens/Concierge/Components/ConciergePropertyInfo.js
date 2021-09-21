import React, {Component} from 'react';
import {View, StyleSheet, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';

import {
  BodyText,
  FeatherIcon,
  Picker,
  SmallBodyText,
} from '../../../components';
import {UserType} from '../../../constants';
import {Spacing, InputStyles, Typography} from '../../../styles';
import {
  Concierge_PropertyType_Options,
  Concierge_CondoAndApt_Option,
  Sale_Options,
  Concierge_PriceRange_Label,
} from '../Constants';
import {FilterOptionsUtil, ObjectUtil} from '../../../utils';
import {PICKER_MODES} from '../../../components/Picker/Constant';
import {AddressAutoComplete} from '../../AutoComplete';

const isIOS = Platform.OS === 'ios';
class ConciergePropertyInfo extends Component {
  static propTypes = {
    propertyInfo: PropTypes.object,
  };

  static defaultProps = {
    propertyInfo: null,
  };

  //get Min & Max Price Range
  getPriceRangesForSaleOrRent(priceRangeType) {
    const {userType} = this.props.propertyInfo;
    const isSale = Sale_Options.has(userType) ? true : false;
    const noSelectOption = {key: priceRangeType, value: ''};
    var tempPriceRangeArray = [];

    if (isSale) {
      tempPriceRangeArray = FilterOptionsUtil.getSearchSalePricesOptions();
    } else {
      tempPriceRangeArray = FilterOptionsUtil.getSearchRentPricesOptions();
    }

    //Remove Any option
    tempPriceRangeArray.splice(0, 1);
    //Add min max unSelect option
    tempPriceRangeArray.unshift(noSelectOption);

    return tempPriceRangeArray;
  }

  //showing auto Complete form
  showAutoComplete = () => {
    AddressAutoComplete.showSuggestionAutoComplete({
      onSuggestionSelected: this.onSuggestionSelected,
    });
  };

  //need validation
  onSuggestionSelected = item => {
    const {displayText, id, type} = item;
    const {propertyInfo, onChangePropertyInfo, errorMessage} = this.props;

    const returningItem = {
      displayText: '',
      districtTownId: '',
      keywords: '',
      locationType: '',
    };

    const hasEntryType = !ObjectUtil.isEmpty(type);
    let selectedLocation = '';
    let errMsgLocation = '';

    if (hasEntryType && type.toUpperCase() === 'DISTRICT') {
      selectedLocation = id;
      returningItem.districtTownId = id;
    } else if (hasEntryType && type.toUpperCase() === 'HDB_ESTATE') {
      selectedLocation = id;
      returningItem.districtTownId = id;
    }
    returningItem.keywords = displayText;
    returningItem.locationType = type;

    if (displayText) {
      selectedLocation = displayText;
    } else if (type) {
      selectedLocation = type;
    }

    //If not select Any location
    if (ObjectUtil.isEmpty(selectedLocation)) {
      errMsgLocation = 'Required field';
    }

    const newPropertyTypeInfo = {
      ...propertyInfo,
      ...returningItem,
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgLocation,
    };
    if (onChangePropertyInfo) {
      onChangePropertyInfo(newPropertyTypeInfo, newErrorMessage);
    }
  };

  //User Type
  onSelectUserType = item => {
    if (!ObjectUtil.isEmpty(item)) {
      const {propertyInfo, onChangePropertyInfo, errorMessage} = this.props;
      const {propertyType} = this.props.propertyInfo;

      let buyNewProjectUserType =
        item.value === UserType.UserTypeValue.buyNewProjects;

      const newPropertyTypeInfo = {
        ...propertyInfo,
        userType: item.value,
        propertyType: buyNewProjectUserType ? '1' : propertyType,
      };
      if (onChangePropertyInfo) {
        onChangePropertyInfo(newPropertyTypeInfo, errorMessage);
      }
    }
  };

  //Property Type
  onSelectPropertyType = item => {
    if (!ObjectUtil.isEmpty(item)) {
      const {propertyInfo, onChangePropertyInfo, errorMessage} = this.props;
      const newPropertyTypeInfo = {
        ...propertyInfo,
        propertyType: item.value,
      };
      if (onChangePropertyInfo) {
        onChangePropertyInfo(newPropertyTypeInfo, errorMessage);
      }
    }
  };

  //need validation
  onChangeMinPrice = item => {
    const {propertyInfo, onChangePropertyInfo, errorMessage} = this.props;
    const newPropertyTypeInfo = {
      ...propertyInfo,
      minPrice: isIOS ? item : item.value.toString(),
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgPriceRange: ObjectUtil.isEmpty(item) ? 'Required field' : '',
    };

    if (onChangePropertyInfo) {
      onChangePropertyInfo(newPropertyTypeInfo, newErrorMessage);
    }
  };

  //need validation
  onChangeMaxPrice = item => {
    const {propertyInfo, onChangePropertyInfo, errorMessage} = this.props;
    const newPropertyTypeInfo = {
      ...propertyInfo,
      maxPrice: isIOS ? item : item.value.toString(),
    };

    const newErrorMessage = {
      ...errorMessage,
      errMsgPriceRange: ObjectUtil.isEmpty(item) ? 'Required field' : '',
    };

    if (onChangePropertyInfo) {
      onChangePropertyInfo(newPropertyTypeInfo, newErrorMessage);
    }
  };

  //Get Location Position Y
  getLocationPosition = layout => {
    const {y} = layout;
    const {viewsPosition, getViewsPosition} = this.props;
    const newPosition = {
      ...viewsPosition,
      locationErrMsgY: y,
    };

    if (getViewsPosition) {
      getViewsPosition(newPosition);
    }
  };

  //Get Price Range Position Y
  getPriceRangePosition = layout => {
    const {y} = layout;
    const {viewsPosition, getViewsPosition} = this.props;
    const newPosition = {
      ...viewsPosition,
      priceRangeErrMsgY: y,
    };

    if (getViewsPosition) {
      getViewsPosition(newPosition);
    }
  };

  //Start rendering method
  renderTitle() {
    const {title} = this.props;
    return (
      <BodyText style={{fontWeight: '600', lineHeight: 24}}>
        {!ObjectUtil.isEmpty(title) ? title : 'Tell us about your needs'}
      </BodyText>
    );
  }

  //Customer Types
  renderCustomerType() {
    const {userType} = this.props.propertyInfo;

    return (
      <View style={{marginTop: Spacing.M}}>
        <BodyText style={styles.pickerLabel}>I am looking to</BodyText>
        <Picker
          data={UserType.USER_TYPE_ARRAY}
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            InputStyles.containerPadding,
            InputStyles.singleLineTextHeight,
            {paddingRight: Spacing.XS, flex: 1},
          ]}
          inputStyle={[Typography.SmallBody]}
          mode={'dialog'}
          prompt={'Customer Type'}
          useCustomPicker={true}
          selectedValue={userType}
          titleOfItem={(item, index) => item.key}
          valueOfItem={(item, index) => item.value}
          onSubmit={item => this.onSelectUserType(item)}
          renderRightAccessory={this.renderDropDownIcon}
        />
      </View>
    );
  }

  //Property Type
  renderPropertyType() {
    const {propertyType, userType} = this.props.propertyInfo;

    let propertyTypeDataArray =
      userType === UserType.UserTypeValue.buyNewProjects
        ? Concierge_CondoAndApt_Option
        : Concierge_PropertyType_Options;

    return (
      <View style={{marginTop: Spacing.M}}>
        <BodyText style={styles.pickerLabel}>Property Type</BodyText>
        <Picker
          data={propertyTypeDataArray}
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            InputStyles.containerPadding,
            InputStyles.singleLineTextHeight,
            {paddingRight: Spacing.XS, flex: 1},
          ]}
          inputStyle={[Typography.SmallBody]}
          mode={'dialog'}
          prompt={'Property Type'}
          useCustomPicker={true}
          selectedValue={propertyType}
          titleOfItem={(item, index) => item.key}
          valueOfItem={(item, index) => item.value}
          onSubmit={item => this.onSelectPropertyType(item)}
          renderRightAccessory={this.renderDropDownIcon}
        />
      </View>
    );
  }

  //Location
  renderLocation() {
    const {keywords} = this.props.propertyInfo;
    const {errMsgLocation} = this.props.errorMessage;
    return (
      <View
        style={{marginTop: Spacing.M}}
        onLayout={event => {
          this.getLocationPosition(event.nativeEvent.layout);
        }}>
        <BodyText style={styles.pickerLabel}>Location</BodyText>
        <TouchableOpacity
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            InputStyles.containerPadding,
            InputStyles.containerAlign,
            InputStyles.singleLineTextHeight,
          ]}
          onPress={() => this.showAutoComplete()}>
          {!ObjectUtil.isEmpty(keywords) ? (
            <SmallBodyText>{keywords}</SmallBodyText>
          ) : (
            <SmallBodyText>
              Enter location (e.g Project, street, landmark)
            </SmallBodyText>
          )}
        </TouchableOpacity>
        {errMsgLocation ? (
          <SmallBodyText style={[styles.errorMessage, {flex: 1}]}>
            {errMsgLocation}
          </SmallBodyText>
        ) : null}
      </View>
    );
  }

  //Price Range
  renderPriceRange() {
    const {minPrice, maxPrice} = this.props.propertyInfo;
    const {errMsgPriceRange} = this.props.errorMessage;

    return (
      <View
        style={{marginTop: Spacing.M}}
        onLayout={event => {
          this.getPriceRangePosition(event.nativeEvent.layout);
        }}>
        <BodyText style={styles.pickerLabel}>Price Range</BodyText>
        <View style={{flexDirection: 'row'}}>
          <Picker
            data={this.getPriceRangesForSaleOrRent(
              Concierge_PriceRange_Label.minPriceTitle,
            )}
            style={[
              InputStyles.container,
              InputStyles.containerBorder,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {
                paddingRight: Spacing.XS,
                marginRight: Spacing.XS,
                flex: 1,
              },
            ]}
            inputStyle={[Typography.SmallBody]}
            mode={isIOS ? null : 'dialog'}
            prompt={'Price Range'}
            useCustomPicker={true}
            height={isIOS ? null : '50%'}
            pickerMode={PICKER_MODES.SINGLE}
            selectedValue={minPrice}
            titleOfItem={(item, index) => item.key}
            valueOfItem={(item, index) => item.value}
            onSubmit={item => this.onChangeMinPrice(item)}
            renderRightAccessory={this.renderDropDownIcon}
          />

          <Picker
            data={this.getPriceRangesForSaleOrRent(
              Concierge_PriceRange_Label.maxPriceTitle,
            )}
            style={[
              InputStyles.container,
              InputStyles.containerBorder,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS, flex: 1},
            ]}
            inputStyle={[Typography.SmallBody]}
            mode={isIOS ? null : 'dialog'}
            prompt={'Price Range'}
            useCustomPicker={true}
            height={isIOS ? null : '50%'}
            pickerMode={PICKER_MODES.SINGLE}
            selectedValue={maxPrice}
            titleOfItem={(item, index) => item.key}
            valueOfItem={(item, index) => item.value}
            onSubmit={item => this.onChangeMaxPrice(item)}
            renderRightAccessory={this.renderDropDownIcon}
          />
        </View>
        {errMsgPriceRange ? (
          <SmallBodyText style={[styles.errorMessage, {flex: 1}]}>
            {errMsgPriceRange}
          </SmallBodyText>
        ) : null}
      </View>
    );
  }

  renderDropDownIcon() {
    return (
      <FeatherIcon
        name="chevron-down"
        size={24}
        color={'#8DABC4'}
        style={{alignSelf: 'center', marginLeft: Spacing.XS}}
      />
    );
  }

  render() {
    return (
      <View style={styles.container}>
        {this.renderTitle()}
        {this.renderCustomerType()}
        {this.renderPropertyType()}
        {this.renderLocation()}
        {this.renderPriceRange()}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    marginTop: Spacing.M,
    marginHorizontal: Spacing.M,
  },
  pickerLabel: {
    fontWeight: '600',
    lineHeight: 24,
    marginBottom: Spacing.XS,
  },
  errorMessage: {
    marginTop: Spacing.XS,
    color: '#FF151F',
    lineHeight: 20,
  },
});

export {ConciergePropertyInfo};
