import React, { Component } from "react";
import { View, ScrollView } from "react-native";
import { LineChart, Grid, YAxis, XAxis } from "react-native-svg-charts";
import { Circle, G, Text, TSpan, Line, Rect } from "react-native-svg";
import * as scale from "d3-scale";
import { Heading2, Button } from "../../../components";
import { SRXColor } from "../../../constants";
import { StringUtil, ObjectUtil } from "../../../utils";
import Moment from "moment";
import { Spacing, Typography } from "../../../styles";

const Periods = {
  fiveYears: "5 yrs",
  threeYears: "3 yrs",
  oneYear: "1 yr",
  sixMonths: "6 mo",
  threeMonths: "3 mo"
};

export default class XTrendGraph extends Component<Props> {
  static PeriodOptions = Periods;

  state = {
    data: [],
    xAxisData: [],
    periodOptions: [
      Periods.fiveYears,
      Periods.threeYears,
      Periods.oneYear,
      Periods.sixMonths,
      Periods.threeMonths
    ],
    selectedPeriod: Periods.fiveYears,
    selectedPointIndex: -1
  };

  componentDidMount() {
    const { allowPeriods } = this.props;
    if (!ObjectUtil.isEmpty(allowPeriods)) {
      const newPeriodOptions = [];
      if (allowPeriods.includes(Periods.fiveYears)) {
        newPeriodOptions.push(Periods.fiveYears);
      }
      if (allowPeriods.includes(Periods.threeYears)) {
        newPeriodOptions.push(Periods.threeYears);
      }
      if (allowPeriods.includes(Periods.oneYear)) {
        newPeriodOptions.push(Periods.oneYear);
      }
      if (allowPeriods.includes(Periods.sixMonths)) {
        newPeriodOptions.push(Periods.sixMonths);
      }
      if (allowPeriods.includes(Periods.threeMonths)) {
        newPeriodOptions.push(Periods.threeMonths);
      }

      this.setState(
        {
          periodOptions: newPeriodOptions
        },
        () => {
          this.updateSelectedPeriod(newPeriodOptions[0]);
        }
      );
    } else {
      this.updateSelectedPeriod(Periods.fiveYears);
    }
  }

  getGraphData(selectedPeriod, xValueTrend) {
    const sortedArray = xValueTrend.sort((a, b) => {
      return new Date(a.key) - new Date(b.key);
    });
    // const sortedArray = xValueTrend; //array is sorted in XTrend.js after load from server.

    const data = [];
    sortedArray.map(item => {
      const handledItem = {
        key: Moment(item.key).format("YYYY-MM-DD"),
        value: item.value
      };

      if (selectedPeriod == Periods.threeMonths) {
        if (
          Moment()
            .subtract(3, "months")
            .isSameOrBefore(handledItem.key)
        ) {
          data.push(handledItem);
        }
      } else if (selectedPeriod == Periods.sixMonths) {
        if (
          Moment()
            .subtract(6, "months")
            .isSameOrBefore(handledItem.key)
        ) {
          data.push(handledItem);
        }
      } else if (selectedPeriod == Periods.oneYear) {
        if (
          Moment()
            .subtract(1, "years")
            .isSameOrBefore(handledItem.key)
        ) {
          data.push(handledItem);
        }
      } else if (selectedPeriod == Periods.threeYears) {
        if (
          Moment()
            .subtract(3, "years")
            .isSameOrBefore(handledItem.key)
        ) {
          data.push(handledItem);
        }
      } else if (selectedPeriod == Periods.fiveYears) {
        if (
          Moment()
            .subtract(5, "years")
            .isSameOrBefore(handledItem.key)
        ) {
          data.push(handledItem);
        }
      }
    });
    var arrayLength = data.length;
    if (
      Moment(data[arrayLength - 2]).format("Q") ==
      Moment(data[arrayLength - 1]).format("Q")
    ) {
      var currentDate = data.pop();
      currentDate.key= Moment().format("YYYY-MM-DD");
      data.pop();
      data.push(currentDate);
    }else{
      var currentDate = data.pop();
      currentDate.key= Moment().format("YYYY-MM-DD");
      data.push(currentDate);
    }

    return data;
  }

  getXAxisData(data) {
    const xAxisData = []; //array of moment item

    if (!ObjectUtil.isEmpty(data)) {
      const firstItem = data[0];
      const lastItem = data[data.length - 1];

      const firstDate = firstItem.key;
      const lastDate = lastItem.key;

      // if (Moment(firstDate).format("Q") == 2) {
      //   var perviousDate = Moment(firstDate)
      //     .subtract(3, "months")
      //     .format("YYYY-MM-DD");
      //   xAxisData.push(Moment(perviousDate));
      // }

      xAxisData.push(Moment(firstDate));

      var nextDate = Moment(firstDate)
        .add(3, "months")
        .format("YYYY-MM-DD");

      while (Moment(nextDate).isSameOrBefore(Moment(lastDate))) {
        xAxisData.push(Moment(nextDate));
        nextDate = Moment(nextDate)
          .add(3, "months")
          .format("YYYY-MM-DD");
      }
    }

    return xAxisData;
  }

  updateSelectedPeriod(selectedPeriod) {
    const { xValueTrend } = this.props;
    const data = this.getGraphData(selectedPeriod, xValueTrend);
    const xAxisData = this.getXAxisData(data);

    this.setState({
      selectedPeriod,
      data,
      xAxisData,
      selectedPointIndex: data.length - 1
    });
  }

  renderGraph() {
    const { data, xAxisData, selectedPointIndex } = this.state;
    const { concealValue } = this.props;

    if (!ObjectUtil.isEmpty(data) && Array.isArray(data)) {
      let showAllXAxisLabel = true;
      for (let i = 0; i < xAxisData.length; i++) {
        const date = xAxisData[i];
        if (Moment(date).format("Q") == 1) {
          showAllXAxisLabel = false;
        }
      }

      const Decorator = ({ x, y, data }) => {
        return data.map((item, index) => (
          <G
            key={"Decorator" + index}
            onPress={() => {
              this.setState({ selectedPointIndex: index });
            }}
          >
            {selectedPointIndex === index ? (
              <G x={x(Moment(item.key)) - 30} y={y(item.value) - 30}>
                <Text fill={SRXColor.Gray}>
                  <TSpan fontSize="10">
                    {index==data.length-1? "Latest":"Q"+Moment(item.key).format("Q, YYYY")}
                    
                  </TSpan>
                  <TSpan fontSize="11" fontWeight="600" x="0" dy="15">
                    {concealValue
                      ? StringUtil.concealValue(
                          StringUtil.formatCurrency(item.value)
                        )
                      : StringUtil.formatCurrency(item.value)}
                  </TSpan>
                </Text>
              </G>
            ) : null}

            <Circle
              key={"BlueDot" + index}
              cx={x(Moment(item.key))}
              cy={y(item.value)}
              r={3}
              fill={SRXColor.Teal}
            />
            <Rect
              x={x(Moment(item.key)) - 35}
              y="0"
              width="70"
              height="100%"
              fill={SRXColor.Transparent}
            />
          </G>
        ));
      };

      const CustomGrid = ({ x, y, data, ticks }) => (
        <G>
          {console.log(ticks)}
          {// Horizontal grid
          ticks.map(tick => (
            <Line
              key={tick}
              x1={"0%"}
              x2={"100%"}
              y1={y(tick)}
              y2={y(tick)}
              stroke={"rgba(0,0,0,0.1)"}
            />
          ))}
          {// Vertical grid
          xAxisData.map((item, index) => {
            if (showAllXAxisLabel || Moment(item).format("Q") == 1) {
              return (
                <Line
                  key={index}
                  y1={"0%"}
                  y2={"100%"}
                  x1={x(Moment(item))}
                  x2={x(Moment(item))}
                  stroke={"rgba(0,0,0,0.1)"}
                />
              );
            }
          })}
        </G>
      );

      return (
        <View
          style={{
            borderColor: "rgba(0,0,0,0.1)",
            borderWidth: 1,
            padding: Spacing.XS,
            backgroundColor: SRXColor.White
          }}
        >
          <View style={{ position: "absolute", margin: Spacing.XS }}>
            <YAxis
              style={{ height: 220 }}
              data={data}
              yAccessor={({ item }) => item.value}
              formatLabel={(value, index) => {
                if (concealValue) {
                  return StringUtil.concealValue(
                    StringUtil.formatCurrencyWithAbbreviation(value)
                  );
                } else {
                  return StringUtil.formatCurrencyWithAbbreviation(value);
                }
              }}
              contentInset={{ top: 50, bottom: 20 }}
              svg={{
                fontSize: 10,
                fill: "grey",
                alignmentBaseline: "baseline",
                baselineShift: "3"
              }}
              numberOfTicks={2}
            />
          </View>
          <ScrollView
            horizontal={true}
            showsHorizontalScrollIndicator={false}
            bounces={false}
            style={{ borderColor: "rgba(0,0,0,0.1)", borderLeftWidth: 1 }}
            ref={scrollView => (this.graphScrollView = scrollView)}
            onContentSizeChange={(contentWidth, contentHeight) => {
              this.graphScrollView.scrollToEnd({ animated: false });
            }}
          >
            <View style={{ minWidth: "100%" }}>
              <LineChart
                data={data}
                svg={{ stroke: SRXColor.Teal }}
                style={{
                  height: 220,
                  width: (data.length - 1) * 75 + 50 + 100,
                  minWidth: "100%"
                }}
                yAccessor={({ item }) => item.value}
                xAccessor={({ item }) => Moment(item.key)}
                xScale={scale.scaleTime}
                contentInset={{ top: 50, bottom: 20, right: 50, left: 100 }}
                numberOfTicks={2}
              >
                <CustomGrid />
                <Decorator />
              </LineChart>
              {!ObjectUtil.isEmpty(xAxisData) ? (
                <XAxis
                  style={{
                    height: 30,
                    width: (data.length - 1) * 75 + 50 + 100,
                    minWidth: "100%",
                    borderTopWidth: 1,
                    borderColor: "rgba(0,0,0,0.1)"
                  }}
                  data={xAxisData}
                  xAccessor={({ item }) => {
                    return item;
                  }}
                  xScale={scale.scaleTime}
                  formatLabel={(value, index) => {
                    if (showAllXAxisLabel) {
                      return "Q" + value.format("Q,YYYY");
                    } else if (value.format("Q") == 1) {
                      return value.format("YYYY");
                    }
                  }}
                  contentInset={{ right: 50, left: 100 }}
                  svg={{
                    fontSize: 10,
                    fill: "grey",
                    y: 5,
                    textAnchor: "start"
                  }} //rotation: -30, y: 12
                />
              ) : null}
            </View>
          </ScrollView>
        </View>
      );
    }
  }

  renderTrendPeriods() {
    const { periodOptions, selectedPeriod } = this.state;

    return (
      <View
        style={{
          flexDirection: "row",
          paddingVertical: 1,
          paddingHorizontal: 0.5,
          backgroundColor: SRXColor.LightGray,
          marginBottom: Spacing.M
        }}
      >
        {periodOptions.map((item, index) => {
          if (selectedPeriod === item) {
            return (
              <Button
                key={item}
                buttonStyle={{
                  backgroundColor: SRXColor.Teal,
                  flex: 1,
                  paddingVertical: Spacing.XS,
                  marginHorizontal: 0.5
                }}
                textStyle={[
                  Typography.SmallBody,
                  { color: SRXColor.White, textAlign: "center", flex: 1 }
                ]}
              >
                {item}
              </Button>
            );
          } else {
            return (
              <Button
                key={item}
                buttonStyle={{
                  backgroundColor: SRXColor.White,
                  flex: 1,
                  paddingVertical: Spacing.XS,
                  marginHorizontal: 0.5
                }}
                textStyle={[
                  Typography.SmallBody,
                  { color: SRXColor.Teal, textAlign: "center", flex: 1 }
                ]}
                onPress={() => this.updateSelectedPeriod(item)}
              >
                {item}
              </Button>
            );
          }
        })}
      </View>
    );
  }

  render() {
    return (
      <View style={{ padding: Spacing.M }}>
        <Heading2 style={{ marginBottom: Spacing.M }}>X-Trend</Heading2>
        {this.renderTrendPeriods()}
        {this.renderGraph()}
      </View>
    );
  }
}
