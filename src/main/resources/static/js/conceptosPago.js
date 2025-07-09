document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/conceptos-pago';
    const conceptoPagoTableBody = document.getElementById('conceptoPagoTableBody');
    const addConceptoPagoButton = document.getElementById('addConceptoPagoButton');

    // Function to get JWT token from localStorage
    const getAuthToken = () => localStorage.getItem('jwt_token');
    
    // Check authentication on page load
    const checkAuth = () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return false;
        }
        return true;
    };
    
    // Initial authentication check
    if (!checkAuth()) {
        return;
    }

    // Function to fetch all conceptos de pago
    const fetchAllConceptosPago = async () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return;
        }

        try {
            console.log('Fetching conceptos de pago with token:', token ? 'Token present' : 'No token');
            console.log('API URL:', API_BASE_URL);
            
            const response = await fetch(`${API_BASE_URL}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            console.log('Response status:', response.status);
            console.log('Response headers:', response.headers);

            if (response.status === 401) {
                console.error('401 Unauthorized - Token may be expired or invalid');
                alert('Sesión expirada o no autorizado. Por favor, inicie sesión nuevamente.');
                localStorage.removeItem('jwt_token');
                localStorage.removeItem('user_data');
                window.location.href = 'login.html';
                return;
            }

            if (!response.ok) {
                const errorText = await response.text();
                console.error('HTTP error response:', errorText);
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }

            const data = await response.json();
            console.log('Received data:', data);
            
            // Handle paginated response
            const conceptos = data.content || data;
            displayConceptosPago(conceptos);
        } catch (error) {
            console.error('Error fetching conceptos de pago:', error);
            alert(`Error al cargar los conceptos de pago: ${error.message}`);
        }
    };

    // Function to display conceptos de pago in the table
    const displayConceptosPago = (conceptos) => {
        if (!conceptoPagoTableBody) return; // Ensure we are on the list page

        conceptoPagoTableBody.innerHTML = '';
        conceptos.forEach(concepto => {
            const row = conceptoPagoTableBody.insertRow();
            
            // Nombre del concepto
            const nombreCell = row.insertCell();
            nombreCell.textContent = concepto.nombreConcepto || 'N/A';
            nombreCell.setAttribute('data-label', 'Nombre');
            
            // Tipo de concepto
            const tipoCell = row.insertCell();
            tipoCell.textContent = concepto.tipoConcepto || 'N/A';
            tipoCell.setAttribute('data-label', 'Tipo');
            
            // Descripción
            const descripcionCell = row.insertCell();
            descripcionCell.textContent = concepto.descripcion || 'N/A';
            descripcionCell.setAttribute('data-label', 'Descripción');
            
            // Permite recargos
            const recargosCell = row.insertCell();
            recargosCell.innerHTML = getPermiteRecargosBadge(concepto.permiteRecargos);
            recargosCell.setAttribute('data-label', 'Permite Recargos');
            
            // Estatus
            const estatusCell = row.insertCell();
            estatusCell.innerHTML = getStatusBadge(concepto.activo);
            estatusCell.setAttribute('data-label', 'Estatus');
            
            // Actions column
            const actionsCell = row.insertCell();
            
            // Check if user is admin to show edit/delete buttons
            const isAdmin = authManager && authManager.isAdmin();
            
            let actionsHTML = '';
            if (isAdmin) {
                actionsHTML = `
                    <button class="edit-button" onclick="editConceptoPago(${concepto.idConcepto})">
                        Editar
                    </button>
                    <button class="delete-button" onclick="deleteConceptoPago(${concepto.idConcepto}, '${(concepto.nombreConcepto || 'Concepto').replace(/'/g, "\\'")}')">
                        Eliminar
                    </button>
                `;
            } else {
                actionsHTML = `
                    <button class="view-button" onclick="viewConceptoPago(${concepto.idConcepto})">
                        Ver
                    </button>
                `;
            }
            
            actionsCell.innerHTML = actionsHTML;
            actionsCell.setAttribute('data-label', 'Acciones');
        });
    };

    // Store all conceptos de pago for filtering
    let allConceptosPago = [];
    
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

    // Enhanced function to fetch all conceptos de pago and store them
    const fetchAllConceptosPagoEnhanced = async () => {
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

            const data = await response.json();
            const conceptos = data.content || data;
            allConceptosPago = conceptos;
            displayConceptosPago(conceptos);
        } catch (error) {
            console.error('Error fetching conceptos de pago:', error);
            alert('Error al cargar los conceptos de pago.');
        }
    };

    // Initial fetch for the list page
    if (conceptoPagoTableBody) {
        fetchAllConceptosPagoEnhanced();
    }

    // Event listener for the Add ConceptoPago button
    if (addConceptoPagoButton) {
        // Hide add button for non-admin users
        if (!authManager.isAdmin()) {
            addConceptoPagoButton.style.display = 'none';
        } else {
            addConceptoPagoButton.addEventListener('click', () => {
                window.location.href = 'concepto-pago-add.html';
            });
        }
    }

    // Function to edit a concepto de pago
    window.editConceptoPago = (idConcepto) => {
        window.location.href = `concepto-pago-edit.html?id=${idConcepto}`;
    };

    // Function to view a concepto de pago (for non-admin users)
    window.viewConceptoPago = (idConcepto) => {
        window.location.href = `concepto-pago-view.html?id=${idConcepto}`;
    };

    // Function to delete a concepto de pago
    window.deleteConceptoPago = async (idConcepto, nombreConcepto) => {
        if (confirm(`¿Está seguro que desea eliminar el concepto de pago "${nombreConcepto}"?`)) {
            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${idConcepto}`, {
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
                    alert('Concepto de pago no encontrado.');
                    return;
                }

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                alert('Concepto de pago eliminado exitosamente.');
                fetchAllConceptosPagoEnhanced(); // Refresh the list
            } catch (error) {
                console.error('Error deleting concepto de pago:', error);
                alert('Error al eliminar el concepto de pago.');
            }
        }
    };

    // Function to get status badge HTML
    const getStatusBadge = (activo) => {
        if (activo === null || activo === undefined) return '<span class="badge badge-secondary">N/A</span>';
        
        const badgeClass = activo ? 'badge-success' : 'badge-danger';
        const status = activo ? 'ACTIVO' : 'INACTIVO';
        
        return `<span class="badge ${badgeClass}">${status}</span>`;
    };

    // Function to get permite recargos badge HTML
    const getPermiteRecargosBadge = (permiteRecargos) => {
        if (permiteRecargos === null || permiteRecargos === undefined) return '<span class="badge badge-secondary">N/A</span>';
        
        const badgeClass = permiteRecargos ? 'badge-success' : 'badge-danger';
        const status = permiteRecargos ? 'SÍ' : 'NO';
        
        return `<span class="badge ${badgeClass}">${status}</span>`;
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
        
        allConceptosPago.forEach(concepto => {
            let value;
            switch (filterType) {
                case 'tipo':
                    value = concepto.tipoConcepto;
                    break;
                case 'activo':
                    value = concepto.activo ? 'ACTIVO' : 'INACTIVO';
                    break;
                case 'permiteRecargos':
                    value = concepto.permiteRecargos ? 'SÍ' : 'NO';
                    break;
                case 'inmobiliaria':
                    // Get the inmobiliaria name from the mapping
                    value = inmobiliariaMap.get(concepto.idInmobiliaria);
                    break;
            }
            
            if (value !== null && value !== undefined && value !== '') {
                uniqueValues.add(value);
            }
        });

        // Sort values
        const sortedValues = Array.from(uniqueValues).sort((a, b) => {
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
            displayConceptosPago(allConceptosPago);
            return;
        }

        const selectedValue = filterValue.value;
        if (!selectedValue) {
            alert('Por favor seleccione un valor para filtrar.');
            return;
        }

        const filteredConceptos = allConceptosPago.filter(concepto => {
            switch (selectedFilter) {
                case 'tipo':
                    return concepto.tipoConcepto === selectedValue;
                case 'activo':
                    const statusValue = concepto.activo ? 'ACTIVO' : 'INACTIVO';
                    return statusValue === selectedValue;
                case 'permiteRecargos':
                    const recargosValue = concepto.permiteRecargos ? 'SÍ' : 'NO';
                    return recargosValue === selectedValue;
                case 'inmobiliaria':
                    // Compare using the inmobiliaria name from the mapping
                    return inmobiliariaMap.get(concepto.idInmobiliaria) === selectedValue;
                default:
                    return true;
            }
        });

        displayConceptosPago(filteredConceptos);
    };

    // Function to clear filter
    const clearFilter = () => {
        filterSelect.value = 'todos';
        filterValueGroup.style.display = 'none';
        displayConceptosPago(allConceptosPago);
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