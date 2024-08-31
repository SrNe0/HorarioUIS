import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse} from '@angular/common/http';
import { Observable, catchError, throwError} from 'rxjs';
import { Router } from '@angular/router';


@Injectable({
  providedIn: 'root'
})

export class ApiService {

  constructor(private router:Router, private http:HttpClient) { }

  private dataUrl:string = 'http://100.112.128.60:8080/api'

  public getData(url:string):Observable<any>{
    return this.http.get<any>(this.dataUrl + url)
  }

  public deleteDataId(url:string, id:number):Observable<{}>{
    const deleteUrl = `${this.dataUrl}${url}/${id}`;
    console.log(`Se elimino el item ${id} con exito`);
    return this.http.delete(deleteUrl);
  }
  
  public authenticateLogin(formValue: any){
    console.log(this.dataUrl)
    return this.http.post<any>(`${this.dataUrl}/security/authenticate`, formValue).pipe(
      catchError(this.handleError)
    );
  }

  public modifyDataId(url:string, objeto:object):Observable<{}>{
    const modifyURL = this.dataUrl + url 
    console.log("modificando", objeto)
    console.log(modifyURL)
    return this.http.put(modifyURL, objeto)
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
    return localStorage.getItem('token_user') ? true : false;
  }

  Logout(){
    localStorage.removeItem('token_user')
    this.router.navigate(['/login'])
  }
}