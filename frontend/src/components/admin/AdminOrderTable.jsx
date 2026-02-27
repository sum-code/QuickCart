import { formatCurrency } from '../../utils/product'
import OrderStatusBadge from './OrderStatusBadge'

function AdminOrderTable({ orders, loading, onUpdateStatus }) {
  if (loading) {
    return <div className="rounded-2xl border border-white/10 bg-white/5 p-6 text-slate-300">Loading orders...</div>
  }

  if (!orders?.length) {
    return <div className="rounded-2xl border border-white/10 bg-white/5 p-6 text-slate-300">No orders found.</div>
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-white/10 bg-slate-900/60">
      <table className="min-w-full divide-y divide-white/10">
        <thead className="bg-slate-950/70 text-left text-xs uppercase tracking-[0.12em] text-slate-400">
          <tr>
            <th className="px-4 py-3">Order</th>
            <th className="px-4 py-3">Customer</th>
            <th className="px-4 py-3">Created</th>
            <th className="px-4 py-3">Total</th>
            <th className="px-4 py-3">Status</th>
            <th className="px-4 py-3">Tracking</th>
            <th className="px-4 py-3 text-right">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-white/10 text-sm text-slate-100">
          {orders.map((order) => (
            <tr key={order.id}>
              <td className="px-4 py-3 font-medium">#{order.id}</td>
              <td className="px-4 py-3 text-slate-300">{order.userEmail || 'N/A'}</td>
              <td className="px-4 py-3 text-slate-300">{new Date(order.createdAt).toLocaleString()}</td>
              <td className="px-4 py-3">{formatCurrency(order.totalAmount)}</td>
              <td className="px-4 py-3">
                <OrderStatusBadge status={order.status} />
              </td>
              <td className="px-4 py-3 text-slate-300">{order.trackingNumber || '—'}</td>
              <td className="px-4 py-3 text-right">
                <button
                  onClick={() => onUpdateStatus(order)}
                  className="rounded-lg border border-white/20 px-3 py-1 text-xs hover:bg-white/10"
                >
                  Update Status
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default AdminOrderTable
