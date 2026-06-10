import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('1234')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await login(username, password)
      if (user.rol === 'ADMIN') navigate('/dashboard')
      else if (user.rol === 'RECEPCION') navigate('/recepcion')
      else if (user.rol === 'OPERADOR') navigate('/operador')
      else navigate('/sala')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <form className="login-box" onSubmit={handleSubmit}>
        <h1>Multiservicio</h1>
        {error && <div className="alert error">{error}</div>}
        <label>Usuario</label>
        <input value={username} onChange={(e) => setUsername(e.target.value)} required />
        <label>Contraseña</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        <button type="submit" disabled={loading} style={{ width: '100%' }}>
          {loading ? 'Entrando...' : 'Iniciar sesión'}
        </button>
        <p style={{ fontSize: 12, color: '#64748b', marginTop: 16, textAlign: 'center' }}>
          admin / operador / recepcion — contraseña: 1234
        </p>
      </form>
    </div>
  )
}
