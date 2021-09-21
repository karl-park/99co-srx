import React, {Component} from 'react';
import {
  View,
  Dimensions,
  Platform,
  Linking,
  Keyboard,
  SafeAreaView,
  Alert,
  TouchableOpacity,
  BackHandler,
} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {IS_IOS, SRXColor, AlertMessage, LoadingState} from '../../constants';
import {
  LargeTitleComponent,
  Button,
  Picker,
  Text,
  FeatherIcon,
  TextInput,
  BodyText,
  TouchableHighlight,
  Separator,
  Heading1,
} from '../../components';
import {
  ObjectUtil,
  PropertyTypeUtil,
  PropertyOptionsUtil,
  CommonUtil,
  StringUtil,
  DebugUtil,
  GoogleAnalyticUtil,
  UserUtil,
} from '../../utils';
import {
  AddressDetailService,
  XValueService,
  LoginService,
} from '../../services';
import Moment from 'moment';
import {AppConstant} from '../../constants';
import {Navigation} from 'react-native-navigation';
import {InputStyles, Spacing} from '../../styles';
import {AddressAutoComplete, AutoCompletePurposes} from '../AutoComplete';
import {connect} from 'react-redux';
import {UpdateProfile} from '../SideMenu';

var {width} = Dimensions.get('window');
const isIOS = Platform.OS === 'ios';
class XValueForm extends LargeTitleComponent {
  state = {
    xValuePO: {
      type: 'S',
      address: null,
      postalCode: null,
      floorNum: null,
      unitNum: null,
      buildingNum: null,
      propertyType: null,
      subType: null,
      landType: null,
      size: null,
      streetId: null,
      gfa: null,
      pesSize: null,
      tenure: null,
      lastConstructed: null,
    },
    recentSearches: [],
    error: {},
    propertyTypeOptions: PropertyOptionsUtil.getPropertyTypesArrayForSubtype(),
    typeOfAreaOptions: PropertyOptionsUtil.getTypesOfAreas(),
    tenureOptions: PropertyOptionsUtil.getTenures(),
    builtYearOptions: this.getBuiltYearOptions(),
  };

  constructor(props) {
    super(props);

    this.closeAddressAutoComplete = this.closeAddressAutoComplete.bind(this);
    this.saleRentSelected = this.saleRentSelected.bind(this);
    this.getPropertyInfo = this.getPropertyInfo.bind(this);
    this.onUnitFloorNumEndEditing = this.onUnitFloorNumEndEditing.bind(this);
    this.viewXValueResult = this.viewXValueResult.bind(this);
    this.directToSRXValuation = this.directToSRXValuation.bind(this);
    this.loadXValue = this.loadXValue.bind(this);
    Navigation.events().bindComponent(this);
  }

  componentDidMount() {
    UserUtil.retrieveRecentXValue().then(result => {
      this.setState({recentSearches: result});
    });
  }

  componentDidAppear(prevState) {
    if (!IS_IOS) {
      //for android
      this.backHandler = BackHandler.addEventListener(
        'hardwareBackPress',
        () => {
          Navigation.mergeOptions(this.props.componentId, {
            bottomTabs: {
              currentTabIndex: 0,
            },
          });
          return true;
        },
      );
    }
    UserUtil.retrieveRecentXValue().then(result => {
      this.setState({recentSearches: result});
    });
  }

  componentDidDisappear() {
    //Remove all listeners
    if (!IS_IOS) {
      this.backHandler.remove();
    }
  }

  componentDidUpdate(prevProps) {
    if (prevProps.userPO !== this.props.userPO) {
      if (ObjectUtil.isEmpty(this.props.userPO)) {
        this.setState({
          xValuePO: {
            type: 'S',
            address: null,
            postalCode: null,
            floorNum: null,
            unitNum: null,
            buildingNum: null,
            propertyType: null,
            subType: null,
            landType: null,
            size: null,
            streetId: null,
            gfa: null,
            pesSize: null,
            tenure: null,
            lastConstructed: null,
          },
        });
      }
    } else if (prevProps.serverDomain !== this.props.serverDomain) {
      this.setState({
        xValuePO: {
          type: 'S',
          address: null,
          postalCode: null,
          floorNum: null,
          unitNum: null,
          buildingNum: null,
          propertyType: null,
          subType: null,
          landType: null,
          size: null,
          streetId: null,
          gfa: null,
          pesSize: null,
          tenure: null,
          lastConstructed: null,
        },
      });
    }
  }

  getBuiltYearOptions() {
    const builtYearArray = PropertyOptionsUtil.getBuiltYears();

    const newArray = [];
    for (let i = 0; i < builtYearArray.length; i++) {
      const option = builtYearArray[i];
      newArray.push({value: option.value, key: option.key});
    }

    return newArray;
  }

  onGetXValueButtonPressed() {
    const {xValuePO} = this.state;
    const {
      address,
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      propertyType,
      subType,
      landType,
      size,
      streetId,
      gfa,
      pesSize,
      tenure,
      lastConstructed,
    } = xValuePO;

    if (!ObjectUtil.isEmpty(xValuePO)) {
      this.onSaveRecentXValue({
        addressData: {
          address,
          type,
          postalCode,
          floorNum,
          unitNum,
          buildingNum,
          propertyType,
          subType,
          landType,
          size,
          streetId,
          gfa,
          pesSize,
          tenure,
          lastConstructed,
        },
        updateRecentSearches: true,
      });
    }
  }

  onSaveRecentXValue = ({addressData, updateRecentSearches}) => {
    const data = {
      //set everything to null to clear all the values
      address: null,
      postalCode: null,
      type: null,
      floorNum: null,
      unitNum: null,
      buildingNum: null,
      propertyType: null,
      subType: null,
      landType: null,
      size: null,
      streetId: null,
      gfa: null,
      pesSize: null,
      tenure: null,
      lastConstructed: null,

      ...addressData,
    };

    if (updateRecentSearches) {
      UserUtil.updateRecentXValue(data);
    }
  };

  recentSearchItemSelected(recentSearchItem) {
    this.onSaveRecentXValue({
      addressData: recentSearchItem,
      updateRecentSearches: false,
    });
    this.setState(
      {
        xValuePO: {
          type: recentSearchItem.type,
          address: recentSearchItem.address,
          postalCode: recentSearchItem.postalCode,
          floorNum: recentSearchItem.floorNum,
          unitNum: recentSearchItem.unitNum,
          buildingNum: recentSearchItem.buildingNum,
          propertyType: recentSearchItem.propertyType,
          subType: recentSearchItem.subType,
          landType: recentSearchItem.landType,
          size: recentSearchItem.size,
          streetId: recentSearchItem.streetId,
          gfa: recentSearchItem.gfa,
          pesSize: recentSearchItem.pesSize,
          tenure: recentSearchItem.tenure,
          lastConstructed: recentSearchItem.lastConstructed,
        },
      },
      () => this.viewRecentXValueResult(),
    );
  }

  renderAddress() {
    return (
      <View style={styles.inputContainer}>
        <TouchableOpacity
          style={[
            InputStyles.container,
            InputStyles.containerBorder,
            InputStyles.containerPadding,
            InputStyles.containerAlign,
            InputStyles.singleLineTextHeight,
          ]}
          onPress={() => this.showAddressAutoComplete()}>
          <FeatherIcon
            name="search"
            size={24}
            color={'#8DABC4'}
            style={{alignSelf: 'center', marginRight: Spacing.XS}}
          />
          {ObjectUtil.isEmpty(this.state.xValuePO.address) ? (
            <Text style={[InputStyles.text, {color: '#8DABC4'}]}>
              Enter your address or postal code
            </Text>
          ) : (
            <Text style={[InputStyles.text, {color: SRXColor.Black}]}>
              {this.state.xValuePO.address}
            </Text>
          )}
        </TouchableOpacity>
      </View>
    );
  }

  renderSubtype() {
    if (
      !ObjectUtil.isEmpty(this.state.xValuePO.postalCode) ||
      this.state.xValuePO.postalCode > 0
    ) {
      return (
        <View style={styles.inputContainer}>
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(this.state.error.subTypeError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={this.state.propertyTypeOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            selectedValue={this.state.xValuePO.subType}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue => this.onPropertyTypeSelected(itemValue)}
            renderRightAccessory={() => {
              return (
                <FeatherIcon
                  name="chevron-right"
                  size={24}
                  color={'#8DABC4'}
                  style={{alignSelf: 'center', marginLeft: Spacing.XS}}
                />
              );
            }}
          />
          {this.renderErrorMessageForPicker(this.state.error.subTypeError)}
        </View>
      );
    } else {
      return null;
    }
  }

  renderFloorNum() {
    if (
      (!ObjectUtil.isEmpty(this.state.xValuePO.postalCode) ||
        this.state.xValuePO.postalCode > 0) &&
      !PropertyTypeUtil.isCommercial(this.state.xValuePO.subType)
    ) {
      return (
        <View style={styles.inputContainer}>
          <View style={[{flexDirection: 'row'}]}>
            <TextInput
              style={{flex: 1}}
              keyboardType={
                Platform.OS === 'ios' ? 'numbers-and-punctuation' : 'default'
              }
              placeholder={'Floor'}
              autoCorrect={false}
              clearButtonMode={'while-editing'}
              returnKeyType={'done'}
              value={this.state.xValuePO.floorNum}
              error={
                !ObjectUtil.isEmpty(this.state.error.unitNumError) &&
                ObjectUtil.isEmpty(this.state.xValuePO.floorNum)
                  ? this.state.error.unitNumError
                  : null
              }
              onChangeText={newText =>
                this.setState({
                  xValuePO: {...this.state.xValuePO, floorNum: newText},
                })
              }
              onEndEditing={this.onUnitFloorNumEndEditing}
            />
            <View>
              <View style={{justifyContent: 'center', height: 40}}>
                <BodyText
                  style={{marginLeft: Spacing.XS, marginRight: Spacing.XS}}>
                  -
                </BodyText>
              </View>
            </View>
            <TextInput
              style={[{flex: 1}]}
              keyboardType={
                Platform.OS === 'ios' ? 'numbers-and-punctuation' : 'default'
              }
              placeholder={'Unit'}
              autoCorrect={false}
              clearButtonMode={'while-editing'}
              returnKeyType={'done'}
              value={this.state.xValuePO.unitNum}
              error={
                !ObjectUtil.isEmpty(this.state.error.unitNumError) &&
                ObjectUtil.isEmpty(this.state.xValuePO.unitNum)
                  ? this.state.error.unitNumError
                  : null
              }
              onChangeText={newText =>
                this.setState({
                  xValuePO: {...this.state.xValuePO, unitNum: newText},
                })
              }
              onEndEditing={this.onUnitFloorNumEndEditing}
            />
          </View>
        </View>
      );
    }
  }

  renderSize() {
    const {
      subType,
      postalCode,
      floorNum,
      unitNum,
      size,
      pesSize,
      possiblyNoUnit,
    } = this.state.xValuePO;

    if (
      (!ObjectUtil.isEmpty(postalCode) || postalCode > 0) &&
      !PropertyTypeUtil.isCommercial(subType)
    ) {
      return (
        <View style={[styles.inputContainer, {flexDirection: 'row'}]}>
          <TextInput
            style={{flex: 2}}
            inputContainerStyle={{
              paddingRight: 0,
              backgroundColor: SRXColor.SmallBodyBackground,
              borderColor: SRXColor.SmallBodyBackground,
            }}
            keyboardType={'number-pad'}
            placeholder={PropertyTypeUtil.isHDB(subType) ? 'Sqm' : 'Sqft'}
            placeholderTextColor={SRXColor.Black}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={size > 0 ? StringUtil.formatThousand(size) : ''}
            error={this.state.error.sizeError}
            onChangeText={newText =>
              this.setState({
                xValuePO: {
                  ...this.state.xValuePO,
                  size: StringUtil.decimalValue(newText, 0),
                },
              })
            }
            // onFocus={() => {
            //   if (ObjectUtil.isEmpty(size)) {
            //     this.getPropertyInfo({
            //       propertySubType: subType,
            //       postalCode,
            //       floorNum,
            //       unitNum,
            //       possiblyNoUnit
            //     });
            //   }
            // }}
            rightView={
              <Button
                buttonStyle={[
                  styles.inputBoxStyle,
                  InputStyles.container,
                  {
                    // flex: 1,
                    borderColor: SRXColor.Teal,
                    marginRight: -1,
                    marginVertical: -1,
                    alignSelf: 'stretch',
                    backgroundColor: SRXColor.White,
                  },
                ]}
                textStyle={{
                  fontSize: 14,
                  color: SRXColor.Black,
                  textAlign: 'center',
                }}
                onPress={() => this.getSizeClicked()}>
                Get Size
              </Button>
            }
          />
          <BodyText
            style={{
              marginLeft: Spacing.XS,
              marginRight: Spacing.XS,
              color: 'transparent',
            }}>
            {'-'}
          </BodyText>
          <TextInput
            style={{flex: 1}}
            keyboardType={'number-pad'}
            underlineColorAndroid="transparent"
            placeholder={'Ext. Area'}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={StringUtil.formatThousand(pesSize)}
            onChangeText={newText =>
              this.setState({
                xValuePO: {
                  ...this.state.xValuePO,
                  pesSize: StringUtil.decimalValue(newText, 0),
                },
              })
            }
          />
        </View>
      );
    }
  }

  renderErrorMessageForPicker(error) {
    if (!ObjectUtil.isEmpty(error)) {
      return <Text style={[InputStyles.errorMessage, {flex: 1}]}>{error}</Text>;
    }
  }

  renderTypeOfArea() {
    if (PropertyTypeUtil.isLanded(this.state.xValuePO.subType)) {
      console.log(this.state.error);
      return (
        <View style={styles.inputContainer}>
          {/* <Text style={InputStyles.label}>Type Of Area</Text> */}
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(this.state.error.landTypeError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={this.state.typeOfAreaOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            selectedValue={this.state.xValuePO.landType || ''}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue =>
              this.setState({
                xValuePO: {
                  ...this.state.xValuePO,
                  landType: isIOS ? itemValue : itemValue.value,
                },
              })
            }
            renderRightAccessory={() => (
              <FeatherIcon
                name="chevron-right"
                size={24}
                color={'#8DABC4'}
                style={{alignSelf: 'center', marginLeft: Spacing.XS}}
              />
            )}
          />
          {this.renderErrorMessageForPicker(this.state.error.landTypeError)}
        </View>
      );
    }
  }

  renderTenure() {
    if (PropertyTypeUtil.isLanded(this.state.xValuePO.subType)) {
      return (
        <View style={styles.inputContainer}>
          {/* <Text style={InputStyles.label}>Tenure</Text> */}
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(this.state.error.tenureError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={this.state.tenureOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            error={this.state.error.tenureError}
            selectedValue={this.state.xValuePO.tenure || ''}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue =>
              this.setState({
                xValuePO: {
                  ...this.state.xValuePO,
                  tenure: isIOS ? itemValue : itemValue.value.toString(),
                },
              })
            }
            renderRightAccessory={() => (
              <FeatherIcon
                name="chevron-right"
                size={24}
                color={'#8DABC4'}
                style={{alignSelf: 'center', marginLeft: Spacing.XS}}
              />
            )}
          />
          {this.renderErrorMessageForPicker(this.state.error.tenureError)}
        </View>
      );
    }
  }

  renderBuiltSize() {
    if (PropertyTypeUtil.isLanded(this.state.xValuePO.subType)) {
      const {gfa, subType} = this.state.xValuePO;
      const subTypeDesc = PropertyTypeUtil.isHDB(subType) ? 'sqm' : 'sqft';
      return (
        <View style={styles.inputContainer}>
          <TextInput
            keyboardType={'number-pad'}
            underlineColorAndroid="transparent"
            placeholder={'Built Size (GFA) (' + subTypeDesc + ')'}
            autoCorrect={false}
            clearButtonMode={'while-editing'}
            returnKeyType={'done'}
            value={StringUtil.formatThousand(gfa)}
            onChangeText={newText =>
              this.setState({
                xValuePO: {
                  ...this.state.xValuePO,
                  gfa: StringUtil.decimalValue(newText, 0),
                },
              })
            }
          />
        </View>
      );
    }
  }

  renderBuiltYear() {
    if (PropertyTypeUtil.isLanded(this.state.xValuePO.subType)) {
      return (
        <View style={styles.inputContainer}>
          {/* <Text style={InputStyles.label}>Built Year</Text> */}
          <Picker
            style={[
              InputStyles.container,
              ObjectUtil.isEmpty(this.state.error.lastConstructError)
                ? InputStyles.containerBorder
                : InputStyles.containerBorder_Error,
              InputStyles.containerPadding,
              InputStyles.singleLineTextHeight,
              {paddingRight: Spacing.XS},
            ]}
            data={this.state.builtYearOptions}
            mode={isIOS ? null : 'dialog'}
            useCustomPicker={true}
            height={isIOS ? null : '50%'}
            selectedValue={this.state.xValuePO.lastConstructed || ''}
            error={this.state.error.lastConstructError}
            titleOfItem={(item, itemIndex, componentIndex) => item.key}
            valueOfItem={(item, itemIndex, componentIndex) => item.value}
            onSubmit={itemValue =>
              this.setState({
                xValuePO: {
                  ...this.state.xValuePO,
                  lastConstructed: isIOS ? itemValue : itemValue.value,
                },
              })
            }
            renderRightAccessory={() => (
              <FeatherIcon
                name="chevron-right"
                size={24}
                color={'#8DABC4'}
                style={{alignSelf: 'center', marginLeft: Spacing.XS}}
              />
            )}
          />
          {this.renderErrorMessageForPicker(
            this.state.error.lastConstructError,
          )}
        </View>
      );
    }
  }

  saleRentSelected(type) {
    this.setState({xValuePO: {...this.state.xValuePO, type}});
  }

  showAddressAutoComplete = () => {
    AddressAutoComplete.show({
      onSuggestionSelected: this.closeAddressAutoComplete,
      purpose: AutoCompletePurposes.xValue,
    });
  };

  closeAddressAutoComplete = selectedDict => {
    Keyboard.dismiss();
    if (!ObjectUtil.isEmpty(selectedDict)) {
      AddressDetailService.getAddressByPostalCode({
        postalCode: selectedDict.postalCode,
        skipCommercial: 'Y',
      })
        .catch(error => {
          this.setState({error: {}});
        })
        .then(response => {
          var propertySubType = 0,
            propertyType = 0;
          var typeOfAreaToUpdate = '';
          var possiblyNoUnit = false;

          if (!ObjectUtil.isEmpty(response.data)) {
            const {data} = response;

            if (data.propertySubType > 0) {
              propertySubType = data.propertySubType;
            } else {
              propertySubType = data.propertySubTypeList[0];
            }
            if (data.propertyType > 0) {
              propertyType = data.propertyType;
            }

            possiblyNoUnit = data.possiblyNoUnit;

            //type of area
            if (PropertyTypeUtil.isLanded(propertySubType)) {
              if (!ObjectUtil.isEmpty(data.typeOfArea)) {
                const typeOfAreaUpperCase = data.typeOfArea.toUpperCase();
                if (typeOfAreaUpperCase == 'STRATA') {
                  typeOfAreaToUpdate = 'S';
                } else if (typeOfAreaUpperCase == 'LAND') {
                  typeOfAreaToUpdate = 'L';
                }
              }
            }
          }

          var propertyTypesArray = [];
          if (propertySubType > 0) {
            propertyTypesArray = PropertyOptionsUtil.getPropertyTypesArrayForSubtype(
              propertySubType,
            );
          } else if (propertyType > 0) {
            propertyTypesArray = PropertyOptionsUtil.getPropertyTypesArrayForType(
              propertyType,
            );
          } else {
            propertyTypesArray = PropertyOptionsUtil.getPropertyTypesArrayForType();
          }

          var selectedPropertyType = ''; //to get string value
          if (propertySubType > 0) {
            if (isNaN(propertySubType)) {
              selectedPropertyType = propertySubType;
            } else {
              selectedPropertyType = propertySubType.toString();
            }

            if (PropertyTypeUtil.isLanded(selectedPropertyType)) {
              const {floorNum, unitNum} = this.state.xValuePO;
              this.getPropertyInfo({
                propertySubType: selectedPropertyType,
                postalCode: selectedDict.postalCode,
                possiblyNoUnit,
              }); //exclude unit number at this stage, it should be cleared
            }
          }

          //update state
          this.setState({
            error: {},
            xValuePO: {
              ...this.state.xValuePO,
              address: selectedDict.address,
              postalCode: selectedDict.postalCode,
              buildingNum: selectedDict.buildingNum,
              propertyType,
              subType: selectedPropertyType,
              landType: typeOfAreaToUpdate,
              possiblyNoUnit,

              //clear values
              size: null,
              streetId: null,
              gfa: null,
              pesSize: null,
              tenure: null,
              lastConstructed: null,
              unitNum: null,
              floorNum: null,
            },
            propertyTypeOptions: propertyTypesArray,
          });
        });
    }
  };

  onPropertyTypeSelected = itemValue => {
    console.log('selected Property Type - ' + itemValue);

    this.setState({
      xValuePO: {
        ...this.state.xValuePO,
        subType: isIOS ? itemValue : itemValue.value,
      },
    });

    if (PropertyTypeUtil.isLanded(itemValue)) {
      const {
        postalCode,
        floorNum,
        unitNum,
        possiblyNoUnit,
      } = this.state.xValuePO;
      this.getPropertyInfo({
        postalCode,
        floorNum,
        unitNum,
        propertySubType: itemValue,
        possiblyNoUnit,
      });
    }
  };

  getPropertyInfo({
    propertySubType,
    postalCode,
    floorNum,
    unitNum,
    possiblyNoUnit,
  }) {
    // console.log((PropertyTypeUtil.isLanded(propertySubType) || possiblyNoUnit || (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum))));

    // if (propertySubType && postalCode && (PropertyTypeUtil.isLanded(propertySubType) || possiblyNoUnit || (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum)))) {
    AddressDetailService.getProject({
      // postal, type, floor, unit, typeOfArea, buildingNum
      postal: postalCode,
      type: propertySubType,
      floor: floorNum,
      unit: unitNum,
    })
      .catch(error => {
        //   AlertBox.showErrorAlert(error);
      })
      .then(response => {
        if (response.error) {
          // AlertBox.showErrorAlert(response.error);
        } else if (response.Error) {
          // AlertBox.showErrorAlert(response.Error);
        } else {
          if (
            !ObjectUtil.isEmpty(response) &&
            !ObjectUtil.isEmpty(response.data) &&
            Array.isArray(response.data)
          ) {
            const {data} = response;

            var correspondingResponse;
            if (data.length > 1) {
              for (var i = 0; i < data.length; i++) {
                const item = data[i];
                if (item.type == propertySubType) {
                  correspondingResponse = item;
                  break;
                }
              }
            }

            if (!correspondingResponse) {
              correspondingResponse = data[0];
            }

            //PES
            var pesSizeToUpdate;
            if (correspondingResponse.pesSize > 0) {
              if (PropertyTypeUtil.isHDB(propertySubType)) {
                pesSizeToUpdate = CommonUtil.convertFeetToMetre(
                  pesSizeToUpdate,
                );
              } else {
                pesSizeToUpdate = correspondingResponse.pesSize;
              }
            }

            //tenure
            var tenureValueToUpdate;
            if (!ObjectUtil.isEmpty(correspondingResponse.tenure)) {
              const tenureUpperCase = correspondingResponse.tenure.toUpperCase();

              if (tenureUpperCase == 'LEASE') tenureValueToUpdate = '2';
              else if (
                tenureUpperCase == 'FREEHOLD' ||
                tenureUpperCase == 'FREE'
              ) {
                tenureValueToUpdate = '1';
              } else {
                tenureValueToUpdate = '';
              }
            }

            //last constructed
            //Show the built year
            Moment.locale('en');
            var builtYrToUpdate = '';
            if (!ObjectUtil.isEmpty(correspondingResponse.lastConstructed)) {
              builtYrToUpdate = Moment(
                correspondingResponse.lastConstructed,
              ).format('YYYY');
            }

            if (correspondingResponse.size <= 0) {
              Alert.alert(
                AlertMessage.SuccessMessageTitle,
                'We are unable to default the size because we do not have that information. Please key in the size if you know.',
              );
            }

            this.setState({
              error: {...this.state.error, sizeError: null},
              xValuePO: {
                ...this.state.xValuePO,
                size:
                  correspondingResponse.size > 0
                    ? correspondingResponse.size
                    : 'N.A.',
                streetId: correspondingResponse.streetId,
                gfa:
                  correspondingResponse.gfa > 0 &&
                  PropertyTypeUtil.isLanded(propertySubType)
                    ? correspondingResponse.gfa
                    : null,
                pesSize: pesSizeToUpdate,
                tenure: tenureValueToUpdate,
                lastConstructed: builtYrToUpdate,
              },
            });
          } else {
            Alert.alert(
              AlertMessage.SuccessMessageTitle,
              'No project found or Wrong property type',
            );
          }
        }
      });
    // }
  }

  //requestMobileVerification API
  requestMobileVerification() {
    const {mobileLocalNum} = this.props.userPO;

    LoginService.requestMobileVerification({
      countryCode: '+65', //default
      mobile: mobileLocalNum,
    })
      .catch(error => {
        Alert.alert(AlertMessage.ErrorMessageTitle, error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            Alert.alert(AlertMessage.ErrorMessageTitle, error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (response.result === 'success') {
                this.directToVerfiyMobileNumber();
              }
            }
          }
        } else {
          Alert.alert(AlertMessage.ErrorMessageTitle, error);
        }
      });
  }

  getSizeClicked() {
    const {
      subType,
      postalCode,
      floorNum,
      unitNum,
      possiblyNoUnit,
    } = this.state.xValuePO;
    console.log(this.state.xValuePO);
    if (
      subType &&
      postalCode &&
      (PropertyTypeUtil.isLanded(subType) ||
        possiblyNoUnit ||
        (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum)))
    ) {
      this.getPropertyInfo({
        propertySubType: subType,
        postalCode,
        floorNum,
        unitNum,
        possiblyNoUnit,
      });
    } else {
      if (subType <= 0) {
        //   AlertBox.showErrorAlert("Please select a property type");
      } else if (!PropertyTypeUtil.isLanded(subType)) {
        //   AlertBox.showErrorAlert("Please provide the unit number");
      }
    }
  }

  onUnitFloorNumEndEditing() {
    const {
      subType,
      postalCode,
      floorNum,
      unitNum,
      possiblyNoUnit,
    } = this.state.xValuePO;

    if (!ObjectUtil.isEmpty(floorNum) && !ObjectUtil.isEmpty(unitNum)) {
      this.getPropertyInfo({
        propertySubType: subType,
        postalCode,
        floorNum,
        unitNum,
        possiblyNoUnit,
      });
    }
  }

  viewXValueResult() {
    const {userPO} = this.props;
    const {xValuePO} = this.state;
    const {
      postalCode,
      unitNum,
      floorNum,
      possiblyNoUnit,
      subType,
      size,
      streetId,
      landType,
      tenure,
      lastConstructed,
    } = xValuePO;

    const error = {};

    if (
      (isNaN(postalCode) && ObjectUtil.isEmpty(postalCode)) ||
      postalCode <= 0
    ) {
      error.addressError = 'Required field';
    }
    if ((isNaN(subType) && ObjectUtil.isEmpty(subType)) || subType <= 0) {
      error.subTypeError = 'Required field';
    }
    if (
      !(possiblyNoUnit || PropertyTypeUtil.isLanded(subType)) &&
      ObjectUtil.isEmpty(floorNum) &&
      ObjectUtil.isEmpty(unitNum)
    ) {
      error.unitNumError = 'Required field';
    }
    if (!(size > 0)) {
      error.sizeError = 'Required field';
    }
    if (PropertyTypeUtil.isLanded(subType)) {
      console.log(landType);
      console.log(tenure);
      console.log(lastConstructed);
      if (ObjectUtil.isEmpty(landType)) {
        console.log('landtype empty');
        error.landTypeError = 'Required field';
      }
      if (ObjectUtil.isEmpty(tenure)) {
        error.tenureError = 'Required field';
      }
      if (lastConstructed <= 0) {
        error.lastConstructError = 'Required field';
      }
    }
    console.log(error);

    if (ObjectUtil.isEmpty(error)) {
      this.onGetXValueButtonPressed();
      if (!ObjectUtil.isEmpty(userPO)) {
        this.mobileVerifyXValue();
      } else {
        this.loadXValue();
      }
    }
    this.setState({error});
  }

  viewRecentXValueResult() {
    const {userPO} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      this.mobileVerifyXValue();
    } else {
      this.loadXValue();
    }
  }

  mobileVerifyXValue() {
    const {userPO} = this.props;
    if (userPO.mobileVerified === true) {
      //If mobile number is already verified,
      //no need to show verify mobile number screen
      this.loadXValue();
    } else {
      if (userPO.mobileLocalNum) {
        this.requestMobileVerification();
      } else {
        Alert.alert(
          AlertMessage.SuccessMessageTitle,
          'Mobile Number needs to be verified before proceeding',
          [
            {text: 'Cancel', style: 'cancel'},
            {
              text: 'OK',
              onPress: () => this.goToUpdateProfile(),
            },
          ],
        );
      }
    }
  }

  directToVerfiyMobileNumber = () => {
    const {userPO} = this.props;
    Navigation.showModal({
      stack: {
        children: [
          {
            component: {
              name: 'SideMainMenu.VerifyMobileNumber',
              passProps: {
                name: userPO.name,
                mobile: userPO.mobileLocalNum,
                updateXValueResult: this.updateXValueResult.bind(this),
              },
              options: {
                modalPresentationStyle: 'overFullScreen',
                topBar: {
                  visible: false,
                  drawBehind: true,
                },
              },
            },
          },
        ],
      },
    });
  };

  updateXValueResult = () => {
    const {userPO} = this.props;
    if (userPO.mobileVerified) {
      this.loadXValue();
    }
  };

  goToUpdateProfile = () => {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'SideMainMenu.UpdateProfile',
        passProps: {
          source: UpdateProfile.Sources.XValueForm,
          updateMobileVerification: this.updateMobileVerification,
        },
      },
    });
  };

  updateMobileVerification = () => {
    const {userPO} = this.props;
    if (userPO.mobileVerified) {
      this.loadXValue();
    }
  };

  loadXValue() {
    const {
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      subType,
      landType,
      size,
      streetId,
      gfa,
      pesSize,
      tenure,
      lastConstructed,
    } = this.state.xValuePO;
    GoogleAnalyticUtil.trackXValueRequest({parameters: this.state.xValuePO});

    var sizeInSqft = size;
    if (PropertyTypeUtil.isHDB(subType)) {
      sizeInSqft = CommonUtil.convertMetreToFeet(size);
    }
    XValueService.promotionGetXValue({
      type,
      postalCode,
      floorNum,
      unitNum,
      buildingNum,
      subType,
      landType,
      streetId,
      size: sizeInSqft,
      builtSizeGfa: gfa,
      pesSize,
      tenure,
      builtYear: lastConstructed,
    })
      .catch(error => {
        // AlertBox.showErrorAlert(error.message);
        // AlertBox.showErrorAlert("An error occurred unexpectedly. Please try again later.");
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            // AlertBox.showErrorAlert(error);
          } else {
            Navigation.push(this.props.componentId, {
              component: {
                name: 'xvalueStack.xvalueResult',
                passProps: {
                  xValuePO: this.state.xValuePO,
                  initialXValueResultPO: response,
                },
              },
            });
          }
        } else {
          // AlertBox.showErrorAlert("An error occurred unexpectedly. Please try again later.");
        }
      });
  }

  directToSRXValuation() {
    url = '/srx-valuations';
    domain = DebugUtil.retrieveStoreDomainURL();
    url = domain + url;

    Linking.canOpenURL(url).then(supported => {
      if (!supported) {
        // console.tron.log('Can\'t handle url: ' + url);
      } else {
        return Linking.openURL(url);
      }
    });
  }

  renderButtons() {
    const {subType} = this.state.xValuePO;
    if (PropertyTypeUtil.isCommercial(subType)) {
      return (
        <View style={{marginVertical: Spacing.M, alignItems: 'center'}}>
          <Button
            buttonType={Button.buttonTypes.primary}
            onPress={this.directToSRXValuation}>
            Get SRX Valuation Report
          </Button>
        </View>
      );
    } else {
      return (
        <View style={{marginVertical: Spacing.M, alignItems: 'center'}}>
          <Button
            buttonType={Button.buttonTypes.primary}
            onPress={this.viewXValueResult}>
            Get X-Value
          </Button>
        </View>
      );
    }
  }

  renderCommercialTypeDesc() {
    if (PropertyTypeUtil.isCommercial(this.state.xValuePO.subType)) {
      return (
        <Text style={{color: SRXColor.Red, marginTop: 8, fontSize: 13}}>
          Commercial & Industrial is only available through the purchase of a
          valuation.
        </Text>
      );
    }
  }

  renderSaleRentButton() {
    const {type} = this.state.xValuePO;
    const isSale = type == 'S';

    return (
      <View style={{flexDirection: 'row', marginVertical: Spacing.XS}}>
        <Button
          buttonStyle={[
            {flex: 1},
            styles.saleRentBtnStyle,
            isSale ? styles.saleRentBtnSelected : styles.saleRentBtnNormal,
          ]}
          textStyle={[
            styles.saleRentBtnTextStyle,
            isSale
              ? styles.saleRentBtnSelectedText
              : styles.saleRentBtnNormalText,
          ]}
          onPress={() => this.saleRentSelected('S')}>
          Sale Value
        </Button>
        <View style={{width: Spacing.M}} />
        <Button
          buttonStyle={[
            {flex: 1},
            styles.saleRentBtnStyle,
            isSale ? styles.saleRentBtnNormal : styles.saleRentBtnSelected,
          ]}
          textStyle={[
            styles.saleRentBtnTextStyle,
            isSale
              ? styles.saleRentBtnNormalText
              : styles.saleRentBtnSelectedText,
          ]}
          onPress={() => this.saleRentSelected('R')}>
          Rent Value
        </Button>
      </View>
    );
  }

  renderRecentSearches() {
    const {recentSearches} = this.state;
    if (!ObjectUtil.isEmpty(recentSearches) && Array.isArray(recentSearches)) {
      return (
        <View style={{marginTop: Spacing.S}}>
          <View style={{paddingHorizontal: 15}}>
            <Heading1 style={Styles.sectionTitle}>Recent Searches</Heading1>
          </View>
          {recentSearches.map((item, index) => (
            <TouchableHighlight
              key={index}
              onPress={() => this.recentSearchItemSelected(item)}>
              <View
                style={{
                  paddingHorizontal: Spacing.M,
                  backgroundColor: SRXColor.White,
                }}>
                <View style={styles.tableCellRow}>
                  <FeatherIcon
                    name="clock"
                    size={18}
                    color={SRXColor.Black}
                    style={{alignSelf: 'center', marginRight: 8}}
                  />
                  <Text style={styles.tableCellRowTitle} numberOfLines={1}>
                    {item.address}
                  </Text>
                  <FeatherIcon
                    name="chevron-right"
                    size={24}
                    color={SRXColor.Black}
                    style={{alignSelf: 'center'}}
                  />
                </View>
                {recentSearches.length > 1 ? <Separator /> : null}
              </View>
            </TouchableHighlight>
          ))}
        </View>
      );
    }
  }

  render() {
    const {type} = this.state.xValuePO;
    const isSale = type == 'S';

    return (
      <SafeAreaView style={{flex: 1}}>
        <KeyboardAwareScrollView
          style={{flex: 1}}
          keyboardShouldPersistTaps={'always'}
          onScroll={this.onScroll}>
          {this.renderLargeTitle('X-Value Calculator')}
          <View>
            <View style={styles.propertyInfoContainerStyle}>
              {this.renderSaleRentButton()}
              {this.renderAddress()}
              {this.renderSubtype()}
              {this.renderFloorNum()}
              {this.renderSize()}
              {this.renderTypeOfArea()}
              {this.renderTenure()}
              {this.renderBuiltSize()}
              {this.renderBuiltYear()}
              {this.renderButtons()}
              {this.renderCommercialTypeDesc()}
            </View>
            {this.renderRecentSearches()}
            <Text
              style={{
                fontSize: 12,
                marginLeft: 20,
                marginRight: 20,
                marginTop: 60,
                marginBottom: 15,
              }}>
              X-Value™ automated valuation serves as a guide for general
              reference. If you require a certified appraisal for any property
              sale, purchase, mortgage, accounting, internal transfer, etc.,
              please order a full inspection and valuation report{' '}
              <Text
                style={{color: SRXColor.Teal}}
                onPress={() => this.directToSRXValuation()}>
                HERE
              </Text>
              .
            </Text>
          </View>
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

const InputContainerStyles = {
  inputContainer: {
    flexDirection: 'column',
    paddingTop: Spacing.S,
    paddingBottom: Spacing.S,
  },
  inputTitleStyle: {
    fontSize: 15,
    lineHeight: 24,
    fontWeight: '600',
    color: SRXColor.Gray,
    flex: 1,
  },
  inputBoxStyle: {
    borderWidth: 1,
    borderColor: '#DCDCDC',
    // height: 40,
    backgroundColor: 'white',
    paddingRight: 12,
  },
  inputBoxContainerAlign: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'stretch',
  },
  inputTextStyle: {
    paddingLeft: 12,
  },
};

const styles = {
  propertyInfoContainerStyle: {
    marginTop: Spacing.XS,
    marginBottom: Spacing.M,
    paddingHorizontal: Spacing.M,
    paddingVertical: Spacing.XS,
    backgroundColor: 'white',
  },
  saleRentBtnStyle: {
    ...InputStyles.container,
    paddingHorizontal: Spacing.M,
    justifyContent: 'center',
    alignItems: 'center',
  },
  saleRentBtnTextStyle: {
    fontSize: 16,
    lineHeight: 40,
    textAlign: 'center',
  },
  saleRentBtnSelected: {
    backgroundColor: SRXColor.Teal,
  },
  saleRentBtnSelectedText: {
    color: 'white',
    fontWeight: '600',
  },
  saleRentBtnNormal: {
    ...InputStyles.containerBorder,
  },
  saleRentBtnNormalText: {
    color: SRXColor.Black,
  },
  inputContainer: {
    ...InputContainerStyles.inputContainer,
  },
  inputTitleStyle: {
    fontSize: 15,
    lineHeight: 24,
    fontWeight: '600',
    color: SRXColor.DarkBlue,
  },
  inputBoxStyle: {
    ...InputContainerStyles.inputBoxStyle,
    paddingLeft: 12,
  },
  tableCellRow: {
    flexDirection: 'row',
    // paddingLeft: 15,
    // paddingRight: 25,
    minHeight: 44,
    alignItems: 'center',
    backgroundColor: 'white',
  },
  tableCellRowTitle: {
    fontSize: 15,
    marginVertical: 8,
    color: SRXColor.Black,
    flex: 1,
  },
  sectionTitle: {
    marginVertical: 12,
    fontSize: 15,
    fontWeight: '600',
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    serverDomain: state.serverDomain.domainURL,
  };
};

export default connect(mapStateToProps)(XValueForm);
// export { XValueForm };
