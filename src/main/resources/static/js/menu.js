document.addEventListener('DOMContentLoaded', () => {
    fetch('/components/menu.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('menu-container').innerHTML = data;
            
            // Create overlay for mobile menu
            const overlay = document.createElement('div');
            overlay.className = 'menu-overlay';
            document.body.appendChild(overlay);
            
            // Wait for header to be loaded before initializing menu toggle
            const initializeMenuToggle = () => {
                const menuToggle = document.getElementById('menu-toggle-btn');
                const menu = document.querySelector('.main-menu');
                
                if (menuToggle && menu) {
                    setupMenuToggleEvents(menuToggle, menu, overlay);
                } else {
                    // Retry after a short delay if elements aren't ready
                    setTimeout(initializeMenuToggle, 100);
                }
            };
            
            // Listen for header loaded event or initialize immediately if header exists
            if (document.getElementById('menu-toggle-btn')) {
                initializeMenuToggle();
            } else {
                document.addEventListener('headerLoaded', initializeMenuToggle);
                // Fallback: try to initialize after a delay
                setTimeout(initializeMenuToggle, 500);
            }
            
            // Function to setup menu toggle events
            function setupMenuToggleEvents(menuToggle, menu, overlay) {
                // Toggle menu
                menuToggle.addEventListener('click', (e) => {
                    e.stopPropagation();
                    menu.classList.toggle('open');
                    overlay.classList.toggle('active');
                    document.body.classList.toggle('menu-open');
                });
                
                // Close menu when clicking overlay
                overlay.addEventListener('click', () => {
                    menu.classList.remove('open');
                    overlay.classList.remove('active');
                    document.body.classList.remove('menu-open');
                });
                
                // Close menu when clicking outside on mobile
                document.addEventListener('click', (e) => {
                    if (window.innerWidth <= 768 && 
                        menu.classList.contains('open') && 
                        !menu.contains(e.target) && 
                        !menuToggle.contains(e.target)) {
                        menu.classList.remove('open');
                        overlay.classList.remove('active');
                        document.body.classList.remove('menu-open');
                    }
                });
                
                // Close menu on window resize if mobile menu is open
                window.addEventListener('resize', () => {
                    if (window.innerWidth > 768) {
                        menu.classList.remove('open');
                        overlay.classList.remove('active');
                        document.body.classList.remove('menu-open');
                    }
                });
                
                console.log('Menu toggle functionality initialized');
            }
            
            // Set active menu item based on current page
            const currentPath = window.location.pathname;
            const menuLinks = document.querySelectorAll('.main-menu a');
            
            menuLinks.forEach(link => {
                const linkPath = new URL(link.href).pathname;
                if (linkPath === currentPath || 
                    (currentPath.includes('inmobiliaria') && linkPath.includes('inmobiliaria'))) {
                    link.parentElement.classList.add('active');
                }
                
                // Close mobile menu when clicking menu link
                link.addEventListener('click', () => {
                    if (window.innerWidth <= 768) {
                        menu.classList.remove('open');
                        overlay.classList.remove('active');
                        document.body.classList.remove('menu-open');
                    }
                });
            });
        });
});