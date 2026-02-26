import { Link } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { getAuthRedirectPath } from '../../utils/auth'

function NotFoundPage() {
  const user = useAuthStore((state) => state.user)
  const accessToken = useAuthStore((state) => state.accessToken)

  const fallbackPath = user && accessToken ? getAuthRedirectPath(user) : '/auth/login'

  return (
    <main className="grid min-h-screen place-items-center bg-slate-950 px-4 text-white">
      <section className="rounded-2xl border border-white/10 bg-slate-900 p-8 text-center shadow-premium">
        <p className="text-sm text-brand-copper">404</p>
        <h1 className="mt-2 text-2xl font-semibold">Page not found</h1>
        <Link to={fallbackPath} className="mt-5 inline-block rounded-lg bg-brand-ocean px-4 py-2 text-sm font-medium">
          Back to app
        </Link> 
      </section>
    </main>
  )
}

export default NotFoundPage
