import { useMemo, useState } from 'react'
import { Empty, Field, Modal } from '../components/ui'
import { useToast } from '../components/Toast'
import { useBilling, money } from './BillingContext'

const LOW = 30
const blank = { name: '', form: 'Tablet', strength: '', price: '', gst: 12, stock: '', rx: false }

export default function MedicinesTab() {
  const { medicines, saveMedicine, removeMedicine, resetMedicines } = useBilling()
  const toast = useToast()
  const [q, setQ] = useState('')
  const [lowOnly, setLowOnly] = useState(false)
  const [edit, setEdit] = useState(null)

  const shown = useMemo(() => medicines.filter((m) =>
    (!lowOnly || m.stock <= LOW) && (!q.trim() || `${m.name} ${m.form}`.toLowerCase().includes(q.trim().toLowerCase()))), [medicines, q, lowOnly])
  const lowCount = medicines.filter((m) => m.stock <= LOW).length

  const submit = (e) => {
    e.preventDefault()
    saveMedicine({ ...edit, price: Number(edit.price), stock: Number(edit.stock), gst: Number(edit.gst) })
    toast.success(edit.id ? 'Medicine updated' : 'Medicine added')
    setEdit(null)
  }
  const set = (k) => (e) => setEdit({ ...edit, [k]: e.target.type === 'checkbox' ? e.target.checked : e.target.value })

  return (
    <>
      <div className="page-actions" style={{ marginBottom: 14 }}>
        <input className="search" placeholder="Search medicines" value={q} onChange={(e) => setQ(e.target.value)} />
        <label className="check"><input type="checkbox" checked={lowOnly} onChange={(e) => setLowOnly(e.target.checked)} /> Low stock only ({lowCount})</label>
        <button className="btn" onClick={() => setEdit(blank)}>Add medicine</button>
        <button className="btn ghost" onClick={() => window.confirm('Restore the sample medicine list and stock?') && (resetMedicines(), toast.success('Sample list restored'))}>Reset</button>
      </div>
      <div className="card table-wrap">
        {shown.length === 0 ? <Empty title="No medicines" text="Add one, or clear the filter." /> : (
          <table>
            <thead><tr><th>Medicine</th><th className="num">Price</th><th className="num">GST</th><th className="num">Stock</th><th></th></tr></thead>
            <tbody>
              {shown.map((m) => (
                <tr key={m.id}>
                  <td>{m.name} {m.rx && <span className="pill amber">Rx</span>}<span className="sub">{m.form} · {m.strength}</span></td>
                  <td className="num">{money(m.price)}</td><td className="num">{m.gst}%</td>
                  <td className="num"><span className={`pill ${m.stock === 0 ? 'red' : m.stock <= LOW ? 'amber' : 'green'}`}>{m.stock === 0 ? 'Out' : m.stock}</span></td>
                  <td><div className="row-actions">
                    <button className="btn small ghost" onClick={() => setEdit(m)}>Edit</button>
                    <button className="btn small danger-ghost" onClick={() => window.confirm(`Remove ${m.name}?`) && removeMedicine(m.id)}>Remove</button>
                  </div></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {edit && (
        <Modal title={edit.id ? `Edit ${edit.name}` : 'Add medicine'} onClose={() => setEdit(null)}>
          <form onSubmit={submit}>
            <div className="form-grid">
              <Field label="Name"><input required value={edit.name} onChange={set('name')} /></Field>
              <Field label="Form">
                <select value={edit.form} onChange={set('form')}>{['Tablet', 'Capsule', 'Syrup', 'Injection', 'Ointment', 'Sachet', 'Drops'].map((f) => <option key={f}>{f}</option>)}</select>
              </Field>
              <Field label="Strength"><input required value={edit.strength} onChange={set('strength')} placeholder="500 mg" /></Field>
              <Field label="Price (₹, excl. GST)"><input type="number" min="0" step="0.01" required value={edit.price} onChange={set('price')} /></Field>
              <Field label="GST"><select value={edit.gst} onChange={set('gst')}>{[0, 5, 12, 18].map((g) => <option key={g} value={g}>{g}%</option>)}</select></Field>
              <Field label="Stock"><input type="number" min="0" step="1" required value={edit.stock} onChange={set('stock')} /></Field>
              <label className="check full"><input type="checkbox" checked={edit.rx} onChange={set('rx')} /> Needs a prescription (Rx)</label>
            </div>
            <div className="form-foot">
              <button type="button" className="btn ghost" onClick={() => setEdit(null)}>Cancel</button>
              <button className="btn">Save</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  )
}
