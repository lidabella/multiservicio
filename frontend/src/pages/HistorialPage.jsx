import { useState } from 'react'
import { turnosApi } from '../api/api'

export default function HistorialPage() {
  const [turnoId, setTurnoId] = useState('')
  const [historial, setHistorial] = useState([])
  const [err, setErr] = useState('')

  const buscar = async (e) => {
    e.preventDefault()
    try {
      setErr('')
      const res = await turnosApi.historial(turnoId)
      setHistorial(res.data || [])
    } catch (e) {
      setErr(e.message)
      setHistorial([])
    }
  }

  return (
    <div>
      <div className="card">
        <h2>Historial / Auditoría del turno</h2>
        <form onSubmit={buscar}>
          <label>ID del turno</label>
          <input type="number" value={turnoId} onChange={(e) => setTurnoId(e.target.value)} required />
          <button type="submit">Consultar historial</button>
        </form>
        {err && <div className="alert error">{err}</div>}
      </div>

      {historial.length > 0 && (
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>Fecha</th><th>Hora</th><th>Usuario</th><th>Ventanilla</th>
                <th>Estado anterior</th><th>Estado nuevo</th><th>Observación</th>
              </tr>
            </thead>
            <tbody>
              {historial.map((h) => (
                <tr key={h.id}>
                  <td>{h.fecha}</td>
                  <td>{h.horaInicio}</td>
                  <td>{h.usuario}</td>
                  <td>{h.ventanilla}</td>
                  <td>{h.estadoAnterior || '-'}</td>
                  <td><strong>{h.estadoNuevo}</strong></td>
                  <td>{h.observaciones}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
