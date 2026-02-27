import { useEffect, useMemo, useState } from 'react'

const NEXT_STATUS_MAP = {
  PLACED: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: ['RETURN_REQUESTED'],
  RETURN_REQUESTED: ['RETURN_APPROVED'],
  RETURN_APPROVED: ['REFUNDED'],
  REFUNDED: [],
  CANCELLED: [],
}

function OrderStatusModal({ isOpen, order, onClose, onSubmit, loading }) {
  const nextStatuses = useMemo(() => NEXT_STATUS_MAP[order?.status] || [], [order?.status])

  const [status, setStatus] = useState('')
  const [courierName, setCourierName] = useState('')
  const [trackingNumber, setTrackingNumber] = useState('')
  const [adminNote, setAdminNote] = useState('')

  useEffect(() => {
    if (!isOpen) {
      return
    }

    setStatus(nextStatuses[0] || '')
    setCourierName(order?.courierName || '')
    setTrackingNumber(order?.trackingNumber || '')
    setAdminNote('')
  }, [isOpen, nextStatuses, order?.courierName, order?.trackingNumber])

  if (!isOpen || !order) {
    return null
  }

  const submit = async (event) => {
    event.preventDefault()
    if (!status) {
      return
    }

    await onSubmit({
      status,
      courierName: status === 'SHIPPED' ? courierName : undefined,
      trackingNumber: status === 'SHIPPED' ? trackingNumber : undefined,
      adminNote,
    })
  }

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/70 p-4" role="dialog" aria-modal="true">
      <form onSubmit={submit} className="w-full max-w-xl rounded-2xl border border-white/10 bg-slate-900 p-6 text-white">
        <h3 className="text-xl font-semibold">Update Order #{order.id}</h3>
        <p className="mt-1 text-sm text-slate-300">Current status: {order.status.replaceAll('_', ' ')}</p>
        <p className="mt-1 text-xs text-slate-400">Only valid next statuses are shown for the current stage.</p>

        {nextStatuses.length > 0 ? (
          <>
            <label className="mt-4 block text-sm font-medium text-slate-200">Next Status</label>
            <select
              value={status}
              onChange={(event) => setStatus(event.target.value)}
              className="mt-2 w-full rounded-xl border border-white/20 bg-slate-800 px-4 py-2 text-sm text-white"
              required
            >
              {nextStatuses.map((option) => (
                <option key={option} value={option}>
                  {option.replaceAll('_', ' ')}
                </option>
              ))}
            </select>

            {status === 'SHIPPED' && (
              <div className="mt-4 grid gap-3 md:grid-cols-2">
                <input
                  value={courierName}
                  onChange={(event) => setCourierName(event.target.value)}
                  placeholder="Courier Name"
                  className="rounded-xl border border-white/20 bg-slate-800 px-4 py-2 text-sm text-white"
                  required
                />
                <input
                  value={trackingNumber}
                  onChange={(event) => setTrackingNumber(event.target.value)}
                  placeholder="Tracking Number"
                  className="rounded-xl border border-white/20 bg-slate-800 px-4 py-2 text-sm text-white"
                  required
                />
              </div>
            )}

            <label className="mt-4 block text-sm font-medium text-slate-200">Notes to Customer</label>
            <textarea
              value={adminNote}
              onChange={(event) => setAdminNote(event.target.value)}
              placeholder="Optional message"
              rows={4}
              className="mt-2 w-full rounded-xl border border-white/20 bg-slate-800 px-4 py-2 text-sm text-white"
            />
          </>
        ) : (
          <div className="mt-4 rounded-xl border border-white/10 bg-white/5 p-4 text-sm text-slate-300">
            This order has no further allowed status transitions.
          </div>
        )}

        <div className="mt-6 flex justify-end gap-3">
          <button type="button" onClick={onClose} className="rounded-xl border border-white/20 px-4 py-2 text-sm">
            Close
          </button>
          <button
            type="submit"
            disabled={loading || nextStatuses.length === 0}
            className="rounded-xl bg-brand-ocean px-4 py-2 text-sm font-semibold disabled:opacity-50"
          >
            {loading ? 'Updating...' : 'Save'}
          </button>
        </div>
      </form>
    </div>
  )
}

export default OrderStatusModal
