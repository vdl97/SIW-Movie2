package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;
	@Autowired
	private PictureService pictureService;
	
	@Transactional
	public boolean createNewArtist(Artist artist,MultipartFile[] files) throws IOException {
		boolean res=false;
		if (!artistRepository.existsByNameAndSurname(artist.getName(), artist.getSurname())) {
			res=true;
			this.pictureService.setPicturesForArtist(artist, files);
			this.artistRepository.save(artist); 	
		}
		return res;
	}
	
	@Transactional
	public void saveUpdatedArtist(Artist artist, Long id) {
		
		this.artistRepository.updateArtistInfo(artist.getName(), artist.getSurname(), artist.getDateOfBirth(), artist.getDateOfDeath(), id); 
		//this.artistRepository.save(artist);
	}
	
	public Artist findArtistById(Long id) {
		return this.artistRepository.findById(id).orElse(null);
	}
	
	public Iterable<Artist> findAllArtists(){
		return this.artistRepository.findAll();
		
	}
	
	public List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}

	@Transactional
	public boolean deleteArtist(Long id) {
		boolean res=false;
		Artist artist=this.artistRepository.findById(id).orElse(null);
		if(artist==null)
			return res;
		else {
		this.artistRepository.delete(artist);
		return true;
		}
	}

	public List<Artist> findArtistBySurname(String surname) {
return this.artistRepository.findAllBySurname(surname);
	}

	public boolean deletePictureFromArtist(Long idA, Long idP) {
		boolean res = false;
		Artist artist = this.findArtistById(idA);
		Picture picture = this.pictureService.findPictureById(idP);
		if (artist == null || picture == null)
			return res;
		artist.getPictures().remove(picture);
		this.artistRepository.save(artist);

		res = true;
		return res;
	}

	public boolean addPicturesToArtist(Long idA, MultipartFile[] files) throws IOException {
		boolean res = false;
		Artist artist = this.findArtistById(idA);
		if (files.equals(null) || artist == null)
			return res;
		this.pictureService.setPicturesForArtist(artist, files);
		this.artistRepository.save(artist);
		res = true;
		return res;
	}
	
	
}
