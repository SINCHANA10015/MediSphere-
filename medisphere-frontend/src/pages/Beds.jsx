import { useEffect, useState } from 'react'
import { roomApi, bedApi, patientApi, errorMessage } from '../api/client'
import { useToast } from '../components/Toast'
import { Modal, Field, PageHead, ErrorBanner, Empty, Spinner } from '../components/ui'

const fmt = (d) => (d ? new Date(d).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : '—')

export default function Beds() {
  const toast = useToast()
  const [rooms, setRooms] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [roomForm, setRoomForm] = useState(false)
  const [bedForm, setBedForm] = useState(null)   // room number to add a bed to
  const [bed, setBed] = useState(null)           // selected bed (object)

  const load = async () => {
    setLoading(true); setError('')
    try { setRooms(await roomApi.list()) } catch (e) { setError(errorMessage(e)) } finally { setLoading(false) }
  }
  useEffect(() => { load() }, [])

  const removeRoom = async (r) => {
    if (!window.confirm(`Delete room ${r.roomNumber} and all of its beds?`)) return
    try { await roomApi.remove(r.roomNumber); toast.success(`Room ${r.roomNumber} deleted`); load() }
    catch (e) { toast.error(errorMessage(e)) }
  }

  return (
    <>
      <PageHead title="Rooms & beds" subtitle="Select a bed to assign a patient, vacate it, or see who used it">
        <div className="legend"><span><i className="dot v" />Vacant</span><span><i className="dot o" />Occupied</span></div>
        <button className="btn" onClick={() => setRoomForm(true)}>Add room</button>
      </PageHead>

      <ErrorBanner message={error} onRetry={load} />

      {loading ? <Spinner /> : rooms.length === 0 && !error ? (
        <div className="card"><Empty title="No rooms yet" text="Add a room, then add beds to it." /></div>
      ) : (
        <div className="rooms">
          {rooms.map((r) => {
            const beds = r.beds || []
            const free = beds.filter((b) => !b.occupied).length
            return (
              <section key={r.roomNumber} className="card room">
                <div className="room-head">
                  <div>
                    <h3>Room {r.roomNumber}</h3>
                    <p>{r.roomType} · capacity {r.roomCapacity} · {free} of {beds.length} beds free</p>
                  </div>
                  <button className="btn small danger-ghost" onClick={() => removeRoom(r)}>Delete room</button>
                </div>
                <div className="bed-grid">
                  {beds.map((b) => (
                    <button key={b.bedNumber} className={`bed ${b.occupied ? 'occupied' : 'vacant'}`} onClick={() => setBed({ ...b, roomNumber: b.roomNumber ?? r.roomNumber })}
                      aria-label={`Bed ${b.bedNumber}, ${b.occupied ? 'occupied' : 'vacant'}`}>
                      <b>{b.bedNumber}</b><span>{b.occupied ? 'Occupied' : 'Vacant'}</span>
                    </button>
                  ))}
                  <button className="bed add" onClick={() => setBedForm(r.roomNumber)} aria-label={`Add bed to room ${r.roomNumber}`}>+</button>
                </div>
              </section>
            )
          })}
        </div>
      )}

      {roomForm && <RoomForm onClose={() => setRoomForm(false)} onSaved={() => { setRoomForm(false); load() }} />}
      {bedForm !== null && <BedForm room={bedForm} onClose={() => setBedForm(null)} onSaved={() => { setBedForm(null); load() }} />}
      {bed && <BedDetail bed={bed} onClose={() => setBed(null)} onChanged={() => { setBed(null); load() }} />}
    </>
  )
}

function RoomForm({ onClose, onSaved }) {
  const toast = useToast()
  const [f, setF] = useState({ roomNumber: '', roomType: 'General ward', roomCapacity: 2, beds: '' })
  const [busy, setBusy] = useState(false)
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    const roomNumber = Number(f.roomNumber)
    const beds = f.beds.split(',').map((s) => s.trim()).filter(Boolean)
      .map((n) => ({ bedNumber: Number(n), roomNumber, occupied: false }))
    try {
      await roomApi.add({ roomNumber, roomType: f.roomType, roomCapacity: Number(f.roomCapacity), beds })
      toast.success(`Room ${roomNumber} added`); onSaved()
    } catch (err) { toast.error(errorMessage(err)) } finally { setBusy(false) }
  }

  return (
    <Modal title="Add room" onClose={onClose}>
      <form onSubmit={submit}>
        <div className="form-grid">
          <Field label="Room number"><input type="number" required min="1" value={f.roomNumber} onChange={set('roomNumber')} /></Field>
          <Field label="Capacity (beds)"><input type="number" required min="1" value={f.roomCapacity} onChange={set('roomCapacity')} /></Field>
          <div className="full"><Field label="Room type"><input required value={f.roomType} onChange={set('roomType')} placeholder="General ward, ICU, Private…" /></Field></div>
          <div className="full"><Field label="Bed numbers (optional)" hint="Separate with commas, e.g. 101, 102. Each bed number must be unique across the hospital."><input value={f.beds} onChange={set('beds')} /></Field></div>
        </div>
        <div className="form-foot">
          <button type="button" className="btn ghost" onClick={onClose}>Cancel</button>
          <button className="btn" disabled={busy}>{busy ? 'Saving…' : 'Add room'}</button>
        </div>
      </form>
    </Modal>
  )
}

function BedForm({ room, onClose, onSaved }) {
  const toast = useToast()
  const [bedNumber, setBedNumber] = useState('')
  const [busy, setBusy] = useState(false)
  const submit = async (e) => {
    e.preventDefault(); setBusy(true)
    try { await bedApi.add({ bedNumber: Number(bedNumber), roomNumber: room, occupied: false }); toast.success('Bed added'); onSaved() }
    catch (err) { toast.error(errorMessage(err)) } finally { setBusy(false) }
  }
  return (
    <Modal title={`Add a bed to room ${room}`} onClose={onClose}>
      <form onSubmit={submit}>
        <Field label="Bed number" hint="Must be unique across the hospital."><input type="number" required min="1" value={bedNumber} onChange={(e) => setBedNumber(e.target.value)} autoFocus /></Field>
        <div className="form-foot">
          <button type="button" className="btn ghost" onClick={onClose}>Cancel</button>
          <button className="btn" disabled={busy}>{busy ? 'Saving…' : 'Add bed'}</button>
        </div>
      </form>
    </Modal>
  )
}

function BedDetail({ bed, onClose, onChanged }) {
  const toast = useToast()
  const [history, setHistory] = useState(null)
  const [patients, setPatients] = useState([])
  const [patientId, setPatientId] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    bedApi.history(bed.bedNumber).then(setHistory).catch(() => setHistory([]))
    if (!bed.occupied) patientApi.list().then(setPatients).catch(() => setPatients([]))
  }, [bed])

  const current = (history || []).find((h) => !h.vacatedAt)
  const run = async (fn, ok) => {
    setBusy(true)
    try { await fn(); toast.success(ok); onChanged() } catch (e) { toast.error(errorMessage(e)) } finally { setBusy(false) }
  }

  return (
    <Modal title={`Bed ${bed.bedNumber}`} onClose={onClose}>
      <dl className="kv">
        <dt>Room</dt><dd>{bed.roomNumber}</dd>
        <dt>Status</dt><dd><span className={`pill ${bed.occupied ? 'red' : 'green'}`}>{bed.occupied ? 'Occupied' : 'Vacant'}</span></dd>
        {bed.occupied && <><dt>Patient</dt><dd>{current ? current.patientId : history === null ? 'Loading…' : 'Unknown'}</dd></>}
      </dl>

      {bed.occupied ? (
        <button className="btn" disabled={busy} onClick={() => run(() => bedApi.vacate(bed.roomNumber, bed.bedNumber), 'Bed vacated')}>Vacate bed</button>
      ) : (
        <form onSubmit={(e) => { e.preventDefault(); run(() => bedApi.assign(bed.bedNumber, patientId), 'Bed assigned') }} style={{ display: 'grid', gap: 12 }}>
          <Field label="Assign to patient">
            <select required value={patientId} onChange={(e) => setPatientId(e.target.value)}>
              <option value="">Choose a patient…</option>
              {patients.map((p) => <option key={p.patientId} value={p.patientId}>{p.patientName} ({p.patientId})</option>)}
            </select>
          </Field>
          <div><button className="btn" disabled={busy || !patientId}>Assign bed</button></div>
        </form>
      )}

      <h3 style={{ fontSize: 15, margin: '22px 0 8px' }}>Bed history</h3>
      {history === null ? <Spinner /> : history.length === 0 ? <p style={{ color: 'var(--muted)', margin: 0 }}>No one has used this bed yet.</p> : (
        <div className="table-wrap" style={{ border: '1px solid var(--line)', borderRadius: 8 }}>
          <table>
            <thead><tr><th>Patient</th><th>Assigned</th><th>Vacated</th></tr></thead>
            <tbody>{history.map((h, i) => <tr key={i}><td className="id">{h.patientId}</td><td>{fmt(h.assignedAt)}</td><td>{h.vacatedAt ? fmt(h.vacatedAt) : <span className="pill red">In bed</span>}</td></tr>)}</tbody>
          </table>
        </div>
      )}

      {!bed.occupied && (
        <div className="form-foot" style={{ justifyContent: 'flex-start' }}>
          <button className="btn small danger-ghost" disabled={busy} onClick={() => window.confirm(`Delete bed ${bed.bedNumber}?`) && run(() => bedApi.remove(bed.bedNumber, bed.roomNumber), 'Bed deleted')}>Delete this bed</button>
        </div>
      )}
    </Modal>
  )
}
