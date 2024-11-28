<template>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Admin Panel</h1>

        <router-link to="/vehicles" class="btn btn-primary ms-3">Home</router-link>
        <!-- Сообщение об отсутствии прав -->
        <div v-if="!isAdmin" class="alert alert-danger text-center">
            You do not have access to this page.
        </div>

        <!-- Таблица с пользователями -->
        <div v-else>
            <table class="table">
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="user in users" :key="user.id">
                        <td>{{ user.username }}</td>
                        <td>{{ user.role }}</td>
                        <td>
                            <button v-if="user.role !== 'ROLE_ADMIN' && user.username !== currentUsername" @click="grantAdmin(user.id)" class="btn btn-primary">Grant Admin</button>
                            <button v-if="user.role === 'ROLE_ADMIN' && user.username !== currentUsername" @click="revokeAdmin(user.id)" class="btn btn-danger">Revoke Admin</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            users: [],
            isAdmin: false,
            currentUsername: JSON.parse(localStorage.getItem('username')),
        };
    },
    created() {
        this.checkAdminAccess();
    },
    methods: {
        checkAdminAccess() {
            const role = JSON.parse(localStorage.getItem('role'));
            console.log(role);
            if (role === "ROLE_ADMIN") {
                this.isAdmin = true;
                this.fetchUsers();
            } else {
                this.isAdmin = false;
                setTimeout(() => {
                    this.$router.push('/vehicles'); 
                }, 2000);
                   

            }
        },
        
        async fetchUsers() {
            try {
                const response = await axios.get('http://localhost:7777/api/admin/users', {
                    withCredentials: true,
                });
                this.users = response.data;
            } catch (error) {
                console.error('Error fetching users', error);
            }
        },
        
        async grantAdmin(userId) {
            try {
                await axios.put(`http://localhost:7777/api/admin/users/${userId}/grant-admin`, null, {
                    withCredentials: true,
                });
                this.fetchUsers();
            } catch (error) {
                console.error('Error granting admin rights', error);
            }
        },

        async revokeAdmin(userId) {
            try {
                await axios.put(`http://localhost:7777/api/admin/users/${userId}/revoke-admin`, null, {
                    withCredentials: true,
                });
                this.fetchUsers();
            } catch (error) {
                console.error('Error revoking admin rights', error);
            }
        },
    },
};
</script>

<style scoped>
.container {
    max-width: 800px;
}
</style>
