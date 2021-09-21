import React, {Component} from 'react';
import {
  View,
  Image,
  TouchableHighlight,
  ScrollView,
  SectionList,
  ActivityIndicator,
} from 'react-native';
import {AppTopBar_BackBtn} from "../../../../assets";
import {AppTheme} from "../../../../styles";
import {Navigation} from 'react-native-navigation';
import SafeAreaView from 'react-native-safe-area-view';

import {ObjectUtil, UserUtil, SetUtil} from '../../../../utils';
import {LocationSearchService} from '../../../../services';
import {FeatherIcon, BodyText, Separator} from '../../../../components';
import {PropertySearch_MRT} from '../../../../assets';
import {SRXColor, MRTs} from '../../../../constants';
import {Styles} from '../Styles';
import {GroupedSearchOptionList} from '../OptionsComponent';
import {LocationSearchOptions} from '../../Location';

class MRTSearchOption extends Component {
  static options(passProps) {
    return {
      topBar: {
        backButton: {
          icon: AppTopBar_BackBtn,
          color: AppTheme.topBarBackButtonColor,
          title: ""
        },
        title: {
          text: 'Search by MRTs',
          // alignment: "center"
        },
        rightButtons: [
          {
            id: 'btn_district_search',
            text: 'Search',
          },
        ],
      },
    };
  }

  state = {
    selectedMrtList: new Set(),
    activeSections: [],
    all_MRT: [],
    isLoading: true,
  };

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.renderHeader = this.renderHeader.bind(this);
    this.renderContent = this.renderContent.bind(this);
  }

  componentDidMount() {
    LocationSearchService.railTransitInformation().then(response => {
      if (!ObjectUtil.isEmpty(response)) {
        const {railTransitInformation} = response;
        if (
          !ObjectUtil.isEmpty(railTransitInformation) &&
          Array.isArray(railTransitInformation)
        ) {
          var newSec = null;
          var sectionArray = [];

          railTransitInformation.map((railTransitInfo, index) => {
            var stationId = new Set();
            var stationArray = [];
            for (let i = 0; i < railTransitInfo.stations.length; i++) {
              stationId.add(railTransitInfo.stations[i].amenityId);
              var newStation = {
                key: railTransitInfo.stations[i].stationName,
                value: railTransitInfo.stations[i].amenityId,
              };
              stationArray.push(newStation);
            }

            newSec = {
              index: index,
              key: railTransitInfo.lineName,
              value: railTransitInfo.lineCode,
              colorCode: railTransitInfo.hexColor,
              data: [
                {
                  key: 'All ' + railTransitInfo.lineName + ' MRT Stations',
                  value: stationId,
                  data: stationArray,
                },
              ],
            };

            sectionArray.push(newSec);
          });
          this.setState({all_MRT: sectionArray, isLoading: false});
        }
      }
    });
  }

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_district_search') {
      this.searchListingResultByMRT();
    }
  }

  searchListingResultByMRT() {
    const {selectedMrtList} = this.state;
    const {onLocationSelected} = this.props;

    var recentSearchText = '';
    selectedMrtList.forEach(value => {
      recentSearchText =
        recentSearchText + MRTs.getMrtDescription(value) + ', ';
    });

    const returningItem = {
      displayText: recentSearchText.substring(0, recentSearchText.length - 2),
      searchText: null,
      selectedAmenitiesIds: Array.from(selectedMrtList).toString(),
      selectedDistrictIds: null,
      selectedHdbTownIds: null,
      locationType: LocationSearchOptions.mrt,
    };

    if (onLocationSelected) {
      onLocationSelected({
        addressData: returningItem,
        updateRecentSearches: true,
      });
    }
  }

  isActiveSection = item => {
    const {activeSections} = this.state;
    let isActive = false;
    if (!ObjectUtil.isEmpty(item)) {
      if (activeSections.includes(item.value)) {
        isActive = true;
      }
    }
    return isActive;
  };

  onPressSectionHeader = section => {
    const {activeSections} = this.state;
    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.value)) {
        activeSections.splice(activeSections.indexOf(section.value), 1);
      } else {
        activeSections.push(section.value);
      }
      this.setState({activeSections});
    }
  };

  onSelectMRTLine = section => {
    const {selectedMrtList} = this.state;
    if (SetUtil.isSuperset(selectedMrtList, section.value)) {
      //UnCheck all
      this.setState({
        selectedMrtList: SetUtil.difference(selectedMrtList, section.value),
      });
    } else {
      //Check all
      this.setState({
        selectedMrtList: SetUtil.union(selectedMrtList, section.value),
      });
    }
  };

  onSelectMRTStation = (index, item, section) => {
    const {selectedMrtList} = this.state;
    if (!ObjectUtil.isEmpty(item)) {
      if (selectedMrtList.has(item.value)) {
        selectedMrtList.delete(item.value);
      } else {
        selectedMrtList.add(item.value);
      }
      this.setState({selectedMrtList});
    }
  };

  //Show MRT Line as headers
  renderHeader = ({section}) => {
    return (
      <TouchableHighlight
        style={{
          backgroundColor:
            section.index % 2 == 0 ? 'white' : SRXColor.AccordionBackground,
        }}
        underlayColor={SRXColor.AccordionBackground}
        onPress={() => this.onPressSectionHeader(section)}>
        <View style={{flex: 1}}>
          {/* {this.renderSpace({ section })} */}
          <View style={Styles.itemContainerStyle}>
            <View style={{flex: 1, flexDirection: 'row', alignItems: 'center'}}>
              <Image
                source={PropertySearch_MRT}
                resizeMode={'contain'}
                style={[Styles.mrtImageStyle, {tintColor: section.colorCode}]}
              />
              <BodyText style={{marginVertical: 8}}>{section.key}</BodyText>
            </View>
            <FeatherIcon
              name={
                this.isActiveSection(section) ? 'chevron-up' : 'chevron-down'
              }
              size={20}
              color={'black'}
            />
          </View>
        </View>
      </TouchableHighlight>
    );
  };

  renderContent = ({index, item, section}) => {
    const {selectedMrtList, activeSections} = this.state;

    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.value)) {
        if (!ObjectUtil.isEmpty(section.data)) {
          return (
            <ScrollView
              style={{
                backgroundColor:
                  index % 2 == 0 ? 'white' : SRXColor.AccordionBackground,
              }}
              index={index}>
              <GroupedSearchOptionList
                dataArray={section.data}
                selectedArray={selectedMrtList}
                onHeaderSelected={this.onSelectMRTLine}
                onItemSelected={this.onSelectMRTStation}
              />
            </ScrollView>
          );
        }
      }
    }
  };

  //MRT List
  renderMRTList() {
    const {all_MRT, isLoading} = this.state;
    if (isLoading) {
      return (
        <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
          <ActivityIndicator />
        </View>
      );
    } else {
      if (!ObjectUtil.isEmpty(all_MRT)) {
        return (
          <SectionList
            sections={all_MRT}
            stickySectionHeadersEnabled={false}
            renderItem={({index, item, section}) =>
              this.renderContent({index, item, section})
            }
            renderSectionHeader={({section}) => this.renderHeader({section})}
            SectionSeparatorComponent={() => {
              return <Separator />;
            }}
            keyExtractor={(item, index) => item + index}
          />
        );
      }
    }
  }

  renderSpace({section}) {
    if (!ObjectUtil.isEmpty(section)) {
      if (section.value === 'BP') {
        return (
          <View
            style={{
              height: 55,
              backgroundColor: SRXColor.White,
              borderBottomWidth: 1,
              borderBottomColor: '#e0e0e0',
            }}
          />
        );
      }
    }
  }

  render() {
    return (
      <SafeAreaView
        style={{flex: 1, backgroundColor: SRXColor.White}}
        forceInset={{bottom: 'never',top:"never"}}>
        {/* <SafeAreaView
          style={{flex: 1, backgroundColor: 'white'}}
          forceInset={{bottom: 'never'}}> */}
          <View style={{flex: 1}}>{this.renderMRTList()}</View>
        {/* </SafeAreaView> */}
      </SafeAreaView>
    );
  }
}
export {MRTSearchOption};
