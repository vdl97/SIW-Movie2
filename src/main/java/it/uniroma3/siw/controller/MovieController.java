package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class MovieController {
	@Autowired
	private MovieService movieService;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private MovieValidator movieValidator;

	@Autowired
	private CredentialsService credentialsService;

	@GetMapping(value = "/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value = "/admin/UpdateMovie/{id}")
	public String UpdateMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.findMovieById(id);
		if (movie != null)
			model.addAttribute("movie", movie);
		else
			return "MovieError.html";
		return "admin/UpdateMovie.html";
	}

	@GetMapping(value = "/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}

	@GetMapping(value = "/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.findAllMovies());
		return "admin/manageMovies.html";
	}

	@GetMapping(value = "/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId,
			Model model) {

		if (this.movieService.setDirectorToMovie(movieId, directorId)) {
			model.addAttribute("movie", this.movieService.findMovieById(movieId));
			return "admin/UpdateMovie.html";
		} else
			return "MovieError.html";
	}

	@GetMapping(value = "/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		Iterable<Artist> artists = this.artistService.findAllArtists();
		Movie movie = this.movieService.findMovieById(id);
		if (movie != null && artists != null) {
			model.addAttribute("artists", artists);
			model.addAttribute("movie", movie);
		} else
			return "MovieError.html";
		return "admin/directorsToAdd.html";
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model,
			@RequestParam("file") MultipartFile[] files) throws IOException {

		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.movieService.saveCreatedNewMovie(movie, files);
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "admin/formNewMovie.html";
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.findMovieById(id);
		if (movie != null) {
			model.addAttribute("movie", movie);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof AnonymousAuthenticationToken) {
				return "movie.html";
			} else {
				UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
						.getPrincipal();
				Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
				String role = credentials.getRole();
				if (role.equals(Credentials.ADMIN_ROLE)) {
					model.addAttribute("role", role);
					return "movie.html";
				}
			}
		} else
			return "MovieError.html";
		return "movie.html";
	}

	@GetMapping("/movie")
	public String getMovies(Model model) {

		Iterable<Movie> movies = this.movieService.findAllMovies();
		model.addAttribute("movies", movies);
		// model.addAttribute("user", credentials.getUser());
		return "movies.html";
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies(Model model) {
		model.addAttribute("movie", new Movie());
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {

		if (movie.getYear() == null)
			return "formSearchMovies.html";

		List<Movie> movies = this.movieService.findMovieByYear(movie.getYear());
		model.addAttribute("movies", movies);
		return "foundMovies.html";

	}

	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		Movie movie = this.movieService.findMovieById(id);
		List<Artist> actorsToAdd = this.artistService.actorsToAdd(id);

		if (movie != null && actorsToAdd != null) {
			model.addAttribute("actorsToAdd", actorsToAdd);
			model.addAttribute("movie", movie);
		} else
			return "MovieError.html";
		return "admin/actorsToAdd.html";
	}

	@GetMapping(value = "/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId,
			Model model) {

		if (this.movieService.addActorToMovie(movieId, actorId)) {

			List<Artist> actorsToAdd = this.artistService.actorsToAdd(movieId);

			model.addAttribute("movie", this.movieService.findMovieById(movieId));
			model.addAttribute("actorsToAdd", actorsToAdd);
		} else
			return "MovieError.html";

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value = "/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId,
			Model model) {
		if (this.movieService.removeActorFromMovie(movieId, actorId)) {

			List<Artist> actorsToAdd = this.artistService.actorsToAdd(movieId);

			model.addAttribute("movie", this.movieService.findMovieById(movieId));
			model.addAttribute("actorsToAdd", actorsToAdd);
		} else
			return "MovieError.html";
		return "admin/actorsToAdd.html";
	}

	@GetMapping(value = "/admin/deleteMovie/{id}")
	public String deleteMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.findMovieById(id);
		if (movie != null)
			model.addAttribute("movie", movie);
		else
			return "MovieError.html";
		return "admin/deleteMovie.html";
	}

	@GetMapping("admin/confirmDeletionMovie/{id}")
	public String confirmDeletionMovie(@PathVariable("id") Long id, Model model) {

		if (this.movieService.deleteMovie(id)) {
			model.addAttribute("movies", this.movieService.findAllMovies());
			return "admin/manageMovies.html";
		} else {
			return "MovieError.html";
		}
	}

	@GetMapping(value = "/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.findMovieById(id);
		if (movie != null)
			model.addAttribute("movie", movie);
		else
			return "MovieError.html";
		return "admin/formUpdateMovie.html";
	}

	@PostMapping("/admin/updateMovie/{id}")
	public String updateMovie(@PathVariable("id") Long id, @Valid @ModelAttribute("movie") Movie movie,
			BindingResult bindingResult, Model model) {

		if (!bindingResult.hasErrors()) {
			movie.setId(id);
			this.movieService.saveUpdatedMovie(movie, id);
			movie = this.movieService.findMovieById(id);
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie.html";
		}
	}

	@GetMapping("/admin/deletePictureFromMovie/{idM}/{idP}")
	public String deletePictureFromMovie(@PathVariable("idM") Long idM, @PathVariable("idP") Long idP, Model model) {
		if (this.movieService.deletePictureFromMovie(idM, idP)) {
			model.addAttribute("movie", this.movieService.findMovieById(idM));
			return "admin/UpdateMovie.html";
		}
		return "movieError.html";
	}
	@PostMapping("/admin/addPictureToMovie/{idM}")
	public String addPictureToMovie(@PathVariable("idM") Long idM,Model model,@RequestParam("file") MultipartFile[] files) throws IOException {
		if(this.movieService.addPicturesToMovie(idM,files)) {
			model.addAttribute("movie", this.movieService.findMovieById(idM));
			return "admin/UpdateMovie.html";
		}
			
		return "movieError.html";
	}
}
