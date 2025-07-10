document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1';
    
    // Function to get JWT token from localStorage
    const getAuthToken = () => localStorage.getItem('jwt_token');

    // Check authentication
    const token = getAuthToken();
    if (!token) {
        alert('No autorizado. Por favor, inicie sesión.');
        window.location.href = 'login.html';
        return;
    }

    // Initialize dashboard
    loadDashboardStatistics();

    // Refresh dashboard function (global)
    window.refreshDashboard = () => {
        showLoadingIndicator();
        loadDashboardStatistics();
    };

    function showLoadingIndicator() {
        document.getElementById('loadingIndicator').style.display = 'block';
    }

    function hideLoadingIndicator() {
        document.getElementById('loadingIndicator').style.display = 'none';
    }

    // Main function to load all statistics
    async function loadDashboardStatistics() {
        showLoadingIndicator();
        
        try {
            await Promise.all([
                loadOverviewStatistics(),
                loadContractStatistics(),
                loadPropertyStatistics(),
                loadSystemStatistics(),
                loadRecentActivity()
            ]);
        } catch (error) {
            console.error('Error loading dashboard statistics:', error);
            alert('Error al cargar las estadísticas del dashboard.');
        } finally {
            hideLoadingIndicator();
        }
    }

    // Load overview statistics
    async function loadOverviewStatistics() {
        try {
            // Get total counts for each entity
            const [inmobiliarias, propiedades, contratos, conceptos] = await Promise.all([
                fetchWithAuth(`${API_BASE_URL}/inmobiliarias?page=0&size=1`),
                fetchWithAuth(`${API_BASE_URL}/propiedades`),
                fetchWithAuth(`${API_BASE_URL}/contratos-renta`),
                fetchWithAuth(`${API_BASE_URL}/conceptos-pago`)
            ]);

            // Update overview cards
            updateElement('totalInmobiliarias', inmobiliarias.totalElements || inmobiliarias.length || 0);
            updateElement('totalPropiedades', Array.isArray(propiedades) ? propiedades.length : 0);
            updateElement('totalContratos', Array.isArray(contratos) ? contratos.length : 0);
            updateElement('totalConceptos', Array.isArray(conceptos) ? conceptos.length : 0);

        } catch (error) {
            console.error('Error loading overview statistics:', error);
        }
    }

    // Load contract statistics
    async function loadContractStatistics() {
        try {
            // Get all contracts to analyze by status
            const contratos = await fetchWithAuth(`${API_BASE_URL}/contratos-renta`);
            
            if (Array.isArray(contratos)) {
                // Count by status
                const statusCounts = contratos.reduce((acc, contrato) => {
                    const status = contrato.estatusContrato || 'UNKNOWN';
                    acc[status] = (acc[status] || 0) + 1;
                    return acc;
                }, {});

                updateElement('contratosActivos', statusCounts['ACTIVO'] || 0);
                updateElement('contratosPendientes', statusCounts['PENDIENTE'] || 0);
                updateElement('contratosTerminados', statusCounts['TERMINADO'] || 0);

                // Calculate expiring contracts (next 30 days)
                const expiring = contratos.filter(contrato => {
                    if (!contrato.fechaFinContrato || contrato.estatusContrato !== 'ACTIVO') return false;
                    const endDate = new Date(contrato.fechaFinContrato);
                    const today = new Date();
                    const daysUntilExpiration = Math.ceil((endDate - today) / (1000 * 60 * 60 * 24));
                    return daysUntilExpiration <= 30 && daysUntilExpiration >= 0;
                });

                updateElement('contratosExpirando', expiring.length);
            }

        } catch (error) {
            console.error('Error loading contract statistics:', error);
            // Fallback: try individual status endpoints if available
            try {
                const activos = await fetchWithAuth(`${API_BASE_URL}/contratos-renta/estatus/ACTIVO/count`);
                updateElement('contratosActivos', activos || 0);
            } catch (e) {
                updateElement('contratosActivos', 0);
            }
        }
    }

    // Load property statistics
    async function loadPropertyStatistics() {
        try {
            const propiedades = await fetchWithAuth(`${API_BASE_URL}/propiedades`);
            
            if (Array.isArray(propiedades)) {
                // Count by status
                const statusCounts = propiedades.reduce((acc, propiedad) => {
                    const status = propiedad.estatusPropiedad || 'UNKNOWN';
                    acc[status] = (acc[status] || 0) + 1;
                    return acc;
                }, {});

                updateElement('propiedadesDisponibles', statusCounts['DISPONIBLE'] || 0);
                updateElement('propiedadesOcupadas', statusCounts['OCUPADA'] || 0);
                updateElement('propiedadesMantenimiento', statusCounts['MANTENIMIENTO'] || 0);
                updateElement('propiedadesVendidas', statusCounts['VENDIDA'] || 0);

                // Calculate occupation percentage
                const total = propiedades.length;
                const occupied = statusCounts['OCUPADA'] || 0;
                const occupationRate = total > 0 ? Math.round((occupied / total) * 100) : 0;
                updateElement('ocupacionPromedio', `${occupationRate}%`);
            }

        } catch (error) {
            console.error('Error loading property statistics:', error);
        }
    }

    // Load system statistics
    async function loadSystemStatistics() {
        try {
            // Try to get statistics from dedicated endpoints
            let conceptosStats = null;
            let inmobiliariasStats = null;

            try {
                conceptosStats = await fetchWithAuth(`${API_BASE_URL}/conceptos-pago/statistics`);
            } catch (e) {
                console.log('Statistics endpoint not available for conceptos-pago');
            }

            try {
                inmobiliariasStats = await fetchWithAuth(`${API_BASE_URL}/inmobiliarias/statistics`);
            } catch (e) {
                console.log('Statistics endpoint not available for inmobiliarias');
            }

            // If statistics endpoints don't work, calculate manually
            if (!conceptosStats) {
                const conceptos = await fetchWithAuth(`${API_BASE_URL}/conceptos-pago`);
                const activeConceptos = Array.isArray(conceptos) ? 
                    conceptos.filter(c => c.activo !== false).length : 0;
                updateElement('conceptosActivos', activeConceptos);
            } else {
                updateElement('conceptosActivos', conceptosStats.activeCount || 0);
            }

            if (!inmobiliariasStats) {
                const inmobiliarias = await fetchWithAuth(`${API_BASE_URL}/inmobiliarias?page=0&size=1000`);
                const activeInmobiliarias = inmobiliarias.content ? 
                    inmobiliarias.content.filter(i => i.estatus === 'ACTIVA').length : 0;
                updateElement('inmobiliariasActivas', activeInmobiliarias);
            } else {
                updateElement('inmobiliariasActivas', inmobiliariasStats.activeCount || 0);
            }

            // Get configuraciones de recargo count
            try {
                const configuraciones = await fetchWithAuth(`${API_BASE_URL}/configuracion-recargos`);
                updateElement('configuracionesRecargo', Array.isArray(configuraciones) ? configuraciones.length : 0);
            } catch (e) {
                updateElement('configuracionesRecargo', 0);
            }

        } catch (error) {
            console.error('Error loading system statistics:', error);
        }
    }

    // Load recent activity (simplified version)
    async function loadRecentActivity() {
        try {
            const activityContainer = document.getElementById('recentActivity');
            
            // Get recent data from different endpoints
            const [contratos, propiedades] = await Promise.all([
                fetchWithAuth(`${API_BASE_URL}/contratos-renta`),
                fetchWithAuth(`${API_BASE_URL}/propiedades`)
            ]);

            const activities = [];

            // Add recent contracts
            if (Array.isArray(contratos)) {
                contratos.slice(0, 3).forEach(contrato => {
                    activities.push({
                        type: 'contrato',
                        message: `Contrato ID: ${contrato.idContrato} - ${contrato.estatusContrato}`,
                        time: contrato.fechaInicioContrato ? new Date(contrato.fechaInicioContrato).toLocaleDateString() : 'N/A'
                    });
                });
            }

            // Add recent properties
            if (Array.isArray(propiedades)) {
                propiedades.slice(0, 2).forEach(propiedad => {
                    activities.push({
                        type: 'propiedad',
                        message: `Propiedad: ${propiedad.tipoPropiedad || 'N/A'} - ${propiedad.estatusPropiedad}`,
                        time: 'Reciente'
                    });
                });
            }

            // Display activities
            if (activities.length === 0) {
                activityContainer.innerHTML = '<p class="no-activity">No hay actividad reciente</p>';
            } else {
                activityContainer.innerHTML = activities.map(activity => `
                    <div class="activity-item">
                        <div class="activity-icon">${activity.type === 'contrato' ? '📋' : '🏠'}</div>
                        <div class="activity-content">
                            <p class="activity-message">${activity.message}</p>
                            <span class="activity-time">${activity.time}</span>
                        </div>
                    </div>
                `).join('');
            }

        } catch (error) {
            console.error('Error loading recent activity:', error);
            document.getElementById('recentActivity').innerHTML = '<p class="error">Error al cargar actividad reciente</p>';
        }
    }

    // Helper function to fetch with authentication
    async function fetchWithAuth(url) {
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401) {
            alert('Sesión expirada. Por favor, inicie sesión nuevamente.');
            window.location.href = 'login.html';
            return null;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    // Helper function to update element text
    function updateElement(elementId, value) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = value;
        }
    }

    // Auto-refresh dashboard every 5 minutes
    setInterval(loadDashboardStatistics, 5 * 60 * 1000);
});