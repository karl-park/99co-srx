import React, { Component } from "react";
import { View, FlatList } from "react-native";

import { ObjectUtil } from "../../../utils";
import { NewLaunchFeaturedProjectItem, NewLaunchLatestProjectItem } from "../NewLaunchesItems";
import { SRXColor } from "../../../constants";
import { Heading2 } from "../../../components";
import { Spacing } from "../../../styles";

class NewLaunchesResultList extends Component {

    static options(passProps) {
        return {
            topBar: {
                visible: false
            }
        };
    }

    state = {
        listingArray: [1, 2, 3, 4]
    };

    //Rendering methods for presentation
    renderNewLaunchesList() {
        const { dataArray } = this.props;
        if (!ObjectUtil.isEmpty(dataArray)) {
            return (
                <FlatList
                    data={dataArray}
                    extraData={this.state}
                    keyExtractor={item => item.key}
                    renderItem={({ item, index }) => this.renderNewLaunchesItems({ item, index })}
                />
            );
        }
    }

    renderNewLaunchesItems({ item, index }) {
        if (index < 3) {
            return (
                <View style={{ flex: 1 }}>
                    {this.renderFeaturedProjectTitle(index)}
                    <NewLaunchFeaturedProjectItem
                        projectDetailPO={item}
                        componentId={this.props.componentId} />
                </View>
            )
        } else {
            return (
                <View key={index}
                    style={{ backgroundColor: index % 2 == 0 ? SRXColor.AccordionBackground : SRXColor.White }}>
                    {this.renderLatestProjectTitle(index)}
                    <NewLaunchLatestProjectItem
                        projectDetailPO={item}
                        componentId={this.props.componentId} />
                </View>
            )
        }
    }

    renderFeaturedProjectTitle = (index) => {
        if (index == 0) {
            return (
                <Heading2 style={{ marginLeft: Spacing.M, marginTop: Spacing.M }}>
                    Featured Projects
                </Heading2>
            )
        }
    }


    renderLatestProjectTitle = (index) => {
        if (index == 3) {
            return (
                <Heading2 style={{ marginLeft: Spacing.M, marginTop: Spacing.M }}>
                    Latest Projects
                </Heading2>
            )
        }
    }


    render() {
        return (
            <View style={{ flex: 1 }}>
                {this.renderNewLaunchesList()}
            </View>
        );
    }
}

export { NewLaunchesResultList };