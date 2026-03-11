import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../../../../../libs/auth-lib/src/lib/auth.service';
import { I18nService } from '../../../../services/i18n.service';

@Component({
    selector: 'app-hero',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './hero.html',
    styleUrl: './hero.css',
})
export class HeroComponent {
    i18n = inject(I18nService);
    auth = inject(AuthService);

    onShopNow(): void {
        if (!this.auth.isLoggedIn()) {
            this.auth.login(window.location.origin + '/products');
        }
    }
}
