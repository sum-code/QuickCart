import { useEffect, useState } from 'react'

function ErrorPanel({ message, onRetry, autoHideMs = 8000, dismissible = true }) {
  const [visible, setVisible] = useState(Boolean(message))

  useEffect(() => {
    setVisible(Boolean(message))
  }, [message])

  useEffect(() => {
    if (!visible || !autoHideMs || autoHideMs < 1) {
      return
    }

    const timeoutId = window.setTimeout(() => {
      setVisible(false)
    }, autoHideMs)

    return () => {
      window.clearTimeout(timeoutId)
    }
  }, [visible, autoHideMs])

  if (!visible || !message) {
    return null
  }

  return (
    <div className="rounded-2xl border border-red-200 bg-red-50 p-4 text-red-700">
      <div className="flex items-start justify-between gap-3">
        <p className="font-medium">{message}</p>
        {dismissible && (
          <button
            type="button"
            aria-label="Dismiss error"
            onClick={() => setVisible(false)}
            className="rounded-md border border-red-200 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-100"
          >
            ✕
          </button>
        )}
      </div>

      {onRetry && (
        <button onClick={onRetry} className="mt-3 rounded-lg bg-red-600 px-3 py-2 text-sm font-semibold text-white">
          Retry
        </button>
      )}
    </div>
  )
}

export default ErrorPanel
