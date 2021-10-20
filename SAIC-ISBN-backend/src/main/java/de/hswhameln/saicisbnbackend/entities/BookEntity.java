package de.hswhameln.saicisbnbackend.entities;

import javax.persistence.*;
/**
 * Bucheinheit, zum Speichern in der Datenbank
 */
@Entity
@Table(name="T_Book")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String titel;
    @Column
    private String autor;
    @Column
    private String verlag;
    @Column
    private String isbn13;

    public BookEntity() {
    }

    public BookEntity(String titel, String autor, String verlag, String isbn13){
        this.titel=titel;
        this.autor=autor;
        this.verlag=verlag;
        this.isbn13=isbn13;
    }


    public long getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public String getAutor() {
        return autor;
    }

    public String getVerlag() {
        return verlag;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setVerlag(String verlag) {
        this.verlag = verlag;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
}
