package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.PictureRepository;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private ArtistService artistService;
	@Autowired
	private PictureService pictureService;

	@Transactional
	public void saveCreatedNewMovie(Movie movie, MultipartFile[] files) throws IOException {
		if (files.length == 0)
			this.movieRepository.save(movie);
		else {
			this.pictureService.setPicturesForMovie(movie, files);
			this.movieRepository.save(movie);
		}
	}

	public Movie findMovieById(Long id) {
		return this.movieRepository.findById(id).orElse(null);
	}

	public List<Movie> findMovieByYear(Integer year) {
		return this.movieRepository.findByYear(year);
	}

	public Iterable<Movie> findAllMovies() {
		return this.movieRepository.findAll();

	}

	@Transactional
	public boolean setDirectorToMovie(Long movieId, Long directorId) {
		boolean res = false;
		Artist director = this.artistService.findArtistById(directorId);
		Movie movie = this.findMovieById(movieId);
		if (movie != null && director != null) {
			movie.setDirector(director);
			this.movieRepository.save(movie);
			res = true;
		}

		return res;

	}

	@Transactional
	public boolean addActorToMovie(Long movieId, Long actorId) {
		boolean res = false;

		Movie movie = this.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		if (movie != null && actor != null) {
			Set<Artist> actors = movie.getActors();
			actors.add(actor);
			this.movieRepository.save(movie);
			res = true;
		}

		return res;
	}

	@Transactional
	public boolean removeActorFromMovie(Long movieId, Long actorId) {
		boolean res = false;

		Movie movie = this.findMovieById(movieId);
		Artist actor = this.artistService.findArtistById(actorId);
		if (movie != null && actor != null) {
			Set<Artist> actors = movie.getActors();
			actors.remove(actor);
			this.movieRepository.save(movie);
			res = true;
		}

		return res;
	}

	@Transactional
	public boolean deleteMovie(Long id) {
		boolean res = false;
		Movie movie = this.movieRepository.findById(id).orElse(null);
		if (movie == null)
			return res;
		else {
			this.movieRepository.delete(movie);
			return true;
		}
	}

	@Transactional
	public void saveUpdatedMovie(Movie movie, Long id) {
		this.movieRepository.updateMovieInfo(movie.getTitle(), movie.getYear(), id);
		// this.movieRepository.save(movie);
	}

	public boolean existByTitleAndYear(String title, int year) {
		return this.movieRepository.existsByTitleAndYear(title, year);
	}

	@Transactional
	public boolean deletePictureFromMovie(Long idM, Long idP) {
		boolean res = false;
		Movie movie = this.findMovieById(idM);
		Picture picture = this.pictureService.findPictureById(idP);
		if (movie == null || picture == null)
			return res;
		movie.getPictures().remove(picture);
		this.movieRepository.save(movie);

		res = true;
		return res;
	}

	@Transactional
	public boolean addPicturesToMovie(Long idM, MultipartFile[] files) throws IOException {
		boolean res = false;
		Movie movie = this.findMovieById(idM);
		if (files.equals(null) || movie == null)
			return res;
		this.pictureService.setPicturesForMovie(movie, files);
		this.movieRepository.save(movie);
		res = true;
		return res;
	}

}
