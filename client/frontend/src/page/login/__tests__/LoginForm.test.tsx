import React from "react";
import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import {Provider} from "react-redux";

import LoginForm, {
    FailMessage,
    ForgotPasswordButton,
    FormInput,
    ShowPasswordButton,
    SubmitButton,
    SupportButtons
} from "../LoginForm";
import mockStore from "./utils/mockStore";
import {loginShowPassword, LoginState} from "../reducer";
import {login} from '../middleware';

jest.mock('../middleware', () => ({
    login: jest.fn(() => async () => {})
}));

describe('Login form test', () => {
    describe('Functional test', () => {
        it('button status disabled because of invalid inputs', async () => {
            render(
                <Provider store={mockStore()}>
                    <LoginForm />
                </Provider>
            );

            const button = screen.getByRole('button');
            fireEvent.submit(button);


            await waitFor(() => {});
            expect(button).toHaveAttribute('disabled');
        });

        it('button status disabled because of logged in', () => {
            render(
                <Provider store={mockStore({ login: { state: LoginState.LOGGED_IN } })}>
                    <LoginForm />
                </Provider>
            );

            const button = screen.getByRole('button');
            expect(button).toHaveAttribute('disabled');
            expect(button).toHaveClass('bg-success');
        });

        it('button status clickable because input valid', async () => {
            render(
                <Provider store={mockStore()}>
                    <LoginForm />
                </Provider>
            );

            const usernameInput = screen.getByPlaceholderText('Username');
            fireEvent.change(usernameInput, { target: { value: 'test' } });
            expect(usernameInput).toHaveValue('test');

            const passwordInput = screen.getByPlaceholderText('Password');
            fireEvent.change(passwordInput, { target: { value: 'test' } });
            expect(passwordInput).toHaveValue('test');

            const button = screen.getByRole('button');
            fireEvent.submit(button);

            await waitFor(() => {
                expect(button).not.toHaveAttribute('disabled');
            });
        });

        it('button status clickable on startup', () => {
            const store = mockStore();
            render(
                <Provider store={store}>
                    <LoginForm />
                </Provider>
            );

            const button = screen.getByRole('button');
            expect(button).not.toHaveAttribute('disabled');
        });

        it('show password', () => {
            render(
                <Provider store={mockStore({ login: { showPassword: true } })}>
                    <LoginForm />
                </Provider>
            );

            const passwordInput = screen.getByPlaceholderText('Password');
            expect(passwordInput).toHaveAttribute('type', 'text');
        });

        it('hide password', () => {
            render(
                <Provider store={mockStore({ login: { showPassword: false } })}>
                    <LoginForm />
                </Provider>
            );

            const passwordInput = screen.getByPlaceholderText('Password')
            expect(passwordInput).toHaveAttribute('type', 'password');
        });

        it('send form', async () => {
            const store = mockStore();
            render(
                <Provider store={store}>
                    <LoginForm />
                </Provider>
            );

            const usernameInput = screen.getByPlaceholderText('Username');
            fireEvent.change(usernameInput, { target: { value: 'test' } });
            expect(usernameInput).toHaveValue('test');

            const passwordInput = screen.getByPlaceholderText('Password');
            fireEvent.change(passwordInput, { target: { value: 'test' } });
            expect(passwordInput).toHaveValue('test');

            const button = screen.getByRole('button');
            fireEvent.submit(button);

            await waitFor(() => {
                expect(login).toHaveBeenLastCalledWith({ username: 'test', password: 'test' });
            });
        });
    });

    it('Snapshot test', () => {
        const component = render(
            <Provider store={mockStore()}>
                <LoginForm />
            </Provider>
        );
        expect(component).toMatchSnapshot();
    });
});

describe('Submit button component test', () => {
    it('Disabled by hand', () => {
        render(
            <Provider store={mockStore()}>
                <SubmitButton disabled={true} />
            </Provider>
        );

        const button = screen.getByRole('button');
        expect(button).toHaveAttribute('disabled');
    });

    it('Disabled because loading', () => {
        render(
            <Provider store={mockStore({ login: { state: LoginState.LOADING } })}>
                <SubmitButton disabled={false} />
            </Provider>
        );

        const button = screen.getByRole('button');
        expect(button).toHaveAttribute('disabled');
    });

    it('Disabled because logged in', () => {
        render(
            <Provider store={mockStore({ login: { state: LoginState.LOGGED_IN } })}>
                <SubmitButton disabled={false} />
            </Provider>
        );

        const button = screen.getByRole('button');
        expect(button).toHaveAttribute('disabled');
    });

    it('Have default background color', () => {
        render(
            <Provider store={mockStore()}>
                <SubmitButton disabled={false} />
            </Provider>
        );

        const button = screen.getByRole('button');
        expect(button).toHaveClass('bg-primary');
        expect(button).not.toHaveClass('bg-success');
    });

    it('Have success background color on loading', () => {
        render(
            <Provider store={mockStore({ login: { state: LoginState.LOGGED_IN } })}>
                <SubmitButton disabled={false} />
            </Provider>
        );

        const button = screen.getByRole('button');
        expect(button).not.toHaveClass('bg-primary');
        expect(button).toHaveClass('bg-success');
    });

    it('Snapshot test', () => {
        const component = render(
            <Provider store={mockStore()}>
                <SubmitButton disabled={false} />
            </Provider>
        );
        expect(component).toMatchSnapshot();
    });
});

describe('Fail message component test', () => {
    it('No message test', () => {
        render(
            <Provider store={mockStore()}>
                <FailMessage />
            </Provider>
        );

        const component = screen.queryByTestId('login_form.error_message');
        expect(component).toBeNull();
    });

    it('Message by error code', () => {
        const storeModification = {
            login: {
                state: LoginState.ERROR,
                errorData: {
                    errorCode: 'bad_credentials',
                    errorMessage: 'test error'
                }
            }
        };
        render(
            <Provider store={mockStore(storeModification)}>
                <FailMessage />
            </Provider>
        );

        const component = screen.queryByTestId('login_form.error_message');
        expect(component).toHaveTextContent('Incorrect login or password!');
    });

    it('Message from server', () => {
        const storeModification = {
            login: {
                state: LoginState.ERROR,
                errorData: { errorMessage: 'test error' }
            }
        };
        render(
            <Provider store={mockStore(storeModification)}>
                <FailMessage />
            </Provider>
        );

        const component = screen.queryByTestId('login_form.error_message');
        expect(component).toHaveTextContent('test error');
    });

    it('Snapshot test', () => {
        const storeModification = {
            login: {
                state: LoginState.ERROR,
                errorData: { errorMessage: 'test error' }
            }
        };
        const component = render(
            <Provider store={mockStore(storeModification)}>
                <FailMessage />
            </Provider>
        );
        expect(component).toMatchSnapshot();
    });
});

describe('Support buttons component test', () => {
    it('Snapshot test', () => {
        const component = render(
            <Provider store={mockStore()}>
                <SupportButtons />
            </Provider>
        );
        expect(component).toMatchSnapshot();
    });
});

describe('Forgot password button component test', () => {
    it('Snapshot test', () => {
        const component = render(<ForgotPasswordButton />);
        expect(component).toMatchSnapshot();
    });
});

describe('Show password checkbox component test', () => {
    it('Show password', () => {
        render(
            <Provider store={mockStore({ login: { showPassword: true } })}>
                <ShowPasswordButton />
            </Provider>
        );

        const element = screen.getByRole('checkbox');
        expect(element).toHaveAttribute('checked');
    });

    it('Hide password', () => {
        render(
            <Provider store={mockStore()}>
                <ShowPasswordButton />
            </Provider>
        );

        const element = screen.getByRole('checkbox');
        expect(element).not.toHaveAttribute('checked');
    });

    it('Click to show password', () => {
        const store = mockStore();
        render(
            <Provider store={store}>
                <ShowPasswordButton />
            </Provider>
        );

        const element = screen.getByRole('checkbox');
        fireEvent.click(element);

        const actions = store.getActions();
        const lastAction = actions[actions.length - 1];
        expect(lastAction).toEqual(loginShowPassword(true));
    });

    it('Click to hide password', () => {
        const store = mockStore({ login: { showPassword: true } });
        render(
            <Provider store={store}>
                <ShowPasswordButton />
            </Provider>
        );

        const element = screen.getByRole('checkbox');
        fireEvent.click(element);

        const actions = store.getActions();
        const lastAction = actions[actions.length - 1];
        expect(lastAction).toEqual(loginShowPassword(false));
    });

    it('Snapshot test', () => {
        const component = render(
            <Provider store={mockStore({ login: { showPassword: true } })}>
                <ShowPasswordButton />
            </Provider>
        );
        expect(component).toMatchSnapshot();
    });
});

describe('Form input test', () => {
    it('Functional test', () => {
        render(
            <FormInput type='password' name='testName' placeholder='testPlaceholder' />
        );

        const element = screen.getByPlaceholderText('testPlaceholder');
        expect(element).toHaveAttribute('type', 'password');
        expect(element).toHaveAttribute('name', 'testName');
        expect(element).toHaveAttribute('placeholder', 'testPlaceholder');
    });

    it('Snapshot test', () => {
        const component = render(
            <FormInput type='password' name='testName' placeholder='testPlaceholder' />
        );
        expect(component).toMatchSnapshot();
    });
});