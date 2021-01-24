import {Dispatch} from 'redux';

import {LoginFormData} from "./LoginForm";
import {loginErrorAction, loginLoadingAction, loginLoggedInAction} from './reducer';
import {readProperty} from "../../utils/globalUtils";
import fetcher from "../../utils/fetcher";

export const login = (data: LoginFormData) => async (dispatch: Dispatch) => {
    dispatch(loginLoadingAction());
    const url = `${readProperty('base_url')}/login`;
    const params = {
        username: data.username,
        password: data.password
    };
    try {
        const info = await fetcher<SuccessAuthenticationInfo>(url, { method: 'POST', params, redirect: 'manual' });
        dispatch(loginLoggedInAction());
        location.href = info.redirectUrl;
    } catch (e) {
        const failAuthenticationInfo = e.response as FailAuthenticationInfo;
        dispatch(loginErrorAction(failAuthenticationInfo));
    }
}

interface SuccessAuthenticationInfo {
    status: string;
    redirectUrl: string;
}

interface FailAuthenticationInfo {
    status: string;
    errorCode?: string;
    errorMessage?: string;
}