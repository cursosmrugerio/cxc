document.addEventListener('DOMContentLoaded', function () {
    const apiUrl = 'http://localhost:8080/api/inmobiliarias';
    const token = localStorage.getItem('jwt_token');

    const form = document.getElementById('inmobiliaria-form');
    const tableBody = document.querySelector('#inmobiliarias-table tbody');

    function getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        };
    }

    async function fetchInmobiliarias() {
        try {
            const response = await fetch(apiUrl, { headers: getHeaders() });
            const inmobiliarias = await response.json();
            tableBody.innerHTML = '';
            inmobiliarias.forEach(inmobiliaria => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${inmobiliaria.id}</td>
                    <td>${inmobiliaria.nombre}</td>
                    <td>${inmobiliaria.direccion}</td>
                    <td>
                        <button onclick="editInmobiliaria(${inmobiliaria.id}, '${inmobiliaria.nombre}', '${inmobiliaria.direccion}')">Editar</button>
                        <button onclick="deleteInmobiliaria(${inmobiliaria.id})">Eliminar</button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error fetching inmobiliarias:', error);
        }
    }

    form.addEventListener('submit', async function (event) {
        event.preventDefault();
        const id = document.getElementById('inmobiliaria-id').value;
        const nombre = document.getElementById('nombre').value;
        const direccion = document.getElementById('direccion').value;

        const method = id ? 'PUT' : 'POST';
        const url = id ? `${apiUrl}/${id}` : apiUrl;

        try {
            await fetch(url, {
                method: method,
                headers: getHeaders(),
                body: JSON.stringify({ nombre, direccion })
            });
            form.reset();
            fetchInmobiliarias();
        } catch (error) {
            console.error('Error saving inmobiliaria:', error);
        }
    });

    window.editInmobiliaria = function (id, nombre, direccion) {
        document.getElementById('inmobiliaria-id').value = id;
        document.getElementById('nombre').value = nombre;
        document.getElementById('direccion').value = direccion;
    }

    window.deleteInmobiliaria = async function (id) {
        if (confirm('¿Estás seguro de que quieres eliminar esta inmobiliaria?')) {
            try {
                await fetch(`${apiUrl}/${id}`, {
                    method: 'DELETE',
                    headers: getHeaders()
                });
                fetchInmobiliarias();
            } catch (error) {
                console.error('Error deleting inmobiliaria:', error);
            }
        }
    }

    fetchInmobiliarias();
});