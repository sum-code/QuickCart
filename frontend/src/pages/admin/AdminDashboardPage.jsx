import { useEffect, useMemo, useState } from 'react'
import toast from 'react-hot-toast'
import { useNavigate } from 'react-router-dom'
import AdminSidebar from '../../components/admin/AdminSidebar'
import ProductModal from '../../components/admin/ProductModal'
import ProductTable from '../../components/admin/ProductTable'
import PaginationBar from '../../components/admin/PaginationBar'
import AdminOrderTable from '../../components/admin/AdminOrderTable'
import OrderStatusModal from '../../components/admin/OrderStatusModal'
import ErrorPanel from '../../components/ui/ErrorPanel'
import { useAuthStore } from '../../store/useAuthStore'
import { createProduct, deleteProduct, listProducts, updateProduct } from '../../services/productService'
import { getAdminOrders, updateAdminOrderStatus } from '../../services/orderService'
import { formatCurrency, optimizeCloudinaryUrl } from '../../utils/product'
import { getErrorMessage } from '../../utils/auth'

function AdminDashboardPage() {
  const navigate = useNavigate()
  const user = useAuthStore((state) => state.user)
  const clearAuth = useAuthStore((state) => state.clearAuth)

  const [collapsed, setCollapsed] = useState(false)
  const [activeView, setActiveView] = useState('inventory')
  const [productsPage, setProductsPage] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [sortBy, setSortBy] = useState('createdAt')
  const [sortDirection, setSortDirection] = useState('desc')
  const [orders, setOrders] = useState([])
  const [ordersLoading, setOrdersLoading] = useState(false)
  const [ordersError, setOrdersError] = useState('')
  const [statusSaving, setStatusSaving] = useState(false)
  const [statusModal, setStatusModal] = useState({ open: false, order: null })

  const [modalState, setModalState] = useState({ open: false, mode: 'create', product: null })

  const fetchProducts = async (nextPage = page, nextSearch = search) => {
    setLoading(true)
    setError('')
    try {
      const response = await listProducts({
        page: nextPage,
        size: 10,
        sortBy,
        direction: sortDirection,
        search: nextSearch || undefined,
      })
      setProductsPage(response)
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to load inventory.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProducts(page)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [sortBy, sortDirection])

  useEffect(() => {
    if (activeView === 'orders') {
      fetchOrders()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeView])

  const fetchOrders = async () => {
    setOrdersLoading(true)
    setOrdersError('')
    try {
      const allOrders = await getAdminOrders()
      setOrders(allOrders)
    } catch (err) {
      setOrdersError(getErrorMessage(err, 'Unable to load orders.'))
    } finally {
      setOrdersLoading(false)
    }
  }

  const mappedRows = useMemo(() => {
    const rows = productsPage?.content || []
    return rows.map((product) => ({
      raw: product,
      id: product.id,
      name: product.name,
      brand: product.brand,
      category: product.category,
      stockQuantity: product.stockQuantity,
      priceText: formatCurrency(product.price),
      thumbUrl: optimizeCloudinaryUrl(product.imageUrl, 'f_auto,q_auto,w_120,h_120,c_fill'),
    }))
  }, [productsPage])

  const onPageChange = async (nextPage) => {
    setPage(nextPage)
    await fetchProducts(nextPage)
  }

  const onSearchSubmit = async (event) => {
    event.preventDefault()
    setPage(0)
    await fetchProducts(0, search)
  }

  const onDelete = async (product) => {
    const ok = window.confirm(`Delete ${product.name}?`)
    if (!ok) {
      return
    }

    try {
      await deleteProduct(product.id)
      await fetchProducts(page)
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to delete product.'))
    }
  }

  const onModalSubmit = async (payload, imageFile) => {
    setSaving(true)
    setError('')

    try {
      if (modalState.mode === 'create') {
        await createProduct(payload, imageFile)
      } else {
        await updateProduct(modalState.product.id, payload, imageFile)
      }

      setModalState({ open: false, mode: 'create', product: null })
      await fetchProducts(page)
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to save product.'))
    } finally {
      setSaving(false)
    }
  }

  const logout = () => {
    clearAuth()
    navigate('/auth/login', { replace: true })
  }

  const submitStatusUpdate = async (payload) => {
    const selectedOrder = statusModal.order
    if (!selectedOrder) {
      return
    }
    const orderId = selectedOrder.id

    const toastId = toast.loading('Updating order status...')
    setStatusSaving(true)
    setOrdersError('')
    try {
      const updated = await updateAdminOrderStatus(orderId, payload)
      setOrders((previous) => previous.map((order) => (order.id === orderId ? updated : order)))
      setStatusModal({ open: true, order: updated })
      toast.success('Order status updated.', { id: toastId })
    } catch (err) {
      const message = getErrorMessage(err, 'Unable to update order status.')
      setOrdersError(message)
      toast.error(message, { id: toastId })
    } finally {
      setStatusSaving(false)
    }
  }

  return (
    <main className="flex min-h-screen bg-slate-900 text-white">
      <AdminSidebar
        collapsed={collapsed}
        onToggle={() => setCollapsed((prev) => !prev)}
        activeView={activeView}
        setActiveView={setActiveView}
      />

      <section className="flex-1 px-6 py-8">
        <div className="mx-auto max-w-7xl">
          <header className="flex flex-wrap items-center justify-between gap-4">
            <div>
              <p className="text-xs uppercase tracking-[0.16em] text-brand-copper">
                {activeView === 'inventory' ? 'Inventory Management' : activeView === 'orders' ? 'Order Fulfillment' : 'Analytics'}
              </p>
              <h1 className="mt-2 text-3xl font-semibold">Welcome, {user?.email}</h1>
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => navigate('/')}
                className="rounded-xl border border-white/20 px-4 py-2 text-sm hover:bg-white/10"
              >
                View Storefront
              </button>
              <button onClick={logout} className="rounded-xl bg-brand-copper px-4 py-2 text-sm font-semibold text-slate-900">
                Logout
              </button>
            </div>
          </header>

          {activeView === 'inventory' ? (
            <section className="mt-8 space-y-4">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <form onSubmit={onSearchSubmit} className="flex gap-2">
                  <input
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    placeholder="Search by name or category"
                    className="w-72 rounded-xl border border-white/20 bg-white/10 px-4 py-2 text-sm text-white placeholder:text-slate-300 md:w-80"
                  />
                  <button className="rounded-xl bg-brand-ocean px-4 py-2 text-sm font-semibold">Search</button>
                </form>
                <div className="flex items-center gap-2">
                  <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    className="admin-filter-select rounded-xl border border-white/20 bg-slate-800/90 px-3 py-2 pr-8 text-sm text-slate-100"
                  >
                    <option value="createdAt">Newest</option>
                    <option value="price">Price</option>
                    <option value="name">Name</option>
                  </select>
                  <button
                    onClick={() => setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'))}
                    className="rounded-xl border border-white/20 bg-slate-800/80 px-3 py-2 text-sm text-slate-100"
                  >
                    {sortDirection.toUpperCase()}
                  </button>
                  <button
                    onClick={() => setModalState({ open: true, mode: 'create', product: null })}
                    className="rounded-xl bg-brand-aurora px-4 py-2 text-sm font-semibold text-slate-900"
                  >
                    Add Product
                  </button>
                </div>
              </div>

              {error && <ErrorPanel message={error} onRetry={() => fetchProducts(page)} />}

              <ProductTable
                rows={mappedRows}
                loading={loading}
                onEdit={(product) => setModalState({ open: true, mode: 'edit', product })}
                onDelete={onDelete}
              />

              <PaginationBar
                page={productsPage?.page ?? 0}
                totalPages={productsPage?.totalPages ?? 0}
                totalElements={productsPage?.totalElements ?? 0}
                onChange={onPageChange}
              />
            </section>
          ) : activeView === 'orders' ? (
            <section className="mt-8 space-y-4">
              <div className="flex justify-end">
                <button
                  onClick={fetchOrders}
                  className="rounded-xl border border-white/20 bg-slate-800/80 px-3 py-2 text-sm text-slate-100"
                >
                  Refresh Orders
                </button>
              </div>

              {ordersError && <ErrorPanel message={ordersError} onRetry={fetchOrders} />}

              <AdminOrderTable
                orders={orders}
                loading={ordersLoading}
                onUpdateStatus={(order) => setStatusModal({ open: true, order })}
              />
            </section>
          ) : (
            <section className="mt-8 rounded-2xl border border-white/10 bg-white/5 p-8 text-slate-300">
              
            </section>
          )}
        </div>
      </section>

      <ProductModal
        isOpen={modalState.open}
        mode={modalState.mode}
        initialProduct={modalState.product}
        onClose={() => setModalState({ open: false, mode: 'create', product: null })}
        onSubmit={onModalSubmit}
        loading={saving}
      />

      <OrderStatusModal
        isOpen={statusModal.open}
        order={statusModal.order}
        onClose={() => setStatusModal({ open: false, order: null })}
        onSubmit={submitStatusUpdate}
        loading={statusSaving}
      />
    </main>
  )
}

export default AdminDashboardPage
