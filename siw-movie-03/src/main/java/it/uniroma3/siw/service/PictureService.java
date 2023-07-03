package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.PictureRepository;

@Service
public class PictureService {

	@Autowired
	private PictureRepository pictureRepository;

	public Picture findPictureById(Long id) {
		return this.pictureRepository.findById(id).orElse(null);
	}

	public void setPicturesForMovie(Movie movie, MultipartFile[] files) throws IOException {
		if (movie.getPictures().isEmpty())
			movie.setPictures(new HashSet<Picture>());
		Picture[] pictures = this.savePictureIfNotExistsOrRetrieve(files);
		for (Picture p : pictures) {
			movie.getPictures().add(p);
		}

	}

	public void setPicturesForArtist(Artist artist, MultipartFile[] files) throws IOException {
		if (artist.getPictures().isEmpty())
			artist.setPictures(new HashSet<Picture>());
		Picture[] pictures = this.savePictureIfNotExistsOrRetrieve(files);
		for (Picture p : pictures) {
			artist.getPictures().add(p);
		}

	}

	private Picture[] savePictureIfNotExistsOrRetrieve(MultipartFile[] files) throws IOException {
		Picture[] pictures = new Picture[files.length];
		int i = 0;
		for (MultipartFile f : files) {
			Picture picture;
			picture = new Picture();
			picture.setName(f.getResource().getFilename());
			picture.setData(f.getBytes());
			this.pictureRepository.save(picture);
			pictures[i] = picture;
			i++;
		}
		return pictures;
	}
}
