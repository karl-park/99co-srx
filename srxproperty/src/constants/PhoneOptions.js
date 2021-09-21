const SMS = 1;
const SMS_TITLE = 'SMS';

const CALL = 2;
const CALL_TITLE = 'Call';

export const PhoneOptions = {
  Sms: SMS,
  Call: CALL,
};

export const PhoneOptionsDescription = {
  Sms: SMS_TITLE,
  Call: CALL_TITLE,
};

export const PHONE_OPTIONS = [
  {
    key: SMS_TITLE,
    value: SMS,
  },
  {
    key: CALL_TITLE,
    value: CALL,
  },
];
