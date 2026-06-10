import { useEffect, useState } from 'react'
import { dashboardApi, serviciosApi } from '../api/api'

export default function DashboardPage() {
  const [data, setData] = useState(null)
  const [servicios, setServicios] = useState([])
  const [fecha, setFecha] = useState(new Date().toISOString().slice(0, 10))
  const [servicioId, setServicioId] = useState('')
  const [error, setError] = useState('')

  const cargar = async () => {
    try {
      setError('')
      const res = await dashboardApi.get(fecha, servicioId || undefined)
      setData(res.data)
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    serviciosApi.list().then((r) => setServicios(r.data || []))
    cargar()
  }, [])

  return (
    <div>
      <div className="card">
        <h2>Dashboard — Indicadores reales</h2>
        <div className="grid grid-2">
          <div>
            <label>Fecha</label>
            <input type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} />
          </div>
          <div>
            <label>Servicio</label>
            <select value={servicioId} onChange={(e) => setServicioId(e.target.value)}>
              <option value="">Todos los servicios</option>
              {servicios.map((s) => (
                <option key={s.id} value={s.id}>{s.nombre}</option>
              ))}
            </select>
          </div>
        </div>
        <button onClick={cargar}>Consultar</button>
      </div>

      {error && <div className="alert error">{error}</div>}

      {data && (
        <>
          <div className="grid grid-4">
            <div className="stat"><div className="num">{data.pendientes}</div><div className="lbl">Pendientes</div></div>
            <div className="stat"><div className="num">{data.atendidos}</div><div className="lbl">Atendidos</div></div>
            <div className="stat"><div className="num">{data.cancelados}</div><div className="lbl">Cancelados</div></div>
            <div className="stat"><div className="num">{data.noPresentados}</div><div className="lbl">No presentados</div></div>
          </div>
          <div className="grid grid-4" style={{ marginTop: 16 }}>
            <div className="stat"><div className="num">{data.llamados}</div><div className="lbl">Llamados</div></div>
            <div className="stat"><div className="num">{data.enAtencion}</div><div className="lbl">En atención</div></div>
            <div className="stat"><div className="num">{data.promedioEsperaMinutos} min</div><div className="lbl">Prom. espera</div></div>
            <div className="stat"><div className="num">{data.promedioAtencionMinutos} min</div><div className="lbl">Prom. atención</div></div>
          </div>
          <div className="card" style={{ marginTop: 16 }}>
            <p>Total turnos: <strong>{data.totalTurnos}</strong></p>
            {data.servicioNombre && <p>Servicio: <strong>{data.servicioNombre}</strong></p>}
          </div>
        </>
      )}
    </div>
  )
}
