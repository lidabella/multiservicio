import { useEffect, useState } from 'react'
import { serviciosApi, ventanillasApi, turnosApi } from '../api/api'

export default function OperadorPage() {
  const [servicios, setServicios] = useState([])
  const [ventanillas, setVentanillas] = useState([])
  const [turnoActual, setTurnoActual] = useState(null)
  const [form, setForm] = useState({ servicioId: '', ventanillaId: '' })
  const [motivo, setMotivo] = useState('')
  const [msg, setMsg] = useState('')
  const [err, setErr] = useState('')

  useEffect(() => {
    Promise.all([serviciosApi.list(true), ventanillasApi.list()])
      .then(([s, v]) => {
        setServicios(s.data || [])
        setVentanillas(v.data || [])
      })
      .catch((e) => setErr(e.message))
  }, [])

  const llamar = async () => {
    try {
      setErr('')
      const res = await turnosApi.llamar({
        servicioId: Number(form.servicioId),
        ventanillaId: Number(form.ventanillaId),
      })
      setTurnoActual(res.data)
      setMsg(`Llamado: ${res.data.codigo}`)
    } catch (e) { setErr(e.message) }
  }

  const accion = async (fn, texto) => {
    if (!turnoActual) return
    try {
      setErr('')
      const res = await fn(turnoActual.id)
      setTurnoActual(res.data)
      setMsg(texto)
      if (res.data.estado === 'FINALIZADO' || res.data.estado === 'NO_PRESENTADO' || res.data.estado === 'CANCELADO') {
        setTurnoActual(null)
      }
    } catch (e) { setErr(e.message) }
  }

  return (
    <div>
      <div className="card">
        <h2>Operador — Atención de turnos</h2>
        {msg && <div className="alert ok">{msg}</div>}
        {err && <div className="alert error">{err}</div>}

        <div className="grid grid-2">
          <div>
            <label>Servicio</label>
            <select value={form.servicioId} onChange={(e) => setForm({ ...form, servicioId: e.target.value })}>
              <option value="">Seleccione...</option>
              {servicios.map((s) => <option key={s.id} value={s.id}>{s.nombre}</option>)}
            </select>
          </div>
          <div>
            <label>Ventanilla</label>
            <select value={form.ventanillaId} onChange={(e) => setForm({ ...form, ventanillaId: e.target.value })}>
              <option value="">Seleccione...</option>
              {ventanillas.filter((v) => v.estado === 'DISPONIBLE').map((v) => (
                <option key={v.id} value={v.id}>{v.nombre} ({v.estado})</option>
              ))}
            </select>
          </div>
        </div>
        <button onClick={llamar} disabled={!form.servicioId || !form.ventanillaId}>
          Llamar siguiente turno
        </button>
      </div>

      {turnoActual && (
        <div className="card">
          <h2>Turno actual: {turnoActual.codigo}</h2>
          <p>Servicio: {turnoActual.servicioNombre} · Prioridad: {turnoActual.prioridadNombre}</p>
          <p>Estado: <span className="badge">{turnoActual.estado}</span></p>
          <div className="actions">
            {turnoActual.estado === 'LLAMADO' && (
              <>
                <button className="success" onClick={() => accion(turnosApi.iniciar, 'Atención iniciada')}>Iniciar atención</button>
                <button className="danger" onClick={() => accion(turnosApi.noPresentado, 'Marcado no presentado')}>No presentado</button>
              </>
            )}
            {turnoActual.estado === 'EN_ATENCION' && (
              <button className="success" onClick={() => accion(turnosApi.finalizar, 'Atención finalizada')}>Finalizar</button>
            )}
            <div style={{ width: '100%', marginTop: 8 }}>
              <label>Motivo cancelación</label>
              <input value={motivo} onChange={(e) => setMotivo(e.target.value)} placeholder="Motivo..." />
              <button className="danger" onClick={() => accion((id) => turnosApi.cancelar(id, motivo), 'Turno cancelado')}>
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
