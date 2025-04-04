import { createRoute as createRoute_1 } from "@vaadin/hilla-file-router/runtime.js";
import type { AgnosticRoute as AgnosticRoute_1 } from "@vaadin/hilla-file-router/types.js";
import * as Layout_1 from "../views/@layout.js";
import * as Page_1 from "../views/about-hilla.js";
import * as Page_2 from "../views/hello-hilla.js";
import * as Page_3 from "../views/telemetry-view.js";
const routes: readonly AgnosticRoute_1[] = [
    createRoute_1("", Layout_1, [
        createRoute_1("about-hilla", Page_1),
        createRoute_1("hello-hilla", Page_2),
        createRoute_1("telemetry-view", Page_3)
    ])
];
export default routes;
