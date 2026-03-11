import { computed, Injectable, signal } from '@angular/core';
import Keycloak from 'keycloak-js';

export interface UserProfile {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    fullName: string;
    roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private keycloak: Keycloak | null = null;

    private _profile = signal<UserProfile | null>(null);
    private _initialized = signal(false);

    readonly profile = this._profile.asReadonly();
    readonly initialized = this._initialized.asReadonly();
    readonly isLoggedIn = computed(() => this._profile() !== null);
    readonly isAdmin = computed(() =>
        this._profile()?.roles.includes('ROLE_ADMIN') ?? false
    );

    async init(keycloakInstance: Keycloak): Promise<void> {
        this.keycloak = keycloakInstance;
        if (keycloakInstance.authenticated) {
            await this.loadProfile();
        }
        this._initialized.set(true);
    }

    private async loadProfile(): Promise<void> {
        if (!this.keycloak) return;
        try {
            const kp = await this.keycloak.loadUserProfile();
            const tokenParsed = this.keycloak.tokenParsed as Record<string, unknown>;
            const realmRoles: string[] =
                (tokenParsed['realm_access'] as { roles?: string[] })?.roles ?? [];

            this._profile.set({
                id: kp.id ?? '',
                username: kp.username ?? '',
                email: kp.email ?? '',
                firstName: kp.firstName ?? '',
                lastName: kp.lastName ?? '',
                fullName: `${kp.firstName ?? ''} ${kp.lastName ?? ''}`.trim(),
                roles: realmRoles,
            });
        } catch {
            this._profile.set(null);
        }
    }

    login(redirectUri?: string): void {
        this.keycloak?.login({ redirectUri: redirectUri ?? window.location.href });
    }

    logout(): void {
        this.keycloak?.logout({ redirectUri: window.location.origin });
    }

    getToken(): string | undefined {
        return this.keycloak?.token;
    }
}
