import React, {Component} from 'react';
import {View, Dimensions, ActivityIndicator, Platform} from 'react-native';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import {Spacing, Typography} from '../../../../styles';
import {SRXColor, IS_IOS} from '../../../../constants';
import {Heading2, Button, MaterialCommunityIcons} from '../../../../components';
import {NewLaunchesService} from '../../../../services';
import {ObjectUtil, CommonUtil, PropertySearchGridUtil} from '../../../../utils';
import {ProjectDetailPO} from '../../../../dataObject';
import {HotProjectsCardItem} from '../../../NewLaunches';

var {width} = Dimensions.get('window');

const LoadingState = {
  Normal: 'normal',
  Loading: 'loading',
  Loaded: 'loaded',
};
class HotProjects extends Component {
  constructor(props) {
    super(props);

    //state variables
    this.state = {
      loadingState: LoadingState.Normal,
      hotProjects: [],
      latestProjects: [],
      featuredProjects: [],
      randomValueArray: [],
    };

    this.onPressHotProjectItem = this.onPressHotProjectItem.bind(this);
    this.onPressViewAll = this.onPressViewAll.bind(this);
  }

  componentDidMount() {
    //get random arrays
    const {randomValueArray} = this.state;
    while (randomValueArray.length !== 4) {
      var randomNumber = Math.floor(Math.random() * 10);
      if (!randomValueArray.includes(randomNumber)) {
        randomValueArray.push(randomNumber);
      }
    }

    //call new launches for showing hot projects
    this.setState(
      {loadingState: LoadingState.Loading, randomValueArray},
      () => {
        this.loadNewProjectLaunches();
      },
    );
  }

  componentDidUpdate(prevProps) {
    const {serverDomain} = this.props;
    if (prevProps.serverDomain !== serverDomain) {
      const {randomValueArray} = this.state;
      while (randomValueArray.length !== 4) {
        var randomNumber = Math.floor(Math.random() * 10);
        if (!randomValueArray.includes(randomNumber)) {
          randomValueArray.push(randomNumber);
        }
      }
      this.setState(
        {loadingState: LoadingState.Loading, randomValueArray},
        () => {
          this.loadNewProjectLaunches();
        },
      );
    }
  }

  onPressHotProjectItem = projectDetailPO => {
    const {onPressHotProjectItem} = this.props;
    if (onPressHotProjectItem) {
      onPressHotProjectItem(projectDetailPO);
    }
  };

  onPressViewAll = () => {
    const {onPressViewAll} = this.props;
    const {latestProjects, featuredProjects} = this.state;
    if (onPressViewAll) {
      onPressViewAll(latestProjects, featuredProjects);
    }
  };

  //API Section
  loadNewProjectLaunches() {
    const {randomValueArray} = this.state;
    NewLaunchesService.loadNewProjectLaunches()
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
              //Const Variables
              const {hotNewProjects, featuredProjects} = response;

              if (!ObjectUtil.isEmpty(hotNewProjects)) {
                //ONLY showing FOUR hot projects in main screen
                const {srxProjectDetailsPOs} = hotNewProjects;
                let hotProjects = [];
                if (!ObjectUtil.isEmpty(srxProjectDetailsPOs)) {
                  if (Array.isArray(srxProjectDetailsPOs)) {
                    //Getting hot projects by random
                    if (
                      Array.isArray(randomValueArray) &&
                      randomValueArray.length === 4
                    ) {
                      randomValueArray.map((item, index) => {
                        var projectDetailsPO = srxProjectDetailsPOs[item]; //get by index;
                        if (!ObjectUtil.isEmpty(projectDetailsPO)) {
                          hotProjects.push(
                            new ProjectDetailPO(projectDetailsPO),
                          );
                        }
                      });
                    } else {
                      //If cannot generate random values
                      srxProjectDetailsPOs.map((item, index) => {
                        if (index < 4) {
                          hotProjects.push(new ProjectDetailPO(item));
                        } else {
                          //terminate loop if more than 4 items
                          return;
                        }
                      });
                    }
                  }
                }

                //store project lists in state
                this.setState({
                  hotProjects,
                  featuredProjects,
                  latestProjects: srxProjectDetailsPOs,
                  loadingState: LoadingState.Loaded, //done loading
                });
              } //end of hot new projects
            } //end of response
          }
        } else {
          console.log(error);
          this.setState({loadingState: LoadingState.Normal});
        }
      });
  }

  //hot project title and icons
  renderHotProjectTitleAndIcon() {
    return (
      <View style={{flex: 1, flexDirection: 'row'}}>
        <Heading2 style={{marginRight: Spacing.XS / 4}}>Hot Projects</Heading2>
        <MaterialCommunityIcons
          name={'fire'}
          size={IS_IOS ? 20 : 24}
          color={SRXColor.Red}
        />
      </View>
    );
  }

  renderHotProjectTitleAndViewAll() {
    return (
      <View style={Styles.titleContainer}>
        {this.renderHotProjectTitleAndIcon()}
        <Button
          textStyle={[Typography.SmallBody, {color: SRXColor.TextLink}]}
          onPress={this.onPressViewAll}>
          View all hot projects
        </Button>
      </View>
    );
  }

  renderHotProjectList() {
    const {hotProjects} = this.state;
    if (!ObjectUtil.isEmpty(hotProjects)) {
      if (Array.isArray(hotProjects) && hotProjects.length > 0) {
        //chunk hot projects by 2 items per row
        const chunkArray = PropertySearchGridUtil.chunkArray(hotProjects, 2);
        return (
          <View>
            {chunkArray.map((items, index) =>
              this.renderHotProjectItem(items, index),
            )}
          </View>
        );
      }
    }
  }

  renderHotProjectItem = (items, index) => {
    return (
      <View style={{flex: 1, flexDirection: 'row'}}>
        {/* divide screen width by 2 to show two hot projects per rows*/}
        {items.map((item, itemIndex) => (
          <HotProjectsCardItem
            key={itemIndex}
            containerStyle={{
              marginLeft: itemIndex % 2 !== 0 ? Spacing.M : 0,
              width: Math.floor((width - Spacing.M * 3) / 2), //remove margin M size
            }}
            projectDetailPO={item}
            onPressHotProjectItem={this.onPressHotProjectItem}
          />
        ))}
      </View>
    );
  };

  render() {
    //show indicator in Loading State
    //show hot projects in Loaded State
    //show no views in normal state
    const {loadingState} = this.state;
    //show lists after done calling apis
    if (loadingState === LoadingState.Loaded) {
      return (
        <View style={Styles.hotProjectsContainer}>
          {this.renderHotProjectTitleAndViewAll()}
          {this.renderHotProjectList()}
        </View>
      );
    } else if (loadingState === LoadingState.Loading) {
      return (
        <View style={[Styles.hotProjectsContainer, {height: 200}]}>
          {this.renderHotProjectTitleAndIcon()}
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

HotProjects.propTypes = {
  /** to direct to hot project web screen */
  onPressHotProjectItem: PropTypes.func,

  /** direct to new launches project lists */
  onPressViewAll: PropTypes.func,
};

const Styles = {
  hotProjectsContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    borderRadius: 10,
    padding: Spacing.M,
    marginBottom: Spacing.S,
  },
  titleContainer: {
    alignItems: 'center',
    flexDirection: 'row',
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
)(HotProjects);
