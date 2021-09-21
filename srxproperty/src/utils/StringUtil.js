import {ObjectUtil} from './ObjectUtil';

//number handling for currency
function decimalValue(text, maxDecimalNo) {
  if (typeof text == 'number') {
    text = text.toString();
  }

  if (ObjectUtil.isEmpty(text)) {
    return '0';
  }
  //returning String

  let newText = '';
  let numbers = '0123456789.-';

  var hasDot = false;
  var decimalCount = 0;

  if (maxDecimalNo == 0) {
    hasDot = true;
  }

  if (text.slice(-1) === 'K') {
    text = text.slice(0, text.length - 1) + '000';
  }
  for (var i = 0; i < text.length; i++) {
    if (numbers.indexOf(text[i]) > -1) {
      if (text[i] != '.' || !hasDot) {
        if (
          !maxDecimalNo ||
          (maxDecimalNo > 0 && decimalCount < maxDecimalNo)
        ) {
          newText = newText + text[i];
          if (text[i] == '.') {
            hasDot = true;
          } else if (hasDot) {
            decimalCount++;
          }
        }
      } else if (text[i] == '.' && hasDot) {
        break;
      }
    }
  }

  return newText;
}

function formatThousand(text, maxDecimalNo) {
  if (typeof text == 'number') {
    text = text.toString();
  }

  if (
    text &&
    ((typeof text === 'string' || text instanceof String) && text != '')
  ) {
    let value = this.decimalValue(text, maxDecimalNo);

    var newText = value;

    if (value.length > 0) {
      if (value.slice(-1) == '.') {
        newText =
          parseFloat(value.slice(0, -1))
            .toFixed(0)
            .replace(/(\d)(?=(\d{3})+(,|$))/g, '$1,') + '.';
      } else {
        if (value.indexOf('.') != -1) {
          let integerValue = value.substr(0, value.indexOf('.'));
          let decimalValue = value.substr(value.indexOf('.') + 1);

          newText =
            parseFloat(integerValue)
              .toFixed(0)
              .replace(/(\d)(?=(\d{3})+(,|$))/g, '$1,') +
            '.' +
            decimalValue;
        } else {
          newText = parseFloat(value)
            .toFixed(0)
            .replace(/(\d)(?=(\d{3})+(,|$))/g, '$1,');
        }
      }
      return newText;
    } else {
      return '';
    }
  } else {
    return '';
  }
}

function formatCurrency(text, maxDecimalNo) {
  let thousandFormatted = this.formatThousand(text, maxDecimalNo);
  if (thousandFormatted.length > 0) {
    // if (thousandFormatted.charAt(0) == '-') {
    //   return "-$" + thousandFormatted.substr(1);
    // }
    // else {
    return '$' + thousandFormatted;
    // }
  } else {
    return thousandFormatted;
  }
}

function formatThousandWithAbbreviation(text) {
  var value = 0;
  if (typeof text == 'string') {
    value = NumberUtil.decimalValue(Text);
  } else if (typeof text == 'number') {
    value = text;
  }

  var thousand = 1000;
  var million = 1000000;
  var billion = 1000000000;
  var trillion = 1000000000000;

  if (Math.abs(value) >= trillion) {
    return this.decimalValue(value / trillion, 1) + 'T';
  } else if (Math.abs(value) >= billion) {
    return this.decimalValue(value / billion, 1) + 'B';
  } else if (Math.abs(value) >= million) {
    return this.decimalValue(value / million, 1) + 'M';
  } else if (Math.abs(value) >= thousand) {
    return this.decimalValue(value / thousand, 1) + 'k';
  } else {
    return this.decimalValue(value, 2);
  }
}

function formatCurrencyWithAbbreviation(text) {
  let thousandFormatted = this.formatThousandWithAbbreviation(text);
  if (thousandFormatted.length > 0) {
    return '$' + thousandFormatted;
  } else {
    return thousandFormatted;
  }
}

function concealValue(text, maxNonConcealChar) {
  //maxShowingChar ==> number of number is display
  let result = '';
  let addedNumber = 0; //number of char display without conceal

  let displayNumber = 1;
  if (maxNonConcealChar) {
    displayNumber = maxNonConcealChar;
  }

  if (!ObjectUtil.isEmpty(text)) {
    for (let i = 0; i < text.length; i++) {
      const char = text[i];
      if (isNaN(char)) {
        result += char;
      } else {
        if (addedNumber < displayNumber) {
          result += char;
          addedNumber++;
        } else {
          result += 'X';
        }
      }
    }
  }

  return result;
}

function replace(source, fromString, toString) {
  return source.replace(new RegExp(fromString, 'g'), toString);
}

function compareStrings(string1, string2, ignoreCase, useLocale) {
  if (ignoreCase) {
    if (useLocale) {
      string1 = string1.toLocaleLowerCase();
      string2 = string2.toLocaleLowerCase();
    } else {
      string1 = string1.toLowerCase();
      string2 = string2.toLowerCase();
    }
  }

  return string1 === string2;
}

function validateEmailFormat(email) {
  let reg = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
  return reg.test(email);
}

function isValidHTMLTag(str) {
  // Regex to check valid HTML tag.
  var regex = /<("[^"]*"|\'[^\']*\'|[^\'">])*>/;
  // var p = Pattern.compile(regex);
  // // If the string is empty return false
  // if (str == null) {
  //   return false;
  // }
  // // Find match between given string and regular expression
  // // using Pattern.matcher()
  // var matcher = p.matcher(str);

  // Return if the string matched the ReGex
  return regex.test(str);
}

//capitalize_Words
function capitalize(str) {
  return str.replace(/\w\S*/g, function(txt) {
    return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
  });
}

function validateYoutubeLink(str) {
  var regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
  var match = str.match(regExp);
  return match;
}

const StringUtil = {
  decimalValue,
  formatThousand,
  formatCurrency,
  formatThousandWithAbbreviation,
  formatCurrencyWithAbbreviation,
  concealValue,
  replace,
  compareStrings,
  validateEmailFormat,
  isValidHTMLTag,
  validateYoutubeLink,
  capitalize,
};

export {StringUtil};
