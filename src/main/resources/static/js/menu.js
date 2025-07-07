document.addEventListener('DOMContentLoaded', () => {
    fetch('/components/menu.html')
        .then(response => response.text())
        .then(data => {
            document.getElementById('menu-container').innerHTML = data;
        });
});