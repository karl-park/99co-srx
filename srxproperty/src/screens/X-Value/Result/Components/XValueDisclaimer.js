import React, {Component} from 'react';
import {View} from 'react-native';
import {SmallBodyText} from '../../../../components';
import {SRXColor} from '../../../../constants';

class XValueDisclaimer extends Component {
  render() {
    return (
      <SmallBodyText style={{color: SRXColor.Gray, lineHeight: 20}}>
        X-Value is provided for general reference and does not constitute a
        valuation by a licensed appraiser. StreetSine Singapore Pte Ltd accepts
        no liability whatsoever arising from any use of or reliance on the
        X-Value. Use of X-Value is subject to SRX's Terms of Use and Privacy
        Policy.
      </SmallBodyText>
    );
  }
}

export {XValueDisclaimer};
