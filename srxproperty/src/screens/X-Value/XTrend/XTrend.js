import React, {Component} from 'react';
import {ActivityIndicator, View, Text} from 'react-native';

import PropTypes from 'prop-types';
import {XValueTrendService} from './Services';
import {XValueService} from '../../../services';

import XTrendGraph from './XTrendGraph';
import {Transactions} from './Transactions';
import {ValueResult} from './Values';
import {SRXColor} from '../../../constants';
import {Separator} from '../../../components';
import {Spacing} from '../../../styles';
import {ObjectUtil, CommonUtil, StringUtil, XValueUtil} from '../../../utils';
import {XValueDisclaimer} from '../Result/Components';

class XTrend extends Component {
  static propTypes = {
    xTrendParameters: PropTypes.object.isRequired,
    showValues: PropTypes.bool,
    showGraph: PropTypes.bool,
    showTransactions: PropTypes.bool,
    /**
     * optional
     * to inform the super view whether this component is display successfully
     * returning with object passing back
     * {
     *   success: bool  //only true when at least 1 item is displayed
     * }
     */
    onTrendEndLoading: PropTypes.func,
    /**
     * Optional
     * if provided, wont call promotionGetXValueTrend or promotionGetXValueTrendForListing to load trenddata
     */
    preLoadedTrendData: PropTypes.array,

    /**
     * Optional
     * if true, value will be showing as $1,XXX,XXX
     */
    concealValue: PropTypes.bool,
  };

  static defaultProps = {
    showValues: true,
    showGraph: true,
    showTransactions: true,
  };

  state = {
    loadedXTrendData: false,
    loadedTransactionData: false,
    trendData: null,
    transactionData: null,
  };

  constructor(props) {
    super(props);

    if (!ObjectUtil.isEmpty(props.preLoadedTrendData)) {
      this.setState({trendData: props.preLoadedTrendData});
    }
  }

  componentDidMount() {
    const {xTrendParameters} = this.props;

    this.loadTrendData();
    this.loadTrendTransactions();
  }

  loadTrendData() {
    const {
      postalCode,
      cdResearchSubtype,
      unitFloor,
      unitNo,
      sizeInSqm,
      isSale,
      listingId,
      listingType,
      landType,
      buildingNum,
      streetId,
      builtSizeGfa,
      pesSize,
      tenure,
      builtYear,
    } = this.props.xTrendParameters;

    this.setState({trendData: 'loading'});
    if (listingId) {
      XValueTrendService.promotionGetXValueTrendForListing({
        listingType,
        listingId,
      })
        .then(res => {
          this.handleXTrendDataResponse(res);
        })
        .catch(error => {
          console.log(error);
          this.setState({trendData: [], loadedXTrendData: true});
        })
        .finally(() => {
          this.updateDisplayStatus();
        });
    } else {
      XValueService.promotionGetXValueTrend({
        type: isSale ? 'S' : 'R',
        postalCode,
        floorNum: unitFloor,
        unitNum: unitNo,
        buildingNum,
        subType: cdResearchSubtype,
        streetId,
        size: CommonUtil.convertMetreToFeet(sizeInSqm),
        builtSizeGfa,
        pesSize,
        tenure,
        builtYear,
        landType,
      })
        .then(res => {
          let newResult = XValueUtil.decryptXValueObj(res.xValueObjTrend);
          this.handleXTrendResponse(newResult);
        })
        .catch(error => {
          console.log(error);
          this.setState({trendData: [], loadedXTrendData: true});
          this.updateDisplayStatus();
        })
        .finally(() => {
          this.updateDisplayStatus();
        });
    }
  }

  loadTrendTransactions() {
    const {
      postalCode,
      cdResearchSubtype,
      unitFloor,
      unitNo,
      sizeInSqm,
      isSale,
      listingId,
      listingType,
    } = this.props.xTrendParameters;

    this.setState({transactionData: 'loading'});
    if (listingId) {
      XValueTrendService.loadXValueTrendTransactionForListing({
        postalCode,
        cdResearchSubtype,
        isSale,
        listingId,
        listingType,
      })
        .then(res => {
          this.handleXTrendTransactionResponse(res, isSale);
        })
        .catch(error => {
          console.log(error);
          this.setState({
            transactionData: [],
            loadedTransactionData: true,
          });
        })
        .finally(() => {
          this.updateDisplayStatus();
        });
    } else {
      XValueTrendService.loadXValueTrendTransaction({
        postalCode,
        cdResearchSubtype,
        unitFloor,
        unitNo,
        sizeInSqm,
        isSale,
      })
        .then(res => {
          this.handleXTrendTransactionResponse(res, isSale);
        })
        .catch(error => {
          console.log(error);
          this.setState({
            transactionData: [],
            loadedTransactionData: true,
          });
        })
        .finally(() => {
          this.updateDisplayStatus();
        });
    }
  }

  handleXTrendDataResponse(res) {
    var data = [];
    const {xValueTrend} = res;
    if (!ObjectUtil.isEmpty(xValueTrend) && Array.isArray(xValueTrend)) {
      data = xValueTrend.sort((a, b) => {
        return new Date(a.key) - new Date(b.key);
      });
      console.log(data);
    }

    this.setState({trendData: data, loadedXTrendData: true});
  }

  handleXTrendResponse(xValueTrend) {
    var data = [];
    if (!ObjectUtil.isEmpty(xValueTrend) && Array.isArray(xValueTrend)) {
      data = xValueTrend.sort((a, b) => {
        return new Date(a.key) - new Date(b.key);
      });
      console.log(data);
    }

    this.setState({trendData: data, loadedXTrendData: true});
  }

  handleXTrendTransactionResponse(res, isSale) {
    const {data, transactions} = res;
    // var trendData = [];
    var transactionData = [];

    // if (data && Array.isArray(data)) {
    //   trendData = data;
    // }

    if (!ObjectUtil.isEmpty(transactions)) {
      console.log('isSale - ' + isSale);
      if (isSale) {
        const {recentTransactionPO} = transactions;
        if (!ObjectUtil.isEmpty(recentTransactionPO)) {
          const {transactionList} = recentTransactionPO;
          if (!ObjectUtil.isEmpty(transactionList)) {
            transactionData = transactionList;
          }
        }
      } else {
        const {rentalOfficialTransactionPO} = transactions;
        if (!ObjectUtil.isEmpty(rentalOfficialTransactionPO)) {
          const {transactionList} = rentalOfficialTransactionPO;
          if (!ObjectUtil.isEmpty(transactionList)) {
            transactionData = transactionList;
          }
        }
      }
    }

    this.setState({
      // trendData: trendData,
      transactionData: transactionData,
      loadedTransactionData: true,
    });
  }

  updateDisplayStatus() {
    var displayed = false;

    const {
      showValues,
      showGraph,
      showTransactions,
      onTrendEndLoading,
    } = this.props;
    const {trendData, transactionData} = this.state;

    console.log('updateDisplayStatus');
    console.log(trendData);
    console.log(transactionData);

    if (!ObjectUtil.isEmpty(trendData)) {
      if (showValues) displayed = true;
      if (showGraph) displayed = true;
      if (ObjectUtil.isEmpty(transactionData)) {
        if (showTransactions) displayed = true;
      }
    }

    if (onTrendEndLoading) {
      onTrendEndLoading({success: displayed});
    }
  }

  renderValues() {
    const {showValues, concealValue} = this.props;
    const {trendData} = this.state;
    if (showValues && !ObjectUtil.isEmpty(trendData)) {
      return (
        <View>
          <ValueResult data={trendData} concealValue={concealValue} />
          <Separator />
        </View>
      );
    }
  }

  renderGraph() {
    const {showGraph, concealValue} = this.props;
    const {trendData} = this.state;
    if (showGraph && !ObjectUtil.isEmpty(trendData)) {
      return (
        <XTrendGraph xValueTrend={trendData} concealValue={concealValue} />
      );
    }
  }

  renderTransactions() {
    const {showTransactions} = this.props;
    const {transactionData} = this.state;
    if (showTransactions && !ObjectUtil.isEmpty(transactionData)) {
      return <Transactions data={transactionData} />;
    }
  }

  renderXValueDisclaimer() {
    return (
      <View
        style={{
          paddingLeft: Spacing.M,
          paddingRight: Spacing.M,
          marginTop: Spacing.M,
          marginBottom: Spacing.M,
        }}>
        <XValueDisclaimer />
      </View>
    );
  }

  render() {
    const {trendData, transactionData} = this.state;
    const {style} = this.props;

    if (trendData == 'loading') {
      return (
        <View
          style={[{marginLeft: 20, marginRight: 20, marginBottom: 15}, style]}>
          <View
            style={{
              height: 100,
              justifyContent: 'center',
              alignItems: 'center',
            }}>
            <ActivityIndicator />
          </View>
        </View>
      );
    } else if (Array.isArray(trendData) && trendData.length > 0) {
      return (
        <View style={[{paddingBottom: 15}, style]}>
          {this.renderValues()}
          {this.renderGraph()}
          {this.renderTransactions()}
          {this.renderXValueDisclaimer()}
        </View>
      );
    } else {
      return <View />;
    }
  }
}

export {XTrend};
