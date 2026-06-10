import { useEffect, useState } from 'react'
import { serviciosApi, ventanillasApi, prioridadesApi } from '../api/api'

export default function AdminPage() {
  const [tab, setTab] = useState('servicios')
  const [servicios, setServicios] = useState([])
  const [ventanillas, setVentanillas] = useState([])
  const [prioridades, setPrioridades] = useState([])
  const [msg, setMsg] = useState('')
  const [err, setErr] = useState('')

  const [servicio, setServicio] = useState({ nombre: '', descripcion: '', prefijo: '', duracionEstimada: 15, activo: true })
  const [ventanilla, setVentanilla] = useState({ nombre: '', ubicacion: '', servicioIds: [], estado: 'DISPONIBLE', activa: true })
  const [prioridad, setPrioridad] = useState({ nombre: '', peso: 1, tiempoMaximoEspera: 30 })

  const cargar = async () => {
    const [s, v, p] = await Promise.all([serviciosApi.list(), ventanillasApi.list(), prioridadesApi.list()])
    setServicios(s.data || [])
    setVentanillas(v.data || [])
    setPrioridades(p.data || [])
  }

  useEffect(() => { cargar().catch((e) => setErr(e.message)) }, [])

  const guardarServicio = async (e) => {
    e.preventDefault()
    try {
      await serviciosApi.create(servicio)
      setMsg('Servicio creado')
      setServicio({ nombre: '', descripcion: '', prefijo: '', duracionEstimada: 15, activo: true })
      cargar()
    } catch (e) { setErr(e.message) }
  }

  const guardarVentanilla = async (e) => {
    e.preventDefault()
    try {
      await ventanillasApi.create({
        ...ventanilla,
        servicioIds: ventanilla.servicioIds.map(Number),
        estado: ventanilla.estado,
      })
      setMsg('Ventanilla creada')
      setVentanilla({ nombre: '', ubicacion: '', servicioIds: [], estado: 'DISPONIBLE', activa: true })
      cargar()
    } catch (e) { setErr(e.message) }
  }

  const guardarPrioridad = async (e) => {
    e.preventDefault()
    try {
      await prioridadesApi.create(prioridad)
      setMsg('Prioridad creada')
      setPrioridad({ nombre: '', peso: 1, tiempoMaximoEspera: 30 })
      cargar()
    } catch (e) { setErr(e.message) }
  }

  return (
    <div>
      <div className="card">
        <h2>Administración</h2>
        <div className="actions">
          <button className={tab === 'servicios' ? '' : 'secondary'} onClick={() => setTab('servicios')}>Servicios</button>
          <button className={tab === 'ventanillas' ? '' : 'secondary'} onClick={() => setTab('ventanillas')}>Ventanillas</button>
          <button className={tab === 'prioridades' ? '' : 'secondary'} onClick={() => setTab('prioridades')}>Prioridades</button>
        </div>
      </div>

      {msg && <div className="alert ok">{msg}</div>}
      {err && <div className="alert error">{err}</div>}

      {tab === 'servicios' && (
        <div className="grid grid-2">
          <div className="card">
            <h2>Nuevo servicio</h2>
            <form onSubmit={guardarServicio}>
              <label>Nombre</label>
              <input value={servicio.nombre} onChange={(e) => setServicio({ ...servicio, nombre: e.target.value })} required />
              <label>Prefijo (ej. RAD)</label>
              <input value={servicio.prefijo} onChange={(e) => setServicio({ ...servicio, prefijo: e.target.value })} required />
              <label>Duración estimada (min)</label>
              <input type="number" value={servicio.duracionEstimada} onChange={(e) => setServicio({ ...servicio, duracionEstimada: +e.target.value })} required />
              <label>Descripción</label>
              <input value={servicio.descripcion} onChange={(e) => setServicio({ ...servicio, descripcion: e.target.value })} />
              <button type="submit">Crear servicio</button>
            </form>
          </div>
          <div className="card">
            <h2>Servicios registrados</h2>
            <table>
              <thead><tr><th>Nombre</th><th>Prefijo</th><th>Activo</th></tr></thead>
              <tbody>
                {servicios.map((s) => (
                  <tr key={s.id}><td>{s.nombre}</td><td>{s.prefijo}</td><td>{s.activo ? 'Sí' : 'No'}</td></tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {tab === 'ventanillas' && (
        <div className="grid grid-2">
          <div className="card">
            <h2>Nueva ventanilla</h2>
            <form onSubmit={guardarVentanilla}>
              <label>Nombre</label>
              <input value={ventanilla.nombre} onChange={(e) => setVentanilla({ ...ventanilla, nombre: e.target.value })} required />
              <label>Ubicación</label>
              <input value={ventanilla.ubicacion} onChange={(e) => setVentanilla({ ...ventanilla, ubicacion: e.target.value })} />
              <label>Servicios que atiende (IDs separados por coma)</label>
              <input placeholder="1,2,3" onChange={(e) => setVentanilla({
                ...ventanilla,
                servicioIds: e.target.value.split(',').map((x) => x.trim()).filter(Boolean),
              })} required />
              <button type="submit">Crear ventanilla</button>
            </form>
          </div>
          <div className="card">
            <h2>Ventanillas</h2>
            <table>
              <thead><tr><th>Nombre</th><th>Estado</th><th>Servicios</th></tr></thead>
              <tbody>
                {ventanillas.map((v) => (
                  <tr key={v.id}>
                    <td>{v.nombre}</td>
                    <td>{v.estado}</td>
                    <td>{v.servicios?.map((s) => s.nombre).join(', ')}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {tab === 'prioridades' && (
        <div className="grid grid-2">
          <div className="card">
            <h2>Nueva prioridad</h2>
            <form onSubmit={guardarPrioridad}>
              <label>Nombre</label>
              <input value={prioridad.nombre} onChange={(e) => setPrioridad({ ...prioridad, nombre: e.target.value })} required />
              <label>Peso (mayor = más prioritario)</label>
              <input type="number" value={prioridad.peso} onChange={(e) => setPrioridad({ ...prioridad, peso: +e.target.value })} required />
              <label>Tiempo máx. espera (min)</label>
              <input type="number" value={prioridad.tiempoMaximoEspera} onChange={(e) => setPrioridad({ ...prioridad, tiempoMaximoEspera: +e.target.value })} required />
              <button type="submit">Crear prioridad</button>
            </form>
          </div>
          <div className="card">
            <h2>Prioridades</h2>
            <table>
              <thead><tr><th>Nombre</th><th>Peso</th><th>Máx. espera</th></tr></thead>
              <tbody>
                {prioridades.map((p) => (
                  <tr key={p.id}><td>{p.nombre}</td><td>{p.peso}</td><td>{p.tiempoMaximoEspera} min</td></tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}
