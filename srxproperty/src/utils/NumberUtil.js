import { StringUtil } from "./StringUtil";

function floatValue(value) {
  if (typeof value == "number") {
    return value;
  } else if (typeof value === "string" || value instanceof String) {
    if (value.length == 0) {
      return 0;
    }
    return parseFloat(StringUtil.decimalValue(value));
  } else if (typeof variable == typeof true) {
    if (variable == true) {
      return 1;
    }
  }
  return 0;
}

function intValue(value) {
  return Math.floor(floatValue(value));
}

const NumberUtil = {
  floatValue,
  intValue,
};

export { NumberUtil };
