package com.example.uasvolunteerhub;
import java.math.BigDecimal;
import java.sql.Date;
public class Activity {
    private int id;
    private String title;
    private Date date;
    private String benefits;
    private String location;
    private String contact;
    private Integer slot;
    private String description;
    private String typeOfVolunteer; // maps to "type_of_volunteer" in database
    private BigDecimal donationAmount;
    private String image;

    // Constructors
    public Activity() {}

    public Activity(int id, String title, Date date, String benefits, String location,
                    String contact, Integer slot, String description, String typeOfVolunteer,
                    BigDecimal donationAmount, String image) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.benefits = benefits;
        this.location = location;
        this.contact = contact;
        this.slot = slot;
        this.description = description;
        this.typeOfVolunteer = typeOfVolunteer;
        this.donationAmount = donationAmount;
        this.image = image;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeOfVolunteer() {
        return typeOfVolunteer;
    }

    public void setTypeOfVolunteer(String typeOfVolunteer) {
        this.typeOfVolunteer = typeOfVolunteer;
    }

    public BigDecimal getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(BigDecimal donationAmount) {
        this.donationAmount = donationAmount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", date=" + date +
                ", benefits='" + benefits + '\'' +
                ", location='" + location + '\'' +
                ", contact='" + contact + '\'' +
                ", slot=" + slot +
                ", description='" + description + '\'' +
                ", typeOfVolunteer='" + typeOfVolunteer + '\'' +
                ", donationAmount=" + donationAmount +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        return id == activity.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}