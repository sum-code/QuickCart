import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/useAuthStore'
import { useCommerceStore } from '../../store/useCommerceStore'
import { logoutRequest } from '../../services/authService'
import { hasAdminRole } from '../../utils/auth'

function HeartIcon({ active = false }) {
  return (
    <svg viewBox="0 0 24 24" fill={active ? 'currentColor' : 'none'} className="h-5 w-5" aria-hidden="true">
      <path
        d="M12 20.4l-1.1-1c-4.4-4-7.3-6.7-7.3-10.1C3.6 6.6 5.7 4.5 8.4 4.5c1.6 0 3.2.8 4.2 2.1 1-1.3 2.6-2.1 4.2-2.1 2.7 0 4.8 2.1 4.8 4.8 0 3.4-2.9 6.1-7.3 10.1l-1.1 1z"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function CartIcon() {
  return (
    <svg viewBox="0 0 24 24" fill="none" className="h-5 w-5" aria-hidden="true">
      <path
        d="M3 4h2l2.2 9.2a1 1 0 0 0 1 .8h8.8a1 1 0 0 0 1-.8L20 7H7"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="10" cy="19" r="1.6" fill="currentColor" />
      <circle cx="17" cy="19" r="1.6" fill="currentColor" />
    </svg>
  )
}

function Badge({ value }) {
  if (!value || value < 1) {
    return null
  }

  const displayValue = value > 99 ? '99+' : value

  return (
    <span className="absolute -right-1.5 -top-1.5 min-w-5 rounded-full border-2 border-white bg-red-500 px-1.5 py-0.5 text-center text-[10px] font-semibold leading-none text-white shadow-sm">
      {displayValue}
    </span>
  )
}

function GlobalNavbar() {
  const navigate = useNavigate()
  const user = useAuthStore((state) => state.user)
  const clearAuth = useAuthStore((state) => state.clearAuth)
  const wishlistCount = useCommerceStore((state) => state.wishlistCount)
  const cartCount = useCommerceStore((state) => state.cartCount)
  const hydrateCommerceData = useCommerceStore((state) => state.hydrateCommerceData)
  const resetCommerce = useCommerceStore((state) => state.resetCommerce)

  const [menuOpen, setMenuOpen] = useState(false)
  const menuContainerRef = useRef(null)

  useEffect(() => {
    hydrateCommerceData()
  }, [hydrateCommerceData])

  useEffect(() => {
    if (!menuOpen) {
      return
    }

    const onPointerDown = (event) => {
      if (!menuContainerRef.current) {
        return
      }

      if (!menuContainerRef.current.contains(event.target)) {
        setMenuOpen(false)
      }
    }

    const onKeyDown = (event) => {
      if (event.key === 'Escape') {
        setMenuOpen(false)
      }
    }

    document.addEventListener('mousedown', onPointerDown)
    document.addEventListener('touchstart', onPointerDown)
    document.addEventListener('keydown', onKeyDown)

    return () => {
      document.removeEventListener('mousedown', onPointerDown)
      document.removeEventListener('touchstart', onPointerDown)
      document.removeEventListener('keydown', onKeyDown)
    }
  }, [menuOpen])

  const logout = async () => {
    try {
      await logoutRequest()
    } catch {
      // Ignore logout network errors; still clear local session.
    }
    resetCommerce()
    clearAuth()
    navigate('/auth/login', { replace: true })
  }

  const avatarInitial = user?.email?.charAt(0)?.toUpperCase() || 'U'

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/95 backdrop-blur">
      <nav className="mx-auto flex w-full max-w-[1600px] items-center justify-between px-3 py-3 sm:px-4 md:px-6 xl:px-8">
        <Link to="/" className="text-2xl font-semibold tracking-tight text-brand-ink">
          Quick<span className="text-brand-ocean">Cart</span>
        </Link>

        <div className="flex items-center gap-2 sm:gap-3">
          <Link
            to="/wishlist"
            className="relative flex h-11 w-11 items-center justify-center rounded-2xl border border-slate-200 bg-white text-slate-700 transition hover:-translate-y-0.5 hover:border-brand-ocean hover:text-red-500 hover:shadow-sm"
            aria-label="Wishlist"
          >
            <HeartIcon active={wishlistCount > 0} />
            <Badge value={wishlistCount} />
          </Link>

          <button
            type="button"
            onClick={() => navigate('/cart')}
            className="relative flex h-11 w-11 items-center justify-center rounded-2xl border border-slate-200 bg-white text-slate-700 transition hover:-translate-y-0.5 hover:border-brand-ocean hover:text-brand-ocean hover:shadow-sm"
            aria-label="Cart"
          >
            <CartIcon />
            <Badge value={cartCount} />
          </button>

          <div ref={menuContainerRef} className="relative">
            <button
              onClick={() => setMenuOpen((previous) => !previous)}
              className="flex h-10 w-10 items-center justify-center rounded-full bg-brand-ink text-sm font-semibold text-white"
            >
              {avatarInitial}
            </button>

            {menuOpen && (
              <div className="absolute right-0 mt-2 w-56 rounded-xl border border-slate-200 bg-white p-2 shadow-lg">
                <p className="mb-2 max-w-full break-all px-2 text-xs text-slate-500" title={user?.email}>
                  {user?.email}
                </p>

                {hasAdminRole(user) && (
                  <button
                    onClick={() => {
                      setMenuOpen(false)
                      navigate('/admin/dashboard')
                    }}
                    className="w-full rounded-lg px-3 py-2 text-left text-sm text-slate-700 hover:bg-slate-100"
                  >
                    Admin Panel
                  </button>
                )}

                <button
                  onClick={() => {
                    setMenuOpen(false)
                    navigate('/orders')
                  }}
                  className="w-full rounded-lg px-3 py-2 text-left text-sm text-slate-700 hover:bg-slate-100"
                >
                  My Orders
                </button>

                <button
                  onClick={logout}
                  className="w-full rounded-lg px-3 py-2 text-left text-sm text-red-600 hover:bg-red-50"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </nav>
    </header>
  )
}

export default GlobalNavbar
