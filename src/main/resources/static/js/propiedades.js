document.addEventListener('DOMContentLoaded', function () {
    // Check authentication first - exit immediately if not authenticated
    if (!authManager.isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }

    const userData = authManager.getUserData();
    if (userData) {
        document.getElementById('user-info').textContent = `Welcome, ${userData.username}`;
    }

    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Logout button clicked on propiedades page');
            authManager.logout();
        });
    } else {
        console.error('Logout button not found on propiedades page');
    }

    const apiUrl = '/api/v1/propiedades';
    const inmobiliariaApiUrl = '/api/v1/inmobiliarias';
    const form = document.getElementById('propiedad-form');
    const tableBody = document.getElementById('propiedad-table-body');
    const cancelButton = document.getElementById('cancel-edit');
    const inmobiliariaSelect = document.getElementById('id-inmobiliaria');

    async function loadInmobiliarias() {
        try {
            const response = await authManager.authenticatedFetch(inmobiliariaApiUrl);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const inmobiliarias = await response.json();
            inmobiliariaSelect.innerHTML = '<option value="">Select Inmobiliaria</option>';
            inmobiliarias.forEach(inmo => {
                const option = `<option value="${inmo.id}">${inmo.nombreComercial}</option>`;
                inmobiliariaSelect.innerHTML += option;
            });
        } catch (error) {
            console.error("Failed to load inmobiliarias:", error);
        }
    }

    async function loadTable() {
        try {
            const response = await authManager.authenticatedFetch(apiUrl);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const propiedades = await response.json();

            tableBody.innerHTML = '';
            propiedades.forEach(prop => {
                const row = `
                    <tr>
                        <td>${prop.id}</td>
                        <td>${prop.direccionCompleta}</td>
                        <td>${prop.tipoPropiedad}</td>
                        <td>${prop.estatusPropiedad}</td>
                        <td>
                            <button class="btn btn-sm btn-warning edit-btn" data-id="${prop.id}">Edit</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${prop.id}">Delete</button>
                        </td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
        } catch (error) {
            console.error("Failed to load data:", error);
        }
    }

    async function editPropiedad(id) {
        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const prop = await response.json();

            document.getElementById('propiedad-id').value = prop.id;
            document.getElementById('id-inmobiliaria').value = prop.idInmobiliaria;
            document.getElementById('direccion-completa').value = prop.direccionCompleta;
            document.getElementById('tipo-propiedad').value = prop.tipoPropiedad;
            document.getElementById('superficie-total').value = prop.superficieTotal;
            document.getElementById('superficie-construida').value = prop.superficieConstruida;
            document.getElementById('numero-habitaciones').value = prop.numeroHabitaciones;
            document.getElementById('numero-banos').value = prop.numeroBanos;
            document.getElementById('caracteristicas-especiales').value = prop.caracteristicasEspeciales;
            document.getElementById('estatus-propiedad').value = prop.estatusPropiedad;

            cancelButton.style.display = 'inline-block';
            window.scrollTo(0, 0);
        } catch (error) {
            console.error(`Failed to fetch record ${id}:`, error);
        }
    }

    async function deletePropiedad(id) {
        if (!confirm(`Are you sure you want to delete record ${id}?`)) return;

        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            
            loadTable();
        } catch (error) {
            console.error(`Failed to delete record ${id}:`, error);
        }
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        const id = document.getElementById('propiedad-id').value;
        const isUpdate = !!id;

        const propData = {
            id: isUpdate ? parseInt(id) : null,
            idInmobiliaria: parseInt(document.getElementById('id-inmobiliaria').value),
            direccionCompleta: document.getElementById('direccion-completa').value,
            tipoPropiedad: document.getElementById('tipo-propiedad').value,
            superficieTotal: parseFloat(document.getElementById('superficie-total').value),
            superficieConstruida: parseFloat(document.getElementById('superficie-construida').value),
            numeroHabitaciones: parseInt(document.getElementById('numero-habitaciones').value),
            numeroBanos: parseInt(document.getElementById('numero-banos').value),
            caracteristicasEspeciales: document.getElementById('caracteristicas-especiales').value,
            fechaRegistro: new Date().toISOString().split('T')[0],
            estatusPropiedad: document.getElementById('estatus-propiedad').value
        };

        const method = isUpdate ? 'PUT' : 'POST';
        const url = isUpdate ? `${apiUrl}/${id}` : apiUrl;

        try {
            const response = await authManager.authenticatedFetch(url, {
                method: method,
                body: JSON.stringify(propData)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            form.reset();
            document.getElementById('propiedad-id').value = '';
            cancelButton.style.display = 'none';
            loadTable();
        } catch (error) {
            console.error("Failed to save data:", error);
        }
    });

    tableBody.addEventListener('click', function (event) {
        if (event.target.classList.contains('edit-btn')) {
            const id = event.target.getAttribute('data-id');
            editPropiedad(id);
        }
        if (event.target.classList.contains('delete-btn')) {
            const id = event.target.getAttribute('data-id');
            deletePropiedad(id);
        }
    });

    cancelButton.addEventListener('click', function() {
        form.reset();
        document.getElementById('propiedad-id').value = '';
        cancelButton.style.display = 'none';
    });

    // Initial load - double check authentication before making API calls
    if (authManager.isAuthenticated()) {
        loadInmobiliarias();
        loadTable();
    }
});
