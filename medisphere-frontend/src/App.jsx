import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Staff from './pages/Staff'
import Patients from './pages/Patients'
import Beds from './pages/Beds'
import Appointments from './pages/Appointments'
import Billing from './pages/Billing'

// Only lets signed-in users in, and (optionally) only certain roles.
function Protected({ roles, children }) {
  const { session } = useAuth()
  if (!session) return <Navigate to="/login" replace />
  if (roles && !roles.includes(session.role)) return <Navigate to="/" replace />
  return children
}

export default function App() {
  const { session } = useAuth()
  return (
    <Routes>
      <Route path="/login" element={session ? <Navigate to="/" replace /> : <Login />} />
      <Route element={<Protected><Layout /></Protected>}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/patients" element={<Protected roles={['ADMIN', 'RECEPTIONIST', 'DOCTOR']}><Patients /></Protected>} />
        <Route path="/appointments" element={<Protected roles={['RECEPTIONIST', 'DOCTOR']}><Appointments /></Protected>} />
        <Route path="/billing" element={<Protected roles={['ADMIN', 'RECEPTIONIST']}><Billing /></Protected>} />
        <Route path="/beds" element={<Protected roles={['ADMIN', 'RECEPTIONIST']}><Beds /></Protected>} />
        <Route path="/staff" element={<Protected roles={['ADMIN']}><Staff /></Protected>} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
