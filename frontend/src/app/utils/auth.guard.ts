import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { isLoggedIn } from './auth-session';

export const authGuard: CanActivateFn = () => {
  if (isLoggedIn()) {
    return true;
  }

  return inject(Router).createUrlTree(['/home-guest']);
};
