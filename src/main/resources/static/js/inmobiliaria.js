document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/inmobiliarias';
    const inmobiliariaTableBody = document.getElementById('inmobiliariaTableBody');

    // Function to get JWT token from localStorage
    const getAuthToken = () => localStorage.getItem('jwt_token');

    // Function to fetch all inmobiliarias
    const fetchAllInmobiliarias = async () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return;
        }

        try {
            // Fetch all inmobiliarias by setting a very large size and page 0
            const response = await fetch(`${API_BASE_URL}?page=0&size=10000&sortBy=nombreComercial&sortDir=asc`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.status === 401) {
                alert('Sesión expirada o no autorizado. Por favor, inicie sesión nuevamente.');
                window.location.href = 'login.html';
                return;
            }

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            displayInmobiliarias(data.content);
        } catch (error) {
            console.error('Error fetching inmobiliarias:', error);
            alert('Error al cargar las inmobiliarias.');
        }
    };

    // Function to display inmobiliarias in the table
    const displayInmobiliarias = (inmobiliarias) => {
        if (!inmobiliariaTableBody) return; // Ensure we are on the list page

        inmobiliariaTableBody.innerHTML = '';
        inmobiliarias.forEach(inmobiliaria => {
            const row = inmobiliariaTableBody.insertRow();
            row.insertCell().textContent = inmobiliaria.nombreComercial;
            row.insertCell().textContent = inmobiliaria.razonSocial;
            row.insertCell().textContent = inmobiliaria.rfcNit;
            row.insertCell().textContent = inmobiliaria.ciudad;
            row.insertCell().textContent = inmobiliaria.estado;
            row.insertCell().textContent = inmobiliaria.estatus;
        });
    };

    // Initial fetch for the list page
    if (inmobiliariaTableBody) {
        fetchAllInmobiliarias();
    }

    // Event listener for the Add Inmobiliaria button
    if (addInmobiliariaButton) {
        addInmobiliariaButton.addEventListener('click', () => {
            window.location.href = 'inmobiliaria-add.html';
        });
    }
});