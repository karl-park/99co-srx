import React, {Component} from 'react';
import {View, SectionList, ScrollView, TouchableHighlight} from 'react-native';
import PropTypes from 'prop-types';
import {FeatherIcon, BodyText, TextInput} from '../../../../../components';
import {Spacing, TopBarStyle} from '../../../../../styles';
import {SRXColor, IS_IOS} from '../../../../../constants';
import {Navigation} from 'react-native-navigation';
import {ObjectUtil, SetUtil} from '../../../../../utils';
import {GroupedSearchOptionList} from '../../../../PropertySearch/SearchOptions/OptionsComponent';
import {CommunitySubzonesList} from './CommunitySubzonesList';

class CommunitySelection extends Component {
  state = {
    searchQuery: '',
    selectedCommunityList: new Set(),
    activeSections: [],
    all_Community: [],
    communitiesArray: this.props.communitiesArray
      ? this.props.communitiesArray
      : [],
  };

  static options(passProps) {
    return {
      topBar: {
        title: {
          text: 'Planning areas/Subzones',
        },
        visible: true,
        animate: true,
      },
    };
  }

  constructor(props) {
    super(props);

    Navigation.events().bindComponent(this);

    this.setNavigationOptions();
    this.renderHeader = this.renderHeader.bind(this);
    this.renderContent = this.renderContent.bind(this);
  }

  setNavigationOptions() {
    const {componentId, action} = this.props;
    return new Promise(function(resolve, reject) {
      FeatherIcon.getImageSource('x', 25, 'blue')
        .then(icon_close => {
          Navigation.mergeOptions(componentId, {
            topBar: {
              leftButtons: [
                {
                  id: 'btn_close',
                  icon: icon_close,
                },
              ],
              rightButtons: [
                {
                  id: 'btn_Done',
                  text: 'Done',
                  color: SRXColor.TextLink,
                  enabled: true,
                },
              ],
            },
          });
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    }); //end of promise
  }

  navigationButtonPressed({buttonId}) {
    const {onSelectCommunities} = this.props;
    const {selectedCommunityList, communitiesArray} = this.state;
    if (buttonId === 'btn_Done') {
      var returnIdArray = Array.from(selectedCommunityList);
      var returnCommunityArray = [];
      var returnCommunityData = null;

      for (let k = 0; k < returnIdArray.length; k++) {
        communitiesArray.map(communityItem => {
          for (let j = 0; j < communityItem.children.length; j++) {
            if (communityItem.children[j].community.id === returnIdArray[k]) {
              //need all data
              returnCommunityData = {
                id: communityItem.children[j].community.id,
                cdCommunityLevel:
                  communityItem.children[j].community.cdCommunityLevel,
                membersTotal: communityItem.children[j].community.membersTotal,
                name: communityItem.children[j].community.name,
                value: communityItem.children[j].community.id,
                geoJson: communityItem.children[j].community.geoJson,
              };
            }
          }
        });
        returnCommunityArray.push(returnCommunityData);
      }

      if (onSelectCommunities) {
        onSelectCommunities(returnCommunityArray);
      }

      Navigation.dismissModal('CommunitySelection');
    } else if (buttonId === 'btn_close') {
      Navigation.dismissModal('CommunitySelection');
    }
  }

  componentDidMount() {
    const {selectedCommunities} = this.props;
    this.getCommunities();
    //initial select
    if (Array.isArray(selectedCommunities) && selectedCommunities.length > 0) {
      this.setState({selectedCommunityList: new Set(selectedCommunities)});
    }
  }

  getCommunities = () => {
    const {communitiesArray} = this.state;
    if (
      !ObjectUtil.isEmpty(communitiesArray) &&
      Array.isArray(communitiesArray)
    ) {
      var newSec = null;
      var sectionArray = [];

      communitiesArray.map((communityItem, index) => {
        var communityId = new Set();
        var communityArray = [];
        for (let i = 0; i < communityItem.children.length; i++) {
          communityId.add(communityItem.children[i].community.id);
          var newCommunity = {
            key: communityItem.children[i].community.name,
            value: communityItem.children[i].community.id,
          };
          communityArray.push(newCommunity);
        }

        newSec = {
          index: index,
          key: communityItem.community.name,
          value: communityItem.community.id,
          data: [
            {
              key: 'All subzones in ' + communityItem.community.name,
              value: communityId,
              data: communityArray,
            },
          ],
        };

        sectionArray.push(newSec);
      });
      this.setState({
        all_Community: sectionArray,
      });
    }
  };

  searchBarUpdated({text, isFocused}) {
    let trimmedText = '';
    if (text) {
      trimmedText = text.trim();
    }

    if (!isFocused) {
      //onEndEditing
      //clean up text
      this.setState({searchQuery: trimmedText});
    } else {
      //onchangeText

      //do auto complete here
      if (!ObjectUtil.isEmpty(trimmedText) && trimmedText.length > 2) {
        this.setState({searchQuery: text}, () => this.searchByQuery(text));
      } else {
        if (!ObjectUtil.isEmpty(trimmedText)) {
          this.setState({searchQuery: text, all_Community: []});
        } else {
          this.setState({searchQuery: text}, () => this.getCommunities());
        }
      }
    }
  }

  searchByQuery(text) {
    const {communitiesArray} = this.state;
    var newSection = null;
    let newSectionArray = [];
    var newCommunities = [];
    var newCommunityData = null;

    communitiesArray.map((communityItem, index) => {
      if (
        communityItem.community.name.toLowerCase().includes(text.toLowerCase())
      ) {
        newCommunityData = {
          children: communityItem.children,
          community: communityItem.community,
          ptUserId: communityItem.ptUserId,
          showSubzonesHeader: true,
        };
        newCommunities.push(newCommunityData);
      }
    });

    communitiesArray.map((communityItem, index) => {
      var newChildrenItem = communityItem.children.filter(childrenItem =>
        childrenItem.community.name.toLowerCase().includes(text.toLowerCase()),
      );

      newCommunityData = {
        children: newChildrenItem,
        community: communityItem.community,
        ptUserId: communityItem.ptUserId,
        showSubzonesHeader: false,
      };

      if (newChildrenItem.length > 0 && newCommunityData.children.length > 0) {
        newCommunities.push(newCommunityData);
      }
    });

    newCommunities.map((communityItem, index) => {
      var communityId = new Set();
      var communityArray = [];
      for (let i = 0; i < communityItem.children.length; i++) {
        communityId.add(communityItem.children[i].community.id);
        var newCommunity = {
          key: communityItem.children[i].community.name,
          value: communityItem.children[i].community.id,
        };
        communityArray.push(newCommunity);
      }

      newSection = {
        index: index,
        showSubzonesHeader: communityItem.showSubzonesHeader,
        value: communityItem.community.id,
        data: [
          {
            name: 'All subzones in ' + communityItem.community.name,
            value: communityId,
            data: communityArray,
          },
        ],
      };

      newSectionArray.push(newSection);
    });

    this.setState({all_Community: newSectionArray});
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

  onSelectCommunity = section => {
    const {selectedCommunityList} = this.state;
    if (SetUtil.isSuperset(selectedCommunityList, section.value)) {
      //UnCheck all
      this.setState({
        selectedCommunityList: SetUtil.difference(
          selectedCommunityList,
          section.value,
        ),
      });
    } else {
      //Check all
      this.setState({
        selectedCommunityList: SetUtil.union(
          selectedCommunityList,
          section.value,
        ),
      });
    }
  };

  onSelectCommunityItem = (index, item, section) => {
    const {selectedCommunityList} = this.state;
    if (!ObjectUtil.isEmpty(item)) {
      if (selectedCommunityList.has(item.value)) {
        selectedCommunityList.delete(item.value);
      } else {
        selectedCommunityList.add(item.value);
      }
      this.setState({selectedCommunityList});
    }
  };

  renderSearchBar() {
    const {searchQuery} = this.state;
    return (
      <View style={[TopBarStyle.topBar, {backgroundColor: SRXColor.White}]}>
        <TextInput
          ref={component => (this.searchBar = component)}
          style={[
            {
              flex: 1,
              borderRadius: 20,
              height: 40,
              color: SRXColor.Black,
            },
          ]}
          autoCorrect={false}
          placeholder={'Enter or choose planning area/subzone'}
          placeholderTextColor={SRXColor.Black}
          value={searchQuery}
          clearButtonMode="while-editing"
          underlineColorAndroid="transparent"
          returnKeyType={'search'}
          onChangeText={text =>
            this.searchBarUpdated({
              text,
              isFocused: this.searchBar.isFocused(),
            })
          }
          onEndEditing={() =>
            this.searchBarUpdated({text: searchQuery, isFocused: false})
          }
        />
      </View>
    );
  }

  renderHeader = ({section}) => {
    return (
      <TouchableHighlight
        style={[
          Styles.sectionHeaderStyle,
          {
            borderBottomWidth: IS_IOS
              ? null
              : this.state.activeSections.includes(section.value)
              ? 0
              : 1,
            borderBottomLeftRadius: this.state.activeSections.includes(
              section.value,
            )
              ? 0
              : 10,
            borderBottomRightRadius: this.state.activeSections.includes(
              section.value,
            )
              ? 0
              : 10,
            marginBottom: this.state.activeSections.includes(section.value)
              ? null
              : Spacing.XS,
          },
        ]}
        underlayColor={SRXColor.White}
        onPress={() => this.onPressSectionHeader(section)}>
        <View style={{flex: 1}}>
          <View style={Styles.itemContainerStyle}>
            <View style={{flex: 1, flexDirection: 'row', alignItems: 'center'}}>
              <BodyText style={{marginVertical: 8}}>{section.key}</BodyText>
            </View>
            <FeatherIcon
              name={
                this.isActiveSection(section) ? 'chevron-down' : 'chevron-up'
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
    const {selectedCommunityList, activeSections, searchQuery} = this.state;

    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.value)) {
        if (!ObjectUtil.isEmpty(section.data)) {
          return (
            <ScrollView index={index}>
              <View style={{overflow: 'hidden'}}>
                <View style={Styles.sectionContentStyle}>
                  <GroupedSearchOptionList
                    dataArray={section.data}
                    selectedArray={selectedCommunityList}
                    onHeaderSelected={this.onSelectCommunity}
                    onItemSelected={this.onSelectCommunityItem}
                    source={GroupedSearchOptionList.Sources.CommunitySelection}
                    searchbyQuery={
                      !ObjectUtil.isEmpty(searchQuery) ? true : false
                    }
                  />
                </View>
              </View>
            </ScrollView>
          );
        }
      }
    }
  };

  //Community List
  renderCommunityList() {
    const {all_Community, selectedCommunityList, searchQuery} = this.state;

    if (!ObjectUtil.isEmpty(all_Community)) {
      if (!ObjectUtil.isEmpty(searchQuery)) {
        return (
          <CommunitySubzonesList
            communitySubzoneArray={all_Community}
            selectedArray={selectedCommunityList}
            onItemSelected={this.onSelectCommunityItem}
            onHeaderSelected={this.onSelectCommunity}
          />
        );
      } else {
        return (
          <SectionList
            sections={all_Community}
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
  }

  render() {
    return (
      <View style={{flex: 1}}>
        <View
          style={{
            padding: Spacing.XS,
            backgroundColor: SRXColor.White,
          }}>
          {this.renderSearchBar()}
        </View>
        {this.renderCommunityList()}
      </View>
    );
  }
}

const Styles = {
  itemContainerStyle: {
    paddingLeft: Spacing.M,
    paddingRight: Spacing.M,
    minHeight: 45,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
  },
  sectionHeaderStyle: {
    marginTop: Spacing.XS / 2,
    borderTopWidth: IS_IOS ? null : 1,
    borderLeftWidth: IS_IOS ? null : 1,
    borderRightWidth: IS_IOS ? null : 1,
    borderColor: IS_IOS ? null : '#e0e0e0',
    borderTopLeftRadius: 10,
    borderTopRightRadius: 10,
    backgroundColor: SRXColor.White,
    //Shadow for iOS
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.4,
    shadowRadius: 4,
  },
  sectionContentStyle: {
    borderBottomWidth: IS_IOS ? null : 1,
    borderLeftWidth: IS_IOS ? null : 1,
    borderRightWidth: IS_IOS ? null : 1,
    borderColor: IS_IOS ? null : '#e0e0e0',
    borderBottomLeftRadius: 10,
    borderBottomRightRadius: 10,
    backgroundColor: SRXColor.White,
    marginBottom: Spacing.XS,
    shadowColor: 'rgb(110,129,154)',
    shadowOffset: {width: 0, height: 1},
    shadowOpacity: 0.4,
    shadowRadius: 4,
  },
};

CommunitySelection.propTypes = {
  onSelectCommunities: PropTypes.func,

  selectedCommunities: PropTypes.array,
};

export {CommunitySelection};
