import fetcher from "../fetcher";

describe('Fetcher test', () => {
    it('Default fetch', async () => {
        const mockResponse = JSON.stringify({ test: 'success' });
        fetchMock.mockResponseOnce(mockResponse);

        const response = await fetcher('/test');
        expect(response).toEqual({ test: 'success' });
        expect(getMockCall()[0]).toEqual('/test');
        expect(getMockCall()[1].headers).toEqual({
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': 'MOCK_CSRF_TOKEN'
        });
        expect(getMockCall()[1].cache).toEqual('no-cache');
    });

    it('Fetch with custom content type',  async() => {
        const mockResponse = JSON.stringify({ test: 'success' });
        fetchMock.mockResponseOnce(mockResponse);

        await fetcher('/test', {
            headers: {
                'Content-Type': 'application/xml'
            }
        });
        expect(getMockCall()[1].headers).toEqual({
            'Content-Type': 'application/xml',
            'X-CSRF-TOKEN': 'MOCK_CSRF_TOKEN'
        });
    });

    it('Fetch without content type', async () => {
        const mockResponse = JSON.stringify({ test: 'success' });
        fetchMock.mockResponseOnce(mockResponse);

        await fetcher('/test', { addContentType: false });
        expect(getMockCall()[1].headers).toEqual({ 'X-CSRF-TOKEN': 'MOCK_CSRF_TOKEN' });
    });

    it('Fetch with custom cache policy', async () => {
        const mockResponse = JSON.stringify({ test: 'success' });
        fetchMock.mockResponseOnce(mockResponse);

        await fetcher('/test', { cache: 'default' });
        expect(getMockCall()[1].cache).toEqual('default');
    });

    it('Fetch without csrf', async () => {
        const mockResponse = JSON.stringify({ test: 'success' });
        fetchMock.mockResponseOnce(mockResponse);

        await fetcher('/test', { addCsrf: false });
        expect(getMockCall()[1].headers).toEqual({ 'Content-Type': 'application/json' });
    });

    it('Fetch with parameters', async () => {
        const mockResponse = JSON.stringify({ test: 'success' });
        fetchMock.mockResponseOnce(mockResponse);

        await fetcher('/test', {
            params: {
                test: 'a',
                hard: 'my&and?i'
            }
        });
        expect(getMockCall()[0]).toEqual('/test?test=a&hard=my%26and%3Fi');
    });

    it('Fetch error', async () => {
        const mockResponse = JSON.stringify({ test: 'error' });
        fetchMock.mockResponseOnce(mockResponse, { status: 404 });

        expect.assertions(4);

        try {
            await fetcher('/test')
        } catch (e) {
            expect(e.name).toEqual('RequestException');
            expect(e.response).toEqual({ test: 'error' });
            expect(e.data.ok).toEqual(false);
            expect(e.data.status).toEqual(404);
        }
    });

    it('Fetch not with json', async () => {
        const mockResponse = '<test>success</test>'
        fetchMock.mockResponseOnce(mockResponse);

        const response = await fetcher('/test');
        expect(response).toEqual(mockResponse);
    });
});

const getMockCall = (): any => {
    // @ts-ignore
    const calls = fetch.mock.calls;
    return calls[calls.length - 1];
}