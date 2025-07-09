document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/configuracion-recargos';
    const configuracionRecargoTableBody = document.getElementById('configuracionRecargoTableBody');
    const addConfiguracionRecargoButton = document.getElementById('addConfiguracionRecargoButton');

    // Filter elements
    const filterSelect = document.getElementById('filterSelect');
    const filterValueGroup = document.getElementById('filterValueGroup');
    const filterValue = document.getElementById('filterValue');
    const applyFilterButton = document.getElementById('applyFilterButton');
    const clearFilterButton = document.getElementById('clearFilterButton');

    // Store all configuraciones for filtering
    let allConfiguracionesRecargos = [];

    // Store inmobiliarias mapping (id -> name)
    let inmobiliariaMap = new Map();

    const getAuthToken = () => localStorage.getItem('jwt_token');

    const checkAuth = () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return false;
        }
        return true;
    };

    if (!checkAuth()) {
        return;
    }

    const fetchAllConfiguracionRecargos = async () => {
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
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.status === 401) {
                alert('Sesión expirada o no autorizado. Por favor, inicie sesión nuevamente.');
                localStorage.removeItem('jwt_token');
                localStorage.removeItem('user_data');
                window.location.href = 'login.html';
                return;
            }

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            allConfiguracionesRecargos = data.content || data; // Store all data
            displayConfiguracionRecargos(allConfiguracionesRecargos); // Display all initially
        } catch (error) {
            console.error('Error fetching configuracion de recargos:', error);
            alert(`Error al cargar las configuraciones de recargos: ${error.message}`);
        }
    };

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
        
        allConfiguracionesRecargos.forEach(configuracion => {
            let value;
            switch (filterType) {
                case 'tipoRecargo':
                    value = configuracion.tipoRecargo;
                    break;
                case 'activo':
                    value = configuracion.activo ? 'ACTIVO' : 'INACTIVO';
                    break;
                case 'inmobiliaria':
                    value = inmobiliariaMap.get(configuracion.idInmobiliaria);
                    break;
            }
            
            if (value !== null && value !== undefined && value !== '') {
                uniqueValues.add(value);
            }
        });

        // Sort values
        const sortedValues = Array.from(uniqueValues).sort((a, b) => a.localeCompare(b));

        // Add options to select
        sortedValues.forEach(value => {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = value;
            filterValue.appendChild(option);
        });
    };

    const displayConfiguracionRecargos = (configuraciones) => {
        if (!configuracionRecargoTableBody) return;

        configuracionRecargoTableBody.innerHTML = '';
        configuraciones.forEach(configuracion => {
            const row = configuracionRecargoTableBody.insertRow();

            row.insertCell().textContent = configuracion.tipoRecargo;
            row.insertCell().textContent = configuracion.monto;
            row.insertCell().textContent = configuracion.diaAplicacion;
            row.insertCell().innerHTML = getStatusBadge(configuracion.activo);

            const actionsCell = row.insertCell();
            actionsCell.innerHTML = `
                <button class="edit-button" onclick="editConfiguracionRecargo(${configuracion.idConfiguracionRecargo})">
                    Editar
                </button>
                <button class="delete-button" onclick="deleteConfiguracionRecargo(${configuracion.idConfiguracionRecargo}, '${configuracion.tipoRecargo}')">
                    Eliminar
                </button>
            `;
        });
    };

    // Function to apply filter
    const applyFilter = () => {
        const selectedFilter = filterSelect.value;
        
        if (selectedFilter === 'todos') {
            displayConfiguracionRecargos(allConfiguracionesRecargos);
            return;
        }

        const selectedValue = filterValue.value;
        if (!selectedValue) {
            alert('Por favor seleccione un valor para filtrar.');
            return;
        }

        const filteredConfiguraciones = allConfiguracionesRecargos.filter(configuracion => {
            switch (selectedFilter) {
                case 'tipoRecargo':
                    return configuracion.tipoRecargo === selectedValue;
                case 'activo':
                    const isActive = selectedValue === 'ACTIVO';
                    return configuracion.activo === isActive;
                case 'inmobiliaria':
                    // Compare using the inmobiliaria name from the mapping
                    return inmobiliariaMap.get(configuracion.idInmobiliaria) === selectedValue;
                default:
                    return true;
            }
        });

        displayConfiguracionRecargos(filteredConfiguraciones);
    };

    // Function to clear filter
    const clearFilter = () => {
        filterSelect.value = 'todos';
        filterValueGroup.style.display = 'none';
        displayConfiguracionRecargos(allConfiguracionesRecargos);
    };

    if (configuracionRecargoTableBody) {
        fetchAllConfiguracionRecargos();
    }

    if (addConfiguracionRecargoButton) {
        addConfiguracionRecargoButton.addEventListener('click', () => {
            window.location.href = 'configuracion-recargo-add.html';
        });
    }

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

    window.editConfiguracionRecargo = (idConfiguracionRecargo) => {
        window.location.href = `configuracion-recargo-edit.html?id=${idConfiguracionRecargo}`;
    };

    window.deleteConfiguracionRecargo = async (idConfiguracionRecargo, tipoRecargo) => {
        if (confirm(`¿Está seguro que desea eliminar la configuración de recargo "${tipoRecargo}"?`)) {
            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${idConfiguracionRecargo}`, {
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
                    alert('Configuración de recargo no encontrada.');
                    return;
                }

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                alert('Configuración de recargo eliminada exitosamente.');
                fetchAllConfiguracionRecargos();
            } catch (error) {
                console.error('Error deleting configuracion de recargo:', error);
                alert('Error al eliminar la configuración de recargo.');
            }
        }
    };

    const getStatusBadge = (activo) => {
        if (activo === null || activo === undefined) return '<span class="badge badge-secondary">N/A</span>';
        const badgeClass = activo ? 'badge-success' : 'badge-danger';
        const status = activo ? 'ACTIVO' : 'INACTIVO';
        return `<span class="badge ${badgeClass}">${status}</span>`;
    };
});