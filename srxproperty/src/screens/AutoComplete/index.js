import { Navigation } from "react-native-navigation";
import {
  AddressAutoComplete as AutoComplete,
  AutoCompletePurposes
} from "./AddressAutoComplete";
import { SuggestionAutoComplete } from "./SuggestionAutoComplete";

const show = passProps => {
  Navigation.showModal({
    stack: {
      // id: "AddressAutoCompleteStack",
      children: [
        {
          component: {
            name: "AddressAutoComplete",
            passProps,
            options: {
              modalPresentationStyle: 'overFullScreen',
            },
          }
        }
      ]
    }
  });
};

//Suggestin for Concierge
const showSuggestionAutoComplete = passProps => {
  Navigation.showModal({
    stack: {
      children: [
        {
          component: {
            name: "SuggestionAutoComplete",
            passProps,
            options: {
              modalPresentationStyle: 'overFullScreen',
            },
          }
        }
      ]
    }
  });
};

const AddressAutoComplete = {
  show,
  showSuggestionAutoComplete
};

export { AddressAutoComplete, AutoCompletePurposes, AutoComplete, SuggestionAutoComplete };
