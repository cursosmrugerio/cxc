// Authentication utilities and event handlers
class AuthManager {
    constructor() {
        this.apiUrl = '/api/v1/auth';
        this.tokenKey = 'jwt_token';
        this.userKey = 'user_data';
    }

    // Save JWT token and user data to localStorage
    saveToken(token, userData) {
        localStorage.setItem(this.tokenKey, token);
        localStorage.setItem(this.userKey, JSON.stringify(userData));
    }

    // Get JWT token from localStorage
    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    // Get user data from localStorage
    getUserData() {
        const userData = localStorage.getItem(this.userKey);
        return userData ? JSON.parse(userData) : null;
    }

    // Check if user is authenticated
    isAuthenticated() {
        return !!this.getToken();
    }

    // Clear authentication data
    logout() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        window.location.href = 'login.html';
    }

    // Make authenticated API requests
    async authenticatedFetch(url, options = {}) {
        const token = this.getToken();
        if (!token) {
            throw new Error('No authentication token found');
        }

        const headers = {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...options.headers
        };

        const response = await fetch(url, {
            ...options,
            headers
        });

        if (response.status === 401) {
            this.logout();
            throw new Error('Authentication failed');
        }

        return response;
    }

    // Login function
    async login(username, password) {
        try {
            const response = await fetch(`${this.apiUrl}/signin`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Login failed');
            }

            // Save token and user data
            this.saveToken(data.token, {
                username: data.username,
                email: data.email,
                roles: data.roles
            });

            return data;
        } catch (error) {
            throw error;
        }
    }

    // Register function
    async register(userData) {
        try {
            const response = await fetch(`${this.apiUrl}/signup`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Registration failed');
            }

            return data;
        } catch (error) {
            throw error;
        }
    }
}

// Create global auth manager instance
const authManager = new AuthManager();

// Utility functions
function showError(message, elementId = 'error-message') {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.remove('d-none');
    }
}

function hideError(elementId = 'error-message') {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.classList.add('d-none');
    }
}

function showSuccess(message, elementId = 'success-message') {
    const successElement = document.getElementById(elementId);
    if (successElement) {
        successElement.textContent = message;
        successElement.classList.remove('d-none');
    }
}

function hideSuccess(elementId = 'success-message') {
    const successElement = document.getElementById(elementId);
    if (successElement) {
        successElement.classList.add('d-none');
    }
}

function showSpinner(buttonId, spinnerId) {
    const button = document.getElementById(buttonId);
    const spinner = document.getElementById(spinnerId);
    if (button && spinner) {
        button.disabled = true;
        spinner.classList.remove('d-none');
    }
}

function hideSpinner(buttonId, spinnerId) {
    const button = document.getElementById(buttonId);
    const spinner = document.getElementById(spinnerId);
    if (button && spinner) {
        button.disabled = false;
        spinner.classList.add('d-none');
    }
}

// Page-specific event handlers
document.addEventListener('DOMContentLoaded', function() {
    // Login form handler
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            
            hideError();
            showSpinner('login-btn', 'login-spinner');
            
            try {
                await authManager.login(username, password);
                window.location.href = 'index.html';
            } catch (error) {
                showError(error.message);
            } finally {
                hideSpinner('login-btn', 'login-spinner');
            }
        });
    }

    // Register form handler
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;
            const role = document.getElementById('role').value;
            
            hideError();
            hideSuccess();
            
            // Validate passwords match
            if (password !== confirmPassword) {
                showError('Passwords do not match');
                return;
            }
            
            // Validate password length
            if (password.length < 6) {
                showError('Password must be at least 6 characters long');
                return;
            }
            
            showSpinner('register-btn', 'register-spinner');
            
            try {
                const userData = {
                    username,
                    email,
                    password,
                    role: [role]
                };
                
                await authManager.register(userData);
                showSuccess('Registration successful! You can now log in.');
                registerForm.reset();
                
                // Redirect to login after 2 seconds
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 2000);
                
            } catch (error) {
                showError(error.message);
            } finally {
                hideSpinner('register-btn', 'register-spinner');
            }
        });
    }
});

// Redirect to login if not authenticated (for protected pages)
function requireAuth() {
    if (!authManager.isAuthenticated()) {
        window.location.href = 'login.html';
        return false;
    }
    return true;
}

// Redirect to main page if already authenticated (for login/register pages)
function redirectIfAuthenticated() {
    if (authManager.isAuthenticated()) {
        window.location.href = 'index.html';
        return true;
    }
    return false;
}