class HeaderComponent {
    constructor() {
        this.userInfo = null;
        this.init();
    }

    async init() {
        await this.loadComponent();
        this.setupEventListeners();
        this.updateUserInfo();
    }

    async loadComponent() {
        try {
            const response = await fetch('/components/header.html');
            
            if (!response.ok) {
                if (response.status === 401) {
                    console.warn('Authentication required to load header component');
                    this.loadFallbackHeader();
                    return;
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const html = await response.text();
            
            // Create header container if it doesn't exist
            let headerContainer = document.getElementById('header-container');
            if (!headerContainer) {
                headerContainer = document.createElement('div');
                headerContainer.id = 'header-container';
                document.body.insertBefore(headerContainer, document.body.firstChild);
            }
            
            headerContainer.innerHTML = html;
        } catch (error) {
            console.error('Error loading header component:', error);
            this.loadFallbackHeader();
        }
    }

    loadFallbackHeader() {
        const userData = this.getUserData();
        const userDisplay = userData ? `Welcome, ${userData.username || userData.email || 'User'}` : 'Guest';
        
        const fallbackHtml = `
            <header class="app-header">
                <div class="header-container">
                    <button class="menu-toggle-btn" id="menu-toggle-btn">
                        <svg width="16" height="16" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM3 10a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM3 15a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd"/>
                        </svg>
                    </button>
                    <div class="header-brand">
                        <h1>Sistema de Gestión</h1>
                    </div>
                    <div class="header-actions">
                        <span class="user-info" id="user-info">${userDisplay}</span>
                        <button class="logout-btn" id="logout-btn">Logout</button>
                    </div>
                </div>
            </header>
        `;
        
        let headerContainer = document.getElementById('header-container');
        if (!headerContainer) {
            headerContainer = document.createElement('div');
            headerContainer.id = 'header-container';
            document.body.insertBefore(headerContainer, document.body.firstChild);
        }
        
        headerContainer.innerHTML = fallbackHtml;
        console.log('Fallback header loaded');
    }

    setupEventListeners() {
        // Wait for DOM to be ready
        document.addEventListener('DOMContentLoaded', () => {
            this.bindEvents();
        });
        
        // If DOM is already loaded
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                this.bindEvents();
            });
        } else {
            this.bindEvents();
        }
    }

    bindEvents() {
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', this.handleLogout.bind(this));
        }

        // Notify that header is loaded so menu.js can initialize
        document.dispatchEvent(new CustomEvent('headerLoaded'));
    }

    updateUserInfo() {
        // Get user info from localStorage using the same key as AuthManager
        const userInfoElement = document.getElementById('user-info');
        if (userInfoElement) {
            const userData = this.getUserData();
            if (userData) {
                userInfoElement.textContent = `Welcome, ${userData.username || userData.email || 'User'}`;
            } else {
                userInfoElement.textContent = 'Guest';
            }
        }
    }

    getUserData() {
        try {
            // Use the same key as AuthManager ('user_data')
            const userData = localStorage.getItem('user_data');
            return userData ? JSON.parse(userData) : null;
        } catch (error) {
            console.error('Error getting user data:', error);
            return null;
        }
    }

    handleLogout() {
        // Use the same logout method as AuthManager if available
        if (window.authManager) {
            window.authManager.logout();
        } else {
            // Fallback: Clear user data using the same keys as AuthManager
            localStorage.removeItem('user_data');
            localStorage.removeItem('jwt_token');
            sessionStorage.clear();
            
            // Redirect to login page
            window.location.href = '/login.html';
        }
    }

    setUserInfo(userData) {
        this.userInfo = userData;
        this.updateUserInfo();
    }
}

// Initialize header component when script loads
const headerComponent = new HeaderComponent();

// Export for use in other scripts
window.HeaderComponent = HeaderComponent;
window.headerComponent = headerComponent;