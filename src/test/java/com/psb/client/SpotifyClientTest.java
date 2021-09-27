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
import com.psb.model.SpotifyPlaylist;
import com.psb.model.SpotifyPlaylists;
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
		ReflectionTestUtils.setField(spotifyClient, "userInfoUrl", "/");
	}

	@Test
	void testGetPlaylistsNoPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		SpotifyPlaylists testPlaylists = spotifyUtil.createTestPlaylists();
		spotifyUtil.addMockPlaylistsResponse(testPlaylists, mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertEquals(testPlaylists, clientPlaylists);
	}

	@Test
	void testGetPlaylistsWithPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		List<SpotifyPlaylists> testPlaylistsList = spotifyUtil.createTestPlaylistsWithPagination();
		SpotifyPlaylists testPlaylists = combinePlaylistsList(testPlaylistsList);
		spotifyUtil.addMockPlaylistsPaginationResponses(testPlaylistsList, mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertEquals(testPlaylists, clientPlaylists);
		assertEquals(testPlaylists.getPlaylists().size(), clientPlaylists.getPlaylists().size());
	}

	private SpotifyPlaylists combinePlaylistsList(List<SpotifyPlaylists> list) {
		SpotifyPlaylists spotifyPlaylists = new SpotifyPlaylists();
		List<SpotifyPlaylist> playlistList = new ArrayList<>();
		for (SpotifyPlaylists playlists : list) {
			playlistList.addAll(playlists.getPlaylists());
		}
		spotifyPlaylists.setPlaylists(playlistList);
		return spotifyPlaylists;
	}

	@Test
	void testGetPlaylistsNull() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		spotifyUtil.addEmptyBodyResponse(mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertTrue(clientPlaylists.getPlaylists().isEmpty());
	}

	@Test
	void testGetPlaylistsUnauthorized() {
		spotifyUtil.addUnauthorizedResponse(mockSpotifyServer);
		assertThrows(SpotifyClientUnauthorizedException.class, () -> {
			spotifyClient.getPlaylists("oauthToken");
		});
	}

	@Test
	void testGetPlaylists5xxError() {
		spotifyUtil.add5xxResponse(mockSpotifyServer);
		assertThrows(SpotifyClientException.class, () -> {
			spotifyClient.getPlaylists("oauthToken");
		});
	}





}
