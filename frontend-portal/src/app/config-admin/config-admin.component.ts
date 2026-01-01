import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ConfigAdminService } from './config-admin.service';

@Component({
    selector: 'app-config-admin',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './config-admin.component.html',
    styleUrls: ['./config-admin.component.scss']
})
export class ConfigAdminComponent implements OnInit {
    files: string[] = [];
    selectedFile: string = '';
    configData: { key: string, value: string }[] = [];
    loading: boolean = false;
    message: string = '';

    constructor(private configService: ConfigAdminService) { }

    ngOnInit(): void {
        this.loadFiles();
    }

    loadFiles(): void {
        this.configService.getConfigFiles().subscribe(files => this.files = files);
    }

    onFileSelect(fileName: string): void {
        this.selectedFile = fileName;
        this.loading = true;
        this.configService.getConfigContent(fileName).subscribe(data => {
            this.configData = Object.keys(data).map(key => ({ key, value: data[key] }));
            this.loading = false;
        });
    }

    saveConfig(): void {
        const properties: any = {};
        this.configData.forEach(item => {
            if (item.key.trim()) {
                properties[item.key] = item.value;
            }
        });

        this.configService.updateConfig(this.selectedFile, properties).subscribe(() => {
            this.message = 'Configuration enregistrée avec succès !';
            setTimeout(() => this.message = '', 3000);
        });
    }

    addField(): void {
        this.configData.push({ key: '', value: '' });
    }

    removeField(index: number): void {
        this.configData.splice(index, 1);
    }
}
