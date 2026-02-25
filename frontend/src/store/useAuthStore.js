import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'

const AUTH_STORAGE_KEY = 'quickcart-auth'

export const useAuthStore = create(
  persist(
    (set) => ({
      accessToken: null,
      user: null,
      setAuthSession: (session) => {
        set({
          accessToken: session?.accessToken ?? null,
          user: session?.user ?? null,
        })
      },
      clearAuth: () => set({ accessToken: null, user: null }),
    }),
    {
      name: AUTH_STORAGE_KEY,
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        accessToken: state.accessToken,
        user: state.user,
      }),
    },
  ),
)
