package com.psb.client;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.PlaylistsPreview;

import reactor.core.publisher.Mono;

@Component
public class SpotifyClient {

	@Value("${spotify.playlists.uri}")
	private String basePlaylistsUrl;

	@Value("${spotify.user.profile.uri}")
	private String userInfoUrl;

	private WebClient client;

	private Logger logger = LoggerFactory.getLogger(SpotifyClient.class);

	private static final String UNAUTHORIZED_ERROR_MESSAGE = "Invalid spotify oauth token.";

	@Autowired
	public SpotifyClient(WebClient webClient) {
		this.client = webClient;
	}

	public PlaylistsPreview getPlaylistsPreview(String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException {
		try {
			return getPlaylistsPreviewWithPagination(oauthToken);
		} catch (RuntimeException e) {
			if (e.getCause().getClass() == SpotifyClientUnauthorizedException.class) {
				throw new SpotifyClientUnauthorizedException(e.getMessage());
			} else {
				throw new SpotifyClientException(e.getMessage());
			}
		}
	}

	private PlaylistsPreview getPlaylistsPreviewWithPagination(String oauthToken) {
		PlaylistsPreview playlistsPreview = new PlaylistsPreview();
		playlistsPreview.setPlaylists(new ArrayList<>());
		String playlistsUrl = basePlaylistsUrl;
		while (playlistsUrl != null) {
			logger.info("Getting playlists at {}", playlistsUrl);
			PlaylistsPreview preview = client.get().uri(playlistsUrl)
					.headers(httpHeaders -> httpHeaders.setBearerAuth(oauthToken)).retrieve()
					.onStatus(HttpStatus::isError, response -> {
						if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
							return Mono.error(new SpotifyClientUnauthorizedException(UNAUTHORIZED_ERROR_MESSAGE));
						} else {
							return Mono.error(new SpotifyClientException(response.statusCode().toString()));
						}
					}).bodyToMono(PlaylistsPreview.class).block();
			logger.info("preview {}", preview);
			playlistsPreview.getPlaylists().addAll(preview.getPlaylists());
			playlistsUrl = preview.getNext();
		}
		return playlistsPreview;
	}

}
