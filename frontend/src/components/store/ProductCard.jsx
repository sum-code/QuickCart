import { formatCurrency, optimizeCloudinaryUrl } from '../../utils/product'

function ProductCard({ product }) {
  return (
    <article className="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-xl">
      <div className="relative overflow-hidden">
        <img
          src={optimizeCloudinaryUrl(product.imageUrl, 'f_auto,q_auto,w_700,h_520,c_fill')}
          alt={product.name}
          className="h-52 w-full object-cover transition duration-500 group-hover:scale-110"
        />
        <button className="absolute bottom-3 right-3 translate-y-3 rounded-full bg-brand-ink px-4 py-2 text-xs font-semibold text-white opacity-0 transition group-hover:translate-y-0 group-hover:opacity-100">
          Add to Cart
        </button>
      </div>
      <div className="space-y-2 p-4">
        <p className="text-xs font-semibold uppercase tracking-[0.12em] text-brand-ocean">{product.category}</p>
        <h3 className="line-clamp-1 text-lg font-semibold text-slate-900">{product.name}</h3>
        <p className="line-clamp-2 text-sm text-slate-600">{product.description || 'No description available.'}</p>
        <div className="flex items-center justify-between">
          <p className="text-lg font-semibold text-slate-900">{formatCurrency(product.price)}</p>
          <p className="text-xs text-slate-500">Stock: {product.stockQuantity}</p>
        </div>
      </div>
    </article>
  )
}

export default ProductCard
