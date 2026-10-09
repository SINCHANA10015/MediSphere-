import { useEffect, useMemo, useState } from 'react'
import { useLocation, useSearchParams } from 'react-router-dom'
import { patientApi } from '../api/client'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../components/Toast'
import { Modal, PageHead, Spinner } from '../components/ui'
import { calcBill, money, useBilling } from '../billing/BillingContext'
import InvoiceModal from '../billing/InvoiceModal'
import InvoicesTab from '../billing/InvoicesTab'
import MedicinesTab from '../billing/MedicinesTab'

const TABS = [['new', 'New bill'], ['invoices', 'Invoices'], ['medicines', 'Medicines']]

export default function Billing() {
  const [tab, setTab] = useState('new')
  return (
    <>
      <PageHead title="Pharmacy billing" subtitle="Bill medicines, take payment and print the invoice" />
      <div className="tabs" role="tablist">
        {TABS.map(([k, label]) => (
          <button key={k} role="tab" aria-selected={tab === k} className={tab === k ? 'on' : ''} onClick={() => setTab(k)}>{label}</button>
        ))}
      </div>
      {tab === 'new' && <NewBill />}
      {tab === 'invoices' && <InvoicesTab />}
      {tab === 'medicines' && <MedicinesTab />}
    </>
  )
}

function NewBill() {
  const { session } = useAuth()
  const toast = useToast()
  const { medicines, createInvoice } = useBilling()
  const [params] = useSearchParams()
  const { state } = useLocation() // may carry { items: [{name, qty}], patientId } from a prescription

  const [patients, setPatients] = useState(null) // null = loading, [] = none / service off
  const [patientId, setPatientId] = useState(params.get('patient') || state?.patientId || '')
  const [walkIn, setWalkIn] = useState({ name: '', email: '' })
  const [lines, setLines] = useState([])
  const [q, setQ] = useState('')
  const [rxOk, setRxOk] = useState(false)
  const [disc, setDisc] = useState({ type: 'PERCENT', value: '' })
  const [pay, setPay] = useState({ method: 'CASH', tendered: '', card: '', upi: '' })
  const [confirming, setConfirming] = useState(false)
  const [done, setDone] = useState(null)

  useEffect(() => { patientApi.list().then(setPatients).catch(() => setPatients([])) }, [])

  // Pre-fill the cart from a prescription (names are matched against the catalogue).
  useEffect(() => {
    if (!state?.items?.length) return
    const found = [], missing = []
    for (const it of state.items) {
      const m = medicines.find((x) => x.name.toLowerCase() === it.name.toLowerCase()) ||
        medicines.find((x) => it.name.toLowerCase().includes(x.name.toLowerCase()))
      m && m.stock > 0 ? found.push({ ...m, medicineId: m.id, qty: Math.min(it.qty || 1, m.stock) }) : missing.push(it.name)
    }
    setLines(found)
    if (found.length) toast.success(`${found.length} prescribed medicine${found.length > 1 ? 's' : ''} added`)
    if (missing.length) toast.error(`Not in stock / not found: ${missing.join(', ')}`)
    // eslint-disable-next-line
  }, [])

  const patient = patients?.find((p) => p.patientId === patientId)
  const totals = useMemo(() => calcBill(lines, disc.type, disc.value), [lines, disc])
  const needsRx = lines.some((l) => l.rx)

  const results = useMemo(() => {
    const t = q.trim().toLowerCase()
    return medicines.filter((m) => !t || `${m.name} ${m.form} ${m.strength}`.toLowerCase().includes(t)).slice(0, 12)
  }, [medicines, q])

  const add = (m) => {
    if (m.stock === 0) return toast.error(`${m.name} is out of stock`)
    setLines((ls) => {
      const ex = ls.find((l) => l.medicineId === m.id)
      if (!ex) return [...ls, { ...m, medicineId: m.id, qty: 1 }]
      if (ex.qty >= m.stock) { toast.error(`Only ${m.stock} of ${m.name} in stock`); return ls }
      return ls.map((l) => (l.medicineId === m.id ? { ...l, qty: l.qty + 1 } : l))
    })
  }
  const setQty = (id, qty) => setLines((ls) => ls.flatMap((l) => {
    if (l.medicineId !== id) return [l]
    const n = Math.min(Math.max(Number(qty) || 0, 0), l.stock)
    return n === 0 ? [] : [{ ...l, qty: n }]
  }))

  const tendered = Number(pay.tendered) || 0
  const problem =
    !lines.length ? 'Add at least one medicine' :
    needsRx && !rxOk ? 'Confirm the prescription has been checked' :
    pay.method === 'CASH' && tendered < totals.total ? `Cash received is less than ${money(totals.total)}` :
    pay.method === 'CARD' && !/^\d{4}$/.test(pay.card) ? 'Enter the last 4 digits of the card' :
    pay.method === 'UPI' && pay.upi.trim().length < 6 ? 'Enter the UPI reference / transaction ID' : ''

  const finish = () => {
    const inv = createInvoice({
      patientId: patientId || null,
      patientName: patient?.patientName || walkIn.name || 'Walk-in customer',
      patientEmail: patient?.patientEmail || walkIn.email || '',
      items: lines.map((l) => ({ medicineId: l.medicineId, name: l.name, form: l.form, strength: l.strength, qty: l.qty, price: l.price, gst: l.gst })),
      totals, method: pay.method,
      tendered: pay.method === 'CASH' ? tendered : null,
      reference: pay.method === 'CARD' ? pay.card : pay.method === 'UPI' ? pay.upi.trim() : '',
      cashier: session.name,
    })
    setConfirming(false); setDone(inv)
    setLines([]); setRxOk(false); setDisc({ type: 'PERCENT', value: '' }); setPay({ method: 'CASH', tendered: '', card: '', upi: '' })
    toast.success(`${inv.id} paid`)
  }

  const quickCash = [totals.total, Math.ceil(totals.total / 100) * 100, Math.ceil(totals.total / 500) * 500]
    .filter((v, i, a) => v > 0 && a.indexOf(v) === i)

  return (
    <div className="bl-grid">
      {/* ---------- left: pick medicines ---------- */}
      <section className="card bl-pick">
        <input autoFocus placeholder="Search medicine (name, form, strength)…" value={q} onChange={(e) => setQ(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter' && results[0]) { add(results[0]); setQ('') } }} />
        <small className="hint">Press Enter to add the first match.</small>
        <ul className="bl-list">
          {results.map((m) => (
            <li key={m.id}>
              <button disabled={m.stock === 0} onClick={() => add(m)}>
                <span><b>{m.name}</b> {m.rx && <span className="pill amber">Rx</span>}<span className="sub">{m.form} · {m.strength}</span></span>
                <span className="bl-price">{money(m.price)}<span className="sub">{m.stock === 0 ? 'Out of stock' : `${m.stock} left`}</span></span>
              </button>
            </li>
          ))}
          {results.length === 0 && <li className="hint" style={{ padding: 14 }}>No medicine matches “{q}”.</li>}
        </ul>
      </section>

      {/* ---------- right: the bill ---------- */}
      <section className="card bl-bill">
        <h2 className="section-title">Bill</h2>

        {patients === null ? <Spinner /> : (
          <div className="form-grid" style={{ marginBottom: 14 }}>
            <label className="field full"><span>Patient</span>
              <select value={patientId} onChange={(e) => setPatientId(e.target.value)}>
                <option value="">Walk-in customer</option>
                {patients.map((p) => <option key={p.patientId} value={p.patientId}>{p.patientName} ({p.patientId})</option>)}
              </select>
              {patients.length === 0 && <small>Patient list unavailable (is the gateway and PatientManagement running?). Billing as walk-in still works.</small>}
            </label>
            {!patientId && <>
              <label className="field"><span>Name</span><input value={walkIn.name} onChange={(e) => setWalkIn({ ...walkIn, name: e.target.value })} /></label>
              <label className="field"><span>Email (for receipt)</span><input type="email" value={walkIn.email} onChange={(e) => setWalkIn({ ...walkIn, email: e.target.value })} /></label>
            </>}
          </div>
        )}

        {lines.length === 0 ? <p className="hint" style={{ padding: '18px 0' }}>Click a medicine on the left to add it.</p> : (
          <table className="bl-lines">
            <tbody>
              {lines.map((l) => (
                <tr key={l.medicineId}>
                  <td>{l.name}<span className="sub">{money(l.price)} · GST {l.gst}%</span></td>
                  <td>
                    <div className="stepper">
                      <button aria-label="Less" onClick={() => setQty(l.medicineId, l.qty - 1)}>−</button>
                      <input aria-label={`${l.name} quantity`} type="number" value={l.qty} onChange={(e) => setQty(l.medicineId, e.target.value)} />
                      <button aria-label="More" onClick={() => setQty(l.medicineId, l.qty + 1)} disabled={l.qty >= l.stock}>+</button>
                    </div>
                  </td>
                  <td className="num">{money(l.qty * l.price)}</td>
                  <td><button className="icon-btn" aria-label={`Remove ${l.name}`} onClick={() => setQty(l.medicineId, 0)}>✕</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {needsRx && <label className="check"><input type="checkbox" checked={rxOk} onChange={(e) => setRxOk(e.target.checked)} /> I have checked the prescription for the Rx medicines</label>}

        <div className="bl-disc">
          <span>Discount</span>
          <select value={disc.type} onChange={(e) => setDisc({ ...disc, type: e.target.value })} style={{ width: 90 }}><option value="PERCENT">%</option><option value="FLAT">₹</option></select>
          <input type="number" min="0" placeholder="0" value={disc.value} onChange={(e) => setDisc({ ...disc, value: e.target.value })} style={{ width: 100 }} />
        </div>

        <dl className="bl-totals">
          <dt>Subtotal</dt><dd>{money(totals.subtotal)}</dd>
          {totals.discount > 0 && <><dt>Discount</dt><dd>− {money(totals.discount)}</dd></>}
          <dt>GST</dt><dd>{money(totals.gst)}</dd>
          {totals.roundOff !== 0 && <><dt>Round off</dt><dd>{money(totals.roundOff)}</dd></>}
          <dt className="grand">To pay</dt><dd className="grand">{money(totals.total)}</dd>
        </dl>

        <div className="methods" role="radiogroup" aria-label="Payment method">
          {[['CASH', 'Cash'], ['CARD', 'Card'], ['UPI', 'UPI']].map(([k, label]) => (
            <button key={k} role="radio" aria-checked={pay.method === k} className={pay.method === k ? 'on' : ''} onClick={() => setPay({ ...pay, method: k })}>{label}</button>
          ))}
        </div>

        {pay.method === 'CASH' && (
          <div style={{ display: 'grid', gap: 8, marginBottom: 12 }}>
            <label className="field"><span>Cash received</span><input type="number" min="0" value={pay.tendered} onChange={(e) => setPay({ ...pay, tendered: e.target.value })} /></label>
            <div className="chips">{quickCash.map((v) => <button key={v} className="btn small ghost" onClick={() => setPay({ ...pay, tendered: String(v) })}>{money(v).replace('.00', '')}</button>)}</div>
            {tendered >= totals.total && totals.total > 0 && <b className="change">Change to return: {money(tendered - totals.total)}</b>}
          </div>
        )}
        {pay.method === 'CARD' && <label className="field" style={{ marginBottom: 12 }}><span>Last 4 digits of card</span><input inputMode="numeric" maxLength={4} value={pay.card} onChange={(e) => setPay({ ...pay, card: e.target.value.replace(/\D/g, '') })} /></label>}
        {pay.method === 'UPI' && <label className="field" style={{ marginBottom: 12 }}><span>UPI transaction ID</span><input value={pay.upi} onChange={(e) => setPay({ ...pay, upi: e.target.value })} placeholder="Shown on the customer's phone" /></label>}

        {problem && lines.length > 0 && <small className="hint" style={{ display: 'block', marginBottom: 8 }}>{problem}</small>}
        <button className="btn bl-pay" disabled={!!problem} onClick={() => setConfirming(true)}>Take payment · {money(totals.total)}</button>
        {lines.length > 0 && <button className="btn small ghost" style={{ marginTop: 8 }} onClick={() => setLines([])}>Clear bill</button>}
      </section>

      {confirming && <ConfirmPayment total={totals.total} method={pay.method} onCancel={() => setConfirming(false)} onPaid={finish} />}
      {done && <InvoiceModal invoice={done} onClose={() => setDone(null)} />}
    </div>
  )
}

// There is no real payment gateway: Card / UPI wait a moment and then succeed.
// Swap the setTimeout for a call to Razorpay / Stripe / a backend endpoint later.
function ConfirmPayment({ total, method, onCancel, onPaid }) {
  const [state, setState] = useState('ask') // ask -> processing
  const go = () => {
    if (method === 'CASH') return onPaid()
    setState('processing')
    setTimeout(onPaid, 1400)
  }
  return (
    <Modal title="Confirm payment" onClose={state === 'processing' ? () => {} : onCancel}>
      <div style={{ textAlign: 'center', padding: '10px 0 6px' }}>
        <div className="big-amount">{money(total)}</div>
        <p className="hint">{method === 'CASH' ? 'Cash received' : method === 'CARD' ? 'Card payment' : 'UPI payment'}</p>
        {state === 'processing' ? <><Spinner /><p className="hint">Waiting for the bank to confirm…</p></> : (
          <div className="form-foot" style={{ justifyContent: 'center' }}>
            <button className="btn ghost" onClick={onCancel}>Back</button>
            <button className="btn" autoFocus onClick={go}>{method === 'CASH' ? 'Mark as paid' : 'Confirm payment'}</button>
          </div>
        )}
        <small className="hint">Demo mode: no money moves - payments are recorded in this browser only.</small>
      </div>
    </Modal>
  )
}
