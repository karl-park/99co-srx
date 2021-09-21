import React, { Component } from "react";
import { View, Text, SectionList, TouchableHighlight } from "react-native";
import { GroupedSectionList } from "../../../components";
import { SRXColor } from "../../../constants";

export const SuggestionList = ({ dataArray, onItemSelected, keyboardShouldPersistTaps, onScroll }) => {
  selectItem = item => {
    if (onItemSelected) {
      onItemSelected(item);
    }
  };

  sectionTitle = ({section}) => {
    if (section.sectionTitle === 'HDB_ESTATE') {
      return "Area";
    }
    return section.sectionTitle;
  }

  return (
    <GroupedSectionList
      keyboardShouldPersistTaps={keyboardShouldPersistTaps}
      sections={dataArray}
      renderItem={({ index, item, section }) => (
          <TouchableHighlight key={index} onPress={() => this.selectItem(item)}>
              <View style={{
                  paddingLeft: 15,
                  paddingRight: 8,
                  minHeight: 44,
                  justifyContent: "center",
                  backgroundColor: "white"
              }}>
                  <Text style={{ fontSize: 15, marginVertical: 8 }}>{item.displayText}</Text>
              </View>
          </TouchableHighlight>
      )}
      titleOfSection={this.sectionTitle}
      onScroll={onScroll}
    />
  );
};
