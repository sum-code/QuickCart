import { formatCurrency, getRatingStars, normalizeRating, optimizeCloudinaryUrl } from '../../utils/product'

function ProductCard({ product, isWishlisted = false, onToggleWishlist, onAddToCart, cartLoading = false }) {
  const rating = normalizeRating(product.averageRating)
  const stars = getRatingStars(rating)
  const reviewCount = Number(product.reviewCount || 0)
  const outOfStock = Number(product.stockQuantity || 0) <= 0

  return (
    <article className="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-xl">
      <div className="relative overflow-hidden">
        <img
          src={optimizeCloudinaryUrl(product.imageUrl, 'f_auto,q_auto,w_700,h_520,c_fill')}
          alt={product.name}
          className="h-52 w-full object-cover transition duration-500 group-hover:scale-110"
        />
        <button
          type="button"
          onClick={() => onToggleWishlist?.(product.id)}
          className={`absolute right-3 top-3 rounded-full p-2 text-sm shadow ${
            isWishlisted ? 'bg-red-500 text-white' : 'bg-white/90 text-slate-600'
          }`}
          aria-label={isWishlisted ? 'Remove from wishlist' : 'Add to wishlist'}
        >
          ♥
        </button>
      </div>
      <div className="space-y-2 p-4">
        <p className="text-xs font-semibold uppercase tracking-[0.12em] text-brand-ocean">{product.category}</p>
        <h3 className="line-clamp-1 text-lg font-semibold text-slate-900">{product.name}</h3>
        <p className="line-clamp-2 text-sm text-slate-600">{product.description || 'No description available.'}</p>
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-0.5" aria-label={`Rated ${rating.toFixed(1)} out of 5`}>
            {stars.map((filled, index) => (
              <span key={index} className={filled ? 'text-amber-400' : 'text-slate-300'}>
                ★
              </span>
            ))}
          </div>
          <p className="text-xs text-slate-500">{rating.toFixed(1)} ({reviewCount})</p>
        </div>
        <div className="flex items-center justify-between">
          <p className="text-lg font-semibold text-slate-900">{formatCurrency(product.price)}</p>
          <p className="text-xs text-slate-500">Stock: {product.stockQuantity}</p>
        </div>

        <button
          type="button"
          disabled={outOfStock || cartLoading}
          onClick={() => onAddToCart?.(product.id)}
          className="mt-2 w-full rounded-xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-deep disabled:cursor-not-allowed disabled:bg-slate-400"
        >
          {outOfStock ? 'Out of Stock' : cartLoading ? 'Adding...' : 'Add to Cart'}
        </button>
      </div>
    </article>
  )
}

export default ProductCard
