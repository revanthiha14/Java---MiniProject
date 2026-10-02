package com.cineflow.service;

import com.cineflow.exception.ResourceNotFoundException;
import com.cineflow.exception.ValidationException;
import com.cineflow.model.Department;
import com.cineflow.model.personnel.Actor;
import com.cineflow.model.personnel.CrewMember;
import com.cineflow.model.personnel.Director;
import com.cineflow.model.personnel.Person;
import com.cineflow.repository.Repository;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service managing cast and crew members, contracts, and payroll calculations.
 * Demonstrates:
 *  - Generic Repository integration
 *  - Polymorphic method invocation on Person hierarchy
 *  - Method Overloading
 *  - Functional programming: Predicate<Person>, Streams, Lambdas
 *  - Custom Exceptions
 */
public class PersonnelService implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Repository<Person, String> personRepository;

    public PersonnelService(Repository<Person, String> personRepository) {
        this.personRepository = personRepository;
    }

    public void registerPerson(Person person) throws ValidationException {
        if (person == null) {
            throw new ValidationException("person", "Personnel object cannot be null");
        }
        if (person.getId() == null || person.getId().trim().isEmpty()) {
            throw new ValidationException("id", "Person ID cannot be blank");
        }
        if (person.getName() == null || person.getName().trim().isEmpty()) {
            throw new ValidationException("name", "Person name cannot be blank");
        }
        if (person.getDailyRate() < 0) {
            throw new ValidationException("dailyRate", "Daily rate cannot be negative");
        }
        personRepository.save(person);
    }

    // Method Overloading Demonstration: registerActor variants
    public Actor registerActor(String id, String name, double dailyRate, String characterName, int billingOrder)
            throws ValidationException {
        Actor actor = new Actor(id, name, dailyRate, characterName, billingOrder);
        registerPerson(actor);
        return actor;
    }

    public Actor registerActor(String id, String name, String email, String phone, double dailyRate,
                              String characterName, int billingOrder, String agency, boolean stuntQualified)
            throws ValidationException {
        Actor actor = new Actor(id, name, email, phone, dailyRate, characterName, billingOrder, agency, stuntQualified);
        registerPerson(actor);
        return actor;
    }

    // Method Overloading Demonstration: registerCrew variants
    public CrewMember registerCrew(String id, String name, double dailyRate, Department department, String designation)
            throws ValidationException {
        CrewMember crew = new CrewMember(id, name, dailyRate, department, designation);
        registerPerson(crew);
        return crew;
    }

    public CrewMember registerCrew(String id, String name, String email, String phone, double dailyRate,
                                  Department department, String designation, boolean unionMember, String[] certs)
            throws ValidationException {
        CrewMember crew = new CrewMember(id, name, email, phone, dailyRate, department, designation, unionMember, certs);
        registerPerson(crew);
        return crew;
    }

    public Person getPersonById(String id) throws ResourceNotFoundException {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personnel", id));
    }

    public List<Person> getAllPersonnel() {
        return personRepository.findAll();
    }

    /**
     * Filters personnel using a functional Predicate.
     * Demonstrates functional interface and lambda expressions.
     */
    public List<Person> findPersonnel(Predicate<Person> filter) {
        return personRepository.findAll().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Person::getName))
                .collect(Collectors.toList());
    }

    public List<Actor> getAllActors() {
        return personRepository.findAll().stream()
                .filter(p -> p instanceof Actor)
                .map(p -> (Actor) p)
                .sorted(Comparator.comparingInt(Actor::getBillingOrder))
                .collect(Collectors.toList());
    }

    public List<CrewMember> getAllCrew() {
        return personRepository.findAll().stream()
                .filter(p -> p instanceof CrewMember)
                .map(p -> (CrewMember) p)
                .sorted(Comparator.comparing(p -> p.getDepartment().name()))
                .collect(Collectors.toList());
    }

    public List<CrewMember> getCrewByDepartment(Department dept) {
        return personRepository.findAll().stream()
                .filter(p -> p instanceof CrewMember)
                .map(p -> (CrewMember) p)
                .filter(c -> c.getDepartment() == dept)
                .collect(Collectors.toList());
    }

    /**
     * Calculates total payroll across all personnel polymorphically.
     * Demonstrates dynamic method dispatch: Person.calculateRemuneration()
     * executes differently whether the underlying instance is Actor, CrewMember, or Director.
     */
    public double computeTotalPayroll(int standardProductionDays) {
        return personRepository.findAll().stream()
                .mapToDouble(person -> {
                    int days = person.getDaysWorked() > 0 ? person.getDaysWorked() : standardProductionDays;
                    // Dynamic Polymorphism
                    return person.calculateRemuneration(days);
                })
                .sum();
    }

    public boolean removePerson(String id) {
        return personRepository.deleteById(id);
    }
}
