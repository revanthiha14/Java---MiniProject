package com.cineflow.model.personnel;

import com.cineflow.model.Identifiable;
import com.cineflow.model.Reportable;
import java.util.Objects;

/**
 * Abstract base class representing any human resource in the movie production.
 * Demonstrates:
 *  - Abstract classes
 *  - Inheritance base
 *  - Constructor overloading & constructor chaining (this() / super())
 *  - Encapsulation with getters/setters
 *  - Common contract implementation
 */
public abstract class Person implements Identifiable<String>, Reportable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String email;
    protected String phone;
    protected double dailyRate;
    protected int daysWorked;

    /**
     * Default constructor providing safe defaults.
     * Demonstrates constructor chaining with this().
     */
    public Person() {
        this("PER-UNKNOWN", "Anonymous", "unassigned@cineflow.studio", "000-000-0000", 0.0);
    }

    /**
     * Minimal constructor chaining to the fully parameterized constructor.
     */
    public Person(String id, String name, double dailyRate) {
        this(id, name, name.toLowerCase().replace(" ", ".") + "@cineflow.studio", "555-0100", dailyRate);
    }

    /**
     * Full parameterized constructor initializing all core person attributes.
     */
    public Person(String id, String name, String email, String phone, double dailyRate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dailyRate = dailyRate;
        this.daysWorked = 0;
    }

    // Abstract methods to be implemented polymorphically by subclasses
    /**
     * Calculates the total remuneration/payroll for this person based on days worked and contract terms.
     * @param days Total days engaged on set
     * @return Total payment amount
     */
    public abstract double calculateRemuneration(int days);

    /**
     * Summary role description of the individual.
     * @return Role title
     */
    public abstract String getRoleTitle();

    // Concrete shared methods
    public void recordDaysWorked(int days) {
        if (days > 0) {
            this.daysWorked += days;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - Daily Rate: $%.2f", id, name, getRoleTitle(), dailyRate);
    }
}
