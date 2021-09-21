import React, { Component } from "react";
import {
  View,
  SectionList,
  TouchableHighlight,
  Image,
  Platform,
  Dimensions
} from "react-native";

import {
  ExtraSmallBodyText,
  Separator,
  FeatherIcon,
  BodyText,
  HorizontalFlatList,
  Button
} from "../../../../components";
import PropTypes from "prop-types";
import { Styles } from "../Styles";
import { FilterOptions, SRXColor } from "../../../../constants";
import { CheckboxStyles, Spacing } from "../../../../styles";
import { SetUtil, ObjectUtil } from "../../../../utils";
import { All_Property_Type_Set } from "../../../PropertySearch/Constants";

const isIOS = Platform.OS === "ios";
var { height, width } = Dimensions.get("window");
class GroupedSelectionPropertyTypeList extends Component {
  constructor(props) {
    super(props);
    this.renderGroupTitle = this.renderGroupTitle.bind(this);
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

  renderGroupTitle = () => {
    const {
      activeSections,
      dataArray,
      isResidential,
      selectedArray
    } = this.props;

    if (isResidential) {
      return (
        <View
          style={{
            flexDirection: "row",
            padding: Spacing.M,
            flex: 1,
            flexWrap: "wrap"
          }}
        >
          {dataArray.map((section, index) => {
            return (
              <TouchableHighlight
                style={{ flex: 1 }}
                onPress={() => this.selectHeader(section)}
              >
                <View
                  style={[
                    {
                      borderWidth: 1,
                      padding: Spacing.S,
                      backgroundColor: SRXColor.White
                    },
                    activeSections.includes(section.key) ||
                    (activeSections.length == 0 && index == 0)
                      ? { borderColor: SRXColor.Teal }
                      : { borderColor: SRXColor.LightGray },
                    index % 4 == 0
                      ? { borderBottomLeftRadius: 10, borderTopLeftRadius: 10 }
                      : null,
                    index % 4 == 3
                      ? {
                          borderBottomRightRadius: 10,
                          borderTopRightRadius: 10
                        }
                      : null
                  ]}
                >
                  <Image
                    style={{ width: 50, height: 26, alignSelf: "center" }}
                    source={section.image}
                    resizeMode="contain"
                  />
                  <BodyText style={{ alignSelf: "center" }}>
                    {section.key}
                  </BodyText>
                </View>
              </TouchableHighlight>
            );
          })}
        </View>
      );
    } else {
      //The Commercial table will not be equal size for each button to accommodate of displaying text in 1-2 lines and same fontSize requirements
      let allSet = dataArray[0].value;
      let dividedWidth = (width - 2 * Spacing.M) / 4;
      return (
        <View
          style={{
            flexDirection: "row",
            padding: Spacing.M,
            flex: 1,
            flexWrap: "wrap",
            alignSelf: "center"
          }}
        >
          {dataArray.map((section, index) => {
            return (
              <TouchableHighlight
                style={[
                  { width: dividedWidth }
                  // index % 4 == 2 || index % 4 == 3
                  //   ? { width: 105 }
                  //   : { width: 80 }
                ]}
                onPress={() => this.selectHeader(section)}
              >
                <View
                  style={[
                    {
                      height: 80,
                      borderWidth: 1,
                      padding: Spacing.XS,
                      backgroundColor: SRXColor.White
                    },
                    index < 4
                      ? { paddingTop: Spacing.M }
                      : { paddingTop: Spacing.S },
                    (selectedArray.has(section.value) &&
                      selectedArray.size != dataArray.length - 1) ||
                    (selectedArray.size == dataArray.length - 1 && index == 0)
                      ? { borderColor: SRXColor.Teal }
                      : { borderColor: SRXColor.LightGray },
                    index == 0
                      ? { borderTopLeftRadius: 10 }
                      : index == 3
                      ? { borderTopRightRadius: 10 }
                      : index == 4
                      ? { borderBottomLeftRadius: 10 }
                      : index == 7
                      ? { borderBottomRightRadius: 10 }
                      : null
                  ]}
                >
                  <Image
                    style={{ width: 50, height: 26, alignSelf: "center" }}
                    source={section.image}
                    resizeMode="contain"
                  />
                  <ExtraSmallBodyText style={{ textAlign: "center" }}>
                    {section.key}
                  </ExtraSmallBodyText>
                </View>
              </TouchableHighlight>
            );
          })}
        </View>
      );
    }

    // return;
    // return (
    //   <View>
    //     <HorizontalFlatList
    //       data={dataArray}
    //       keyExtractor={item => item.key}
    //       renderItem={this.renderGroupHeader}
    //     />
    //   </View>
    // );
  };

  renderGroupContents = () => {
    const { dataArray } = this.props;
    const { activeSections } = this.props;

    return (
      <View
        style={{
          flexDirection: "row",
          flexWrap: "wrap",
          paddingHorizontal: Spacing.M
        }}
      >
        {dataArray.map((section, index) => {
          if (!ObjectUtil.isEmpty(section)) {
            if (activeSections.includes(section.key)) {
              return section.data.map(item => {
                return (
                  <Button
                    buttonStyle={[
                      {
                        borderWidth: 1,
                        paddingHorizontal: Spacing.S,
                        borderRadius: 18,
                        margin: Spacing.XS
                      },
                      isIOS
                        ? { height: 40, borderRadius: 20 }
                        : { height: 36, borderRadius: 18 },
                      this.isItemChecked(item, section) ||
                      this.isCheckAll(section)
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
            }
          }
        })}
      </View>
    );
  };

  renderGroupHeader({ item, index }) {
    return (
      <View style={{ borderWidth: 1, padding: Spacing.XS, flex: 1 }}>
        <BodyText>{item.key}</BodyText>
      </View>
    );
  }
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
            {this.renderHeaderCheckbox(section)}
          </View>
        </TouchableHighlight>
        {this.renderHeaderSeparator(section)}
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
          <View style={{ flex: 1 }} key={index + "-" + item.value}>
            <TouchableHighlight
              onPress={() => this.selectContent(index, item, section)}
            >
              <View style={[Styles.itemContainerStyle]}>
                <View style={{ flex: 1 }}>
                  <BodyText style={{ marginVertical: 8 }}>
                    {this.renderRowText({ item, section })}
                  </BodyText>
                </View>
                {this.renderRowCheckbox({ index, item, section })}
              </View>
            </TouchableHighlight>
            {this.renderSeparator({ index, item, section })}
          </View>
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

  renderHeaderCheckbox = section => {
    if (!ObjectUtil.isEmpty(section)) {
      if (
        section.key === FilterOptions.FilterOptionDescription.all_residential
      ) {
        if (this.isCheckAll(section)) {
          return (
            <View style={CheckboxStyles.checkStyle}>
              <FeatherIcon name={"check"} size={15} color={"white"} />
            </View>
          );
        } else {
          return <View style={CheckboxStyles.unCheckStyle} />;
        }
      } else {
        return (
          <FeatherIcon
            name={this.isActiveSection(section) ? "chevron-up" : "chevron-down"}
            size={24}
            color={"black"}
          />
        );
      }
    }
  };

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

  render() {
    const { dataArray } = this.props;
    return (
      <View style={{ flex: 1 }}>
        {this.renderGroupTitle()}
        {this.renderGroupContents()}
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
GroupedSelectionPropertyTypeList.propTypes = {
  dataArray: PropTypes.arrayOf(PropTypes.object),
  isResidential: PropTypes.bool,
  //activeSections is used for residential
  activeSections: PropTypes.arrayOf(PropTypes.number),
  selectedArray: PropTypes.array,
  onSelectHeader: PropTypes.func,
  onSelectContent: PropTypes.func
};

export { GroupedSelectionPropertyTypeList };
