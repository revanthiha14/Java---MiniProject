package com.cineflow.model.personnel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents an on-screen talent or cast member.
 * Demonstrates:
 *  - Inheritance (extends Person)
 *  - Constructor chaining with super() and this()
 *  - Collections Framework: Set (TreeSet for sorted skills, HashSet for scenes)
 *  - Method Overloading (assignRole variants)
 *  - Method Overriding (calculateRemuneration, generateDetailedReport)
 */
public class Actor extends Person {
    private static final long serialVersionUID = 1L;

    private String characterName;
    private int billingOrder; // 1 = Lead, 2 = Co-Lead, 3+ = Supporting
    private String agency;
    private boolean stuntQualified;
    private Set<String> specialSkills; // Demonstrates TreeSet for sorted unique elements
    private Set<String> assignedSceneIds; // Demonstrates HashSet for fast containment checks

    public Actor() {
        this("ACT-000", "Unnamed Actor", 1500.0, "Background Extra", 99);
    }

    public Actor(String id, String name, double dailyRate, String characterName, int billingOrder) {
        this(id, name, name.toLowerCase().replace(" ", ".") + "@talent.com", "555-CAST", dailyRate, characterName, billingOrder, "Independent", false);
    }

    public Actor(String id, String name, String email, String phone, double dailyRate,
                 String characterName, int billingOrder, String agency, boolean stuntQualified) {
        super(id, name, email, phone, dailyRate);
        this.characterName = characterName;
        this.billingOrder = billingOrder;
        this.agency = agency;
        this.stuntQualified = stuntQualified;
        this.specialSkills = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.assignedSceneIds = new HashSet<>();
    }

    // Method Overloading Demonstration
    public void assignRole(String characterName) {
        assignRole(characterName, 10);
    }

    public void assignRole(String characterName, int billingOrder) {
        assignRole(characterName, billingOrder, this.agency != null ? this.agency : "Independent");
    }

    public void assignRole(String characterName, int billingOrder, String agency) {
        this.characterName = characterName;
        this.billingOrder = billingOrder;
        this.agency = agency;
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.specialSkills.add(skill.trim());
        }
    }

    public void assignScene(String sceneId) {
        this.assignedSceneIds.add(sceneId);
    }

    public void removeScene(String sceneId) {
        this.assignedSceneIds.remove(sceneId);
    }

    @Override
    public double calculateRemuneration(int days) {
        double base = dailyRate * days;
        // Stunt hazard bonus of 20% if stunt-qualified
        double stuntBonus = stuntQualified ? base * 0.20 : 0.0;
        // Agency representation commission overhead (10%)
        double agencyFee = base * 0.10;
        return base + stuntBonus + agencyFee;
    }

    @Override
    public String getRoleTitle() {
        return billingOrder == 1 ? "Lead Actor (" + characterName + ")"
                : "Supporting Actor (" + characterName + ")";
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== TALENT PROFILE: %s (ID: %s) ===\n", name, id));
        sb.append(String.format("Character Role : %s [Billing Order #%d]\n", characterName, billingOrder));
        sb.append(String.format("Representation : %s\n", agency));
        sb.append(String.format("Daily Rate     : $%.2f | Stunt Qualified: %s\n", dailyRate, stuntQualified ? "YES" : "NO"));
        sb.append(String.format("Days Contracted: %d | Total Remuneration: $%.2f\n", daysWorked, calculateRemuneration(daysWorked)));
        sb.append("Special Skills : ").append(specialSkills.isEmpty() ? "None listed" : String.join(", ", specialSkills)).append("\n");
        sb.append("Assigned Scenes: ").append(assignedSceneIds.isEmpty() ? "None" : String.join(", ", assignedSceneIds)).append("\n");
        return sb.toString();
    }

    // Getters and Setters
    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public int getBillingOrder() {
        return billingOrder;
    }

    public void setBillingOrder(int billingOrder) {
        this.billingOrder = billingOrder;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public boolean isStuntQualified() {
        return stuntQualified;
    }

    public void setStuntQualified(boolean stuntQualified) {
        this.stuntQualified = stuntQualified;
    }

    public Set<String> getSpecialSkills() {
        return Collections.unmodifiableSet(specialSkills);
    }

    public Set<String> getAssignedSceneIds() {
        return Collections.unmodifiableSet(assignedSceneIds);
    }
}
