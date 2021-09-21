import React, { Component } from "react";
import { View, TouchableHighlight, FlatList, ScrollView } from "react-native";

import {
    Separator,
    FeatherIcon,
    BodyText
} from "../../../../components";
import { Styles } from "../Styles";
import { CheckboxStyles } from "../../../../styles"
import { SRXColor } from "../../../../constants";
import { ObjectUtil } from "../../../../utils";

export const SearchOptionList = ({ dataArray, selectedArray, onItemSelected, isSchoolOption }) => {

    //event 
    selectItem = (item, index) => {
        if (onItemSelected) {
            onItemSelected(item, index);
        }
    };

    //methods
    isSelectedItem = (item) => {
        let isSelected = false;
        if (!ObjectUtil.isEmpty(item)) {
            if (selectedArray.includes(item.value)) {
                isSelected = true;
            }
        }
        return isSelected;
    }

    //rendering methods
    renderEachItem = (item, index) => {
        return (
            <View style={{ flex: 1 }} key={index + item.value}>
                <TouchableHighlight
                    underlayColor={SRXColor.AccordionBackground}
                    onPress={() => this.selectItem(item, index)}>
                    <View
                        style={Styles.itemContainerStyle}>
                        <View style={{ flex: 1 }}>
                            <BodyText style={{ marginVertical: 8 }}>
                                {isSchoolOption ? item.name : item.key}
                            </BodyText>
                        </View>
                        {this.renderItemCheckbox(item, index)}
                    </View>
                </TouchableHighlight>
                {this.renderSeparator(index)}
            </View>
        )
    }

    renderItemCheckbox = (item, index) => {
        if (isSchoolOption) {
            return (
                <FeatherIcon
                    name={"chevron-right"}
                    size={20}
                    color={"black"}
                />
            )
        } else {
            const isSelected = this.isSelectedItem(item);
            if (isSelected) {
                return (
                    <View style={CheckboxStyles.checkStyle}>
                        <FeatherIcon name={"check"} size={15} color={"white"} />
                    </View>
                )
            } else {
                return (
                    <View style={CheckboxStyles.unCheckStyle} />
                )
            }

        }
    }

    renderSeparator = (index) => {
        if ((index + 1) !== dataArray.length) {
            return (
                <Separator edgeInset={{ left: 15, right: 15 }} />
            )
        }
    }

    return (
        <ScrollView>
            {dataArray.map((item, index) => this.renderEachItem(item, index))}
        </ScrollView>
    );
};