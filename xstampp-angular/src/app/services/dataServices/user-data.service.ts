import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AdminPasswordChangeRequestDTO, Icon, LoginRequestDTO, PageRequest, UserDTO } from '../../types/local-types';
import { PostRequestParameters, RequestService } from '../request.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';





@Injectable({
  providedIn: 'root'
})
export class UserDataService {

  constructor(private readonly request: RequestService) {
  }

  /**
   * changes the password of a user. You have to be the a system administator to use this function.
   * @param request the admin change password request
   * @param userId the id of the affected user
   */
  public setNewUserPassword(request: AdminPasswordChangeRequestDTO, userId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.request.performPUTRequest<boolean>('/api/auth/user/' + userId + '/password', JSON.stringify(request), false, true).then((value: boolean): void => {
        if (value) {
          resolve(true);
        } else {
          const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.error(error);
          reject(false);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(false);
      });
    });

  }

  /**
   * lists all users in the system
   * @param page this parameter handles the paging attribute to allow pagination of the data
   */
  public getAllUsers(page: PageRequest): Promise<UserDTO[]> {
    const postParameters: PostRequestParameters<UserDTO[]> = {
      path: '/api/auth/user/search',
      json: JSON.stringify(page),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<UserDTO[]>((resolve: (value?: UserDTO[] | PromiseLike<UserDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.request.performPOSTRequest<UserDTO[]>(postParameters).then((value: UserDTO[]): void => {
        if (value != null) {
          resolve(value);
        } else {
          const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
          console.log(error);
          reject(error);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(reason.error);
      });
    });
  }

  /**
   * delete a user from the system. you have to have system admin rights to use this function
   * @param userId the id of the affected user
   */
  public deleteUser(userId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.request.performDELETERequest<boolean>('/api/auth/user/' + userId, false, true).then((value: boolean): void => {
        if (value) {
          resolve(true);
        } else {
          const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.error(error);
          reject(false);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(false);
      });
    });
  }

  public deleteUserByUser(userId: string, userToDelete: LoginRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      console.log(JSON.stringify(userToDelete))
      this.request.performPUTRequest<boolean>('/api/auth/user/' + userId, JSON.stringify(userToDelete), false, true).then((value: boolean): void => {
        if (value) {
          resolve(true);
        } else {
          const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.error(error);
          reject(error);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(reason);
      });
    });
  }

  public confirmUserForEdit(userID: string, userForEdit:LoginRequestDTO): Promise<boolean>{
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void) => {
      this.request.performPUTRequest<boolean>('/api/auth/user/' + userID +'/compare', JSON.stringify(userForEdit), false, true).then((value: boolean): void => {
        if (value !== null) {
          resolve(true);
        } else {
          const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          resolve(false);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        resolve(false);
      });
    });
  }
  public setIcon(userId: string, icon: Icon): Promise<boolean> {

    return new Promise<boolean>((resolve, reject) => {
      this.request.performPUTRequest('/api/auth/user/' + userId + '/icon',JSON.stringify(icon),false, true).then(value => {

        if (value) {
          resolve(true);
        } else {
          const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.error(error);
          reject(false);
        }
      }).catch(reason => {
        console.error(reason);
        reject(false);
      });
    });
  }

  public setTheme(userId: string, themeId:string): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      this.request.performPUTRequest('/api/auth/user/' + userId + '/theme/' + themeId, JSON.stringify(themeId), false, true).then(value => {
        if (value) {
          resolve(true);
        } else {
          const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
          console.error(error);
          reject(false);
        }
      }).catch(reason => {
        console.error(reason);
        reject(false);
      });
    });
  }

  public getTheme(userId: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      this.request.performGETRequest('/api/auth/user/' + userId + '/theme', false, true).then(value => {

        if (value !== null) {
          const themeId = value as string;
          resolve(themeId);
        } else {
          //damit wird das default theme aus dem Backend angefordert
         resolve('0');
        }
      }).catch(reason => {
        console.error(reason);
        reject(false);
      });
    });
  }

  public getIcon(userId: string, userName: string): Promise<string> {

    return new Promise<string>((resolve, reject) => {
      if(userId === null && userName === "deleted User" || userId === undefined && userName ==="deleted User"){
        resolve('./../../assets/avatar/delete_icon.svg');
      }else if(userId !== null && userId !== undefined){

          this.request.performGETRequest('/api/auth/user/' + userId + '/icon', false, true).then((value: string) => {
            if (value) {
              const iconPath = value as string;
              resolve(iconPath);
            } else{
              resolve('./../../assets/avatar/round default.svg');
            }
          }).catch(reason => {
            console.error(reason);
            reject(false);
          });
        }else{
          resolve('./../../assets/avatar/round default.svg');
        }
    });
  }
  public getDefaultOrDeleteIcon(identifier: string){
    if(identifier === 'deleted User'){
      return './../../assets/avatar/delete_icon.svg';
    }else{
      return './../../assets/avatar/round default.svg';
    }
  }
  public getUserDisplay(userIds: UserDisplayRequestDTO): Promise<Map<string, string[]>>{
    console.warn(userIds)
    const postParameters: PostRequestParameters<UserDisplayRequestDTO> = {
      path: '/api/auth/user/display',
      json: JSON.stringify(userIds),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<Map<string, string[]>>((resolve: (value?: Map<string, string[]> | PromiseLike<Map<string, string[]>>) => void, reject: (reason?: any) => void): void => {
      this.request.performPOSTRequest<Map<string, string[]>>(postParameters).then((value: Map<string, string[]>): void => {
        console.warn(value)
        if (value !== null) {
          let testmap = new Map();
          Object.entries(value).forEach(([uuid, info]: [string, string[]]) =>
          {
            testmap.set(uuid, info);
          });

        resolve(testmap);
        } else {
          const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
          console.log(error);
          reject(error);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(reason.error);
      });
    });
  }

  public getEmail(userId: string){

    return new Promise<string>((resolve, reject) => {
      this.request.performGETRequest('/api/auth/user/' + userId + '/email', false, true).then((value: string) => {
        if (value) {
          const email = value as string;
          resolve(email);
        } else {

         resolve(value);
        }
      }).catch(reason => {
        console.error(reason);
        reject(false);
      });
    });
  }

  public setEmail(userId: string, user: LoginRequestDTO){
    if(user.email !== null){
      return new Promise<boolean>((resolve, reject) => {
        this.request.performPUTRequest('/api/auth/user/' + userId + '/setEmail',JSON.stringify(user), false, true).then((value: boolean) => {
          if (value) {
            resolve(true);
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.error(error);
            reject(false);
          }
        }).catch(reason => {
          console.error(reason);
          reject(false);
        });
      });
    }

  }

  /**
   public checkEmail(userId: string, email: string, options: SearchRequest){
    if(email !== null){
      return new Promise<boolean>((resolve, reject) => {
        this.request.performPUTRequest('/api/auth/user/' + userId + '/checkEmail/' + email,JSON.stringify(options), false, true).then((value: boolean) => {
          if (value) {
            resolve(true);
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.error(error);
            reject(false);
          }
        }).catch(reason => {
          console.error(reason);
          reject(false);
        });
      });
    }
  }
   */


  public setPasswordByUser(userId: string, user: LoginRequestDTO){
    if(user.password !== null){
      return new Promise<boolean>((resolve, reject) => {
        this.request.performPUTRequest('/api/auth/user/' + userId + '/user',JSON.stringify(user), false, true).then((value: boolean) => {
          if (value) {
            resolve(true);
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.error(error);
            reject(false);
          }
        }).catch(reason => {
          console.error(reason);
          reject(false);
        });
      });
    }

  }
  public setPasswordEmailByUser(userId: string, user: LoginRequestDTO){
      return new Promise<boolean>((resolve, reject) => {
        this.request.performPUTRequest('/api/auth/user/' + userId + '/user/both',JSON.stringify(user), false, true).then((value: boolean) => {
          if (value) {
            resolve(true);
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.error(error);
            reject(false);
          }
        }).catch(reason => {
          console.error(reason);
          reject(false);
        });
      });


  }


  public updateDisplayNameAndEmail(uid: string,emailDisplayName :AdminUserdataRequestDTO): Promise<boolean> {
    const postParameters: PostRequestParameters<boolean> = {
      path: '/api/auth/user/'+ uid +'/displayName',
      json: JSON.stringify(emailDisplayName),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.request.performPOSTRequest<boolean>(postParameters).then((value: boolean): void => {
        if (value != null) {
          resolve(value);
        } else {
          const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
          console.log(error);
          reject(error);
        }
      }).catch((reason: HttpErrorResponse) => {
        console.error(reason);
        reject(reason.error);
      });
    });
  }

}

export class AdminUserdataRequestDTO {
  displayName: string;
  email: string;
}

export class UserDisplayRequestDTO {
  public userIds: string[];

}


