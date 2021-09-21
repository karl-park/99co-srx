import React, {Component} from 'react';
import {View, ScrollView, SectionList} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';

import {FeatherIcon, BodyText} from '../../../../components';
import {ObjectUtil, UserUtil} from '../../../../utils';
import {Styles} from '../Styles';
import {SRXColor} from '../../../../constants';
import {CheckboxStyles, Spacing} from '../../../../styles';
import {LocationSearchOptions} from '../../Location';
import {GroupedSearchOptionList} from '../OptionsComponent';

class SchoolGroupList extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text: 'Search by Schools',
          // alignment: "center"
        },
        rightButtons: [
          {
            id: 'btn_search',
            text: 'Search',
          },
        ],
      },
    };
  }

  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);
    this.renderHeader = this.renderHeader.bind(this);
    this.renderContent = this.renderContent.bind(this);
  }

  state = {
    selectedSchoolDescList: [],
    selectedSchoolList: new Set(),
  };

  navigationButtonPressed({buttonId}) {
    if (buttonId === 'btn_search') {
      this.searchListingResultBySchoolList();
    }
  }

  searchListingResultBySchoolList() {
    const {selectedSchoolList, selectedSchoolDescList} = this.state;
    const {onLocationSelected} = this.props;

    const returningItem = {
      displayText: selectedSchoolDescList.toString(),
      searchText: null,
      selectedAmenitiesIds: Array.from(selectedSchoolList).toString(),
      selectedDistrictIds: null,
      selectedHdbTownIds: null,
      locationType: LocationSearchOptions.school,
    };

    if (onLocationSelected) {
      onLocationSelected({
        addressData: returningItem,
        updateRecentSearches: true,
      });
    }
  }

  componentDidMount() {
    const {location} = this.props;
    //get selected school ids
    //this method is unsafe as it might consist value of other amenities
    if (!ObjectUtil.isEmpty(location)) {
      if (location.selectedAmenitiesIds) {
        this.setState({
          selectedSchoolList: location.selectedAmenitiesIds
            .split(',')
            .map(Number), //change to number array
        });
      }
    }
  }

  //rendering methods to present
  renderSchoolsListByGroup() {
    const {schoolListByGroup} = this.props;
    var sectionArray = [];
    var newSec = null;
    for (var i = 0; i < schoolListByGroup.length; i++) {
      var stationId = new Set();
      var dataArray = [];
      for (var j = 0; j < schoolListByGroup[i].data.length; j++) {
        stationId.add(schoolListByGroup[i].data[j].id);
        var newData = {
          key: schoolListByGroup[i].data[j].name,
          value: schoolListByGroup[i].data[j].id,
        };
        dataArray.push(newData);
      }

      newSec = {
        index: i,
        group: schoolListByGroup[i].group,
        data: [{value: stationId, data: dataArray}],
      };
      sectionArray.push(newSec);
    }

    if (!ObjectUtil.isEmpty(sectionArray) && sectionArray.length > 0) {
      return (
        <SectionList
          sections={sectionArray}
          stickySectionHeadersEnabled={false}
          renderItem={({index, item, section}) =>
            this.renderContent({index, item, section})
          }
          renderSectionHeader={({section}) => this.renderHeader({section})}
          SectionSeparatorComponent={() => {
            return <View />;
          }}
          keyExtractor={(item, index) => item + index}
        />
      );
    }
  }

  renderContent = ({index, item, section}) => {
    const {selectedSchoolList} = this.state;
    if (!ObjectUtil.isEmpty(section)) {
      if (!ObjectUtil.isEmpty(section.data)) {
        return (
          <View
            style={{
              backgroundColor:
                section.index % 2 == 0
                  ? SRXColor.White
                  : SRXColor.AccordionBackground,
            }}
            index={index}>
            <GroupedSearchOptionList
              dataArray={section.data}
              selectedArray={selectedSchoolList}
              onItemSelected={this.onSelectEachSchool}
              source={GroupedSearchOptionList.Sources.SchoolGroupList}
            />
          </View>
        );
      }
    }
  };

  renderHeader = ({section}) => {
    return (
      <View
        style={[
          Styles.itemContainerStyle,
          {
            backgroundColor:
              section.index % 2 == 0
                ? SRXColor.White
                : SRXColor.AccordionBackground,
          },
        ]}>
        <View style={{flex: 1}}>
          <BodyText style={{marginVertical: Spacing.XS, fontWeight: '600'}}>
            {section.group}
          </BodyText>
        </View>
      </View>
    );
  };

  isSchoolSelected = item => {
    const {selectedSchoolList} = this.state;
    let isSelected = false;
    if (!ObjectUtil.isEmpty(item)) {
      if (selectedSchoolList.includes(item.id)) {
        isSelected = true;
      }
    }
    return isSelected;
  };

  onSelectEachSchool = (index, item, section) => {
    const {selectedSchoolList, selectedSchoolDescList} = this.state;
    if (!ObjectUtil.isEmpty(item)) {
      if (selectedSchoolList.has(item.value)) {
        selectedSchoolList.delete(item.value);
        selectedSchoolDescList.splice(
          selectedSchoolDescList.indexOf(item.key),
          1,
        );
      } else {
        selectedSchoolList.add(item.value);
        selectedSchoolDescList.push(item.key);
      }
      this.setState({selectedSchoolList, selectedSchoolDescList});
    }
  };

  render() {
    return (
      <SafeAreaView
        style={{flex: 1, backgroundColor: SRXColor.White}}
        forceInset={{bottom: 'never',top:"never"}}>
          <ScrollView style={{flex: 1}}>
            {this.renderSchoolsListByGroup()}
          </ScrollView>
      </SafeAreaView>
    );
  }
}

export {SchoolGroupList};
