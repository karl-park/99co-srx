import React, {Component} from 'react';
import {View} from 'react-native';
import PropTypes from 'prop-types';
import {Avatar, BodyText, Heading2} from '../../../../components';
import {Spacing} from '../../../../styles';
import {ObjectUtil} from '../../../../utils';

class PostingInformation extends Component {
  renderPostDate() {
    const {listingPO} = this.props;
    if (
      !ObjectUtil.isEmpty(listingPO)
    ) {
      const date = listingPO.getFormattedActualDatePosted(' days ago');
      if (!ObjectUtil.isEmpty(date)) {
        return <BodyText>{' on ' + date}</BodyText>;
      }
    }
  }

  renderAgent() {
    const {listingPO} = this.props;
    if (!ObjectUtil.isEmpty(listingPO)) {
      const {agentPO} = listingPO;
      if (!ObjectUtil.isEmpty(agentPO)) {
        return (
          <View
            style={{
              flexDirection: 'row',
              alignItems: 'center',
              flex: 1,
            }}>
              <BodyText>
                {' by '}
              </BodyText>
            <Avatar
              name={agentPO.name}
              imageUrl={agentPO.photo}
              size={45}
              borderColor={'#DCDCDC'}
              textSize={25}
            />
            <Heading2
              style={{marginLeft: Spacing.XS, flex: 1}}
              numberOfLines={2}>
              {agentPO.name}
            </Heading2>
          </View>
        );
      }
    }
  }

  render() {
    const {listingPO, onLayout} = this.props;
    return (
      <View
        onLayout={onLayout}
        style={{
          flexDirection: 'row',
          justifyContent: 'space-between',
          alignItems: 'center',
          paddingVertical: Spacing.XS,
          paddingHorizontal: Spacing.M,
        }}>
        <BodyText>Posted</BodyText>
        {this.renderPostDate()}
        {this.renderAgent()}
      </View>
    );
  }
}

export {PostingInformation};
