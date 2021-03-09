import { Injectable } from '@angular/core';
import { RequestService, PostRequestParameters } from '../request.service';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { reject } from 'q';
import { Theme, SearchRequest, } from 'src/app/types/local-types';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './../auth.service';

//TODO: Methoden fertig schreiben
@Injectable({
  providedIn: 'root'
})
export class ThemeDataService {

  constructor(private readonly requestService: RequestService, private readonly config: ConfigService, private readonly authService: AuthService) {
  }
  /**
      public createIcon( fileName: string, file: any): Promise<File> {
        return new Promise<File>((resolve, reject) => {
        //muesste man ausprobieren
        /*  const formData= new FormData();
          formData.append('image',file, fileName);
  */



  public createTheme(theme: Theme): Promise<Theme> {

    const postParameters: PostRequestParameters<Theme> = {
      path: '/api/auth/theme',
      json: JSON.stringify(theme),
      insertUserToken: this.config.useUserToken()
    };
    return new Promise<Theme>((resolve: (value?: Theme | PromiseLike<Theme>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<Theme>(postParameters)
        .then(value => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.error(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }


  public getThemeByID(themeId: string): Promise<Theme> {
    return new Promise<Theme>((resolve, reject) => {
      this.requestService.performGETRequest('/api/auth/theme/' + themeId, false, true)
        .then(value => {
          if (value != null) {
            const theme: Theme = value as Theme;
            resolve(theme);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });


  }

  public deleteTheme(themeId: string): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      this.requestService.performDELETERequest('/api/auth/theme/' + themeId, false, true)
        .then(value => {
          if (value != null) {
            resolve();
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });

  }
  public alterTheme(themeId: string, theme: Theme): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {
      this.requestService.performPUTRequest('/api/auth/theme/' + themeId, JSON.stringify(theme), false, true)
        .then(value => {
          if (value != null) {
            resolve();
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  // below are the methodes for the logo
  public createIcon(file: File): Promise<File> {

    return new Promise<File>((resolve, reject) => {
      this.requestService.performPOSTRequestFile('/api/auth/icon/', file, false, true)
        .then(value => {

          if (value != null) {
            const file: File = value as File;
            resolve(file);
          } else {
            const error = new Error(EXPECTED_ENTITY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }

  public deleteIcon(iconId: string) {

    return new Promise<boolean>((resolve, reject) => {
      this.requestService.performDELETERequest('/api/icon/' + iconId)
        .then(value => {
          if (value != null) {
            resolve();
          } else {
            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });
  }


  public getAllThemes(options: SearchRequest): Promise<Theme[]> {
    const postParameters: PostRequestParameters<Theme[]> = {
      path: '/api/auth/theme/search',
      json: JSON.stringify(options),
      insertUserToken: this.config.useUserToken()
    };
    return new Promise<Theme[]>((resolve: (value?: Theme[] | PromiseLike<Theme[]>) => void, reject: (reason?: any) => void): void => {
      this.requestService.performPOSTRequest<Theme[]>(postParameters)
        .then(value => {
          if (value != null) {
            resolve(value);
          } else {
            const error = new Error(EXPECTED_ENTITY_ARRAY_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }).catch(reason => {
          console.error(reason);
          reject(reason.error);
        }
        );
    });

  }


  public getLogo(): Promise<string> {
    return new Promise<string>((resolve, reject) => {


      this.requestService.performGETRequest('/api/auth/icon', false, true).then(
        (value: string) => {

          if (value !== null) {
            const logo = value as string;
            resolve(logo);
          } else {
            //if there was no logo uploaded
            resolve(null)
          }
        }
      ).catch((reason: HttpErrorResponse) => {
        console.log(reason);
        reject(reason.error);
      });
    }




    );
  }

  public deleteLogo(): Promise<boolean> {
    return new Promise<boolean>((resolve, reject) => {


      this.requestService.performDELETERequest('/api/auth/icon/delete', false, true).then(
        value => {
          if (value != null) {
            resolve(true);
          } else {

            const error = new Error(EXPECTED_BOOLEAN_NOT_NULL);
            console.log(error);
            reject(error);
          }
        }
      ).catch(reason => {
        console.error(reason);
        reject(reason.error);
      });
    }




    );
  }

}



