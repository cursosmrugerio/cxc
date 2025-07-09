document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/configuracion-recargos';
    const configuracionRecargoEditForm = document.getElementById('configuracionRecargoEditForm');
    
    const getAuthToken = () => localStorage.getItem('jwt_token');

    const checkAuth = () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return false;
        }
        return true;
    };

    if (!checkAuth()) {
        return;
    }
    
    const urlParams = new URLSearchParams(window.location.search);
    const configuracionRecargoId = urlParams.get('id');

    const loadConfiguracionRecargoData = async () => {
        if (!configuracionRecargoId) {
            alert('ID de configuración de recargo no encontrado.');
            window.location.href = 'configuracionRecargos.html';
            return;
        }

        try {
            const token = getAuthToken();
            const response = await fetch(`${API_BASE_URL}/${configuracionRecargoId}`, {
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

            const configuracionRecargo = await response.json();
            
            document.getElementById('tipoRecargo').value = configuracionRecargo.tipoRecargo || '';
            document.getElementById('monto').value = configuracionRecargo.monto || '';
            document.getElementById('diaAplicacion').value = configuracionRecargo.diaAplicacion || '';
            document.getElementById('activo').value = String(configuracionRecargo.activo);

        } catch (error) {
            console.error('Error loading configuración de recargo data:', error);
            alert('Error al cargar los datos de la configuración de recargo.');
            window.location.href = 'configuracionRecargos.html';
        }
    };

    loadConfiguracionRecargoData();

    if (configuracionRecargoEditForm) {
        configuracionRecargoEditForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            const configuracionRecargoData = {
                tipoRecargo: document.getElementById('tipoRecargo').value.trim(),
                monto: parseFloat(document.getElementById('monto').value),
                diaAplicacion: parseInt(document.getElementById('diaAplicacion').value),
                activo: document.getElementById('activo').value === 'true'
            };

            if (!configuracionRecargoData.tipoRecargo) {
                alert('El tipo de recargo es requerido.');
                return;
            }

            if (isNaN(configuracionRecargoData.monto)) {
                alert('El monto es requerido y debe ser un número válido.');
                return;
            }

            if (isNaN(configuracionRecargoData.diaAplicacion)) {
                alert('El día de aplicación es requerido y debe ser un número válido.');
                return;
            }

            try {
                const submitButton = configuracionRecargoEditForm.querySelector('button[type="submit"]');
                const originalText = submitButton.textContent;
                submitButton.disabled = true;
                submitButton.textContent = 'Guardando...';

                const response = await fetch(`${API_BASE_URL}/${configuracionRecargoId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(configuracionRecargoData)
                });

                if (response.status === 401) {
                    alert('Sesión expirada o no autorizado. Por favor, inicie sesión nuevamente.');
                    window.location.href = 'login.html';
                    return;
                }

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || errorData.error || `HTTP error! status: ${response.status}`);
                }

                alert('Configuración de recargo actualizada exitosamente!');
                window.location.href = 'configuracionRecargos.html';
            } catch (error) {
                console.error('Error al actualizar configuración de recargo:', error);
                alert('Error al actualizar la configuración de recargo: ' + error.message);
            } finally {
                const submitButton = configuracionRecargoEditForm.querySelector('button[type="submit"]');
                submitButton.disabled = false;
                submitButton.textContent = originalText;
            }
        });
    }
});