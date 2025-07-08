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
            
            // Nombre Comercial
            const nombreCell = row.insertCell();
            nombreCell.textContent = inmobiliaria.nombreComercial;
            nombreCell.setAttribute('data-label', 'Nombre Comercial');
            
            // Razón Social
            const razonCell = row.insertCell();
            razonCell.textContent = inmobiliaria.razonSocial;
            razonCell.setAttribute('data-label', 'Razón Social');
            
            // Ciudad
            const ciudadCell = row.insertCell();
            ciudadCell.textContent = inmobiliaria.ciudad;
            ciudadCell.setAttribute('data-label', 'Ciudad');
            
            // Estatus
            const estatusCell = row.insertCell();
            estatusCell.textContent = inmobiliaria.estatus;
            estatusCell.setAttribute('data-label', 'Estatus');
            
            // Actions column
            const actionsCell = row.insertCell();
            actionsCell.innerHTML = `
                <button class="edit-button" onclick="editInmobiliaria(${inmobiliaria.idInmobiliaria})">
                    Editar
                </button>
                <button class="delete-button" onclick="deleteInmobiliaria(${inmobiliaria.idInmobiliaria}, '${inmobiliaria.nombreComercial}')">
                    Eliminar
                </button>
            `;
            actionsCell.setAttribute('data-label', 'Acciones');
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

    // Function to edit an inmobiliaria
    window.editInmobiliaria = (idInmobiliaria) => {
        window.location.href = `inmobiliaria-edit.html?id=${idInmobiliaria}`;
    };

    // Function to delete an inmobiliaria
    window.deleteInmobiliaria = async (idInmobiliaria, nombreComercial) => {
        if (confirm(`¿Está seguro que desea eliminar la inmobiliaria "${nombreComercial}"?`)) {
            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${idInmobiliaria}`, {
                    method: 'DELETE',
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

                alert('Inmobiliaria eliminada exitosamente.');
                fetchAllInmobiliarias(); // Refresh the list
            } catch (error) {
                console.error('Error deleting inmobiliaria:', error);
                alert('Error al eliminar la inmobiliaria.');
            }
        }
    };
});