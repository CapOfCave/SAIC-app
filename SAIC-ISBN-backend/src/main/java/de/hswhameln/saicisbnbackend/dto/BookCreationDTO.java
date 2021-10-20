package de.hswhameln.saicisbnbackend.dto;

public class BookCreationDTO {
    private String titel;
    private String autor;
    private String verlag;
    private String isbn13;

    public BookCreationDTO(String titel, String autor, String verlag, String isbn13) {
        this.titel = titel;
        this.autor = autor;
        this.verlag = verlag;
        this.isbn13 = isbn13;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getVerlag() {
        return verlag;
    }

    public void setVerlag(String verlag) {
        this.verlag = verlag;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
}
