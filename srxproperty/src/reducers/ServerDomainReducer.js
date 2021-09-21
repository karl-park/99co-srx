import { UPDATE_SERVER_DOMAIN } from "../actions";
import { AppConstant } from "../constants";

const INITIAL_STATE = { domainURL: AppConstant.DomainUrl };

const updateDomainToState = ({ originalState, newDomainUrl }) => {
  return {
    ...originalState,
    domainURL: newDomainUrl
  };
};

export default (state = INITIAL_STATE, action) => {
  switch (action.type) {
    case UPDATE_SERVER_DOMAIN:
      return updateDomainToState({
        originalState: state,
        newDomainUrl: action.payload
      });
    default:
      return state;
  }
};
