package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.ReviewService;

@Component
public class ReviewValidator implements Validator {
	@Autowired
	private ReviewService reviewRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Long idm= (Long)o;
		//DA SISTEMARE QUERY CHE INDICA ESISTENZA DI RECENSIONE PER QUEL FILM!!
		boolean check=this.reviewRepository.checkReviewForAuthorAndRelatedMovie(idm);
		if (check)
				  {
			errors.reject("review.duplicate");
				  }
	}
	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}
}