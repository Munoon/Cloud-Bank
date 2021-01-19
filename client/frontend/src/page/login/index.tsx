import React from 'react';
import {readProperty, render} from '../../utils/globalUtils';

const LoginPage = () => (
    <div>
        <h1>Login</h1>
        <form action={readProperty("forms:login:url")} method='post'>
            <input type="hidden" name={readProperty('_csrf_parameter')} value={readProperty('_csrf')} />
            <input type="text" name='username' placeholder='username' />
            <input type="password" name='password' placeholder='password' />
            <button>Log in</button>
        </form>
    </div>
);

render(<LoginPage />);