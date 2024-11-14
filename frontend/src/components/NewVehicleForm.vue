<template>
  <div>
    <button type="button" class="btn btn-primary" @click="openModal">Add</button>

    <div class="modal fade" id="vehicleModal" tabindex="-1" aria-labelledby="vehicleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="vehicleModalLabel">{{ isEditMode ? 'Edit Vehicle' : 'Add New Vehicle' }}</h5>
            <button @click="closeModal" type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="submitForm" class="form">
              <div class="mb-3">
                <label for="name" class="form-label">Vehicle Name</label>
                <input type="text" id="name" v-model="vehicle.name" class="form-control" placeholder="Enter vehicle name" required />
              </div>

              <div class="mb-3">
                <label for="vehicleType" class="form-label">Vehicle Type</label>
                <select id="vehicleType" v-model="vehicle.vehicleType" class="form-select" required>
                  <option v-for="type in vehicleTypes" :key="type.id" :value="type.id">
                    {{ type.name }}
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label for="fuelType" class="form-label">Fuel Type</label>
                <select id="fuelType" v-model="vehicle.fuelType" class="form-select" required>
                  <option v-for="fuel in fuelTypes" :key="fuel.id" :value="fuel.id">
                    {{ fuel.name }}
                  </option>
                </select>
              </div>

              <div class="mb-3">
                <label for="enginePower" class="form-label">Engine Power</label>
                <input type="number" id="enginePower" v-model="vehicle.enginePower" class="form-control" placeholder="Enter engine power" required />
              </div>

              <div class="mb-3">
                <label for="numberOfWheels" class="form-label">Number of Wheels</label>
                <input type="number" id="numberOfWheels" v-model="vehicle.numberOfWheels" class="form-control" placeholder="Enter number of wheels" required />
              </div>

              <div class="mb-3">
                <label for="capacity" class="form-label">Capacity</label>
                <input type="number" id="capacity" v-model="vehicle.capacity" class="form-control" placeholder="Enter capacity" required />
              </div>

              <div class="mb-3">
                <label for="distanceTravelled" class="form-label">Distance Travelled</label>
                <input type="number" id="distanceTravelled" v-model="vehicle.distanceTravelled" class="form-control" placeholder="Enter distance travelled" required />
              </div>

              <div class="mb-3">
                <label for="fuelConsumption" class="form-label">Fuel Consumption</label>
                <input type="number" id="fuelConsumption" v-model="vehicle.fuelConsumption" class="form-control" placeholder="Enter fuel consumption" required />
              </div>

              <div class="mb-3">
                <label for="coordinates" class="form-label">Coordinates</label>
                <div class="d-flex">
                  <div>
                    <label for="x" class="form-label">X</label>
                    <input type="number" id="x" v-model="coordinates.x" class="form-control" placeholder="X Coordinate" required :disabled="isCoordinatesSelected" />
                  </div>
                  <div class="ms-3">
                    <label for="y" class="form-label">Y</label>
                    <input type="number" id="y" v-model="coordinates.y" class="form-control" placeholder="Y Coordinate" required :disabled="isCoordinatesSelected" />
                  </div>
                  <button type="button" class="btn btn-secondary ms-3" @click="loadCoordinatesFromDB">Load Coordinates</button>
                </div>
              </div>

              <div v-if="existingCoordinates.length > 0" class="mb-3">
                <label for="existingCoordinates" class="form-label">Or Choose Existing Coordinates</label>
                <select v-model="coordinatesFromDB" id="existingCoordinates" class="form-select" @change="onCoordinatesSelect">
                  <option v-for="coord in existingCoordinates" :key="coord.id" :value="coord.id">
                    X: {{ coord.x }}, Y: {{ coord.y }}
                  </option>
                </select>
                <button v-if="coordinatesFromDB" type="button" class="btn btn-warning mt-2" @click="resetCoordinatesSelection">Reset Selection</button>
              </div>

              <div class="mb-3">
                <button type="submit" class="btn btn-primary" :disabled="!isFormValid">{{ isEditMode ? 'Save Changes' : 'Add Vehicle' }}</button>
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
import * as bootstrap from 'bootstrap';

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
      isCoordinatesSelected: false,
      userId: null,
    };
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
  async mounted() {
    this.modal = new bootstrap.Modal(document.getElementById('vehicleModal'), {
      backdrop: 'static',
      keyboard: true,
    });
  },
  async created() {
    this.fetchVehicleTypes();
    this.fetchFuelTypes();
    this.userId = JSON.parse(localStorage.getItem('userId')).id;
    const id = this.$route.params.vehicleId;
    if (id) {
      this.isEditMode = true;
      this.vehicleId = id;
      await this.loadVehicleData(id);
      this.openModal();
    }
  },
  methods: {
    openModal() {
      this.modal.show();
    },
    closeModal() {
      this.modal.hide();
      this.$router.push('/vehicles');
    },
    async fetchVehicleTypes() {
      try {
        const token = JSON.parse(localStorage.getItem('token'));
        const response = await axios.get('http://localhost:7777/api/vehicles/vehicleTypes', {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.vehicleTypes = response.data;
      } catch (error) {
        console.error('Error fetching vehicle types', error);
      }
    },
    async fetchFuelTypes() {
      try {
        const token = JSON.parse(localStorage.getItem('token'));
        const response = await axios.get('http://localhost:7777/api/vehicles/fuelTypes', {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.fuelTypes = response.data;
      } catch (error) {
        console.error('Error fetching fuel types', error);
      }
    },
    async loadCoordinatesFromDB() {
      try {
        const token = JSON.parse(localStorage.getItem('token'));
        const response = await axios.get('http://localhost:7777/api/vehicles/coordinates', {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.existingCoordinates = response.data;
      } catch (error) {
        console.error('Error fetching coordinates', error);
      }
    },
    onCoordinatesSelect() {
      const selectedCoordinates = this.existingCoordinates.find(
        (coord) => coord.id === this.coordinatesFromDB
      );
      if (selectedCoordinates) {
        this.coordinates.x = selectedCoordinates.x;
        this.coordinates.y = selectedCoordinates.y;
        this.isCoordinatesSelected = true;
      }
    },
    resetCoordinatesSelection() {
      this.isCoordinatesSelected = false;
      this.coordinates.x = null;
      this.coordinates.y = null;
      this.coordinatesFromDB = null;
    },
    async loadVehicleData(vehicleId) {
      try {
        const token = JSON.parse(localStorage.getItem('token'));
        const response = await axios.get(`http://localhost:7777/api/vehicles/${vehicleId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.vehicle = response.data;
        if (this.vehicle.coordinates) {
          this.coordinates.x = this.vehicle.coordinates.x;
          this.coordinates.y = this.vehicle.coordinates.y;
        }
      } catch (error) {
        console.error('Error fetching vehicle data', error);
      }
    },
    async submitForm() {
  try {
    const token = JSON.parse(localStorage.getItem('token'));

    const requestData = {
      name: this.vehicle.name,
      vehicleType: {id: this.vehicle.vehicleType},
      fuelType: {id: this.vehicle.fuelType},
      enginePower: this.vehicle.enginePower,
      numberOfWheels: this.vehicle.numberOfWheels,
      capacity: this.vehicle.capacity,
      distanceTravelled: this.vehicle.distanceTravelled,
      fuelConsumption: this.vehicle.fuelConsumption,
      coordinates: this.isCoordinatesSelected
        ? { id: this.coordinatesFromDB }
        : this.coordinates,
    };

    if (this.isEditMode) {
      await axios.put(`http://localhost:7777/api/vehicles/${this.vehicleId}`, requestData, {
        headers: { Authorization: `Bearer ${token}` },
      });
    } else {
      const requestData = {
      name: this.vehicle.name,
      vehicleType: {id: this.vehicle.vehicleType},
      fuelType: {id: this.vehicle.fuelType},
      enginePower: this.vehicle.enginePower,
      numberOfWheels: this.vehicle.numberOfWheels,
      capacity: this.vehicle.capacity,
      distanceTravelled: this.vehicle.distanceTravelled,
      fuelConsumption: this.vehicle.fuelConsumption,
      coordinates: this.isCoordinatesSelected
        ? { id: this.coordinatesFromDB }
        : this.coordinates,
      userID: JSON.parse(localStorage.getItem('userId')),
    };
      await axios.post('http://localhost:7777/api/vehicles', requestData, {
        headers: { Authorization: `Bearer ${token}` },
      });
    }

    this.closeModal();
  } catch (error) {
    console.error('Error saving vehicle data', error);
  }
}

    
  },
};
</script>

<style scoped>
/* Add your custom styles here */
</style>
