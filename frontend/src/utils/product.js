export function optimizeCloudinaryUrl(url, transformation = 'f_auto,q_auto,w_600') {
  if (!url || typeof url !== 'string' || !url.includes('/upload/')) {
    return url
  }

  const [prefix, suffix] = url.split('/upload/')
  return `${prefix}/upload/${transformation}/${suffix}`
}

export function formatCurrency(value) {
  if (typeof value !== 'number' && typeof value !== 'string') {
    return '$0.00'
  }

  const numeric = Number(value)
  if (Number.isNaN(numeric)) {
    return '$0.00'
  }

  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(numeric)
}
