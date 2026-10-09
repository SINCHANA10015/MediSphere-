import { createContext, useContext, useState } from 'react'
import { staffApi } from '../api/client'

// Roles used by the backend README: ADMIN, DOCTOR, RECEPTIONIST
export const ROLES = ['ADMIN', 'RECEPTIONIST', 'DOCTOR']

const AuthContext = createContext(null)

function loadSession() {
  try {
    return JSON.parse(localStorage.getItem('ms_session')) || null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [session, setSession] = useState(loadSession)

  const save = (s) => {
    setSession(s)
    localStorage.setItem('ms_session', JSON.stringify(s))
    if (s.token) localStorage.setItem('ms_token', s.token)
  }

  // Real login - works once the backend has POST /staff/login returning
  // { token, expiresInMillis, role, staffId, requirePasswordReset }
  const login = async (email, password) => {
    const res = await staffApi.login(email, password)
    save({
      token: res.token,
      role: String(res.role || '').toUpperCase(),
      staffId: res.staffId,
      name: email,
      demo: false,
    })
  }

  // Demo login - lets you explore every screen before the backend has auth.
  const demoLogin = (role) => save({ token: null, role, staffId: 'DEMO', name: `Demo ${role.toLowerCase()}`, demo: true })

  const logout = () => {
    setSession(null)
    localStorage.removeItem('ms_session')
    localStorage.removeItem('ms_token')
  }

  return <AuthContext.Provider value={{ session, login, demoLogin, logout }}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
