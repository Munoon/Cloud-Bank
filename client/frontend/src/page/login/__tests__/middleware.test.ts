import {login} from "../middleware";
import {loginErrorAction, loginLoadingAction, loginLoggedInAction} from "../reducer";

describe('middleware test', () => {
    describe('login test', () => {
        const realWindow = global["window"];

        beforeAll(() => {
            global["window"] = Object.create(window);
            Object.defineProperty(window, 'location', {
                value: {
                    ...window.location,
                },
                writable: true,
            });
        });

        afterAll(() => global["window"] = realWindow);

        it('login success test', async () => {
            const dispatch = jest.fn();
            fetchMock.mockOnce(JSON.stringify({ status: 'SUCCESS', redirectUrl: '/test' }));

            await login({ username: 'test', password: 'test' })(dispatch);

            expect(dispatch).toHaveBeenCalledTimes(2);
            expect(dispatch).toHaveBeenNthCalledWith(1, loginLoadingAction());
            expect(dispatch).toHaveBeenNthCalledWith(2, loginLoggedInAction());
        });

        it('login failure test', async () => {
            const dispatch = jest.fn();
            const errorData = { status: 'FAIL', errorCode: 'incorrect_password', errorMessage: 'Bad credentials' };
            fetchMock.mockOnce(JSON.stringify(errorData), { status: 401 });

            await login({ username: 'test', password: 'test' })(dispatch);

            expect(dispatch).toHaveBeenCalledTimes(2);
            expect(dispatch).toHaveBeenNthCalledWith(1, loginLoadingAction());
            expect(dispatch).toHaveBeenNthCalledWith(2, loginErrorAction(errorData));
        });

        it('fetch request test', async () => {
            const dispatch = jest.fn();
            fetchMock.mockOnce(JSON.stringify({ status: 'SUCCESS', redirectUrl: '/test' }));

            await login({ username: 'test', password: 'test' })(dispatch);

            // @ts-ignore
            const calls = fetch.mock.calls;
            expect(calls[calls.length - 1][0]).toEqual('http://localhost/login?username=test&password=test')
        });

        it('redirect test', async () => {
            const dispatch = jest.fn();
            const redirectUrl = '/test';
            fetchMock.mockOnce(JSON.stringify({ status: 'SUCCESS', redirectUrl }));

            await login({ username: 'test', password: 'test' })(dispatch);

            expect(location.href).toBe(redirectUrl);
        });
    });
});