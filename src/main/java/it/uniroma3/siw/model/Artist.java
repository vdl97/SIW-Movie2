package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Artist {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String surname;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfBirth;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date dateOfDeath;
	
	@ManyToMany(mappedBy = "actors")
	private Set<Movie> starredMovies;

	@OneToMany(mappedBy = "director")
	private List<Movie> directedMovies;

	@ManyToMany
	private Set<Picture> pictures;

	public Artist() {
		this.starredMovies = new HashSet<>();
		this.directedMovies = new LinkedList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	
	public Set<Movie> getActorOf() {
		return starredMovies;
	}

	public void setActorOf(Set<Movie> starredMovies) {
		this.starredMovies = starredMovies;
	}

	public List<Movie> getDirectorOf() {
		return directedMovies;
	}

	public void setDirectorOf(List<Movie> directedMovies) {
		this.directedMovies = directedMovies;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateOfBirth, directedMovies, name, pictures, starredMovies, surname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(dateOfBirth, other.dateOfBirth) && Objects.equals(directedMovies, other.directedMovies)
				&& Objects.equals(name, other.name) && Objects.equals(pictures, other.pictures)
				&& Objects.equals(starredMovies, other.starredMovies) && Objects.equals(surname, other.surname);
	}

	public Set<Movie> getStarredMovies() {
		return starredMovies;
	}

	public void setStarredMovies(Set<Movie> starredMovies) {
		this.starredMovies = starredMovies;
	}

	public List<Movie> getDirectedMovies() {
		return directedMovies;
	}

	public void setDirectedMovies(List<Movie> directedMovies) {
		this.directedMovies = directedMovies;
	}

	public Set<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(Set<Picture> picture) {
		this.pictures = picture;
	}

	public Date getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(Date dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}


}
