import { createContext, useContext, useState, useCallback } from 'react'
import api from '../api/axios'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [email, setEmail] = useState(() => localStorage.getItem('email'))

  const login = useCallback(async (loginEmail, password) => {
    const response = await api.post('/auth/login', { email: loginEmail, password })
    const { token: newToken } = response.data
    localStorage.setItem('token', newToken)
    localStorage.setItem('email', loginEmail)
    setToken(newToken)
    setEmail(loginEmail)
  }, [])

  const register = useCallback(async (regEmail, password) => {
    await api.post('/auth/register', { email: regEmail, password })
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('email')
    setToken(null)
    setEmail(null)
  }, [])

  const value = {
    token,
    email,
    isAuthenticated: !!token,
    login,
    register,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
