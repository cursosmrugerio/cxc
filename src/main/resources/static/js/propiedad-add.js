document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/propiedades';
    const propiedadAddForm = document.getElementById('propiedadAddForm');

    const getAuthToken = () => localStorage.getItem('jwt_token');

    if (propiedadAddForm) {
        propiedadAddForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            // Validate required fields
            const idInmobiliaria = document.getElementById('idInmobiliaria').value;
            if (!idInmobiliaria) {
                alert('El ID de Inmobiliaria es requerido.');
                return;
            }

            const propiedadData = {
                idInmobiliaria: parseInt(idInmobiliaria),
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
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(propiedadData)
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

                alert('Propiedad agregada exitosamente!');
                window.location.href = 'propiedades.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al agregar propiedad:', error);
                alert('Error al agregar la propiedad: ' + error.message);
            }
        });
    }

    // Load available inmobiliarias for selection (optional enhancement)
    loadInmobiliarias();

    async function loadInmobiliarias() {
        const token = getAuthToken();
        if (!token) return;

        try {
            const response = await fetch('http://localhost:8080/api/v1/inmobiliarias?page=0&size=100', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

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
                
                // Add placeholder with available options
                if (inmobiliarias.length > 0) {
                    idInmobiliariaInput.placeholder = `Ej: ${inmobiliarias[0].idInmobiliaria} (${inmobiliarias[0].nombreComercial})`;
                }
            }
        } catch (error) {
            console.error('Error loading inmobiliarias:', error);
        }
    }
});