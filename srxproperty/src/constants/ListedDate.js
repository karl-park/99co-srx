var year = new Date().getFullYear();
var month = new Date().getMonth();
var date = new Date().getDate();

var within3Days = new Date(year, month, date - 3);
var formattedWithin3Days = within3Days.getFullYear() + "-" +
    (within3Days.getMonth() + 1) + "-" +
    within3Days.getDate();

var lastWeekDate = new Date(year, month, date - 7);
var formattedLastWeekDate = lastWeekDate.getFullYear() + "-" +
    (lastWeekDate.getMonth() + 1) + "-" +
    lastWeekDate.getDate();

var lastTwoWeekDate = new Date(year, month, date - 14);
var formattedLastTwoWeekDate = lastTwoWeekDate.getFullYear() + "-" +
    (lastTwoWeekDate.getMonth() + 1) + "-" +
    lastTwoWeekDate.getDate();

var lastMonth = new Date(year, month - 1, date);
var formattedLastMonthDate = lastMonth.getFullYear() + "-" +
    (lastMonth.getMonth() + 1) + "-" +
    lastMonth.getDate();

const LISTED_WITHIN_3_DAYS = formattedWithin3Days;
const LISTED_TITLE_WITHIN_3_DAYS = "Within 3 days";

const LISTED_WITHIN_1_WEEK = formattedLastWeekDate;
const LISTED_TITLE_WITHIN_1_WEEK = "Within 1 week";

const LISTED_WITHIN_2_WEEK = formattedLastTwoWeekDate;
const LISTED_TITLE_WITHIN_2_WEEK = "Within 2 weeks";

const LISTED_WITHIN_1_MONTH = formattedLastMonthDate;
const LISTED_TITLE_WITHIN_1_MONTH = "Within 1 month";

export const Type = {
    Within_3_days: LISTED_WITHIN_3_DAYS,
    Within_1_week: LISTED_WITHIN_1_WEEK,
    Within_2_weeks: LISTED_WITHIN_2_WEEK,
    Within_1_month: LISTED_WITHIN_1_MONTH
}

export const Description = {
    Within_3_days: LISTED_TITLE_WITHIN_3_DAYS,
    Within_1_week: LISTED_TITLE_WITHIN_1_WEEK,
    Within_2_weeks: LISTED_TITLE_WITHIN_2_WEEK,
    Within_1_month: LISTED_TITLE_WITHIN_1_MONTH
}

export const LISTED_DATE_ARRAY = [
    {
        key: LISTED_TITLE_WITHIN_3_DAYS,
        value: LISTED_WITHIN_3_DAYS
    },
    {
        key: LISTED_TITLE_WITHIN_1_WEEK,
        value: LISTED_WITHIN_1_WEEK
    },
    {
        key: LISTED_TITLE_WITHIN_2_WEEK,
        value: LISTED_WITHIN_2_WEEK
    },
    {
        key: LISTED_TITLE_WITHIN_1_MONTH,
        value: LISTED_WITHIN_1_MONTH
    }
]