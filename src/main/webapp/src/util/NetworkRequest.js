export default class NetworkRequest {
    static async get(url, body) {
        url += "?";
        for (const key in body) {
            if (body.hasOwnProperty(key)) {
                url += `&${key}=${body[key]}`;
            }
        }

        return await NetworkRequest.makeRequest(url, "GET");
    }

    static async post(url, body, isJson = true) {
        return await NetworkRequest.makeRequest(url, "POST", body, isJson);
    }

    static async put(url, body, isJson = true) {
        return await NetworkRequest.makeRequest(url, "PUT", body, isJson);
    }

    static async delete(url, body, isJson = true) {
        return await NetworkRequest.makeRequest(url, "DELETE", body, isJson);
    }

    static async makeRequest(url, method, body = null, isJson = true) {
        const headers = new Headers({
            authorization: sessionStorage.getItem("authToken")
        });
        if (isJson) {
            headers.append("Content-Type", "application/json");
            body = body ? JSON.stringify(body) : null;
        }

        return await fetch(url, {
            method, headers, body
        });
    }
}