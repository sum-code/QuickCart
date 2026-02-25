export function getPrimaryRole(user) {
  if (!user?.roles || !Array.isArray(user.roles) || user.roles.length === 0) {
    return 'USER'
  }

  return user.roles.includes('ADMIN') ? 'ADMIN' : user.roles[0]
}

export function isAdmin(user) {
  return getPrimaryRole(user) === 'ADMIN'
}

export function getAuthRedirectPath(user) {
  return isAdmin(user) ? '/admin/dashboard' : '/'
}

export function getErrorMessage(error, fallback = 'Something went wrong. Please try again.') {
  if (!error) {
    return fallback
  }

  const apiMessage = error?.response?.data?.message
  if (apiMessage) {
    return apiMessage
  }

  return error?.message || fallback
}
