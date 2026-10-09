import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// ---------------------------------------------------------------
// Every request goes through the Spring Cloud API Gateway (port 9000).
// The gateway (APIGateway/application.properties) exposes these paths:
//   /staff/**              -> StaffManagement
//   /patient/**            -> PatientManagement
//   /bed/**                -> BedManagement
//   /room/**               -> BedManagement
//   /bedAssignment/**      -> BedManagement
//   /appointment/**        -> AppointmentManagement
//   /api/notifications/**  -> NotificationService
//
// The browser calls  /gw/patient/...  on this dev server and Vite forwards it
// to  http://localhost:9000/patient/...  - so no CORS setup is needed in dev.
// ---------------------------------------------------------------
const GATEWAY = process.env.VITE_GATEWAY_URL || 'http://localhost:9000'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/gw': { target: GATEWAY, changeOrigin: true, rewrite: (p) => p.replace(/^\/gw/, '') },
    },
  },
})
