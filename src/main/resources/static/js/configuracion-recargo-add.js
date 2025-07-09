document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/configuracion-recargos';
    const configuracionRecargoAddForm = document.getElementById('configuracionRecargoAddForm');

    const getAuthToken = () => localStorage.getItem('jwt_token');

    if (configuracionRecargoAddForm) {
        configuracionRecargoAddForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            const tipoRecargo = document.getElementById('tipoRecargo').value;
            const monto = document.getElementById('monto').value;
            const diaAplicacion = document.getElementById('diaAplicacion').value;
            const activo = document.getElementById('activo').value === 'true';
            const idInmobiliaria = document.getElementById('idInmobiliaria').value;

            if (!tipoRecargo || !monto || !diaAplicacion || !idInmobiliaria) {
                alert('Todos los campos son requeridos.');
                return;
            }

            const configuracionData = {
                tipoRecargo: tipoRecargo,
                monto: parseFloat(monto),
                diaAplicacion: parseInt(diaAplicacion),
                activo: activo,
                idInmobiliaria: parseInt(idInmobiliaria)
            };

            try {
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(configuracionData)
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

                alert('Configuración de recargo agregada exitosamente!');
                window.location.href = 'configuracionRecargos.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al agregar configuración de recargo:', error);
                alert('Error al agregar la configuración de recargo: ' + error.message);
            }
        });
    }

    // Load available inmobiliarias for selection (optional enhancement)
    loadInmobiliarias();

    async function loadInmobiliarias() {
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