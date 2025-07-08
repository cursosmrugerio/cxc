document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/inmobiliarias';
    const inmobiliariaAddForm = document.getElementById('inmobiliariaAddForm');

    const getAuthToken = () => localStorage.getItem('jwt_token');

    if (inmobiliariaAddForm) {
        inmobiliariaAddForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

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
                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(inmobiliariaData)
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

                alert('Inmobiliaria agregada exitosamente!');
                window.location.href = 'inmobiliaria.html'; // Redirect to the list page
            } catch (error) {
                console.error('Error al agregar inmobiliaria:', error);
                alert('Error al agregar la inmobiliaria: ' + error.message);
            }
        });
    }
});