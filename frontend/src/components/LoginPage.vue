<template>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Login</h1>
        <form @submit.prevent="login" class="border p-4 rounded shadow-sm">
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input 
                    type="text" 
                    id="username" 
                    v-model="username" 
                    class="form-control" 
                    placeholder="Enter your username" 
                    required 
                />
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input 
                    type="password" 
                    id="password" 
                    v-model="password" 
                    class="form-control" 
                    placeholder="Enter your password" 
                    required 
                />
            </div>
            <button type="submit" class="btn btn-primary w-100">Login</button>
            <div v-if="errorMessage" class="alert alert-danger mt-3" role="alert">
                {{ errorMessage }}
            </div>
        </form>
        <div class="text-center mt-3">
            <router-link to="/register" class="link-secondary">Don't have an account? Register</router-link>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import {jwtDecode} from 'jwt-decode';

export default {
    data() {
        return {
            username: '',
            password: '',
            errorMessage: '', // Переменная для сообщения об ошибке
        };
    },
    methods: {
        async login() {
            this.errorMessage = ''; // Сбрасываем сообщение об ошибке

            // Проверка длины логина и пароля
            if (this.username.length < 5) {
                this.errorMessage = 'Username must be more than 5 characters.';
                return; // Прерываем выполнение, если есть ошибка
            }
            if (this.password.length < 8) {
                this.errorMessage = 'Password must be more than 8 characters.';
                return; // Прерываем выполнение, если есть ошибка
            }

            try {
                const response = await axios.post('http://localhost:7777/auth/sign-in', {
                    username: this.username,
                    password: this.password,
                });

                const decodedToken = jwtDecode(response.data.token);
                const userRole = decodedToken.role;
                const username = decodedToken.sub;
                const userId = decodedToken.id;
                localStorage.setItem('role', JSON.stringify(userRole));
                localStorage.setItem('username', JSON.stringify(username));
                localStorage.setItem('token', JSON.stringify(response.data.token));
                localStorage.setItem('userId', JSON.stringify(userId));
                this.$router.push('/vehicles');
            } catch (error) {
                if (error.response) {
                    this.errorMessage = error.response.data.message || 'Incorrect username or password, please try again';
                } else {
                    this.errorMessage = 'Login failed! Please check your network connection.';

                }
            }
        },
    },
};
</script>

<style scoped>
.container {
    max-width: 400px;
}
</style>
