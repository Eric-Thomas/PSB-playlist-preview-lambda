package com.psb.client;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.PlaylistsPreview;
import com.psb.model.SpotifyPlaylist;
import com.psb.testUtils.SpotifyUtil;

import okhttp3.mockwebserver.MockWebServer;

class SpotifyClientTest {

	private static MockWebServer mockSpotifyServer;
	private SpotifyClient spotifyClient;
	private SpotifyUtil spotifyUtil = new SpotifyUtil();

	@BeforeAll
	public static void setUp() throws IOException {
		mockSpotifyServer = new MockWebServer();
		mockSpotifyServer.start();
	}

	@AfterAll
	public static void tearDown() throws IOException {
		mockSpotifyServer.shutdown();
	}

	@BeforeEach
	void initialize() {
		String baseUrl = String.format("http://localhost:%s", mockSpotifyServer.getPort());
		WebClient client = WebClient.create(baseUrl);
		spotifyClient = new SpotifyClient(client);
		spotifyUtil.setMockServerUrl(baseUrl);
		// Sets playlists url since it is a value drawn from properties file in the
		// class under test
		ReflectionTestUtils.setField(spotifyClient, "basePlaylistsUrl", "/");

	}

	@Test
	void testGetPlaylistsPreviewNoPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		PlaylistsPreview testPlaylistsPreview = spotifyUtil.createTestPlaylistsPreview();
		spotifyUtil.addMockPlaylistsPreviewResponse(testPlaylistsPreview, mockSpotifyServer);
		PlaylistsPreview clientPlaylists = spotifyClient.getPlaylistsPreview("oauthToken");
		assertEquals(testPlaylistsPreview, clientPlaylists);
	}

	@Test
	void testGetPlaylistsWithPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		List<PlaylistsPreview> paginationResp = spotifyUtil.createTestPlaylistsPreviewWithPagination();
		spotifyUtil.addMockPlaylistsPreviewPaginationResponses(paginationResp, mockSpotifyServer);
		PlaylistsPreview testPreview = combinePreviews(paginationResp);
		PlaylistsPreview clientPreview = spotifyClient.getPlaylistsPreview("oauthToken");
		assertEquals(testPreview, clientPreview);
		assertEquals(testPreview.getPlaylists().size(), clientPreview.getPlaylists().size());
	}

	private PlaylistsPreview combinePreviews(List<PlaylistsPreview> previewsList) {
		PlaylistsPreview previews = new PlaylistsPreview();
		List<SpotifyPlaylist> playlists = new ArrayList<>();
		for (PlaylistsPreview preview : previewsList) {
			playlists.addAll(preview.getPlaylists());
		}
		previews.setPlaylists(playlists);
		return previews;
	}

	@Test
	void testGetPlaylistsPreviewNull() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		spotifyUtil.addEmptyPlaylistsResponse(mockSpotifyServer);
		PlaylistsPreview clientPlaylistsPreview = spotifyClient.getPlaylistsPreview("oauthToken");
		assertTrue(clientPlaylistsPreview.getPlaylists().isEmpty());
	}

	@Test
	void testGetPlaylistsUnauthorized() {
		spotifyUtil.addUnauthorizedResponse(mockSpotifyServer);
		assertThrows(SpotifyClientUnauthorizedException.class, () -> {
			spotifyClient.getPlaylistsPreview("oauthToken");
		});
	}

	@Test
	void testGetPlaylists5xxError() {
		spotifyUtil.add5xxResponse(mockSpotifyServer);
		assertThrows(SpotifyClientException.class, () -> {
			spotifyClient.getPlaylistsPreview("oauthToken");
		});
	}





}
