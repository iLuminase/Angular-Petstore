import * as i0 from '@angular/core';
import { Component } from '@angular/core';

class SharedLib {
    static ɵfac = function SharedLib_Factory(__ngFactoryType__) { return new (__ngFactoryType__ || SharedLib)(); };
    static ɵcmp = /*@__PURE__*/ i0.ɵɵdefineComponent({ type: SharedLib, selectors: [["lib-shared-lib"]], decls: 2, vars: 0, template: function SharedLib_Template(rf, ctx) { if (rf & 1) {
            i0.ɵɵdomElementStart(0, "p");
            i0.ɵɵtext(1, "shared-lib works!");
            i0.ɵɵdomElementEnd();
        } }, encapsulation: 2 });
}
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassMetadata(SharedLib, [{
        type: Component,
        args: [{ selector: 'lib-shared-lib', imports: [], template: ` <p>shared-lib works!</p> ` }]
    }], null, null); })();
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassDebugInfo(SharedLib, { className: "SharedLib", filePath: "lib/shared-lib.ts", lineNumber: 9 }); })();

/*
 * Public API Surface of shared-lib
 */

/**
 * Generated bundle index. Do not edit.
 */

export { SharedLib };
//# sourceMappingURL=shared-lib.mjs.map
