import { Navigate, Route, Routes } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'
import PublicOnlyRoute from './routes/PublicOnlyRoute'
import ProtectedRoute from './routes/ProtectedRoute'
import AdminRoute from './routes/AdminRoute'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import OAuth2CallbackPage from './pages/auth/OAuth2CallbackPage'
import AdminDashboardPage from './pages/admin/AdminDashboardPage'
import CustomerHomePage from './pages/customer/CustomerHomePage'
import CartPage from './pages/customer/CartPage'
import MyOrdersPage from './pages/customer/MyOrdersPage'
import OrderDetailsPage from './pages/customer/OrderDetailsPage'
import WishlistPage from './pages/customer/WishlistPage'
import NotFoundPage from './pages/shared/NotFoundPage'
import StoreLayout from './components/layout/StoreLayout'

function App() {
  return (
    <>
      <Routes>
        <Route path="/auth" element={<Navigate to="/auth/login" replace />} />
        <Route
          path="/auth/login"
          element={
            <PublicOnlyRoute>
              <LoginPage />
            </PublicOnlyRoute>
          }
        />
        <Route
          path="/auth/register"
          element={
            <PublicOnlyRoute>
              <RegisterPage />
            </PublicOnlyRoute>
          }
        />
        <Route path="/auth/oauth2/callback" element={<OAuth2CallbackPage />} />
        <Route
          path="/admin/dashboard"
          element={
            <AdminRoute>
              <AdminDashboardPage />
            </AdminRoute>
          }
        />
        <Route
          element={
            <ProtectedRoute>
              <StoreLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/" element={<CustomerHomePage />} />
          <Route path="/wishlist" element={<WishlistPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/orders" element={<MyOrdersPage />} />
          <Route path="/orders/:id" element={<OrderDetailsPage />} />
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>

      <Toaster
        position="top-right"
        containerStyle={{ top: 84, right: 16 }}
        toastOptions={{
          duration: 3000,
          style: { zIndex: 30 },
        }}
      />
    </>
  )
}

export default App
