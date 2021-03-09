import { HttpErrorResponse } from '@angular/common/http';
import { PageRequest } from './../../types/local-types';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { GroupRequestDTO, MemberDTO } from '../../types/local-types';
import { ConfigService } from '../config.service';

@Injectable({
  providedIn: 'root'
})
export class GroupDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
  }

  public alterGroup(groupId: string, group: GroupRequestDTO): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/auth/group/' + groupId, JSON.stringify(group), false, true)
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
          } else {
            const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public deleteGroup(groupId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/auth/group/' + groupId, false, true)
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
          } else {
            const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            //console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          //console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  // TODO Typing of Response
  public createGroup(group: GroupRequestDTO): Promise<any> {
    const postParameters: PostRequestParameters<GroupRequestDTO> = {
      path: '/api/auth/group',
      json: JSON.stringify(group),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<any>((resolve: (value?: any | PromiseLike<any>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<GroupRequestDTO>(postParameters)
        .then((value: GroupRequestDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public getAllMembersOfGroup(groupId: string): Promise<MemberDTO[]> {
    return new Promise<MemberDTO[]>((resolve: (value?: MemberDTO[] | PromiseLike<MemberDTO[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<MemberDTO[]>('/api/auth/group/' + groupId + '/users', false, true)
        .then((value: MemberDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public joinGroup(groupId: string, memberEmail: string, accessLevel: string): Promise<MemberDTO> {
    const postParameters: PostRequestParameters<MemberDTO> = {
      path: '/api/auth/group/' + groupId + '/member/' + encodeURI(memberEmail),
      json: JSON.stringify(accessLevel),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<MemberDTO>((resolve: (value?: MemberDTO | PromiseLike<MemberDTO>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<MemberDTO>(postParameters)
        .then((value: MemberDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public changeAccessLevel(groupId: string, memberId: string, accessLevel: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPUTRequest<boolean>('/api/auth/group/' + groupId + '/member/' + memberId, JSON.stringify(accessLevel), false, true)
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
          } else {
            const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public leaveGroup(groupId: string, memberId: string): Promise<boolean> {
    return new Promise<boolean>((resolve: (value?: boolean | PromiseLike<boolean>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performDELETERequest<boolean>('/api/auth/group/' + groupId + '/member/' + memberId, false, true)
        .then((value: boolean): void => {
          if (value != null) {
            resolve(true);
          } else {
            const error: Error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  // TODO Typing
  public getAllGroupsOfUser(userId: string): Promise<GroupResponseDTO[]> {
    return new Promise<any[]>((resolve: (value?: any[] | PromiseLike<any[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<GroupResponseDTO[]>('/api/auth/user/' + userId + '/groups', false, true, new GroupResponseDTO())
        .then((value: GroupResponseDTO[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public getAllBlockingGroupsOfUser(userId: string): Promise<BlockingGroupResponseDTO> {
    return new Promise<BlockingGroupResponseDTO>((resolve: (value?: BlockingGroupResponseDTO | PromiseLike<BlockingGroupResponseDTO>) => void,
      reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<BlockingGroupResponseDTO>(
        '/api/auth/group?blockedUserId=' + userId,
        false,
        true,
        new BlockingGroupResponseDTO()
      )
        .then((value: BlockingGroupResponseDTO): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }
  // TODO Typing
  public getAllProjectsForGroup(groupID: string): Promise<any[]> {
    return new Promise<any[]>((resolve: (value?: any[] | PromiseLike<any[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performGETRequest<any[]>('/api/auth/group/' + groupID + '/projects', false, true)
        .then((value: any[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }

  // TODO Typing
  public getAllGroupsSysAdmin(page: PageRequest): Promise<any[]> {
    const postParameters: PostRequestParameters<any[]> = {
      path: '/api/auth/group/search',
      json: JSON.stringify(page),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<any[]>((resolve: (value?: any[] | PromiseLike<any[]>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<any[]>(postParameters)
        .then((value: any[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch((reason: HttpErrorResponse) => {
          console.error(reason);
          reject(reason.error);
        });
    });
  }

  // TODO Typing and fix typo in backend abandoned
  public getAllGroupsWithoutLeaderSysAdmin(page: PageRequest): Promise<any[]> {
    const postParameters: PostRequestParameters<any[]> = {
      path: '/api/auth/group/search/abandoned',
      json: JSON.stringify(page),
      insertProjectToken: false,
      insertUserToken: true
    };
    return new Promise<any[]>((resolve: (value?: any[] | PromiseLike<any[]>) => void, reject: (reason?: any) => void): void  => {
      this.requestService.performPOSTRequest<any[]>(postParameters)
        .then((value: any[]): void => {
          if (value != null) {
            resolve(value);
          } else {
            const error: Error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
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

export class BlockingGroupResponseDTO {
  ONLY_ADMIN_MULTI_USER?: string[] = null;
  ONLY_USER_GROUP_WITH_PROJECTS?: string[] = null;
}

export class GroupResponseDTO {
  accessLevel: string = null;
  description: string = null;
  id: string = null;
  name: string = null;
  private: boolean = null;
  deletion?: string;
}
