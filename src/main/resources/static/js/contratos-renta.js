document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/contratos-renta';
    const PROPIEDADES_API_URL = 'http://localhost:8080/api/v1/propiedades';
    const contratoTableBody = document.getElementById('contratoTableBody');
    const addContratoButton = document.getElementById('addContratoButton');

    // Function to get JWT token from localStorage
    const getAuthToken = () => localStorage.getItem('jwt_token');

    // Store all contratos for filtering
    let allContratos = [];
    let propiedadMap = new Map();
    let inmobiliariaMap = new Map();

    // Function to fetch propiedades and create mapping
    const fetchPropiedades = async () => {
        const token = getAuthToken();
        if (!token) return;

        try {
            const response = await fetch(`${PROPIEDADES_API_URL}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const propiedades = await response.json();
                
                // Create mapping from id to property info
                propiedades.forEach(propiedad => {
                    propiedadMap.set(propiedad.idPropiedad, {
                        direccion: propiedad.direccionCompleta,
                        tipo: propiedad.tipoPropiedad,
                        idInmobiliaria: propiedad.idInmobiliaria
                    });
                });
            }
        } catch (error) {
            console.error('Error fetching propiedades:', error);
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
                
                // Create mapping from id to inmobiliaria name
                inmobiliarias.forEach(inmobiliaria => {
                    inmobiliariaMap.set(inmobiliaria.idInmobiliaria, inmobiliaria.nombreComercial);
                });
            }
        } catch (error) {
            console.error('Error fetching inmobiliarias:', error);
        }
    };

    // Function to fetch all contratos
    const fetchAllContratos = async () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return;
        }

        try {
            // First fetch propiedades and inmobiliarias to create the mappings
            await fetchPropiedades();
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

            const contratos = await response.json();
            allContratos = contratos;
            displayContratos(contratos);
        } catch (error) {
            console.error('Error fetching contratos:', error);
            alert('Error al cargar los contratos.');
        }
    };

    // Function to display contratos in the table
    const displayContratos = (contratos) => {
        if (!contratoTableBody) return;

        contratoTableBody.innerHTML = '';
        contratos.forEach(contrato => {
            const row = contratoTableBody.insertRow();
            
            // Propiedad
            const propiedadCell = row.insertCell();
            const propiedadInfo = propiedadMap.get(contrato.idPropiedad);
            if (propiedadInfo) {
                propiedadCell.innerHTML = `
                    <div class="property-info">
                        <div class="property-type">${propiedadInfo.tipo || 'N/A'}</div>
                        <div class="property-address">${propiedadInfo.direccion || 'N/A'}</div>
                    </div>
                `;
            } else {
                propiedadCell.textContent = `ID: ${contrato.idPropiedad}`;
            }
            propiedadCell.setAttribute('data-label', 'Propiedad');
            
            // Fecha inicio
            const inicioCell = row.insertCell();
            inicioCell.textContent = formatDate(contrato.fechaInicioContrato);
            inicioCell.setAttribute('data-label', 'Inicio');
            
            // Fecha fin
            const finCell = row.insertCell();
            finCell.textContent = formatDate(contrato.fechaFinContrato);
            finCell.setAttribute('data-label', 'Fin');
            
            // Duración
            const duracionCell = row.insertCell();
            duracionCell.textContent = contrato.duracionMeses ? `${contrato.duracionMeses} meses` : 'N/A';
            duracionCell.setAttribute('data-label', 'Duración');
            
            // Estatus
            const estatusCell = row.insertCell();
            estatusCell.textContent = contrato.estatusContrato || 'N/A';
            estatusCell.setAttribute('data-label', 'Estatus');
            
            // Actions column
            const actionsCell = row.insertCell();
            actionsCell.innerHTML = `
                <button class="edit-button" onclick="editContrato(${contrato.idContrato})">
                    Editar
                </button>
                <button class="delete-button" onclick="deleteContrato(${contrato.idContrato}, '${(contrato.idContrato || 'Contrato').toString().replace(/'/g, "\\'")}')">
                    Eliminar
                </button>
            `;
            actionsCell.setAttribute('data-label', 'Acciones');
        });
    };

    // Function to format date
    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
    };


    // Initial fetch
    if (contratoTableBody) {
        fetchAllContratos();
    }

    // Event listener for Add Contrato button
    if (addContratoButton) {
        addContratoButton.addEventListener('click', () => {
            window.location.href = 'contrato-add.html';
        });
    }


    // Function to edit contrato
    window.editContrato = (idContrato) => {
        window.location.href = `contrato-edit.html?id=${idContrato}`;
    };


    // Function to delete contrato
    window.deleteContrato = async (idContrato, contratoLabel) => {
        if (confirm(`¿Está seguro que desea eliminar el contrato "${contratoLabel}"?`)) {
            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${idContrato}`, {
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
                    alert('Contrato no encontrado.');
                    return;
                }

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                alert('Contrato eliminado exitosamente.');
                fetchAllContratos(); // Refresh the list
            } catch (error) {
                console.error('Error deleting contrato:', error);
                alert('Error al eliminar el contrato.');
            }
        }
    };

    // Filter functionality
    const filterSelect = document.getElementById('filterSelect');
    const filterValueGroup = document.getElementById('filterValueGroup');
    const filterValue = document.getElementById('filterValue');
    const dateFilterGroup = document.getElementById('dateFilterGroup');
    const dateFilter = document.getElementById('dateFilter');
    const applyFilterButton = document.getElementById('applyFilterButton');
    const clearFilterButton = document.getElementById('clearFilterButton');

    // Function to populate filter values
    const populateFilterValues = (filterType) => {
        if (!filterValue) return;

        filterValue.innerHTML = '';
        filterValueGroup.style.display = 'none';
        dateFilterGroup.style.display = 'none';
        
        if (filterType === 'todos') {
            return;
        }

        if (filterType === 'proximosVencer') {
            dateFilterGroup.style.display = 'block';
            return;
        }

        filterValueGroup.style.display = 'block';
        
        const uniqueValues = new Set();
        
        allContratos.forEach(contrato => {
            let value;
            switch (filterType) {
                case 'estatus':
                    value = contrato.estatusContrato;
                    break;
                case 'propiedad':
                    const propiedadInfo = propiedadMap.get(contrato.idPropiedad);
                    value = propiedadInfo ? `${propiedadInfo.tipo} - ${propiedadInfo.direccion}` : `ID: ${contrato.idPropiedad}`;
                    break;
                case 'duracion':
                    value = contrato.duracionMeses;
                    break;
                case 'inmobiliaria':
                    const propInfo = propiedadMap.get(contrato.idPropiedad);
                    if (propInfo && propInfo.idInmobiliaria) {
                        value = inmobiliariaMap.get(propInfo.idInmobiliaria);
                    }
                    break;
            }
            
            if (value !== null && value !== undefined && value !== '') {
                uniqueValues.add(value);
            }
        });

        // Sort values
        const sortedValues = Array.from(uniqueValues).sort((a, b) => {
            if (filterType === 'duracion') {
                return parseInt(a) - parseInt(b);
            }
            return a.toString().localeCompare(b.toString());
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
            displayContratos(allContratos);
            return;
        }

        if (selectedFilter === 'proximosVencer') {
            dateFilterGroup.style.display = 'block';
            const days = parseInt(dateFilter.value) || 30;
            const today = new Date();
            const filteredContratos = allContratos.filter(contrato => {
                if (!contrato.fechaFinContrato || contrato.estatusContrato !== 'ACTIVO') return false;
                const endDate = new Date(contrato.fechaFinContrato);
                const daysUntilExpiration = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));
                return daysUntilExpiration <= days && daysUntilExpiration >= 0;
            });
            displayContratos(filteredContratos);
            return;
        }

        const selectedValue = filterValue.value;
        if (!selectedValue) {
            alert('Por favor seleccione un valor para filtrar.');
            return;
        }

        const filteredContratos = allContratos.filter(contrato => {
            switch (selectedFilter) {
                case 'estatus':
                    return contrato.estatusContrato === selectedValue;
                case 'propiedad':
                    const propiedadInfo = propiedadMap.get(contrato.idPropiedad);
                    const propiedadDisplay = propiedadInfo ? `${propiedadInfo.tipo} - ${propiedadInfo.direccion}` : `ID: ${contrato.idPropiedad}`;
                    return propiedadDisplay === selectedValue;
                case 'duracion':
                    return contrato.duracionMeses === parseInt(selectedValue);
                case 'inmobiliaria':
                    const propInfo = propiedadMap.get(contrato.idPropiedad);
                    if (propInfo && propInfo.idInmobiliaria) {
                        const inmobiliariaName = inmobiliariaMap.get(propInfo.idInmobiliaria);
                        return inmobiliariaName === selectedValue;
                    }
                    return false;
                default:
                    return true;
            }
        });

        displayContratos(filteredContratos);
    };

    // Function to clear filter
    const clearFilter = () => {
        filterSelect.value = 'todos';
        filterValueGroup.style.display = 'none';
        dateFilterGroup.style.display = 'none';
        displayContratos(allContratos);
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

});