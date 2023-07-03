package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.PictureRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.PictureService;

@Controller
public class ArtistController {

	@Autowired
	private ArtistService artistService;
	@Autowired
	private ArtistValidator artistValidator;
	@Autowired
	private PictureService pictureRepository;

	@GetMapping(value = "/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}

	@GetMapping(value = "/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}

	@PostMapping("/admin/artist")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist,
			BindingResult bindingResult, Model model, @RequestParam("file") MultipartFile[] files) throws IOException {
		this.artistValidator.validate(artist, bindingResult);
		if (!bindingResult.hasErrors()) {
			
			this.artistService.createNewArtist(artist,files);
			model.addAttribute("artist", artist);
			return "artist.html";
		} else {
			model.addAttribute("messaggioErrore", "Questo artista esiste già");
			return "admin/formNewArtist.html";
		}
	}

	@GetMapping(value = "/admin/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.findArtistById(id);
		if (artist != null)
			model.addAttribute("artist", artist);
		else
			return "MovieError.html";
		return "admin/formUpdateArtist.html";
	}

	@PostMapping("/admin/updateArtist/{id}")
	public String updateArtist(@PathVariable("id") Long id, @Valid @ModelAttribute("artist") Artist artist,
			BindingResult bindingResult, Model model,@RequestParam("file") MultipartFile[] files) throws IOException {

		if (!bindingResult.hasErrors()) {
			if(!files.equals(null)) this.artistService.addPicturesToArtist(id, files);
			artist.setId(id);
			this.artistService.saveUpdatedArtist(artist,id);
			artist = this.artistService.findArtistById(id);
			model.addAttribute("artist", artist);
			return "artist.html";
		} else {
			model.addAttribute("artist", artist);
			return "admin/formUpdateArtist.html";
		}
	}

	@GetMapping("/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.findArtistById(id));
		return "artist.html";
	}

	@GetMapping("/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistService.findAllArtists());
		return "artists.html";
	}

	@GetMapping("/admin/manageArtists")
	public String manageArtists(Model model) {
		model.addAttribute("artists", this.artistService.findAllArtists());
		return "admin/manageArtists.html";

	}

	@GetMapping(value = "/admin/deleteArtist/{id}")
	public String deleteArtist(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.findArtistById(id);
		if (artist != null)
			model.addAttribute("artist", artist);
		else
			return "MovieError.html";
		return "admin/deleteArtist.html";
	}

	@GetMapping("admin/confirmDeletionArtist/{id}")
	public String confirmDeletionArtist(@PathVariable("id") Long id, Model model) {

		if (this.artistService.deleteArtist(id)) {
			model.addAttribute("artists", this.artistService.findAllArtists());
			return "admin/manageArtists.html";
		} else {
			return "MovieError.html";
		}
	}
	
	@GetMapping("/formSearchArtists")
	public String formSearchArtists(Model model){
		model.addAttribute("artist", new Artist());
		return "formSearchArtists.html";
		
	}

	@PostMapping("/searchArtists")
	public String searchArtists(@Valid @ModelAttribute("artist") Artist artist,
	BindingResult bindingResult, Model model) {
		if(artist.getSurname().equals(null)) 	return "formSearchArtists.html";
		List<Artist> artists = this.artistService.findArtistBySurname(artist.getSurname());
		model.addAttribute("artists", artists);
		return "foundArtists.html";
	}
	
	@GetMapping("/admin/deletePictureFromArtist/{idA}/{idP}")
	public String deletePictureFromArtist(@PathVariable("idA") Long idA, @PathVariable("idP") Long idP, Model model) {
		if (this.artistService.deletePictureFromArtist(idA, idP)) {
			model.addAttribute("artist", this.artistService.findArtistById(idA));
			return "admin/formUpdateArtist.html";
		}
		return "movieError.html";
	}
	@PostMapping("/admin/addPictureToArtist/{idA}")
	public String addPictureToArtist(@PathVariable("idA") Long idA,Model model,@RequestParam("file") MultipartFile[] files) throws IOException {
		if(this.artistService.addPicturesToArtist(idA,files)) {
			model.addAttribute("artist", this.artistService.findArtistById(idA));
			return "admin/formUpdateArtist.html";
		}
			
		return "artistError.html";
	}
}
