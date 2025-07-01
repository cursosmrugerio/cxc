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

    const apiUrl = '/api/v1/conceptos-pago';
    const inmobiliariaApiUrl = '/api/v1/inmobiliarias';
    const form = document.getElementById('concepto-pago-form');
    const tableBody = document.getElementById('concepto-pago-table-body');
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
            const conceptos = await response.json();

            tableBody.innerHTML = '';
            conceptos.forEach(c => {
                const row = `
                    <tr>
                        <td>${c.id}</td>
                        <td>${c.nombreConcepto}</td>
                        <td>${c.tipoConcepto}</td>
                        <td>${c.activo ? 'Yes' : 'No'}</td>
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

    async function editConcepto(id) {
        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const c = await response.json();

            document.getElementById('concepto-pago-id').value = c.id;
            document.getElementById('id-inmobiliaria').value = c.idInmobiliaria;
            document.getElementById('nombre-concepto').value = c.nombreConcepto;
            document.getElementById('descripcion').value = c.descripcion;
            document.getElementById('tipo-concepto').value = c.tipoConcepto;
            document.getElementById('permite-recargos').checked = c.permiteRecargos;
            document.getElementById('activo').checked = c.activo;

            cancelButton.style.display = 'inline-block';
            window.scrollTo(0, 0);
        } catch (error) {
            console.error(`Failed to fetch record ${id}:`, error);
        }
    }

    async function deleteConcepto(id) {
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

        const id = document.getElementById('concepto-pago-id').value;
        const isUpdate = !!id;

        const conceptoData = {
            id: isUpdate ? parseInt(id) : null,
            idInmobiliaria: parseInt(document.getElementById('id-inmobiliaria').value),
            nombreConcepto: document.getElementById('nombre-concepto').value,
            descripcion: document.getElementById('descripcion').value,
            tipoConcepto: document.getElementById('tipo-concepto').value,
            permiteRecargos: document.getElementById('permite-recargos').checked,
            activo: document.getElementById('activo').checked,
            fechaCreacion: new Date().toISOString().split('T')[0]
        };

        const method = isUpdate ? 'PUT' : 'POST';
        const url = isUpdate ? `${apiUrl}/${id}` : apiUrl;

        try {
            const response = await authManager.authenticatedFetch(url, {
                method: method,
                body: JSON.stringify(conceptoData)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            form.reset();
            document.getElementById('concepto-pago-id').value = '';
            cancelButton.style.display = 'none';
            loadTable();
        } catch (error) {
            console.error("Failed to save data:", error);
        }
    });

    tableBody.addEventListener('click', function (event) {
        if (event.target.classList.contains('edit-btn')) {
            const id = event.target.getAttribute('data-id');
            editConcepto(id);
        }
        if (event.target.classList.contains('delete-btn')) {
            const id = event.target.getAttribute('data-id');
            deleteConcepto(id);
        }
    });

    cancelButton.addEventListener('click', function() {
        form.reset();
        document.getElementById('concepto-pago-id').value = '';
        cancelButton.style.display = 'none';
    });

    loadInmobiliarias();
    loadTable();
});
