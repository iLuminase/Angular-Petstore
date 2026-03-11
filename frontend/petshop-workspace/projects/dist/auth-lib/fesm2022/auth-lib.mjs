import * as i0 from '@angular/core';
import { signal, computed, Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

class AuthService {
    keycloak = null;
    _profile = signal(null, ...(ngDevMode ? [{ debugName: "_profile" }] : []));
    _initialized = signal(false, ...(ngDevMode ? [{ debugName: "_initialized" }] : []));
    profile = this._profile.asReadonly();
    initialized = this._initialized.asReadonly();
    isLoggedIn = computed(() => this._profile() !== null, ...(ngDevMode ? [{ debugName: "isLoggedIn" }] : []));
    isAdmin = computed(() => this._profile()?.roles.includes('ROLE_ADMIN') ?? false, ...(ngDevMode ? [{ debugName: "isAdmin" }] : []));
    async init(keycloakInstance) {
        this.keycloak = keycloakInstance;
        if (keycloakInstance.authenticated) {
            await this.loadProfile();
        }
        this._initialized.set(true);
    }
    async loadProfile() {
        if (!this.keycloak)
            return;
        try {
            const kp = await this.keycloak.loadUserProfile();
            const tokenParsed = this.keycloak.tokenParsed;
            const realmRoles = tokenParsed['realm_access']?.roles ?? [];
            this._profile.set({
                id: kp.id ?? '',
                username: kp.username ?? '',
                email: kp.email ?? '',
                firstName: kp.firstName ?? '',
                lastName: kp.lastName ?? '',
                fullName: `${kp.firstName ?? ''} ${kp.lastName ?? ''}`.trim(),
                roles: realmRoles,
            });
        }
        catch {
            this._profile.set(null);
        }
    }
    login(redirectUri) {
        this.keycloak?.login({ redirectUri: redirectUri ?? window.location.href });
    }
    logout() {
        this.keycloak?.logout({ redirectUri: window.location.origin });
    }
    getToken() {
        return this.keycloak?.token;
    }
    static ɵfac = function AuthService_Factory(__ngFactoryType__) { return new (__ngFactoryType__ || AuthService)(); };
    static ɵprov = /*@__PURE__*/ i0.ɵɵdefineInjectable({ token: AuthService, factory: AuthService.ɵfac, providedIn: 'root' });
}
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassMetadata(AuthService, [{
        type: Injectable,
        args: [{ providedIn: 'root' }]
    }], null, null); })();

const authGuard = () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (auth.isLoggedIn())
        return true;
    auth.login(window.location.href);
    return router.createUrlTree(['/']);
};

/*
 * Public API Surface of auth-lib
 */

/**
 * Generated bundle index. Do not edit.
 */

export { AuthService, authGuard };
//# sourceMappingURL=auth-lib.mjs.map
