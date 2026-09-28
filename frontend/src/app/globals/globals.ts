import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class Globals {
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  get backendUri(): string {
    if (isPlatformBrowser(this.platformId)) {
      const { hostname } = window.location;

      if (hostname === 'localhost' || hostname === '127.0.0.1') {
        return 'http://localhost:8080/api';
      }

      return '/api';
    }

    const internalBackendUrl = process.env['BACKEND_INTERNAL_URL'];

    if (internalBackendUrl) {
      return `${internalBackendUrl.replace(/\/$/, '')}/api`;
    }

    return 'http://127.0.0.1:8080/api';
  }
}
