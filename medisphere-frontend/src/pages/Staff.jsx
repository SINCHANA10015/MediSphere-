import { useEffect, useState } from 'react'
import { staffApi, errorMessage } from '../api/client'
import { useToast } from '../components/Toast'
import { Modal, Field, PageHead, ErrorBanner, Empty, Spinner, titleCase } from '../components/ui'

// Values must match the Java enums exactly.
const SPECIALIZATIONS = ['GENERAL_PHYSICIAN', 'CARDIOLOGIST', 'NEUROLOGIST', 'ENT_SPECIALIST', 'DENTIST', 'HEMATOLOGISTS', 'PULMONOLOGIST', 'GASTROENTEROLOGIST', 'PEDIATRICIAN', 'GYNECOLOGIST', 'RECEPTIONIST', 'OTHERS']
const ROLES = ['DOCTOR', 'RECEPTIONIST', 'ADMIN', 'NURSE', 'OTHER']

const blank = {
  firstName: '', lastName: '', email: '', phoneNumber: '', gender: 'MALE', role: 'DOCTOR',
  dateOfJoining: '', experienceInYears: 0, specialization: 'GENERAL_PHYSICIAN',
  landmark: '', city: '', state: '', country: 'India', pinCode: '',
}

export default function Staff() {
  const toast = useToast()
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [q, setQ] = useState('')
  const [open, setOpen] = useState(false)

  const load = async (name = '') => {
    setLoading(true); setError('')
    try {
      setRows(name ? await staffApi.search(name) : await staffApi.list())
    } catch (e) {
      // The backend answers "no staff found" with an error - show that as an empty list.
      if (name && e.response) setRows([])
      else setError(errorMessage(e))
    } finally { setLoading(false) }
  }
  useEffect(() => { load() }, [])

  const remove = async (s) => {
    if (!window.confirm(`Remove ${s.firstName} ${s.lastName}? They will no longer be able to sign in.`)) return
    try { await staffApi.remove(s.staffId); toast.success('Staff member removed'); load(q) }
    catch (e) { toast.error(errorMessage(e)) }
  }

  return (
    <>
      <PageHead title="Staff" subtitle="Doctors and hospital employees">
        <form onSubmit={(e) => { e.preventDefault(); load(q.trim()) }} style={{ display: 'flex', gap: 8 }}>
          <input className="search" placeholder="Search by first or last name" value={q} onChange={(e) => setQ(e.target.value)} />
          <button className="btn ghost">Search</button>
        </form>
        <button className="btn" onClick={() => setOpen(true)}>Register staff</button>
      </PageHead>

      <ErrorBanner message={error} onRetry={() => load(q)} />

      <div className="card table-wrap">
        {loading ? <Spinner /> : rows.length === 0 ? (
          <Empty title="No staff found" text={q ? 'Try a different name.' : 'Register the first staff member to get started.'} />
        ) : (
          <table>
            <thead><tr><th>Staff</th><th>Role</th><th>Specialization</th><th>Contact</th><th>Experience</th><th></th></tr></thead>
            <tbody>
              {rows.map((s) => (
                <tr key={s.staffId}>
                  <td>{s.firstName} {s.lastName}<span className="sub id">{s.staffId}</span></td>
                  <td><span className="pill">{titleCase(s.role)}</span></td>
                  <td>{titleCase(s.specialization)}</td>
                  <td>{s.email}<span className="sub">{s.phoneNumber}</span></td>
                  <td>{s.experienceInYears} yr</td>
                  <td><div className="row-actions"><button className="btn small danger-ghost" onClick={() => remove(s)}>Remove</button></div></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {open && <StaffForm onClose={() => setOpen(false)} onSaved={() => { setOpen(false); load(q) }} />}
    </>
  )
}

function StaffForm({ onClose, onSaved }) {
  const toast = useToast()
  const [f, setF] = useState(blank)
  const [busy, setBusy] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  const isDoctor = f.role === 'DOCTOR'

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    // Shape matches StaffRequestDto exactly (note: staffAddressDto, pinCode)
    const body = {
      firstName: f.firstName, lastName: f.lastName, email: f.email, phoneNumber: f.phoneNumber,
      gender: f.gender, role: f.role, dateOfJoining: f.dateOfJoining || null,
      experienceInYears: Number(f.experienceInYears) || 0,
      staffType: isDoctor ? 'DOCTOR' : 'NON_DOCTOR',
      specialization: isDoctor ? f.specialization : f.role === 'RECEPTIONIST' ? 'RECEPTIONIST' : 'OTHERS',
      staffAddressDto: { landmark: f.landmark, city: f.city, state: f.state, country: f.country, pinCode: f.pinCode },
    }
    try { await staffApi.register(body); toast.success('Staff member registered'); onSaved() }
    catch (err) { toast.error(errorMessage(err)) }
    finally { setBusy(false) }
  }

  return (
    <Modal title="Register staff" onClose={onClose} wide>
      <form onSubmit={submit}>
        <div className="form-grid">
          <Field label="First name"><input required value={f.firstName} onChange={set('firstName')} /></Field>
          <Field label="Last name"><input required value={f.lastName} onChange={set('lastName')} /></Field>
          <Field label="Email"><input type="email" required value={f.email} onChange={set('email')} /></Field>
          <Field label="Phone number"><input required value={f.phoneNumber} onChange={set('phoneNumber')} /></Field>
          <Field label="Gender">
            <select value={f.gender} onChange={set('gender')}><option>MALE</option><option>FEMALE</option><option>OTHERS</option></select>
          </Field>
          <Field label="Role">
            <select value={f.role} onChange={set('role')}>{ROLES.map((r) => <option key={r} value={r}>{titleCase(r)}</option>)}</select>
          </Field>
          {isDoctor && (
            <Field label="Specialization">
              <select value={f.specialization} onChange={set('specialization')}>
                {SPECIALIZATIONS.filter((s) => s !== 'RECEPTIONIST' && s !== 'OTHERS').map((s) => <option key={s} value={s}>{titleCase(s)}</option>)}
              </select>
            </Field>
          )}
          <Field label="Date of joining"><input type="date" required value={f.dateOfJoining} onChange={set('dateOfJoining')} /></Field>
          <Field label="Experience (years)"><input type="number" min="0" value={f.experienceInYears} onChange={set('experienceInYears')} /></Field>
          <fieldset>
            <legend>Address</legend>
            <div className="form-grid">
              <Field label="Landmark"><input value={f.landmark} onChange={set('landmark')} /></Field>
              <Field label="City"><input value={f.city} onChange={set('city')} /></Field>
              <Field label="State"><input value={f.state} onChange={set('state')} /></Field>
              <Field label="Country"><input value={f.country} onChange={set('country')} /></Field>
              <Field label="Pin code"><input value={f.pinCode} onChange={set('pinCode')} /></Field>
            </div>
          </fieldset>
        </div>
        <div className="form-foot">
          <button type="button" className="btn ghost" onClick={onClose}>Cancel</button>
          <button className="btn" disabled={busy}>{busy ? 'Saving…' : 'Register staff'}</button>
        </div>
      </form>
    </Modal>
  )
}
