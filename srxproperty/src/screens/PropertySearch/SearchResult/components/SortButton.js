import React, { Component } from "react";
import { Picker, SmallBodyText } from "../../../../components";
import { SRXColor } from "../../../../constants";
import { Typography } from "../../../../styles";

class SortButton extends Component {
  render() {
    const { data, selectedOption, onSubmit } = this.props;
    return (
      <Picker
        mode={"dialog"}
        prompt={"Sort by"}
        inputStyle={[
          Typography.SmallBody,
          {
            color: SRXColor.TextLink,
            marginLeft: 3,
            flex: 0
          }
        ]}
        data={data}
        selectedValue={selectedOption}
        titleOfItem={(item, itemIndex, componentIndex) => item.key}
        valueOfItem={(item, itemIndex, componentIndex) => item.value}
        useCustomPicker={true}
        onSubmit={onSubmit}
        renderLeftAccessory={() => (
          //by new design, change text color to gray
          <SmallBodyText style={{ color: SRXColor.Gray }}>
            Sort by
          </SmallBodyText>
        )}
      />
    );
  }
}

export { SortButton };
