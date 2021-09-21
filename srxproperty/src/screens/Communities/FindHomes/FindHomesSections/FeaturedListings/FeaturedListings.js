import React, {Component} from 'react';
import {View} from 'react-native';
import {SRXColor} from '../../../../../constants';
import {MainScreenService, SearchPropertyService} from '../../../../../services';
import {ObjectUtil, CommonUtil} from '../../../../../utils';
import {ListingPO} from '../../../../../dataObject';
import {FeaturedListingsPreviewList} from './FeaturedListingsPreviewList';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {Spacing} from '../../../../../styles';

const cdResearchSubtype_Residential = 83;
const cdResearchSubtype_Commercial = 82;

class FeaturedListings extends Component {
  static propTypes = {
    showListingDetails: PropTypes.func,
    onViewAllListing: PropTypes.func,
    isResidential: PropTypes.bool,
  };

  static defaultProps = {
    isResidential: true,
  };

  state = {
    featuredListingsList: [],
  };

  constructor(props) {
    super(props);

    this.showListingDetails = this.showListingDetails.bind(this);
    this.onViewAllListing = this.onViewAllListing.bind(this);
  }

  componentDidMount() {
    let cdResearchSubtype = cdResearchSubtype_Residential;
    this.loadFeaturedListings(cdResearchSubtype);
  }

  componentDidUpdate(prevProps) {
    //check residential
    if (prevProps.isResidential !== this.props.isResidential) {
      let cdResearchSubtype = cdResearchSubtype_Residential;
      const {isResidential} = this.props;
      if (isResidential === false) {
        cdResearchSubtype = cdResearchSubtype_Commercial;
      }
      this.loadFeaturedListings(cdResearchSubtype);
    }

    //login or not by checking UserPO
    if (
      prevProps.userPO !== this.props.userPO ||
      prevProps.serverDomain !== this.props.serverDomain
    ) {
      let cdResearchSubtype = cdResearchSubtype_Residential;
      const {isResidential} = this.props;
      if (isResidential === false) {
        cdResearchSubtype = cdResearchSubtype_Commercial;
      }
      this.loadFeaturedListings(cdResearchSubtype);
    }
  }

  //go to listing details
  showListingDetails = listingPO => {
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {showListingDetails} = this.props;
      if (showListingDetails) {
        showListingDetails(listingPO);
      }
    }
  };

  // on select on view all listings
  onViewAllListing() {
    const {onViewAllListing} = this.props;
    if (onViewAllListing) {
      onViewAllListing();
    }
  }

  //Call load featured listings api
  loadFeaturedListings = cdResearchSubtype => {
    SearchPropertyService.loadFeaturedListings({cdResearchSubtype})
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
              const {result} = response;
              if (!ObjectUtil.isEmpty(result)) {
                let featuredListingsList = [];
                result.map((item, index) => {
                  featuredListingsList.push(new ListingPO(item));
                });

                this.setState({
                  featuredListingsList: featuredListingsList,
                });
              } //end of if case
            }
          }
        } else {
          console.log(error);
        }
      });
  };

  renderFeaturedListings() {
    const {featuredListingsList} = this.state;
    const {isResidential, userPO} = this.props;
    // not login user and for residential => Featured Listings
    let title = 'Featured Listings';
    // for commercial => Commercial Space near you
    if (isResidential === false) {
      title = 'Commercial Space near you';
    }
    // login user and for residential => Recommended Listings
    if (isResidential && !ObjectUtil.isEmpty(userPO)) {
      title = 'Recommended Listings';
    }
    if (featuredListingsList.length > 0) {
      return (
        <FeaturedListingsPreviewList
          data={featuredListingsList}
          title={title}
          onViewAll={() => this.onViewAllListing()}
          onListingSelected={selectedListing =>
            this.showListingDetails({listingPO: selectedListing})
          }
        />
      );
    }
  }

  render() {
    return (
      <View
        style={{
          flex: 1,
          backgroundColor: SRXColor.White,
          borderRadius: 10,
          overflow: 'hidden',
          marginBottom: Spacing.S,
        }}>
        {this.renderFeaturedListings()}
      </View>
    );
  }
}

const mapStateToProps = state => {
  return {
    userPO: state.loginData.userPO,
    serverDomain: state.serverDomain.domainURL,
  };
};

export default connect(mapStateToProps)(FeaturedListings);
