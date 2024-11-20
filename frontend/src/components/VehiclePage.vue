<template>
  <div class="container">
    <div class="header">
      <h1>Current User: {{ currentUsername }}</h1>
      <router-link v-if="isAdmin" to="/admin" class="btn btn-primary ms-3">Admin Panel</router-link>
      <router-link to="/manage-vehicles" class="btn btn-primary ms-3">Manage Vehicles</router-link>
      <router-link to="/audit-logs" class="btn btn-primary ms-3">History</router-link>
      <NewVehicleForm @vehicle-added="fetchVehicles"/>
      <button @click="logout" class="btn btn-danger">Logout</button>
    </div>

    <div class="filter-container">
      <input type="text" v-model="filterText" placeholder="Filter vehicles..." class="form-control mb-4 w-25" />
    </div>

    <table class="table mt-4">
      <thead>
        <tr>
          <th>Name</th>
          <th>Coordinates</th>
          <th>Creation Date</th>
          <th>Engine Power<br><button @click="toggleSort('enginePower')" class="sort-btn">{{ getSortSymbol('enginePower') }}</button></th>
          <th>Number of Wheels <br><button @click="toggleSort('numberOfWheels')" class="sort-btn">{{ getSortSymbol('numberOfWheels') }}</button></th>
          <th>Capacity <br><br><button @click="toggleSort('capacity')" class="sort-btn">{{ getSortSymbol('capacity') }}</button></th>
          <th>Distance Travelled <br><button @click="toggleSort('distanceTravelled')" class="sort-btn">{{ getSortSymbol('distanceTravelled') }}</button></th>
          <th>Fuel Consumption <br><button @click="toggleSort('fuelConsumption')" class="sort-btn">{{ getSortSymbol('fuelConsumption') }}</button></th>
          <th>Fuel Type</th>
          <th>Vehicle Type</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="vehicle in filteredSortedVehicles" :key="vehicle?.vehicleId">
          <td>{{ vehicle.name }}</td>
          <td>X: {{ vehicle.coordinates?.x }}<br>Y: {{ vehicle.coordinates?.y }}</td>
          <td>{{ formatDate(vehicle.creationDate) }}</td>
          <td>{{ vehicle.enginePower }}</td>
          <td>{{ vehicle.numberOfWheels }}</td>
          <td>{{ vehicle.capacity }}</td>
          <td>{{ vehicle.distanceTravelled }}</td>
          <td>{{ vehicle.fuelConsumption }}</td>
          <td>{{ vehicle.fuelType?.name }}</td>
          <td>{{ vehicle.vehicleType?.name }}</td>
          <td>
            <div class="td_buttons">
              <button v-if="vehicle.userID == currentUserId" class="btn btn-outline-secondary" @click="editVehicle(vehicle.vehicleId)"><i class="bi bi-pencil-square"></i></button>
              <button v-if="vehicle.userID == currentUserId || isAdmin" class="btn btn btn-outline-dark" @click="deleteVehicle(vehicle.vehicleId)"><i class="bi bi-trash"></i></button>
            </div>
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
import { format } from 'date-fns';

import NewVehicleForm from './NewVehicleForm.vue';

export default {
  components: {
    NewVehicleForm
  },
  data() {
    return {
      vehicles: [],
      isAdmin: false,
      currentUserId: null,
      currentUsername: null,
      currentPage: 1,
      itemsPerPage: 8,
      totalItems: 0, // Add this property to track the total number of items
      filterText: '',
      sortColumn: '',
      sortOrder: 'asc', // or 'desc'
      stompClient: null,
    };
  },
  computed: {
    filteredSortedVehicles() {
      const filterText = this.filterText.toLowerCase();
      const excludedFields = [
        'enginePower', 'numberOfWheels', 'capacity', 'distanceTravelled', 'fuelConsumption', 'coordinates', 'creationDate'
      ];

      const filtered = this.vehicles.filter(vehicle =>
        Object.keys(vehicle).some(key => {
          if (excludedFields.includes(key)) {
            return false;
          }

          const value = vehicle[key];
          const stringValue = value !== null && value !== undefined ? value.toString().toLowerCase() : "";
          const regex = new RegExp(filterText, 'i');

          if (!isNaN(value)) {
            return regex.test(value.toString());
          }

          if (key === 'fuelType' || key === 'vehicleType') {
            return regex.test(value.name ? value.name.toLowerCase() : '');
          }

          return regex.test(stringValue);
        })
      );

      if (this.sortColumn) {
        const sortOrder = this.sortOrder === 'asc' ? 1 : -1;
        filtered.sort((a, b) => {
          const valueA = a[this.sortColumn];
          const valueB = b[this.sortColumn];

          if (!isNaN(valueA) && !isNaN(valueB)) {
            return (valueA - valueB) * sortOrder;
          } else {
            const stringA = valueA !== null && valueA !== undefined ? valueA.toString().toLowerCase() : "";
            const stringB = valueB !== null && valueB !== undefined ? valueB.toString().toLowerCase() : "";
            if (stringA < stringB) return -sortOrder;
            if (stringA > stringB) return sortOrder;
            return 0;
          }
        });
      }

      return filtered;
    },
    
    totalPages() {
      return Math.ceil(this.totalItems / this.itemsPerPage);
    },
  },
  created() {
    this.checkAdminRole();
    this.fetchVehicles();
    this.connectToWebSocket();
  },
  methods: {
    formatDate(date) {
      const parsedDate = new Date(date);
      if (isNaN(parsedDate)) {
        return 'Invalid Date';
      }
      return format(parsedDate, 'dd.MM.yyyy HH:mm:ss');
    },
    toggleSort(column) {
      if (this.sortColumn === column) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortColumn = column;
        this.sortOrder = 'asc';
      }
    },
    getSortSymbol(column) {
      if (this.sortColumn === column) {
        return this.sortOrder === 'asc' ? '↑' : '↓';
      }
      return '⇅';
    },
    checkAdminRole() {
      const role = JSON.parse(localStorage.getItem('role'));
      if (role === 'ROLE_ADMIN') {
        this.isAdmin = true;
      }
      this.currentUserId = JSON.parse(localStorage.getItem('userId'));
      this.currentUsername = JSON.parse(localStorage.getItem('username'));
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
      params: {
        page: this.currentPage - 1,
        size: this.itemsPerPage,
        filter: this.filterText,
        sort: `${this.sortColumn},${this.sortOrder}`
      },
      headers: {
        Authorization: `Bearer ${JSON.parse(localStorage.getItem('token'))}`,
      },
    });

    this.vehicles = response.data.content;
    this.totalItems = response.data.page.totalElements;
    
  } catch (error) {
    console.error('Error fetching vehicles:', error);
  }
},
    changePage(page) {
      this.currentPage = page;
      this.fetchVehicles();
    },
    editVehicle(id) {
      this.$router.push(`/edit-vehicle/${id}`);
    },
    async deleteVehicle(id) {
      const userId = Number(JSON.parse(localStorage.getItem('userId')));
      try {
        await axios.delete(`http://localhost:7777/api/vehicles/${id}`, {
          headers: {
            Authorization: `Bearer ${JSON.parse(localStorage.getItem('token'))}`,
          },
            data: { userId},
          
        });
        this.fetchVehicles();
      } catch (error) {
        console.error('Error deleting vehicle:', error);
      }
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
  console.log(update);
  if (update.vehicle) {
    const index = this.vehicles.findIndex((vehicle) => vehicle.vehicleId === update.vehicleId);
    
    if (index !== -1) {
      console.log('Updating vehicle');
      this.vehicles.splice(index, 1, update.vehicle);
    } else {
      console.log('Adding new vehicle');
      this.vehicles.push(update.vehicle);
    }
  } else {
    console.log('Vehicle to update is null, removing from array');
    this.vehicles = this.vehicles.filter((vehicle) => vehicle.vehicleId !== update.vehicleId);
  }
}
  },
};
</script>

<style>
.header{
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-container{
  margin-top: 5vh;
}

.td_buttons{
  display: flex;
  justify-content: space-around;
  gap: 10px
}
thead th {
  text-align: top;
  vertical-align: top;
}
</style>