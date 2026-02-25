import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { hasAdminRole } from '../../utils/auth'

function CustomerHomePage() {
  const navigate = useNavigate()
  const user = useAuthStore((state) => state.user)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  const logout = () => {
    clearAuth()
    navigate('/auth/login', { replace: true })
  }

  return (
    <main className="min-h-screen bg-gradient-to-br from-slate-950 via-brand-deep to-brand-ink px-6 py-10 text-white">
      <section className="mx-auto max-w-5xl rounded-3xl border border-white/10 bg-white/10 p-8 shadow-premium backdrop-blur-md">
        <p className="text-xs uppercase tracking-[0.16em] text-brand-aurora">Customer Space</p>
        <h1 className="mt-2 text-3xl font-semibold">Welcome to QuickCart</h1>
        <p className="mt-3 text-slate-100">Logged in as {user?.email}</p>

        <div className="mt-8 flex flex-wrap gap-3">
          {hasAdminRole(user) && (
            <button
              onClick={() => navigate('/admin/dashboard')}
              className="rounded-xl border border-white/30 px-4 py-2 text-sm font-medium hover:bg-white/15"
            >
              Go to Admin Dashboard
            </button>
          )}
          <button onClick={logout} className="rounded-xl bg-brand-copper px-4 py-2 text-sm font-semibold text-slate-900">
            Logout
          </button>
        </div>
      </section>
    </main>
  )
}

export default CustomerHomePage
