//convert value string to set
function convertValueStringToSet(valueString) {
  let tempValueSet = new Set();
  valueString.split(",").forEach(value => {
    tempValueSet.add(parseInt(value));
    if (isNaN(value)) {
      tempValueSet.add(value);
    } else {
      tempValueSet.add(parseInt(value));
    }
  });
  return tempValueSet;
}

const CollectionConversionUtil = {
  convertValueStringToSet
};

export { CollectionConversionUtil };
