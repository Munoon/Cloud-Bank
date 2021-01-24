import {readProperty} from "./globalUtils";

interface CustomRequestInit extends RequestInit {
    params?: Record<string, string>;
    headers?: Record<string, string>;
    addContentType?: boolean;
    addCsrf?: boolean
}

export default function fetcher<T = any>(input: RequestInfo, init: CustomRequestInit = {}): Promise<T> {
    if (!init.headers) {
        init.headers = {};
    }

    if (init.addContentType === undefined) {
        init.addContentType = true;
    }

    if (init.headers['Content-Type'] === undefined && init.addContentType) {
        init.headers['Content-Type'] = 'application/json';
    } else if (!init.addContentType) {
        delete init.headers['Content-Type'];
    }

    if (!init.cache) {
        init.cache = 'no-cache';
    }

    if (init.addCsrf === undefined || init.addCsrf) {
        init.headers['X-CSRF-TOKEN'] = readProperty('tokens:csrf');
    }

    if (init.params) {
        input += '?' + Object.keys(init.params)
            .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(init.params[k]))
            .join('&');
    }

    let data: Response;
    return fetch(input, init)
        .then(resp => {
            data = resp;
            return resp.text();
        })
        .then(text => {
            const json = parseFromJson(text);
            if (!data.ok) {
                throw {
                    name: 'RequestException',
                    response: json,
                    data
                };
            }
            return json as T;
        });
}

function parseFromJson(text: string) {
    try {
        return JSON.parse(text);
    } catch (e) {
        return text;
    }
}