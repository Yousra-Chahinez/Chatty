package com.example.azzem.chatty.Model;

import com.google.firebase.firestore.Exclude;

public class User {
    private String id;
    private String username;
    private String imageURL;
    private String status;
    private boolean selected = false;
    private String search;
    private String about;
    /*Cette méthode est utilisée pour rassembler les données contenues
    dans cet instantané dans une classe de votre choix. La classe doit répondre à 2 contraintes simples:
    La classe doit avoir un constructeur par défaut qui ne prend aucun argument
    La classe doit définir des getters publics pour les propriétés à affecter.
    Les propriétés sans getter public auront la valeur par défaut lorsqu'une instance est désérialisée*/
    public User(String id, String username, String imageURL, String status, Boolean selected, String search, String about)
    {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.selected = selected;
        this.search = search;
        this.about = about;
    }

    public User()
    {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
