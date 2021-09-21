import React from "react";
import {
    View,
    ActivityIndicator
} from "react-native";

export const LoadMoreIndicator = style => {
  return (
    <View style={[{ paddingVertical: 30 }, style]}>
      <ActivityIndicator animating />
    </View>
  );
};
