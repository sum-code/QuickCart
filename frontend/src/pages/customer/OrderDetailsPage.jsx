import { useState } from 'react'
import { useParams } from 'react-router-dom'
import ErrorPanel from '../../components/ui/ErrorPanel'
import OrderTrackingSkeleton from '../../components/store/OrderTrackingSkeleton'
import OrderTimelineStepper from '../../components/store/OrderTimelineStepper'
import { useOrderTracking } from '../../hooks/useOrderTracking'
import { downloadOrderInvoice, requestOrderReturn } from '../../services/orderService'
import { formatCurrency } from '../../utils/product'
import { getErrorMessage } from '../../utils/auth'

function OrderDetailsPage() {
  const { id } = useParams()
  const { data, loading, error, refresh } = useOrderTracking(id)
  const [isRequestingReturn, setIsRequestingReturn] = useState(false)
  const [isDownloading, setIsDownloading] = useState(false)

  const downloadInvoice = async () => {
    if (!id) {
      return
    }

    setIsDownloading(true)
    try {
      const blob = await downloadOrderInvoice(id)
      const url = window.URL.createObjectURL(blob)
      const anchor = document.createElement('a')
      anchor.href = url
      anchor.download = `invoice-${id}.pdf`
      document.body.appendChild(anchor)
      anchor.click()
      anchor.remove()
      window.URL.revokeObjectURL(url)
    } finally {
      setIsDownloading(false)
    }
  }

  const requestReturn = async () => {
    if (!id) {
      return
    }

    setIsRequestingReturn(true)
    try {
      await requestOrderReturn(id)
      await refresh()
    } catch (err) {
      window.alert(getErrorMessage(err, 'Unable to request return at the moment.'))
    } finally {
      setIsRequestingReturn(false)
    }
  }

  if (loading) {
    return (
      <div className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 xl:px-8">
        <OrderTrackingSkeleton />
      </div>
    )
  }

  if (error) {
    return (
      <div className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 xl:px-8">
        <ErrorPanel message={error} onRetry={refresh} />
      </div>
    )
  }

  if (!data) {
    return null
  }

  const trackingUrl = data.trackingNumber ? `https://tracking.quickcart.example/${data.trackingNumber}` : ''

  return (
    <div className="mx-auto w-full max-w-6xl space-y-5 px-4 py-8 sm:px-6 xl:px-8">
      <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <p className="text-xs uppercase tracking-[0.14em] text-slate-500">Order Details</p>
        <h1 className="mt-2 text-2xl font-semibold text-slate-900">Order #{data.id}</h1>
        <p className="mt-1 text-sm text-slate-600">Status: {data.status.replaceAll('_', ' ')}</p>
        <p className="mt-1 text-sm text-slate-600">Placed on: {new Date(data.createdAt).toLocaleString()}</p>
        <p className="mt-1 text-sm font-semibold text-slate-800">Total: {formatCurrency(data.totalAmount)}</p>
      </section>

      <OrderTimelineStepper status={data.status} history={data.history} />

      <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 className="text-sm font-semibold uppercase tracking-[0.12em] text-slate-600">Action Hub</h2>

        {data.status === 'SHIPPED' && (
          <div className="mt-3 rounded-xl border border-slate-200 bg-slate-50 p-4 text-sm text-slate-700">
            <p>Courier: {data.courierName || 'N/A'}</p>
            <p className="mt-1">Tracking #: {data.trackingNumber || 'N/A'}</p>
            {trackingUrl && (
              <a href={trackingUrl} target="_blank" rel="noreferrer" className="mt-2 inline-block font-medium text-brand-ocean">
                Open Tracking URL
              </a>
            )}
          </div>
        )}

        <div className="mt-4 flex flex-wrap gap-3">
          <button
            onClick={downloadInvoice}
            disabled={isDownloading}
            className="rounded-xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
          >
            {isDownloading ? 'Preparing Invoice...' : 'Download Invoice PDF'}
          </button>

          {data.status === 'DELIVERED' && (
            <button
              onClick={requestReturn}
              disabled={isRequestingReturn}
              className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 disabled:opacity-50"
            >
              {isRequestingReturn ? 'Submitting...' : 'Request Return'}
            </button>
          )}
        </div>
      </section>

      <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <h2 className="text-sm font-semibold uppercase tracking-[0.12em] text-slate-600">Items</h2>
        <div className="mt-3 space-y-3">
          {data.items.map((item) => (
            <div key={`${item.productId}-${item.productName}`} className="flex items-center justify-between rounded-xl border border-slate-200 p-3">
              <div>
                <p className="text-sm font-semibold text-slate-900">{item.productName}</p>
                <p className="text-xs text-slate-600">Qty: {item.quantity}</p>
              </div>
              <p className="text-sm font-semibold text-slate-800">{formatCurrency(item.lineTotal)}</p>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}

export default OrderDetailsPage
