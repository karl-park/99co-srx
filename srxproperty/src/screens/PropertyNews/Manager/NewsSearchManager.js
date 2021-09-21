import React, { Component } from "react";
import { NewsPropertyService } from "../../../services";
import { ObjectUtil } from "../../../utils";
import { OnlineCommunicationPO } from "../../../dataObject";
import { Channel_Constants } from "../Constants";

class NewsSearchManager {
  allResults = [];
  isLoading = false;
  page = 1;
  hasData = false;

  constructor(data) {
    if (data) {
      const { defaultCategory, searchText } = data;

      //get params from search manager
      this.options = {
        ...this.options,
        defaultCategory,
        searchText
      };
    }
  }

  register = callBack => {
    this.callBack = callBack;
  };

  sendBackResponse = (allResults, allNewsList, error) => {
    if (this.callBack) {
      this.callBack({
        allResults,
        allNewsList,
        error,
        manager: this
      });
    }
  };

  canLoadMore = () => {
    return this.hasData;
  };

  search = pageNo => {
    if (this.isLoading) return;
    this.allResults = [];
    if (pageNo) {
      this.page = pageNo;
    }

    this.loadPropertyNewLists();
  };

  loadMore = () => {
    if (this.isLoading) return;

    if (!this.canLoadMore()) return;

    this.page = this.page + 1;

    this.loadPropertyNewLists();
  };

  loadPropertyNewLists = () => {
    this.isLoading = true;

    const { defaultCategory, searchText } = this.options;

    NewsPropertyService.loadOnlineCommunicationsWithOptionalSearch({
      id: defaultCategory,
      title: searchText,
      channel: Channel_Constants.propertyNews,
      limit: 10,
      page: this.page
    })
      .then(response => {
        this.isLoading = false;
        const { result } = response;
        const allNewsList = [];

        if (!ObjectUtil.isEmpty(result)) {
          result.map(item => {
            allNewsList.push(new OnlineCommunicationPO(item));
          });
          if (!ObjectUtil.isEmpty(allNewsList)) {
            this.allResults = [...this.allResults, ...allNewsList];
          }
          this.hasData = true;
        } else {
          //there is no news result
          this.isLoading = false;
          this.hasData = false;
        }
        this.sendBackResponse(this.allResults, allNewsList, null);
      })
      .catch(error => {
        this.isLoading = false;
        console.log(
          "NewsPropertyService.loadOnlineCommunicationsWithOptionalSearch - failed - NewsSearchManager"
        );
        console.log(error);
      });
  };
}
export { NewsSearchManager };
