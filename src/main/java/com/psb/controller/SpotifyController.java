package com.psb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.client.SpotifyClient;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.PlaylistsPreview;

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
	public PlaylistsPreview getPlaylistsInfo(@RequestHeader String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException {
		return spotifyClient.getPlaylistsPreview(oauthToken);
	}
}
