class AuthManager {
    constructor() {
        this.apiUrl = '/api/v1/auth';
        this.tokenKey = 'jwt_token';
        this.userKey = 'user_data';
    }

    saveToken(token, userData) {
        localStorage.setItem(this.tokenKey, token);
        localStorage.setItem(this.userKey, JSON.stringify(userData));
    }

    getToken() {
        return localStorage.getItem(this.tokenKey);
    }

    getUserData() {
        const userData = localStorage.getItem(this.userKey);
        return userData ? JSON.parse(userData) : null;
    }

    isAuthenticated() {
        return !!this.getToken();
    }

    logout() {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        window.location.href = '/login.html';
    }

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

    async getRoles() {
        try {
            const response = await fetch(`${this.apiUrl}/roles`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || 'Failed to fetch roles');
            }

            return data;
        } catch (error) {
            throw error;
        }
    }

    hasRole(role) {
        const userData = this.getUserData();
        if (!userData || !userData.roles) {
            return false;
        }
        
        // Check if user has the specified role
        return userData.roles.some(userRole => 
            userRole.toLowerCase() === role.toLowerCase() || 
            userRole.toLowerCase() === `role_${role.toLowerCase()}`
        );
    }

    isAdmin() {
        return this.hasRole('admin');
    }

    isUser() {
        return this.hasRole('user');
    }
}

const authManager = new AuthManager();
window.authManager = authManager;

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

document.addEventListener('DOMContentLoaded', function() {
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

    const roleSelect = document.getElementById('role');
    if (roleSelect) {
        (async () => {
            try {
                const roles = await authManager.getRoles();
                roleSelect.innerHTML = '';
                
                const defaultOption = document.createElement('option');
                defaultOption.value = '';
                defaultOption.textContent = 'Select a role';
                defaultOption.disabled = true;
                defaultOption.selected = true;
                roleSelect.appendChild(defaultOption);
                
                roles.forEach(role => {
                    const option = document.createElement('option');
                    const roleValue = role.name.replace('ROLE_', '').toLowerCase();
                    const roleDisplay = roleValue.charAt(0).toUpperCase() + roleValue.slice(1);
                    
                    option.value = roleValue;
                    option.textContent = roleDisplay;
                    roleSelect.appendChild(option);
                });
            } catch (error) {
                console.error('Failed to load roles:', error);
                roleSelect.innerHTML = `
                    <option value="">Select a role</option>
                    <option value="user">User</option>
                    <option value="admin">Admin</option>
                `;
            }
        })();
    }

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
            
            if (password !== confirmPassword) {
                showError('Passwords do not match');
                return;
            }
            
            if (password.length < 6) {
                showError('Password must be at least 6 characters long');
                return;
            }
            
            showSpinner('register-btn', 'register-spinner');
            
            try {
                const userData = { username, email, password, role: [role] };
                await authManager.register(userData);
                showSuccess('Registration successful! You can now log in.');
                registerForm.reset();
                
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