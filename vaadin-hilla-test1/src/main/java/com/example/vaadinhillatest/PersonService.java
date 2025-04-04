package com.example.vaadinhillatest;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.hilla.Endpoint;
import com.vaadin.hilla.Nonnull;

@Endpoint
public class PersonService {

	public @Nonnull List<@Nonnull Person> getPersons() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("John", "Doe", 28));
		persons.add(new Person("Jane", "Doe", 32));
		persons.add(new Person("Alice", "Johnson", 24));
		persons.add(new Person("Bob", "Smith", 45));
		return persons;
	}
}