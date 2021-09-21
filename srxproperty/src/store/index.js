import { createStore, applyMiddleware } from "redux";
import ReduxThunk from "redux-thunk";
import reducers from "../reducers";

const store = createStore(reducers, {}, applyMiddleware(ReduxThunk));//adding email & password here, in the middle block

export { store };