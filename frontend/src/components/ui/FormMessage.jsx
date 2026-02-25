function FormMessage({ error, success }) {
  if (!error && !success) {
    return null
  }

  return (
    <p
      className={`rounded-xl px-4 py-3 text-sm font-medium ${
        error ? 'bg-red-50 text-red-700' : 'bg-emerald-50 text-emerald-700'
      }`}
    >
      {error || success}
    </p>
  )
}

export default FormMessage
