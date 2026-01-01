import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService } from '../document.service';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-document-upload',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './document-upload.component.html',
    styleUrls: ['./document-upload.component.scss']
})
export class DocumentUploadComponent {
    userId: number = 1; // Demo
    selectedFile: File | null = null;
    documentType: string = 'CV';
    uploading: boolean = false;
    message: string = '';
    error: string = '';

    documentTypes = [
        { value: 'CV', label: 'Curriculum Vitae' },
        { value: 'DIPLOMA', label: 'Diplôme' },
        { value: 'TRANSCRIPT', label: 'Relevé de notes' },
        { value: 'THESIS_DRAFT', label: 'Projet de thèse' },
        { value: 'OTHER', label: 'Autre' }
    ];

    constructor(private documentService: DocumentService, private router: Router) { }

    onFileSelected(event: any): void {
        this.selectedFile = event.target.files[0];
    }

    upload(): void {
        if (!this.selectedFile) {
            this.error = 'Veuillez sélectionner un fichier.';
            return;
        }

        this.uploading = true;
        this.message = '';
        this.error = '';

        this.documentService.uploadDocument(this.selectedFile, this.userId, this.documentType).subscribe({
            next: (res) => {
                this.uploading = false;
                this.message = 'Document déposé avec succès !';
                setTimeout(() => this.router.navigate(['/documents']), 2000);
            },
            error: (err) => {
                this.uploading = false;
                this.error = 'Erreur lors du dépôt du document. ' + (err.error?.error || err.statusText);
            }
        });
    }
}
