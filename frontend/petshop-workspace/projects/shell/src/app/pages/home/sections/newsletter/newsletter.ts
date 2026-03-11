import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { I18nService } from '../../../../services/i18n.service';

@Component({
    selector: 'app-newsletter',
    standalone: true,
    imports: [FormsModule],
    templateUrl: './newsletter.html',
    styleUrl: './newsletter.css',
})
export class NewsletterComponent {
    i18n = inject(I18nService);
    email = signal('');
    submitted = signal(false);

    submit(): void {
        if (this.email().includes('@')) {
            this.submitted.set(true);
        }
    }
}
