import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-portal-layout',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="app-header">
      <div class="container text-center py-4">
        <div class="logo justify-content-center">
          <span class="logo-icon">🎓</span>
          <span class="logo-text">PORTAIL DOCTORANT</span>
        </div>
      </div>
    </header>

    <main>
      <router-outlet></router-outlet>
    </main>

    <footer>
      <div class="container text-center">
        <p>&copy; 2026 ECOLE MAROCAINE DES SCIENCES DE L'INGENIEUR - Casablanca</p>
      </div>
    </footer>
  `,
  styles: [`
    .app-header {
      background: white;
      border-bottom: 4px solid var(--secondary-color);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      margin-bottom: 2rem;
    }
    .logo-icon {
      font-size: 2.5rem;
      margin-right: 1rem;
    }
    .logo-text {
      font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
      font-size: 2.2rem;
      font-weight: 800;
      letter-spacing: 2px;
      color: var(--primary-color);
      text-transform: uppercase;
      background: linear-gradient(135deg, var(--primary-color) 0%, #0056b3 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    footer {
      background: #1a1a1a;
      color: white;
      padding: 3rem 0;
      margin-top: 4rem;
      border-top: 5px solid var(--primary-color);
    }
  `]
})
export class PortalLayoutComponent { }
