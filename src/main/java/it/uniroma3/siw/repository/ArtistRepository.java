package it.uniroma3.siw.repository;


import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Artist;

public interface ArtistRepository extends CrudRepository<Artist, Long> {

	public boolean existsByNameAndSurname(String name, String surname);	

	@Query(value="select * "
			+ "from artist a "
			+ "where a.id not in "
			+ "(select actors_id "
			+ "from movie_actors "
			+ "where movie_actors.starred_movies_id = :movieId)", nativeQuery=true)
	public Iterable<Artist> findActorsNotInMovie(@Param("movieId") Long id);

	@Modifying
	@Query(value="update Artist a set "
			+ "a.name = :name, a.surname=:surname, a.dateOfBirth=:dateOfBirth, a.dateOfDeath=:dateOfDeath "
			+ "where a.id = :id")
	int updateArtistInfo(@Param("name") String name,@Param("surname") String surname, @Param("dateOfBirth")Date dateOfBirth,@Param("dateOfDeath")Date dateOfDeath,@Param("id")Long id);

	public List<Artist> findAllBySurname(String surname);

	

}