import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import {LoginState} from "../../reducer";
import {LoginStoreType} from "../../store";

const mockState: LoginStoreType = {
    login: {
        state: LoginState.IDLE,
        showPassword: false,
        errorData: null
    }
};

const store = configureStore([thunk]);

const mockStore = (changes: Record<any, any> = {}) => store(() => ({ ...mockState, ...changes }));
export default mockStore;