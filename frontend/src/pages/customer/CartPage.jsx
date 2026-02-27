import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { Link, useNavigate } from 'react-router-dom'
import ErrorPanel from '../../components/ui/ErrorPanel'
import { clearCart, getCart, removeCartItem, updateCartItem } from '../../services/cartService'
import { placeOrderFromCart } from '../../services/orderService'
import { formatCurrency } from '../../utils/product'
import { getErrorMessage } from '../../utils/auth'
import { useCommerceStore } from '../../store/useCommerceStore'

function CartPage() {
  const navigate = useNavigate()
  const setCartCountFromCart = useCommerceStore((state) => state.setCartCountFromCart)
  const [cart, setCart] = useState({ items: [], totalQuantity: 0, totalAmount: 0 })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const fetchCart = async () => {
    setLoading(true)
    setError('')
    try {
      const data = await getCart()
      const nextCart = data || { items: [], totalQuantity: 0, totalAmount: 0 }
      setCart(nextCart)
      setCartCountFromCart(nextCart)
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to load your cart.'))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchCart()
  }, [])

  const adjustQty = async (productId, currentQty, delta) => {
    const nextQty = currentQty + delta
    if (nextQty < 1) {
      return
    }

    setSaving(true)
    setError('')
    try {
      const data = await updateCartItem(productId, nextQty)
      setCart(data)
      setCartCountFromCart(data)
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to update cart item.'))
    } finally {
      setSaving(false)
    }
  }

  const removeItem = async (productId) => {
    setSaving(true)
    setError('')
    try {
      const data = await removeCartItem(productId)
      setCart(data)
      setCartCountFromCart(data)
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to remove item.'))
    } finally {
      setSaving(false)
    }
  }

  const clearAll = async () => {
    setSaving(true)
    setError('')
    try {
      const data = await clearCart()
      setCart(data)
      setCartCountFromCart(data)
    } catch (err) {
      setError(getErrorMessage(err, 'Unable to clear cart.'))
    } finally {
      setSaving(false)
    }
  }

  const placeOrder = async () => {
    setSaving(true)
    setError('')
    const toastId = toast.loading('Placing your order...')
    try {
      const order = await placeOrderFromCart()
      setCart({ items: [], totalQuantity: 0, totalAmount: 0 })
      setCartCountFromCart({ totalQuantity: 0 })
      toast.success('Order placed successfully.', { id: toastId })
      if (order?.id) {
        navigate(`/orders/${order.id}`)
      } else {
        navigate('/orders')
      }
    } catch (err) {
      const message = getErrorMessage(err, 'Unable to place order right now.')
      setError(message)
      toast.error(message, { id: toastId })
    } finally {
      setSaving(false)
    }
  }

  return (
    <main className="min-h-screen bg-slate-50 pb-12">
      <section className="mx-auto w-full max-w-6xl px-4 py-8 sm:px-6 xl:px-8">
        <header className="mb-5 flex flex-wrap items-center justify-between gap-3">
          <div>
            <p className="text-xs uppercase tracking-[0.14em] text-slate-500">Checkout</p>
            <h1 className="mt-1 text-2xl font-semibold text-slate-900">My Cart</h1>
          </div>
          <button
            onClick={fetchCart}
            className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-700"
          >
            Refresh
          </button>
        </header>

        {error && <ErrorPanel message={error} onRetry={fetchCart} />}

        {loading ? (
          <div className="space-y-3">
            <div className="h-20 animate-pulse rounded-2xl border border-slate-200 bg-slate-100" />
            <div className="h-20 animate-pulse rounded-2xl border border-slate-200 bg-slate-100" />
          </div>
        ) : !cart.items?.length ? (
          <section className="rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900">Your cart is empty</h2>
            <p className="mt-2 text-sm text-slate-600">Add products from the storefront to continue.</p>
            <Link to="/" className="mt-5 inline-flex rounded-xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white">
              Continue Shopping
            </Link>
          </section>
        ) : (
          <>
            <div className="space-y-3">
              {cart.items.map((item) => (
                <article key={item.productId} className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
                  <div className="flex flex-wrap items-center justify-between gap-3">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">{item.productName}</p>
                      <p className="text-xs text-slate-600">Unit Price: {formatCurrency(item.price)}</p>
                      <p className="text-xs text-slate-600">Line Total: {formatCurrency(item.lineTotal)}</p>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => adjustQty(item.productId, item.quantity, -1)}
                        disabled={saving || item.quantity <= 1}
                        className="rounded-lg border border-slate-300 px-2.5 py-1 text-sm disabled:opacity-50"
                      >
                        -
                      </button>
                      <span className="min-w-8 text-center text-sm font-semibold text-slate-800">{item.quantity}</span>
                      <button
                        onClick={() => adjustQty(item.productId, item.quantity, 1)}
                        disabled={saving}
                        className="rounded-lg border border-slate-300 px-2.5 py-1 text-sm disabled:opacity-50"
                      >
                        +
                      </button>
                      <button
                        onClick={() => removeItem(item.productId)}
                        disabled={saving}
                        className="ml-2 rounded-lg border border-rose-300 px-3 py-1 text-xs text-rose-700 disabled:opacity-50"
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>

            <section className="mt-4 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <p className="text-sm text-slate-700">Items: {cart.totalQuantity}</p>
                  <p className="text-lg font-semibold text-slate-900">Total: {formatCurrency(cart.totalAmount)}</p>
                </div>

                <div className="flex items-center gap-3">
                  <button
                    onClick={clearAll}
                    disabled={saving}
                    className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 disabled:opacity-50"
                  >
                    Clear Cart
                  </button>
                  <button
                    onClick={placeOrder}
                    disabled={saving || !cart.items?.length}
                    className="rounded-xl bg-brand-ink px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
                  >
                    {saving ? 'Processing...' : 'Place Order'}
                  </button>
                  <Link to="/" className="rounded-xl bg-brand-ocean px-4 py-2 text-sm font-semibold text-white">
                    Continue Shopping
                  </Link>
                </div>
              </div>
            </section>
          </>
        )}
      </section>
    </main>
  )
}

export default CartPage
