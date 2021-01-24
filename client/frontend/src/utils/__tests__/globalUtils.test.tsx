import React from "react";

import {readProperty, render} from "../globalUtils";
import mockStore from "../../page/login/__tests__/utils/mockStore";
import {render as reactRender} from 'react-dom';

jest.mock('react-dom', () => ({
    render: jest.fn()
}));

jest.mock('../settings', () => ({
    useCustomProperties: false,
    testEnvironment: false,
    development: false
}));

describe('render test', () => {
    beforeEach(() => jest.resetAllMocks());

    it('Render test without store', () => {
        render(<h1>test</h1>);
        expect(reactRender).toHaveBeenCalledTimes(1);
        // @ts-ignore
        expect(reactRender.mock.calls[0]).toMatchSnapshot();
    });

    it('Render test with store', () => {
        render(<h1>test</h1>, mockStore());
        expect(reactRender).toHaveBeenCalledTimes(1);
        // @ts-ignore
        expect(reactRender.mock.calls[0]).toMatchSnapshot();
    });
});

describe('read property test', () => {
    let querySelectorSpy: jest.Mock<any, any[]>;
    let querySelectorProxy = (selector: string) => querySelectorSpy(selector);

    beforeAll(() => {
        Object.defineProperty(global.document, 'querySelector', { value: querySelectorProxy });
    });

    it('read key test', () => {
        querySelectorSpy = jest.fn(() => ({
            content: 'test'
        }));

        const result = readProperty('key');
        expect(result).toEqual('test');

        const calls: any[] = querySelectorSpy.mock.calls;
        expect(calls[calls.length - 1][0]).toEqual('meta[name="key"]');
    });

    it('read key, that not exist test', () => {
        querySelectorSpy = jest.fn(() => null);

        const result = readProperty('key');
        expect(result).toEqual(null);
    });
});