import { html, LitElement, customElement, state } from 'lit';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import Person from 'Frontend/generated/com/example/application/model/Person';
import { getPersons } from 'Frontend/generated/PersonService';

@customElement('person-grid-view')
export class PersonGridView extends LitElement {
  @state()
  private persons: Person[] = [];

  connectedCallback() {
    super.connectedCallback();
    this.fetchPersons();
  }

  async fetchPersons() {
    this.persons = await getPersons();
  }

  render() {
    return html`
      <vaadin-grid .items="${this.persons}">
        <vaadin-grid-column path="firstName" header="First Name"></vaadin-grid-column>
        <vaadin-grid-column path="lastName" header="Last Name"></vaadin-grid-column>
        <vaadin-grid-column path="age" header="Age"></vaadin-grid-column>
      </vaadin-grid>
    `;
  }
}
