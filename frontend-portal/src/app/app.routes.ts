import { Routes } from '@angular/router';
import { DocumentListComponent } from './documents/document-list/document-list.component';
import { DocumentUploadComponent } from './documents/document-upload/document-upload.component';
import { DocumentGenerateComponent } from './documents/document-generate/document-generate.component';
import { ConfigAdminComponent } from './config-admin/config-admin.component';
import { PortalLayoutComponent } from './layouts/portal-layout.component';

export const routes: Routes = [
    {
        path: '',
        component: PortalLayoutComponent,
        children: [
            { path: '', redirectTo: 'documents', pathMatch: 'full' },
            { path: 'documents', component: DocumentListComponent },
            { path: 'upload', component: DocumentUploadComponent },
            { path: 'generate', component: DocumentGenerateComponent }
        ]
    },
    { path: 'admin/config', component: ConfigAdminComponent }
];
