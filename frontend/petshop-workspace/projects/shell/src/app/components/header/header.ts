import { isPlatformBrowser, NgClass, NgIf } from '@angular/common';
import {
    Component, HostListener,
    inject, OnInit, PLATFORM_ID,
    signal
} from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../../../libs/auth-lib/src/lib/auth.service';
import { I18nService } from '../../services/i18n.service';

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [NgClass, NgIf, RouterLink, RouterLinkActive],
    templateUrl: './header.html',
    styleUrl: './header.css',
})
export class HeaderComponent implements OnInit {
    auth = inject(AuthService);
    i18n = inject(I18nService);
    private platformId = inject(PLATFORM_ID);

    scrolled = signal(false);
    menuOpen = signal(false);
    langMenuOpen = signal(false);

    ngOnInit(): void {
        if (isPlatformBrowser(this.platformId)) {
            this.scrolled.set(window.scrollY > 20);
        }
    }

    @HostListener('window:scroll')
    onScroll(): void {
        this.scrolled.set(window.scrollY > 20);
    }

    toggleMenu(): void {
        this.menuOpen.update(v => !v);
    }

    closeMenu(): void {
        this.menuOpen.set(false);
    }

    toggleLangMenu(): void {
        this.langMenuOpen.update(v => !v);
    }

    setLang(lang: 'vi' | 'en'): void {
        this.i18n.setLanguage(lang);
        this.langMenuOpen.set(false);
    }

    login(): void {
        this.auth.login();
    }

    logout(): void {
        this.auth.logout();
    }
}
