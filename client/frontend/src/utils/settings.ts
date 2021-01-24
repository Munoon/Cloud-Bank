declare global {
    interface Window {
        useCustomProperties?: boolean
        customProperties?: Record<string, string>
    }
}

interface Settings {
    useCustomProperties: boolean,
    customProperties?: Record<string, string>,
    testEnvironment: boolean,
    development: boolean
}

export default {
    useCustomProperties: window.useCustomProperties || false,
    customProperties: window.customProperties,
    testEnvironment: false,
    // @ts-ignore
    development: DEVELOPEMENT
} as Settings