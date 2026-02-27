function ProductGridSkeleton({ count = 8 }) {
  return (
    <div className="grid grid-cols-1 gap-5 md:grid-cols-3 lg:grid-cols-4">
      {[...Array(count)].map((_, index) => (
        <div key={index} className="animate-pulse rounded-2xl border border-slate-200 bg-white p-3">
          <div className="h-48 rounded-xl bg-slate-200" />
          <div className="mt-3 h-4 rounded bg-slate-200" />
          <div className="mt-2 h-4 w-3/4 rounded bg-slate-200" />
          <div className="mt-4 h-5 w-1/2 rounded bg-slate-200" />
        </div>
      ))}
    </div>
  )
}

export default ProductGridSkeleton
