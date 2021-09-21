//the object in the array, “key” should act as the "title", “value“ should be the "id"
const SORT_POST_MOST_RECENT = 0;
const SORT_POST_TITLE_MOST_RECENT = 'Most recent';

const SORT_POST_POPULAR = 1;
const SORT_POST_TITLE_POPULAR = 'Popular';

const SORT_POST_TRENDING = 2;
const SORT_POST_TITLE_TRENDING = 'Trending';

export const SortPostTypeValue = {
  mostrecent: SORT_POST_MOST_RECENT,
  popular: SORT_POST_POPULAR,
  trending: SORT_POST_TRENDING,
};

export const SortPostTypeDescription = {
  mostrecent: SORT_POST_TITLE_MOST_RECENT,
  popular: SORT_POST_TITLE_POPULAR,
  trending: SORT_POST_TITLE_TRENDING,
};

export const SORT_POST_ARRAY = [
  {
    key: SORT_POST_TITLE_MOST_RECENT,
    value: SORT_POST_MOST_RECENT,
  },
  {
    key: SORT_POST_TITLE_POPULAR,
    value: SORT_POST_POPULAR,
  },
  {
    key: SORT_POST_TITLE_TRENDING,
    value: SORT_POST_TRENDING,
  },
];
