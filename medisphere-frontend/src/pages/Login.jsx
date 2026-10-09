import { useState } from 'react'
import { useAuth, ROLES } from '../context/AuthContext'
import { errorMessage } from '../api/client'
import { Field } from '../components/ui'

export default function Login() {
  const { login, demoLogin } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await login(email, password)
    } catch (err) {
      if (err.response?.status === 404) {
        setError('The backend has no login endpoint yet (POST /staff/login). Use demo mode below to explore the app.')
      } else {
        setError(errorMessage(err))
      }
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="login">
      <section className="login-art">
        <div className="brand">
          <span className="brand-mark">M</span>
          <div><strong>MediSphere</strong><small>Rainbow Hospitals</small></div>
        </div>
        <div>
          <h1>Every ward, one view.</h1>
          <p>Patients, appointments, rooms and staff for the whole hospital, in one place.</p>
        </div>
        <small>Hospital management system</small>
      </section>

      <section className="login-form">
        <div className="login-box">
          <h2>Sign in</h2>
          {error && <div className="banner err" role="alert">{error}</div>}
          <form onSubmit={submit} style={{ display: 'grid', gap: 14 }}>
            <Field label="Work email">
              <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="username" />
            </Field>
            <Field label="Password">
              <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="current-password" />
            </Field>
            <button className="btn" disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>
          </form>

          <div className="demo">
            <p>Backend login is not built yet. Explore the app as:</p>
            <div className="demo-row">
              {ROLES.map((r) => (
                <button key={r} className="btn small ghost" onClick={() => demoLogin(r)}>
                  {r.charAt(0) + r.slice(1).toLowerCase()}
                </button>
              ))}
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}
