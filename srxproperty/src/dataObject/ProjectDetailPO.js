import { ObjectUtil, CommonUtil, NumberUtil } from "../utils";
import { DroneViewPO } from "../dataObject";

class ProjectDetailPO {
  constructor(data) {
    if (!ObjectUtil.isEmpty(data)) {
      this.id = data.id;
      this.address = data.address;
      this.name = data.name;
      this.propertySubtype = data.propertySubtype;
      this.propertySubtypeDescription = data.propertySubtypeDescription;
      this.propertyType = data.propertyType;
      this.marketingCircleListingMinPriceSale =
        data.marketingCircleListingMinPriceSale;
      this.marketingCircleListingMaxPriceSale =
        data.marketingCircleListingMaxPriceSale;
      this.url = data.url;
      //FOR Drone Views
      this.droneViews = [];
      if (!ObjectUtil.isEmpty(data.droneViews)) {
        data.droneViews.map(item => {
          this.droneViews.push(new DroneViewPO(item));
        });
      }
      //FOR Project Image links
      this.projectImageLinks = [];
      if (!ObjectUtil.isEmpty(data.projectImageLinks)) {
        data.projectImageLinks.map(item => {
          this.projectImageLinks.push(item); //need to change to image
        });
      }
    }
  } //end of constructor

  getProjectId = () => {
    const { id } = this;
    return id;
  };

  getProjectAddress = () => {
    const { address } = this;
    return address;
  };

  getProjectName = () => {
    const { name } = this;
    return name;
  };

  getProjectTypeDescription = () => {
    const { propertySubtypeDescription } = this;
    return propertySubtypeDescription;
  };

  getProjectPriceRange = () => {
    const {
      marketingCircleListingMinPriceSale,
      marketingCircleListingMaxPriceSale
    } = this;
    let priceRange = "";

    if (
      NumberUtil.floatValue(marketingCircleListingMinPriceSale) !== 0 &&
      NumberUtil.floatValue(marketingCircleListingMaxPriceSale) !== 0
    ) {
      priceRange =
        marketingCircleListingMinPriceSale +
        " - " +
        marketingCircleListingMaxPriceSale;
    }
    return priceRange;
  };

  getProjectUrl = () => {
    const { url } = this;
    return url;
  };

  //Drone
  getProjectDroneViews = () => {
    const { droneViews } = this;

    return droneViews;
  };

  hasProjectDroneView = () => {
    let droneView = this.getProjectDroneViews();

    return !ObjectUtil.isEmpty(droneView);
  };

  //Image Links
  getProjectImageLinks = () => {
    const { projectImageLinks } = this;
    return projectImageLinks;
  };

  hasProjectImageLinks = () => {
    let projectImageLinks = this.getProjectImageLinks();

    return !ObjectUtil.isEmpty(projectImageLinks);
  };

  getProjectImageUrl = () => {
    const { projectImageLinks } = this;
    let imageURL = "";
    if (!ObjectUtil.isEmpty(projectImageLinks)) {
      let item = projectImageLinks[0];
      if (item.thumbnailUrl) {
        imageURL = item.thumbnailUrl;
      } else {
        imageURL = item.url;
      }
    }
    // projectImageLinks.forEach((item, index) => {
    //   if (index != lastIndex) {
    //     if (item.thumbnailUrl) {
    //       imageURL = item.thumbnailUrl;
    //     } else {
    //       imageURL = item.url;
    //     }
    //   }
    // });

    return CommonUtil.handleImageUrl(imageURL);
  };
}

export { ProjectDetailPO };
