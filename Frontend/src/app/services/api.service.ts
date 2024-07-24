import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import { Observable, catchError, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})

export class ApiService {

  constructor(private http:HttpClient) { }

  private dataUrl:string = 'http://100.64.236.62:8080/api'

  public getData(url:string):Observable<any>{
    return this.http.get<any>(this.dataUrl + url, this.createHeaders())
  }

  public deleteDataId(url:string, id:number):Observable<{}>{
    const deleteUrl = `${this.dataUrl}${url}/${id}`;
    console.log(`Se elimino el item ${id} con exito`);
    return this.http.delete(deleteUrl);
  }
  
  public authenticateLogin(formValue: any){
    return this.http.post<any>(`${this.dataUrl}/security/authenticate`, formValue).pipe(
      catchError(this.handleError)
    );
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

  private createHeaders() {
    const token = localStorage.getItem('token_user');
    if (token) {
      return {
        headers: new HttpHeaders({
          'Authorization': `Bearer ${token}`
        })
      };
    } else {
      return {
        headers: new HttpHeaders()
      };
    }
  }

}