import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Layout() {
  const { user, logout, hasRole } = useAuth()

  return (
    <div className="layout">
      <aside className="sidebar">
        <h1>Multiservicio</h1>
        <p style={{ fontSize: 12, opacity: 0.8, marginBottom: 12 }}>
          {user?.nombre} · <span className="badge">{user?.rol}</span>
        </p>

        {hasRole('ADMIN') && (
          <>
            <NavLink to="/dashboard">Dashboard</NavLink>
            <NavLink to="/admin">Administración</NavLink>
          </>
        )}
        {hasRole('RECEPCION', 'ADMIN') && (
          <NavLink to="/recepcion">Recepción</NavLink>
        )}
        {hasRole('OPERADOR', 'ADMIN') && (
          <NavLink to="/operador">Operador</NavLink>
        )}
        <NavLink to="/sala">Sala de espera</NavLink>
        <NavLink to="/historial">Historial</NavLink>

        <button className="nav-btn" onClick={logout} style={{ marginTop: 'auto' }}>
          Cerrar sesión
        </button>
      </aside>
      <main className="main">
        <Outlet />
      </main>
    </div>
  )
}
