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
            
            // Populate all form fields
            document.getElementById('idConfiguracionRecargo').value = configuracionRecargo.idConfiguracionRecargo || '';
            document.getElementById('tipoRecargo').value = configuracionRecargo.tipoRecargo || '';
            document.getElementById('monto').value = configuracionRecargo.monto || '';
            document.getElementById('diaAplicacion').value = configuracionRecargo.diaAplicacion || '';
            document.getElementById('activo').value = String(configuracionRecargo.activo);
            document.getElementById('idInmobiliaria').value = configuracionRecargo.idInmobiliaria || '';
            
            // Populate optional fields
            if (configuracionRecargo.activa !== null && configuracionRecargo.activa !== undefined) {
                document.getElementById('activa').value = String(configuracionRecargo.activa);
            }
            document.getElementById('aplicaAConceptos').value = configuracionRecargo.aplicaAConceptos || '';
            document.getElementById('diasCorteServicios').value = configuracionRecargo.diasCorteServicios || '';
            document.getElementById('diasGracia').value = configuracionRecargo.diasGracia || '';
            document.getElementById('montoRecargoFijo').value = configuracionRecargo.montoRecargoFijo || '';
            document.getElementById('nombrePolitica').value = configuracionRecargo.nombrePolitica || '';
            document.getElementById('porcentajeRecargoDiario').value = configuracionRecargo.porcentajeRecargoDiario || '';
            document.getElementById('recargoMaximo').value = configuracionRecargo.recargoMaximo || '';
            document.getElementById('tasaRecargoDiaria').value = configuracionRecargo.tasaRecargoDiaria || '';
            document.getElementById('tasaRecargoFija').value = configuracionRecargo.tasaRecargoFija || '';

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

            // Get all form values
            const tipoRecargo = document.getElementById('tipoRecargo').value.trim();
            const monto = document.getElementById('monto').value;
            const diaAplicacion = document.getElementById('diaAplicacion').value;
            const activo = document.getElementById('activo').value === 'true';
            const idInmobiliaria = document.getElementById('idInmobiliaria').value;
            
            // Get optional fields
            const activa = document.getElementById('activa').value;
            const aplicaAConceptos = document.getElementById('aplicaAConceptos').value;
            const diasCorteServicios = document.getElementById('diasCorteServicios').value;
            const diasGracia = document.getElementById('diasGracia').value;
            const montoRecargoFijo = document.getElementById('montoRecargoFijo').value;
            const nombrePolitica = document.getElementById('nombrePolitica').value;
            const porcentajeRecargoDiario = document.getElementById('porcentajeRecargoDiario').value;
            const recargoMaximo = document.getElementById('recargoMaximo').value;
            const tasaRecargoDiaria = document.getElementById('tasaRecargoDiaria').value;
            const tasaRecargoFija = document.getElementById('tasaRecargoFija').value;

            // Validate required fields
            if (!tipoRecargo) {
                alert('El tipo de recargo es requerido.');
                return;
            }

            if (!monto || isNaN(parseFloat(monto))) {
                alert('El monto es requerido y debe ser un número válido.');
                return;
            }

            if (!diaAplicacion || isNaN(parseInt(diaAplicacion))) {
                alert('El día de aplicación es requerido y debe ser un número válido.');
                return;
            }

            if (!idInmobiliaria) {
                alert('El ID de inmobiliaria es requerido.');
                return;
            }

            // Build configuration data object
            const configuracionRecargoData = {
                idConfiguracionRecargo: parseInt(configuracionRecargoId),
                tipoRecargo: tipoRecargo,
                monto: parseFloat(monto),
                diaAplicacion: parseInt(diaAplicacion),
                activo: activo,
                idInmobiliaria: parseInt(idInmobiliaria)
            };

            // Add optional fields if they have values
            if (activa !== '') configuracionRecargoData.activa = activa === 'true';
            if (aplicaAConceptos) configuracionRecargoData.aplicaAConceptos = aplicaAConceptos;
            if (diasCorteServicios) configuracionRecargoData.diasCorteServicios = parseInt(diasCorteServicios);
            if (diasGracia) configuracionRecargoData.diasGracia = parseInt(diasGracia);
            if (montoRecargoFijo) configuracionRecargoData.montoRecargoFijo = parseFloat(montoRecargoFijo);
            if (nombrePolitica) configuracionRecargoData.nombrePolitica = nombrePolitica;
            if (porcentajeRecargoDiario) configuracionRecargoData.porcentajeRecargoDiario = parseFloat(porcentajeRecargoDiario);
            if (recargoMaximo) configuracionRecargoData.recargoMaximo = parseFloat(recargoMaximo);
            if (tasaRecargoDiaria) configuracionRecargoData.tasaRecargoDiaria = parseFloat(tasaRecargoDiaria);
            if (tasaRecargoFija) configuracionRecargoData.tasaRecargoFija = parseFloat(tasaRecargoFija);

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