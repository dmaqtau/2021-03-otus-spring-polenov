package ru.otus.spring.domain;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "books")
@NamedEntityGraph(name = "bookWithAuthorAndGenre",
        attributeNodes = {@NamedAttributeNode("author"), @NamedAttributeNode("genre")}
)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "book_name", nullable = false)
    private String name;

    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Author author;

    @JoinColumn(name = "genre_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    @ManyToOne(optional = false)
    private Genre genre;

    @Column(name = "description")
    private String description;

    public Book(long id){
        this.id = id;
    }

    public Book(String name, Author author, Genre genre, String description){
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.description = description;
    }

    public Book(Long id, String name, Author author, Genre genre, String description){
        this.id = id;
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.description = description;
    }
}
