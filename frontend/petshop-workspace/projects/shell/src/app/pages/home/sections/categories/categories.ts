import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { I18nService } from '../../../../services/i18n.service';

interface Category {
    key: string;
    emoji: string;
    color: string;
    bg: string;
    route: string;
}

@Component({
    selector: 'app-categories',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './categories.html',
    styleUrl: './categories.css',
})
export class CategoriesComponent {
    i18n = inject(I18nService);

    categories: Category[] = [
        { key: 'dogs', emoji: '🐕', color: '#d4621a', bg: 'linear-gradient(135deg,#fff3e0,#ffe0b2)', route: '/products/dogs' },
        { key: 'cats', emoji: '🐈', color: '#7b1fa2', bg: 'linear-gradient(135deg,#f3e5f5,#e1bee7)', route: '/products/cats' },
        { key: 'fish', emoji: '🐠', color: '#0277bd', bg: 'linear-gradient(135deg,#e1f5fe,#b3e5fc)', route: '/products/fish' },
    ];
}
