import React from 'react';

import {readProperty, render} from '../../utils/globalUtils';
import LoginForm from './LoginForm';
import store from './store';

export const LoginPage = () => (
    <div className='shadow-2xl w-1/4 min-w-min bg-white p-10 m-auto mt-10 login-panel self-center'>
        <LoginHeader />
        <LoginForm />
    </div>
);

export const LoginHeader = () => (
    <div className='h16 w-16 m-auto'>
        <img src={`${readProperty('base_url')}/static/assets/icons/hello_hand.png`} alt='Hello hand icon'/>
    </div>
);

render(<LoginPage />, store);