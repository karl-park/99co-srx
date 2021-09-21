import React, {Component} from 'react';
import {Platform, Dimensions} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import SafeAreaView from 'react-native-safe-area-view';

import {ObjectUtil, CollectionConversionUtil, SetUtil} from '../../../utils';
import {SearchOptionsStruct} from '../Manager';
import {ListSearch, MapSearch} from './content';
import {SearchType} from './PropertySearchConstant';
import {FloatingShortcutItem} from './components';
import {GridSearch} from './content/Grid';
import {PropertyTypeValueSet} from '../Constants';

const isIOS = Platform.OS === 'ios';

var {height, width} = Dimensions.get('window');

class PropertySearchResult extends Component {
  static options(passProps) {
    console.log(passProps);
    const {disabledSearchModification, title} = passProps;
    console.log(disabledSearchModification);
    if (disabledSearchModification) {
      return {
        topBar: {
          title: {
            text: title || '',
          },
        },
        bottomTabs: {
          visible: false,
          animate: true,
          drawBehind: true,
        },
      };
    } else {
      return {
        topBar: {
          title: {
            component: {
              id: 'thisPageHeader',
              name: 'PropertySearchResult.TopBar',
            },
          },
          visible: true,
          animate: true,
          backButton: {
            visible: false,
          },
          noBorder: true,
          elevation: 0, //for android
        },
        statusBar: {
          hideWithTopBar: false,
        },
        bottomTabs: {
          visible: false,
          animate: true,
          drawBehind: true,
        },
      };
    }
  }

  static defaultProps = {
    disabledSearchModification: false,
  };

  constructor(props) {
    super(props);

    this.renderMapSearchContent = this.renderMapSearchContent.bind(this);
    this.onSearchTypeSelected = this.onSearchTypeSelected.bind(this);
    this.onFilterPressed = this.onFilterPressed.bind(this);
    this.onSelectFilterOptions = this.onSelectFilterOptions.bind(this);
    this.onTransactedSwitchValueChange = this.onTransactedSwitchValueChange.bind(
      this,
    );
    this.updateSearchBar = this.updateSearchBar.bind(this);

    const {initialSearchOptions, disabledSearchModification} = props;

    this.state = {
      selectedFilterOptionsCount: null,

      searchType: SearchType.List,
      searchOptions: initialSearchOptions,
      isTransacted:
        (!ObjectUtil.isEmpty(initialSearchOptions) &&
          initialSearchOptions.isTransacted) ||
        false,
    };

    if (!disabledSearchModification) {
      this.updateSearchBar();
    }
  }

  componentDidMount() {
    // this.updateSearchBar();
  }

  updateSearchBar() {
    Navigation.mergeOptions(this.props.componentId, {
      topBar: {
        title: {
          component: {
            id: 'thisPageHeader',
            name: 'PropertySearchResult.TopBar',
            passProps: {
              onSearchTypeSelected: this.onSearchTypeSelected,
              onBackPressed: this.onPressBackBtn,
              onLocationSelected: this.onLocationSelected,
              initialLocation: this.state.searchOptions,
              passRef: ref => {
                this._searchResultTopBar = ref;
              },
            },
          },
        },
      },
    });
  }

  //Event lists
  onPressBackBtn = () => {
    //Go Back to home screen page
    Navigation.popToRoot('HomeScreenId');
  };

  onLocationSelected = (addressData, type) => {
    const {searchOptions} = this.state;
    var newSearchOptions = {
      ...searchOptions,
    };
    if (type) {
      newSearchOptions = {
        ...searchOptions,
        type,
      };
    }
    this.setState(
      {
        searchOptions: {
          ...newSearchOptions,
          ...addressData,
        },
      },
      () => {
        this._searchResultTopBar.updateSearchBar(this.state.searchOptions);
      },
    );
  };

  onFilterPressed = () => {
    const {searchOptions} = this.state;
    Navigation.showModal({
      stack: {
        id: 'PropertyFilterOptions',
        children: [
          {
            component: {
              name: 'PropertySearchStack.PropertyFilterOptions',
              options: {
                modalPresentationStyle: 'overFullScreen',
              },
              passProps: {
                filterOptions: this.onSelectFilterOptions.bind(this),
                getSelectedFilterOptionsCount: this.getSelectedFilterOptionsCount.bind(
                  this,
                ),
                searchOptions: searchOptions,
              },
            },
          },
        ],
      },
    });
  };

  //Another props methods
  onSelectFilterOptions = filterOptions => {
    if (!ObjectUtil.isEmpty(filterOptions)) {
      const {searchOptions} = this.state;
      this.setState({
        searchOptions: {
          ...searchOptions,
          ...filterOptions,
        },
      });
    }
  };

  onTransactedSwitchValueChange = value => {
    this.setState({isTransacted: value});
  };

  getSelectedFilterOptionsCount = selectedFilterOptionsCount => {
    this.setState({selectedFilterOptionsCount});
  };

  onSearchTypeSelected(type) {
    if (type === SearchType.List) {
      this.setState({searchType: SearchType.List}, () => {
        // this.scrollView.scrollTo({ x: 0, y: 0, animated: false });
      });
      // Navigation.mergeOptions(this.props.componentId, {
      //   bottomTabs: {
      //     visible: true,
      //     animate: true,
      //     drawBehind: false
      //   }
      // });
    } else {
      this.setState({searchType: SearchType.Map}, () => {
        // this.scrollView.scrollTo({ x: width, y: 0, animated: false });
      });
      // Navigation.mergeOptions(this.props.componentId, {
      //   bottomTabs: {
      //     visible: false,
      //     animate: true,
      //     drawBehind: true
      //   }
      // });
    }
  }

  //change views by clicking shortcuts to update search type
  onToggleSearchTypeViews = initSearchType => {
    this.setState({searchType: initSearchType});
  };

  //reset Search options
  onResetSearchOptions = () => {
    const {initialSearchOptions, disabledSearchModification} = this.props;
    //Even user reset search options, need to put back initial cdResearchSubstype
    if (!ObjectUtil.isEmpty(initialSearchOptions)) {
      let initalCdResearchSubTypeSet = CollectionConversionUtil.convertValueStringToSet(
        initialSearchOptions.cdResearchSubTypes,
      );

      let propertyType = null;
      if (
        SetUtil.isSuperset(
          PropertyTypeValueSet.commercial,
          initalCdResearchSubTypeSet,
        )
      ) {
        propertyType = PropertyTypeValueSet.commercial;
      } else {
        propertyType = PropertyTypeValueSet.allResidential;
      }

      var newSearchOptions = {
        type: initialSearchOptions.type,
        displayText: 'Everywhere in Singapore',
        cdResearchSubTypes: Array.from(propertyType).join(','),
      };

      this.setState(
        {searchOptions: newSearchOptions, selectedFilterOptionsCount: null},
        () => {
          if (!disabledSearchModification) {
            this.updateSearchBar();
          }
        },
      );
    } //end of initial searchOptions
  };

  renderGridSearchContent() {
    const {
      searchOptions,
      isTransacted,
      selectedFilterOptionsCount,
    } = this.state;
    const {
      disabledSearchModification,
      disableGoogleAnalytic,
      directorySource,
    } = this.props;
    return (
      <GridSearch
        componentId={this.props.componentId}
        onFilterPressed={this.onFilterPressed}
        onSmartFilterSelected={this.onSelectFilterOptions}
        onTransactedSwitchValueChange={this.onTransactedSwitchValueChange}
        isTransacted={isTransacted}
        searchOptions={searchOptions}
        selectedFilterOptionsCount={selectedFilterOptionsCount}
        onResetSearchOptions={this.onResetSearchOptions} //reset search
        disabledSearchModification={disabledSearchModification}
        disableGoogleAnalytic={disableGoogleAnalytic}
        directorySource={directorySource}
      />
    );
  }

  renderListSearchContent() {
    const {
      searchOptions,
      isTransacted,
      selectedFilterOptionsCount,
    } = this.state;
    const {
      disabledSearchModification,
      disableGoogleAnalytic,
      source,
    } = this.props;
    return (
      <ListSearch
        componentId={this.props.componentId}
        onFilterPressed={this.onFilterPressed}
        onSmartFilterSelected={this.onSelectFilterOptions}
        onTransactedSwitchValueChange={this.onTransactedSwitchValueChange}
        isTransacted={isTransacted}
        searchOptions={searchOptions}
        selectedFilterOptionsCount={selectedFilterOptionsCount}
        onResetSearchOptions={this.onResetSearchOptions} //reset search
        disabledSearchModification={disabledSearchModification}
        disableGoogleAnalytic={disableGoogleAnalytic}
        source={source}
      />
    );
  }

  renderMapSearchContent() {
    const {searchOptions, isTransacted} = this.state;
    const {disabledSearchModification, disableGoogleAnalytic} = this.props;
    return (
      <MapSearch
        componentId={this.props.componentId}
        onFilterPressed={this.onFilterPressed}
        onTransactedSwitchValueChange={this.onTransactedSwitchValueChange}
        isTransacted={isTransacted}
        searchOptions={searchOptions}
        disabledSearchModification={disabledSearchModification}
        disableGoogleAnalytic={disableGoogleAnalytic}
      />
    );
  }

  renderContent() {
    const {searchType} = this.state;
    if (searchType === SearchType.Grid) {
      //grid view
      return this.renderGridSearchContent();
    } else if (searchType === SearchType.Map) {
      //map view
      return this.renderMapSearchContent();
    } else {
      //list view
      return this.renderListSearchContent();
    }
  }

  //floating short cut to toggle listview, gridview and mapview
  renderFloatingShortcutButton() {
    const {searchType} = this.state;
    return (
      <FloatingShortcutItem
        searchType={searchType}
        onToggleSearchTypeViews={this.onToggleSearchTypeViews}
      />
    );
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}} forceInset={{bottom: 'never'}}>
        {this.renderContent()}
        {this.renderFloatingShortcutButton()}
      </SafeAreaView>
    );
  }
}

PropertySearchResult.propTypes = {
  initialSearchOptions: SearchOptionsStruct,
  /**
   * (Optional)
   * For display purpose with initialSearchOptions,
   * not allow to filter/sort the search
   * by default is false
   */
  disabledSearchModification: PropTypes.bool,
  /**
   * Title of the screen, only showing if disabledSearchModification=true
   */
  title: PropTypes.string,

  disableGoogleAnalytic: PropTypes.bool,
  source: PropTypes.string,
  directorySource: PropTypes.string,
};

const mapStateToProps = state => {
  return {shortlistData: state.shortlistData};
};

export default connect(mapStateToProps)(PropertySearchResult);
