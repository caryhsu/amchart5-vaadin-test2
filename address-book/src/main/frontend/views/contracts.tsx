
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';

import React, { useEffect } from 'react';
import { useSignal } from '@vaadin/hilla-react-signals';
import { Grid } from '@vaadin/react-components/Grid.js';
import { GridColumn } from '@vaadin/react-components/GridColumn.js';
import { ContractService } from 'Frontend/generated/endpoints';
import ContractDTO from 'Frontend/generated/com/example/application/ContractDTO';

export const config: ViewConfig = {
    menu: {
        order: 2,
        icon: 'line-awesome/svg/file.svg',
    },
    title: 'Contracts',
};

export default function ContractView() {
    const items = useSignal<ContractDTO[]>([]);

    useEffect(() => {
        ContractService.findAll().then((response) => {
            items.value = response;
        });
    }, []); // Ensure useEffect runs once when the component mounts

    return (
            <Grid items={items.value}>
                <GridColumn path="firstName" />
                <GridColumn path="lastName" />
                <GridColumn path="email" />
                <GridColumn path="phone" />
            </Grid>
    );
}
