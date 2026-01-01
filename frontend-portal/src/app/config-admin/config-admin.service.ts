import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ConfigAdminService {
    private apiUrl = 'http://localhost:8888/admin/configs';

    constructor(private http: HttpClient) { }

    getConfigFiles(): Observable<string[]> {
        return this.http.get<string[]>(this.apiUrl);
    }

    getConfigContent(fileName: string): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/${fileName}`);
    }

    updateConfig(fileName: string, properties: any): Observable<void> {
        return this.http.post<void>(`${this.apiUrl}/${fileName}`, properties);
    }
}
