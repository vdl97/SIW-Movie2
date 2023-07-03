package it.uniroma3.siw.model;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Review {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
	private String title;
    
    //@NotBlank
    private String description;
    
    @ManyToOne
    private Credentials author;
    
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
    
    //@NotNull
    @ManyToOne
    //@Id
    private Movie relatedMovie;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Credentials getAuthor() {
		return author;
	}

	public void setAuthor(Credentials author) {
		this.author = author;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Movie getRelatedMovie() {
		return relatedMovie;
	}

	public void setRelatedMovie(Movie relatedMovie) {
		this.relatedMovie = relatedMovie;
	}

	@Override
	public int hashCode() {
		return Objects.hash(author, rating, relatedMovie, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Review other = (Review) obj;
		return Objects.equals(author, other.author) && Objects.equals(rating, other.rating)
				&& Objects.equals(relatedMovie, other.relatedMovie) && Objects.equals(title, other.title);
	}

	
}
