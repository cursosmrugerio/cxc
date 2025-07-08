document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/propiedades';
    const propiedadTableBody = document.getElementById('propiedadTableBody');
    const addPropiedadButton = document.getElementById('addPropiedadButton');

    // Function to get JWT token from localStorage
    const getAuthToken = () => localStorage.getItem('jwt_token');

    // Function to fetch all propiedades
    const fetchAllPropiedades = async () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}`, {
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

            const propiedades = await response.json();
            displayPropiedades(propiedades);
        } catch (error) {
            console.error('Error fetching propiedades:', error);
            alert('Error al cargar las propiedades.');
        }
    };

    // Function to display propiedades in the table
    const displayPropiedades = (propiedades) => {
        if (!propiedadTableBody) return; // Ensure we are on the list page

        propiedadTableBody.innerHTML = '';
        propiedades.forEach(propiedad => {
            const row = propiedadTableBody.insertRow();
            
            // Tipo de propiedad
            const tipoCell = row.insertCell();
            tipoCell.textContent = propiedad.tipoPropiedad || 'N/A';
            tipoCell.setAttribute('data-label', 'Tipo');
            
            // Dirección
            const direccionCell = row.insertCell();
            direccionCell.textContent = propiedad.direccionCompleta || 'N/A';
            direccionCell.setAttribute('data-label', 'Dirección');
            
            // Superficie Total
            const superficieTotalCell = row.insertCell();
            superficieTotalCell.textContent = propiedad.superficieTotal ? 
                `${propiedad.superficieTotal} m²` : 'N/A';
            superficieTotalCell.setAttribute('data-label', 'Superficie Total');
            
            // Superficie Construida
            const superficieConstruidaCell = row.insertCell();
            superficieConstruidaCell.textContent = propiedad.superficieConstruida ? 
                `${propiedad.superficieConstruida} m²` : 'N/A';
            superficieConstruidaCell.setAttribute('data-label', 'Superficie Construida');
            
            // Habitaciones
            const habitacionesCell = row.insertCell();
            habitacionesCell.textContent = propiedad.numeroHabitaciones || 'N/A';
            habitacionesCell.setAttribute('data-label', 'Habitaciones');
            
            // Baños
            const banosCell = row.insertCell();
            banosCell.textContent = propiedad.numeroBanos || 'N/A';
            banosCell.setAttribute('data-label', 'Baños');
            
            // Estatus
            const estatusCell = row.insertCell();
            estatusCell.innerHTML = getStatusBadge(propiedad.estatusPropiedad);
            estatusCell.setAttribute('data-label', 'Estatus');
            
            // Actions column
            const actionsCell = row.insertCell();
            actionsCell.innerHTML = `
                <button class="edit-button" onclick="editPropiedad(${propiedad.idPropiedad})">
                    Editar
                </button>
                <button class="delete-button" onclick="deletePropiedad(${propiedad.idPropiedad}, '${(propiedad.tipoPropiedad || 'Propiedad').replace(/'/g, "\\'")}')">
                    Eliminar
                </button>
            `;
            actionsCell.setAttribute('data-label', 'Acciones');
        });
    };

    // Initial fetch for the list page
    if (propiedadTableBody) {
        fetchAllPropiedades();
    }

    // Event listener for the Add Propiedad button
    if (addPropiedadButton) {
        addPropiedadButton.addEventListener('click', () => {
            window.location.href = 'propiedades-add.html';
        });
    }

    // Function to edit a propiedad
    window.editPropiedad = (idPropiedad) => {
        window.location.href = `propiedades-edit.html?id=${idPropiedad}`;
    };

    // Function to delete a propiedad
    window.deletePropiedad = async (idPropiedad, tipoPropiedad) => {
        if (confirm(`¿Está seguro que desea eliminar la propiedad "${tipoPropiedad}"?`)) {
            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${idPropiedad}`, {
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

                if (response.status === 404) {
                    alert('Propiedad no encontrada.');
                    return;
                }

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                alert('Propiedad eliminada exitosamente.');
                fetchAllPropiedades(); // Refresh the list
            } catch (error) {
                console.error('Error deleting propiedad:', error);
                alert('Error al eliminar la propiedad.');
            }
        }
    };

    // Function to format numbers with proper decimal places
    const formatNumber = (num) => {
        if (!num) return 'N/A';
        return parseFloat(num).toFixed(2);
    };

    // Function to get status badge HTML
    const getStatusBadge = (status) => {
        if (!status) return '<span class="badge badge-secondary">N/A</span>';
        
        const statusClass = {
            'DISPONIBLE': 'badge-success',
            'OCUPADA': 'badge-danger',
            'MANTENIMIENTO': 'badge-warning',
            'VENDIDA': 'badge-secondary'
        };
        
        const badgeClass = statusClass[status.toUpperCase()] || 'badge-secondary';
        return `<span class="badge ${badgeClass}">${status}</span>`;
    };
});