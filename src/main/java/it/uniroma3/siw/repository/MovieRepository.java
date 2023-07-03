package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> {

	public List<Movie> findByYear(int year);
	

	public boolean existsByTitleAndYear(String title, int year);	
	
	
	@Modifying
	@Query("update Movie m set "
			+ "m.title = :title, m.year=:year "
			+ "where m.id = :id")
	int updateMovieInfo(@Param("title") String title, @Param("year")Integer year,@Param("id")Long id);

	
}