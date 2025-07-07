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

    document.getElementById('logout-btn').addEventListener('click', function() {
        authManager.logout();
    });

    const apiUrl = '/api/v1/configuracion-recargos';
    const inmobiliariaApiUrl = '/api/v1/inmobiliarias';
    const form = document.getElementById('configuracion-recargos-form');
    const tableBody = document.getElementById('configuracion-recargos-table-body');
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
            const configs = await response.json();

            tableBody.innerHTML = '';
            configs.forEach(c => {
                const row = `
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.nombrePolitica}</td>
                        <td>${c.tipoRecargo}</td>
                        <td>${c.activa ? 'Yes' : 'No'}</td>
                        <td>
                            <button class="btn btn-sm btn-warning edit-btn" data-id="${c.id}">Edit</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${c.id}">Delete</button>
                        </td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
        } catch (error) {
            console.error("Failed to load data:", error);
        }
    }

    async function editConfig(id) {
        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const c = await response.json();

            document.getElementById('configuracion-recargos-id').value = c.id;
            document.getElementById('id-inmobiliaria').value = c.idInmobiliaria;
            document.getElementById('nombre-politica').value = c.nombrePolitica;
            document.getElementById('dias-gracia').value = c.diasGracia;
            document.getElementById('tipo-recargo').value = c.tipoRecargo;
            document.getElementById('porcentaje-recargo-diario').value = c.porcentajeRecargoDiario;
            document.getElementById('monto-recargo-fijo').value = c.montoRecargoFijo;
            document.getElementById('recargo-maximo').value = c.recargoMaximo;
            document.getElementById('dias-corte-servicios').value = c.diasCorteServicios;
            document.getElementById('aplica-a-conceptos').value = c.aplicaAConceptos;
            document.getElementById('activa').checked = c.activa;

            cancelButton.style.display = 'inline-block';
            window.scrollTo(0, 0);
        } catch (error) {
            console.error(`Failed to fetch record ${id}:`, error);
        }
    }

    async function deleteConfig(id) {
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

        const id = document.getElementById('configuracion-recargos-id').value;
        const isUpdate = !!id;

        const configData = {
            id: isUpdate ? parseInt(id) : null,
            idInmobiliaria: parseInt(document.getElementById('id-inmobiliaria').value),
            nombrePolitica: document.getElementById('nombre-politica').value,
            diasGracia: parseInt(document.getElementById('dias-gracia').value),
            tipoRecargo: document.getElementById('tipo-recargo').value,
            porcentajeRecargoDiario: parseFloat(document.getElementById('porcentaje-recargo-diario').value),
            montoRecargoFijo: parseFloat(document.getElementById('monto-recargo-fijo').value),
            recargoMaximo: parseFloat(document.getElementById('recargo-maximo').value),
            diasCorteServicios: parseInt(document.getElementById('dias-corte-servicios').value),
            aplicaAConceptos: document.getElementById('aplica-a-conceptos').value,
            activa: document.getElementById('activa').checked
        };

        const method = isUpdate ? 'PUT' : 'POST';
        const url = isUpdate ? `${apiUrl}/${id}` : apiUrl;

        try {
            const response = await authManager.authenticatedFetch(url, {
                method: method,
                body: JSON.stringify(configData)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            form.reset();
            document.getElementById('configuracion-recargos-id').value = '';
            cancelButton.style.display = 'none';
            loadTable();
        } catch (error) {
            console.error("Failed to save data:", error);
        }
    });

    tableBody.addEventListener('click', function (event) {
        if (event.target.classList.contains('edit-btn')) {
            const id = event.target.getAttribute('data-id');
            editConfig(id);
        }
        if (event.target.classList.contains('delete-btn')) {
            const id = event.target.getAttribute('data-id');
            deleteConfig(id);
        }
    });

    cancelButton.addEventListener('click', function() {
        form.reset();
        document.getElementById('configuracion-recargos-id').value = '';
        cancelButton.style.display = 'none';
    });

    // Initial load - double check authentication before making API calls
    if (authManager.isAuthenticated()) {
        loadInmobiliarias();
        loadTable();
    }
});
