document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/propiedades';
    const propiedadEditForm = document.getElementById('propiedadEditForm');
    
    // Check authentication first
    if (!authManager.isAuthenticated()) {
        alert('No autorizado. Por favor, inicie sesión.');
        window.location.href = 'login.html';
        return;
    }
    
    // Get propiedad ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const propiedadId = urlParams.get('id');

    // Function to load propiedad data
    const loadPropiedadData = async () => {
        if (!propiedadId) {
            alert('ID de propiedad no encontrado.');
            window.location.href = 'propiedades.html';
            return;
        }

        try {
            const response = await authManager.authenticatedFetch(`${API_BASE_URL}/${propiedadId}`);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const propiedad = await response.json();
            
            // Populate form fields
            document.getElementById('idInmobiliaria').value = propiedad.idInmobiliaria || '';
            document.getElementById('tipoPropiedad').value = propiedad.tipoPropiedad || '';
            document.getElementById('direccionCompleta').value = propiedad.direccionCompleta || '';
            document.getElementById('superficieTotal').value = propiedad.superficieTotal || '';
            document.getElementById('superficieConstruida').value = propiedad.superficieConstruida || '';
            document.getElementById('numeroHabitaciones').value = propiedad.numeroHabitaciones || '';
            document.getElementById('numeroBanos').value = propiedad.numeroBanos || '';
            document.getElementById('estatusPropiedad').value = propiedad.estatusPropiedad || 'DISPONIBLE';
            document.getElementById('caracteristicasEspeciales').value = propiedad.caracteristicasEspeciales || '';

        } catch (error) {
            console.error('Error loading propiedad data:', error);
            alert('Error al cargar los datos de la propiedad.');
            window.location.href = 'propiedades.html';
        }
    };

    // Load data when page loads
    loadPropiedadData();

    // Handle form submission
    if (propiedadEditForm) {
        propiedadEditForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            // Validate required fields
            const idInmobiliaria = document.getElementById('idInmobiliaria').value;
            if (!idInmobiliaria) {
                alert('El ID de Inmobiliaria es requerido.');
                return;
            }

            const propiedadData = {
                tipoPropiedad: document.getElementById('tipoPropiedad').value || null,
                direccionCompleta: document.getElementById('direccionCompleta').value || null,
                superficieTotal: parseFloat(document.getElementById('superficieTotal').value) || null,
                superficieConstruida: parseFloat(document.getElementById('superficieConstruida').value) || null,
                numeroHabitaciones: parseInt(document.getElementById('numeroHabitaciones').value) || null,
                numeroBanos: parseInt(document.getElementById('numeroBanos').value) || null,
                estatusPropiedad: document.getElementById('estatusPropiedad').value || 'DISPONIBLE',
                caracteristicasEspeciales: document.getElementById('caracteristicasEspeciales').value || null
            };

            try {
                const response = await authManager.authenticatedFetch(`${API_BASE_URL}/${propiedadId}`, {
                    method: 'PUT',
                    body: JSON.stringify(propiedadData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.error || `HTTP error! status: ${response.status}`);
                }

                alert('Propiedad actualizada exitosamente!');
                window.location.href = 'propiedades.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al actualizar propiedad:', error);
                alert('Error al actualizar la propiedad: ' + error.message);
            }
        });
    }

    // Load available inmobiliarias for reference (optional enhancement)
    loadInmobiliarias();

    async function loadInmobiliarias() {
        try {
            const response = await authManager.authenticatedFetch('http://localhost:8080/api/v1/inmobiliarias?page=0&size=100');

            if (response.ok) {
                const data = await response.json();
                const inmobiliarias = data.content || data;
                
                // Create datalist for inmobiliaria suggestions
                const datalist = document.createElement('datalist');
                datalist.id = 'inmobiliarias-list';
                
                inmobiliarias.forEach(inmobiliaria => {
                    const option = document.createElement('option');
                    option.value = inmobiliaria.idInmobiliaria;
                    option.textContent = `${inmobiliaria.idInmobiliaria} - ${inmobiliaria.nombreComercial}`;
                    datalist.appendChild(option);
                });
                
                document.body.appendChild(datalist);
                
                // Add datalist to input
                const idInmobiliariaInput = document.getElementById('idInmobiliaria');
                idInmobiliariaInput.setAttribute('list', 'inmobiliarias-list');
            }
        } catch (error) {
            console.error('Error loading inmobiliarias:', error);
        }
    }
});