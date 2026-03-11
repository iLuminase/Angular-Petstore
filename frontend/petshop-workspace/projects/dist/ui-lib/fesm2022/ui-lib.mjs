import * as i0 from '@angular/core';
import { Component } from '@angular/core';

class UiLib {
    static ɵfac = function UiLib_Factory(__ngFactoryType__) { return new (__ngFactoryType__ || UiLib)(); };
    static ɵcmp = /*@__PURE__*/ i0.ɵɵdefineComponent({ type: UiLib, selectors: [["lib-ui-lib"]], decls: 2, vars: 0, template: function UiLib_Template(rf, ctx) { if (rf & 1) {
            i0.ɵɵdomElementStart(0, "p");
            i0.ɵɵtext(1, "ui-lib works!");
            i0.ɵɵdomElementEnd();
        } }, encapsulation: 2 });
}
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassMetadata(UiLib, [{
        type: Component,
        args: [{ selector: 'lib-ui-lib', imports: [], template: ` <p>ui-lib works!</p> ` }]
    }], null, null); })();
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassDebugInfo(UiLib, { className: "UiLib", filePath: "lib/ui-lib.ts", lineNumber: 9 }); })();

/*
 * Public API Surface of ui-lib
 */

/**
 * Generated bundle index. Do not edit.
 */

export { UiLib };
//# sourceMappingURL=ui-lib.mjs.map
