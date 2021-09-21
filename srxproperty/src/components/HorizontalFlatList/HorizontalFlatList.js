import React, { Component } from "react";
import { View, FlatList } from "react-native";
import { Spacing } from "../../styles";

class HorizontalFlatList extends Component {

  scrollToOffset(params) {
    this.flatlist.scrollToOffset(params);
  }

  render() {
    const { data, renderItem, style, ...rest } = this.props;
    return (
      <FlatList
        ref={component => this.flatlist = component}
        {...rest}
        style={[{ paddingBottom: Spacing.S }, style]}
        horizontal={true}
        showsHorizontalScrollIndicator={false}
        keyExtractor={item => item.key}
        data={data}
        renderItem={({ item, index }) => renderItem({ item, index })}
        ItemSeparatorComponent={() => <View style={{ width: Spacing.M }} />}
        ListHeaderComponent={<View style={{ width: Spacing.M }} />}
        ListFooterComponent={<View style={{ width: Spacing.M }} />}
      />
    );
  }
}

export { HorizontalFlatList };
