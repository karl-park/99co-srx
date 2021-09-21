import {CommunityItem} from '../../../dataObject';

const COMMUNITIES_POST_TYPE_NORMAL = 1;
const COMMUNITIES_POST_TYPE_COMMENT = 2;
const COMMUNITIES_POST_TYPE_LISTING = 3;
const COMMUNITIES_POST_TYPE_TRANSACTED_LISTING = 4;
const COMMUNITIES_POST_TYPE_NEWS = 5;
const COMMUNITIES_POST_TYPE_SPONSORED = 6;

const COMMUNITIES_POST_MEDIA_TYPE_PHOTO = 1;
const COMMUNITIES_POST_MEDIA_TYPE_VIDEO = 2;

const COMMUNITIES_POST_SORT_TYPE_MOST_RECENT = 'MOST_RECENT';
const COMMUNITIES_POST_SORT_TYPE_POPULAR = 'POPULAR';
const COMMUNITIES_POST_SORT_TYPE_TRENDING = 'TRENDING';

const COMMUNITIES_POST_SORT_TYPE_TITLE_MOST_RECENT = 'Most recent';
const COMMUNITIES_POST_SORT_TYPE_TITLE_POPULAR = 'Popular';
const COMMUNITIES_POST_SORT_TYPE_TITLE_TRENDING = 'Trending';

const COMMUNITIES_LEVEL_PRIVATE = 1;
const COMMUNITIES_LEVEL_POSTAL = 56;
const COMMUNITIES_LEVEL_ESTATE = 64;
const COMMUNITIES_LEVEL_SUBZONE = 128;
const COMMUNITIES_LEVEL_PLANNING_AREA = 136;
const COMMUNITIES_LEVEL_COUNTRY = 144;

const COMMUNITIES_POST_SPONSORED_TYPE_LISTING = 1;
const COMMUNITIES_POST_SPONSORED_TYPE_BUSINESS = 2;

const COMMUNITIES_SRX_NEWS_INFO_USER_ID = 6;

const PostType = {
  normal: COMMUNITIES_POST_TYPE_NORMAL,
  comment: COMMUNITIES_POST_TYPE_COMMENT,
  listing: COMMUNITIES_POST_TYPE_LISTING,
  transactedListing: COMMUNITIES_POST_TYPE_TRANSACTED_LISTING,
  news: COMMUNITIES_POST_TYPE_NEWS,
  sponsored: COMMUNITIES_POST_TYPE_SPONSORED,
};

const SponsoredType = {
  listing: COMMUNITIES_POST_SPONSORED_TYPE_LISTING,
  business: COMMUNITIES_POST_SPONSORED_TYPE_BUSINESS,
};

const MediaType = {
  photo: COMMUNITIES_POST_MEDIA_TYPE_PHOTO,
  video: COMMUNITIES_POST_MEDIA_TYPE_VIDEO,
};

const SingaporeCommunity = new CommunityItem({
  id: 1,
  cdCommunityLevel: 144,
  name: 'My Communities', //'All of Singapore',
});

const CommunityLevel = {
  private: COMMUNITIES_LEVEL_PRIVATE,
  postal: COMMUNITIES_LEVEL_POSTAL,
  estate: COMMUNITIES_LEVEL_ESTATE,
  subzone: COMMUNITIES_LEVEL_SUBZONE,
  planningArea: COMMUNITIES_LEVEL_PLANNING_AREA,
  country: COMMUNITIES_LEVEL_COUNTRY,
};

const SortOptions = [
  {
    title: COMMUNITIES_POST_SORT_TYPE_TITLE_MOST_RECENT,
    value: COMMUNITIES_POST_SORT_TYPE_MOST_RECENT,
  },
  {
    title: COMMUNITIES_POST_SORT_TYPE_TITLE_POPULAR,
    value: COMMUNITIES_POST_SORT_TYPE_POPULAR,
  },
  {
    title: COMMUNITIES_POST_SORT_TYPE_TITLE_TRENDING,
    value: COMMUNITIES_POST_SORT_TYPE_TRENDING,
  },
];

const CommunityEvents = {
  updateFeedListEvent: 'updateFeedListEvent',
};

const CommunitySRXInfoUserIds = {
  newPostUserId: COMMUNITIES_SRX_NEWS_INFO_USER_ID,
};

const CommunitiesConstant = {
  singaporeCommunity: SingaporeCommunity,
  postType: PostType,
  mediaType: MediaType,
  sortOptions: SortOptions,
  events: CommunityEvents,
  communityLevel: CommunityLevel,
  sponsoredType: SponsoredType,
  srxInfoUserIds: CommunitySRXInfoUserIds,
};

export {CommunitiesConstant};
