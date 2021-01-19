import React from 'react';
import ReactDOM from 'react-dom';

export function render(component: React.ReactElement) {
    ReactDOM.render(component, document.body);
}

export function readProperty(key: string): string {
    let element = document.querySelector<HTMLMetaElement>(`meta[name="${key}"]`);
    return element ? element.content : null;
}