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
            
            // Habitaciones
            const habitacionesCell = row.insertCell();
            habitacionesCell.textContent = propiedad.numeroHabitaciones || 'N/A';
            habitacionesCell.setAttribute('data-label', 'Habitaciones');
            
            // Estatus
            const estatusCell = row.insertCell();
            estatusCell.textContent = propiedad.estatusPropiedad || 'N/A';
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
            window.location.href = 'propiedad-add.html';
        });
    }

    // Function to edit a propiedad
    window.editPropiedad = (idPropiedad) => {
        window.location.href = `propiedad-edit.html?id=${idPropiedad}`;
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

    // Store all propiedades for filtering
    let allPropiedades = [];
    
    // Store inmobiliarias mapping (id -> name)
    let inmobiliariaMap = new Map();
    
    // Function to fetch inmobiliarias and create mapping
    const fetchInmobiliarias = async () => {
        const token = getAuthToken();
        if (!token) return;

        try {
            const response = await fetch('http://localhost:8080/api/v1/inmobiliarias?page=0&size=10000', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                const inmobiliarias = data.content || data;
                
                // Create mapping from id to name
                inmobiliarias.forEach(inmobiliaria => {
                    inmobiliariaMap.set(inmobiliaria.idInmobiliaria, inmobiliaria.nombreComercial);
                });
            }
        } catch (error) {
            console.error('Error fetching inmobiliarias:', error);
        }
    };

    // Enhanced function to fetch all propiedades and store them
    const fetchAllPropiedadesEnhanced = async () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return;
        }

        try {
            // First fetch inmobiliarias to create the mapping
            await fetchInmobiliarias();
            
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
            allPropiedades = propiedades;
            displayPropiedades(propiedades);
        } catch (error) {
            console.error('Error fetching propiedades:', error);
            alert('Error al cargar las propiedades.');
        }
    };

    // Filter functionality
    const filterSelect = document.getElementById('filterSelect');
    const filterValueGroup = document.getElementById('filterValueGroup');
    const filterValue = document.getElementById('filterValue');
    const applyFilterButton = document.getElementById('applyFilterButton');
    const clearFilterButton = document.getElementById('clearFilterButton');

    // Function to populate filter values based on selected filter type
    const populateFilterValues = (filterType) => {
        if (!filterValue) return;

        filterValue.innerHTML = '';
        
        if (filterType === 'todos') {
            filterValueGroup.style.display = 'none';
            return;
        }

        filterValueGroup.style.display = 'block';
        
        const uniqueValues = new Set();
        
        allPropiedades.forEach(propiedad => {
            let value;
            switch (filterType) {
                case 'tipo':
                    value = propiedad.tipoPropiedad;
                    break;
                case 'estatus':
                    value = propiedad.estatusPropiedad;
                    break;
                case 'habitaciones':
                    value = propiedad.numeroHabitaciones;
                    break;
                case 'inmobiliaria':
                    // Get the inmobiliaria name from the mapping
                    value = inmobiliariaMap.get(propiedad.idInmobiliaria);
                    break;
            }
            
            if (value !== null && value !== undefined && value !== '') {
                uniqueValues.add(value);
            }
        });

        // Sort values
        const sortedValues = Array.from(uniqueValues).sort((a, b) => {
            if (filterType === 'habitaciones') {
                return parseInt(a) - parseInt(b);
            }
            return a.localeCompare(b);
        });

        // Add options to select
        sortedValues.forEach(value => {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = value;
            filterValue.appendChild(option);
        });
    };

    // Function to apply filter
    const applyFilter = () => {
        const selectedFilter = filterSelect.value;
        
        if (selectedFilter === 'todos') {
            displayPropiedades(allPropiedades);
            return;
        }

        const selectedValue = filterValue.value;
        if (!selectedValue) {
            alert('Por favor seleccione un valor para filtrar.');
            return;
        }

        const filteredPropiedades = allPropiedades.filter(propiedad => {
            switch (selectedFilter) {
                case 'tipo':
                    return propiedad.tipoPropiedad === selectedValue;
                case 'estatus':
                    return propiedad.estatusPropiedad === selectedValue;
                case 'habitaciones':
                    return propiedad.numeroHabitaciones === parseInt(selectedValue);
                case 'inmobiliaria':
                    // Compare using the inmobiliaria name from the mapping
                    return inmobiliariaMap.get(propiedad.idInmobiliaria) === selectedValue;
                default:
                    return true;
            }
        });

        displayPropiedades(filteredPropiedades);
    };

    // Function to clear filter
    const clearFilter = () => {
        filterSelect.value = 'todos';
        filterValueGroup.style.display = 'none';
        displayPropiedades(allPropiedades);
    };

    // Event listeners for filter functionality
    if (filterSelect) {
        filterSelect.addEventListener('change', (e) => {
            populateFilterValues(e.target.value);
        });
    }

    if (applyFilterButton) {
        applyFilterButton.addEventListener('click', applyFilter);
    }

    if (clearFilterButton) {
        clearFilterButton.addEventListener('click', clearFilter);
    }

    // Replace the initial fetch with enhanced version
    if (propiedadTableBody) {
        fetchAllPropiedadesEnhanced();
    }
});