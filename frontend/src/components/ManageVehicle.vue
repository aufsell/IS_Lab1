<template>
    <div class="vehicle-management container mt-5">
      <div class="header">
      <h1>Current User: {{ currentUsername }}</h1>
      <router-link to="/vehicles" class="btn btn-primary ms-3">Home</router-link>
    </div>
      <!-- 1. Группировка по enginePower -->
      <section class="group-by-engine-power mt-4">
      <h2>Vehicles Grouped by Engine Power</h2>
      <ul v-if="groupedByEnginePower" class="list-group mt-3">
        <li v-for="(vehicles, enginePower) in groupedByEnginePower" :key="enginePower" class="list-group-item">
          Engine Power: {{ enginePower }} — {{ vehicles.length }} vehicles
        </li>
      </ul>
    </section>
  
      <!-- 2. Подсчет объектов с fuelConsumption меньше заданного значения -->
      <section class="count-by-fuel-consumption mt-4">
        <h2>Count Vehicles with Fuel Consumption Less Than {{ fuelConsumptionValue }}</h2>
        <div class="input-group">
          <input v-model="fuelConsumptionValue" placeholder="Enter value" type="number" class="form-control" />
          <button @click="getVehiclesCountByFuelConsumption" class="btn btn-primary">Get Count</button>
        </div>
        <div v-if="vehicleCount !== null" class="mt-3">
          <p>Number of vehicles with fuel consumption less than {{ fuelConsumptionValue }}: {{ vehicleCount }}</p>
        </div>
      </section>
  
      <!-- 3. Поиск объектов по части имени -->
      <section class="search-by-name mt-4">
        <h2>Search Vehicles by Name</h2>
        <div class="input-group">
          <input v-model="searchName" placeholder="Enter name substring" class="form-control" />
          <button @click="searchVehiclesByName" class="btn btn-primary">Search</button>
        </div>
        <table v-if="searchResults && searchResults.length > 0" class="table mt-4">
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
              <th>Vehicle Type</th>
              <th>Fuel Type</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="vehicle in searchResults" :key="vehicle.id">
              <td>{{ vehicle.name }}</td>
              <td>{{ vehicle.coordinates.x }};{{ vehicle.coordinates.y }}</td>
              <td>{{ vehicle.creationDate }}</td>
              <td>{{ vehicle.enginePower }}</td>
              <td>{{ vehicle.numberOfWheels }}</td>
              <td>{{ vehicle.capacity }}</td>
              <td>{{ vehicle.distanceTravelled }}</td>
              <td>{{ vehicle.fuelConsumption }}</td>
              <td>{{ vehicle.vehicleType.name }}</td>
              <td>{{ vehicle.fuelType.name }}</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination d-flex justify-content-between mt-3" v-if="totalPages > 1">
  <button :disabled="currentPage === 1" @click="changePage(currentPage - 1)" class="btn btn-secondary">Prev</button>
  <span>Page {{ currentPage }} of {{ totalPages }}</span>
  <button :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)" class="btn btn-secondary">Next</button>
</div>
      </section>
  
      <!-- 4. Поиск транспортных средств по типу -->
      <section class="search-by-type mt-4">
  <h2>Search Vehicles by Type</h2>
  <div class="input-group">
    <select v-model="selectedVehicleType" class="form-select">
      <option v-for="type in vehicleTypes" :key="type.id" :value="type.id">{{ type.name }}</option>
    </select>
    <button @click="searchVehiclesByType" class="btn btn-primary">Search</button>
  </div>
  
  <table v-if="vehicleTypeResults.length > 0" class="table mt-4">
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
        <th>Vehicle Type</th>
        <th>Fuel Type</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="vehicle in vehicleTypeResults" :key="vehicle.id">
        <td>{{ vehicle.name }}</td>
        <td>{{ vehicle.coordinates.x }};{{ vehicle.coordinates.y }}</td>
        <td>{{ vehicle.creationDate }}</td>
        <td>{{ vehicle.enginePower }}</td>
        <td>{{ vehicle.numberOfWheels }}</td>
        <td>{{ vehicle.capacity }}</td>
        <td>{{ vehicle.distanceTravelled }}</td>
        <td>{{ vehicle.fuelConsumption }}</td>
        <td>{{ vehicle.vehicleType.name }}</td>
        <td>{{ vehicle.fuelType.name }}</td>
      </tr>
    </tbody>
  </table>
  <h3 v-else-if="isSearchTriggered">Nothing</h3>
</section>
  
      <!-- 5. Добавить колеса к транспортному средству -->
      <section class="add-wheels mt-4">
        <h2>Add Wheels to Vehicle</h2>
        <div class="input-group">
          <select v-model="vehicleIdForWheels" class="form-select">
            <option v-for="vehicle in existingVehicles" :key="vehicle.id" :value="vehicle.vehicleId">
              {{ vehicle.name }} (id: {{ vehicle.vehicleId }}) ({{ vehicle.numberOfWheels }} wheels)
            </option>
          </select>
          <input v-model="wheelsToAdd" placeholder="Enter number of wheels" type="number" class="form-control" />
          <button @click="addWheelsToVehicle" class="btn btn-success">Add Wheels</button>
        </div>
        <p v-if="addWheelsResponse" class="mt-3 text-success">{{ addWheelsResponse }}</p>
      </section>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        groupedByEnginePower: null,
        fuelConsumptionValue: 0,
        vehicleCount: null,
        searchName: '',
        searchResults: [],
        currentPage: 1,
        totalPages: 0,
        selectedVehicleType: null,
        vehicleTypes: [],
        vehicleTypeResults: [],
        vehicleIdForWheels: null,
        wheelsToAdd: 0,
        addWheelsResponse: '',
        existingVehicles: [],
        visibleGroups: {},
        currentUsername:null,
        isSearchTriggered: false,
      };
    },
    created() {
      this.fetchVehicleTypes();
      this.getGroupedByEnginePower();
      this.getExistingVehicles();
      this.currentUsername=JSON.parse(localStorage.getItem('username'));
    },
    methods: {
    isVisible(enginePower) {
      return !!this.visibleGroups[enginePower];
    },
    
    toggleVisibility(enginePower) {
      this.visibleGroups[enginePower] = !this.isVisible(enginePower);
    },
      async fetchVehicleTypes() {
        try {
          const response = await axios.get('http://localhost:7777/api/vehicles/vehicleTypes', {
            withCredentials: true,
          });
          this.vehicleTypes = response.data;
        } catch (error) {
          console.error('Error fetching vehicle types', error);
        }
      },
  
      async getExistingVehicles() {
  try {
    let page = 0;
    const vehicles = [];
    let totalPages;

    do {
      const response = await axios.get(`http://localhost:7777/api/vehicles?page=${page}&size=50`, {
        withCredentials: true,
      });
      
      vehicles.push(...response.data.content);
      totalPages = response.data.totalPages;
      page++;
    } while (page < totalPages);

    this.existingVehicles = vehicles;
    console.log('All existing vehicles:', vehicles);
  } catch (error) {
    console.error('Error fetching all existing vehicles', error);
  }
},
  
      async getGroupedByEnginePower() {
        try {
          const response = await axios.get('http://localhost:7777/api/vehicles/group-by-engine-power', {
            withCredentials: true,
          });
          this.groupedByEnginePower = response.data;
        } catch (error) {
          console.error('Error fetching grouped vehicles', error);
        }
      },
      async getVehiclesCountByFuelConsumption() {
        try {
          const response = await axios.get(
            `http://localhost:7777/api/vehicles/count-by-fuel-consumption?value=${this.fuelConsumptionValue}`,
            {
              withCredentials: true,
            }
          );
          this.vehicleCount = response.data;
        } catch (error) {
          console.error('Error fetching vehicle count by fuel consumption', error);
        }
      },
  
      async searchVehiclesByName() {
  try {
    const response = await axios.get(
      `http://localhost:7777/api/vehicles/search`, 
      {
        withCredentials: true,
        params: {
          nameSubstring: this.searchName,
          page: this.currentPage - 1,  // текущая страница для сервера
          size: 10  // количество объектов на страницу
        }
      }
    );
    
    // Настройка данных для пагинации
    this.searchResults = response.data.content; // Содержимое текущей страницы
    this.totalPages = response.data.totalPages; // Общее количество страниц
    
  } catch (error) {
    console.error('Error searching vehicles by name', error);
  }
},
  
      async searchVehiclesByType() {
        this.isSearchTriggered = true;
        try {
           const response = await axios.get(
            `http://localhost:7777/api/vehicles/type`,
            {
              withCredentials: true,
              params: { vehicleType: this.selectedVehicleType },
            }
          );
          this.vehicleTypeResults=response.data;
          console.log(response.data);
        } catch (error) {
          console.error('Error searching vehicles by type', error);
        }
      },
  
      async addWheelsToVehicle() {
        try {
          await axios.patch(
            `http://localhost:7777/api/vehicles/${this.vehicleIdForWheels}/add-wheels?count=${parseInt(this.wheelsToAdd)}`,
            console.log(this.wheelsToAdd)
,
           {
            withCredentials: true,
            }
          );
          this.addWheelsResponse = `Added ${this.wheelsToAdd} wheels to vehicle ${this.vehicleIdForWheels}`;
        } catch (error) {
          console.error('Error adding wheels to vehicle', error);
        }
      },
  
      changePage(page) {
  this.currentPage = page;
  this.searchVehiclesByName();
}
    }
  };
  </script>

<style>
.vehicle-management{
  margin-bottom: 15vh;
}
</style>