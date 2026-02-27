function StepIcon({ type, active }) {
  const common = active ? 'text-white' : 'text-slate-500'

  if (type === 'box') {
    return (
      <svg viewBox="0 0 24 24" className={`h-5 w-5 ${common}`} fill="none" aria-hidden="true">
        <path d="M4 7.5 12 4l8 3.5M4 7.5V16.5L12 20l8-3.5V7.5M12 20V12" stroke="currentColor" strokeWidth="1.8" />
      </svg>
    )
  }

  if (type === 'gears') {
    return (
      <svg viewBox="0 0 24 24" className={`h-5 w-5 ${common}`} fill="none" aria-hidden="true">
        <path d="M12 8.7a3.3 3.3 0 1 1 0 6.6 3.3 3.3 0 0 1 0-6.6Zm8 3.3-1.5.5a6.8 6.8 0 0 1-.4 1l.7 1.4-1.8 1.8-1.4-.7a6.8 6.8 0 0 1-1 .4L14 20h-4l-.5-1.5a6.8 6.8 0 0 1-1-.4l-1.4.7-1.8-1.8.7-1.4a6.8 6.8 0 0 1-.4-1L4 12l1.5-.5a6.8 6.8 0 0 1 .4-1l-.7-1.4 1.8-1.8 1.4.7a6.8 6.8 0 0 1 1-.4L10 4h4l.5 1.5a6.8 6.8 0 0 1 1 .4l1.4-.7 1.8 1.8-.7 1.4c.2.3.3.7.4 1L20 12Z" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round" />
      </svg>
    )
  }

  if (type === 'truck') {
    return (
      <svg viewBox="0 0 24 24" className={`h-5 w-5 ${common}`} fill="none" aria-hidden="true">
        <path d="M3 7h11v8H3V7Zm11 2h3l3 3v3h-6V9Zm-7 8.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0Zm11 0a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0Z" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round" />
      </svg>
    )
  }

  return (
    <svg viewBox="0 0 24 24" className={`h-5 w-5 ${common}`} fill="none" aria-hidden="true">
      <path d="m5 13 4 4 10-10" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

function getTimestampForStatus(history, status) {
  const entry = history?.find((item) => item.status === status)
  if (!entry?.createdAt) {
    return ''
  }
  return new Date(entry.createdAt).toLocaleString()
}

function statusToStepIndex(status) {
  if (status === 'PLACED') return 0
  if (status === 'PROCESSING') return 1
  if (status === 'SHIPPED') return 2
  if (status === 'DELIVERED' || status === 'RETURN_REQUESTED' || status === 'RETURN_APPROVED' || status === 'REFUNDED') return 3
  return 0
}

function OrderTimelineStepper({ status, history = [] }) {
  const activeIndex = statusToStepIndex(status)

  const steps = [
    { key: 'PLACED', label: 'Placed', icon: 'box' },
    { key: 'PROCESSING', label: 'Processing', icon: 'gears' },
    { key: 'SHIPPED', label: 'Shipped', icon: 'truck' },
    { key: 'DELIVERED', label: 'Delivered', icon: 'check' },
  ]

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h3 className="text-sm font-semibold uppercase tracking-[0.12em] text-slate-600">Order Timeline</h3>
      <div className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-4">
        {steps.map((step, index) => {
          const completed = index <= activeIndex
          const timestamp = completed ? getTimestampForStatus(history, step.key) : ''

          return (
            <div key={step.key} className="relative">
              <div className="flex items-center gap-3">
                <span
                  className={`inline-flex h-10 w-10 items-center justify-center rounded-full border ${
                    completed ? 'border-brand-ocean bg-brand-ocean' : 'border-slate-300 bg-slate-100'
                  }`}
                >
                  <StepIcon type={step.icon} active={completed} />
                </span>
                <div>
                  <p className={`text-sm font-semibold ${completed ? 'text-slate-900' : 'text-slate-500'}`}>{step.label}</p>
                  <p className="text-xs text-slate-500">{timestamp || 'Pending'}</p>
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

export default OrderTimelineStepper
