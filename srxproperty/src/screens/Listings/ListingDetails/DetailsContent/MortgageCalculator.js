import React, { Component } from "react";
import { View, SafeAreaView } from "react-native";
import PropTypes from "prop-types";
import { PieChart } from "react-native-svg-charts";
import {
  TextInput,
  Heading2,
  BodyText,
  Text,
  Subtext
} from "../../../../components";
import { ListingPO } from "../../../../dataObject";
import { Spacing } from "../../../../styles";
import { SRXColor } from "../../../../constants";
import { ObjectUtil, StringUtil, PropertyTypeUtil } from "../../../../utils";

class MortgageCalculator extends Component {
  static propTypes = {
    listingPO: PropTypes.instanceOf(ListingPO)
  };

  static defaultProps = {
    listingPO: null
  };

  constructor(props) {
    super(props);

    const { listingPO } = props;

    var price = "0";
    if (!ObjectUtil.isEmpty(listingPO)) {
      price = StringUtil.decimalValue(listingPO.getAskingPrice());
    }

    this.state = {
      mortgage: {
        propertyPrice: price,
        borrowerAge: "35",
        interestRate: "2.0",
        loanToValue: "75"
      },
      error: {
        borrowerAgeError: ""
      }
    };

    this.borrowerAgEndEditing = this.borrowerAgEndEditing.bind(this);
  }

  getMaxTenure() {
    const { mortgage } = this.state;
    const { listingPO } = this.props;
    var isHDB = false;
    if (!ObjectUtil.isEmpty(listingPO)) {
      isHDB = PropertyTypeUtil.isHDB(listingPO.cdResearchSubType);
    }
    return Math.max(Math.min(65 - mortgage.borrowerAge, isHDB ? 25 : 30), 0);
  }

  getPrinciple() {
    const { mortgage } = this.state;
    return (mortgage.propertyPrice * mortgage.loanToValue) / 100;
  }

  getMonthlyRate() {
    const { mortgage } = this.state;
    return mortgage.interestRate / 12 / 100;
  }

  getNumberOfPeriod() {
    return this.getMaxTenure() * 12;
  }

  getMonthlyPayment() {
    const principal = this.getPrinciple();
    const monthlyRate = this.getMonthlyRate();
    const numberOfPeriod = this.getNumberOfPeriod();

    if (numberOfPeriod == 0) return 0;

    if (monthlyRate === 0) {
      return principal / numberOfPeriod;
    } else {
      const monthlyPayment =
        (principal * monthlyRate * Math.pow(1 + monthlyRate, numberOfPeriod)) /
        (Math.pow(1 + monthlyRate, numberOfPeriod) - 1);
      return monthlyPayment;
    }
  }

  getAvgPrincipal() {
    const principal = this.getPrinciple();
    const numberOfPeriod = this.getNumberOfPeriod();

    if (numberOfPeriod == 0) return 0;

    return principal / numberOfPeriod;
  }

  getAvgInterestRate() {
    return this.getMonthlyPayment() - this.getAvgPrincipal();
  }

  renderPrice() {
    const { mortgage } = this.state;
    return (
      <View style={Styles.textInputContainer}>
        <TextInput
          title={"Property price (SGD)"}
          keyboardType={"decimal-pad"}
          value={StringUtil.formatCurrency(mortgage.propertyPrice)}
          onChangeText={text =>
            this.setState({
              mortgage: {
                ...mortgage,
                propertyPrice: StringUtil.decimalValue(text, 2)
              }
            })
          }
        />
      </View>
    );
  }

  borrowerAgEndEditing = () => {
    const { mortgage, error } = this.state;

    var errorMsg = "";
    var age = mortgage.borrowerAge;
    if (age < 21) {
      age = 21;
    } else if (age > 60) {
      age = 60;
      errorMsg =
        "Please consult a mortgage specialist if you want to take a loan that extends beyond the official retirement age of 65.";
    }

    this.setState({
      mortgage: {
        ...mortgage,
        borrowerAge: age
      },
      error: {
        ...error,
        borrowerAgeError: errorMsg
      }
    });
  };

  renderBorrowerAge() {
    const { mortgage, error } = this.state;
    return (
      <View style={Styles.textInputContainer}>
        <TextInput
          title={"Borrower's age"}
          keyboardType={"number-pad"}
          value={StringUtil.decimalValue(mortgage.borrowerAge, 0)}
          onChangeText={text =>
            this.setState({
              mortgage: {
                ...mortgage,
                borrowerAge: StringUtil.decimalValue(text, 0)
              }
            })
          }
          error={error.borrowerAgeError}
          onEndEditing={this.borrowerAgEndEditing}
        />
      </View>
    );
  }

  renderMaxTenure() {
    return (
      <View style={Styles.textInputContainer}>
        <BodyText>Max Tenure</BodyText>
        <Heading2>{this.getMaxTenure()} yrs</Heading2>
      </View>
    );
  }

  renderInterestRate() {
    const { mortgage } = this.state;
    return (
      <View style={{ minWidth: "30%" }}>
        <TextInput
          title={"Interest rate"}
          keyboardType={"decimal-pad"}
          inputContainerStyle={{ paddingRight: 0 }}
          rightView={
            <View style={Styles.textInputRightPercentageLabel}>
              <BodyText>%</BodyText>
            </View>
          }
          value={StringUtil.decimalValue(mortgage.interestRate)}
          onChangeText={text =>
            this.setState({
              mortgage: {
                ...mortgage,
                interestRate: StringUtil.decimalValue(text)
              }
            })
          }
        />
      </View>
    );
  }

  renderLoanToValue() {
    const { mortgage } = this.state;
    return (
      <View style={{ minWidth: "30%" }}>
        <TextInput
          title={"Loan-to-value"}
          keyboardType={"decimal-pad"}
          inputContainerStyle={{ paddingRight: 0 }}
          rightView={
            <View style={Styles.textInputRightPercentageLabel}>
              <BodyText>%</BodyText>
            </View>
          }
          value={StringUtil.decimalValue(mortgage.loanToValue)}
          onChangeText={text =>
            this.setState({
              mortgage: {
                ...mortgage,
                loanToValue: StringUtil.decimalValue(text)
              }
            })
          }
        />
      </View>
    );
  }

  renderInterestRateAndLoanToValue() {
    return (
      <View style={[Styles.textInputContainer, { flexDirection: "row" }]}>
        {this.renderInterestRate()}
        <View style={{ width: Spacing.XL }} />
        {this.renderLoanToValue()}
      </View>
    );
  }

  renderPieChart() {
    const data = [
      {
        key: "principal_avg",
        value: this.getAvgPrincipal(),
        svg: { fill: SRXColor.Teal }
      },
      {
        key: "interest_avg",
        value: this.getAvgInterestRate(),
        svg: { fill: "#F4F4F4" }
      }
    ];

    return (
      <View
        style={{
          flex: 1,
          paddingVertical: Spacing.S,
          justifyContent: "center"
        }}
      >
        <PieChart
          style={{ height: 160 }}
          data={data}
          valueAccessor={({ item }) => item.value}
          innerRadius={"68%"}
        />
        <Text
          style={{
            textAlign: "center",
            position: "absolute",
            alignSelf: "center"
          }}
        >
          {StringUtil.formatCurrency(this.getMonthlyPayment(), 0)}
          <Subtext> /mo</Subtext>
        </Text>
      </View>
    );
  }

  renderValues() {
    return (
      <View
        style={{
          flex: 1,
          paddingHorizontal: Spacing.XS,
          justifyContent: "center"
        }}
      >
        <View style={{ marginVertical: Spacing.S }}>
          <Heading2>Loan Amount</Heading2>
          <BodyText>
            {StringUtil.formatCurrency(this.getPrinciple(), 0)}
          </BodyText>
        </View>
        <View
          style={{
            flexDirection: "row",
            alignItems: "center",
            marginVertical: Spacing.S
          }}
        >
          <View
            style={{
              height: 12,
              width: 12,
              marginRight: Spacing.XS,
              backgroundColor: SRXColor.Teal
            }}
          />
          <BodyText>
            {StringUtil.formatCurrency(this.getAvgPrincipal(), 0)}{" "}
            <Subtext> principal</Subtext>
          </BodyText>
        </View>
        <View
          style={{
            flexDirection: "row",
            alignItems: "center",
            marginVertical: Spacing.S
          }}
        >
          <View
            style={{
              height: 12,
              width: 12,
              marginRight: Spacing.XS,
              backgroundColor: "#F4F4F4"
            }}
          />
          <BodyText>
            {StringUtil.formatCurrency(this.getAvgInterestRate(), 0)}{" "}
            <Subtext> interest</Subtext>
          </BodyText>
        </View>
      </View>
    );
  }

  renderAmortizationValue() {
    return (
      <View style={{ flexDirection: "row", marginBottom: Spacing.M }}>
        {this.renderPieChart()}
        <View style={{ width: Spacing.M }} />
        {this.renderValues()}
      </View>
    );
  }

  render() {
    const { style } = this.props;
    return (
      <SafeAreaView style={style}>
        <View style={{ padding: Spacing.M }}>
          {this.renderAmortizationValue()}
          {this.renderPrice()}
          {this.renderBorrowerAge()}
          {this.renderMaxTenure()}
          {this.renderInterestRateAndLoanToValue()}
        </View>
      </SafeAreaView>
    );
  }
}

const Styles = {
  textInputContainer: {
    paddingVertical: Spacing.XS
  },
  textInputRightPercentageLabel: {
    paddingHorizontal: Spacing.S,
    borderColor: "#8DABC4",
    borderLeftWidth: 1,
    alignSelf: "stretch",
    margin: -1,
    justifyContent: "center"
  }
};

export { MortgageCalculator };
