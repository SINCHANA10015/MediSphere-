import axios from 'axios'

// Every call goes to the API Gateway (http://localhost:9000). In dev, Vite proxies
// /gw/* -> gateway, so the paths below are exactly the gateway's route paths.
const http = axios.create({ baseURL: import.meta.env.VITE_API_BASE || '/gw', timeout: 15000 })

// Attach the JWT (once the backend issues one) to every request.
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('ms_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Turn any error into a readable message for the UI.
export function errorMessage(err) {
  if (err.code === 'ERR_NETWORK' || err.response?.status === 502 || err.response?.status === 504 || err.code === 'ECONNREFUSED') {
    return 'Cannot reach the service. Is that Spring Boot application running?'
  }
  const data = err.response?.data
  if (typeof data === 'string' && data.trim()) return data
  if (data?.message) return data.message
  if (err.response) return `Request failed (HTTP ${err.response.status})`
  return err.message || 'Something went wrong'
}

const data = (p) => p.then((r) => r.data)

// ---------------- Staff  ->  gateway route /staff/**  (StaffManagement) ----------------
export const staffApi = {
  list: () => data(http.get('/staff/allStaff')),
  byId: (id) => data(http.get(`/staff/staffById/${id}`)),
  doctorName: (id) => data(http.get(`/staff/doctorByName/${id}`)),
  search: (name) => data(http.get('/staff/byName', { params: { name } })),
  register: (body) => data(http.post('/staff/register-staff', body)),
  // NOTE: backend update method has no @PathVariable on staffId, so it is sent as ?staffId=
  update: (id, body) => data(http.put(`/staff/update/${id}`, body, { params: { staffId: id } })),
  // NOTE: backend returns HTTP 404 with body "Successfully deleted" - treat that as success.
  remove: async (id) => {
    try {
      return await data(http.delete(`/staff/delete/${id}`))
    } catch (e) {
      if (e.response?.data === 'Successfully deleted') return 'Successfully deleted'
      throw e
    }
  },
  // Not in the backend yet (LoginRequest/LoginResponse DTOs exist, no controller method).
  // The gateway already forwards /staff/**, so this works as soon as you add it.
  login: (email, password) => data(http.post('/staff/login', { email, password })),
}

// ---------------- Patients  ->  /patient/**  (PatientManagement) ----------------
export const patientApi = {
  list: () => data(http.get('/patient/get-all-patients')),
  byId: (id) => data(http.get(`/patient/get-by-id/${id}`)),
  register: (body) => data(http.post('/patient/register-patient', body)),
  update: (id, body) => data(http.put(`/patient/patientDetails/${id}`, body)),
  remove: (id) => data(http.delete(`/patient/delete/${id}`)),
}

// ---------------- Rooms & Beds  ->  /room/**, /bed/**, /bedAssignment/**  (BedManagement) ----------------
export const roomApi = {
  list: () => data(http.get('/room/getRooms')),
  add: (body) => data(http.post('/room/add', body)),
  update: (roomNumber, body) => data(http.put(`/room/update/${roomNumber}`, body)),
  remove: (roomNumber) => data(http.delete(`/room/delete/${roomNumber}`)),
}

export const bedApi = {
  list: () => data(http.get('/bed/allBeds')),
  inRoom: (room) => data(http.get(`/bed/room/${room}`)),
  vacantInRoom: (room) => data(http.get(`/bed/vacant/room/${room}`)),
  add: (body) => data(http.post('/bed/add', body)),
  remove: (bedNumber, roomNumber) => data(http.delete(`/bed/delete-bed/${bedNumber}/${roomNumber}`)),
  assign: (bedNumber, patientId) => data(http.post(`/bedAssignment/assign/${bedNumber}/${patientId}`)),
  vacate: (roomNumber, bedNumber) => data(http.put(`/bedAssignment/vacatebed/${roomNumber}/${bedNumber}`)),
  history: (bedNumber) => data(http.get(`/bedAssignment/bed-history/${bedNumber}`)),
}

// ---------------- Appointments  ->  /appointment/**  (AppointmentManagement) ----------------
export const appointmentApi = {
  book: (body) => data(http.post('/appointment/bookAppointment', body)),
}

// ---------------- Notifications  ->  /api/notifications/**  (NotificationService) ----------------
export const notificationApi = {
  send: (body) => data(http.post('/api/notifications/send', body)),
}
