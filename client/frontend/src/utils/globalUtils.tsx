import React from 'react';
import ReactDOM from 'react-dom';
import {Store} from "redux";
import {Provider} from "react-redux";

import applicationSettings from "./settings";

export function render(component: React.ReactElement, store?: Store) {
    if (applicationSettings.testEnvironment) {
        return;
    }

    let renderComponent = store ? (
        <Provider store={store}>
            {component}
        </Provider>
    ) : component;

    if (applicationSettings.development) {
        renderComponent = (
            <React.StrictMode>
                {renderComponent}
            </React.StrictMode>
        );
    }

    ReactDOM.render(renderComponent, document.getElementById('root'));
}

export function readProperty(key: string): string {
    if (applicationSettings.useCustomProperties) {
        return applicationSettings.customProperties[key];
    }

    let element = document.querySelector<HTMLMetaElement>(`meta[name="${key}"]`);
    return element ? element.content : null;
}

type TestIdType = { 'data-testid': string };

export function testId(key: string): TestIdType {
    if (!applicationSettings.testEnvironment) {
        return null;
    }
    return { "data-testid": key };
}