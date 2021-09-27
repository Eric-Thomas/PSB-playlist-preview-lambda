package com.psb.testUtils;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.constants.Constants;
import com.psb.model.PlaylistsPreview;
import com.psb.model.SpotifyImage;
import com.psb.model.SpotifyPlaylist;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class SpotifyUtil {

	private ObjectMapper objectMapper;
	private String mockServerUrl;

	private final int PAGINATION_COUNT = 5;
	private final int UNAUTHORIZED = 401;

	public SpotifyUtil() {
		this.objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public void setMockServerUrl(String url) {
		this.mockServerUrl = url;
	}
	
	public PlaylistsPreview createTestPlaylistsPreview() {
		PlaylistsPreview playlistsPreview = new PlaylistsPreview();
		List<SpotifyPlaylist> playlists = new ArrayList<>();
		playlists.add(createTestPlaylist());
		playlistsPreview.setPlaylists(playlists);
		playlistsPreview.setNext(null);
		return playlistsPreview;
	}

	public List<PlaylistsPreview> createTestPlaylistsPreviewWithPagination() {
		List<PlaylistsPreview> previews = new ArrayList<>();
		for (int i = 0; i < PAGINATION_COUNT; i++) {
			PlaylistsPreview playlistsPreview = new PlaylistsPreview();
			List<SpotifyPlaylist> playlists = new ArrayList<>();
			playlists.add(createTestPlaylist());
			playlistsPreview.setPlaylists(playlists);
			playlistsPreview.setNext(mockServerUrl + Constants.NEXT_URL);
			previews.add(playlistsPreview);
		}
		// Set last 'next' field to null to avoid infinite loop
		previews.get(previews.size() - 1).setNext(null);
		return previews;
	}

	public SpotifyPlaylist createTestPlaylist() {
		SpotifyPlaylist testPlaylist = new SpotifyPlaylist();
		testPlaylist.setName(Constants.TEST_PLAYLIST_NAME);
		List<SpotifyImage> images = new ArrayList<>();
		images.add(createTestImage());
		testPlaylist.setImages(images);
		testPlaylist.setId(Constants.TEST_PLAYLIST_ID);
		return testPlaylist;
	}

	public SpotifyImage createTestImage() {
		SpotifyImage image = new SpotifyImage();
		image.setHeight("500");
		image.setWidth("500");
		image.setUrl(Constants.TEST_PLAYLIST_IMAGE_URL);
		return image;
	}

	public void addMockPlaylistsPreviewResponse(PlaylistsPreview preview, MockWebServer server) {
		try {
			server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(preview))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void addUnauthorizedResponse(MockWebServer server) {
		server.enqueue(new MockResponse().setResponseCode(UNAUTHORIZED));
	}

	public void add5xxResponse(MockWebServer server) {
		server.enqueue(new MockResponse().setResponseCode(503));
	}

	public void addMockPlaylistsPreviewPaginationResponses(List<PlaylistsPreview> playlistsPreviews, MockWebServer server) {
		for (PlaylistsPreview preview : playlistsPreviews) {
			try {
				server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(preview))
						.addHeader("Content-Type", "application/json"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	public void addEmptyPlaylistsResponse(MockWebServer server) {
		PlaylistsPreview preview = new PlaylistsPreview();
		preview.setNext(null);
		preview.setPlaylists(new ArrayList<>());
		try {
			server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(preview)).addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
