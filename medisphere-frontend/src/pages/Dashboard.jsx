import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { staffApi, patientApi, roomApi } from '../api/client'
import { PageHead } from '../components/ui'
import { money, useBilling } from '../billing/BillingContext'

// Each number is fetched on its own, so one service being down
// only greys out that one tile instead of breaking the page.
export default function Dashboard() {
  const { session } = useAuth()
  const { invoices, medicines } = useBilling()
  const [s, setS] = useState({ staff: null, patients: null, rooms: null })
  const todays = invoices.filter((i) => i.status === 'PAID' && new Date(i.createdAt).toDateString() === new Date().toDateString())
  const lowStock = medicines.filter((m) => m.stock <= 30).length

  useEffect(() => {
    const done = (key) => (v) => setS((p) => ({ ...p, [key]: v }))
    staffApi.list().then((d) => done('staff')(d.length)).catch(() => done('staff')('off'))
    patientApi.list().then((d) => done('patients')(d.length)).catch(() => done('patients')('off'))
    roomApi.list()
      .then((rooms) => {
        const beds = rooms.flatMap((r) => r.beds || [])
        done('rooms')({ rooms: rooms.length, beds: beds.length, free: beds.filter((b) => !b.occupied).length })
      })
      .catch(() => done('rooms')('off'))
  }, [])

  const tile = (label, value) => (
    <div className={`card stat ${value === 'off' ? 'off' : ''}`}>
      <b>{value === null ? '…' : value === 'off' ? 'Service offline' : value}</b>
      <span>{label}</span>
    </div>
  )
  const r = s.rooms && s.rooms !== 'off' ? s.rooms : null

  const links = [
    { to: '/patients', t: 'Register or find a patient', d: 'Records, contact details and addresses', roles: ['ADMIN', 'RECEPTIONIST', 'DOCTOR'] },
    { to: '/appointments', t: 'Book an appointment', d: 'Pick a doctor, a date and a time slot', roles: ['RECEPTIONIST', 'DOCTOR'] },
    { to: '/billing', t: 'Bill medicines', d: 'Take payment and print the invoice', roles: ['ADMIN', 'RECEPTIONIST'] },
    { to: '/beds', t: 'Assign a bed', d: 'See which beds are free and who is in them', roles: ['ADMIN', 'RECEPTIONIST'] },
    { to: '/staff', t: 'Manage staff', d: 'Register doctors and employees', roles: ['ADMIN'] },
  ].filter((l) => l.roles.includes(session.role))

  return (
    <>
      <PageHead title="Dashboard" subtitle={`Signed in as ${session.role.toLowerCase()}`} />
      <div className="stats">
        {tile('Staff members', s.staff)}
        {tile('Registered patients', s.patients)}
        {s.rooms === 'off' ? tile('Rooms', 'off') : tile('Rooms', r ? r.rooms : null)}
        {s.rooms === 'off' ? tile('Free beds', 'off') : tile(r ? `Free beds of ${r.beds}` : 'Free beds', r ? r.free : null)}
      </div>
      {['ADMIN', 'RECEPTIONIST'].includes(session.role) && (
        <div className="stats">
          <div className="card stat"><b>{money(todays.reduce((s, i) => s + i.totals.total, 0))}</b><span>Pharmacy sales today · {todays.length} bills</span></div>
          <div className={`card stat`}><b style={lowStock ? { color: 'var(--amber)' } : null}>{lowStock}</b><span>Medicines low on stock</span></div>
        </div>
      )}
      <h2 className="section-title">Quick actions</h2>
      <div className="quick">
        {links.map((l) => (
          <Link key={l.to} to={l.to} className="card">
            <strong>{l.t}</strong>
            <span>{l.d}</span>
          </Link>
        ))}
      </div>
    </>
  )
}
