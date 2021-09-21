import React, {Component} from 'react';
import {View, Linking, Image} from 'react-native';
import PropTypes from 'prop-types';
import {Listing_WhatsappIcon} from '../../../../assets';
import {BodyText, Button, FeatherIcon, Heading2} from '../../../../components';
import {Spacing} from '../../../../styles';
import {SRXColor, ListingDetailsViewingItems} from '../../../../constants';
import {GoogleAnalyticUtil, ObjectUtil} from '../../../../utils';

class Description extends Component {
  static propTypes = {
    description: PropTypes.string,
  };

  static defaultProps = {
    description: '',
  };

  state = {
    showFullContent: false,
  };

  constructor(props) {
    super(props);

    this.onShowMoreOrLessPressed = this.onShowMoreOrLessPressed.bind(this);
    this.getDescriptionBody = this.getDescriptionBody.bind(this);
    this.redirectToWhatsapp = this.redirectToWhatsapp.bind(this);
  }

  onShowMoreOrLessPressed = () => {
    const {showFullContent} = this.state;
    const isToShowFullContent = !showFullContent;
    if (isToShowFullContent) {
      GoogleAnalyticUtil.trackListingDetailsUserActions({
        viewingItem: ListingDetailsViewingItems.description,
      });
    }
    this.setState({showFullContent: isToShowFullContent});
  };

  renderShowMoreButton() {
    const {showFullContent} = this.state;
    return (
      <View style={{alignItems: 'flex-end', paddingTop: Spacing.M}}>
        <Button
          textStyle={{fontSize: 14, color: SRXColor.Teal, marginRight: 4}}
          rightView={
            <FeatherIcon
              name={showFullContent ? 'chevron-up' : 'chevron-down'}
              size={16}
              color={SRXColor.Teal}
            />
          }
          onPress={this.onShowMoreOrLessPressed}>
          {showFullContent ? 'Close' : 'Show More'}
        </Button>
      </View>
    );
  }

  redirectToWhatsapp() {
    const {agentPO} = this.props;

    var url =
      'https://api.whatsapp.com/send?phone=65' + agentPO.getMobileNumber();

    Linking.canOpenURL(url)
      .then(supported => {
        if (!supported) {
          console.log(
            "listing details description - whatsapp - Can't handle url: " + url,
          );
        } else {
          return Linking.openURL(url);
        }
      })
      .catch(err =>
        console.error(
          'listing details description - An error occurred - whatsapp agent - ',
          err,
        ),
      );
  }

  getDescriptionBody() {
    const {description, onMaskMobilePress} = this.props;

    /**
     * AgentPO is required to get the mobile number while redirecting to whatsapp
     * although it is not required for display updates
     *
     * the instruction for using agentPO is from management/backend,
     * this might causing issues when the number in description is different with the mobile number in agentPO
     */
    if (!ObjectUtil.isEmpty(description)) {
      var regex = /[89]\d{3}[ ]{0,1}XXXX/g;
      const matches = description.match(regex);
      console.log(matches);

      if (!ObjectUtil.isEmpty(matches)) {
        let phoneTextArray = matches.map(item => {
          return {
            type: 'phone',
            text: item,
          };
        });

        const splits = description.split(regex);
        console.log(splits);

        let splitTextArray = [];

        if (!ObjectUtil.isEmpty(splits)) {
          splitTextArray = splits.map(item => {
            return {
              type: 'text',
              text: item,
            };
          });
          console.log(splitTextArray);
        }

        var l = Math.min(phoneTextArray.length, splitTextArray.length);
        //assuming splitTextArray is always 1 more than phoneTextArray
        //even if the description started with phone number, it should have an empty string as 1st

        var result = [];
        for (var i = 0; i < l; i++) {
          result.push(splitTextArray[i], phoneTextArray[i]);
        }
        result.push(...splitTextArray.slice(l), ...phoneTextArray.slice(l));

        console.log(result);

        return result.map(textItem => {
          if (textItem.type === 'phone') {
            return (
              <BodyText>
                <BodyText
                  style={{color: SRXColor.Teal}}
                  onPress={onMaskMobilePress}>
                  {textItem.text}{' '}
                </BodyText>
                <Image
                  style={{height: 16, width: 16}}
                  resizeMode={'contain'}
                  source={Listing_WhatsappIcon}
                />
              </BodyText>
            );
          } else {
            return textItem.text;
          }
        });
      } else {
        return description;
      }
    } else {
      return description;
    }
  }

  renderDescriptionText() {
    const {showFullContent} = this.state;

    const content = this.getDescriptionBody();

    if (showFullContent) {
      return <BodyText>{content}</BodyText>;
    } else {
      return <BodyText numberOfLines={4}>{content}</BodyText>;
    }
  }

  render() {
    const {style} = this.props;

    return (
      <View
        style={[
          {
            paddingHorizontal: Spacing.M,
            paddingTop: Spacing.M,
            paddingBottom: Spacing.L,
          },
          style,
        ]}>
        <Heading2 style={{marginBottom: Spacing.L}}>Description</Heading2>
        {this.renderDescriptionText()}
        {this.renderShowMoreButton()}
      </View>
    );
  }
}

export {Description};
