function ErrorPanel({ message, onRetry }) {
  return (
    <div className="rounded-2xl border border-red-200 bg-red-50 p-4 text-red-700">
      <p className="font-medium">{message}</p>
      <button onClick={onRetry} className="mt-3 rounded-lg bg-red-600 px-3 py-2 text-sm font-semibold text-white">
        Retry
      </button>
    </div>
  )
}

export default ErrorPanel
