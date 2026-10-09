import { useMemo, useState } from 'react'
import { Empty, Modal, Field } from '../components/ui'
import { useToast } from '../components/Toast'
import { useBilling, money } from './BillingContext'
import InvoiceModal from './InvoiceModal'

const dayKey = (iso) => new Date(iso).toDateString()

export default function InvoicesTab() {
  const { invoices, voidInvoice } = useBilling()
  const toast = useToast()
  const [q, setQ] = useState('')
  const [status, setStatus] = useState('ALL')
  const [open, setOpen] = useState(null)
  const [voiding, setVoiding] = useState(null)
  const [reason, setReason] = useState('')

  const shown = useMemo(() => {
    const t = q.trim().toLowerCase()
    return invoices.filter((i) => (status === 'ALL' || i.status === status) &&
      (!t || [i.id, i.patientName, i.patientId].some((v) => String(v || '').toLowerCase().includes(t))))
  }, [invoices, q, status])

  const paid = invoices.filter((i) => i.status === 'PAID')
  const todayPaid = paid.filter((i) => dayKey(i.createdAt) === new Date().toDateString())
  const sum = (l) => l.reduce((s, i) => s + i.totals.total, 0)
  const byMethod = (m) => sum(todayPaid.filter((i) => i.method === m))

  const doVoid = (e) => {
    e.preventDefault()
    voidInvoice(voiding.id, reason)
    toast.success(`${voiding.id} voided - stock returned`)
    setVoiding(null); setReason('')
  }

  return (
    <>
      <div className="stats">
        <div className="card stat"><b>{money(sum(todayPaid))}</b><span>Today's sales · {todayPaid.length} bills</span></div>
        <div className="card stat"><b>{money(byMethod('CASH'))}</b><span>Cash today</span></div>
        <div className="card stat"><b>{money(byMethod('CARD'))}</b><span>Card today</span></div>
        <div className="card stat"><b>{money(byMethod('UPI'))}</b><span>UPI today</span></div>
      </div>

      <div className="page-actions" style={{ marginBottom: 14 }}>
        <input className="search" placeholder="Search invoice, patient, ID" value={q} onChange={(e) => setQ(e.target.value)} />
        <select value={status} onChange={(e) => setStatus(e.target.value)} style={{ width: 'auto' }}>
          <option value="ALL">All</option><option value="PAID">Paid</option><option value="VOID">Void</option>
        </select>
      </div>

      <div className="card table-wrap">
        {shown.length === 0 ? <Empty title="No invoices" text={invoices.length ? 'Nothing matches that search.' : 'Bills you create will show up here.'} /> : (
          <table>
            <thead><tr><th>Invoice</th><th>Patient</th><th>Method</th><th className="num">Total</th><th>Status</th><th></th></tr></thead>
            <tbody>
              {shown.map((i) => (
                <tr key={i.id}>
                  <td>{i.id}<span className="sub">{new Date(i.createdAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}</span></td>
                  <td>{i.patientName}<span className="sub id">{i.patientId || 'Walk-in'}</span></td>
                  <td>{i.method}</td>
                  <td className="num">{money(i.totals.total)}</td>
                  <td><span className={`pill ${i.status === 'PAID' ? 'green' : 'red'}`}>{i.status === 'PAID' ? 'Paid' : 'Void'}</span></td>
                  <td><div className="row-actions">
                    <button className="btn small ghost" onClick={() => setOpen(i)}>View</button>
                    {i.status === 'PAID' && <button className="btn small danger-ghost" onClick={() => setVoiding(i)}>Void</button>}
                  </div></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {open && <InvoiceModal invoice={open} onClose={() => setOpen(null)} />}
      {voiding && (
        <Modal title={`Void ${voiding.id}?`} onClose={() => setVoiding(null)}>
          <form onSubmit={doVoid} style={{ display: 'grid', gap: 14 }}>
            <p style={{ margin: 0, color: 'var(--muted)' }}>The stock goes back into the pharmacy. Hand the {money(voiding.totals.total)} refund over at the cash desk.</p>
            <Field label="Reason"><input required autoFocus value={reason} onChange={(e) => setReason(e.target.value)} placeholder="e.g. wrong medicine billed" /></Field>
            <div className="form-foot" style={{ marginTop: 0 }}>
              <button type="button" className="btn ghost" onClick={() => setVoiding(null)}>Keep invoice</button>
              <button className="btn danger">Void invoice</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  )
}
