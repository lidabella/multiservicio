import { useEffect, useState } from 'react'
import { serviciosApi, turnosApi } from '../api/api'

export default function SalaPage() {
  const [servicios, setServicios] = useState([])
  const [filtro, setFiltro] = useState('')
  const [pendientes, setPendientes] = useState([])
  const [llamados, setLlamados] = useState([])

  const cargar = () => {
    const sid = filtro || undefined
    Promise.all([turnosApi.pendientes(sid), turnosApi.llamados(sid)])
      .then(([p, l]) => {
        setPendientes(p.data || [])
        setLlamados(l.data || [])
      })
  }

  useEffect(() => {
    serviciosApi.list(true).then((r) => setServicios(r.data || []))
  }, [])

  useEffect(() => { cargar() }, [filtro])

  useEffect(() => {
    const id = setInterval(cargar, 10000)
    return () => clearInterval(id)
  }, [filtro])

  return (
    <div>
      <div className="card">
        <h2>Sala de espera</h2>
        <label>Filtrar servicio</label>
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
          <option value="">Todos</option>
          {servicios.map((s) => <option key={s.id} value={s.id}>{s.nombre}</option>)}
        </select>
      </div>

      <div className="grid grid-2">
        <div className="card">
          <h2>Pendientes ({pendientes.length})</h2>
          <table>
            <thead><tr><th>Código</th><th>Servicio</th><th>Prioridad</th></tr></thead>
            <tbody>
              {pendientes.map((t) => (
                <tr key={t.id}><td>{t.codigo}</td><td>{t.servicioNombre}</td><td>{t.prioridadNombre}</td></tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="card">
          <h2>Últimos llamados</h2>
          <table>
            <thead><tr><th>Código</th><th>Ventanilla</th><th>Estado</th></tr></thead>
            <tbody>
              {llamados.map((t) => (
                <tr key={t.id}>
                  <td><strong>{t.codigo}</strong></td>
                  <td>{t.ventanillaNombre || '-'}</td>
                  <td><span className="badge">{t.estado}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
