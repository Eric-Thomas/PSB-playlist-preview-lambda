package com.psb.controller;

import com.psb.client.SpotifyClient;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.SpotifyPlaylist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spotify")
@CrossOrigin
public class SpotifyController {

	private SpotifyClient spotifyClient;

	@Autowired
	public SpotifyController(SpotifyClient spotifyClient) {
		this.spotifyClient = spotifyClient;
	}

	@GetMapping(path = "/playlists/preview")
	public List<SpotifyPlaylist> getPlaylistsInfo(@RequestHeader String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException {
		return spotifyClient.getPlaylistsPreview(oauthToken).getPlaylists();
	}
}
