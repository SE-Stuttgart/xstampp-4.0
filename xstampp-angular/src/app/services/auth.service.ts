import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})

// TODO: erstellen eines respons types
export class AuthService {
  tokenGraceTime: number = 5 * 60;

  constructor(private http: HttpClient, private configService: ConfigService) {
  }

  public setUserToken(token: string): void {
    localStorage.setItem('user', token);
  }

  public async getUserToken(): Promise<string> {
    const token: string = localStorage.getItem('user');

    const claimSet: any = this.JWTClaimSet(token);

    if (claimSet && claimSet.exp) {
      const expTime: number = claimSet.exp as number;

      if ((new Date().getTime() / 1000) + this.tokenGraceTime > expTime) {
        // if token is expired
        return null;
      } else {
        // if token is valid return current token
        return token;
      }
    } else {
      return null;
    }
  }

  public setProjectToken(token: string): void {
    sessionStorage.setItem('project', token);
  }

  public async getProjectToken(): Promise<string> {
    const token: string = sessionStorage.getItem('project');
    const claimSet: any = this.JWTClaimSet(token);

    if (claimSet && claimSet.exp) {
      const expTime: number = claimSet.exp as number;

      if ((new Date().getTime() / 1000) + this.tokenGraceTime > expTime) {
        // if token is expired
        const newToken: string = await this.requestProjectToken(claimSet.projectId, await this.getUserToken());
        if (newToken) {
          this.setProjectToken(newToken);
          return newToken;
        }
      } else {
        // if token is valid return current token
        return token;
      }
    }
    return null;
  }

  public async requestUserToken(email: string, password: string): Promise<string> {

    const options: { email: string, password: string } = { email, password };

    const response: any = await this.http.post(this.configService.getServerIp() + '/api/auth/login', options).toPromise();
    if (response && response.status === 'SUCCESS') {
      return response.token;
    } else {
      throw Error('could not get user token');
    }
  }

  public async requestProjectToken(projectId: string, userToken: string): Promise<string> {

    const httpOptions: { headers: HttpHeaders } = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': userToken
      })
    };

    const options: { projectId: string } = { projectId };

    const response: any = await this.http.post(this.configService.getServerIp() + '/api/auth/project-token', options, httpOptions).toPromise();
    if (response && response.status === 'SUCCESS') {
      return response.token;
    } else {
      return null;
    }
  }

  public async register(email: string, password: string, passwordRepeat: string, displayName: string): Promise<boolean> {
    const options: {
      email: string,
      password: string,
      passwordRepeat: string,
      displayName: string
    } = { email, password, passwordRepeat, displayName };

    const response: any = await this.http.post(this.configService.getServerIp() + '/api/auth/register', options).toPromise();

    if (response && response.status === 'SUCCESS') {
      return true;
    } else {
      throw Error('register failed');
    }
  }

  public getUserName(): string {
    const userToken: string = localStorage.getItem('user');
    if (userToken) {
      try {
        const jwt: any = this.JWTClaimSet(userToken);
        return jwt.displayName;
      } catch (e) {
        console.log(e);
      }
    }

    return null;
  }

  public getUserID(): string {
    const userToken: string = localStorage.getItem('user');
    if (userToken) {
      try {
        const jwt: any = this.JWTClaimSet(userToken);
        return jwt.uid;
      } catch (e) {
        console.log(e);
      }
    }

    return null;
  }

  public logout(): void {
    sessionStorage.removeItem('project');
    localStorage.removeItem('user');
  }

  public async refetchProjectToken(projectId: string): Promise<void> {
    const token: string = await this.requestProjectToken(projectId, await this.getUserToken());
    if (token) {
      this.setProjectToken(token);
    }
  }

  private base64Decode(base64String: string): string {
    return atob(base64String);
  }

  validateProjectToken(token: string, projectId: string): boolean {

    if (!token || !projectId) {
      return false;
    }

    const claimSet = this.JWTClaimSet(token);

    if (claimSet.projectId === projectId) {
      if (claimSet.exp > (new Date()).getTime()) {
        return true;
      }
    }
    return false;
  }

  private JWTClaimSet(token: string): any {
    let parts: string[];
    if (token) {
      parts = token.split('.');
      return JSON.parse(this.base64Decode(parts[1]));
    } else {
      return null;
    }
  }
}
