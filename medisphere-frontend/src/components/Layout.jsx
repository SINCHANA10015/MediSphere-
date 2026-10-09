import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Which sidebar links each role can see (matches the README's role table).
const NAV = [
  { to: '/', label: 'Dashboard', roles: ['ADMIN', 'RECEPTIONIST', 'DOCTOR'], end: true },
  { to: '/patients', label: 'Patients', roles: ['RECEPTIONIST', 'DOCTOR', 'ADMIN'] },
  { to: '/appointments', label: 'Appointments', roles: ['RECEPTIONIST', 'DOCTOR'] },
  { to: '/billing', label: 'Pharmacy billing', roles: ['RECEPTIONIST', 'ADMIN'] },
  { to: '/beds', label: 'Rooms & beds', roles: ['RECEPTIONIST', 'ADMIN'] },
  { to: '/staff', label: 'Staff', roles: ['ADMIN'] },
]

export default function Layout() {
  const { session, logout } = useAuth()
  const navigate = useNavigate()
  const links = NAV.filter((n) => n.roles.includes(session.role))

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">M</span>
          <div>
            <strong>MediSphere</strong>
            <small>Rainbow Hospitals</small>
          </div>
        </div>
        <nav>
          {links.map((n) => (
            <NavLink key={n.to} to={n.to} end={n.end} className={({ isActive }) => (isActive ? 'active' : '')}>
              {n.label}
            </NavLink>
          ))}
        </nav>
        <div className="me">
          <div>
            <strong>{session.name}</strong>
            <small>{session.role.charAt(0) + session.role.slice(1).toLowerCase()}{session.demo ? ' · demo mode' : ''}</small>
          </div>
          <button className="btn small ghost-dark" onClick={() => { logout(); navigate('/login') }}>Sign out</button>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
