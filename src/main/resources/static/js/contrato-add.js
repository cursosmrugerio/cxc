document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/contratos-renta';
    const contratoAddForm = document.getElementById('contratoAddForm');

    const getAuthToken = () => localStorage.getItem('jwt_token');

    if (contratoAddForm) {
        contratoAddForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

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
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(contratoData)
                });

                if (response.status === 401) {
                    alert('Sesión expirada o no autorizado. Por favor, inicie sesión nuevamente.');
                    window.location.href = 'login.html';
                    return;
                }

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                }

                alert('Contrato agregado exitosamente!');
                window.location.href = 'contratos-renta.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al agregar contrato:', error);
                alert('Error al agregar el contrato: ' + error.message);
            }
        });
    }

    // Load available propiedades for selection
    loadPropiedades();

    async function loadPropiedades() {
        const token = getAuthToken();
        if (!token) return;

        try {
            const response = await fetch('http://localhost:8080/api/v1/propiedades?page=0&size=100', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                const propiedades = data.content || data;
                
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
                
                // Add placeholder with available options
                if (propiedades.length > 0) {
                    const firstProp = propiedades[0];
                    idPropiedadInput.placeholder = `Ej: ${firstProp.idPropiedad} (${firstProp.tipoPropiedad || 'N/A'})`;
                }
            }
        } catch (error) {
            console.error('Error loading propiedades:', error);
        }
    }

    // Set default date to today
    const today = new Date();
    const formattedDate = today.toISOString().slice(0, 16); // Format for datetime-local
    document.getElementById('fechaInicioContrato').value = formattedDate;
});