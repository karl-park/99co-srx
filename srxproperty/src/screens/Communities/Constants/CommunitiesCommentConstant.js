const SORT_COMMENT_OLDEST_TO_NEWEST = 'OldToNew';
const SORT_COMMENT_TITLE_OLDEST_TO_NEWEST = 'Oldest to newest';

const SORT_COMMENT_NEWEST_TO_OLDEST = 'NewToOld';
const SORT_COMMENT_TITLE_NEWEST_TO_OLDEST = 'Newest to oldest';

const SortCommentTypeValue = {
  oldestToNewest: SORT_COMMENT_OLDEST_TO_NEWEST,
  newestToOldest: SORT_COMMENT_NEWEST_TO_OLDEST,
};

const SortCommentTypeDescription = {
  oldestToNewest: SORT_COMMENT_TITLE_OLDEST_TO_NEWEST,
  newestToOldest: SORT_COMMENT_TITLE_NEWEST_TO_OLDEST,
};

const SORT_COMMENT_ARRAY = [
  {
    key: SORT_COMMENT_TITLE_OLDEST_TO_NEWEST,
    value: SORT_COMMENT_OLDEST_TO_NEWEST,
  },
  {
    key: SORT_COMMENT_TITLE_NEWEST_TO_OLDEST,
    value: SORT_COMMENT_NEWEST_TO_OLDEST,
  },
];

const DefaultCommentSort = {
  key: SORT_COMMENT_TITLE_OLDEST_TO_NEWEST,
  value: SORT_COMMENT_OLDEST_TO_NEWEST,
};

const CommunitiesCommentConstant = {
  defaultCommentSort: DefaultCommentSort,
  sortOptions: SORT_COMMENT_ARRAY,
  sortOptionsValue: SortCommentTypeValue,
  sortOptionsDescription: SortCommentTypeDescription,
};

export {CommunitiesCommentConstant};
