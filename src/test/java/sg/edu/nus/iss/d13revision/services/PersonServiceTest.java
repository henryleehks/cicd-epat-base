package sg.edu.nus.iss.d13revision.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sg.edu.nus.iss.d13revision.models.Person;

public class PersonServiceTest {

    private PersonService personService;

    @BeforeEach
    public void setUp() {
        personService = new PersonService();
    }

    private Person findById(String id) {
        return personService.getPersons().stream()
                .filter(p -> p.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Test
    public void constructor_seedsTwoDefaultPersons() {
        List<Person> persons = personService.getPersons();

        assertEquals(2, persons.size());
        assertEquals("Mark", persons.get(0).getFirstName());
        assertEquals("Zuckerberg", persons.get(0).getLastName());
        assertEquals("Elon", persons.get(1).getFirstName());
        assertEquals("Musk", persons.get(1).getLastName());
    }

    @Test
    public void constructor_assignsUniqueIdsToSeededPersons() {
        List<Person> persons = personService.getPersons();

        assertNotNull(persons.get(0).getId());
        assertNotNull(persons.get(1).getId());
        assertNotEquals(persons.get(0).getId(), persons.get(1).getId());
    }

    @Test
    public void addPerson_appendsPersonWithSameNames() {
        personService.addPerson(new Person("Bill", "Gates"));

        List<Person> persons = personService.getPersons();
        assertEquals(3, persons.size());
        Person added = persons.get(2);
        assertEquals("Bill", added.getFirstName());
        assertEquals("Gates", added.getLastName());
    }

    @Test
    public void addPerson_storesCopyWithNewlyGeneratedId() {
        Person input = new Person("fixed-id", "Bill", "Gates");

        personService.addPerson(input);

        Person added = personService.getPersons().get(2);
        assertNotSame(input, added);
        assertNotNull(added.getId());
        assertNotEquals("fixed-id", added.getId());
    }

    @Test
    public void updatePerson_replacesNamesOfExistingPerson() {
        String id = personService.getPersons().get(0).getId();

        personService.updatePerson(new Person(id, "Mark", "Updated"));

        assertEquals(2, personService.getPersons().size());
        Person updated = findById(id);
        assertNotNull(updated);
        assertEquals("Mark", updated.getFirstName());
        assertEquals("Updated", updated.getLastName());
    }

    @Test
    public void updatePerson_leavesOtherPersonsUnchanged() {
        Person other = personService.getPersons().get(1);
        String id = personService.getPersons().get(0).getId();

        personService.updatePerson(new Person(id, "New", "Name"));

        Person stillThere = findById(other.getId());
        assertNotNull(stillThere);
        assertEquals("Elon", stillThere.getFirstName());
        assertEquals("Musk", stillThere.getLastName());
    }

    @Test
    public void updatePerson_withUnknownId_addsPerson() {
        // Current behaviour: no match means nothing is removed, so the person is appended
        personService.updatePerson(new Person("unknown", "Jeff", "Bezos"));

        assertEquals(3, personService.getPersons().size());
        Person added = findById("unknown");
        assertNotNull(added);
        assertEquals("Jeff", added.getFirstName());
    }

    @Test
    public void removePerson_removesMatchingPerson() {
        String id = personService.getPersons().get(0).getId();

        personService.removePerson(new Person(id, null, null));

        List<Person> persons = personService.getPersons();
        assertEquals(1, persons.size());
        assertFalse(persons.stream().anyMatch(p -> p.getId().equals(id)));
        assertEquals("Elon", persons.get(0).getFirstName());
    }

    @Test
    public void removePerson_withUnknownId_doesNothing() {
        personService.removePerson(new Person("unknown", "Jeff", "Bezos"));

        assertEquals(2, personService.getPersons().size());
    }

    @Test
    public void removePerson_allPersons_leavesEmptyList() {
        for (Person p : List.copyOf(personService.getPersons())) {
            personService.removePerson(p);
        }

        assertTrue(personService.getPersons().isEmpty());
    }
}
