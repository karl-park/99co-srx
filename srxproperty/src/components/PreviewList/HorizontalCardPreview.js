import React, { Component } from "react";
import { View } from "react-native";
import { Button, Heading2, HorizontalFlatList } from "../../components";
import { Spacing, Typography } from "../../styles";
import { SRXColor } from "../../constants";
import { HorizontalCardItemPlaceholder } from "./HorizontalCardItemPlaceholder";
import { ObjectUtil } from "../../utils";

class HorizontalCardPreview extends Component {
  componentDidUpdate(prevProps, prevState) {
    if (prevProps.data != this.props.data) {
      if (!this.hasData()) {
        this.flatlist.scrollToOffset({ offset: 0, animated: false });
      }
    }
  }

  hasData = () => {
    const { data } = this.props;
    return data && Array.isArray(data) && data.length > 0;
  };

  renderHeader() {
    const { data, title, onViewAll } = this.props;

    const hasData = data && Array.isArray(data) && data.length > 0;

    if (!ObjectUtil.isEmpty(title) || (hasData && onViewAll)) {
      return (
        <View style={{ padding: Spacing.M, flexDirection: "row" }}>
          {!ObjectUtil.isEmpty(title) ? (
            <Heading2 style={{ flex: 1 }}>{title}</Heading2>
          ) : null}
          {hasData && onViewAll ? (
            <Button
              textStyle={[Typography.SmallBody, { color: SRXColor.TextLink }]}
              onPress={onViewAll}
            >
              View all
            </Button>
          ) : null}
        </View>
      );
    } else {
      return <View style={{ height: Spacing.S }} />;
    }
  }

  render() {
    const { data, title, renderItem, onViewAll, style, ...rest } = this.props;

    const hasData = this.hasData();

    const placeholderObject = [
      { key: "1" },
      { key: "2" },
      { key: "3" },
      { key: "4" },
      { key: "5" }
    ];

    return (
      <View style={[{ paddingBottom: Spacing.L }, style]}>
        {this.renderHeader()}
        {hasData ? (
          <HorizontalFlatList
            ref={component => (this.flatlist = component)}
            scrollEnabled={true}
            data={data}
            renderItem={renderItem}
            {...rest}
          />
        ) : (
          <HorizontalFlatList
            scrollEnabled={false}
            ref={component => (this.flatlist = component)}
            data={placeholderObject}
            renderItem={() => <HorizontalCardItemPlaceholder />}
            keyExtractor={(item, index) => item.key}
            {...rest}
          />
        )}
      </View>
    );
  }
}

export { HorizontalCardPreview };
