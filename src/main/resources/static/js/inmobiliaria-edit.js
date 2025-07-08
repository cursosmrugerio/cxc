document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/inmobiliarias';
    const inmobiliariaEditForm = document.getElementById('inmobiliariaEditForm');
    
    // Check authentication first
    if (!authManager.isAuthenticated()) {
        alert('No autorizado. Por favor, inicie sesión.');
        window.location.href = 'login.html';
        return;
    }
    
    // Get inmobiliaria ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const inmobiliariaId = urlParams.get('id');

    // Function to load inmobiliaria data
    const loadInmobiliariaData = async () => {
        if (!inmobiliariaId) {
            alert('ID de inmobiliaria no encontrado.');
            window.location.href = 'inmobiliaria.html';
            return;
        }

        try {
            const response = await authManager.authenticatedFetch(`${API_BASE_URL}/${inmobiliariaId}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const inmobiliaria = await response.json();
            
            // Populate form fields
            document.getElementById('nombreComercial').value = inmobiliaria.nombreComercial || '';
            document.getElementById('razonSocial').value = inmobiliaria.razonSocial || '';
            document.getElementById('rfcNit').value = inmobiliaria.rfcNit || '';
            document.getElementById('telefonoPrincipal').value = inmobiliaria.telefonoPrincipal || '';
            document.getElementById('emailContacto').value = inmobiliaria.emailContacto || '';
            document.getElementById('direccionCompleta').value = inmobiliaria.direccionCompleta || '';
            document.getElementById('ciudad').value = inmobiliaria.ciudad || '';
            document.getElementById('estado').value = inmobiliaria.estado || '';
            document.getElementById('codigoPostal').value = inmobiliaria.codigoPostal || '';
            document.getElementById('personaContacto').value = inmobiliaria.personaContacto || '';
            document.getElementById('estatus').value = inmobiliaria.estatus || 'ACTIVE';

        } catch (error) {
            console.error('Error loading inmobiliaria data:', error);
            alert('Error al cargar los datos de la inmobiliaria.');
            window.location.href = 'inmobiliaria.html';
        }
    };

    // Load data when page loads
    loadInmobiliariaData();

    // Handle form submission
    if (inmobiliariaEditForm) {
        inmobiliariaEditForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const inmobiliariaData = {
                nombreComercial: document.getElementById('nombreComercial').value,
                razonSocial: document.getElementById('razonSocial').value,
                rfcNit: document.getElementById('rfcNit').value,
                telefonoPrincipal: document.getElementById('telefonoPrincipal').value,
                emailContacto: document.getElementById('emailContacto').value,
                direccionCompleta: document.getElementById('direccionCompleta').value,
                ciudad: document.getElementById('ciudad').value,
                estado: document.getElementById('estado').value,
                codigoPostal: document.getElementById('codigoPostal').value,
                personaContacto: document.getElementById('personaContacto').value,
                estatus: document.getElementById('estatus').value
            };

            try {
                const response = await authManager.authenticatedFetch(`${API_BASE_URL}/${inmobiliariaId}`, {
                    method: 'PUT',
                    body: JSON.stringify(inmobiliariaData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                }

                alert('Inmobiliaria actualizada exitosamente!');
                window.location.href = 'inmobiliaria.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al actualizar inmobiliaria:', error);
                alert('Error al actualizar la inmobiliaria: ' + error.message);
            }
        });
    }
});