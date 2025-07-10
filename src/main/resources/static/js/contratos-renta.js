document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/contratos-renta';
    const PROPIEDADES_API_URL = 'http://localhost:8080/api/v1/propiedades';
    const contratoTableBody = document.getElementById('contratoTableBody');
    const addContratoButton = document.getElementById('addContratoButton');
    const notificationsButton = document.getElementById('notificationsButton');

    // Function to get JWT token from localStorage
    const getAuthToken = () => localStorage.getItem('jwt_token');

    // Store all contratos for filtering
    let allContratos = [];
    let propiedadMap = new Map();

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
                        tipo: propiedad.tipoPropiedad
                    });
                });
            }
        } catch (error) {
            console.error('Error fetching propiedades:', error);
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
            // First fetch propiedades to create the mapping
            await fetchPropiedades();
            
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
            
            // ID
            const idCell = row.insertCell();
            idCell.textContent = contrato.idContrato;
            idCell.setAttribute('data-label', 'ID');
            
            // Propiedad
            const propiedadCell = row.insertCell();
            const propiedadInfo = propiedadMap.get(contrato.idPropiedad);
            if (propiedadInfo) {
                propiedadCell.innerHTML = `
                    <div class="property-info">
                        <div class="property-type">${propiedadInfo.tipo}</div>
                        <div class="property-address">${propiedadInfo.direccion}</div>
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
            estatusCell.innerHTML = getStatusBadge(contrato.estatusContrato);
            estatusCell.setAttribute('data-label', 'Estatus');
            
            // Depósito
            const depositoCell = row.insertCell();
            depositoCell.textContent = contrato.depositoGarantia ? 
                `$${parseFloat(contrato.depositoGarantia).toLocaleString()}` : 'N/A';
            depositoCell.setAttribute('data-label', 'Depósito');
            
            // Actions column
            const actionsCell = row.insertCell();
            actionsCell.innerHTML = `
                <div class="action-buttons">
                    <button class="view-button" onclick="viewContrato(${contrato.idContrato})">
                        Ver
                    </button>
                    <button class="edit-button" onclick="editContrato(${contrato.idContrato})">
                        Editar
                    </button>
                    ${contrato.estatusContrato === 'ACTIVO' ? `
                        <button class="renew-button" onclick="renewContrato(${contrato.idContrato})">
                            Renovar
                        </button>
                        <button class="terminate-button" onclick="terminateContrato(${contrato.idContrato})">
                            Terminar
                        </button>
                    ` : ''}
                    <button class="delete-button" onclick="deleteContrato(${contrato.idContrato})">
                        Eliminar
                    </button>
                </div>
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

    // Function to get status badge HTML
    const getStatusBadge = (status) => {
        if (!status) return '<span class="badge badge-secondary">N/A</span>';
        
        const statusClass = {
            'ACTIVO': 'badge-success',
            'VENCIDO': 'badge-warning',
            'TERMINADO': 'badge-danger',
            'SUSPENDIDO': 'badge-secondary'
        };
        
        const badgeClass = statusClass[status.toUpperCase()] || 'badge-secondary';
        return `<span class="badge ${badgeClass}">${status}</span>`;
    };

    // Function to check if contract is expiring soon
    const isExpiringSoon = (contrato, days = 30) => {
        if (!contrato.fechaFinContrato || contrato.estatusContrato !== 'ACTIVO') return false;
        
        const today = new Date();
        const endDate = new Date(contrato.fechaFinContrato);
        const daysUntilExpiration = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));
        
        return daysUntilExpiration <= days && daysUntilExpiration >= 0;
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

    // Event listener for Notifications button
    if (notificationsButton) {
        notificationsButton.addEventListener('click', () => {
            showNotifications();
        });
    }

    // Function to show notifications
    const showNotifications = async () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/notificaciones`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const notifications = await response.json();
                if (notifications.length === 0) {
                    alert('No hay contratos que requieran notificaciones.');
                } else {
                    displayContratos(notifications);
                    alert(`Se encontraron ${notifications.length} contrato(s) que requieren notificación.`);
                }
            }
        } catch (error) {
            console.error('Error fetching notifications:', error);
            alert('Error al cargar las notificaciones.');
        }
    };

    // Function to view contrato details
    window.viewContrato = async (idContrato) => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${idContrato}`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const contrato = await response.json();
                showContratoDetails(contrato);
            } else {
                alert('Error al cargar los detalles del contrato.');
            }
        } catch (error) {
            console.error('Error fetching contrato details:', error);
            alert('Error al cargar los detalles del contrato.');
        }
    };

    // Function to show contrato details in modal
    const showContratoDetails = (contrato) => {
        const modal = document.getElementById('contratoModal');
        const details = document.getElementById('contratoDetails');
        const propiedadInfo = propiedadMap.get(contrato.idPropiedad);

        details.innerHTML = `
            <div class="details-grid">
                <div class="detail-item">
                    <strong>ID Contrato:</strong> ${contrato.idContrato}
                </div>
                <div class="detail-item">
                    <strong>Propiedad:</strong> ${propiedadInfo ? `${propiedadInfo.tipo} - ${propiedadInfo.direccion}` : `ID: ${contrato.idPropiedad}`}
                </div>
                <div class="detail-item">
                    <strong>Fecha Inicio:</strong> ${formatDate(contrato.fechaInicioContrato)}
                </div>
                <div class="detail-item">
                    <strong>Fecha Fin:</strong> ${formatDate(contrato.fechaFinContrato)}
                </div>
                <div class="detail-item">
                    <strong>Duración:</strong> ${contrato.duracionMeses || 'N/A'} meses
                </div>
                <div class="detail-item">
                    <strong>Estatus:</strong> ${getStatusBadge(contrato.estatusContrato)}
                </div>
                <div class="detail-item">
                    <strong>Depósito de Garantía:</strong> ${contrato.depositoGarantia ? `$${parseFloat(contrato.depositoGarantia).toLocaleString()}` : 'N/A'}
                </div>
                <div class="detail-item">
                    <strong>Email Notificaciones:</strong> ${contrato.emailNotificaciones || 'N/A'}
                </div>
                <div class="detail-item">
                    <strong>Teléfono Notificaciones:</strong> ${contrato.telefonoNotificaciones || 'N/A'}
                </div>
                <div class="detail-item">
                    <strong>Notificación Días Previos:</strong> ${contrato.notificacionDiasPrevios || 'N/A'}
                </div>
                <div class="detail-item full-width">
                    <strong>Condiciones Especiales:</strong> ${contrato.condicionesEspeciales || 'Ninguna'}
                </div>
            </div>
        `;

        modal.style.display = 'block';
    };

    // Function to edit contrato
    window.editContrato = (idContrato) => {
        window.location.href = `contrato-edit.html?id=${idContrato}`;
    };

    // Function to terminate contrato
    window.terminateContrato = (idContrato) => {
        const modal = document.getElementById('terminateModal');
        modal.style.display = 'block';
        
        document.getElementById('confirmTerminate').onclick = () => {
            performTerminateContrato(idContrato);
            modal.style.display = 'none';
        };
    };

    // Function to perform terminate contrato
    const performTerminateContrato = async (idContrato) => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${idContrato}/terminar`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert('Contrato terminado exitosamente.');
                fetchAllContratos();
            } else {
                alert('Error al terminar el contrato.');
            }
        } catch (error) {
            console.error('Error terminating contrato:', error);
            alert('Error al terminar el contrato.');
        }
    };

    // Function to renew contrato
    window.renewContrato = (idContrato) => {
        const modal = document.getElementById('renewModal');
        modal.style.display = 'block';
        
        document.getElementById('confirmRenew').onclick = () => {
            const months = document.getElementById('renewMonths').value;
            if (months && months > 0) {
                performRenewContrato(idContrato, parseInt(months));
                modal.style.display = 'none';
            } else {
                alert('Por favor ingrese un número válido de meses.');
            }
        };
    };

    // Function to perform renew contrato
    const performRenewContrato = async (idContrato, months) => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${idContrato}/renovar?meses=${months}`, {
                method: 'PATCH',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert(`Contrato renovado exitosamente por ${months} meses.`);
                fetchAllContratos();
            } else {
                alert('Error al renovar el contrato.');
            }
        } catch (error) {
            console.error('Error renewing contrato:', error);
            alert('Error al renovar el contrato.');
        }
    };

    // Function to delete contrato
    window.deleteContrato = async (idContrato) => {
        if (confirm('¿Está seguro que desea eliminar este contrato?')) {
            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/${idContrato}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (response.ok) {
                    alert('Contrato eliminado exitosamente.');
                    fetchAllContratos();
                } else {
                    alert('Error al eliminar el contrato.');
                }
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
            const days = parseInt(dateFilter.value) || 30;
            const filteredContratos = allContratos.filter(contrato => isExpiringSoon(contrato, days));
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

    // Modal functionality
    const modals = document.querySelectorAll('.modal');
    const closeButtons = document.querySelectorAll('.close');

    closeButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.target.closest('.modal').style.display = 'none';
        });
    });

    // Cancel buttons
    document.getElementById('cancelTerminate')?.addEventListener('click', () => {
        document.getElementById('terminateModal').style.display = 'none';
    });

    document.getElementById('cancelRenew')?.addEventListener('click', () => {
        document.getElementById('renewModal').style.display = 'none';
    });

    // Close modal when clicking outside
    window.addEventListener('click', (e) => {
        modals.forEach(modal => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
    });
});