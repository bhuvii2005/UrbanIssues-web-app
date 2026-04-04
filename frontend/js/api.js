const BASE_URL = 'http://localhost:8080/api';

/**
 * Custom fetch wrapper that automatically appends JWT tokens and handles status errors.
 */
async function fetchAPI(endpoint, options = {}) {
    // Inject Authorization header if token exists
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(`${BASE_URL}${endpoint}`, config);
        
        if (!response.ok) {
            // Unpack Bucket4j Too Many Requests
            if (response.status === 429) {
                throw new Error("Too many requests! Please wait before trying again.");
            }
            if (response.status === 403 || response.status === 401) {
                // Token invalid or forbidden, logout user
                localStorage.removeItem('token');
                window.location.href = 'login.html';
            }
            
            const errorBody = await response.text();
            throw new Error(`API Error ${response.status}: ${errorBody}`);
        }
        
        // Handle 204 No Content
        if (response.status === 204) return null;
        
        return await response.json();
    } catch (error) {
        console.error("fetchAPI Exception:", error);
        throw error;
    }
}
