
document.addEventListener('DOMContentLoaded', function () {
    // Check authentication first
    if (!requireAuth()) {
        return;
    }

    // Display user info
    const userData = authManager.getUserData();
    if (userData) {
        document.getElementById('user-info').textContent = `Welcome, ${userData.username}`;
    }

    // Logout handler
    document.getElementById('logout-btn').addEventListener('click', function() {
        authManager.logout();
    });

    const apiUrl = '/api/v1/inmobiliarias';
    const form = document.getElementById('inmobiliaria-form');
    const tableBody = document.getElementById('inmobiliaria-table-body');
    const cancelButton = document.getElementById('cancel-edit');

    // Function to fetch and display all records
    async function loadTable() {
        try {
            const response = await authManager.authenticatedFetch(apiUrl);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const inmobiliarias = await response.json();

            tableBody.innerHTML = ''; // Clear existing rows
            inmobiliarias.forEach(inmo => {
                const row = `
                    <tr>
                        <td>${inmo.id}</td>
                        <td>${inmo.nombreComercial}</td>
                        <td>${inmo.razonSocial}</td>
                        <td>${inmo.rfcNit}</td>
                        <td>${inmo.estatus}</td>
                        <td>
                            <button class="btn btn-sm btn-warning edit-btn" data-id="${inmo.id}">Edit</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${inmo.id}">Delete</button>
                        </td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
        } catch (error) {
            console.error("Failed to load data:", error);
            alert("Failed to load data. See console for details.");
        }
    }

    // Function to populate form for editing
    async function editInmobiliaria(id) {
        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const inmo = await response.json();

            document.getElementById('inmobiliaria-id').value = inmo.id;
            document.getElementById('nombre-comercial').value = inmo.nombreComercial;
            document.getElementById('razon-social').value = inmo.razonSocial;
            document.getElementById('rfc-nit').value = inmo.rfcNit;
            document.getElementById('telefono-principal').value = inmo.telefonoPrincipal;
            document.getElementById('email-contacto').value = inmo.emailContacto;
            document.getElementById('direccion-completa').value = inmo.direccionCompleta;
            document.getElementById('ciudad').value = inmo.ciudad;
            document.getElementById('estado').value = inmo.estado;
            document.getElementById('codigo-postal').value = inmo.codigoPostal;
            document.getElementById('persona-contacto').value = inmo.personaContacto;
            document.getElementById('estatus').value = inmo.estatus;

            cancelButton.style.display = 'inline-block';
            window.scrollTo(0, 0); // Scroll to top to see the form
        } catch (error) {
            console.error(`Failed to fetch record ${id}:`, error);
            alert("Failed to fetch record. See console for details.");
        }
    }

    // Function to delete a record
    async function deleteInmobiliaria(id) {
        if (!confirm(`Are you sure you want to delete record ${id}?`)) return;

        try {
            const response = await authManager.authenticatedFetch(`${apiUrl}/${id}`, { method: 'DELETE' });
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            
            loadTable(); // Refresh the table
        } catch (error) {
            console.error(`Failed to delete record ${id}:`, error);
            alert("Failed to delete record. See console for details.");
        }
    }

    // Handle form submission for both create and update
    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        const id = document.getElementById('inmobiliaria-id').value;
        const isUpdate = !!id;

        const inmoData = {
            id: isUpdate ? id : null,
            nombreComercial: document.getElementById('nombre-comercial').value,
            razonSocial: document.getElementById('razon-social').value,
            rfcNit: document.getElementById('rfc-nit').value,
            telefonoPrincipal: document.getElementById('telefono-principal').value,
            emailContacto: document.getElementById('email-contacto').value,
            direccionCompleta: document.getElementById('direccion-completa').value,
            ciudad: document.getElementById('ciudad').value,
            estado: document.getElementById('estado').value,
            codigoPostal: document.getElementById('codigo-postal').value,
            personaContacto: document.getElementById('persona-contacto').value,
            fechaRegistro: new Date().toISOString().split('T')[0], // Set current date
            estatus: document.getElementById('estatus').value
        };

        const method = isUpdate ? 'PUT' : 'POST';
        const url = isUpdate ? `${apiUrl}/${id}` : apiUrl;

        try {
            const response = await authManager.authenticatedFetch(url, {
                method: method,
                body: JSON.stringify(inmoData)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            form.reset();
            document.getElementById('inmobiliaria-id').value = '';
            cancelButton.style.display = 'none';
            loadTable(); // Refresh table
        } catch (error) {
            console.error("Failed to save data:", error);
            alert("Failed to save data. See console for details.");
        }
    });

    // Handle clicks on edit and delete buttons
    tableBody.addEventListener('click', function (event) {
        if (event.target.classList.contains('edit-btn')) {
            const id = event.target.getAttribute('data-id');
            editInmobiliaria(id);
        }
        if (event.target.classList.contains('delete-btn')) {
            const id = event.target.getAttribute('data-id');
            deleteInmobiliaria(id);
        }
    });

    // Handle cancel button click
    cancelButton.addEventListener('click', function() {
        form.reset();
        document.getElementById('inmobiliaria-id').value = '';
        cancelButton.style.display = 'none';
    });

    // Initial load
    loadTable();
});
