import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import HeroBanner from '../../components/store/HeroBanner'
import ProductCard from '../../components/store/ProductCard'
import ProductGridSkeleton from '../../components/store/ProductGridSkeleton'
import ErrorPanel from '../../components/ui/ErrorPanel'
import useProductCatalogQuery from '../../hooks/useProductCatalogQuery'
import { useCommerceStore } from '../../store/useCommerceStore'
import { getErrorMessage } from '../../utils/auth'

function CustomerHomePage() {
  const navigate = useNavigate()
  const wishlistProductIds = useCommerceStore((state) => state.wishlistProductIds)
  const toggleWishlist = useCommerceStore((state) => state.toggleWishlist)
  const addItemToCart = useCommerceStore((state) => state.addItemToCart)

  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [minRating, setMinRating] = useState('')
  const [sortBy, setSortBy] = useState('createdAt')
  const [direction, setDirection] = useState('desc')
  const [page, setPage] = useState(0)
  const [uiError, setUiError] = useState('')
  const [cartLoadingProductId, setCartLoadingProductId] = useState(null)

  const { data, loading, error, fetchProducts } = useProductCatalogQuery({
    search,
    category,
    minPrice,
    maxPrice,
    minRating,
    page,
    size: 12,
    sortBy,
    direction,
  })

  const products = data?.content || []

  const categories = useMemo(() => {
    const source = data?.content || []
    return [...new Set(source.map((item) => item.category).filter(Boolean))].sort((a, b) => a.localeCompare(b))
  }, [data])

  const updateFilter = (setter) => (event) => {
    setter(event.target.value)
    setPage(0)
  }

  const handleToggleWishlist = async (productId) => {
    setUiError('')
    try {
      await toggleWishlist(productId)
    } catch (err) {
      setUiError(getErrorMessage(err, 'Unable to update wishlist.'))
    }
  }

  const handleAddToCart = async (productId) => {
    setUiError('')
    setCartLoadingProductId(productId)
    try {
      await addItemToCart(productId, 1)
    } catch (err) {
      setUiError(getErrorMessage(err, 'Unable to add item to cart.'))
    } finally {
      setCartLoadingProductId(null)
    }
  }

  return (
    <main className="min-h-screen bg-slate-50 pb-12">
      <section className="mx-auto w-full max-w-[1600px] px-3 pt-6 sm:px-4 md:px-6 xl:px-8">
        <HeroBanner />

        <section className="mt-8 grid gap-6 xl:grid-cols-[300px_1fr]">
          <aside className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
            <h3 className="text-sm font-semibold uppercase tracking-[0.12em] text-slate-700">Filters</h3>
            <div className="mt-4 space-y-4">
              <div>
                <label className="mb-1 block text-xs font-medium text-slate-600">Category</label>
                <select
                  value={category}
                  onChange={updateFilter(setCategory)}
                  className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800"
                >
                  <option value="">All Categories</option>
                  {categories.map((item) => (
                    <option key={item} value={item}>
                      {item}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="mb-1 block text-xs font-medium text-slate-600">Min Price</label>
                  <input
                    type="number"
                    min="0"
                    value={minPrice}
                    onChange={updateFilter(setMinPrice)}
                    className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm text-slate-800"
                    placeholder="0"
                  />
                </div>
                <div>
                  <label className="mb-1 block text-xs font-medium text-slate-600">Max Price</label>
                  <input
                    type="number"
                    min="0"
                    value={maxPrice}
                    onChange={updateFilter(setMaxPrice)}
                    className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm text-slate-800"
                    placeholder="500"
                  />
                </div>
              </div>

              <div>
                <label className="mb-1 block text-xs font-medium text-slate-600">Minimum Rating</label>
                <select
                  value={minRating}
                  onChange={updateFilter(setMinRating)}
                  className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800"
                >
                  <option value="">Any Rating</option>
                  <option value="5">5 stars</option>
                  <option value="4">4+ stars</option>
                  <option value="3">3+ stars</option>
                  <option value="2">2+ stars</option>
                  <option value="1">1+ stars</option>
                </select>
              </div>

              <button
                onClick={() => {
                  setCategory('')
                  setMinPrice('')
                  setMaxPrice('')
                  setMinRating('')
                  setPage(0)
                }}
                className="w-full rounded-xl border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
              >
                Clear Filters
              </button>
            </div>
          </aside>

          <div>
            <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
              <h3 className="text-xl font-semibold text-slate-900">Storefront</h3>
              <div className="flex items-center gap-3">
                <button
                  onClick={() => navigate('/orders')}
                  className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700"
                >
                  My Orders
                </button>
                <button onClick={fetchProducts} className="text-sm font-semibold text-brand-ocean">
                  Refresh
                </button>
              </div>
            </div>

            <div className="mb-5 flex flex-wrap items-center gap-2">
              <input
                value={search}
                onChange={updateFilter(setSearch)}
                placeholder="Search products, category, description"
                className="min-w-[280px] flex-1 rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm text-slate-800"
              />
              <select
                value={sortBy}
                onChange={(event) => {
                  setSortBy(event.target.value)
                  setPage(0)
                }}
                className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800"
              >
                <option value="createdAt">Newest</option>
                <option value="price">Price</option>
                <option value="name">Name</option>
              </select>
              <button
                onClick={() => {
                  setDirection((previous) => (previous === 'asc' ? 'desc' : 'asc'))
                  setPage(0)
                }}
                className="rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700"
              >
                {direction.toUpperCase()}
              </button>
            </div>

            {(error || uiError) && <ErrorPanel message={error || uiError} onRetry={fetchProducts} />}

            {loading ? (
              <ProductGridSkeleton count={8} />
            ) : (
              <>
                <div className="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4">
                  {products.map((product) => (
                    <ProductCard
                      key={product.id}
                      product={product}
                      isWishlisted={wishlistProductIds.includes(product.id)}
                      onToggleWishlist={handleToggleWishlist}
                      onAddToCart={handleAddToCart}
                      cartLoading={cartLoadingProductId === product.id}
                    />
                  ))}
                </div>

                {products.length === 0 && (
                  <div className="mt-6 rounded-2xl border border-slate-200 bg-white p-6 text-center text-sm text-slate-600">
                    No products match your filters.
                  </div>
                )}

                {data && data.totalPages > 1 && (
                  <div className="mt-6 flex items-center justify-between">
                    <button
                      onClick={() => setPage((previous) => Math.max(0, previous - 1))}
                      disabled={page === 0}
                      className="rounded-xl border border-slate-300 px-4 py-2 text-sm text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      Previous
                    </button>
                    <p className="text-sm text-slate-600">
                      Page {data.page + 1} of {data.totalPages}
                    </p>
                    <button
                      onClick={() => setPage((previous) => Math.min(data.totalPages - 1, previous + 1))}
                      disabled={page >= data.totalPages - 1}
                      className="rounded-xl border border-slate-300 px-4 py-2 text-sm text-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                      Next
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </section>
      </section>
    </main>
  )
}

export default CustomerHomePage
