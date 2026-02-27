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

function OrderStatusBadge({ status }) {
  const normalized = status || 'PLACED'
  const style = STATUS_STYLES[normalized] || 'bg-slate-100 text-slate-700 border-slate-200'

  return (
    <span className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-semibold ${style}`}>
      {normalized.replaceAll('_', ' ')}
    </span>
  )
}

export default OrderStatusBadge
