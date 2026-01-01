import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocumentService, DocumentRequestDTO } from '../document.service';
import { Router, RouterModule } from '@angular/router';

@Component({
    selector: 'app-document-generate',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './document-generate.component.html',
    styleUrls: ['./document-generate.component.scss']
})
export class DocumentGenerateComponent {
    selectedType: string = 'ATTESTATION_INSCRIPTION';

    request: any = {
        userId: 1, // Demo
        firstName: '',
        lastName: '',
        cin: '',
        cne: '',
        formation: 'Sciences de l\'Ingénieur',
        university: 'Ecole Marocaine des Sciences de l\'Ingénieur',
        laboratory: '',
        thesisTitle: '',
        directorName: '',
        soutenanceDate: '',
        soutenanceTime: '',
        soutenanceLocation: '',
        juryMembers: [],
        decision: 'ADMIS(E)',
        mention: 'Très Honorable'
    };

    juryMember: any = { name: '', grade: '', role: 'Rapporteur', institution: '' };

    loading: boolean = false;
    message: string = '';
    error: string = '';

    constructor(private documentService: DocumentService, private router: Router) { }

    addJuryMember(): void {
        if (this.juryMember.name) {
            this.request.juryMembers.push({ ...this.juryMember });
            this.juryMember = { name: '', grade: '', role: 'Rapporteur', institution: '' };
        }
    }

    removeJuryMember(index: number): void {
        this.request.juryMembers.splice(index, 1);
    }

    generate(): void {
        this.loading = true;
        this.message = '';
        this.error = '';

        let obs;
        switch (this.selectedType) {
            case 'ATTESTATION_INSCRIPTION':
                obs = this.documentService.generateAttestation(this.request);
                break;
            case 'AUTORISATION_SOUTENANCE':
                obs = this.documentService.generateAuthorization(this.request);
                break;
            case 'PROCES_VERBAL':
                obs = this.documentService.generatePV(this.request);
                break;
            case 'DEMANDE_MANUSCRITE':
                obs = this.documentService.generateHandwrittenRequest(this.request);
                break;
            case 'ATTESTATIONS_FORMATION':
                obs = this.documentService.generateTrainingCertificates(this.request);
                break;
            default:
                this.error = 'Type de document non supporté';
                this.loading = false;
                return;
        }

        obs.subscribe({
            next: (res) => {
                this.loading = false;
                this.message = 'Document généré avec succès !';
                setTimeout(() => this.router.navigate(['/documents']), 2000);
            },
            error: (err) => {
                this.loading = false;
                this.error = 'Erreur lors de la génération. ' + (err.error?.error || err.statusText);
            }
        });
    }
}
