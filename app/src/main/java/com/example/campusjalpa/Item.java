package com.example.campusjalpa;


public class Item {
    private final String title;         // Título del elemento
    private final String description;   // Descripción del elemento
    private final String imageUrl;      // URL de la imagen del elemento

    public Item(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;   // Devuelve el título del elemento
    }

    public String getDescription() {
        return description;   // Devuelve la descripción del elemento
    }

    public String getImageUrl() {
        return imageUrl;   // Devuelve la URL de la imagen del elemento
    }
}