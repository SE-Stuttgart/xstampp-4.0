import { RequestService, PostRequestParameters } from '../request.service';
import { ImplementationConstraintRequestDTO, ImplementationConstraintResponseDTO } from '../../types/local-types';
import { Injectable } from '@angular/core';
import { ConfigService } from '../config.service';
import { EXPECTED_BOOLEAN_NOT_NULL, EXPECTED_ENTITY_NOT_NULL, EXPECTED_ENTITY_ARRAY_NOT_NULL } from './../../globals';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class ImplementationConstraintDataService {

    constructor(private readonly requestService: RequestService, private readonly config: ConfigService) {
    }

    /**
     * Requests creation of a new implementation constraint from backend
     * @param projectId current project's id
     * @param implConst Implementation constraint data supplied by user
     */
    createImplementationConstraint(projectId: string, implConst: ImplementationConstraintRequestDTO): Promise<ImplementationConstraintResponseDTO> {
        const postParameters: PostRequestParameters<ImplementationConstraintRequestDTO> = {
            path: '/api/project/' + projectId + '/implementation-constraint',
            json: JSON.stringify(implConst),
            insertProjectToken: this.config.useProjectToken()
        };
        return new Promise<ImplementationConstraintResponseDTO>((resolve: (value?: ImplementationConstraintResponseDTO | Promise<ImplementationConstraintResponseDTO>) => void,
            reject: (reason?: any) => void): void => {
            this.requestService.performPOSTRequest<ImplementationConstraintResponseDTO>(postParameters)
                .then((value: ImplementationConstraintResponseDTO) => {
                    if (value != null) {
                        const createdImplConstraint: ImplementationConstraintResponseDTO = value as ImplementationConstraintResponseDTO;
                        resolve(createdImplConstraint);
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

    /**
     * Changes implementation constraint with id=implConstId to contain the values of implConst.
     * @param projectId id of current project
     * @param implConstId id of implementation constraint to be changed
     * @param implConst the new values to be set in the implementation contraint with id implConstId
     */
    alterImplementationConstraint(projectId: string, implConstId: string, implConst: ImplementationConstraintRequestDTO): Promise<ImplementationConstraintResponseDTO> {
        return new Promise<ImplementationConstraintResponseDTO>((resolve: (value?: ImplementationConstraintResponseDTO | Promise<ImplementationConstraintResponseDTO>) => void,
            reject: (reason?: any) => void): void => {
            this.requestService.performPUTRequest('/api/project/' + projectId + '/implementation-constraint/' + implConstId,
                JSON.stringify(implConst), this.config.useProjectToken())
                .then((value: ImplementationConstraintResponseDTO) => {
                    if (value != null) {
                        resolve();
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

    /**
     * Deletes a single implementation constraint with id projectId
     * @param projectId id of current project
     * @param implConstId id of implementation constraint to be deleted
     */
    deleteImplementationConstraint(projectId: string, implConstId: string): Promise<boolean> {
        return new Promise<boolean>((resolve: (value?: boolean | Promise<boolean>) => void, reject: (reason?: any) => void): void => {
            this.requestService.performDELETERequest('/api/project/' + projectId + '/implementation-constraint/' + implConstId, this.config.useProjectToken())
                .then((value: Boolean) => {
                    if (value != null) {
                        resolve();
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

    /**
     * Request implementation constraint with id implConstId from backend.
     * @param projectId id of current project
     * @param implConstId id of implementation constraint to be retrieved from backend
     */
    getImplementationConstraintById(projectId: string, implConstId: string): Promise<ImplementationConstraintResponseDTO> {
        return new Promise<ImplementationConstraintResponseDTO>((resolve: (value?: ImplementationConstraintResponseDTO | Promise<ImplementationConstraintResponseDTO>) => void,
            reject: (reason?: any) => void): void => {
            this.requestService.performGETRequest('/api/project/' + projectId + '/implementation-constraint/' + implConstId, this.config.useProjectToken())
                .then((value: ImplementationConstraintResponseDTO) => {
                    if (value != null) {
                        const receivedImplConstraints: ImplementationConstraintResponseDTO = value as ImplementationConstraintResponseDTO;
                        resolve(receivedImplConstraints);
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

    /**
     * Loss Scenario are associated with 0..n implementation constraints.
     * This method retrieves all implementation constraints pertaining to loss scenario with id=lsId.
     * @param projectId id of current project
     * @param lsId id of loss scenario of interest
     */
    getImplementationConstraintsByLsId(projectId: string, lsId: string): Promise<ImplementationConstraintResponseDTO[]> {
        return new Promise<ImplementationConstraintResponseDTO[]>((resolve: (value?: ImplementationConstraintResponseDTO[]
            | Promise<ImplementationConstraintResponseDTO[]>) => void,
            reject: (reason?: any) => void): void => {
            this.requestService.performGETRequest('/api/project/' + projectId + '/implementation-constraint/loss-scenario/' + lsId, this.config.useProjectToken())
                .then((value: ImplementationConstraintResponseDTO[]) => {
                    if (value != null) {
                        const receivedImplConstraints: ImplementationConstraintResponseDTO[] = value as ImplementationConstraintResponseDTO[];
                        resolve(receivedImplConstraints);
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
}
