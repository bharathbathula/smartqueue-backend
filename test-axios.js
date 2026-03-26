const axios = require('axios');
const api = axios.create({ baseURL: 'https://smartqueue-backend-3beq.onrender.com/api' });
console.log(api.getUri({ url: '/auth/register' }));
console.log(api.getUri({ url: 'auth/register' }));
