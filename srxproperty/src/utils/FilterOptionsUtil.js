import { StringUtil } from "./StringUtil";
import { PropertySize, Tenures } from "../constants";

const FilterOptionsUtil = {
    getBedAndBathRoomsOptions,
    getConstructionOptions,
    getSearchRentPricesOptions,
    getSearchSalePricesOptions,
    getSearchRentPsfOptions,
    getSearchSalePsfOptions,
    getSizeOptions,
    getTenureOptions,
    getTopYearOptions
};

const NON_SELECT_OPTION = { key: "Any", value: "" };

//Bath and Bed rooms lists
function getBedAndBathRoomsOptions() {
    let roomsArray = [];
    for (let i = 1; i <= 6; i++) {
        let label = i;
        if (i == 6) {
            label = label + "+"
        }
        roomsArray.push({ key: label, value: i.toString() });
    }
    return [NON_SELECT_OPTION, ...roomsArray];
}

//Built Year Lists
function getConstructionOptions() {
    const year = new Date().getFullYear();
    const builtYear = [];
    builtYear.push(NON_SELECT_OPTION);
    for (var i = year; i > 1960; i--) {
        builtYear.push({ key: i.toString(), value: i });
    }
    builtYear.push({ key: "Before 1960", value: 1959 });
    return builtYear;
}

//Price Option lists for RENT
function getSearchRentPricesOptions() {
    let val = 500;
    let max = 200000;
    let step = 250;
    let rentPriceArray = [];

    rentPriceArray.push(NON_SELECT_OPTION)
    while (val <= max) {
        switch (val) {
            case 10000: step = 1000; break;
            case 25000: step = 2500; break;
            case 50000: step = 10000; break;
            case 100000: step = 25000; break;
            default: step = 250; break;
        }
        rentPriceArray.push({ key: StringUtil.formatCurrency(val), value: val })
        val = val + step;
    }

    return rentPriceArray;
}

//Price Option Lists for SALE
function getSearchSalePricesOptions() {

    let val = 100000;
    let max = 50000000;
    let step = 50000;
    let salePriceArray = [];

    salePriceArray.push(NON_SELECT_OPTION)
    while (val <= max) {
        switch (val) {
            case 1000000: step = 100000; break;
            case 2000000: step = 250000; break;
            case 5000000: step = 500000; break;
            case 10000000: step = 1000000; break;
            case 20000000: step = 2500000; break;
            default: step = 50000; break;
        }
        salePriceArray.push({ key: StringUtil.formatCurrency(val), value: val })
        val = val + step;
    }

    return salePriceArray;
}

//Rent PSF
function getSearchRentPsfOptions() {
    let val = 1;
    let max = 5;
    let step = 1;
    let rentPsfArray = [];
    rentPsfArray.push(NON_SELECT_OPTION)
    while (val <= max) {
        rentPsfArray.push({ key: StringUtil.formatCurrency(val), value: val })
        val = val + step;
    }
    return rentPsfArray;
}

//Sale PSF
function getSearchSalePsfOptions() {

    let val = 200;
    let max = 1200;
    let step = 200;
    let salePsfArray = [];

    salePsfArray.push(NON_SELECT_OPTION)
    while (val <= max) {
        salePsfArray.push({ key: StringUtil.formatCurrency(val), value: val })
        val = val + step;
    }

    return salePsfArray;
}

//get Size 
function getSizeOptions() {
    return [NON_SELECT_OPTION, ...PropertySize.PROPERTY_SIZE_ARRAY];
}

//get Tenure
function getTenureOptions() {
    return [NON_SELECT_OPTION, ...Tenures.TENURE_FILTER_ARRAY];
}

//get Top year options
function getTopYearOptions() {
    let val = 1;
    let max = 5;
    let step = 2;
    let topYearArray = [];

    while (val <= max) {
        let label = "";
        if (val == 1) {
            label = val + " year";
        } else {
            label = val + "years"
        }
        topYearArray.push({ key: label, value: val })
        val = val + step;
    }
    return [NON_SELECT_OPTION, ...topYearArray];
}

export { FilterOptionsUtil };