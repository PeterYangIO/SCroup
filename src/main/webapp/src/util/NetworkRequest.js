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

    static async post(url, body) {
        return await NetworkRequest.makeRequest(url, "POST", body);
    }

    static async put(url, body) {
        return await NetworkRequest.makeRequest(url, "PUT", body);
    }

    static async delete(url, body) {
        return await NetworkRequest.makeRequest(url, "DELETE", body);
    }

    static async makeRequest(url, method, body = null) {
        const headers = new Headers({
            authorization: sessionStorage.getItem("authToken")
        });

        headers.append("Content-Type", "application/json");
        body = body ? JSON.stringify(body) : null;

        return await fetch(url, {
            method, headers, body
        });
    }
}