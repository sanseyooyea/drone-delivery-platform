const TOKEN_KEY = 'drone_token'
const USER_KEY = 'drone_user'

export interface UserInfo {
  userId: number
  username: string
  role: number
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getUserInfo(): UserInfo | null {
  const data = localStorage.getItem(USER_KEY)
  return data ? JSON.parse(data) : null
}

export function setUserInfo(info: UserInfo) {
  localStorage.setItem(USER_KEY, JSON.stringify(info))
}
