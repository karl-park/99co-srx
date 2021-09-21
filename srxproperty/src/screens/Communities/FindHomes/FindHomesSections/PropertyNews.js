import React, {Component} from 'react';
import {
  View,
  Dimensions,
  ActivityIndicator,
  TouchableOpacity,
} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {Spacing} from '../../../../styles';
import {SRXColor} from '../../../../constants';
import {SmallBodyText} from '../../../../components';
import {NewsPropertyService} from '../../../../services';
import {ObjectUtil, CommonUtil} from '../../../../utils';
import {OnlineCommunicationPO} from '../../../../dataObject';
import {Channel_Constants} from '../../../PropertyNews/Constants';
import {NewsListItem3, NewsListItem4} from '../../../PropertyNews/NewsItems';

var {height, width} = Dimensions.get('window');

const LoadingState = {
  Normal: 'normal',
  Loading: 'loading',
  Loaded: 'loaded',
};
class PropertyNews extends Component {
  static propTypes = {
    showNewsDetails: PropTypes.func,
    directToPropertyNews: PropTypes.func,
  };

  constructor(props) {
    super(props);

    //state variables
    this.state = {
      loadingState: LoadingState.Normal,
      newsList: [],
    };

    this.showNewsDetails = this.showNewsDetails.bind(this);
  }

  newsSearchOption = {defaultCategory: ''};

  componentDidMount() {
    this.setState({loadingState: LoadingState.Loading}, () => {
      this.loadPropertyNewLists();
    });
  }

  componentDidUpdate(prevProps) {
    const {serverDomain} = this.props;
    if (prevProps.serverDomain !== serverDomain) {
      this.setState({loadingState: LoadingState.Loading}, () => {
        this.loadPropertyNewLists();
      });
    }
  }

  //API Section
  loadPropertyNewLists() {
    NewsPropertyService.loadOnlineCommunicationsWithOptionalSearch({
      channel: Channel_Constants.propertyNews,
      limit: 3,
      page: 1,
    })
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
              const {result} = response;
              if (!ObjectUtil.isEmpty(result)) {
                const allNewsList = [];
                result.map(item => {
                  allNewsList.push(new OnlineCommunicationPO(item));
                });
                if (!ObjectUtil.isEmpty(allNewsList)) {
                  this.setState({
                    newsList: allNewsList,
                    loadingState: LoadingState.Loaded,
                  });
                }
              }
            } //end of response
          }
        } else {
          console.log(error);
          this.setState({loadingState: LoadingState.Normal});
        }
      });
  }

  showNewsDetails = (onlineCommunicationPO, index) => {
    if (!ObjectUtil.isEmpty(onlineCommunicationPO)) {
      //from home screen
      const {showNewsDetails} = this.props;
      if (showNewsDetails) {
        showNewsDetails(
          onlineCommunicationPO,
          index,
          this.newsSearchOption,
          'All Categories',
        );
      }
    }
  };

  //go to property news home screen
  directToPropertyNews = () => {
    const {directToPropertyNews} = this.props;
    if (directToPropertyNews) {
      directToPropertyNews();
    }
  };

  renderPropertyNews() {
    const {newsList} = this.state;
    return (
      <View>
        {newsList.map((item, index) => this.renderNewsItem(item, index))}
      </View>
    );
  }

  renderNewsItem = (item, index) => {
    const {newsList} = this.state;
    if (index < 1) {
      // NewsListItem3 => big url image, news title and content short
      return (
        <NewsListItem3
          onlineCommunicationPO={item}
          indexNo={index}
          showNewsDetails={this.showNewsDetails}
          key={index}
        />
      );
    } else {
      return (
        //NewsListItem4 => small url image and news title
        <NewsListItem4
          onlineCommunicationPO={item}
          indexNo={index}
          showNewsDetails={this.showNewsDetails}
          key={index}
        />
      );
    }
  };

  render() {
    //show indicator in Loading State
    //show property news in Loaded State
    //show no views in normal state
    const {loadingState} = this.state;
    //show lists after done calling apis
    if (loadingState === LoadingState.Loaded) {
      return (
        <View style={Styles.propertyNewsContainer}>
          {this.renderPropertyNews()}
          <TouchableOpacity
            style={{alignItems: 'center'}}
            onPress={() => this.directToPropertyNews()}>
            <SmallBodyText
              style={{color: SRXColor.TextLink, marginTop: Spacing.XS / 2}}>
              View All News
            </SmallBodyText>
          </TouchableOpacity>
        </View>
      );
    } else if (loadingState === LoadingState.Loading) {
      return (
        <View style={[Styles.propertyNewsContainer, {height: 200}]}>
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
  propertyNewsContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    padding: Spacing.M,
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
)(PropertyNews);
