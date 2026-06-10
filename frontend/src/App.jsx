import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import AdminPage from './pages/AdminPage'
import RecepcionPage from './pages/RecepcionPage'
import OperadorPage from './pages/OperadorPage'
import SalaPage from './pages/SalaPage'
import HistorialPage from './pages/HistorialPage'

function PrivateRoute({ children, roles }) {
  const { user, hasRole } = useAuth()
  if (!user) return <Navigate to="/login" />
  if (roles && !hasRole(...roles)) return <Navigate to="/sala" />
  return children
}

export default function App() {
  const { user } = useAuth()

  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/sala" /> : <LoginPage />} />
      <Route element={<PrivateRoute><Layout /></PrivateRoute>}>
        <Route path="/dashboard" element={<PrivateRoute roles={['ADMIN']}><DashboardPage /></PrivateRoute>} />
        <Route path="/admin" element={<PrivateRoute roles={['ADMIN']}><AdminPage /></PrivateRoute>} />
        <Route path="/recepcion" element={<PrivateRoute roles={['RECEPCION', 'ADMIN']}><RecepcionPage /></PrivateRoute>} />
        <Route path="/operador" element={<PrivateRoute roles={['OPERADOR', 'ADMIN']}><OperadorPage /></PrivateRoute>} />
        <Route path="/sala" element={<SalaPage />} />
        <Route path="/historial" element={<HistorialPage />} />
        <Route path="*" element={<Navigate to={user ? '/sala' : '/login'} />} />
      </Route>
    </Routes>
  )
}
