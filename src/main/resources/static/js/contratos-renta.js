document.addEventListener('DOMContentLoaded', function () {
    if (!requireAuth()) {
        return;
    }

    const userData = authManager.getUserData();
    if (userData) {
        document.getElementById('user-info').textContent = `Welcome, ${userData.username}`;
    }

    document.getElementById('logout-btn').addEventListener('click', function() {
        authManager.logout();
    });

    const apiUrl = '/api/v1/contratos-renta';
    const propiedadApiUrl = '/api/v1/propiedades';
    const form = document.getElementById('contrato-renta-form');
    const tableBody = document.getElementById('contrato-renta-table-body');
    const cancelButton = document.getElementById('cancel-edit');
    const propiedadSelect = document.getElementById('id-propiedad');

    async function loadPropiedades() {
        try {
            const response = await authManager.authenticatedFetch(propiedadApiUrl);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const propiedades = await response.json();
            propiedadSelect.innerHTML = '<option value="">Select Propiedad</option>';
            propiedades.forEach(prop => {
                const option = `<option value="${prop.id}">${prop.direccionCompleta}</option>`;
                propiedadSelect.innerHTML += option;
            });
        } catch (error) {
            console.error("Failed to load propiedades:", error);
        }
    }

    async function loadTable() {
        try {
            const response = await authManager.authenticatedFetch(apiUrl);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const contratos = await response.json();

            tableBody.innerHTML = '';
            for (const contrato of contratos) {
                const propResponse = await authManager.authenticatedFetch(`${propiedadApiUrl}/${contrato.idPropiedad}`);
                const prop = await propResponse.json();
                const row = `
                    <tr>
                        <td>${contrato.idContrato}</td>
                        <td>${prop.direccionCompleta}</td>
                        <td>${new Date(contrato.fechaInicioContrato).toLocaleDateString()}</td>
                        <td>${new Date(contrato.fechaFinContrato).toLocaleDateString()}</td>
                        <td>${contrato.estatusContrato}</td>
                        <td>
                            <button class="btn btn-sm btn-warning edit-btn" data-id="${contrato.idContrato}">Edit</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${contrato.idContrato}">Delete</button>
                        </td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            }
        } catch (error) {
            console.error("Failed to load data:", error);
        }
    }

    async function editContrato(id) {
        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const contrato = await response.json();

            document.getElementById('contrato-id').value = contrato.idContrato;
            document.getElementById('id-propiedad').value = contrato.idPropiedad;
            document.getElementById('fecha-inicio-contrato').value = new Date(contrato.fechaInicioContrato).toISOString().split('T')[0];
            document.getElementById('fecha-fin-contrato').value = new Date(contrato.fechaFinContrato).toISOString().split('T')[0];
            document.getElementById('duracion-meses').value = contrato.duracionMeses;
            document.getElementById('deposito-garantia').value = contrato.depositoGarantia;
            document.getElementById('condiciones-especiales').value = contrato.condicionesEspeciales;
            document.getElementById('notificacion-dias-previos').value = contrato.notificacionDiasPrevios;
            document.getElementById('email-notificaciones').value = contrato.emailNotificaciones;
            document.getElementById('telefono-notificaciones').value = contrato.telefonoNotificaciones;
            document.getElementById('estatus-contrato').value = contrato.estatusContrato;

            cancelButton.style.display = 'inline-block';
            window.scrollTo(0, 0);
        } catch (error) {
            console.error(`Failed to fetch record ${id}:`, error);
        }
    }

    async function deleteContrato(id) {
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

        const id = document.getElementById('contrato-id').value;
        const isUpdate = !!id;

        const contratoData = {
            idContrato: isUpdate ? parseInt(id) : null,
            idPropiedad: parseInt(document.getElementById('id-propiedad').value),
            fechaInicioContrato: document.getElementById('fecha-inicio-contrato').value,
            fechaFinContrato: document.getElementById('fecha-fin-contrato').value,
            duracionMeses: parseInt(document.getElementById('duracion-meses').value),
            depositoGarantia: parseFloat(document.getElementById('deposito-garantia').value),
            condicionesEspeciales: document.getElementById('condiciones-especiales').value,
            notificacionDiasPrevios: parseInt(document.getElementById('notificacion-dias-previos').value),
            emailNotificaciones: document.getElementById('email-notificaciones').value,
            telefonoNotificaciones: document.getElementById('telefono-notificaciones').value,
            estatusContrato: document.getElementById('estatus-contrato').value
        };

        const method = isUpdate ? 'PUT' : 'POST';
        const url = isUpdate ? `${apiUrl}/${id}` : apiUrl;

        try {
            const response = await authManager.authenticatedFetch(url, {
                method: method,
                body: JSON.stringify(contratoData)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            form.reset();
            document.getElementById('contrato-id').value = '';
            cancelButton.style.display = 'none';
            loadTable();
        } catch (error) {
            console.error("Failed to save data:", error);
        }
    });

    tableBody.addEventListener('click', function (event) {
        if (event.target.classList.contains('edit-btn')) {
            const id = event.target.getAttribute('data-id');
            editContrato(id);
        }
        if (event.target.classList.contains('delete-btn')) {
            const id = event.target.getAttribute('data-id');
            deleteContrato(id);
        }
    });

    cancelButton.addEventListener('click', function() {
        form.reset();
        document.getElementById('contrato-id').value = '';
        cancelButton.style.display = 'none';
    });

    loadPropiedades();
    loadTable();
});
