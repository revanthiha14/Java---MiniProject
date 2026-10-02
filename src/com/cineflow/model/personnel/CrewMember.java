package com.cineflow.model.personnel;

import com.cineflow.model.Department;
import java.util.Arrays;

/**
 * Represents a crew member belonging to a specific production department.
 * Demonstrates:
 *  - Inheritance (extends Person)
 *  - Primitive & Object Arrays (String[] certifications)
 *  - Constructor chaining with super() and this()
 *  - Method overriding (calculateRemuneration, generateDetailedReport)
 */
public class CrewMember extends Person {
    private static final long serialVersionUID = 1L;

    protected Department department;
    protected String designation;
    protected boolean unionMember;
    protected String[] certifications; // Explicit demonstration of arrays

    public CrewMember() {
        this("CRW-000", "Unnamed Crew", 800.0, Department.CAMERA, "Production Assistant", false, new String[0]);
    }

    public CrewMember(String id, String name, double dailyRate, Department department, String designation) {
        this(id, name, name.toLowerCase().replace(" ", ".") + "@crew.cineflow.studio", "555-CREW", dailyRate, department, designation, true, new String[]{"Basic Set Safety"});
    }

    public CrewMember(String id, String name, double dailyRate, Department department,
                      String designation, boolean unionMember) {
        this(id, name, name.toLowerCase().replace(" ", ".") + "@crew.cineflow.studio", "555-CREW",
                dailyRate, department, designation, unionMember, new String[]{"Basic Set Safety"});
    }

    public CrewMember(String id, String name, double dailyRate, Department department,
                      String designation, boolean unionMember, String[] certifications) {
        this(id, name, name.toLowerCase().replace(" ", ".") + "@crew.cineflow.studio", "555-CREW",
                dailyRate, department, designation, unionMember, certifications);
    }

    public CrewMember(String id, String name, String email, String phone, double dailyRate,
                      Department department, String designation, boolean unionMember, String[] certifications) {
        super(id, name, email, phone, dailyRate);
        this.department = department;
        this.designation = designation;
        this.unionMember = unionMember;
        this.certifications = certifications != null ? Arrays.copyOf(certifications, certifications.length) : new String[0];
    }

    /**
     * Checks if this crew member holds a specific industry certification.
     * Demonstrates array traversal.
     */
    public boolean hasCertification(String cert) {
        if (cert == null || certifications == null) return false;
        for (String c : certifications) {
            if (c.equalsIgnoreCase(cert.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Appends a new certification to the fixed-size array by resizing.
     * Demonstrates manual array expansion and copying.
     */
    public void addCertification(String newCert) {
        if (newCert == null || newCert.trim().isEmpty()) return;
        String[] expanded = Arrays.copyOf(this.certifications, this.certifications.length + 1);
        expanded[this.certifications.length] = newCert.trim();
        this.certifications = expanded;
    }

    @Override
    public double calculateRemuneration(int days) {
        double base = dailyRate * days;
        // Union members get 15% guaranteed pension & health contribution
        double unionBenefits = unionMember ? base * 0.15 : 0.0;
        return base + unionBenefits;
    }

    @Override
    public String getRoleTitle() {
        return designation + " (" + department.getDisplayName() + ")";
    }

    public String getJobTitle() {
        return designation;
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== CREW PROFILE: %s (ID: %s) ===\n", name, id));
        sb.append(String.format("Department     : %s\n", department.getDisplayName()));
        sb.append(String.format("Designation    : %s\n", designation));
        sb.append(String.format("Union Member   : %s\n", unionMember ? "YES (IATSE/DGA)" : "NO"));
        sb.append(String.format("Daily Rate     : $%.2f | Days Worked: %d\n", dailyRate, daysWorked));
        sb.append(String.format("Total Remuneration: $%.2f\n", calculateRemuneration(daysWorked)));
        sb.append("Certifications : ").append(certifications.length == 0 ? "None" : String.join(", ", certifications)).append("\n");
        return sb.toString();
    }

    // Getters and Setters
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isUnionMember() {
        return unionMember;
    }

    public void setUnionMember(boolean unionMember) {
        this.unionMember = unionMember;
    }

    public String[] getCertifications() {
        return Arrays.copyOf(certifications, certifications.length);
    }
}
