import { inject } from "@angular/core"
import { Router } from "@angular/router"

export const loginGuard = () => {
    const router = inject(Router)

    if (typeof window !== 'undefined' && localStorage.getItem('token_user')) {
        return true;
      } else {
        router.navigate(['/login']);
        return false;
      }
}