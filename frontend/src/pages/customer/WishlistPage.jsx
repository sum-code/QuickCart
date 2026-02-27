import { useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ProductCard from '../../components/store/ProductCard'
import ProductGridSkeleton from '../../components/store/ProductGridSkeleton'
import ErrorPanel from '../../components/ui/ErrorPanel'
import { useCommerceStore } from '../../store/useCommerceStore'
import { getErrorMessage } from '../../utils/auth'

function WishlistPage() {
  const navigate = useNavigate()
  const wishlistItems = useCommerceStore((state) => state.wishlistItems)
  const wishlistProductIds = useCommerceStore((state) => state.wishlistProductIds)
  const isLoadingWishlist = useCommerceStore((state) => state.isLoadingWishlist)
  const toggleWishlist = useCommerceStore((state) => state.toggleWishlist)
  const addItemToCart = useCommerceStore((state) => state.addItemToCart)

  const [uiError, setUiError] = useState('')
  const [cartLoadingProductId, setCartLoadingProductId] = useState(null)

  const products = useMemo(() => wishlistItems.map((item) => item.product).filter(Boolean), [wishlistItems])

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
        <div className="mb-5 flex items-center justify-between">
          <div>
            <p className="text-xs uppercase tracking-[0.16em] text-brand-ocean">Save for Later</p>
            <h1 className="mt-1 text-2xl font-semibold text-slate-900">My Wishlist</h1>
          </div>
          <button
            onClick={() => navigate('/')}
            className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700"
          >
            Continue Shopping
          </button>
        </div>

        {uiError && <ErrorPanel message={uiError} onRetry={() => navigate('/wishlist')} />}

        {isLoadingWishlist ? (
          <ProductGridSkeleton count={6} />
        ) : products.length === 0 ? (
          <section className="rounded-3xl border border-slate-200 bg-white p-10 text-center shadow-sm">
            <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-rose-50 text-2xl text-rose-500">
              ♥
            </div>
            <h2 className="text-xl font-semibold text-slate-900">Your wishlist is empty</h2>
            <p className="mt-2 text-sm text-slate-600">Save items you love and come back to them anytime.</p>
            <button
              onClick={() => navigate('/')}
              className="mt-6 rounded-xl bg-brand-ink px-5 py-2.5 text-sm font-semibold text-white"
            >
              Continue Shopping
            </button>
          </section>
        ) : (
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
        )}
      </section>
    </main>
  )
}

export default WishlistPage
