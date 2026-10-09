import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { patientApi, errorMessage } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../components/Toast'
import { Modal, Field, PageHead, ErrorBanner, Empty, Spinner, titleCase } from '../components/ui'

const blank = {
  patientName: '', gender: 'MALE', patientEmail: '', patientPhoneNumber: '', dateOfBirth: '',
  doorNumber: '', landmark: '', city: '', state: '', country: 'India', pincode: '',
}

const age = (dob) => {
  if (!dob) return '—'
  const d = new Date(dob), n = new Date()
  let a = n.getFullYear() - d.getFullYear()
  if (n < new Date(n.getFullYear(), d.getMonth(), d.getDate())) a--
  return a
}

export default function Patients() {
  const { session } = useAuth()
  const toast = useToast()
  const navigate = useNavigate()
  const canEdit = true // every role may edit patient details; restrict here if you want
  const canRegister = session.role === 'RECEPTIONIST' || session.role === 'ADMIN'
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [q, setQ] = useState('')
  const [editing, setEditing] = useState(null) // null | 'new' | patient

  const load = async () => {
    setLoading(true); setError('')
    try { setRows(await patientApi.list()) } catch (e) { setError(errorMessage(e)) } finally { setLoading(false) }
  }
  useEffect(() => { load() }, [])

  const shown = useMemo(() => {
    const t = q.trim().toLowerCase()
    if (!t) return rows
    return rows.filter((p) => [p.patientName, p.patientId, p.patientEmail, p.patientPhoneNumber].some((v) => String(v || '').toLowerCase().includes(t)))
  }, [rows, q])

  const remove = async (p) => {
    if (!window.confirm(`Delete patient ${p.patientName}? This cannot be undone.`)) return
    try { await patientApi.remove(p.patientId); toast.success('Patient deleted'); load() }
    catch (e) { toast.error(errorMessage(e)) }
  }

  return (
    <>
      <PageHead title="Patients" subtitle={`${rows.length} registered`}>
        <input className="search" placeholder="Search name, ID, phone, email" value={q} onChange={(e) => setQ(e.target.value)} />
        {canRegister && <button className="btn" onClick={() => setEditing('new')}>Register patient</button>}
      </PageHead>

      <ErrorBanner message={error} onRetry={load} />

      <div className="card table-wrap">
        {loading ? <Spinner /> : shown.length === 0 ? (
          <Empty title={q ? 'No matching patients' : 'No patients yet'} text={q ? 'Check the spelling or try the patient ID.' : 'Register the first patient to get started.'} />
        ) : (
          <table>
            <thead><tr><th>Patient</th><th>Gender · Age</th><th>Contact</th><th>City</th><th></th></tr></thead>
            <tbody>
              {shown.map((p) => (
                <tr key={p.patientId}>
                  <td>{p.patientName}<span className="sub id">{p.patientId}</span></td>
                  <td>{titleCase(p.gender)} · {age(p.dateOfBirth)}</td>
                  <td>{p.patientEmail}<span className="sub">{p.patientPhoneNumber}</span></td>
                  <td>{p.patientAddress?.city || '—'}</td>
                  <td>
                    <div className="row-actions">
                      {canRegister && <button className="btn small ghost" onClick={() => navigate(`/billing?patient=${p.patientId}`)}>Bill medicines</button>}
                      {canEdit && <button className="btn small ghost" onClick={() => setEditing(p)}>Edit</button>}
                      {canRegister && <button className="btn small danger-ghost" onClick={() => remove(p)}>Delete</button>}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {editing && <PatientForm patient={editing === 'new' ? null : editing} onClose={() => setEditing(null)} onSaved={() => { setEditing(null); load() }} />}
    </>
  )
}

function PatientForm({ patient, onClose, onSaved }) {
  const toast = useToast()
  const a = patient?.patientAddress || {}
  const [f, setF] = useState(patient ? {
    patientName: patient.patientName || '', gender: patient.gender || 'MALE', patientEmail: patient.patientEmail || '',
    patientPhoneNumber: patient.patientPhoneNumber || '', dateOfBirth: patient.dateOfBirth || '',
    doorNumber: a.doorNumber || '', landmark: a.landmark || '', city: a.city || '', state: a.state || '', country: a.country || 'India', pincode: a.pincode || '',
  } : blank)
  const [busy, setBusy] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    // Shape matches PatientRequestDto exactly (note: patientAddressRequestDto, pincode)
    const body = {
      patientName: f.patientName, gender: f.gender, patientEmail: f.patientEmail,
      patientPhoneNumber: f.patientPhoneNumber, dateOfBirth: f.dateOfBirth,
      patientAddressRequestDto: { doorNumber: f.doorNumber, landmark: f.landmark, city: f.city, state: f.state, country: f.country, pincode: f.pincode },
    }
    try {
      patient ? await patientApi.update(patient.patientId, body) : await patientApi.register(body)
      toast.success(patient ? 'Patient updated' : 'Patient registered'); onSaved()
    } catch (err) { toast.error(errorMessage(err)) } finally { setBusy(false) }
  }

  return (
    <Modal title={patient ? `Edit ${patient.patientName}` : 'Register patient'} onClose={onClose} wide>
      <form onSubmit={submit}>
        <div className="form-grid">
          <Field label="Full name"><input required value={f.patientName} onChange={set('patientName')} /></Field>
          <Field label="Date of birth"><input type="date" required value={f.dateOfBirth} onChange={set('dateOfBirth')} /></Field>
          <Field label="Gender">
            <select value={f.gender} onChange={set('gender')}><option value="MALE">Male</option><option value="FEMALE">Female</option><option value="OTHERS">Others</option></select>
          </Field>
          <Field label="Phone number"><input required value={f.patientPhoneNumber} onChange={set('patientPhoneNumber')} /></Field>
          <Field label="Email"><input type="email" required value={f.patientEmail} onChange={set('patientEmail')} /></Field>
          <div />
          <fieldset>
            <legend>Address</legend>
            <div className="form-grid">
              <Field label="Door number"><input value={f.doorNumber} onChange={set('doorNumber')} /></Field>
              <Field label="Landmark"><input value={f.landmark} onChange={set('landmark')} /></Field>
              <Field label="City"><input value={f.city} onChange={set('city')} /></Field>
              <Field label="State"><input value={f.state} onChange={set('state')} /></Field>
              <Field label="Country"><input value={f.country} onChange={set('country')} /></Field>
              <Field label="Pin code"><input value={f.pincode} onChange={set('pincode')} /></Field>
            </div>
          </fieldset>
        </div>
        <div className="form-foot">
          <button type="button" className="btn ghost" onClick={onClose}>Cancel</button>
          <button className="btn" disabled={busy}>{busy ? 'Saving…' : patient ? 'Save changes' : 'Register patient'}</button>
        </div>
      </form>
    </Modal>
  )
}
