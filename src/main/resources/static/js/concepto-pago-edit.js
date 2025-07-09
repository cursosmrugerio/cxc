document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/conceptos-pago';
    const INMOBILIARIAS_API_URL = 'http://localhost:8080/api/v1/inmobiliarias';
    const conceptoPagoEditForm = document.getElementById('conceptoPagoEditForm');
    const inmobiliariaSelect = document.getElementById('idInmobiliaria');
    
    const getAuthToken = () => localStorage.getItem('jwt_token');
    
    // Check authentication first
    const checkAuth = () => {
        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return false;
        }
        return true;
    };
    
    // Check if user has admin role
    const checkAdminRole = () => {
        if (!authManager.isAdmin()) {
            alert('No tiene permisos para editar conceptos de pago. Se requiere rol de ADMIN.');
            window.location.href = 'conceptosPago.html';
            return false;
        }
        return true;
    };
    
    // Initial authentication check
    if (!checkAuth()) {
        return;
    }
    
    // Check admin role
    if (!checkAdminRole()) {
        return;
    }
    
    // Get concepto pago ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const conceptoPagoId = urlParams.get('id');

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

    // Function to load concepto pago data
    const loadConceptoPagoData = async () => {
        if (!conceptoPagoId) {
            alert('ID de concepto de pago no encontrado.');
            window.location.href = 'conceptosPago.html';
            return;
        }

        const token = getAuthToken();
        if (!token) {
            alert('No autorizado. Por favor, inicie sesión.');
            window.location.href = 'login.html';
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/${conceptoPagoId}`, {
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

            const conceptoPago = await response.json();
            
            // Populate form fields
            document.getElementById('nombreConcepto').value = conceptoPago.nombreConcepto || '';
            document.getElementById('tipoConcepto').value = conceptoPago.tipoConcepto || '';
            document.getElementById('descripcion').value = conceptoPago.descripcion || '';
            document.getElementById('permiteRecargos').value = conceptoPago.permiteRecargos ? 'true' : 'false';
            document.getElementById('activo').value = conceptoPago.activo ? 'true' : 'false';
            
            // Load inmobiliarias first, then set the selected value
            await loadInmobiliarias();
            document.getElementById('idInmobiliaria').value = conceptoPago.idInmobiliaria || '';
            
            // Set the inmobiliaria name for display (since it's disabled)
            const inmobiliariaSelect = document.getElementById('idInmobiliaria');
            const selectedOption = inmobiliariaSelect.options[inmobiliariaSelect.selectedIndex];
            if (selectedOption) {
                selectedOption.selected = true;
            }

        } catch (error) {
            console.error('Error loading concepto pago data:', error);
            alert('Error al cargar los datos del concepto de pago.');
            window.location.href = 'conceptosPago.html';
        }
    };

    // Load data when page loads
    loadConceptoPagoData();

    // Handle form submission
    if (conceptoPagoEditForm) {
        conceptoPagoEditForm.addEventListener('submit', async (event) => {
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
                activo: document.getElementById('activo').value === 'true'
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

            try {
                // Show loading state
                const submitButton = conceptoPagoEditForm.querySelector('button[type="submit"]');
                const originalText = submitButton.textContent;
                submitButton.disabled = true;
                submitButton.textContent = 'Guardando...';

                const response = await fetch(`${API_BASE_URL}/${conceptoPagoId}`, {
                    method: 'PUT',
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

                alert('Concepto de pago actualizado exitosamente!');
                window.location.href = 'conceptosPago.html'; // Redirect to the list page

            } catch (error) {
                console.error('Error al actualizar concepto de pago:', error);
                alert('Error al actualizar el concepto de pago: ' + error.message);
            } finally {
                // Restore button state
                const submitButton = conceptoPagoEditForm.querySelector('button[type="submit"]');
                submitButton.disabled = false;
                submitButton.textContent = originalText;
            }
        });
    }
});