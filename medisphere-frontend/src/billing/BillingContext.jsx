import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'

// ---------------------------------------------------------------------------
// There is no pharmacy/billing microservice yet, so this keeps medicines and
// invoices in the browser (localStorage). Everything goes through this one
// file, so when you build a backend you only replace the load/save calls
// below with API calls - the pages do not change.
// ---------------------------------------------------------------------------

const MEDS_KEY = 'ms_medicines'
const INV_KEY = 'ms_invoices'

export const money = (n) => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const r2 = (n) => Math.round((n + Number.EPSILON) * 100) / 100

const SEED = [
  ['Paracetamol', 'Tablet', '500 mg', 2.5, 12, 500, false],
  ['Azithromycin', 'Tablet', '500 mg', 24, 12, 120, true],
  ['Amoxicillin', 'Capsule', '500 mg', 11, 12, 200, true],
  ['Cetirizine', 'Tablet', '10 mg', 3, 12, 300, false],
  ['Pantoprazole', 'Tablet', '40 mg', 9, 12, 250, false],
  ['Metformin', 'Tablet', '500 mg', 2, 12, 400, true],
  ['Amlodipine', 'Tablet', '5 mg', 4, 12, 350, true],
  ['Atorvastatin', 'Tablet', '10 mg', 8, 12, 180, true],
  ['Ibuprofen', 'Tablet', '400 mg', 3.5, 12, 260, false],
  ['Cough syrup', 'Syrup', '100 ml', 85, 12, 60, false],
  ['ORS sachet', 'Sachet', '21 g', 18, 5, 150, false],
  ['Vitamin D3', 'Capsule', '60000 IU', 32, 12, 90, false],
  ['Insulin glargine', 'Injection', '100 IU/ml', 520, 5, 25, true],
  ['Povidone iodine', 'Ointment', '5% · 15 g', 62, 12, 40, false],
].map(([name, form, strength, price, gst, stock, rx], i) => ({
  id: `MED-${String(i + 1).padStart(3, '0')}`, name, form, strength, price, gst, stock, rx,
}))

function read(key, fallback) {
  try { return JSON.parse(localStorage.getItem(key)) ?? fallback } catch { return fallback }
}

// Pure calculation used by the bill screen AND when saving the invoice,
// so the number on screen is always the number that is stored.
// Prices are GST-exclusive. Discount is spread across lines before GST.
export function calcBill(lines, discountType, discountValue) {
  const subtotal = r2(lines.reduce((s, l) => s + l.qty * l.price, 0))
  let discount = discountType === 'PERCENT' ? subtotal * (Number(discountValue) || 0) / 100 : Number(discountValue) || 0
  discount = r2(Math.min(Math.max(discount, 0), subtotal))
  const ratio = subtotal ? discount / subtotal : 0
  const gstByRate = {}
  let gst = 0
  for (const l of lines) {
    const taxable = l.qty * l.price * (1 - ratio)
    const tax = taxable * l.gst / 100
    gstByRate[l.gst] = (gstByRate[l.gst] || 0) + tax
    gst += tax
  }
  gst = r2(gst)
  const exact = subtotal - discount + gst
  const total = Math.round(exact)
  return {
    subtotal, discount, gst, total,
    roundOff: r2(total - exact),
    gstByRate: Object.fromEntries(Object.entries(gstByRate).map(([k, v]) => [k, r2(v)])),
  }
}

const BillingContext = createContext(null)

export function BillingProvider({ children }) {
  const [medicines, setMedicines] = useState(() => read(MEDS_KEY, SEED))
  const [invoices, setInvoices] = useState(() => read(INV_KEY, []))

  useEffect(() => { localStorage.setItem(MEDS_KEY, JSON.stringify(medicines)) }, [medicines])
  useEffect(() => { localStorage.setItem(INV_KEY, JSON.stringify(invoices)) }, [invoices])

  const saveMedicine = useCallback((m) => {
    setMedicines((list) => m.id
      ? list.map((x) => (x.id === m.id ? { ...x, ...m } : x))
      : [...list, { ...m, id: `MED-${String(Date.now()).slice(-6)}` }])
  }, [])
  const removeMedicine = useCallback((id) => setMedicines((l) => l.filter((x) => x.id !== id)), [])
  const resetMedicines = useCallback(() => setMedicines(SEED), [])

  // Creates the invoice and takes the stock out. Returns the saved invoice.
  const createInvoice = useCallback((draft) => {
    const year = new Date().getFullYear()
    const seq = invoices.filter((i) => String(i.id).includes(`-${year}-`)).length + 1
    const saved = { ...draft, id: `INV-${year}-${String(seq).padStart(4, '0')}`, createdAt: new Date().toISOString(), status: 'PAID' }
    setInvoices([saved, ...invoices])
    setMedicines((list) => list.map((m) => {
      const line = draft.items.find((i) => i.medicineId === m.id)
      return line ? { ...m, stock: Math.max(0, m.stock - line.qty) } : m
    }))
    return saved
  }, [invoices])

  // Voiding puts the stock back (the actual refund is a cash-desk action, not tracked here).
  const voidInvoice = useCallback((id, reason) => {
    const inv = invoices.find((i) => i.id === id)
    if (!inv || inv.status === 'VOID') return
    setInvoices(invoices.map((i) => (i.id === id ? { ...i, status: 'VOID', voidReason: reason, voidedAt: new Date().toISOString() } : i)))
    setMedicines((list) => list.map((m) => {
      const line = inv.items.find((x) => x.medicineId === m.id)
      return line ? { ...m, stock: m.stock + line.qty } : m
    }))
  }, [invoices])

  const value = useMemo(() => ({
    medicines, invoices, saveMedicine, removeMedicine, resetMedicines, createInvoice, voidInvoice,
  }), [medicines, invoices, saveMedicine, removeMedicine, resetMedicines, createInvoice, voidInvoice])

  return <BillingContext.Provider value={value}>{children}</BillingContext.Provider>
}

export const useBilling = () => useContext(BillingContext)
