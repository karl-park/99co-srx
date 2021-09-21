import Moment from 'moment';

const DATE_TIME_FORMAT_1 = 'DD MMM YYYY hh:mm a';
const DATE_TIME_FORMAT_2 = 'DD-MMM-YYYY';

getCurrentUnixTimeInMilliSeconds = () => {
  //note: unix time return seconds
  return Moment().unix() * 1000;
};

getDurationDifference = (endDate, startDate) => {
  return Moment.duration(Moment(endDate).diff(Moment(startDate)));
};

getFormattedCurrentTime = format => {
  //Note: when user doesn't pass format, use DATE_TIME_FORMAT_1 as default
  var tempFormat = format ? format : DATE_TIME_FORMAT_1;
  return Moment().format(tempFormat);
};

const DateTimeUtil = {
  //const variables
  DATE_TIME_FORMAT_1,
  DATE_TIME_FORMAT_2,
  //functions
  getCurrentUnixTimeInMilliSeconds,
  getDurationDifference,
  getFormattedCurrentTime,
};

export {DateTimeUtil};
