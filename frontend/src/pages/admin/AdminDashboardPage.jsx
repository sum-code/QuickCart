import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'

function AdminDashboardPage() {
  const navigate = useNavigate()
  const user = useAuthStore((state) => state.user)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  const logout = () => {
    clearAuth()
    navigate('/auth/login', { replace: true })
  }

  return (
    <main className="min-h-screen bg-slate-950 px-6 py-10 text-white">
      <section className="mx-auto max-w-5xl rounded-3xl border border-white/10 bg-slate-900/80 p-8 shadow-premium">
        <p className="text-xs uppercase tracking-[0.16em] text-brand-copper">Admin Control Center</p>
        <h1 className="mt-2 text-3xl font-semibold">Dashboard</h1>
        <p className="mt-3 text-slate-300">Welcome, {user?.email}. You are authenticated as ADMIN.</p>

        <div className="mt-8 flex flex-wrap gap-3">
          <button
            onClick={() => navigate('/')}
            className="rounded-xl border border-white/20 px-4 py-2 text-sm font-medium hover:bg-white/10"
          >
            Open Customer Home
          </button>
          <button onClick={logout} className="rounded-xl bg-brand-copper px-4 py-2 text-sm font-semibold text-slate-900">
            Logout
          </button>
        </div>
      </section>
    </main>
  )
}

export default AdminDashboardPage
