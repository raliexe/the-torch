import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../globals/globals';
import { CreateRiderRequest } from '../dto/rider/create-rider-request';
import { Rider } from '../dto/rider/rider';

@Injectable({
  providedIn: 'root',
})
export class RiderService {
  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
  ) {}

  private get riderBaseURI(): string {
    return `${this.globals.backendUri}/riders`;
  }

  getRider(name: string): Observable<Rider> {
    const params = new HttpParams().set('name', name);
    return this.httpClient.get<Rider>(this.riderBaseURI, {params});
  }

  loginRider(studentMail: string, password: string): Observable<Rider> {
    const params = new HttpParams()
      .set('name', studentMail.trim())
      .set('tuidToken', password);
    return this.httpClient.get<Rider>(this.riderBaseURI, {params});
  }

  createRider(request: CreateRiderRequest): Observable<Rider> {
    return this.httpClient.post<Rider>(this.riderBaseURI, request);
  }
}
