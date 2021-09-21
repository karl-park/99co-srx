import PropTypes from "prop-types";
import { AgentSearchService } from "../../../services";
import { ObjectUtil } from "../../../utils";
import { AgentPO } from "../../../dataObject";

const SearchAgentOptionsStruct = PropTypes.shape({
  /*agent name, mobile, cea registration number and license number*/
  searchText: PropTypes.string,

  selectedDistrictIds: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number
  ]),

  selectedHdbTownIds: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),

  selectedAreaSpecializations: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number
  ])
});

class AgentSearchManager {
  featuredAgentResultArray = [];
  agentResultArray = [];
  totalAgentList = 0;
  isLoading = false;
  page = 1;

  constructor(data) {
    if (data) {
      const {
        searchText,
        selectedDistrictIds,
        selectedHdbTownIds,
        selectedAreaSpecializations
      } = data;

      console.log(data);

      this.options = {
        ...this.options,
        searchText,
        selectedDistrictIds,
        selectedHdbTownIds,
        selectedAreaSpecializations
      };
    }
  }

  register = callBack => {
    this.callBack = callBack;
  };

  sendBackResponse = (
    newAgentList,
    featuredAgentList,
    allAgentList,
    error,
    totalAgentList
  ) => {
    if (this.callBack) {
      this.callBack({
        newAgentList,
        featuredAgentList,
        allAgentList,
        error,
        totalAgentList,
        manager: this
      });
    }
  };

  canLoadMore = () => {
    return this.agentResultArray.length < this.totalAgentList;
  };

  search = () => {
    console.log("search agent manager");

    if (this.isLoading) return;

    this.featuredAgentResultArray = [];
    this.agentResultArray = [];

    this.loadAgentLists();
  };

  loadMore = () => {
    if (this.isLoading) return;

    if (!this.canLoadMore()) return;

    this.page = this.page + 1;
    this.loadAgentLists();
  };

  loadAgentLists = () => {
    this.isLoading = true;

    console.log(this.options);

    AgentSearchService.searchAgents({
      ...this.options,
      page: this.page
    })
      .then(response => {
        this.isLoading = false;
        const newAgentList = [];
        const { agents, featuredAgents2, conversationIds } = response;

        //separating the userId and its conversationdId to 2 array
        let conversationUserIdList = [];
        let conversationIdList = [];
        if (!ObjectUtil.isEmpty(conversationIds)) {
          conversationUserIdList = Object.keys(conversationIds);
          conversationIdList = Object.values(conversationIds);
        }

        //populate featured agents only one time.
        //featured agents result is keep changing everytime call the API.
        if (this.featuredAgentResultArray.length === 0) {
          const newFeaturedAgentList = [];
          if (!ObjectUtil.isEmpty(featuredAgents2)) {
            const { agentPOs } = featuredAgents2;
            if (!ObjectUtil.isEmpty(agentPOs) && Array.isArray(agentPOs)) {
              agentPOs.map((item, index) => {
                newFeaturedAgentList.push(
                  new AgentPO({
                    ...item,
                    key: (
                      this.featuredAgentResultArray.length + index
                    ).toString()
                  })
                );
              });
            }
          }
          //check and add if the agent had a conversation with consumer
          if (conversationUserIdList.length > 0) {
            newFeaturedAgentList.map((item, index) => {
              let ind = conversationUserIdList.indexOf(item.userId.toString());
              if (ind >= 0) {
                item.conversationId = conversationIdList[ind];
              }
            });
          }

          this.featuredAgentResultArray = [...newFeaturedAgentList];
        }

        //Get agents lists
        if (!ObjectUtil.isEmpty(agents)) {
          const { total, agentPOs } = agents;
          this.totalAgentList = total;
          if (!ObjectUtil.isEmpty(agentPOs) && Array.isArray(agentPOs)) {
            agentPOs.map((item, index) => {
              newAgentList.push(
                new AgentPO({
                  ...item,
                  key: (this.agentResultArray.length + index).toString()
                })
              );
            });
          }
          //check and add if the agent had a conversation with consumer
          if (conversationUserIdList.length > 0) {
            newAgentList.map((item, index) => {
              let ind = conversationUserIdList.indexOf(item.userId.toString());
              if (ind >= 0) {
                item.conversationId = conversationIdList[ind];
              }
            });
          }
        } //end of agents

        //combine agent result arrays
        if (!ObjectUtil.isEmpty(newAgentList)) {
          this.agentResultArray = [...this.agentResultArray, ...newAgentList];
          this.sendBackResponse(
            newAgentList,
            this.featuredAgentResultArray,
            this.agentResultArray,
            null,
            this.totalAgentList
          );
        } else {
          //need to send back response even there is no result
          this.sendBackResponse(
            null,
            this.featuredAgentResultArray,
            this.agentResultArray,
            null,
            this.totalAgentList
          );
        }
      })
      .catch(error => {
        this.isLoading = false;
        console.log(
          "AgentSearchService.searchAgentListings - failed - AgentSearchManager"
        );
        console.log(error);
      });
  };
}

export { AgentSearchManager, SearchAgentOptionsStruct };
