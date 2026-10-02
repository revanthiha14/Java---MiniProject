package com.cineflow.model.personnel;

import com.cineflow.model.Department;

/**
 * Represents the film Director, heading the creative vision.
 * Demonstrates:
 *  - Multilevel inheritance (Person -> CrewMember -> Director)
 *  - Dynamic method dispatch and method overriding
 *  - Constructor chaining with super()
 */
public class Director extends CrewMember {
    private static final long serialVersionUID = 1L;

    private String visionStatement;
    private double royaltyPercentage;
    private double directionalFeeBonus;

    public Director() {
        this("DIR-001", "Auteur Director", 3500.0, "Cinematic Visual Realism", 2.5, 50000.0);
    }

    public Director(String id, String name, double dailyRate, String visionStatement,
                    double royaltyPercentage, double directionalFeeBonus) {
        super(id, name, name.toLowerCase().replace(" ", ".") + "@director.cineflow.studio",
                "555-FILM", dailyRate, Department.DIRECTING, "Principal Director", true,
                new String[]{"Directors Guild of America (DGA)", "Advanced Virtual Production"});
        this.visionStatement = visionStatement;
        this.royaltyPercentage = royaltyPercentage;
        this.directionalFeeBonus = directionalFeeBonus;
    }

    @Override
    public double calculateRemuneration(int days) {
        // Base remuneration from CrewMember calculation plus fixed directorial bonus
        double base = super.calculateRemuneration(days);
        return base + directionalFeeBonus;
    }

    @Override
    public String getRoleTitle() {
        return "Director & Creative Visionary";
    }

    @Override
    public String generateDetailedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.generateDetailedReport());
        sb.append(String.format("Vision         : \"%s\"\n", visionStatement));
        sb.append(String.format("Backend Royalty: %.2f%% of Box Office Net\n", royaltyPercentage));
        sb.append(String.format("Director Bonus : $%.2f (guaranteed upon project completion)\n", directionalFeeBonus));
        return sb.toString();
    }

    // Getters and Setters
    public String getVisionStatement() {
        return visionStatement;
    }

    public void setVisionStatement(String visionStatement) {
        this.visionStatement = visionStatement;
    }

    public double getRoyaltyPercentage() {
        return royaltyPercentage;
    }

    public void setRoyaltyPercentage(double royaltyPercentage) {
        this.royaltyPercentage = royaltyPercentage;
    }

    public double getDirectionalFeeBonus() {
        return directionalFeeBonus;
    }

    public void setDirectionalFeeBonus(double directionalFeeBonus) {
        this.directionalFeeBonus = directionalFeeBonus;
    }
}
