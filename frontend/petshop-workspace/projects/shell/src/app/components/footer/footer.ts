import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { I18nService } from '../../services/i18n.service';

@Component({
    selector: 'app-footer',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './footer.html',
    styleUrl: './footer.css'
})
export class FooterComponent {
    protected i18n = inject(I18nService);
    protected year = new Date().getFullYear();
}
