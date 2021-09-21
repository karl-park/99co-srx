import React, { Component } from "react";
import { View, SectionList, TouchableHighlight } from "react-native";

import {
  Separator,
  FeatherIcon,
  BodyText,
  Button
} from "../../../../components";
import { Styles } from "../Styles";
import { FilterOptions, SRXColor } from "../../../../constants";
import { CheckboxStyles, Spacing } from "../../../../styles";
import { SetUtil, ObjectUtil } from "../../../../utils";
import { All_Property_Type_Set } from "../../../PropertySearch/Constants";

class GroupedSelectionList extends Component {
  constructor(props) {
    super(props);
  }

  //Select Header
  selectHeader = section => {
    const { onSelectHeader } = this.props;
    if (onSelectHeader) {
      onSelectHeader(section);
    }
  };

  //On Select Each Item
  selectContent = (index, item, section) => {
    const { onSelectContent } = this.props;
    if (onSelectContent) {
      onSelectContent(index, item, section);
    }
  };

  //Check isActive section or not
  isActiveSection = section => {
    const { activeSections } = this.props;
    let isActive = false;
    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.key)) {
        isActive = true;
      }
    }
    return isActive;
  };

  isCheckAll = section => {
    const { selectedArray } = this.props;
    let isCheckAll = false;
    if (section.value instanceof Set) {
      if (SetUtil.isSuperset(selectedArray, section.value)) {
        isCheckAll = true;
      }
    }
    return isCheckAll;
  };

  isItemChecked = (item, section) => {
    const { selectedArray } = this.props;
    let isItemChecked = false;
    if (section.key === FilterOptions.FilterOptionDescription.condo) {
      if (item.value instanceof Set) {
        if (SetUtil.isSuperset(selectedArray, item.value)) {
          isItemChecked = true;
        }
      }
    } else {
      if (selectedArray.has(item.value)) {
        isItemChecked = true;
      }
    }

    return isItemChecked;
  };

  renderHeader = ({ section }) => {
    const { containerStyle } = this.props;
    return (
      <View style={{ flex: 1 }} key={section.value}>
        <TouchableHighlight onPress={() => this.selectHeader(section)}>
          <View
            style={[
              containerStyle,
              All_Property_Type_Set.has(section.key) &&
              section.key !==
                FilterOptions.FilterOptionDescription.all_residential
                ? Styles.itemContainerStyle
                : Styles.headerContainerStyle
            ]}
          >
            <View style={{ flex: 1 }}>
              <BodyText style={Styles.bodyTextExtraStyle}>
                {section.key}
              </BodyText>
            </View>
            {/* {this.renderHeaderCheckbox(section)} */}
          </View>
        </TouchableHighlight>
        {/* {this.renderHeaderSeparator(section)} */}
      </View>
    );
  };

  renderHeaderSeparator(section) {
    return <Separator />;
  }

  renderRow = ({ index, item, section }) => {
    const { activeSections } = this.props;
    if (!ObjectUtil.isEmpty(section)) {
      if (activeSections.includes(section.key)) {
        return (
          <Button
            buttonStyle={[
              {
                borderWidth: 1,
                paddingHorizontal: Spacing.S,
                margin: Spacing.XS
              },
              isIOS
                ? { height: 40, borderRadius: 20 }
                : { height: 36, borderRadius: 18 },
              this.isItemChecked(item, section) || this.isCheckAll(section)
                ? { borderColor: SRXColor.Teal }
                : { borderColor: SRXColor.LightGray }
            ]}
            textStyle={{ color: SRXColor.Black }}
            onPress={() => this.selectContent(index, item, section)}
          >
            {this.renderRowText({ item, section })}
          </Button>
          // <View style={{ flex: 1 }} key={index + "-" + item.value}>
          //   <TouchableHighlight
          //     onPress={() => this.selectContent(index, item, section)}
          //   >
          //     <View style={[Styles.itemContainerStyle]}>
          //       <View style={{ flex: 1 }}>
          //         <BodyText style={{ marginVertical: 8 }}>
          //           {this.renderRowText({ item, section })}
          //         </BodyText>
          //       </View>
          //       {this.renderRowCheckbox({ index, item, section })}
          //     </View>
          //   </TouchableHighlight>
          //   {this.renderSeparator({ index, item, section })}
          // </View>
        );
      }
    }
  };

  renderRowText = ({ item, section }) => {
    if (
      item.value === FilterOptions.FilterOptionDescription.all_tenure ||
      All_Property_Type_Set.has(item.value)
    ) {
      if (this.isCheckAll(section)) {
        return "Unselect all";
      } else {
        return "Select all";
      }
    }
    return item.key;
  };

  // renderHeaderCheckbox = section => {
  //   if (!ObjectUtil.isEmpty(section)) {
  //     if (
  //       section.key === FilterOptions.FilterOptionDescription.all_residential
  //     ) {
  //       if (this.isCheckAll(section)) {
  //         return (
  //           <View style={CheckboxStyles.checkStyle}>
  //             <FeatherIcon name={"check"} size={15} color={"white"} />
  //           </View>
  //         );
  //       } else {
  //         return <View style={CheckboxStyles.unCheckStyle} />;
  //       }
  //     } else {
  //       return (
  //         <FeatherIcon
  //           name={this.isActiveSection(section) ? "chevron-up" : "chevron-down"}
  //           size={24}
  //           color={"black"}
  //         />
  //       );
  //     }
  //   }
  // };

  renderRowCheckbox = ({ index, item, section }) => {
    if (!ObjectUtil.isEmpty(item)) {
      if (this.isItemChecked(item, section) || this.isCheckAll(section)) {
        return (
          <View style={CheckboxStyles.checkStyle}>
            <FeatherIcon name={"check"} size={15} color={"white"} />
          </View>
        );
      } else {
        return <View style={CheckboxStyles.unCheckStyle} />;
      }
    }
  };

  renderSeparator = ({ index, item, section }) => {
    if (index + 1 !== section.data.length) {
      return <Separator edgeInset={{ left: 15, right: 15 }} />;
    } else {
      return <Separator />;
    }
  };

  renderTitle = () => {
    const { dataArray } = this.props;
    return dataArray.map((section, index) => {
      console.log(section);
      return this.renderHeader({ section });
    });
  };

  renderContent = () => {
    const { dataArray } = this.props;
    return (
      <View
        style={{
          flexDirection: "row",
          flexWrap: "wrap",
          paddingHorizontal: Spacing.M
        }}
      >
        {dataArray.map(section => {
          return section.data.map((item, index) => {
            return (
              <Button
                buttonStyle={[
                  {
                    borderWidth: 1,
                    paddingHorizontal: Spacing.S,
                    margin: Spacing.XS
                  },
                  isIOS
                    ? { height: 40, borderRadius: 20 }
                    : { height: 36, borderRadius: 18 },
                  this.isItemChecked(item, section) || this.isCheckAll(section)
                    ? { borderColor: SRXColor.Teal }
                    : { borderColor: SRXColor.LightGray }
                ]}
                textStyle={{ color: SRXColor.Black }}
                onPress={() => this.selectContent(index, item, section)}
              >
                {this.renderRowText({ item, section })}
              </Button>
            );
          });
        })}
      </View>
    );
  };

  render() {
    const { dataArray } = this.props;
    return (
      <View style={{ flex: 1 }}>
        {this.renderTitle()}
        {this.renderContent()}
        <Separator edgeInset={{ top: Spacing.S }} />
        {/* <SectionList
          sections={dataArray}
          stickySectionHeadersEnabled={false}
          renderItem={({ index, item, section }) =>
            this.renderRow({ index, item, section })
          }
          renderSectionHeader={({ section }) => this.renderHeader({ section })}
          ItemSeparatorComponent={() => {
            return null;
          }}
          SectionSeparatorComponent={() => {
            return null;
          }}
          keyExtractor={(item, index) => item + index}
        /> */}
      </View>
    );
  }
}

export { GroupedSelectionList };
