import { CanActivateFn } from '@angular/router';
import * as _angular_core from '@angular/core';
import Keycloak from 'keycloak-js';

declare const authGuard: CanActivateFn;

interface UserProfile {
    id: string;
    username: string;
    email: string;
    firstName: string;
    lastName: string;
    fullName: string;
    roles: string[];
}
declare class AuthService {
    private keycloak;
    private _profile;
    private _initialized;
    readonly profile: _angular_core.Signal<UserProfile | null>;
    readonly initialized: _angular_core.Signal<boolean>;
    readonly isLoggedIn: _angular_core.Signal<boolean>;
    readonly isAdmin: _angular_core.Signal<boolean>;
    init(keycloakInstance: Keycloak): Promise<void>;
    private loadProfile;
    login(redirectUri?: string): void;
    logout(): void;
    getToken(): string | undefined;
    static ɵfac: _angular_core.ɵɵFactoryDeclaration<AuthService, never>;
    static ɵprov: _angular_core.ɵɵInjectableDeclaration<AuthService>;
}

export { AuthService, authGuard };
export type { UserProfile };
//# sourceMappingURL=auth-lib.d.ts.map
