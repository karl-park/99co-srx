import Moment, {months} from 'moment';

import {ObjectUtil, DateTimeUtil} from '../utils';
import {CommunityPostReactionPO} from './CommunityPostReactionPO';
import {CommunityPostMediaPO} from './CommunityPostMediaPO';
import {ListingPO} from './ListingPO';
import {CommunityPO} from './CommunityPO';
import {UserPO} from './UserPO';
import {Community} from '../screens';
import {BusinessPO} from './BusinessPO';
import { CommunityPostSponsoredPO } from './CommunityPostSponsoredPO';

//TODO: create more PO for objects inside CommunityPostPO
class CommunityPostPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.id = data.id;
      this.user = new UserPO(data.user);
      this.type = data.type;
      this.title = data.title;
      this.content = data.content;
      this.externalUrl = data.externalUrl;
      this.dateCreate = data.dateCreate;
      this.commentsTotal = data.commentsTotal;
      this.listing = new ListingPO(data.listing);

      if (!ObjectUtil.isEmpty(data.parentPost)) {
        this.parentPost = new CommunityPostPO(data.parentPost);
      }

      //business
      if (!ObjectUtil.isEmpty(data.business)) {
        this.business = new BusinessPO(data.business);
      }

      //community
      if (!ObjectUtil.isEmpty(data.community)) {
        this.community = new CommunityPO(data.community);
      }

      //communities
      this.communities = [];
      if (!ObjectUtil.isEmpty(data.communities)) {
        data.communities.map(item => {
          this.communities.push(new CommunityPO(item));
        });
      }

      //Media
      this.media = [];
      if (!ObjectUtil.isEmpty(data.media)) {
        data.media.map(item => {
          this.media.push(new CommunityPostMediaPO(item));
        });
      }

      //parent Post
      if (!ObjectUtil.isEmpty(data.parentPost)) {
        this.parentPost = new CommunityPostPO(data.parentPost);
      } else {
        this.parentPost = null;
      }

      //For Comments
      this.comments = [];
      if (!ObjectUtil.isEmpty(data.comments)) {
        data.comments.map(item => {
          this.comments.push(new CommunityPostPO(item));
        });
      }

      //For Reactions
      this.reactions = [];
      if (!ObjectUtil.isEmpty(data.reactions)) {
        data.reactions.map(item => {
          this.reactions.push(new CommunityPostReactionPO(item));
        });
      }

      this.transactedListings = [];
      if (!ObjectUtil.isEmpty(data.transactedListings)) {
        data.transactedListings.map(item => {
          this.transactedListings.push(new ListingPO(item));
        });
      }

      if (!ObjectUtil.isEmpty(data.sponsored)) {
        this.sponsored = new CommunityPostSponsoredPO(data.sponsored);
      } else {
        this.sponsored = null;
      }
    }
  }

  //TODO: Will be decided which one to use
  getDateDisplay = () => {
    if (this.dateCreate) {
      const postCreateDate = Moment.unix(this.dateCreate);
      const currentDate = Moment();

      const secondsDiff = currentDate.diff(postCreateDate, 'seconds');
      if (secondsDiff < 60) {
        return secondsDiff.toString() + 's';
      } else {
        const minsDiff = currentDate.diff(postCreateDate, 'minutes');
        if (minsDiff < 60) {
          return minsDiff.toString() + 'm';
        } else {
          const hoursDiff = currentDate.diff(postCreateDate, 'hours');
          if (hoursDiff < 24) {
            return hoursDiff.toString() + 'h';
          } else {
            const daysDiff = currentDate.diff(postCreateDate, 'days');
            if (daysDiff < 7) {
              return daysDiff.toString() + 'd';
            } else {
              const weeksDiff = currentDate.diff(postCreateDate, 'weeks');
              const monthsDiff = currentDate.diff(postCreateDate, 'months');

              if (monthsDiff < 1) {
                return weeksDiff.toString() + 'w';
              } else if (monthsDiff < 12) {
                return monthsDiff.toString() + 'mon';
              } else {
                const yearsDiff = currentDate.diff(postCreateDate, 'years');
                return yearsDiff.toString() + 'yr';
              }
            }
          }
        }
      }
    }
  };
}

export {CommunityPostPO};
