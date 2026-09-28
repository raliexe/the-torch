import { HttpInterceptorFn } from '@angular/common/http';

export const ngrokInterceptor: HttpInterceptorFn = (req, next) => {
  if (typeof window !== 'undefined' && window.location.hostname.includes('ngrok')) {
    req = req.clone({
      setHeaders: {
        'ngrok-skip-browser-warning': 'true',
      },
    });
  }

  return next(req);
};
