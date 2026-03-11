import { Component, inject } from '@angular/core';
import { AuthService } from '../../../../../libs/auth-lib/src/lib/auth.service';
import { FooterComponent } from '../../components/footer/footer';
import { I18nService } from '../../services/i18n.service';
import { CategoriesComponent } from './sections/categories/categories';
import { FeaturedProductsComponent } from './sections/featured-products/featured-products';
import { HeroComponent } from './sections/hero/hero';
import { NewsletterComponent } from './sections/newsletter/newsletter';
import { WhyUsComponent } from './sections/why-us/why-us';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        HeroComponent,
        CategoriesComponent,
        FeaturedProductsComponent,
        WhyUsComponent,
        NewsletterComponent,
        FooterComponent,
    ],
    template: `
    <app-hero />
    <app-categories />
    <app-featured-products />
    <app-why-us />
    <app-newsletter />
    <app-footer />
  `,
})
export class HomeComponent {
    i18n = inject(I18nService);
    auth = inject(AuthService);
}
