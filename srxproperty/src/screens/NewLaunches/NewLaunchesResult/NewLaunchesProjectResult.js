import React, { Component } from "react";
import { View, SafeAreaView } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import PropTypes from "prop-types";

import { Button, FeatherIcon, LargeTitleComponent } from "../../../components";
import { NewLaunchesResultList } from "../NewLaunchesList";
import { Typography } from "../../../styles";
import { Styles } from "../Styles";
import { ProjectDetailPO } from "../../../dataObject";
import { ObjectUtil } from "../../../utils";

class NewLaunchesProjectResult extends LargeTitleComponent {
  constructor(props) {
    super(props);
  }

  state = {
    projectList: []
  };

  componentDidMount() {
    const { newLaunchLatestProjects, newLaunchFeaturedProjects } = this.props;
    const { Primary, Secondary } = newLaunchFeaturedProjects;

    //Latest projects
    let latestProjects = [];
    newLaunchLatestProjects.map((item, index) => {
      latestProjects.push(new ProjectDetailPO(item));
    });

    //change featured project to ProjectDetailPo
    let featuredProjectList = [];
    Primary.map((item, index) => {
      featuredProjectList.push(new ProjectDetailPO(item));
    });
    Secondary.map((item, index) => {
      featuredProjectList.push(new ProjectDetailPO(item));
    });

    let newProjectList = [...featuredProjectList, ...latestProjects];
    this.setState({ projectList: newProjectList });
  }

  onSelectFilterOption = () => {
    console.log("Show Modal");
  };

  //Start redenering methods for Presentation
  renderFilterAndSortBy() {
    return (
      <View style={Styles.filterAndSortContainer}>
        <View style={Styles.subContainer}>
          <Button
            textStyle={[Typography.SmallBody, Styles.blueColorText]}
            leftView={<FeatherIcon name={"filter"} size={20} color={"black"} />}
            onPress={() => this.onSelectFilterOption()}
          >
            Filter results
          </Button>
        </View>
        {/* <View style={[Styles.subContainer, { justifyContent: "flex-end" }]}>
                    <SmallBodyText>Sort by</SmallBodyText>
                    <Picker
                        mode={"dialog"}
                        prompt={"Sort by"}
                        inputStyle={[Typography.SmallBody, Styles.blueColorText, { flex: 0 }]}
                        data={SortByOptions.SORT_OPTION_ARRAY}
                        selectedValue={selectedSortOption}
                        titleOfItem={(item, itemIndex, componentIndex) => item.key}
                        valueOfItem={(item, itemIndex, componentIndex) => item.value}
                        useCustomPicker={true}
                        onSubmit={(item) => this.onSelectSortOption(item)} />
                </View> */}
      </View>
    );
  }

  renderNewLaunchesList() {
    const { projectList } = this.state;
    if (!ObjectUtil.isEmpty(projectList)) {
      return (
        <NewLaunchesResultList
          dataArray={projectList}
          componentId={this.props.componentId}
        />
      );
    }
  }

  render() {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: "white" }}>
        <KeyboardAwareScrollView onScroll={this.onScroll} style={{ flex: 1 }}>
          {this.renderLargeTitle("New Launches")}
          <View style={{ flex: 1 }}>
            {/* {this.renderFilterAndSortBy()} */}
            {this.renderNewLaunchesList()}
          </View>
        </KeyboardAwareScrollView>
      </SafeAreaView>
    );
  }
}

NewLaunchesProjectResult.propTypes = {
  /**latest projects*/
  newLaunchLatestProjects: PropTypes.array,

  /**featured projects*/
  newLaunchFeaturedProjects: PropTypes.array
};

export { NewLaunchesProjectResult };
