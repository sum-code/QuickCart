function normalizeRole(role) {
  if (!role || typeof role !== 'string') {
    return null
  }

  return role.startsWith('ROLE_') ? role.substring(5) : role
}

export function getPrimaryRole(user) {
  if (!user?.roles || !Array.isArray(user.roles) || user.roles.length === 0) {
    return 'USER'
  }

  const normalizedRoles = user.roles.map(normalizeRole).filter(Boolean)
  return normalizedRoles.includes('ADMIN') ? 'ADMIN' : normalizedRoles[0]
}

export function hasAdminRole(user) {
  return getPrimaryRole(user) === 'ADMIN'
}

export function getAuthRedirectPath(user) {
  return hasAdminRole(user) ? '/admin/dashboard' : '/'
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
