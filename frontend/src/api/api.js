const API = '/api'

function getToken() {
  return localStorage.getItem('token')
}

export async function request(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }

  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch(`${API}${path}`, { ...options, headers })
  const data = await res.json().catch(() => ({}))

  if (!res.ok) {
    throw new Error(data.message || 'Error en la petición')
  }

  return data
}

export const authApi = {
  login: (username, password) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),
}

export const serviciosApi = {
  list: (activos) => request(`/servicios${activos ? '?activos=true' : ''}`),
  create: (body) => request('/servicios', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => request(`/servicios/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
}

export const ventanillasApi = {
  list: () => request('/ventanillas'),
  create: (body) => request('/ventanillas', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => request(`/ventanillas/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
}

export const prioridadesApi = {
  list: () => request('/prioridades'),
  create: (body) => request('/prioridades', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => request(`/prioridades/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
}

export const turnosApi = {
  crear: (body) => request('/turnos', { method: 'POST', body: JSON.stringify(body) }),
  pendientes: (servicioId) =>
    request(`/turnos/pendientes${servicioId ? `?servicioId=${servicioId}` : ''}`),
  llamados: (servicioId) =>
    request(`/turnos/llamados${servicioId ? `?servicioId=${servicioId}` : ''}`),
  llamar: (body) => request('/turnos/llamar', { method: 'POST', body: JSON.stringify(body) }),
  iniciar: (id) => request(`/turnos/${id}/iniciar`, { method: 'PATCH' }),
  finalizar: (id) => request(`/turnos/${id}/finalizar`, { method: 'PATCH' }),
  cancelar: (id, motivo) =>
    request(`/turnos/${id}/cancelar`, { method: 'PATCH', body: JSON.stringify({ motivo }) }),
  noPresentado: (id) => request(`/turnos/${id}/no-presentado`, { method: 'PATCH' }),
  historial: (id) => request(`/turnos/${id}/historial`),
}

export const dashboardApi = {
  get: (fecha, servicioId) => {
    const params = new URLSearchParams()
    if (fecha) params.append('fecha', fecha)
    if (servicioId) params.append('servicioId', servicioId)
    const q = params.toString()
    return request(`/dashboard${q ? `?${q}` : ''}`)
  },
}
