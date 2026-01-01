import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UploadedDocument {
  id: number;
  fileName: string;
  fileType: string;
  filePath: string;
  userId: number;
  documentType: string;
  uploadDate: string;
}

export interface GeneratedDocument {
  id: number;
  title: string;
  reference: string;
  documentType: string;
  userId: number;
  generationDate: string;
}

export interface DocumentRequestDTO {
  firstName: string;
  lastName: string;
  cin: string;
  cne: string;
  formation: string;
  university: string;
  laboratory: string;
  thesisTitle: string;
  userId: number;
  directorName?: string;
}

export interface JuryMember {
  name: string;
  grade: string;
  role: string;
  institution: string;
}

export interface SoutenanceAuthorizationDTO extends DocumentRequestDTO {
  soutenanceDate?: string; // ISO format
  soutenanceTime?: string;
  soutenanceLocation?: string;
  juryMembers?: JuryMember[];
}

export interface ProcesVerbalDTO extends SoutenanceAuthorizationDTO {
  decision?: string;
  mention?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = '/api/documents'; // Proxy points to http://localhost:8083

  constructor(private http: HttpClient) { }

  // Upload
  uploadDocument(file: File, userId: number, type: string): Observable<UploadedDocument> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId.toString());
    formData.append('type', type);

    return this.http.post<UploadedDocument>(`${this.apiUrl}/upload`, formData);
  }

  // Generate Attestation
  generateAttestation(request: DocumentRequestDTO): Observable<GeneratedDocument> {
    return this.http.post<GeneratedDocument>(`${this.apiUrl}/attestation-inscription`, request);
  }

  // Generate Authorization
  generateAuthorization(request: SoutenanceAuthorizationDTO): Observable<GeneratedDocument> {
    return this.http.post<GeneratedDocument>(`${this.apiUrl}/autorisation-soutenance`, request);
  }

  // Generate PV
  generatePV(request: ProcesVerbalDTO): Observable<GeneratedDocument> {
    return this.http.post<GeneratedDocument>(`${this.apiUrl}/proces-verbal`, request);
  }

  // Generate Handwritten Request
  generateHandwrittenRequest(request: DocumentRequestDTO): Observable<GeneratedDocument> {
    return this.http.post<GeneratedDocument>(`${this.apiUrl}/handwritten-request`, request);
  }

  // Generate Training Certificates
  generateTrainingCertificates(request: DocumentRequestDTO): Observable<GeneratedDocument> {
    return this.http.post<GeneratedDocument>(`${this.apiUrl}/training-certificates`, request);
  }

  // Get User Uploads
  getUserUploads(userId: number): Observable<UploadedDocument[]> {
    return this.http.get<UploadedDocument[]>(`${this.apiUrl}/user/${userId}/uploads`);
  }

  // Get User Generated Docs
  getUserGenerated(userId: number): Observable<GeneratedDocument[]> {
    return this.http.get<GeneratedDocument[]>(`${this.apiUrl}/user/${userId}/generated`);
  }

  // Preview Upload Link
  getUploadPreviewUrl(id: number): string {
    return `${this.apiUrl}/preview/upload/${id}`;
  }

  // Preview Generated Link
  getGeneratedPreviewUrl(id: number): string {
    return `${this.apiUrl}/preview/generated/${id}`;
  }

  // Download URLs
  getUploadDownloadUrl(id: number): string {
    return `${this.apiUrl}/download/upload/${id}`;
  }

  getGeneratedDownloadUrl(id: number): string {
    return `${this.apiUrl}/download/generated/${id}`;
  }
}
