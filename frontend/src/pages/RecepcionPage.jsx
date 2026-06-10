import { useEffect, useState } from 'react'
import { serviciosApi, prioridadesApi, turnosApi } from '../api/api'

export default function RecepcionPage() {
  const [servicios, setServicios] = useState([])
  const [prioridades, setPrioridades] = useState([])
  const [pendientes, setPendientes] = useState([])
  const [form, setForm] = useState({ servicioId: '', prioridadId: '' })
  const [filtroServicio, setFiltroServicio] = useState('')
  const [msg, setMsg] = useState('')
  const [err, setErr] = useState('')

  const cargar = async () => {
    const [s, p, t] = await Promise.all([
      serviciosApi.list(true),
      prioridadesApi.list(),
      turnosApi.pendientes(filtroServicio || undefined),
    ])
    setServicios(s.data || [])
    setPrioridades(p.data || [])
    setPendientes(t.data || [])
  }

  useEffect(() => { cargar().catch((e) => setErr(e.message)) }, [filtroServicio])

  const crearTurno = async (e) => {
    e.preventDefault()
    try {
      const res = await turnosApi.crear({
        servicioId: Number(form.servicioId),
        prioridadId: Number(form.prioridadId),
      })
      setMsg(`Turno creado: ${res.data.codigo}`)
      cargar()
    } catch (e) { setErr(e.message) }
  }

  return (
    <div>
      <div className="card">
        <h2>Recepción — Crear turno</h2>
        {msg && <div className="alert ok">{msg}</div>}
        {err && <div className="alert error">{err}</div>}
        <form onSubmit={crearTurno}>
          <label>Servicio</label>
          <select value={form.servicioId} onChange={(e) => setForm({ ...form, servicioId: e.target.value })} required>
            <option value="">Seleccione...</option>
            {servicios.map((s) => <option key={s.id} value={s.id}>{s.nombre} ({s.prefijo})</option>)}
          </select>
          <label>Prioridad</label>
          <select value={form.prioridadId} onChange={(e) => setForm({ ...form, prioridadId: e.target.value })} required>
            <option value="">Seleccione...</option>
            {prioridades.map((p) => <option key={p.id} value={p.id}>{p.nombre} (peso {p.peso})</option>)}
          </select>
          <button type="submit">Crear turno</button>
        </form>
      </div>

      <div className="card">
        <h2>Turnos pendientes</h2>
        <label>Filtrar por servicio</label>
        <select value={filtroServicio} onChange={(e) => setFiltroServicio(e.target.value)}>
          <option value="">Todos</option>
          {servicios.map((s) => <option key={s.id} value={s.id}>{s.nombre}</option>)}
        </select>
        <table>
          <thead><tr><th>Código</th><th>Servicio</th><th>Prioridad</th><th>Estado</th></tr></thead>
          <tbody>
            {pendientes.map((t) => (
              <tr key={t.id}>
                <td><strong>{t.codigo}</strong></td>
                <td>{t.servicioNombre}</td>
                <td>{t.prioridadNombre}</td>
                <td><span className="badge">{t.estado}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
