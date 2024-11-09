<template>
  <div>
    <h1>Vehicle Page</h1>

    <button @click="logout" class="btn btn-danger">Logout</button>

    <router-link v-if="isAdmin" to="/admin" class="btn btn-primary ms-3">Admin Panel</router-link>

    <router-link to="/new-vehicle" class="btn btn-success ms-3">Add New Vehicle</router-link>
    <router-link to="/manage-vehicles" class="btn btn-primary ms-3">Manage Vehicles</router-link>
    <table class="table mt-4">
      <thead>
        <tr>
          <th>Name</th>
          <th>Coordinates</th>
          <th>Creation Date</th>
          <th>Engine Power</th>
          <th>Number of Wheels</th>
          <th>Capacity</th>
          <th>Distance Travelled</th>
          <th>Fuel Consumption</th>
          <th>Fuel Type</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="vehicle in paginatedVehicles" :key="vehicle.vehicleId">
          <td>{{ vehicle.name }}</td>
          <td>X : {{vehicle.coordinates.x }}<br>Y : {{ vehicle.coordinates.y }}</td>
          <td>{{ vehicle.creationDate }}</td>
          <td>{{ vehicle.enginePower }}</td>
          <td>{{ vehicle.numberOfWheels }}</td>
          <td>{{ vehicle.capacity }}</td>
          <td>{{ vehicle.distanceTravelled }}</td>
          <td>{{ vehicle.fuelConsumption }}</td>
          <td>{{ vehicle.fuelType.name }}</td>
          <td>
            <button v-if="vehicle.userID == currentUserId" class="btn btn-warning" @click="editVehicle(vehicle.vehicleId)">Edit</button>
            <button v-if="vehicle.userID == currentUserId" class="btn btn-danger" @click="deleteVehicle(vehicle.vehicleId)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>

    <nav aria-label="Page navigation example">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">Previous</a>
        </li>
        <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
          <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">Next</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export default {
  data() {
    return {
      vehicles: [],
      isAdmin: false,
      currentUserId: null,
      currentPage: 1,
      itemsPerPage: 8,
      stompClient: null,
    };
  },
  computed: {
    paginatedVehicles() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.vehicles.slice(start, start + this.itemsPerPage);
    },
    totalPages() {
      return Math.ceil(this.vehicles.length / this.itemsPerPage);
    },
  },
  created() {
    this.checkAdminRole();
    this.fetchVehicles();
    this.connectToWebSocket();
  },
  methods: {
    checkAdminRole() {
      const role = JSON.parse(localStorage.getItem('role'));
      if (role === 'ROLE_ADMIN') {
        this.isAdmin = true;
      }
      this.currentUserId = JSON.parse(localStorage.getItem('userId'));
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('userId');
      this.$router.push('/');
    },
    async fetchVehicles() {
      try {
        const response = await axios.get('http://localhost:7777/api/vehicles', {
          headers: {
            Authorization: `Bearer ${JSON.parse(localStorage.getItem('token'))}`,
          },
        });
        this.vehicles = response.data;
      } catch (error) {
        console.error('Error fetching vehicles', error);
      }
    },
    editVehicle(vehicleId) {
      this.$router.push({ name: 'edit-vehicle', params: { vehicleId } });
    },
    async deleteVehicle(vehicleId) {
      const userId = Number(JSON.parse(localStorage.getItem('userId')));
      if (isNaN(userId)) {
        console.error('Invalid user ID');
        return;
      }
      try {
        await axios.delete(`http://localhost:7777/api/vehicles/${vehicleId}`, {
          headers: {
            Authorization: `Bearer ${JSON.parse(localStorage.getItem('token'))}`,
          },
          data: { userId },
        });
        this.fetchVehicles();
      } catch (error) {
        console.error('Error deleting vehicle', error);
      }
    },
    changePage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
    },
    connectToWebSocket() {
      const socket = new SockJS('http://localhost:7777/ws');
      this.stompClient = new Client({
        webSocketFactory: () => socket,
        onConnect: () => {
          this.stompClient.subscribe('/topic/vehicles', (message) => {
            const update = JSON.parse(message.body);
            this.handleVehicleUpdate(update);
          });
        },
        onStompError: (error) => {
          console.error('WebSocket error:', error);
        },
      });
      this.stompClient.activate();
      
    },
    handleVehicleUpdate(update) {
      switch (update.activity) {
        case 'delete': {
          this.vehicles = this.vehicles.filter(v => v.vehicleId !== update.vehicleId);
          break;
        }
        case 'update': {
          const index = this.vehicles.findIndex(v => v.vehicleId === update.vehicleId);
          if (index !== -1) {
            this.vehicles.splice(index, 1, update.vehicle);
          }
          break;
        }
        case 'add': {
          this.vehicles.push(update.vehicle);
          break;
        }
      }
    }
  },
};
</script>

<style scoped>
.table th, .table td {
  vertical-align: middle;
}
.pagination {
  justify-content: center;
}
</style>
