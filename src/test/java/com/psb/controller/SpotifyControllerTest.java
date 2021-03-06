package com.psb.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.psb.client.SpotifyClient;
import com.psb.constants.Constants;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.PlaylistsPreview;
import com.psb.testUtils.SpotifyUtil;

@WebMvcTest(controllers = { SpotifyController.class })
class SpotifyControllerTest {

	@LocalServerPort
	private static int port;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SpotifyClient spotifyClient;

	private SpotifyUtil spotifyUtil = new SpotifyUtil();

	private static final String OAUTH = "oauthToken";
	private static final String ERROR_MESSAGE = "Test error message";
	private static final String PLAYLISTS_URL = "/spotify/playlists/preview";

	@BeforeEach
	public void initialize() {
		spotifyUtil.setMockServerUrl(String.format("http://localhost:%s", port));
	}

	@Test
	void testDefaultPlaylistsPreview() throws Exception {
		PlaylistsPreview testPlaylists = spotifyUtil.createTestPlaylistsPreview();
		when(spotifyClient.getPlaylistsPreview(Mockito.any(String.class))).thenReturn(testPlaylists);

		this.mockMvc
				.perform(MockMvcRequestBuilders.get(PLAYLISTS_URL).contentType(MediaType.APPLICATION_JSON)
						.header("OAUTHToken", OAUTH))
				.andExpect(status().isOk()).andExpect(content().string(containsString(Constants.TEST_PLAYLIST_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_PLAYLIST_ID)))
				.andExpect(content().string(containsString(Constants.TEST_PLAYLIST_IMAGE_URL)));
	}

	@Test
	void testInvalidOauthToken() throws Exception {
		when(spotifyClient.getPlaylistsPreview(Mockito.anyString()))
				.thenThrow(new SpotifyClientUnauthorizedException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(PLAYLISTS_URL).contentType(MediaType.APPLICATION_JSON)
						.header("OAUTHToken", OAUTH))
				.andExpect(status().isUnauthorized()).andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

	@Test
	void testNoContentTypeHeader() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get(PLAYLISTS_URL)).andExpect(status().isBadRequest());
	}

	@Test
	void testInvalidBody() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get(PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void testGetPlaylistsError() throws Exception {
		when(spotifyClient.getPlaylistsPreview(Mockito.anyString())).thenThrow(new SpotifyClientException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
						.header("OAUTHToken", OAUTH).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable()).andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

	@Test
	void testGetTracksError() throws Exception {
		when(spotifyClient.getPlaylistsPreview(Mockito.anyString())).thenThrow(new SpotifyClientException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
						.header("OAUTHToken", OAUTH).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable()).andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

}
