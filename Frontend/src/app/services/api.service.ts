import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, BehaviorSubject, catchError, throwError, tap } from 'rxjs';
import { Router } from '@angular/router';
import ips from '../../assets/Ips.json';

@Injectable({
  providedIn: 'root'
})

export class ApiService{
  constructor(private router: Router, private http: HttpClient) { 
    if (typeof window !== 'undefined') {
      const hostname = window.location.hostname;
      console.log(hostname)
      if (hostname.startsWith('100.112.') || hostname === 'localhost') {
        this.dataUrl = this.ipData.tailScaleApiUrl;
      } else {
        this.dataUrl = this.ipData.localApiUrl;
      }
    } else {
      this.dataUrl = this.ipData.localApiUrl;
    }
    this.dataUrl = this.dataUrl + ':8080/api'
  }
  
  private dataUrl: string;
  private ipData: any = ips ;

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
