import {ObjectUtil, SetUtil, CollectionConversionUtil} from '../../../utils';
import {Models, FilterOptions} from '../../../constants';
import {
  PropertyTypeValueSet,
  CondoApt_Model_Set,
  Landed_Model_Set,
  Furnishing_Options,
} from '../../PropertySearch/Constants';

var PropertyTypeOption = FilterOptions.FilterOptionDescription;
var ModelType = Models.Model;

const FilterOptionsHelper = {
  isHDBOnly,
  isResidentialOrCommercial,
  getPropertyTypeValueSet,
  propertyTypeSearchOptions,
  getPriceRangeValueArray,
  getBedroomsSet,
  getBathroomSet,
  getPSFValueArray,
  getFloorAreaArray,
  getTenureValueSet,
  getBuildYearValueArray,
  getFurnishValueSet,
  getFurnishedKeyDescription,
  getLeaseTermValueSet,
  getFloorLevelValueSet,
  getSelectedFilterOptionsCount,
  isLandedOnly,
};

function isHDBOnly(searchOptions) {
  let isHDBOnly = false;
  if (!ObjectUtil.isEmpty(searchOptions)) {
    let residentialSubTypeSet = new Set();
    searchOptions.cdResearchSubTypes.split(',').forEach(value => {
      residentialSubTypeSet.add(parseInt(value));
    });
    if (
      SetUtil.isSuperset(PropertyTypeValueSet.hdb, residentialSubTypeSet) &&
      SetUtil.isSuperset(residentialSubTypeSet, PropertyTypeValueSet.hdb)
    ) {
      isHDBOnly = true;
    }
  }
  return isHDBOnly;
}

function isResidentialOrCommercial(searchOptions) {
  let isResidential = true;
  if (!ObjectUtil.isEmpty(searchOptions)) {
    let commercialSubTypeSet = new Set();
    searchOptions.cdResearchSubTypes.split(',').forEach(value => {
      commercialSubTypeSet.add(parseInt(value));
    });
    if (
      SetUtil.isSuperset(PropertyTypeValueSet.commercial, commercialSubTypeSet)
    ) {
      isResidential = false;
    }
  }
  return isResidential;
}

function getPropertyTypeValueSet(searchOptions) {
  let propertyTypeSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    //Sub Types
    if (searchOptions.cdResearchSubTypes) {
      searchOptions.cdResearchSubTypes.split(',').forEach(value => {
        if (isNaN(value)) {
          propertyTypeSet.add(value);
        } else {
          propertyTypeSet.add(parseInt(value));
        }
      });
    }

    //models and modelNotRequired is empty
    if (
      ObjectUtil.isEmpty(searchOptions.models) &&
      ObjectUtil.isEmpty(searchOptions.modelsNotRequired)
    ) {
      if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.condo)) {
        //Condo
        propertyTypeSet.add(ModelType.executive_Condo);
        propertyTypeSet.add(ModelType.walkup_Apt);
      }
      if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.landed)) {
        //Landed
        propertyTypeSet.add(ModelType.cluster_Hse);
      }
    } else {
      //models
      if (searchOptions.models) {
        searchOptions.models.split(',').forEach(value => {
          propertyTypeSet.add(value);
        });
        if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.condo)) {
          //For Condo
          propertyTypeSet = SetUtil.difference(
            propertyTypeSet,
            PropertyTypeValueSet.condo,
          );
        }
        if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.landed)) {
          //For Landed
          propertyTypeSet = SetUtil.difference(
            propertyTypeSet,
            PropertyTypeValueSet.landed,
          );
        }
      }

      //Models Not Required
      if (searchOptions.modelsNotRequired) {
        searchOptions.modelsNotRequired.split(',').forEach(value => {
          if (value === ModelType.executive_Condo) {
            propertyTypeSet.add(ModelType.walkup_Apt);
          } else if (value === ModelType.walkup_Apt) {
            propertyTypeSet.add(ModelType.executive_Condo);
          } else {
            propertyTypeSet.add(ModelType.cluster_Hse);
          }
        });
      }
    }
  }
  return propertyTypeSet;
}

//check model included or not
function propertyTypeSearchOptions(propertyTypeSet, searchOptions, section) {
  var models = '';
  var modelsNotRequired = '';
  var cdResearchSubTypes = '';

  //removed models from selected proeprty type
  var removedModelsPropertyTypeSet = SetUtil.difference(
    SetUtil.difference(new Set(propertyTypeSet), CondoApt_Model_Set),
    Landed_Model_Set,
  );

  //For Other PropertyType Except from landed and condo
  var newOptions = {
    ...searchOptions,
    cdResearchSubTypes: Array.from(removedModelsPropertyTypeSet).join(','),
    modelsNotRequired: '',
    models: '',
  };

  if (!ObjectUtil.isEmpty(section)) {
    if (
      section.key === PropertyTypeOption.condo ||
      section.key === PropertyTypeOption.landed
    ) {
      if (section.key === PropertyTypeOption.condo) {
        if (isCondoOrModelSelected(propertyTypeSet)) {
          //For Condo
          if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.condo)) {
            if (isModelNotSelected(propertyTypeSet)) {
              modelsNotRequired =
                ModelType.executive_Condo + ',' + ModelType.walkup_Apt;
            } else if (isOnlyExecutiveCondoSelected(propertyTypeSet)) {
              modelsNotRequired = ModelType.walkup_Apt;
            } else if (isOnlyWalkupAptSelected(propertyTypeSet)) {
              modelsNotRequired = ModelType.executive_Condo;
            }
            cdResearchSubTypes = Array.from(removedModelsPropertyTypeSet).join(
              ',',
            ); //need
          } else {
            if (isOnlyExecutiveCondoSelected(propertyTypeSet)) {
              models = ModelType.executive_Condo;
            } else if (isOnlyWalkupAptSelected(propertyTypeSet)) {
              models = ModelType.walkup_Apt;
            } else if (isAllCondoModelSelected(propertyTypeSet)) {
              models = ModelType.executive_Condo + ',' + ModelType.walkup_Apt;
            }
            //remove models and add condo to cdResearchSubType
            cdResearchSubTypes = Array.from(
              SetUtil.union(
                removedModelsPropertyTypeSet,
                PropertyTypeValueSet.condo,
              ),
            ).join(','); //need
          }
        } else {
          cdResearchSubTypes = Array.from(removedModelsPropertyTypeSet).join(
            ',',
          ); //need
        }
      } else if (section.key === PropertyTypeOption.landed) {
        if (isLandedOrModelSelected(propertyTypeSet)) {
          //For Landed
          if (
            SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.landed)
          ) {
            if (!propertyTypeSet.has(ModelType.cluster_Hse)) {
              modelsNotRequired = ModelType.cluster_Hse;
            }
            cdResearchSubTypes = Array.from(removedModelsPropertyTypeSet).join(
              ',',
            ); //need
          } else {
            if (propertyTypeSet.has(ModelType.cluster_Hse)) {
              models = ModelType.cluster_Hse;
            }
            cdResearchSubTypes = Array.from(
              SetUtil.union(
                removedModelsPropertyTypeSet,
                PropertyTypeValueSet.landed,
              ),
            ).join(','); //need
          }
        } else {
          cdResearchSubTypes = Array.from(removedModelsPropertyTypeSet).join(
            ',',
          ); //need
        }
      }
      newOptions = {
        ...searchOptions,
        cdResearchSubTypes,
        modelsNotRequired,
        models,
      };
    }
  }

  return newOptions;
}

//Check condo is selected or not
function isCondoOrModelSelected(propertyTypeSet) {
  var isCondoOrModelSelected = false;
  if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.condo)) {
    isCondoOrModelSelected = true;
  }
  if (
    propertyTypeSet.has(ModelType.executive_Condo) ||
    propertyTypeSet.has(ModelType.walkup_Apt)
  ) {
    isCondoOrModelSelected = true;
  }
  return isCondoOrModelSelected;
}

function isLandedOrModelSelected(propertyTypeSet) {
  var isLandedOrModelSelected = false;
  if (SetUtil.isSuperset(propertyTypeSet, PropertyTypeValueSet.landed)) {
    isLandedOrModelSelected = true;
  }
  if (propertyTypeSet.has(ModelType.cluster_Hse)) {
    isLandedOrModelSelected = true;
  }
  return isLandedOrModelSelected;
}

function isModelNotSelected(propertyTypeSet) {
  var isModelNotSelected = false;
  if (
    !propertyTypeSet.has(ModelType.executive_Condo) &&
    !propertyTypeSet.has(ModelType.walkup_Apt)
  ) {
    isModelNotSelected = true;
  }
  return isModelNotSelected;
}

function isOnlyExecutiveCondoSelected(propertyTypeSet) {
  if (
    propertyTypeSet.has(ModelType.executive_Condo) &&
    !propertyTypeSet.has(ModelType.walkup_Apt)
  ) {
    return true;
  }
  return false;
}

function isOnlyWalkupAptSelected(propertyTypeSet) {
  if (
    propertyTypeSet.has(ModelType.walkup_Apt) &&
    !propertyTypeSet.has(ModelType.executive_Condo)
  ) {
    return true;
  }
  return false;
}

function isAllCondoModelSelected(propertyTypeSet) {
  if (
    propertyTypeSet.has(ModelType.walkup_Apt) &&
    propertyTypeSet.has(ModelType.executive_Condo)
  ) {
    return true;
  }
  return false;
}

//Price Range
function getPriceRangeValueArray(searchOptions) {
  let priceArray = [];
  let minPrice = '';
  let maxPrice = '';
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.type === 'S') {
      if (searchOptions.minSalePrice) {
        minPrice = searchOptions.minSalePrice;
      }
      if (searchOptions.maxSalePrice) {
        maxPrice = searchOptions.maxSalePrice;
      }
    } else {
      if (searchOptions.minRentPrice) {
        minPrice = searchOptions.minRentPrice;
      }
      if (searchOptions.maxRentPrice) {
        maxPrice = searchOptions.maxRentPrice;
      }
    }
    priceArray.push(minPrice, maxPrice);
  }
  return priceArray;
}

//Bedrooms
function getBedroomsSet(searchOptions) {
  let bedRoomSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.bedrooms) {
      searchOptions.bedrooms.split(',').forEach(value => {
        bedRoomSet.add(value);
      });
    } else {
      //For Any Selection
      bedRoomSet.add('');
    }
  }
  return bedRoomSet;
}

//Bathrooms
function getBathroomSet(searchOptions) {
  let bathRoomSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.bathrooms) {
      searchOptions.bathrooms.split(',').forEach(value => {
        bathRoomSet.add(value);
      });
    } else {
      //For Any Selection
      bathRoomSet.add('');
    }
  }
  return bathRoomSet;
}

//PSF Value
function getPSFValueArray(searchOptions) {
  let psfArray = [];
  let minPSF = '';
  let maxPSF = '';
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.minPSF) {
      minPSF = searchOptions.minPSF;
    }
    if (searchOptions.maxPSF) {
      maxPSF = searchOptions.maxPSF;
    }
    psfArray.push(minPSF, maxPSF);
  }
  return psfArray;
}

//Floor Area
function getFloorAreaArray(searchOptions) {
  let floorSizeArray = [];
  let minBuiltSize = '';
  let maxBuiltSize = '';
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.minBuiltSize) {
      minBuiltSize = searchOptions.minBuiltSize;
    }
    if (searchOptions.maxBuiltSize) {
      maxBuiltSize = searchOptions.maxBuiltSize;
    }
    floorSizeArray.push(minBuiltSize, maxBuiltSize);
  }
  return floorSizeArray;
}

//Tenure
function getTenureValueSet(searchOptions) {
  let tenureSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.cdTenure) {
      searchOptions.cdTenure.split(',').forEach(value => {
        tenureSet.add(parseInt(value));
      });
    }
  }
  return tenureSet;
}

//Construction Year
function getBuildYearValueArray(searchOptions) {
  let builtYearArray = [];
  let builtYearMin = '';
  let builtYearMax = '';
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.builtYearMin) {
      builtYearMin = searchOptions.builtYearMin;
    }
    if (searchOptions.builtYearMax) {
      builtYearMax = searchOptions.builtYearMax;
    }
    builtYearArray.push(builtYearMin, builtYearMax);
  }
  return builtYearArray;
}

//Furnishing
function getFurnishValueSet(searchOptions) {
  let furnishSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.furnish) {
      searchOptions.furnish.split(',').forEach(value => {
        furnishSet.add(value);
      });
    }
  }
  return furnishSet;
}

function getFurnishedKeyDescription(furnishSet) {
  let selectedFurnishedList = '';
  if (
    !ObjectUtil.isEmpty(Furnishing_Options) &&
    !ObjectUtil.isEmpty(Furnishing_Options[0])
  ) {
    if (Array.isArray(Furnishing_Options[0].data)) {
      furnishSet.forEach(item => {
        selectedFurnishedList +=
          Furnishing_Options[0].data.find(option => option.value == item).key +
          ',';
      });
    }
  }
  return selectedFurnishedList;
}

//Lease Term
function getLeaseTermValueSet(searchOptions) {
  let leaseTermSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.leaseTermOptions) {
      searchOptions.leaseTermOptions.split(',').forEach(value => {
        leaseTermSet.add(parseInt(value));
      });
    }
  }
  return leaseTermSet;
}

//Floor Level
function getFloorLevelValueSet(searchOptions) {
  let floorSet = new Set();
  if (!ObjectUtil.isEmpty(searchOptions)) {
    if (searchOptions.floor) {
      searchOptions.floor.split(',').forEach(value => {
        floorSet.add(value);
      });
    }
  }
  return floorSet;
}

//get filter options description
function getSelectedFilterOptionsCount(searchOptions) {
  var selectedFilterOptionsCount = 0;
  if (!ObjectUtil.isEmpty(searchOptions)) {
    //For room rental
    const {isRoomRental, type} = searchOptions;
    if (isRoomRental && type === 'R') {
      selectedFilterOptionsCount++;
    }

    //sale and rent price
    if (type === 'S') {
      //sale price
      const {minSalePrice, maxSalePrice} = searchOptions;
      if (minSalePrice || maxSalePrice) {
        selectedFilterOptionsCount++;
      }
    } else if (type === 'R') {
      //rent price
      const {minRentPrice, maxRentPrice} = searchOptions;
      if (minRentPrice || maxRentPrice) {
        selectedFilterOptionsCount++;
      }
    }

    //bedrooms
    const {bedrooms} = searchOptions;
    if (bedrooms) {
      selectedFilterOptionsCount++;
    }

    //bathrooms
    const {bathrooms} = searchOptions;
    if (bathrooms) {
      selectedFilterOptionsCount++;
    }

    //PSF
    const {minPSF, maxPSF} = searchOptions;
    if (minPSF || maxPSF) {
      selectedFilterOptionsCount++;
    }

    //Floor Area
    const {minBuiltSize, maxBuiltSize} = searchOptions;
    if (minBuiltSize || maxBuiltSize) {
      selectedFilterOptionsCount++;
    }

    //Floor level
    const {floor} = searchOptions;
    if (floor) {
      selectedFilterOptionsCount++;
    }

    //Tenure
    const {cdTenure} = searchOptions;
    if (cdTenure) {
      selectedFilterOptionsCount++;
    }

    //construction, build year
    const {builtYearMin, builtYearMax} = searchOptions;
    if (builtYearMin || builtYearMax) {
      selectedFilterOptionsCount++;
    }

    //Furnish
    const {furnish} = searchOptions;
    if (furnish) {
      selectedFilterOptionsCount++;
    }

    //Listed date
    const {minDateFirstPosted} = searchOptions;
    if (minDateFirstPosted) {
      selectedFilterOptionsCount++;
    }

    //Lease Term for Rent
    const {leaseTermOptions} = searchOptions;
    if (leaseTermOptions) {
      selectedFilterOptionsCount++;
    }

    //x-listing price
    const {spvFilter} = searchOptions;
    if (spvFilter) {
      selectedFilterOptionsCount++;
    }

    //v360 virtual tour
    const {vt360Filter} = searchOptions;
    if (vt360Filter) {
      selectedFilterOptionsCount++;
    }

    //drone view
    const {droneViewFilter} = searchOptions;
    if (droneViewFilter) {
      selectedFilterOptionsCount++;
    }

    //exclusive
    const {exclusiveFilter} = searchOptions;
    if (exclusiveFilter) {
      selectedFilterOptionsCount++;
    }

    //certified
    const {ownerCertificationFilter} = searchOptions;
    if (ownerCertificationFilter) {
      selectedFilterOptionsCount++;
    }
  }
  return selectedFilterOptionsCount;
}

function isLandedOnly(cdResearchSubTypes) {
  var landedSubTypes = CollectionConversionUtil.convertValueStringToSet(
    cdResearchSubTypes,
  );

  if (landedSubTypes.size !== PropertyTypeValueSet.landed.size) return false;
  return (
    SetUtil.isSuperset(landedSubTypes, PropertyTypeValueSet.landed) &&
    SetUtil.isSuperset(PropertyTypeValueSet.landed, landedSubTypes)
  );
}

export {FilterOptionsHelper};
