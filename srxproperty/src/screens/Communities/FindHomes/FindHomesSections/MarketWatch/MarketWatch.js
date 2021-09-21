import React, {Component} from 'react';
import {View, ActivityIndicator} from 'react-native';
import {Spacing} from '../../../../../styles';
import {SRXColor} from '../../../../../constants';
import Moment from 'moment';
import {connect} from 'react-redux';

import {
  SmallBodyText,
  BodyText,
  Heading2,
  FeatherIcon,
} from '../../../../../components';
import {MainScreenService} from '../../../../../services';
import {CommonUtil, ObjectUtil} from '../../../../../utils';
import {MarketWatchPO} from '../../../../../dataObject';
import {MarketWatchList} from './MarketWatchList';

const LoadingState = {
  Normal: 'normal',
  Loading: 'loading',
  Loaded: 'loaded',
};

class MarketWatch extends Component {
  constructor(props) {
    super(props);

    this.state = {
      loadingState: LoadingState.Normal,
      marketWatchList: [],
    };
  }

  componentDidMount() {
    this.setState({loadingState: LoadingState.Loading}, () => {
      this.loadMarketWatchIndices();
    });
  }

  componentDidUpdate(prevProps) {
    const {serverDomain} = this.props;
    if (prevProps.serverDomain !== serverDomain) {
      this.setState({loadingState: LoadingState.Loading}, () => {
        this.loadMarketWatchIndices();
      });
    }
  }

  //API Section
  loadMarketWatchIndices() {
    MainScreenService.loadMarketWatchIndices()
      .catch(error => {
        console.log(error);
        this.setState({loadingState: LoadingState.Normal});
      })
      .then(response => {
        if (!ObjectUtil.isEmpty(response)) {
          const error = CommonUtil.getErrorMessageFromSRXResponse(response);
          if (!ObjectUtil.isEmpty(error)) {
            console.log(error);
            this.setState({loadingState: LoadingState.Normal});
          } else {
            if (!ObjectUtil.isEmpty(response)) {
              const {
                hdb,
                hdbRent,
                nonLandedPrivate,
                nonLandedPrivateRent,
              } = response;
              let marketWatchList = [];

              //nonLandedPrivate Resale
              if (!ObjectUtil.isEmpty(nonLandedPrivate)) {
                var newNonLandedPrivateResale = {
                  ...nonLandedPrivate,
                  subType: 'Condo/Apt Resale',
                };
                marketWatchList.push(
                  new MarketWatchPO(newNonLandedPrivateResale),
                );
              }

              //HDB Resale
              if (!ObjectUtil.isEmpty(hdb)) {
                var newHDBResale = {
                  ...hdb,
                  subType: 'HDB Resale',
                };
                marketWatchList.push(new MarketWatchPO(newHDBResale));
              }

              //nonLandedPrivate Rent
              if (!ObjectUtil.isEmpty(nonLandedPrivateRent)) {
                var newNonLandedPrivateRent = {
                  ...nonLandedPrivateRent,
                  subType: 'Condo/Apt Rent',
                };
                marketWatchList.push(
                  new MarketWatchPO(newNonLandedPrivateRent),
                );
              }

              //HDB Rent
              if (!ObjectUtil.isEmpty(hdbRent)) {
                var newHDBRent = {
                  ...hdbRent,
                  subType: 'HDB Rent',
                };
                marketWatchList.push(new MarketWatchPO(newHDBRent));
              }

              this.setState({
                marketWatchList,
                loadingState: LoadingState.Loaded, //loaded
              });
            } //end of response
          }
        } else {
          console.log(error);
          this.setState({loadingState: LoadingState.Normal});
        }
      });
  }

  //Start Rendering methods
  renderMarketWatchTitle() {
    return (
      <View style={Styles.titleContainer}>
        <Heading2 style={{flex: 1, marginRight: Spacing.XS}}>
          Market Watch
        </Heading2>
        {this.renderLastUpdatedDate()}
      </View>
    );
  }

  renderLastUpdatedDate() {
    const {marketWatchList} = this.state;
    var marketWatchPO = marketWatchList[0];
    if (!ObjectUtil.isEmpty(marketWatchPO)) {
      if (!ObjectUtil.isEmpty(marketWatchPO.getLastUpdatedDate())) {
        let lastUpdateDate = marketWatchPO.getLastUpdatedDate();
        for (var i = 1; i < marketWatchList.length; i++) {
          let newMarketWatchPO = marketWatchList[i];
          if (!ObjectUtil.isEmpty(newMarketWatchPO)) {
            if (!ObjectUtil.isEmpty(newMarketWatchPO.getLastUpdatedDate())) {
              let newLastUpdateDate = newMarketWatchPO.getLastUpdatedDate();
              let isDateBefore = Moment(
                lastUpdateDate,
                'MMM YYYY',
              ).isSameOrBefore(Moment(newLastUpdateDate, 'MMM YYYY'));
              if (isDateBefore) {
                lastUpdateDate = newLastUpdateDate;
              }
            }
          }
        }

        return (
          <View style={{flexDirection: 'row', alignItems: 'center'}}>
            <FeatherIcon name="clock" size={16} color={SRXColor.Gray} />
            <SmallBodyText
              style={{fontStyle: 'italic', marginLeft: Spacing.XS / 2}}>
              Last updated: {lastUpdateDate}
            </SmallBodyText>
          </View>
        );
      }
    }
  }

  renderMarketWatchList() {
    const {marketWatchList} = this.state;
    if (!ObjectUtil.isEmpty(marketWatchList)) {
      if (Array.isArray(marketWatchList) && marketWatchList.length > 0) {
        return <MarketWatchList data={marketWatchList} />;
      }
    }
  }

  render() {
    //Loaded => show market watch po lists
    //Loading => show indicators
    //Normal => show nothing
    const {loadingState} = this.state;
    if (loadingState === LoadingState.Loaded) {
      return (
        <View style={Styles.marketWatchContainer}>
          {this.renderMarketWatchTitle()}
          {this.renderMarketWatchList()}
        </View>
      );
    } else if (loadingState === LoadingState.Loading) {
      return (
        <View style={[Styles.marketWatchContainer, {height: 150}]}>
          <Heading2 style={{flex: 1, marginRight: Spacing.XS}}>
            Market Watch
          </Heading2>
          <View style={Styles.activityIndicatorContainer}>
            <ActivityIndicator />
          </View>
        </View>
      );
    } else {
      return <View />;
    }
  }
}

const Styles = {
  marketWatchContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    borderTopLeftRadius: 10,
    borderTopRightRadius: 10,
    paddingTop: Spacing.M,
    paddingBottom: Spacing.S,
  },
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: Spacing.M,
    marginBottom: Spacing.S,
  },
  activityIndicatorContainer: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
};

const mapStateToProps = state => {
  return {
    serverDomain: state.serverDomain,
  };
};

export default connect(
  mapStateToProps,
  null,
)(MarketWatch);
