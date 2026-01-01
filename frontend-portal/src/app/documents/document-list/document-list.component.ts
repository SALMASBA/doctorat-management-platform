import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentService, UploadedDocument, GeneratedDocument } from '../document.service';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-document-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './document-list.component.html',
    styleUrls: ['./document-list.component.scss']
})
export class DocumentListComponent implements OnInit {
    userId: number = 1; // Hardcoded for demo/testing
    uploadedDocs: UploadedDocument[] = [];
    generatedDocs: GeneratedDocument[] = [];
    activeTab: 'uploads' | 'generated' = 'uploads';
    loading: boolean = false;

    constructor(private documentService: DocumentService) { }

    ngOnInit(): void {
        this.loadDocuments();
    }

    loadDocuments(): void {
        this.loading = true;
        // Load Uploads
        this.documentService.getUserUploads(this.userId).subscribe({
            next: (data) => this.uploadedDocs = data,
            error: (err) => console.error(err)
        });

        // Load Generated
        this.documentService.getUserGenerated(this.userId).subscribe({
            next: (data) => {
                this.generatedDocs = data;
                this.loading = false;
            },
            error: (err) => {
                console.error(err);
                this.loading = false;
            }
        });
    }

    getPreviewUrl(doc: any, type: 'upload' | 'generated'): string {
        if (type === 'upload') {
            return this.documentService.getUploadPreviewUrl(doc.id);
        } else {
            return this.documentService.getGeneratedPreviewUrl(doc.id);
        }
    }

    getDownloadUrl(doc: any, type: 'upload' | 'generated'): string {
        if (type === 'upload') {
            return this.documentService.getUploadDownloadUrl(doc.id);
        } else {
            return this.documentService.getGeneratedDownloadUrl(doc.id);
        }
    }

    setActiveTab(tab: 'uploads' | 'generated'): void {
        this.activeTab = tab;
    }
}
