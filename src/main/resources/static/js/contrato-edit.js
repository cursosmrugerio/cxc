document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/contratos-renta';
    const contratoEditForm = document.getElementById('contratoEditForm');
    
    // Check authentication first
    if (!authManager.isAuthenticated()) {
        alert('No autorizado. Por favor, inicie sesión.');
        window.location.href = 'login.html';
        return;
    }
    
    // Get contrato ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const contratoId = urlParams.get('id');

    // Function to format datetime for datetime-local input
    const formatDateTimeLocal = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        // Format as YYYY-MM-DDTHH:MM
        return date.toISOString().slice(0, 16);
    };

    // Function to load contrato data
    const loadContratoData = async () => {
        if (!contratoId) {
            alert('ID de contrato no encontrado.');
            window.location.href = 'contratos-renta.html';
            return;
        }

        try {
            const response = await authManager.authenticatedFetch(`${API_BASE_URL}/${contratoId}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const contrato = await response.json();
            
            // Populate form fields
            document.getElementById('idPropiedad').value = contrato.idPropiedad || '';
            document.getElementById('fechaInicioContrato').value = formatDateTimeLocal(contrato.fechaInicioContrato);
            document.getElementById('duracionMeses').value = contrato.duracionMeses || '';
            document.getElementById('depositoGarantia').value = contrato.depositoGarantia || '';
            document.getElementById('emailNotificaciones').value = contrato.emailNotificaciones || '';
            document.getElementById('telefonoNotificaciones').value = contrato.telefonoNotificaciones || '';
            document.getElementById('notificacionDiasPrevios').value = contrato.notificacionDiasPrevios || '';
            document.getElementById('estatusContrato').value = contrato.estatusContrato || 'ACTIVO';
            document.getElementById('condicionesEspeciales').value = contrato.condicionesEspeciales || '';

        } catch (error) {
            console.error('Error loading contrato data:', error);
            alert('Error al cargar los datos del contrato.');
            window.location.href = 'contratos-renta.html';
        }
    };

    // Load data when page loads
    loadContratoData();

    // Handle form submission
    if (contratoEditForm) {
        contratoEditForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            // Validate required fields
            const idPropiedad = document.getElementById('idPropiedad').value;
            const fechaInicioContrato = document.getElementById('fechaInicioContrato').value;
            const duracionMeses = document.getElementById('duracionMeses').value;

            if (!idPropiedad) {
                alert('El ID de Propiedad es requerido.');
                return;
            }
            if (!fechaInicioContrato) {
                alert('La fecha de inicio es requerida.');
                return;
            }
            if (!duracionMeses) {
                alert('La duración en meses es requerida.');
                return;
            }

            const contratoData = {
                idPropiedad: parseInt(idPropiedad),
                fechaInicioContrato: new Date(fechaInicioContrato).toISOString(),
                duracionMeses: parseInt(duracionMeses),
                depositoGarantia: parseFloat(document.getElementById('depositoGarantia').value) || null,
                emailNotificaciones: document.getElementById('emailNotificaciones').value || null,
                telefonoNotificaciones: document.getElementById('telefonoNotificaciones').value || null,
                notificacionDiasPrevios: parseInt(document.getElementById('notificacionDiasPrevios').value) || null,
                estatusContrato: document.getElementById('estatusContrato').value || 'ACTIVO',
                condicionesEspeciales: document.getElementById('condicionesEspeciales').value || null
            };

            try {
                const response = await authManager.authenticatedFetch(`${API_BASE_URL}/${contratoId}`, {
                    method: 'PUT',
                    body: JSON.stringify(contratoData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                }

                alert('Contrato actualizado exitosamente!');
                window.location.href = 'contratos-renta.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al actualizar contrato:', error);
                alert('Error al actualizar el contrato: ' + error.message);
            }
        });
    }

    // Load available propiedades for reference (optional enhancement)
    loadPropiedades();

    async function loadPropiedades() {
        try {
            const response = await authManager.authenticatedFetch('http://localhost:8080/api/v1/propiedades?page=0&size=100');

            if (response.ok) {
                const propiedades = await response.json();
                
                // Create datalist for propiedad suggestions
                const datalist = document.createElement('datalist');
                datalist.id = 'propiedades-list';
                
                propiedades.forEach(propiedad => {
                    const option = document.createElement('option');
                    option.value = propiedad.idPropiedad;
                    option.textContent = `${propiedad.idPropiedad} - ${propiedad.tipoPropiedad || 'N/A'} - ${propiedad.direccionCompleta || 'Sin dirección'}`;
                    datalist.appendChild(option);
                });
                
                document.body.appendChild(datalist);
                
                // Add datalist to input
                const idPropiedadInput = document.getElementById('idPropiedad');
                idPropiedadInput.setAttribute('list', 'propiedades-list');
            }
        } catch (error) {
            console.error('Error loading propiedades:', error);
        }
    }
});