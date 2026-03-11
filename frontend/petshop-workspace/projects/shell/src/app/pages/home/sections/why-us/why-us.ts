import { Component, inject } from '@angular/core';
import { I18nService } from '../../../../services/i18n.service';

interface Feature { icon: string; key: string; }

@Component({
    selector: 'app-why-us',
    standalone: true,
    templateUrl: './why-us.html',
    styleUrl: './why-us.css',
})
export class WhyUsComponent {
    i18n = inject(I18nService);

    features: Feature[] = [
        { icon: '🏆', key: 'quality' },
        { icon: '🚀', key: 'delivery' },
        { icon: '💬', key: 'support' },
        { icon: '🩺', key: 'vet' },
    ];
}
