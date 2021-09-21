import React, {Component} from 'react';
import {View, SafeAreaView, ActivityIndicator} from 'react-native';
import {Navigation} from 'react-native-navigation';
import PropTypes from 'prop-types';

import {AgentCvProfile} from '../AgentCvComponent';
import {ObjectUtil} from '../../../utils';
import {Spacing} from '../../../styles';
import {SRXColor} from '../../../constants';
import {BodyText, EnquirySheetSource, EnquirySheet} from '../../../components';
import {AgentPO, ListingResultPO} from '../../../dataObject';
import {AgentCvService} from '../../../services';
import {ListingSearchResultList} from '../../PropertySearch';

class AgentListingResult extends Component {
  static options(passProps) {
    return {
      topBar: {
        title: {
          text:
            passProps.type == 'S'
              ? 'Listings for Sale (' + passProps.agentPO.name + ')'
              : 'Listings for Rent (' + passProps.agentPO.name + ')',
        },
      },
      bottomTabs: {
        visible: false,
        animate: true,
        drawBehind: true,
      },
    };
  }

  static propTypes = {
    agentPO: PropTypes.instanceOf(AgentPO),
    agentUserId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    type: PropTypes.string,
  };

  static defaultProps = {
    agentPO: null,
    agentUserId: '',
    type: '',
  };

  state = {
    isLoading: true,
  };

  constructor(props) {
    super(props);

    this.onEnquiryButtonPressed = this.onEnquiryButtonPressed.bind(this);
    this.showEnquiryForm = this.showEnquiryForm.bind(this);
  }

  componentDidMount() {
    const {agentUserId, type} = this.props;
    this.findActiveAndTransactedListingsByUserId({agentUserId, type});
  }

  findActiveAndTransactedListingsByUserId = ({agentUserId, type}) => {
    if (agentUserId) {
      AgentCvService.findActiveAndTransactedListingsByUserId({
        userId: agentUserId,
      })
        .then(response => {
          const newActiveListings = [];
          let listingResultPO = null;

          if (!ObjectUtil.isEmpty(response)) {
            const {activeListings} = response;

            if (!ObjectUtil.isEmpty(activeListings)) {
              activeListings.map(item => {
                newActiveListings.push(new ListingResultPO(item));
              });

              if (type == 'S') {
                listingResultPO = newActiveListings.find(
                  item => item.type == 'sale',
                );
              } else {
                listingResultPO = newActiveListings.find(
                  item => item.type == 'rent',
                );
              }
            }

            this.setState({
              listingResultPO,
              isLoading: false,
            });
          }
        })
        .catch(error => {
          console.log(error);
        });
    }
  };

  onEnquiryButtonPressed(passProps) {
    const {listingPOs} = passProps;
    if (!ObjectUtil.isEmpty(listingPOs) && Array.isArray(listingPOs)) {
      if (listingPOs.length == 1) {
        const {onSuccess} = passProps;
        this.showEnquirySheet(listingPOs, onSuccess);
      } else {
        this.showEnquiryForm(passProps);
      }
    }
  }

  showEnquirySheet = (listingPOs, onSuccess) => {
    const passData = {
      enquiryCallerComponentId: this.props.componentId,
      onEnquirySubmitted: onSuccess,
      source: EnquirySheetSource.AgentListings,
      listingPO: listingPOs[0],
    };

    EnquirySheet.show(passData);
  };

  showEnquiryForm(passProps) {
    Navigation.push(this.props.componentId, {
      component: {
        name: 'PropertySearchStack.EnquiryForm',
        passProps,
      },
    });
  }

  //Start Rendering methods
  renderAgentCvProfile() {
    const {agentPO} = this.props;
    if (!ObjectUtil.isEmpty(agentPO)) {
      return <AgentCvProfile agentPO={agentPO} />;
    }
  }

  renderNoOfListingAndResultList() {
    const {isLoading, listingResultPO} = this.state;

    if (!isLoading && !ObjectUtil.isEmpty(listingResultPO)) {
      return (
        <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
          {this.renderNoOfListingProperties()}
          {this.renderResultList()}
        </SafeAreaView>
      );
    } else {
      return (
        <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
          <ActivityIndicator />
        </View>
      );
    }
  }

  renderNoOfListingProperties() {
    const {type} = this.props;
    const {listingResultPO} = this.state;
    if (!ObjectUtil.isEmpty(listingResultPO) && listingResultPO.total > 0) {
      return (
        <View style={Styles.noOfListingPropertiesContainer}>
          <BodyText>
            {listingResultPO.total}{' '}
            {listingResultPO.total > 1 ? 'properties' : 'property'}
          </BodyText>
        </View>
      );
    }
  }

  renderResultList() {
    const {listingResultPO, isLoading} = this.state;
    if (!ObjectUtil.isEmpty(listingResultPO) && listingResultPO.total > 0) {
      const {listingPOs} = this.state.listingResultPO;
      const sections = [
        {
          key: 'General',
          data: listingPOs,
        },
      ];
      return (
        <ListingSearchResultList
          componentId={this.props.componentId}
          sections={sections}
          isLoading={isLoading}
          onEnquiryButtonPressed={this.onEnquiryButtonPressed}
          source={ListingSearchResultList.Sources.AgentCV}
        />
      );
    }
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1, backgroundColor: SRXColor.White}}>
        {this.renderAgentCvProfile()}
        {this.renderNoOfListingAndResultList()}
      </SafeAreaView>
    );
  }
}

const Styles = {
  noOfListingPropertiesContainer: {
    flexDirection: 'row',
    paddingHorizontal: Spacing.M,
    minHeight: 45,
    backgroundColor: SRXColor.White,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
    alignItems: 'center',
  },
};

export {AgentListingResult};
