package it.uniroma3.siw.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {

	public boolean existsByAuthor(Credentials author);	
	public boolean existsByAuthorAndRelatedMovie(Credentials author,Movie relatedMovie);	

	@Query(value=
			" SELECT * FROM Review rev "
			+ "WHERE rev.related_movie_id = :related_movie_id "
			+ "and rev.author_id= :author_id", nativeQuery=true)
	public List<Review> checkReviewForAuthorAndRelatedMovie(@Param("related_movie_id") Long related_movie_id,@Param("author_id") Long author_id);

	@Modifying
	@Query("update Review rev set "
			+ "rev.title = :title, rev.rating= :rating, rev.description=:description "
			+ "where rev.id = :id")
	int updateReviewInfo(@Param("title") String title, @Param("rating")Integer rating,@Param("description") String description, @Param("id")Long id);

	

}