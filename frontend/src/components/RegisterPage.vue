<template>
    <div class="container mt-5">
        <h1 class="text-center mb-4">Register</h1>
        <div class="row justify-content-center">
            <div class="col-md-4">
                <form @submit.prevent="register" class="border p-4 rounded shadow-sm">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input 
                            type="text" 
                            v-model="username" 
                            id="username" 
                            class="form-control" 
                            placeholder="Username" 
                            required 
                        />
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input 
                            type="password" 
                            v-model="password" 
                            id="password" 
                            class="form-control" 
                            placeholder="Password" 
                            required 
                        />
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Register</button>
                </form>
                <div class="text-center mt-3">
                    <router-link to="/login">Already have an account? Login</router-link>
                </div>
                <div v-if="errorMessage" class="alert alert-danger mt-3" role="alert">
                    {{ errorMessage }}
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            username: '',
            password: '',
            errorMessage: '',
        };
    },
    methods: {
        async register() {
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
                // Ожидаем ответа от сервера
                const response = await axios.post('http://localhost:7777/auth/sign-up', {
                    username: this.username,
                    password: this.password,
                });

                // Проверяем успешность регистрации
                if (response.status === 200) {
                    this.$router.push('/login'); // Перенаправление на страницу логина
                }
            } catch (error) {
                // Обработка ошибки ответа сервера
                if (error.response) {
                    this.errorMessage = error.response.data.message || 'Registration failed! Please try again.'; // Отображаем сообщение с ошибкой
                } else {
                    this.errorMessage = 'Registration failed! Please check your network connection.';
                }
            }
        },
    },
};
</script>
