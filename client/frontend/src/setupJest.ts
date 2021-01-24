import '@testing-library/jest-dom';
import {enableFetchMocks} from 'jest-fetch-mock';

enableFetchMocks();

jest.mock('./utils/settings', () => ({
    useCustomProperties: true,
    testEnvironment: true,
    development: false,
    customProperties: {
        'base_url': 'http://localhost',
        'tokens:csrf': 'MOCK_CSRF_TOKEN'
    }
}));