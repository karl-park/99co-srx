import React, {Component} from 'react';
import {View} from 'react-native';
import SafeAreaView from 'react-native-safe-area-view';
import {Navigation} from 'react-native-navigation';
import {AppTopBar_BackBtn} from "../../../../assets";
import {AppTheme} from "../../../../styles";

import {SearchOptionList} from '../OptionsComponent';
import {SearchPropertyService} from '../../../../services';
import {ObjectUtil, CommonUtil} from '../../../../utils';
import {SRXColor} from '../../../../constants';

class SchoolOption extends Component {
  static options(passProps) {
    return {
      topBar: {
        backButton: {
          icon: AppTopBar_BackBtn,
          color: AppTheme.topBarBackButtonColor,
          title: ""
        },
        title: {
          text: 'Search by Schools',
          // alignment: "center"
        },
      },
    };
  }

  constructor(props) {
    super(props);

    this.onSelectSchoolGroup = this.onSelectSchoolGroup.bind(this);
  }

  state = {
    schoolGroupObject: null,
  };

  componentDidMount() {
    this.getAmenitiesForProjectSearch();
  }

  getAmenitiesForProjectSearch() {
    SearchPropertyService.getAmenitiesForProjectSearch()
      .catch(error => {
        console.log(error);
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            console.log(error);
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              if (!ObjectUtil.isEmpty(response.result)) {
                this.setState({schoolGroupObject: response.result.groups[0]}); //for school
              }
            }
          }
        } else {
          console.log(
            'An error occurred unexpectedly. Please try again later.',
          );
        }
      });
  }

  onSelectSchoolGroup = item => {
    //pass search options to get the selected data
    const {onLocationSelected, location} = this.props;

    if (!ObjectUtil.isEmpty(item)) {
      if (!ObjectUtil.isEmpty(item.amenities)) {
        var rawData = item.amenities;
        //Grouped
        let data = rawData.reduce((r, e) => {
          let group = e.name[0];
          if (!r[group]) r[group] = {group, data: [e]};
          else r[group].data.push(e);
          return r;
        }, {});
        let result = Object.values(data);

        //Sorted
        result.sort((a, b) =>
          a.group.toLowerCase().localeCompare(b.group.toLowerCase()),
        );

        Navigation.push(this.props.componentId, {
          component: {
            name: 'PropertySearchStack.SchoolGroupList',
            passProps: {
              schoolListByGroup: result,
              onLocationSelected: onLocationSelected,
              location,
            },
          },
        });
      }
    }
    console.log(item);
  };

  //rendering methods to present
  renderSchoolsGroupList() {
    const {schoolGroupObject} = this.state;
    if (!ObjectUtil.isEmpty(schoolGroupObject)) {
      return (
        <View style={{ paddingBottom: 5}}>
          <SearchOptionList
            dataArray={schoolGroupObject.elements}
            onItemSelected={this.onSelectSchoolGroup}
            isSchoolOption={true}
          />
        </View>
      );
    }
  }

  render() {
    return (
      <SafeAreaView
        style={{flex: 1, backgroundColor: SRXColor.White}}
        forceInset={{bottom: 'never', top:"never"}}>
        {/* <SafeAreaView
          style={{flex: 1, backgroundColor: 'white'}}
          forceInset={{bottom: 'never'}}> */}
          {this.renderSchoolsGroupList()}
        {/* </SafeAreaView> */}
      </SafeAreaView>
    );
  }
}

export {SchoolOption};
