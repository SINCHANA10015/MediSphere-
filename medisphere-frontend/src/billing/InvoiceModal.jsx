import { Fragment, useState } from 'react'
import { Modal } from '../components/ui'
import { useToast } from '../components/Toast'
import { notificationApi, errorMessage } from '../api/client'
import { money } from './BillingContext'

const METHOD = { CASH: 'Cash', CARD: 'Card', UPI: 'UPI' }
const when = (iso) => new Date(iso).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })

function receiptText(inv) {
  const lines = inv.items.map((i) => `  ${i.name} ${i.strength || ''} x ${i.qty}  =  ${money(i.qty * i.price)}`).join('\n')
  return [
    `Dear ${inv.patientName},`, '',
    `Thank you for your purchase at Rainbow Hospitals Pharmacy.`, '',
    `Invoice: ${inv.id}`, `Date: ${when(inv.createdAt)}`, '', lines, '',
    `Subtotal: ${money(inv.totals.subtotal)}`,
    inv.totals.discount ? `Discount: -${money(inv.totals.discount)}` : null,
    `GST: ${money(inv.totals.gst)}`,
    `Total paid: ${money(inv.totals.total)} (${METHOD[inv.method]})`, '',
    'Get well soon,', 'Rainbow Hospitals',
  ].filter((l) => l !== null).join('\n')
}

// Printable invoice. "Print" uses the browser dialog - choose "Save as PDF" to get a PDF.
export default function InvoiceModal({ invoice: inv, onClose }) {
  const toast = useToast()
  const [email, setEmail] = useState(inv.patientEmail || '')
  const [sending, setSending] = useState(false)

  const send = async () => {
    setSending(true)
    try {
      // Goes gateway -> /api/notifications/send -> NotificationService (SendGrid)
      await notificationApi.send({ to: email, subject: `Your invoice ${inv.id} - Rainbow Hospitals`, body: receiptText(inv) })
      toast.success(`Receipt emailed to ${email}`)
    } catch (e) { toast.error(errorMessage(e)) } finally { setSending(false) }
  }

  return (
    <Modal title={`Invoice ${inv.id}`} onClose={onClose} wide>
      <div className="invoice-print">
        <div className="inv-head">
          <div>
            <h3>Rainbow Hospitals · Pharmacy</h3>
            <small>Tax invoice</small>
          </div>
          <div className="inv-meta">
            <b>{inv.id}</b>
            <small>{when(inv.createdAt)}</small>
            {inv.status === 'VOID' && <span className="pill red">VOID</span>}
          </div>
        </div>
        <p className="inv-to">
          <b>{inv.patientName}</b>{inv.patientId ? <span className="id"> · {inv.patientId}</span> : ' (walk-in)'}
          {inv.patientEmail && <small style={{ display: 'block', color: 'var(--muted)' }}>{inv.patientEmail}</small>}
        </p>
        <table className="inv-table">
          <thead><tr><th>Medicine</th><th className="num">Qty</th><th className="num">Rate</th><th className="num">GST</th><th className="num">Amount</th></tr></thead>
          <tbody>
            {inv.items.map((i) => (
              <tr key={i.medicineId}>
                <td>{i.name}<span className="sub">{i.form} · {i.strength}</span></td>
                <td className="num">{i.qty}</td><td className="num">{money(i.price)}</td><td className="num">{i.gst}%</td>
                <td className="num">{money(i.qty * i.price)}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <dl className="inv-totals">
          <dt>Subtotal</dt><dd>{money(inv.totals.subtotal)}</dd>
          {inv.totals.discount > 0 && <><dt>Discount</dt><dd>− {money(inv.totals.discount)}</dd></>}
          {Object.entries(inv.totals.gstByRate).map(([rate, amt]) => <Fragment key={rate}><dt>GST @ {rate}%</dt><dd>{money(amt)}</dd></Fragment>)}
          {inv.totals.roundOff !== 0 && <><dt>Round off</dt><dd>{money(inv.totals.roundOff)}</dd></>}
          <dt className="grand">Total</dt><dd className="grand">{money(inv.totals.total)}</dd>
        </dl>
        <p className="inv-pay">
          Paid by <b>{METHOD[inv.method]}</b>
          {inv.method === 'CASH' && inv.tendered ? ` · received ${money(inv.tendered)}, change ${money(inv.tendered - inv.totals.total)}` : ''}
          {inv.method === 'CARD' && inv.reference ? ` · card ending ${inv.reference}` : ''}
          {inv.method === 'UPI' && inv.reference ? ` · ref ${inv.reference}` : ''}
          {inv.status === 'VOID' && <span style={{ color: 'var(--red)' }}> · voided: {inv.voidReason}</span>}
        </p>
        <small className="inv-foot">Rates are shown before GST; tax is added in the totals. Computer-generated invoice.</small>
      </div>

      <div className="form-foot no-print" style={{ alignItems: 'center', flexWrap: 'wrap' }}>
        <input type="email" placeholder="Email address" value={email} onChange={(e) => setEmail(e.target.value)} style={{ maxWidth: 240 }} />
        <button className="btn ghost" disabled={!email || sending} onClick={send}>{sending ? 'Sending…' : 'Email receipt'}</button>
        <button className="btn" onClick={() => window.print()}>Print / Save PDF</button>
      </div>
    </Modal>
  )
}
