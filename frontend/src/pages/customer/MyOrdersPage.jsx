import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import ErrorPanel from '../../components/ui/ErrorPanel'
import { getMyTrackingOrders } from '../../services/orderService'
import { formatCurrency } from '../../utils/product'
import { getErrorMessage } from '../../utils/auth'

const STATUS_STYLES = {
  PLACED: 'bg-amber-100 text-amber-800 border-amber-200',
  PROCESSING: 'bg-indigo-100 text-indigo-800 border-indigo-200',
  SHIPPED: 'bg-blue-100 text-blue-800 border-blue-200',
  DELIVERED: 'bg-emerald-100 text-emerald-800 border-emerald-200',
  RETURN_REQUESTED: 'bg-purple-100 text-purple-800 border-purple-200',
  RETURN_APPROVED: 'bg-fuchsia-100 text-fuchsia-800 border-fuchsia-200',
  REFUNDED: 'bg-slate-200 text-slate-800 border-slate-300',
  CANCELLED: 'bg-rose-100 text-rose-800 border-rose-200',
}

function MyOrdersPage() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchOrders = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getMyTrackingOrders()
      setOrders(data || [])
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to load your orders.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  return (
    <main className="min-h-screen bg-slate-50 pb-12">
      <section className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 xl:px-8">
        <header className="mb-5 flex items-center justify-between gap-3">
          <div>
            <p className="text-xs uppercase tracking-[0.14em] text-slate-500">Post-Purchase</p>
            <h1 className="mt-1 text-2xl font-semibold text-slate-900">My Orders</h1>
          </div>
          <button
            onClick={fetchOrders}
            className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700"
          >
            Refresh
          </button>
        </header>

        {error && <ErrorPanel message={error} onRetry={fetchOrders} />}

        {loading ? (
          <div className="space-y-3">
            <div className="h-24 animate-pulse rounded-2xl border border-slate-200 bg-slate-100" />
            <div className="h-24 animate-pulse rounded-2xl border border-slate-200 bg-slate-100" />
            <div className="h-24 animate-pulse rounded-2xl border border-slate-200 bg-slate-100" />
          </div>
        ) : orders.length === 0 ? (
          <section className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900">No orders yet</h2>
            <p className="mt-2 text-sm text-slate-600">Your placed orders will appear here.</p>
            <Link
              to="/"
              className="mt-5 inline-flex rounded-xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white"
            >
              Continue Shopping
            </Link>
          </section>
        ) : (
          <div className="space-y-3">
            {orders.map((order) => {
              const style = STATUS_STYLES[order.status] || 'bg-slate-100 text-slate-700 border-slate-200'
              return (
                <article key={order.id} className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">Order #{order.id}</p>
                      <p className="text-xs text-slate-600">Placed: {new Date(order.createdAt).toLocaleString()}</p>
                      <p className="text-xs text-slate-600">Total: {formatCurrency(order.totalAmount)}</p>
                    </div>

                    <div className="flex items-center gap-3">
                      <span className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-semibold ${style}`}>
                        {order.status?.replaceAll('_', ' ')}
                      </span>

                      <Link
                        to={`/orders/${order.id}`}
                        className="rounded-xl bg-brand-ocean px-4 py-2 text-sm font-semibold text-white"
                      >
                        View Timeline
                      </Link>
                    </div>
                  </div>
                </article>
              )
            })}
          </div>
        )}
      </section>
    </main>
  )
}

export default MyOrdersPage
