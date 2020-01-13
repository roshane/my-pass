package com.example.mypass.model;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;

public class Password {

    private int id;
    private String title;
    private String notes;
    private String username;
    private String password;
    private String website;
    private LocalDateTime createAt = LocalDateTime.now();
    private boolean isActive = true;

    public Password(String title, String username, String website, String notes, String password) {
        this.title = title.trim();
        this.password = password.trim();
        this.notes = notes.trim();
        this.username = username.trim();
        this.website = website.trim();
    }

    public Password(int id, String title, String username, String password,
                    String notes, String website, LocalDateTime createAt, boolean isActive) {
        this.title = title.trim();
        this.notes = notes.trim();
        this.website = website;
        this.createAt = createAt;
        this.isActive = isActive;
        this.id = id;
        this.password = password;
        this.username = username;
    }

    public Password(String title, String username, String password, String website, String notes, LocalDateTime createdAt, boolean isActive) {
        this.title = title;
        this.password = password;
        this.notes = notes;
        this.createAt = createdAt;
        this.username = username;
        this.isActive = isActive;
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        } else if (obj.getClass().isAssignableFrom(Password.class)) {
            Password that = (Password) obj;
            return this.getTitle().equals(that.getTitle())
                    && this.getNotes().equals(that.getNotes())
                    && this.isActive() == that.isActive();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getTitle().hashCode()
                + this.getNotes().hashCode()
                + (this.isActive() ? 1 : 0);
    }

    @Override
    public String toString() {
        return "Password{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", notes='" + notes + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", website='" + website + '\'' +
                ", createAt=" + createAt +
                ", isActive=" + isActive +
                '}';
    }
}
