import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { I18nService } from '../../../../services/i18n.service';

export interface Product {
    id: number;
    name: string;
    nameEn: string;
    price: number;
    originalPrice?: number;
    image: string;
    category: 'dogs' | 'cats' | 'fish';
    rating: number;
    reviews: number;
    tag?: 'new' | 'hot' | 'sale';
}

@Component({
    selector: 'app-featured-products',
    standalone: true,
    imports: [RouterLink],
    templateUrl: './featured-products.html',
    styleUrl: './featured-products.css',
})
export class FeaturedProductsComponent {
    protected Math = Math;
    i18n = inject(I18nService);

    products: Product[] = [
        { id: 1, name: 'Thức ăn Royal Canin cho Chó', nameEn: 'Royal Canin Dog Food', price: 285000, originalPrice: 320000, image: '🐕', category: 'dogs', rating: 5, reviews: 128, tag: 'hot' },
        { id: 2, name: 'Vòng cổ GPS theo dõi thú cưng', nameEn: 'GPS Pet Tracking Collar', price: 650000, image: '🦮', category: 'dogs', rating: 4, reviews: 64, tag: 'new' },
        { id: 3, name: 'Cát vệ sinh cho Mèo Nhật Bản', nameEn: 'Japanese Cat Litter', price: 185000, originalPrice: 220000, image: '🐈', category: 'cats', rating: 5, reviews: 203, tag: 'sale' },
        { id: 4, name: 'Đồ chơi cần câu mèo có đèn', nameEn: 'LED Cat Fishing Toy', price: 95000, image: '🎣', category: 'cats', rating: 4, reviews: 87 },
        { id: 5, name: 'Bể cá cảnh Mini AIO 30cm', nameEn: 'Mini AIO Fish Tank 30cm', price: 890000, image: '🐠', category: 'fish', rating: 5, reviews: 45, tag: 'new' },
        { id: 6, name: 'Thức ăn cá Koi viên cao cấp', nameEn: 'Premium Koi Fish Pellets', price: 125000, image: '🐟', category: 'fish', rating: 4, reviews: 92 },
        { id: 7, name: 'Nhà chuồng chó vải Oxford', nameEn: 'Oxford Dog House', price: 420000, originalPrice: 480000, image: '🏠', category: 'dogs', rating: 4, reviews: 56, tag: 'sale' },
        { id: 8, name: 'Máy bơm lọc bể cá 600L/h', nameEn: 'Aquarium Filter Pump 600L/h', price: 350000, image: '💧', category: 'fish', rating: 5, reviews: 38, tag: 'hot' },
    ];

    formatPrice(price: number): string {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
    }

    getStars(rating: number): string {
        return '★'.repeat(rating) + '☆'.repeat(5 - rating);
    }

    getTagLabel(tag: Product['tag']): string {
        if (!tag) return '';
        return this.i18n.t(`featured.${tag}`);
    }
}
