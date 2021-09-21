import React, { Component } from "react";
import { View, Text, SectionList, TouchableHighlight } from "react-native";
import PropTypes from "prop-types";
import { SRXColor } from "../../constants";
import { Separator } from "../Separator";

class GroupedSectionList extends Component {
  static propTypes = {
    /*
     * required
     *for structure, follow GroupedSectionList's sections
     */
    sections: PropTypes.array,

    /*
     * required
     * { index, item, section } will be provided
     */
    renderItem: PropTypes.func,
    /*
     * optional
     * { section } will be provided
     */
    titleOfSection: PropTypes.func,
    /*
     * optional
     * { section } will be provided
     */
    renderSectionHeader: PropTypes.func,
    /*
     * optional
     * { section } will be provided
     */
    renderSectionFooter: PropTypes.func,
    /**
     * optional
     * default is single
     * when selecting default, if provide
     */
    separatorStyle: PropTypes.oneOf(["single", "none"])
  };

  //method

  //refer to https://facebook.github.io/react-native/docs/sectionlist#scrolltolocation
  scrollToLocation = params => {
    this.sectionList.scrollToLocation(params);
  };

  //view render

  renderRow = ({ index, item, section }) => {
    if (this.props.renderItem) {
      return this.props.renderItem({ index, item, section });
    } else {
      return (
        <TouchableHighlight key={index} onPress={() => console.log(item)}>
          <View style={GroupedSectionList.rowCell}>
            <Text style={{ fontSize: 15, marginVertical: 8 }}>{item}</Text>
          </View>
        </TouchableHighlight>
      );
    }
  };

  renderSectionTitle = section => {
    if (this.props.titleOfSection) {
      return (
        <Text style={GroupedTableStyles.headerTitle}>
          {this.props.titleOfSection({ section })}
        </Text>
      );
    }
  };

  renderSectionHeader = ({ section }) => {
    const { renderSectionHeader } = this.props;
    if (renderSectionHeader) {
      return renderSectionHeader({ section });
    } else {
      return (
        <View style={GroupedTableStyles.header}>
          {this.renderSectionTitle(section)}
        </View>
      );
    }
  };

  renderSectionFooter = ({ section }) => {
    const { renderSectionFooter, sections } = this.props;
    if (renderSectionFooter) {
      return renderSectionFooter({ section });
    } else {
      if (section === sections[sections.length - 1]) {
        return <View style={{ height: 50 }} />;
      } else {
        return <View style={{ height: 5 }} />;
      }
    }
  };

  renderItemSeparatorComponent = () => {
    const { separatorStyle } = this.props;
    if (separatorStyle === "none") {
      return null;
    } else {
      return <Separator edgeInset={{ left: 15 }} />;
    }
  };

  renderSectionSeparatorComponent = () => {
    const { separatorStyle } = this.props;
    if (separatorStyle === "none") {
      return null;
    } else {
      return <Separator />;
    }
  };

  render() {
    const {
      sections,
      style,
      renderItem,
      renderSectionHeader,
      renderSectionFooter,
      separatorStyle,
      ...rest
    } = this.props;
    return (
      <SectionList
        {...rest}
        ref={component => (this.sectionList = component)}
        style={[GroupedTableStyles.SectionList, style]}
        sections={sections}
        stickySectionHeadersEnabled={false}
        renderItem={({ index, item, section }) =>
          this.renderRow({ index, item, section })
        }
        renderSectionHeader={({ section }) =>
          this.renderSectionHeader({ section })
        }
        renderSectionFooter={({ section }) =>
          this.renderSectionFooter({ section })
        }
        ItemSeparatorComponent={() => this.renderItemSeparatorComponent()}
        SectionSeparatorComponent={() => this.renderSectionSeparatorComponent()}
        keyExtractor={(item, index) => item + index}
      />
    );
  }
}

const GroupedTableStyles = {
  SectionList: {
    flex: 1,
    backgroundColor: "#efefef"
  },
  rowCell: {
    paddingLeft: 15,
    paddingRight: 8,
    minHeight: 44,
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "white"
  },
  header: {
    paddingHorizontal: 8,
    paddingBottom: 6,
    paddingTop: 25
  },
  headerTitle: {
    fontSize: 13,
    color: SRXColor.Gray
  }
};

export { GroupedSectionList };
