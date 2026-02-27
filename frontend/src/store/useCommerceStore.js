import { create } from 'zustand'
import { addToCart, getCart } from '../services/cartService'
import { addWishlistItem, listWishlistItems, removeWishlistItem } from '../services/wishlistService'
import { getErrorMessage } from '../utils/auth'

function toWishlistState(items = []) {
  const ids = items.map((item) => item?.product?.id).filter(Boolean)
  return {
    wishlistItems: items,
    wishlistProductIds: ids,
    wishlistCount: items.length,
  }
}

export const useCommerceStore = create((set, get) => ({
  wishlistItems: [],
  wishlistProductIds: [],
  wishlistCount: 0,
  cartCount: 0,
  isLoadingWishlist: false,
  isTogglingWishlist: false,

  hydrateCommerceData: async () => {
    await Promise.all([get().fetchWishlist(), get().fetchCartCount()])
  },

  fetchWishlist: async () => {
    set({ isLoadingWishlist: true })
    try {
      const items = await listWishlistItems()
      set({ ...toWishlistState(items), isLoadingWishlist: false })
    } catch {
      set({ isLoadingWishlist: false })
    }
  },

  fetchCartCount: async () => {
    try {
      const cart = await getCart()
      set({ cartCount: Number(cart?.totalQuantity || 0) })
    } catch {
      set({ cartCount: 0 })
    }
  },

  toggleWishlist: async (productId) => {
    const state = get()
    const isWishlisted = state.wishlistProductIds.includes(productId)

    set({ isTogglingWishlist: true })
    try {
      if (isWishlisted) {
        await removeWishlistItem(productId)
        const nextItems = state.wishlistItems.filter((item) => item?.product?.id !== productId)
        set({ ...toWishlistState(nextItems), isTogglingWishlist: false })
        return false
      }

      const added = await addWishlistItem(productId)
      const nextItems = [added, ...state.wishlistItems]
      set({ ...toWishlistState(nextItems), isTogglingWishlist: false })
      return true
    } catch (error) {
      set({ isTogglingWishlist: false })
      throw new Error(getErrorMessage(error, 'Unable to update wishlist right now.'))
    }
  },

  addItemToCart: async (productId, quantity = 1) => {
    const cart = await addToCart({ productId, quantity })
    set({ cartCount: Number(cart?.totalQuantity || 0) })
    return cart
  },

  setCartCountFromCart: (cart) => {
    set({ cartCount: Number(cart?.totalQuantity || 0) })
  },

  resetCommerce: () =>
    set({
      wishlistItems: [],
      wishlistProductIds: [],
      wishlistCount: 0,
      cartCount: 0,
      isLoadingWishlist: false,
      isTogglingWishlist: false,
    }),
}))
