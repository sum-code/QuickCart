function SubmitButton({ label, loading }) {
  return (
    <button
      type="submit"
      disabled={loading}
      className="mt-2 w-full rounded-xl bg-brand-ink px-4 py-3 text-sm font-semibold text-white transition hover:bg-brand-deep disabled:cursor-not-allowed disabled:opacity-70"
    >
      {loading ? 'Please wait...' : label}
    </button>
  )
}

export default SubmitButton
