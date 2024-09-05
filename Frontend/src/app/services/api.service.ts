import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, catchError, throwError, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})

export class ApiService {

  constructor(private router:Router, private http:HttpClient) { }
  
  private dataUrl: string = 'http://100.112.128.60/api';
  
  // Declaración de la propiedad dataUrl
  // private dataUrl: string;

  // constructor(private router: Router, private http: HttpClient) { 
  //   const hostname = window.location.hostname;
  //   if (hostname === 'localhost' || hostname.startsWith('192.168.')) {
  //     this.dataUrl = 'http://192.168.0.100/api';
  //   } else {
  //     this.dataUrl = 'http://100.112.128.60/api';
  //   }
  // }

  public dataSubject = new BehaviorSubject<any[]>([]);
  public data$ = this.dataSubject.asObservable();

  public getData(url: string): Observable<any[]> {
    return this.http.get<any[]>(this.dataUrl + url).pipe(
      tap(data => this.dataSubject.next(data)),
      catchError(this.handleError)
    );
  }

  public deleteDataId(url: string, id: number): Observable<any> {
    const deleteUrl = `${this.dataUrl}${url}/${id}`;
    return this.http.delete(deleteUrl).pipe(
      tap(() => this.refreshData(url)), // Forzar la recarga de datos
      catchError(this.handleError)
    );
  }

  public authenticateLogin(formValue: any) {
    return this.http.post<any>(`${this.dataUrl}/security/authenticate`, formValue).pipe(
      catchError(this.handleError)
    );
  }

  public modifyDataId(url: string, objeto: any, idKey: string = 'id'): Observable<any> {
    const modifyURL = this.dataUrl + url;
    return this.http.put(modifyURL, objeto).pipe(
      tap((updatedObject: any) => {
        const currentData = this.dataSubject.getValue();
        const index = currentData.findIndex(item => item[idKey] === updatedObject[idKey]);
        if (index !== -1) {
          currentData[index] = updatedObject;
          this.dataSubject.next([...currentData]);
        }
      }),
      catchError(this.handleError)
    );
  }

  private refreshData(url: string): void {
    this.getData(url).subscribe();
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client-side error: ${error.error.message}`;
    } else {
      errorMessage = `Server-side error: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }

  isLogged(): boolean {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('token_user') ? true : false;
    }
    return false;
  }

  Logout() {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token_user');
      this.router.navigate(['/login']);
    }
  }
}

