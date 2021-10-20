package de.hswhameln.saicisbnbackend.dto;


/**
 * Datenhaltungsklasse die ein Buch Repräsentiert
 */
public class DOBook {

    private long id;
    private String titel;
    private String autor;
    private String verlag;
    private String isbn13;

    public DOBook() {
    }

    public DOBook(String titel, String autor, String verlag, String isbn13){
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
