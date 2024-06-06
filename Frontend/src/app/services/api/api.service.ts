import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http:HttpClient) { }

  private dataUrl:string = 'http://100.64.236.62:8080/api/'

  public getData(url:string):Observable<any>{
    console.log(this.dataUrl + url)
    return this.http.get<any>(this.dataUrl + url)
  }

  public deleteDataId(url:string, id:number):Observable<{}>{
    const deleteUrl = `${this.dataUrl}${url}/${id}`;
    console.log(deleteUrl, `se elimino un item con id = ${id}`);
    return this.http.delete(deleteUrl);
  }
}


