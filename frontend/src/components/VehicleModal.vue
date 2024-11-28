<template>
    <div>
      <!-- Кнопка для открытия модального окна -->
      <button type="button" class="btn btn-primary" @click="openModal">Add</button>
  
      <!-- Модальное окно -->
      <div class="modal fade" id="vehicleModal" tabindex="-1" aria-labelledby="vehicleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="vehicleModalLabel">{{ isEditMode ? 'Edit Vehicle' : 'Add New Vehicle' }}</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
              <form @submit.prevent="submitForm" class="form">
                <div class="mb-3">
                  <label for="name" class="form-label">Name</label>
                  <input type="text" id="name" v-model="vehicle.name" class="form-control" required />
                  <div v-if="!vehicle.name" class="text-danger">Name must not be null</div>
                </div>
  
                <div class="mb-3">
                  <label for="vehicleType" class="form-label">Vehicle Type</label>
                  <select v-model="vehicle.vehicleType" id="vehicleType" class="form-select" required>
                    <option v-for="type in vehicleTypes" :key="type.id" :value="type.id">{{ type.name }}</option>
                  </select>
                </div>
  
                <div class="mb-3">
                  <label for="fuelType" class="form-label">Fuel Type</label>
                  <select v-model="vehicle.fuelType" id="fuelType" class="form-select" required>
                    <option v-for="type in fuelTypes" :key="type.id" :value="type.id">{{ type.name }}</option>
                  </select>
                </div>
  
                <div class="mb-3">
                  <label for="coordinates" class="form-label">Coordinates</label>
                  <div class="d-flex">
                    <div>
                      <label for="x" class="form-label">X</label>
                      <input type="number" id="x" v-model="coordinates.x" class="form-control" placeholder="X Coordinate" required />
                    </div>
                    <div class="ms-3">
                      <label for="y" class="form-label">Y</label>
                      <input type="number" id="y" v-model="coordinates.y" class="form-control" placeholder="Y Coordinate" required />
                    </div>
                    <button type="button" class="btn btn-secondary ms-3" @click="loadCoordinatesFromDB">Load Coordinates</button>
                  </div>
                </div>
  
                <div v-if="existingCoordinates.length > 0" class="mb-3">
                  <label for="existingCoordinates" class="form-label">Or Choose Existing Coordinates</label>
                  <select v-model="coordinatesFromDB" id="existingCoordinates" class="form-select">
                    <option v-for="coord in existingCoordinates" :key="coord.id" :value="coord.id">
                      X: {{ coord.x }}, Y: {{ coord.y }}
                    </option>
                  </select>
                </div>
  
                <div class="mb-3">
          <label for="enginePower" class="form-label">Engine Power</label>
          <input type="number" id="enginePower" v-model="vehicle.enginePower" class="form-control" placeholder="Engine Power" required min="1" />
          <div v-if="vehicle.enginePower <= 0" class="text-danger">Engine power must be greater than 0</div>
        </div>
  
        <div class="mb-3">
          <label for="numberOfWheels" class="form-label">Number of Wheels</label>
          <input type="number" id="numberOfWheels" v-model="vehicle.numberOfWheels" class="form-control" placeholder="Number of Wheels" required min="1" />
          <div v-if="vehicle.numberOfWheels <= 0" class="text-danger">Number of wheels must be greater than 0</div>
        </div>
  
        <div class="mb-3">
          <label for="capacity" class="form-label">Capacity</label>
          <input type="number" id="capacity" v-model="vehicle.capacity" class="form-control" placeholder="Capacity" required min="1" />
          <div v-if="vehicle.capacity <= 0" class="text-danger">Capacity must be greater than 0</div>
        </div>
  
        <div class="mb-3">
          <label for="distanceTravelled" class="form-label">Distance Travelled</label>
          <input type="number" id="distanceTravelled" v-model="vehicle.distanceTravelled" class="form-control" placeholder="Distance Travelled" required min="1" />
          <div v-if="vehicle.distanceTravelled <= 0" class="text-danger">Distance travelled must be greater than 0</div>
        </div>
  
        <div class="mb-3">
          <label for="fuelConsumption" class="form-label">Fuel Consumption</label>
          <input type="number" id="fuelConsumption" v-model="vehicle.fuelConsumption" class="form-control" placeholder="Fuel Consumption" required min="1" />
          <div v-if="vehicle.fuelConsumption <= 0" class="text-danger">Fuel consumption must be greater than 0</div>
        </div>
  
                <div class="mb-3">
                  <button type="submit" class="btn btn-primary" :disabled="!isFormValid">Add Vehicle</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
    import * as bootstrap from 'bootstrap'
  export default {
    data() {
      return {
        vehicle: {
          name: '',
          vehicleType: null,
          fuelType: null,
          enginePower: null,
          numberOfWheels: null,
          capacity: null,
          distanceTravelled: null,
          fuelConsumption: null,
        },
        coordinates: {
          x: null,
          y: null,
        },
        coordinatesFromDB: null,
        vehicleTypes: [],
        fuelTypes: [],
        existingCoordinates: [],
        isEditMode: false,
        vehicleId: null,
        modal: null,
      };
    },
    mounted() {
    this.modal = new bootstrap.Modal(document.getElementById('vehicleModal'), {
    backdrop: 'static', 
    keyboard: true,
  });
},
    computed: {
      isFormValid() {
        return (
          this.vehicle.name &&
          this.vehicle.vehicleType &&
          this.vehicle.fuelType &&
          this.vehicle.enginePower > 0 &&
          this.vehicle.numberOfWheels > 0 &&
          this.vehicle.capacity > 0 &&
          this.vehicle.distanceTravelled > 0 &&
          this.vehicle.fuelConsumption > 0 &&
          (this.coordinatesFromDB || (this.coordinates.x && this.coordinates.y))
        );
      },
    },
    async created() {
      this.fetchVehicleTypes();
      this.fetchFuelTypes();
      const id = this.$route.params.vehicleId;
      if (id) {
        this.isEditMode = true;
        this.vehicleId = id;
        await this.loadVehicleData(id);
      }
    },
    methods: {
      openModal() {
    
        this.modal.show();
      },
  
      closeModal() {
       
        this.modal.hide();
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
  
      async fetchFuelTypes() {
        try {
          
          const response = await axios.get('http://localhost:7777/api/vehicles/fuelTypes', {
            withCredentials: true,
          });
          this.fuelTypes = response.data;
        } catch (error) {
          console.error('Error fetching fuel types', error);
        }
      },
  
      async loadCoordinatesFromDB() {
        try {
          
          const response = await axios.get('http://localhost:7777/api/vehicles/coordinates', {
            withCredentials: true,
          });
          this.existingCoordinates = response.data;
        } catch (error) {
          console.error('Error fetching coordinates', error);
        }
      },
  
      async loadVehicleData(id) {
        try {
          
          const response = await axios.get(`http://localhost:7777/api/vehicles/${id}`, {
            withCredentials: true,
          });
          const data = response.data;
          this.vehicle = {
            name: data.name,
            vehicleType: data.vehicleType.id,
            fuelType: data.fuelType.id,
            enginePower: data.enginePower,
            numberOfWheels: data.numberOfWheels,
            capacity: data.capacity,
            distanceTravelled: data.distanceTravelled,
            fuelConsumption: data.fuelConsumption,
            cooordiantes: data.coordinates || { x: null, y: null },
          };
          this.coordinates = data.coordinates || { x: null, y: null };
          this.coordinatesFromDB = data.coordinates?.id || null
        } catch (error) {
          console.error('Error loading vehicle data', error);
        }
      },
      async submitForm() {
        try {
          const payload = {
            name: this.vehicle.name,
            vehicleType: { id: this.vehicle.vehicleType },
            fuelType: { id: this.vehicle.fuelType },
            enginePower: this.vehicle.enginePower,
            numberOfWheels: this.vehicle.numberOfWheels,
            capacity: this.vehicle.capacity,
            distanceTravelled: this.vehicle.distanceTravelled,
            fuelConsumption: this.vehicle.fuelConsumption,
            oordinates: this.coordinatesFromDB 
        ? { id: this.coordinatesFromDB }
        : (this.coordinates.x !== null && this.coordinates.y !== null) 
          ? { x: this.coordinates.x, y: this.coordinates.y }
          : this.vehicle.coordinates,
            
            creationDate: new Date().toISOString(),
            userID: JSON.parse(localStorage.getItem('userId')),
          };
          console.log('Coordinates --------:', this.coordinates);
          
          
          
          if (this.isEditMode && this.vehicleId) {
            payload.vehicleId = this.vehicleId;
            // PUT запрос для обновления существующего объекта
            await axios.put(`http://localhost:7777/api/vehicles/${this.vehicleId}`, payload, { withCredentials: true, });
          } else {
            // POST запрос для создания нового объекта
            await axios.post('http://localhost:7777/api/vehicles', payload, { withCredentials: true, });
          }
          this.$emit('vehicle-updated', this.vehicle);
          this.$router.push('/vehicles');
          this.closeModal();
        } catch (error) {
          console.error('Error submitting vehicle', error);
        }
      },
    },
  };
  </script>
  
  <style scoped>
  .modal-body .form {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .modal-body .form .mb-3 {
    display: flex;
    flex-direction: column;
  }
  
  button.btn-close {
    font-size: 1.5rem;
  }
  </style>
  