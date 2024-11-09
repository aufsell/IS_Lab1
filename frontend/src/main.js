import { createApp } from 'vue'
import App from './App.vue'

import { createMemoryHistory, createRouter } from 'vue-router'

import AdminPage from './components/AdminPage.vue'
import LoginPage from './components/LoginPage.vue'
import RegisterPage from './components/RegisterPage.vue'
import VehiclePage from './components/VehiclePage.vue'
import NewVehicleForm from './components/NewVehicleForm.vue'
import ManageVehicle from './components/ManageVehicle.vue'


const routes = [
  { path: '/', component: LoginPage },
  { path: '/admin', component: AdminPage, meta: { requiresAuth: true , requiredAdmin: true} },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: '/vehicles', component: VehiclePage },
  { path: '/new-vehicle', component: NewVehicleForm },
  { path: '/edit-vehicle/:vehicleId', component: NewVehicleForm , props: true, name: 'edit-vehicle'},
  { path: '/manage-vehicles', component: ManageVehicle},
  { path: '/', redirect: '/login' }
]

const router = createRouter({
  history: createMemoryHistory(),
  routes
})

router.beforeEach((to, from, next) => {
    const isAuthenticated = !!localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (to.meta.requiresAuth && !isAuthenticated) {
        next("/login");
    } else if (to.meta.requiresAdmin && role !== 'ROLE_ADMIN') {
        console.log(localStorage.getItem('role'));

            next("/vehicles");

    } else {
        next();
    }
});

createApp(App).use(router).mount('#app')
