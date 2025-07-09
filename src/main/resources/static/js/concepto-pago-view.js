document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/conceptos-pago';
    const INMOBILIARIAS_API_URL = 'http://localhost:8080/api/v1/inmobiliarias';
    
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
    
    // Initial authentication check
    if (!checkAuth()) {
        return;
    }
    
    // Get concepto pago ID from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const conceptoPagoId = urlParams.get('id');

    // Function to load inmobiliarias mapping
    const loadInmobiliarias = async () => {
        const token = getAuthToken();
        if (!token) return {};

        try {
            const response = await fetch(`${INMOBILIARIAS_API_URL}?page=0&size=1000`, {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                const inmobiliarias = data.content || data;
                
                // Create mapping from id to name
                const inmobiliariaMap = {};
                inmobiliarias.forEach(inmobiliaria => {
                    inmobiliariaMap[inmobiliaria.idInmobiliaria] = inmobiliaria.nombreComercial;
                });
                
                return inmobiliariaMap;
            }
            return {};
        } catch (error) {
            console.error('Error loading inmobiliarias:', error);
            return {};
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
            // Load inmobiliarias mapping
            const inmobiliariaMap = await loadInmobiliarias();
            
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
            
            // Populate form fields with read-only data
            document.getElementById('nombreConcepto').value = conceptoPago.nombreConcepto || '';
            document.getElementById('tipoConcepto').value = conceptoPago.tipoConcepto || '';
            document.getElementById('descripcion').value = conceptoPago.descripcion || '';
            document.getElementById('permiteRecargos').value = conceptoPago.permiteRecargos ? 'SÍ' : 'NO';
            document.getElementById('activo').value = conceptoPago.activo ? 'ACTIVO' : 'INACTIVO';
            
            // Set the inmobiliaria name
            const inmobiliariaNombre = inmobiliariaMap[conceptoPago.idInmobiliaria] || 'No encontrada';
            document.getElementById('idInmobiliaria').value = inmobiliariaNombre;

        } catch (error) {
            console.error('Error loading concepto pago data:', error);
            alert('Error al cargar los datos del concepto de pago.');
            window.location.href = 'conceptosPago.html';
        }
    };

    // Load data when page loads
    loadConceptoPagoData();
});