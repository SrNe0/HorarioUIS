import { HttpInterceptorFn } from '@angular/common/http';

export const authApiInterceptor: HttpInterceptorFn = (req, next) => {
  let clonedRequest = req;

  if (typeof window !== 'undefined' && localStorage.getItem('token_user')) {
    clonedRequest = req.clone({
      setHeaders:{
        Authorization: `Bearer ${localStorage.getItem('token_user')}`
      }
    });
  }
  
  return next(clonedRequest);
}