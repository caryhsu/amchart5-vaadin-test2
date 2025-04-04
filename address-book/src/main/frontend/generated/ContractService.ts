import { EndpointRequestInit as EndpointRequestInit_1 } from "@vaadin/hilla-frontend";
import type ContractDTO_1 from "./com/example/application/ContractDTO.js";
import client_1 from "./connect-client.default.js";
async function findAll_1(init?: EndpointRequestInit_1): Promise<Array<ContractDTO_1>> { return client_1.call("ContractService", "findAll", {}, init); }
export { findAll_1 as findAll };
