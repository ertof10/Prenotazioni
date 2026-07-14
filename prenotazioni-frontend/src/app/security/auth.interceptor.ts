import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthService } from '../service/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const token = this.authService.getToken();

    if (token) {
      const requestConToken = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });

      return next.handle(requestConToken);
    }

    return next.handle(request);
  }
}