import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import HeroBanner from '../../components/store/HeroBanner'
import ProductCard from '../../components/store/ProductCard'
import ProductGridSkeleton from '../../components/store/ProductGridSkeleton'
import ErrorPanel from '../../components/ui/ErrorPanel'
import { useAuthStore } from '../../store/useAuthStore'
import { hasAdminRole } from '../../utils/auth'
import { listProducts } from '../../services/productService'
import { getErrorMessage } from '../../utils/auth'

function CustomerHomePage() {
  const navigate = useNavigate()
  const user = useAuthStore((state) => state.user)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [products, setProducts] = useState([])

  const fetchProducts = async () => {
    setLoading(true)
    setError('')

    try {
      const response = await listProducts({ page: 0, size: 20, sortBy: 'createdAt', direction: 'desc' })
      setProducts(response?.content || [])
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load products.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProducts()
  }, [])

  const logout = () => {
    clearAuth()
    navigate('/auth/login', { replace: true })
  }

  return (
    <main className="min-h-screen bg-slate-50 pb-12">
      <section className="mx-auto max-w-7xl px-4 pt-8 md:px-6">
        <header className="mb-6 flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-xs uppercase tracking-[0.16em] text-brand-ocean">QuickCart Store</p>
            <h2 className="mt-1 text-2xl font-semibold text-slate-900">Welcome, {user?.email}</h2>
          </div>
          <div className="flex gap-2">
            {hasAdminRole(user) && (
              <button
                onClick={() => navigate('/admin/dashboard')}
                className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
              >
                Admin Panel
              </button>
            )}
            <button onClick={logout} className="rounded-xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white">
              Logout
            </button>
          </div>
        </header>

        <HeroBanner />

        <section className="mt-8">
          <div className="mb-4 flex items-center justify-between">
            <h3 className="text-xl font-semibold text-slate-900">Featured Products</h3>
            <button onClick={fetchProducts} className="text-sm font-semibold text-brand-ocean">
              Refresh
            </button>
          </div>

          {error && <ErrorPanel message={error} onRetry={fetchProducts} />}

          {loading ? (
            <ProductGridSkeleton />
          ) : (
            <div className="grid grid-cols-1 gap-5 md:grid-cols-3 lg:grid-cols-4">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          )}
        </section>
      </section>
    </main>
  )
}

export default CustomerHomePage
