# MediSphere frontend

React + Vite single-page app for the MediSphere / Rainbow Hospitals microservices.
All calls go through the **API Gateway (port 9000)**.

## Run

1. Start MySQL, **EurekaServer (8761)**, the services you need, then **APIGateway (9000)**.
2. `npm install` then `npm run dev` -> http://localhost:5173

In dev, Vite proxies `/gw/*` to `http://localhost:9000/*` (no CORS setup needed).
For a production build set `VITE_API_BASE=http://<gateway-host>:9000` and enable CORS on the gateway.

## Gateway routes used

| Frontend call | Gateway route | Service |
|---|---|---|
| `/staff/**` | `Path=/staff/**` | StaffManagement |
| `/patient/**` | `Path=/patient/**` | PatientManagement |
| `/bed/**`, `/room/**`, `/bedAssignment/**` | same | BedManagement |
| `/appointment/**` | `Path=/appointment/**` | AppointmentManagement |
| `/api/notifications/**` | `Path=/api/notifications/**` | NotificationService |

## Pages

Dashboard · Patients · Appointments · Pharmacy billing · Rooms & beds · Staff.
Roles: Admin, Receptionist, Doctor (demo login on the sign-in page until `/staff/login` exists).

## Pharmacy billing (no backend yet)

Medicines, stock and invoices live in `localStorage` through `src/billing/BillingContext.jsx`.
To add a real backend later, replace the load/save calls in that one file.
Receipts are emailed through the real `/api/notifications/send` route.
