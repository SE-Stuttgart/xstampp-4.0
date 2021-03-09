import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { ConfigService } from './config.service';
import { validate } from './JSON-validator';
import { OperatorFunction } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  constructor(private authService: AuthService, private http: HttpClient, private config: ConfigService) {
  }

  // general methods for perform requests to the backend
  public async performGETRequest<T>(path: string, insertProjectToken: boolean = true, insertUserToken: boolean = false, interfaceObj?: any | T): Promise<T> {
    if (insertProjectToken && insertUserToken) {
      throw Error('invalid request configuration');
    }

    if (insertProjectToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getProjectToken()
        })
      };
      return this.http.get<T>(this.config.getServerIp() + path, httpOptions).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    } else if (insertUserToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getUserToken()
        })
      };
      return this.http.get<T>(this.config.getServerIp() + path, httpOptions).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    } else {
      return this.http.get<T>(this.config.getServerIp() + path).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    }
  }

  public async performPOSTRequestBlob(param: PostRequestParameters<Blob>): Promise<Blob> {
    // Setting default parameter values
    if (param == null) {
      throw Error('parameter null/undefined not allowed');
    } else if (param.insertProjectToken && param.insertUserToken) {
      throw Error('invalid request configuration');
    } else if (param.insertProjectToken === undefined && param.insertUserToken === undefined) {
      param.insertProjectToken = true;
      param.insertUserToken = false;
    } else {
      param.insertProjectToken = !!param.insertProjectToken;
      param.insertUserToken = !!param.insertUserToken;
    }
    if (param.acceptMime === undefined) {
      param.acceptMime = 'application/json';
    }

    // Building HTTP header
    let headerValues = {
      'Content-Type': 'application/json',
      'Accept': param.acceptMime
    };
    if (param.insertProjectToken) {
      headerValues['Authorization'] = await this.authService.getProjectToken();
    } else if (param.insertUserToken) {
      headerValues['Authorization'] = await this.authService.getUserToken();
    }
    class HttpOptions {
      headers: HttpHeaders;
      responseType: 'blob';
      observe: 'body';
    }
    const httpOptions: HttpOptions = { headers: new HttpHeaders(headerValues), responseType: 'blob', observe: 'body' };

    return this.http.post(this.config.getServerIp() + param.path, param.json, httpOptions).toPromise<Blob>();
  }

  public async performPOSTRequest<T>(param: PostRequestParameters<T>): Promise<T> {
    // Setting default parameter values
    if (param == null) {
      throw Error('parameter null/undefined not allowed');
    } else if (param.insertProjectToken && param.insertUserToken) {
      throw Error('invalid request configuration');
    } else if (param.insertProjectToken === undefined && param.insertUserToken === undefined) {
      param.insertProjectToken = true;
      param.insertUserToken = false;
    } else {
      param.insertProjectToken = !!param.insertProjectToken;
      param.insertUserToken = !!param.insertUserToken;
    }
    if (param.acceptMime === undefined) {
      param.acceptMime = 'application/json';
    }

    // Building HTTP header
    let headerValues = {
      'Content-Type': 'application/json',
      'Accept': param.acceptMime
    };
    if (param.insertProjectToken) {
      headerValues['Authorization'] = await this.authService.getProjectToken();
    } else if (param.insertUserToken) {
      headerValues['Authorization'] = await this.authService.getUserToken();
    }
    const httpOptions = { headers: new HttpHeaders(headerValues) };

    // Finalizing HTTP request
    const processingFunction: OperatorFunction<T, T> =
      tap((response: T) => {
        if (!!param.interfaceObj) {
          return validate(response, param.interfaceObj);
        }
      });

    return this.http.post<T>(this.config.getServerIp() + param.path, param.json, httpOptions)
      .pipe(processingFunction).toPromise();
  }

  public async performPOSTRequestFile(path: string, file: File, insertProjectToken: boolean = true, insertUserToken: boolean = false): Promise<Object> {


    if (insertProjectToken && insertUserToken) {
      throw Error('invalid request configuration');
    }

    if (insertProjectToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getProjectToken()
        })
      };
      return this.http.post(this.config.getServerIp() + path, file, httpOptions).toPromise();
    } else if (insertUserToken) {
      console.log('hier')
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getUserToken()
        })
      };
      console.log(httpOptions)
      return this.http.post(this.config.getServerIp() + path, file, httpOptions).toPromise();
    } else {
      return this.http.post(this.config.getServerIp() + path, file).toPromise();
    }
  }

  public async performDELETERequest<T>(path: string, insertProjectToken: boolean = true, insertUserToken: boolean = false, interfaceObj?: any | T): Promise<T> {

    if (insertProjectToken && insertUserToken) {
      throw Error('invalid request configuration');
    }

    if (insertProjectToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getProjectToken()
        })
      };
      return this.http.delete<T>(this.config.getServerIp() + path, httpOptions).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();

    } else if (insertUserToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getUserToken()
        })
      };
      return this.http.delete<T>(this.config.getServerIp() + path, httpOptions).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    } else {
      return this.http.delete<T>(this.config.getServerIp() + path).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    }
  }

  public async performPUTRequest<T>(path: string, json: string, insertProjectToken: boolean = true, insertUserToken: boolean = false, interfaceObj?: any | T): Promise<T> {

    if (insertProjectToken && insertUserToken) {
      throw Error('invalid request configuration');
    }

    if (insertProjectToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getProjectToken()
        })
      };
      return this.http.put<T>(this.config.getServerIp() + path, json, httpOptions).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    } else if (insertUserToken) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': await this.authService.getUserToken()
        })
      };
      return this.http.put<T>(this.config.getServerIp() + path, json, httpOptions).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    } else {
      return this.http.put<T>(this.config.getServerIp() + path, json).pipe(
        tap((response: T) => {
          if (!!interfaceObj) {
            return validate(response, interfaceObj);
          }
        })
      ).toPromise();
    }
  }
}

export interface PostRequestParameters<T> {
  /** The HTTP Request-URI */
  path: string;
  /** This JSON will be attached to the HTTP Request */
  json: string;
  /** Whether to use the project token for authorization.
   * insertProjectToken and insertUserToken can't be true
   * at the same time. If both are undefined,
   * insertProjectToken is true by default. */
  insertProjectToken?: boolean;
  /** Whether to use the user token for authorization.
   * insertProjectToken and insertUserToken can't be true
   * at the same time. If both are undefined,
   * insertProjectToken is true by default. */
  insertUserToken?: boolean;
  /** Pass a class instance to validate the response JSON against
   * the passed instance. If the response JSON misses attributes
   * or contains attributes not included in the passed instance,
   * messages about this get printed to the console. If the response
   * JSON is an array, each element in the array gets validated
   * against the passed instance. */
  interfaceObj?: any | T;
  /** Defines a custom MIME type to be accepted by this HTTP
   * request. 'application/json' by default. */
  acceptMime?: string;
}
