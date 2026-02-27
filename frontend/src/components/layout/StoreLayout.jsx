import { Outlet } from 'react-router-dom'
import GlobalNavbar from './GlobalNavbar'

function StoreLayout() {
  return (
    <>
      <GlobalNavbar />
      <Outlet />
    </>
  )
}

export default StoreLayout
