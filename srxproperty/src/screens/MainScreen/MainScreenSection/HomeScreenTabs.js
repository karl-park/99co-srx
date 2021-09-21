import React, {Component} from 'react';
import {View, StyleSheet, TouchableOpacity} from 'react-native';
import {Spacing} from '../../../styles';
import {
  Heading2,
  SmallBodyText,
  LottieView,
  BodyText,
} from '../../../components';
import {SRXColor} from '../../../constants';
import {ObjectUtil, StringUtil} from '../../../utils';
import PropTypes from 'prop-types';

const homeScreenTabList = [
  {msg: 'Find Homes', id: 0},
  // {msg: 'Home Value', id: 1},
  {msg: 'Community', id: 2},
  // {msg: 'Services', id: 3},
];
class HomeScreenTabs extends Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedTab: props.selectedTab,
    };
  }

  componentDidUpdate(prevProps) {
    if (prevProps.selectedTab !== this.props.selectedTab) {
      if (this.props.selectedTab !== this.state.selectedTab) {
        this.setState({selectedTab: this.props.selectedTab});
      }
    }
  }

  onClickTab(id) {
    const {updateTabButton} = this.props;
    if (updateTabButton) {
      updateTabButton(id);
    }
    this.setState({selectedTab: id});
  }

  renderTabButton(text, id, index) {
    const {selectedTab} = this.state;
    if (id === selectedTab) {
      return (
        <TouchableOpacity
          onPress={null}
          style={{flexDirection: 'row', flex: 1,borderBottomWidth: 4,
          borderBottomColor: SRXColor.Teal}}>
          <BodyText
            style={[
              styles.selectedButtonStyle,
              index === 0 ? styles.leftButtonStyle : styles.normalButtonStyle,
            ]}>
            {text}
          </BodyText>
          
            {index === homeScreenTabList.length - 1 ? null : (
              <View
                style={{
                  height: 15,
                  width: 1,
                  backgroundColor: "#DDDDDD",
                  // alignSelf:"center"  
                }}
              />
            )}
        </TouchableOpacity>
      );
    } else {
      return (
        <TouchableOpacity
          onPress={() => {
            this.onClickTab(id);
          }}
          style={{
            flexDirection: 'row',
            flex: 1,
            borderBottomWidth: 4,
            borderBottomColor: "#DDDDDD",
          }}>
          <BodyText
            style={[
              styles.unselectedButtonStyle,
              index === 0 ? styles.leftButtonStyle : styles.normalButtonStyle,
            ]}>
            {text}
          </BodyText>
          
            {index === homeScreenTabList.length - 1 ? null : (
              <View
                style={{
                  height: 15,
                  width: 1,
                  backgroundColor: "#DDDDDD",
                  // alignSelf:"center"
                }}
              />
            )}
        </TouchableOpacity>
      );
    }
  }

  render() {
    const {selectedTab} = this.state;
    return (
      <View style={{backgroundColor: SRXColor.White, height: 45}}>
        <View style={styles.tabContainer}>
          {homeScreenTabList.map((item, index) => {
            return this.renderTabButton(item.msg, item.id, index);
          })}
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  tabContainer: {
    flex: 1,
    backgroundColor: SRXColor.White,
    flexDirection: 'row',
    paddingHorizontal: Spacing.M,
    paddingTop: Spacing.M,
  },
  selectedButtonStyle: {
    flex: 1,
    // paddingBottom: Spacing.XS,
    // borderBottomWidth: 3,
    // borderBottomColor: SRXColor.Teal,
    color: SRXColor.Teal,
    fontSize: 16,
  },
  leftButtonStyle: {
    textAlign: 'center',
  },
  normalButtonStyle: {
    textAlign: 'center',
  },
  unselectedButtonStyle: {
    flex: 1,
    // paddingBottom: Spacing.XS,
    color: SRXColor.Black,
    fontSize: 16,
  },
});

HomeScreenTabs.propTypes = {
  selectedTab: PropTypes.number,
  updateTabButton: PropTypes.func,
};
//the selectedTab allows parent update children feature

HomeScreenTabs.defaultProps = {
  selectedTab: 0,
};
export default HomeScreenTabs;
