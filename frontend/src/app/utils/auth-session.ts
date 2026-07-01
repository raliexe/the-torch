const AUTH_SESSION_KEYS = [
  'currentRiderId',
  'currentRiderName',
  'currentTeamId',
  'currentTeamName',
] as const;

export function isLoggedIn(): boolean {
  return !!(
    sessionStorage.getItem('currentRiderId') ||
    sessionStorage.getItem('currentRiderName')
  );
}

export function clearAuthSession(): void {
  AUTH_SESSION_KEYS.forEach((key) => sessionStorage.removeItem(key));
}
