import React from 'react';
import {render, screen} from "@testing-library/react";
import {Provider} from 'react-redux';

import {LoginHeader, LoginPage} from '../';
import mockStore from "./utils/mockStore";

describe('Login page test', () => {
    it('Snapshot test', () => {
        const component = render(
            <Provider store={mockStore()}>
                <LoginPage />
            </Provider>
        );
        expect(component).toMatchSnapshot();
    });
});

describe('Login header test', () => {
    it('Image url test', () => {
        render(<LoginHeader />);

        const component = screen.getByRole('img');
        expect(component).toHaveAttribute('src', 'http://localhost/static/assets/icons/hello_hand.png');
    });

    it('Snapshot test', () => {
        const component = render(<LoginHeader />);
        expect(component).toMatchSnapshot();
    });
});