import reducer, {
    loginErrorAction,
    loginLoadingAction,
    loginLoggedInAction,
    loginShowPassword,
    LoginState,
    LoginStoreStateType
} from '../reducer';

describe('login store reducer test', () => {
    describe('functional tests', () => {
        it('loading', () => {
            const expected: LoginStoreStateType = {
                state: LoginState.LOADING,
                showPassword: false,
                errorData: null
            };

            const store: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: false,
                errorData: { errorCode: 'incorrect_password' }
            };

            const actual = reducer(store, loginLoadingAction());
            expect(actual).toEqual(expected);
        });

        it('logged in', () => {
            const expected: LoginStoreStateType = {
                state: LoginState.LOGGED_IN,
                showPassword: false,
                errorData: null
            };

            const store: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: false,
                errorData: { errorCode: 'incorrect_password' }
            };

            const actual = reducer(store, loginLoggedInAction());
            expect(actual).toEqual(expected);
        });

        it('login error', () => {
            const errorData = { errorCode: 'incorrect_password', errorMessage: 'Incorrect password' };
            const expected: LoginStoreStateType = {
                state: LoginState.ERROR,
                showPassword: false,
                errorData: errorData
            };

            const actual = reducer(undefined, loginErrorAction(errorData));
            expect(actual).toEqual(expected);
        });

        it('password toggle on', () => {
            const expected: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: true,
                errorData: null
            };

            const actual = reducer(undefined, loginShowPassword(true));
            expect(actual).toEqual(expected);
        });

        it('password toggle off', () => {
            const store: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: true,
                errorData: null
            };

            const expected: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: false,
                errorData: null
            };

            const actual = reducer(store, loginShowPassword(false));
            expect(actual).toEqual(expected);
        });
    });

    describe('snapshot tests', () => {
        it('loading', () => {
            const store: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: false,
                errorData: { errorCode: 'incorrect_password' }
            };

            const actual = reducer(store, loginLoadingAction());
            expect(actual).toMatchSnapshot();
        });

        it('logged in', () => {
            const store: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: false,
                errorData: { errorCode: 'incorrect_password' }
            };

            const actual = reducer(store, loginLoggedInAction());
            expect(actual).toMatchSnapshot();
        });

        it('login error', () => {
            const errorData = { errorCode: 'incorrect_password', errorMessage: 'Incorrect password' };
            const actual = reducer(undefined, loginErrorAction(errorData));
            expect(actual).toMatchSnapshot();
        });

        it('password toggle', () => {
            const actual1 = reducer(undefined, loginShowPassword(true));
            expect(actual1).toMatchSnapshot();

            const store: LoginStoreStateType = {
                state: LoginState.IDLE,
                showPassword: true,
                errorData: null
            };

            const actual2 = reducer(store, loginShowPassword(false));
            expect(actual2).toMatchSnapshot();
        });
    });
});