import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { appointmentApi, staffApi, patientApi, errorMessage } from '../api/client'
import { useToast } from '../components/Toast'
import { Field, PageHead, Empty, titleCase } from '../components/ui'

// "Paracetamol 500mg, Cetirizine" -> [{ name, qty }]  (the billing page matches names to the catalogue)
const toItems = (text) => String(text).split(/[,;\n]+/).map((t) => t.trim()).filter(Boolean).map((name) => ({ name, qty: 1 }))

const today = () => new Date().toISOString().slice(0, 10)

export default function Appointments() {
  const toast = useToast()
  const navigate = useNavigate()
  const { session } = useAuth()
  const [doctors, setDoctors] = useState([])
  const [patients, setPatients] = useState([])
  const [loadError, setLoadError] = useState('')
  const [booked, setBooked] = useState([]) // appointments booked in this browser session
  const [busy, setBusy] = useState(false)
  const [f, setF] = useState({ patientId: '', doctorId: '', appointmentDate: today(), startTime: '10:00', endTime: '10:30', reasonForVisit: '', notes: '' })
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  useEffect(() => {
    Promise.all([staffApi.list(), patientApi.list()])
      .then(([staff, pats]) => { setDoctors(staff.filter((s) => s.staffType === 'DOCTOR')); setPatients(pats) })
      .catch((e) => setLoadError(errorMessage(e)))
  }, [])

  const submit = async (e) => {
    e.preventDefault()
    if (f.endTime <= f.startTime) return toast.error('End time must be after the start time')
    setBusy(true)
    try {
      // Shape matches AppointmentRequestDto
      const res = await appointmentApi.book(f)
      const p = patients.find((x) => x.patientId === f.patientId)
      const d = doctors.find((x) => x.staffId === f.doctorId)
      setBooked((b) => [{
        ...f, appointmentId: res?.appointmentId, status: res?.status || 'SCHEDULED',
        medicines: res?.medicines || '',
        patientName: res?.patientName || p?.patientName, doctorName: res?.doctorName || (d ? `Dr. ${d.firstName} ${d.lastName}` : f.doctorId),
      }, ...b])
      toast.success('Appointment booked')
      setF({ ...f, patientId: '', reasonForVisit: '', notes: '' })
    } catch (err) { toast.error(errorMessage(err)) } finally { setBusy(false) }
  }

  return (
    <>
      <PageHead title="Appointments" subtitle="Book a visit with a doctor" />
      {loadError && <div className="banner err">{loadError} The patient and doctor lists need those services running.</div>}
      <div className="banner info">
        The appointment service can currently book only. Listing, rescheduling and cancelling need new backend endpoints,
        so appointments below show only what you booked in this session.
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 420px) minmax(0, 1fr)', gap: 20, alignItems: 'start' }} className="appt-grid">
        <form className="card" style={{ padding: 20, display: 'grid', gap: 14 }} onSubmit={submit}>
          <Field label="Patient">
            <select required value={f.patientId} onChange={set('patientId')}>
              <option value="">Choose a patient…</option>
              {patients.map((p) => <option key={p.patientId} value={p.patientId}>{p.patientName} ({p.patientId})</option>)}
            </select>
          </Field>
          <Field label="Doctor">
            <select required value={f.doctorId} onChange={set('doctorId')}>
              <option value="">Choose a doctor…</option>
              {doctors.map((d) => <option key={d.staffId} value={d.staffId}>Dr. {d.firstName} {d.lastName} · {titleCase(d.specialization)}</option>)}
            </select>
          </Field>
          <Field label="Date"><input type="date" required min={today()} value={f.appointmentDate} onChange={set('appointmentDate')} /></Field>
          <div className="form-grid">
            <Field label="Starts"><input type="time" required value={f.startTime} onChange={set('startTime')} /></Field>
            <Field label="Ends"><input type="time" required value={f.endTime} onChange={set('endTime')} /></Field>
          </div>
          <Field label="Reason for visit"><input required value={f.reasonForVisit} onChange={set('reasonForVisit')} /></Field>
          <Field label="Notes (optional)"><textarea value={f.notes} onChange={set('notes')} /></Field>
          <button className="btn" disabled={busy}>{busy ? 'Booking…' : 'Book appointment'}</button>
        </form>

        <div className="card table-wrap">
          {booked.length === 0 ? <Empty title="Nothing booked yet" text="Appointments you book will appear here." /> : (
            <table>
              <thead><tr><th>Patient</th><th>Doctor</th><th>When</th><th>Status</th><th></th></tr></thead>
              <tbody>
                {booked.map((a, i) => (
                  <tr key={i}>
                    <td>{a.patientName}<span className="sub">{a.reasonForVisit}</span></td>
                    <td>{a.doctorName}</td>
                    <td>{a.appointmentDate}<span className="sub">{a.startTime} – {a.endTime}</span></td>
                    <td><span className="pill amber">{titleCase(a.status)}</span></td>
                    <td>{a.medicines && session.role !== 'DOCTOR' && (
                      <button className="btn small ghost" onClick={() => navigate('/billing', { state: { patientId: a.patientId, items: toItems(a.medicines) } })}>Bill prescription</button>
                    )}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </>
  )
}
