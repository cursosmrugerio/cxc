document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/conceptos-pago';
    const INMOBILIARIAS_API_URL = 'http://localhost:8080/api/v1/inmobiliarias';
    const conceptoPagoAddForm = document.getElementById('conceptoPagoAddForm');
    const inmobiliariaSelect = document.getElementById('idInmobiliaria');

    const getAuthToken = () => localStorage.getItem('jwt_token');

    // Check authentication on page load
    const checkAuth = () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return false;
        }
        return true;
    };

    // Initial authentication check
    if (!checkAuth()) {
        return;
    }

    // Function to load inmobiliarias for the select dropdown
    const loadInmobiliarias = async () => {
        const token = getAuthToken();
        if (!token) return;

        try {
            const response = await fetch(`${INMOBILIARIAS_API_URL}?page=0&size=1000`, {
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

            const data = await response.json();
            const inmobiliarias = data.content || data;
            
            // Clear existing options except the first one
            inmobiliariaSelect.innerHTML = '<option value="">Seleccione una inmobiliaria</option>';
            
            // Add inmobiliarias to select
            inmobiliarias.forEach(inmobiliaria => {
                const option = document.createElement('option');
                option.value = inmobiliaria.idInmobiliaria;
                option.textContent = inmobiliaria.nombreComercial;
                inmobiliariaSelect.appendChild(option);
            });

        } catch (error) {
            console.error('Error loading inmobiliarias:', error);
            alert('Error al cargar las inmobiliarias.');
        }
    };

    // Load inmobiliarias on page load
    loadInmobiliarias();

    // Handle form submission
    if (conceptoPagoAddForm) {
        conceptoPagoAddForm.addEventListener('submit', async (event) => {
            event.preventDefault();

            const token = getAuthToken();
            if (!token) {
                alert('No autorizado. Por favor, inicie sesión.');
                window.location.href = 'login.html';
                return;
            }

            // Get form values
            const conceptoPagoData = {
                nombreConcepto: document.getElementById('nombreConcepto').value.trim(),
                tipoConcepto: document.getElementById('tipoConcepto').value,
                descripcion: document.getElementById('descripcion').value.trim(),
                permiteRecargos: document.getElementById('permiteRecargos').value === 'true',
                activo: document.getElementById('activo').value === 'true',
                idInmobiliaria: parseInt(document.getElementById('idInmobiliaria').value)
            };

            // Validate required fields
            if (!conceptoPagoData.nombreConcepto) {
                alert('El nombre del concepto es requerido.');
                return;
            }

            if (!conceptoPagoData.tipoConcepto) {
                alert('El tipo de concepto es requerido.');
                return;
            }

            if (!conceptoPagoData.idInmobiliaria) {
                alert('Debe seleccionar una inmobiliaria.');
                return;
            }

            try {
                // Show loading state
                const submitButton = conceptoPagoAddForm.querySelector('button[type="submit"]');
                const originalText = submitButton.textContent;
                submitButton.disabled = true;
                submitButton.textContent = 'Guardando...';

                const response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify(conceptoPagoData)
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

                alert('Concepto de pago agregado exitosamente!');
                window.location.href = 'conceptosPago.html'; // Redirect to the list page

            } catch (error) {
                console.error('Error al agregar concepto de pago:', error);
                alert('Error al agregar el concepto de pago: ' + error.message);
            } finally {
                // Restore button state
                const submitButton = conceptoPagoAddForm.querySelector('button[type="submit"]');
                submitButton.disabled = false;
                submitButton.textContent = originalText;
            }
        });
    }
});