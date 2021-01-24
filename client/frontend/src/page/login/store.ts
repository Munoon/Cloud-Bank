import {applyMiddleware, combineReducers, createStore} from 'redux';
import thunk from 'redux-thunk';

import reducer, {LoginStoreStateType} from "./reducer";

export type LoginStoreType = { login: LoginStoreStateType }
const reducers = combineReducers<LoginStoreType>({ login: reducer });
export default createStore(reducers, applyMiddleware(thunk));