function ProductTable({ rows, onEdit, onDelete, loading }) {
  if (loading) {
    return (
      <div className="rounded-2xl border border-white/10 bg-white/5 p-6 text-slate-300">
        Loading products...
      </div>
    )
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-white/10 bg-slate-900/60">
      <table className="min-w-full divide-y divide-white/10">
        <thead className="bg-slate-950/70 text-left text-xs uppercase tracking-[0.12em] text-slate-400">
          <tr>
            <th className="px-4 py-3">Product</th>
            <th className="px-4 py-3">Brand</th>
            <th className="px-4 py-3">Category</th>
            <th className="px-4 py-3">Price</th>
            <th className="px-4 py-3">Stock</th>
            <th className="px-4 py-3 text-right">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-white/10 text-sm text-slate-100">
          {rows.map((product) => (
            <tr key={product.id}>
              <td className="px-4 py-3">
                <div className="flex items-center gap-3">
                  <img src={product.thumbUrl} alt={product.name} className="h-10 w-10 rounded-lg object-cover" />
                  <span>{product.name}</span>
                </div>
              </td>
              <td className="px-4 py-3 text-slate-300">{product.brand}</td>
              <td className="px-4 py-3 text-slate-300">{product.category}</td>
              <td className="px-4 py-3">{product.priceText}</td>
              <td className="px-4 py-3">{product.stockQuantity}</td>
              <td className="px-4 py-3 text-right">
                <div className="inline-flex gap-2">
                  <button
                    onClick={() => onEdit(product.raw)}
                    className="rounded-lg border border-white/20 px-3 py-1 text-xs hover:bg-white/10"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => onDelete(product.raw)}
                    className="rounded-lg border border-red-400/60 px-3 py-1 text-xs text-red-300 hover:bg-red-500/10"
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default ProductTable
