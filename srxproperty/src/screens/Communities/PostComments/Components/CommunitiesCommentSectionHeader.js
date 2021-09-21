import React, {Component} from 'react';
import {View, ActivityIndicator, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';

import {CommunitiesCommentSortOptions} from './CommunitiesCommentSortOptions';
import {CommunitiesCommentConstant} from '../../Constants';
import {CommunitiesCommentEmptyTemplate} from './CommunitiesCommentEmptyTemplate';
import {LoadingState, SRXColor} from '../../../../constants';
import {BodyText} from '../../../../components';
import {Spacing} from '../../../../styles';
import {ObjectUtil} from '../../../../utils';

class CommunitiesCommentSectionHeader extends Component {
  constructor(props) {
    super(props);

    const {sortType, loadingState} = this.props;

    this.state = {
      initialSortType:
        sortType ?? CommunitiesCommentConstant.defaultCommentSort,
      loadingState: loadingState ?? LoadingState.Normal,
    };

    this.onSelectSortOption = this.onSelectSortOption.bind(this);
    this.loadMore = this.loadMore.bind(this);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.sortType !== this.props.sortType) {
      this.setState({initialSortType: this.props.sortType});
    }

    if (prevProps.loadingState !== this.props.loadingState) {
      this.setState({loadingState: this.props.loadingState});
    }
  }

  onSelectSortOption(item) {
    const {onSortTypeUpdate} = this.props;
    if (onSortTypeUpdate) {
      onSortTypeUpdate(item);
    }
  }

  loadMore() {
    const {onLoadPreviousComments} = this.props;
    if (onLoadPreviousComments) {
      onLoadPreviousComments();
    }
  }

  renderViewPreviousComments() {
    const {loadingState} = this.state;
    const {commentsTotal, section} = this.props;
    if (loadingState == LoadingState.Loading) {
      return (
        <ActivityIndicator
          style={{
            alignSelf: 'flex-start',
            margin: Spacing.M,
          }}
        />
      );
    } else if (
      !ObjectUtil.isEmpty(section) &&
      section.data.length < commentsTotal
    ) {
      return (
        <TouchableOpacity
          onPress={() => this.loadMore()}
          style={{marginTop: Spacing.M, marginBottom: Spacing.S}}>
          <BodyText style={{color: SRXColor.Teal}}>
            View previous comments
          </BodyText>
        </TouchableOpacity>
      );
    }
  }

  render() {
    const {initialSortType} = this.state;
    const {section} = this.props;
    return (
      <View>
        <CommunitiesCommentSortOptions
          section={section}
          sortType={initialSortType}
          onSortTypeUpdate={this.onSelectSortOption}
        />
        {!ObjectUtil.isEmpty(section) && section.data.length === 0 ? (
          <CommunitiesCommentEmptyTemplate />
        ) : initialSortType.value ==
          CommunitiesCommentConstant.sortOptionsValue.oldestToNewest ? (
          <View style={{backgroundColor: SRXColor.LightGray}}>
            <View
              style={{
                backgroundColor: SRXColor.White,
                paddingRight: Spacing.M,
                paddingLeft: Spacing.M,
                borderTopLeftRadius: 10,
                borderTopRightRadius: 10,
              }}>
              {this.renderViewPreviousComments()}
            </View>
          </View>
        ) : null}
      </View>
    );
  }
}

CommunitiesCommentSectionHeader.propTypes = {
  /**
   * comment section to get comment list length
   */
  section: PropTypes.object,
  /**
   * selected comment option
   */
  sortType: PropTypes.object,
  /**
   * to show loading indicator for load more
   */
  loadingState: PropTypes.object,
  /**
   * on change sort option of comment list
   */
  onSortTypeUpdate: PropTypes.func,
  /**
   * load previous comments
   */
  onLoadPreviousComments: PropTypes.func,
  /**
   * to check showing view previous comment or not
   */
  commentsTotal: PropTypes.number,
};
export {CommunitiesCommentSectionHeader};
