import React, {Component} from 'react';
import {View, FlatList} from 'react-native';
import {ObjectUtil} from '../../../../../utils';
import {MyPropertyListItem1} from '../../../../MyProperties/components/MyPropertyListItem1';
import {SRXPropertyUserPO} from '../../../../../dataObject';
import PropTypes from 'prop-types';

class MyPropertyList extends Component {
  static propTypes = {
    srxPropertyUserPOs: PropTypes.arrayOf(
      PropTypes.instanceOf(SRXPropertyUserPO),
    ),
    loadingState: PropTypes.string,
    directToMyProperty: PropTypes.func,
    directToManageProperty: PropTypes.func,
  };

  static defaultProps = {
    srxPropertyUserPOs: [],
  };

  constructor(props) {
    super(props);

    this.directToMyProperty = this.directToMyProperty.bind(this);
  }

  // go to property details
  directToMyProperty = (srxPropertyUserPO, isCertifyListing) => {
    if (!ObjectUtil.isEmpty(srxPropertyUserPO)) {
      const {directToMyProperty} = this.props;
      if (directToMyProperty) {
        directToMyProperty(srxPropertyUserPO, isCertifyListing);
      }
    }
  };

  directToAddUpdateProperty = srxPropertyUserPO => {
    if (!ObjectUtil.isEmpty(srxPropertyUserPO)) {
      const {directToAddUpdateProperty} = this.props;
      if (directToAddUpdateProperty) {
        directToAddUpdateProperty(srxPropertyUserPO);
      }
    }
  };

  renderListingItem = ({item, index}) => {
    const {loadingState} = this.props;
    return (
      <MyPropertyListItem1
        key={index}
        srxPropertyUserPO={item}
        loadingState={loadingState}
        directToMyProperty={this.directToMyProperty}
        directToAddUpdateProperty={this.directToAddUpdateProperty}
      />
    );
  };

  renderResultList() {
    const {srxPropertyUserPOs} = this.props;
    if (!ObjectUtil.isEmpty(srxPropertyUserPOs)) {
      return (
        <FlatList
          style={{flex: 1}}
          data={srxPropertyUserPOs}
          extraData={this.props}
          keyExtractor={item => item.key}
          renderItem={({item, index}) => this.renderListingItem({item, index})}
        />
      );
    }
  }

  render() {
    return <View style={{flex: 1}}>{this.renderResultList()}</View>;
  }
}

export {MyPropertyList};
