<template>
    <div class="container">
      <h2>Audit Logs</h2>
      <router-link to="/vehicles" class="btn btn-primary ms-3">Home</router-link>
      
      <!-- Таблица с историей -->
      <table class="table table-striped">
        <thead>
          <tr>
            <th>Action</th>
            <th>Vehicle ID</th>
            <th>Vehicle Name</th>
            <th>User ID</th>
            <th>Username</th>
            <th>Timestamp</th>
            <th>Details</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in auditLogs.content" :key="log.id">
            <td>{{ log.actionType }}</td>
            <td>{{ log.objectId }}</td>
            <td>{{ log.objectName }}</td>
            <td>{{ log.userId }}</td>
            <td>{{ log.userName }}</td>
            <td>{{ formatDate(log.createdAt) }}</td>
            <td>{{ log.details }}</td>
          </tr>
        </tbody>
      </table>
  
      <!-- Пагинация -->
      <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
          <li class="page-item" :class="{ disabled: currentPage === 0 }">
            <button class="page-link" @click="changePage(currentPage - 1)" :disabled="currentPage === 0">Previous</button>
          </li>
          <li v-for="page in totalPages" :key="page" class="page-item" :class="{ active: currentPage === page - 1 }">
            <button class="page-link" @click="changePage(page - 1)">{{ page }}</button>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
            <button class="page-link" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages - 1">Next</button>
          </li>
        </ul>
      </nav>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import { format } from 'date-fns';  // Импортируем функцию format из date-fns
  
  export default {
    data() {
      return {
        auditLogs: {
          content: [],
          totalPages: 0,
        },
        currentPage: 0,
        itemsPerPage: 10,
      };
    },
    computed: {
      totalPages() {
        return this.auditLogs.totalPages;
      }
    },
    methods: {
      formatDate(date) {
        const parsedDate = new Date(date);
        if (isNaN(parsedDate)) {
          return 'Invalid Date';
        }
        return format(parsedDate, 'dd.MM.yyyy HH:mm:ss');
      },
      async fetchAuditLogs() {
        try {
          const response = await axios.get('http://localhost:7777/api/vehicles/audit', {
            params: {
              page: this.currentPage,
              size: this.itemsPerPage,
            },
            headers: {
              Authorization: `Bearer ${JSON.parse(localStorage.getItem('token'))}`,
            }
          });
          this.auditLogs = response.data;
          console.log('История изменений:', this.auditLogs);
        } catch (error) {
          console.error('Ошибка при получении истории изменений:', error);
        }
      },
      changePage(page) {
        this.currentPage = page;
        this.fetchAuditLogs();
      }
    },
    mounted() {
      this.fetchAuditLogs();
    }
  };
  </script>
  
  <style>
  .container {
    margin-top: 20px;
  }
  </style>
  