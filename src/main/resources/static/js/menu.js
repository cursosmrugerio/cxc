document.addEventListener('DOMContentLoaded', () => {
    fetch('/components/menu.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('menu-container').innerHTML = data;
            
            // Initialize menu toggle functionality
            const menuToggle = document.getElementById('menu-toggle-btn');
            const menu = document.querySelector('.main-menu');
            
            if (menuToggle && menu) {
                menuToggle.addEventListener('click', () => {
                    menu.classList.toggle('open');
                });
            }
            
            // Set active menu item based on current page
            const currentPath = window.location.pathname;
            const menuLinks = document.querySelectorAll('.main-menu a');
            
            menuLinks.forEach(link => {
                const linkPath = new URL(link.href).pathname;
                if (linkPath === currentPath) {
                    link.parentElement.classList.add('active');
                }
            });
        });
});