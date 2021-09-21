import React, {Component} from 'react';
import {View, TouchableOpacity, ActivityIndicator, Alert} from 'react-native';
import {ObjectUtil} from '../../../../../utils';
import {Spacing} from '../../../../../styles';
import {connect} from 'react-redux';
import {CertifiedListingService} from '../../../../../services';
import {loadPropertyTrackers} from '../../../../../actions';
import {SRXColor, LoadingState} from '../../../../../constants';
import PropTypes from 'prop-types';
import {Heading2, SmallBodyText, FeatherIcon} from '../../../../../components';
import {MyPropertyList} from '../MyPropertyAtAGlance';
import {Button} from '../../../../../components';
import {LoginStack} from '../../../../../config';

class MyPropertyAtAGlance extends Component {
  static propTypes = {
    onViewAllMyProperty: PropTypes.func,
    onAddPropertyPressed: PropTypes.func,
    directToMyProperty: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.onViewAllMyProperty = this.onViewAllMyProperty.bind(this);
    this.onAddPropertyPressed = this.onAddPropertyPressed.bind(this);
    this.directToMyProperty = this.directToMyProperty.bind(this);
  }

  // when press view all in your property at a glance section
  onViewAllMyProperty = () => {
    const {onViewAllMyProperty} = this.props;
    if (onViewAllMyProperty) {
      onViewAllMyProperty();
    }
  };

  //add property
  onAddPropertyPressed = () => {
    const {userPO, onAddPropertyPressed} = this.props;
    if (!ObjectUtil.isEmpty(userPO)) {
      if (onAddPropertyPressed) {
        onAddPropertyPressed();
      }
    } else {
      LoginStack.showSignInRegisterModal();
    }
  };

  // async componentDidUpdate(prevProps) {
  //   console.log('Check props');
  //   console.log(this.props);
  //   const {properties, loadingState} = this.props;
  //   if (
  //     prevProps.loadingState != loadingState &&
  //     loadingState === LoadingState.Loaded
  //   ) {
  //     if (!ObjectUtil.isEmpty(properties) && Array.isArray(properties)) {
  //       this.checkCertifiedListing(0);
  //     }
  //   }
  // }

  // checkCertifiedListing(index) {
  //   const {properties, directToMyProperty} = this.props;

  //   if (index < properties.length) {
  //     let srxPropertyUserPO = properties[index];
  //     if (
  //       !ObjectUtil.isEmpty(srxPropertyUserPO.ownerNric) &&
  //       !ObjectUtil.isEmpty(srxPropertyUserPO.dateOwnerNricVerified) &&
  //       directToMyProperty
  //     ) {
  //       CertifiedListingService.findCertifiedListings({
  //         ptUserId: properties[index].ptUserId,
  //       }).then(response => {
  //         if (!ObjectUtil.isEmpty(response)) {
  //           if (!ObjectUtil.isEmpty(response.result)) {
  //             let result = response.result;
  //             for (let j = 0; j < result.length; j++) {
  //               if (result[j].ownerCertifiedInd == false) {
  //                 Alert.alert(
  //                   'SRX',
  //                   'An agent has posted a listing for your unit. Do you want to certify it?',
  //                   [
  //                     {
  //                       text: 'No',
  //                       onPress: () => console.log('Cancel Pressed'),
  //                       style: 'cancel',
  //                     },
  //                     {
  //                       text: 'Yes',
  //                       onPress: () => {
  //                         directToMyProperty(srxPropertyUserPO, true);
  //                       },
  //                     },
  //                     {cancelable: true},
  //                   ],
  //                 );
  //                 return;
  //               }
  //             }
  //             return this.checkCertifiedListing(index + 1);
  //           } else {
  //             return this.checkCertifiedListing(index + 1);
  //           }
  //         } else {
  //           return;
  //         }
  //       });
  //     } else {
  //       return this.checkCertifiedListing(index + 1);
  //     }
  //   } else {
  //     return;
  //   }
  // }

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
      const {verifyOrCertifyPressed} = this.props;
      if (verifyOrCertifyPressed) {
        verifyOrCertifyPressed(srxPropertyUserPO);
      }
    }
  };

  renderHeader() {
    const {properties, loadingState} = this.props;
    return (
      <View style={Styles.headerContainer}>
        <Heading2>Your mySG Home properties</Heading2>
        {loadingState === LoadingState.Loaded ? (
          <TouchableOpacity
            style={{flexDirection: 'row', justifyContent: 'flex-end', flex: 1}}
            onPress={() => this.onViewAllMyProperty()}>
            <SmallBodyText style={{color: SRXColor.TextLink}}>
              {!ObjectUtil.isEmpty(properties) ? 'Manage' : null}
            </SmallBodyText>
          </TouchableOpacity>
        ) : null}
      </View>
    );
  }

  renderMyPropertyResultList() {
    const {properties, loadingState, loadPropertyTrackers} = this.props;
    if (
      loadingState === LoadingState.Loading &&
      ObjectUtil.isEmpty(properties)
    ) {
      return (
        <View style={{alignItems: 'center', padding: Spacing.M}}>
          <ActivityIndicator />
        </View>
      );
    }
    if (loadingState === LoadingState.Failed) {
      return (
        <View
          style={{
            padding: Spacing.M,
            alignItems: 'center',
          }}>
          <Button
            buttonStyle={{padding: Spacing.XS}}
            textStyle={{color: SRXColor.Teal}}
            rightView={
              <FeatherIcon
                name="rotate-cw"
                size={16}
                color={SRXColor.Teal}
                style={{marginLeft: Spacing.XS}}
              />
            }
            onPress={() => loadPropertyTrackers({populateXvalue: true})}>
            Reload
          </Button>
        </View>
      );
    } else if (Array.isArray(properties) && properties.length > 0) {
      return (
        <MyPropertyList
          srxPropertyUserPOs={properties}
          loadingState={loadingState}
          directToMyProperty={this.directToMyProperty}
          directToAddUpdateProperty={this.directToAddUpdateProperty}
        />
      );
    }
  }

  renderAddPropertyTextAndIcon() {
    const {properties, loadingState} = this.props;

    if (
      (ObjectUtil.isEmpty(properties) ||
        (Array.isArray(properties) && properties.length < 3)) &&
      (loadingState === LoadingState.Loaded ||
        loadingState === LoadingState.Normal)
    ) {
      return (
        <Button
          buttonStyle={{
            flex: 1,
            alignItems: 'center',
            justifyContent: 'center',
            marginVertical: Spacing.S,
          }}
          textStyle={{
            fontSize: 14,
            color: SRXColor.TextLink,
            paddingRight: Spacing.XS / 2,
          }}
          leftView={<FeatherIcon name="plus" size={20} color={'#8DABC4'} />}
          onPress={this.onAddPropertyPressed}>
          Add Property
        </Button>
      );
    }
  }

  render() {
    const {userPO} = this.props;

    return (
      <View style={Styles.container}>
        {this.renderHeader()}
        {this.renderMyPropertyResultList()}
        {this.renderAddPropertyTextAndIcon()}
      </View>
    );
  }
}

const Styles = {
  container: {
    backgroundColor: SRXColor.White,
    borderRadius: 10,
    overflow: 'hidden',
    padding: Spacing.M, //for property glance section
    marginBottom: Spacing.S,
  },
  headerContainer: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: SRXColor.White,
    alignItems: 'center',
    marginBottom: Spacing.S,
  },
};

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    properties: state.myPropertiesData.list,
    loadingState: state.myPropertiesData.loadingState,
  };
};

export default connect(
  mapStateToProps,
  {loadPropertyTrackers},
)(MyPropertyAtAGlance);
