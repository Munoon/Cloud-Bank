import React, {useCallback} from 'react';
import {connect, ConnectedProps, useSelector} from 'react-redux';
import {useForm} from 'react-hook-form';

import {login} from './middleware';
import {readProperty} from '../../utils/globalUtils';
import {loginShowPassword, LoginState, LoginStoreStateType, LoginStoreType} from "./store";

const connector = connect(null, { login });

export interface LoginFormData {
    username: string;
    password: string;
}

const LoginForm = ({ login }: ConnectedProps<typeof connector>) => {
    const { handleSubmit, register, errors } = useForm();
    const onSubmit = useCallback((data: LoginFormData) => login(data), []);
    const showPassword = useSelector((store: LoginStoreType) => store.login.showPassword);

    return (
        <form action={`${readProperty('base_url')}/login`} method='post'
              onSubmit={handleSubmit(onSubmit)} className='object-center font-mono mt-3'>
            <FailMessage />
            <input type='hidden' name='_csrf' value={readProperty('tokens:csrf')} />
            <FormInput placeholder='Username' name='username' ref={register({ required: true })} />
            <FormInput type={showPassword ? 'text' : 'password'}
                       placeholder='Password' name='password' ref={register({ required: true })} />
            <SupportButtons />
            <SubmitButton disabled={Object.entries(errors).length > 0} />
        </form>
    );
}

const SubmitButton = ({ disabled }: { disabled: boolean }) => {
    const loginState = useSelector((store: LoginStoreType) => store.login.state);
    const shouldDisabled = disabled || loginState === LoginState.LOADING;
    const loginSuccess = loginState === LoginState.LOGGED_IN;
    return (
        <button type="submit"
                className={(loginSuccess ? 'bg-success' : 'bg-primary')
                    + ' disabled:bg-disabled mt-10 p-1 transition rounded-full'
                    + ' w-full text-center text-white focus:outline-none'}
                disabled={shouldDisabled}>Sign in</button>
    );
}

const failMessages: Record<string, string> = {
    'bad_credentials': 'Incorrect login or password!'
};

const FailMessage = () => {
    const login: LoginStoreStateType = useSelector((store: LoginStoreType) => store.login);
    const loginFailed = login.state === LoginState.ERROR;
    if (!loginFailed) {
        return null;
    }
    const errorCode = login.errorData.errorCode;
    if (errorCode && failMessages[errorCode] !== undefined) {
        return <div>{failMessages[errorCode]}</div>;
    }
    return <div>{login.errorData.errorMessage}</div>
}

const SupportButtons = () => (
    <div className='flex items-center justify-between mt-1'>
        <ShowPasswordButton />
        <ForgotPasswordButton />
    </div>
);

const showPasswordButtonConnector = connect(
    (state: LoginStoreType) => ({ showPassword: state.login.showPassword }),
    { loginShowPassword }
);
const ShowPasswordButton = showPasswordButtonConnector((props: ConnectedProps<typeof showPasswordButtonConnector>) => (
    <div className='flex items-center'>
        <input onChange={e => props.loginShowPassword(e.target.checked)}
               checked={props.showPassword}
               id="showPassword"
               type="checkbox"
               className='h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded' />
        <label htmlFor="showPassword"
               className="ml-2 block text-sm text-gray-900 select-none">Show password</label>
    </div>
));

const ForgotPasswordButton = () => (
    <div className="text-sm">
        <a href="#" className='font-medium text-indigo-600 hover:text-indigo-500 select-none'>Forgot password?</a>
    </div>
);

interface FormInputProps {
    type?: string;
    name?: string;
    placeholder?: string;
}

const FormInput = React.forwardRef<HTMLInputElement, FormInputProps>((props, ref)  => (
    <input type={props.type} name={props.name} placeholder={props.placeholder} ref={ref}
           className="mt-10 bg-primary p-1 rounded-full w-full text-center text-white outline-none placeholder-secondary" />
));

export default connector(LoginForm);