function PaginationBar({ page, totalPages, onChange, totalElements }) {
  return (
    <div className="mt-4 flex flex-wrap items-center justify-between gap-3 text-sm text-slate-300">
      <p>{totalElements} products</p>
      <div className="flex items-center gap-2">
        <button
          disabled={page <= 0}
          onClick={() => onChange(page - 1)}
          className="rounded-lg border border-white/20 px-3 py-1 disabled:opacity-40"
        >
          Prev
        </button>
        <span>
          Page {page + 1} / {Math.max(totalPages, 1)}
        </span>
        <button
          disabled={page + 1 >= totalPages}
          onClick={() => onChange(page + 1)}
          className="rounded-lg border border-white/20 px-3 py-1 disabled:opacity-40"
        >
          Next
        </button>
      </div>
    </div>
  )
}

export default PaginationBar
