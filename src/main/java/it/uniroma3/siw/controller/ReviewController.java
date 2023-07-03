package it.uniroma3.siw.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class ReviewController {
	@Autowired
	private MovieService movieService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewValidator reviewValidator;
	@Autowired
	private MovieController movieController;

	@GetMapping(value="/default/formNewReview/{id}")
	public String formNewReview(@PathVariable("id") Long id, Model model) {
		
		Movie movie=this.movieService.findMovieById(id);
		model.addAttribute("movie", movie);
		if(this.reviewService.checkReviewForAuthorAndRelatedMovie(id)) {
			boolean flag=this.reviewService.checkReviewForAuthorAndRelatedMovie(id);
			model.addAttribute("flag", flag);
			model.addAttribute("text", new String("Hai già inserito una recensione per questo film!"));
			return "movie.html";
			}
		Review review = new Review();
		model.addAttribute("review",review);

		return "default/formNewReview.html";
		}

	@PostMapping("/default/review/{idM}")
	public String newReview(@PathVariable("idM") Long idM, @Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult, Model model) {
		// DEVO PASSARE ANCHE L'ID DEL FILM, ALTRIMENTI NON SO COME CONTROLLARE SE
		// ESISTE GIA'
		this.reviewValidator.validate(idM, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.reviewService.saveCreatedNewReview(review, idM);

			// NON SERVE movie.getReviews().add(review);
			model.addAttribute("review", review);
			return "review.html";
		} else {
			Movie movie=this.movieService.findMovieById(idM);
			model.addAttribute("movie", movie);
			return "default/formNewReview.html";
			// return "index.html";

		}
	}

	@GetMapping("/review/{id}")
	public String getReview(@PathVariable("id") Long id, Model model) {
		Review review = this.reviewService.findById(id);
		model.addAttribute("review", review);
		return "review.html";
	}

	@GetMapping("/default/updateReview/{id}")
	public String getUpdateReview(@PathVariable("id") Long id, Model model) {
		model.addAttribute("review", this.reviewService.findById(id));

		return "/default/updateReview.html";
	}

	@PostMapping("//default/updateReview/{id}")
	public String updateReview(@PathVariable("id") Long id, @Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult, Model model) {

		if (!bindingResult.hasErrors()) {
			review.setId(id);
			this.reviewService.saveUpdatedReview(review, id);
			// this.reviewRepository.save(review);
			// NON SERVE movie.getReviews().add(review);
			review = this.reviewService.findById(id);
			model.addAttribute("review", review);
			return "review.html";
		} else {

			model.addAttribute("review", review);
			return "/default/updateReview.html";

		}
	}
	

	@GetMapping(value = "/admin/deleteReview/{id}")
	public String deleteReview(@PathVariable("id") Long id, Model model) {
		Review review = this.reviewService.findById(id);
		if (review != null)
			model.addAttribute("review", review);
		else
			return "MovieError.html";
		return "admin/deleteReview.html";
	}

	@GetMapping("admin/confirmDeletionReview/{id}")
	public String confirmDeletionReview(@PathVariable("id") Long id, Model model) {
		Movie movie=this.reviewService.findById(id).getRelatedMovie();
		if (this.reviewService.deleteReview(id)) {
			model.addAttribute("movie",movie);
			return "movie.html";
		} else {
			return "MovieError.html";
		}
	}

}