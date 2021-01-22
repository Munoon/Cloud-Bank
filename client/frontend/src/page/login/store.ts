import {applyMiddleware, combineReducers, createStore} from 'redux';
import thunk from 'redux-thunk';

export const LoginStoreType = {
    LOADING: 'login:loading',
    LOGGED_IN: 'login:logged_in',
    LOGIN_ERROR: 'login:error',
    PASSWORD: 'login:password'
}

export const LoginState = {
    IDLE: 'login_state:idle',
    LOADING: 'login_state:loading',
    LOGGED_IN: 'login_state:logged_in',
    ERROR: 'login_state:error',
}

interface LoginErrorData {
    errorCode?: string
    errorMessage?: string
}

export interface LoginStoreStateType {
    state: string;
    showPassword: boolean;
    errorData: LoginErrorData | null;
}

const defaultStoreState: LoginStoreStateType = {
    state: LoginState.IDLE,
    showPassword: false,
    errorData: null
}

type SimpleLoginAction = { type: string }
type LoginStoreActionsType = SimpleLoginAction;

function reducer(state: LoginStoreStateType = defaultStoreState, action: LoginStoreActionsType): LoginStoreStateType {
    switch (action.type) {
        case LoginStoreType.LOADING:
            return {
                ...state,
                errorData: null,
                state: LoginState.LOADING
            };
        case LoginStoreType.LOGGED_IN:
            return {
                ...state,
                errorData: null,
                state: LoginState.LOGGED_IN
            };
        case LoginStoreType.LOGIN_ERROR:
            const { errorData } = (action as LoginErrorActionType)
            return {
                ...state,
                state: LoginState.ERROR,
                errorData
            };
        case LoginStoreType.PASSWORD:
            return {
                ...state,
                showPassword: (action as ShowPasswordActionType).show
            };
        default:
            return state;
    }
}

export const loginLoadingAction = (): SimpleLoginAction => ({ type: LoginStoreType.LOADING })
export const loginLoggedInAction = (): SimpleLoginAction => ({ type: LoginStoreType.LOGGED_IN })

type LoginErrorActionType = SimpleLoginAction & { errorData: LoginErrorData }
export const loginErrorAction = (errorData: LoginErrorData): LoginErrorActionType => ({ type: LoginStoreType.LOGIN_ERROR, errorData })

type ShowPasswordActionType = SimpleLoginAction & { show: boolean }
export const loginShowPassword = (show: boolean): ShowPasswordActionType => ({ type: LoginStoreType.PASSWORD, show })

export type LoginStoreType = { login: LoginStoreStateType }
const reducers = combineReducers<LoginStoreType>({ login: reducer });
export default createStore(reducers, applyMiddleware(thunk));