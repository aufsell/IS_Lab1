import { createApp } from 'vue'
import App from './App.vue'

import { createRouter, createWebHistory } from 'vue-router'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import AdminPage from './components/AdminPage.vue'
import LoginPage from './components/LoginPage.vue'
import RegisterPage from './components/RegisterPage.vue'
import VehiclePage from './components/VehiclePage.vue'
import NewVehicleForm from './components/NewVehicleForm.vue'
import ManageVehicle from './components/ManageVehicle.vue'
import AuditLogTable from './components/AuditLogTable.vue'


const routes = [
  { path: '/', component: LoginPage },
  { path: '/admin', component: AdminPage, meta: { requiresAuth: true , requiredAdmin: true} },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: '/vehicles', component: VehiclePage },
  { path: '/new-vehicle', component: NewVehicleForm },
  { path: '/edit-vehicle/:vehicleId', component: NewVehicleForm , props: true, name: 'edit-vehicle'},
  { path: '/manage-vehicles', component: ManageVehicle},
  {path: '/audit-logs', component: AuditLogTable},
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

createApp(App).use(router).mount('#app')
